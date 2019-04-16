package com.github.mag0716.scopedstoragesample

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ScopedStorage"
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

        saveToSandbox()
    }

    private fun saveToSandbox() {
        val sandbox = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // /storage/emulated/0/Android/data/com.github.mag0716.scopedstoragesample/files/Pictures/sample.png
        val file = File(sandbox, "sample.png")
        Log.d(TAG, "saveToSandbox : $file")
        file.createNewFile()
        val bitmap = getDrawable(R.mipmap.ic_launcher).toBitmap()

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)

        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(outputStream.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    private fun loadFromSandbox() {

    }

    private fun saveToSharedCollection() {

    }

    private fun loadFromSharedCollection() {

    }
}
