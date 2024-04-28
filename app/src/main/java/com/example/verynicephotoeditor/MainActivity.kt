package com.example.verynicephotoeditor

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.example.verynicephotoeditor.basicEditingAlgorithms.RotationAlgorithm
import com.example.verynicephotoeditor.basicEditingAlgorithms.ScalingAlgorithms
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.verynicephotoeditor.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val rotationAlgorithm = RotationAlgorithm()
    private val scalingAlgorithms = ScalingAlgorithms()

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                val bitmap = ImageDecoder.decodeBitmap(source)
                binding.imageView.setImageBitmap(bitmap)
                updateImageViewAndText(bitmap)
            }
        }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED,
                    WRITE_EXTERNAL_STORAGE
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val bitmap = drawableToBitmap(binding.imageView.drawable)
            lifecycleScope.launch {
                val rotatedBitmap = rotationAlgorithm.rotateBitmap(bitmap, 45.0)
                binding.imageView.setImageBitmap(rotatedBitmap)
                updateImageViewAndText(rotatedBitmap)
            }
        }
        binding.button2.setOnClickListener {
            val bitmap = drawableToBitmap(binding.imageView.drawable)
            val minimizedBitmap = scalingAlgorithms.scaleImage(bitmap, 0.5)
            binding.imageView.setImageBitmap(minimizedBitmap)
            updateImageViewAndText(minimizedBitmap)
        }

        binding.button3.setOnClickListener {
            val bitmap = drawableToBitmap(binding.imageView.drawable)
            val scaledBitmap = scalingAlgorithms.scaleImage(bitmap, 2.0)
            binding.imageView.setImageBitmap(scaledBitmap)
            updateImageViewAndText(scaledBitmap)

        }
        binding.button4.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }
        binding.button5.setOnClickListener {
            //TODO: обработать проверку на наличие разрешения на сохранение фото
            saveImageToGallery()
        }

    }

    private fun saveImageToGallery() {
        val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        contentResolver.openOutputStream(uri!!).use { out ->
            if (out != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, values, null, null)
        }
    }

    private fun updateImageViewAndText(bitmap: Bitmap) {
        binding.imageView.setImageBitmap(bitmap)
        val imageSize = "Image Size: ${bitmap.width} x ${bitmap.height}"
        binding.textView.text = imageSize
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}