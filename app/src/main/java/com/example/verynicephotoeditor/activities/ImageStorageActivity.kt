package com.example.verynicephotoeditor.activities


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verynicephotoeditor.ButtonAdapter
import com.example.verynicephotoeditor.ButtonModel
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.fragments.RotateFragment
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.databinding.ActivityImageStorageBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ImageStorageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageStorageBinding
    private lateinit var sharedViewModel: SharedViewModel

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                val bitmap = ImageDecoder.decodeBitmap(source)

                binding.mainImage.setImageBitmap(bitmap)
                sharedViewModel.setBitmap(bitmap)
            }
        }
    }
    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val imagePath = intent.getStringExtra("imagePath")
        val bitmap = BitmapFactory.decodeFile(imagePath)



        binding = ActivityImageStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainImage.setImageBitmap(bitmap)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.setBitmap(bitmap)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.bitmap.observe(this) { bitmap ->
            binding.mainImage.setImageBitmap(bitmap)
        }

        supportFragmentManager.commit {
            replace(R.id.frame, RotateFragment::class.java, null)
        }

        val buttonList = listOf(
            ButtonModel("Rotate"),
            ButtonModel("Scale"),
            ButtonModel("Grayscale"),
            ButtonModel("Contrast"),
            ButtonModel("Pixelate"),
            ButtonModel("Sepia"),
            ButtonModel("Solarize"),
            ButtonModel("Dither"),
            ButtonModel("Edge"),
            ButtonModel("Blur"),
            ButtonModel("Glass"),
            ButtonModel("Oil"),
            ButtonModel("Emboss"),
            ButtonModel("Wave"),
            ButtonModel("Mask"),
            ButtonModel("Encode1"),
            ButtonModel("Decode1"),
            ButtonModel("Encode2"),
            ButtonModel("Decode2"),
            ButtonModel("Encode3"),
            ButtonModel("Decode3"),
            ButtonModel("Face gaRecognition"),
            ButtonModel("")
        )
        binding.galleryButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }
        binding.imageButton7.setOnClickListener {
            val bitmap = sharedViewModel.bitmap.value
            if (bitmap != null) {
                saveImageToGallery(bitmap)
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ButtonAdapter(buttonList, supportFragmentManager)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}