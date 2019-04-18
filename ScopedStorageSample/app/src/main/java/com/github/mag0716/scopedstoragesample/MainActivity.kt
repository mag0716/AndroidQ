package com.github.mag0716.scopedstoragesample

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
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
        const val SHARED_FILE_NAME = "sample_shared.png"
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
            //loadFromSharedCollection()
            loadOtherAppFileFromSharedCollection()
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
        val shared = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (shared.isDirectory && shared.exists().not()) {
            shared.mkdir()
        }
        // /storage/emulated/0/Pictures/sample.png
        val file = createFileFromBitmap(getDrawable(R.mipmap.ic_launcher).toBitmap(), shared, SHARED_FILE_NAME)

        // TODO: Caused by: java.io.IOException: No such file or directory で保存できない
        // Android 9 以下であればパーミションがあれば保存できる

        // shared collection に保存するというのは、insert で行うの？
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, SHARED_FILE_NAME)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.DATA, file.absolutePath)
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        Log.d(TAG, "saveToSharedCollection : $uri")

    }

    private fun loadFromSharedCollection() {
        val shared = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(shared, SHARED_FILE_NAME)
        Log.d(TAG, "loadFromSharedCollection : $file")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            imageView.setImageBitmap(bitmap)
        } else {
            Log.w(TAG, "loadFromSharedCollection : $file is not exists.")
        }
    }

    private fun loadOtherAppFileFromSharedCollection() {
        val projection = listOf(BaseColumns._ID, MediaStore.Images.Media.DISPLAY_NAME)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(), null, null, null
        )

        // パーミッションがないと cursor が 0件になる。エラーは表示されない
        if (cursor.moveToFirst().not()) {
            cursor.close()
            return
        }

        val idColumnIndex = cursor.getColumnIndex(BaseColumns._ID)
        val displayNameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

        Log.d(
            TAG,
            "loadOtherAppFileFromSharedCollection : " +
                    "${cursor.getString(displayNameColumnIndex)} : ${cursor.getLong(idColumnIndex)}"
        )

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            cursor.getLong(idColumnIndex)
        )
        Log.d(TAG, "loadOtherAppFileFromSharedCollection : $contentUri")
        imageView.setImageURI(contentUri)

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

        file.createNewFile()

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)

        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(outputStream.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()

        return file
    }
}
