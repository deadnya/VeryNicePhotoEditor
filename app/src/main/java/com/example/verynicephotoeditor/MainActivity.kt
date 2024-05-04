package com.example.verynicephotoeditor

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.verynicephotoeditor.databinding.ActivityMainBinding
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.truncate
import kotlin.random.Random
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import java.lang.Math.pow


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("ClickableViewAccessibility")
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
                    READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                    val bitmap = drawableToBitmap(binding.imageView.drawable)
                    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                    val x = ((event.x) * 225 / 1000).toInt()
                    val y = ((event.y) * 225 / 1000).toInt()

                    if (x in 0 until mutableBitmap.width && y in 0 until mutableBitmap.height) {
                        binding.imageView.setImageBitmap(applyEditing(bitmap, x, y, 10.0, 0.4))
                    }
                }
            }
            true
        }
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

    private fun applyEditing(bitmap: Bitmap, x: Int, y: Int, brushSize: Double, strength: Double) : Bitmap {

        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var pixelCount = 0

        for (xx in x - Math.round(brushSize).toInt() + 1 .. (x + Math.round(brushSize).toInt() + 1)) {
            for (yy in y - Math.round(brushSize).toInt() + 1 .. (y + Math.round(brushSize).toInt() + 1)) {

                val currX = Math.max(Math.min(xx, bitmap.width - 1), 0)
                val currY = Math.max(Math.min(yy, bitmap.height - 1), 0)

                val dist = dist(x, y, xx, yy)

                if (dist <= brushSize) {

                    val pixel = bitmap.getPixel(currX, currY)

                    redSum += Color.red(pixel)
                    greenSum += Color.green(pixel)
                    blueSum += Color.blue(pixel)
                    pixelCount++
                }
            }
        }

        val newRed = redSum / pixelCount
        val newGreen = greenSum / pixelCount
        val newBlue = blueSum / pixelCount

        for (xx in x - Math.round(brushSize).toInt() + 1 .. (x + Math.round(brushSize).toInt() + 1)) {
            for (yy in y - Math.round(brushSize).toInt() + 1 .. (y + Math.round(brushSize).toInt() + 1)) {

                val currX = Math.max(Math.min(xx, bitmap.width - 1), 0)
                val currY = Math.max(Math.min(yy, bitmap.height - 1), 0)

                val dist = dist(x, y, xx, yy)

                if (dist <= brushSize) {

                    val pixel = bitmap.getPixel(currX, currY)

                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    val redDiff = Math.round((newRed - red) * (brushSize - dist) / brushSize * strength).toInt()
                    val greenDiff = Math.round((newGreen - green) * (brushSize - dist) / brushSize * strength).toInt()
                    val blueDiff = Math.round((newBlue - blue) * (brushSize - dist) / brushSize * strength).toInt()

                    val newPixel = Color.argb(
                        255,
                        red + redDiff,
                        green + greenDiff,
                        blue + blueDiff
                    )

                    mutableBitmap.setPixel(
                        currX,
                        currY,
                        newPixel
                    )
                }
            }
        }

        return mutableBitmap
    }

    private fun dist(x1: Int, y1: Int, x2: Int, y2: Int) : Double {
        return sqrt(((x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0)))
    }
}
