package com.example.verynicephotoeditor

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.ui.AppBarConfiguration
import androidx.appcompat.app.AppCompatActivity
import com.example.verynicephotoeditor.databinding.ActivityMainBinding
import kotlin.math.truncate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.applyFilter.setOnClickListener {
            val bitmap = drawableToBitmap(binding.imageView.drawable)
            val grayscaledBitmap = applyGrayscaleFilter(bitmap)

            val contrastedBitmap = applyContrastFilter(bitmap);
            binding.imageView.setImageBitmap(contrastedBitmap);
        }

    }

    private fun applyGrayscaleFilter(bitmap: Bitmap): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {

            val pixel = srcPixels[i]
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

            destPixels[i] = Color.argb(Color.alpha(pixel), gray, gray, gray)
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun applyContrastFilter(bitmap: Bitmap, value: Float = 100.0f) : Bitmap {

        val factor = (259.0f * (value + 255.0f)) / (255.0f * (259.0f - value))

        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {
            val pixel = srcPixels[i]
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            destPixels[i] = Color.argb(Color.alpha(pixel),
                0.coerceAtLeast(255.coerceAtMost(truncate(factor * (r - 128) + 128).toInt())),
                0.coerceAtLeast(255.coerceAtMost(truncate(factor * (g - 128) + 128).toInt())),
                0.coerceAtLeast(255.coerceAtMost(truncate(factor * (b - 128) + 128).toInt()))
            )
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }
}