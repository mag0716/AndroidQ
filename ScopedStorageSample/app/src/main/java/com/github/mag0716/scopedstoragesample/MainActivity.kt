package com.github.mag0716.scopedstoragesample

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ScopedStorage"
        const val FILE_NAME = "sample.png"
        /**
         * FIXME: deleteFileFromContentResolver で消せないのでファイル名を毎回変えて確認している
         */
        const val SHARED_FILE_NAME = "sample_shared9.png"
        const val REQUEST_READ_MEDIA_IMAGES = 1
    }

    private lateinit var loadFromSandboxButton: Button
    private lateinit var saveToSharedCollectionButton: Button
    private lateinit var loadFromSharedCollectionButton: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFromSandboxButton = findViewById(R.id.load_sandbox_button)
        saveToSharedCollectionButton = findViewById(R.id.save_shared_button)
        loadFromSharedCollectionButton = findViewById(R.id.load_shared_button)
        imageView = findViewById(R.id.image_view)

        loadFromSandboxButton.setOnClickListener {
            loadFromSandbox()
        }
        saveToSharedCollectionButton.setOnClickListener {
            saveToSharedCollection()
        }
        loadFromSharedCollectionButton.setOnClickListener {
            loadFromSharedCollection()
        }

        requestPermission()
        saveToSandbox()

        // debug
        Log.d(TAG, "MediaStore.getAllVolumeNames = ${MediaStore.getAllVolumeNames(this)}")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_MEDIA_IMAGES) {
            val isGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "onRequestPermissionResult : $requestCode, $isGranted")
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun requestPermission() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

            } else {
                ActivityCompat.requestPermissions(this, listOf(permission).toTypedArray(), REQUEST_READ_MEDIA_IMAGES);
            }
        }
    }

    /**
     * sandbox へのファイル作成はパーミッション不要
     * 他のアプリからのアクセスは不可
     */
    private fun saveToSandbox() {
        val sandbox = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (sandbox.isDirectory && sandbox.exists().not()) {
            sandbox.mkdir()
        }
        // /storage/emulated/0/Android/data/com.github.mag0716.scopedstoragesample/files/Pictures/sample.png
        createFileFromBitmap(getDrawable(R.mipmap.ic_launcher).toBitmap(), sandbox, FILE_NAME)
    }

    /**
     * sandbox へのファイル読み込みはパーミッション不要
     * 他のアプリからのアクセスは不可
     */
    private fun loadFromSandbox() {
        val sandbox = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // /storage/emulated/0/Android/data/com.github.mag0716.scopedstoragesample/files/Pictures/sample.png
        val file = File(sandbox, FILE_NAME)
        Log.d(TAG, "loadFromSandbox : $file")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            imageView.setImageBitmap(bitmap)
        } else {
            Log.w(TAG, "loadFromSandbox : $file is not exists.")
        }
    }

    private fun saveToSharedCollection() {
        deleteFileFromContentResolver(SHARED_FILE_NAME) // DEBUG: DISPLAY_NAME が同じだと null が返ってくるので消しておく

        // まず先に保存先の ContentResolver 経由で insert して Uri を確保し、そのあとにその Uri に対して書き込みを行うらしい
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, SHARED_FILE_NAME)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, true)
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        Log.d(TAG, "saveToSharedCollection : $uri")
        if (uri != null) {
            // File のパスが知りたいが、MediaStore.MediaColumns.DATA は deprecated で ContentResolver.openFileDescriptor を利用する必要がある
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "rwt")
            writeBitmapToFile(getDrawable(R.mipmap.ic_launcher).toBitmap(), fileDescriptor)
        }
    }

    private fun loadFromSharedCollection() {
        // READ_MEDIA_IMAGES の permission が許可されていたら他のアプリの画像も許可されていなかったら自分のアプリのみの画像を読み込む想定
        val projection = listOf(BaseColumns._ID, MediaStore.Images.Media.DISPLAY_NAME)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(), null, null, null
        )
        
        val imageList: MutableList<Pair<Long, String>> = mutableListOf()
        // パーミッションがないと cursor が 0件になる。エラーは表示されない
        if (cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndex(BaseColumns._ID)
            val displayNameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.isAfterLast.not()) {
                imageList.add(cursor.getLong(idColumnIndex) to cursor.getString(displayNameColumnIndex))
                cursor.moveToNext()
            }
        }

        Log.d(TAG, "loadFromSharedCollection : $imageList")

        if (imageList.isNotEmpty()) {
            val firstImageId = imageList.first().first

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                firstImageId
            )
            Log.d(TAG, "loadFromSharedCollection : $contentUri")
            imageView.setImageURI(contentUri)
        }

        cursor.close()
    }

    private fun createFileFromBitmap(bitmap: Bitmap, directory: File, fileName: String): File {
        if (directory.isDirectory && directory.exists().not()) {
            directory.mkdir()
        }
        val file = File(directory, fileName)
        Log.d(TAG, "createFileFromBitmap : ${file.path}, ${file.exists()}")
        if (file.exists()) {
            return file
        }

        writeBitmapToFile(bitmap, file)

        return file
    }

    private fun writeBitmapToFile(bitmap: Bitmap, file: File) {
        if (file.exists().not()) {
            file.createNewFile()
        }

        val outputStream = ByteArrayOutputStream()
        val result = bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)

        Log.d(TAG, "writeBitmapToFile : $result")

        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(outputStream.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    private fun writeBitmapToFile(bitmap: Bitmap, fileDescriptor: ParcelFileDescriptor) {
        val fileOutputStream = ParcelFileDescriptor.AutoCloseOutputStream(fileDescriptor)
        val result = bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream)

        Log.d(TAG, "writeBitmapToFile : $result")

        fileOutputStream.flush()
    }

    private fun deleteFileFromContentResolver(fileName: String) {
        // 動作しない：DISPLAY_NAME でのフィルタリングでは削除できない(0が返却される)
        val id = contentResolver.delete(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "${MediaStore.Images.Media.DISPLAY_NAME}=?",
            listOf(fileName).toTypedArray()
        )
        Log.d(TAG, "deleteFileFromContentResolver : $id")
    }
}
