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
import android.graphics.Matrix
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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


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

        val bitmap = drawableToBitmap(binding.imageView.drawable)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val dotsList = mutableListOf<Dot>()

        binding.imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    val x = ((event.x) * 225 / 1000)
                    val y = ((event.y) * 225 / 1000)

                    dotsList.add(Dot(x, y))

                    if (dotsList.size >= 6) {
                        binding.imageView.setImageBitmap(affinTransmutation(mutableBitmap, dotsList))
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

    fun affinTransmutation(bitmap: Bitmap, dotsList: List<Dot>): Bitmap {

        val src = floatArrayOf(
            dotsList[0].getX(), dotsList[0].getY(),
            dotsList[1].getX(), dotsList[1].getY(),
            dotsList[2].getX(), dotsList[2].getY()
        )

        val dst = floatArrayOf(
            dotsList[3].getX(), dotsList[3].getY(),
            dotsList[4].getX(), dotsList[4].getY(),
            dotsList[5].getX(), dotsList[5].getY()
        )

        val matrix = Matrix()
        matrix.setPolyToPoly(src, 0, dst, 0, 3)

        var newMinX = Int.MAX_VALUE
        var newMaxX = Int.MIN_VALUE
        var newMinY = Int.MAX_VALUE
        var newMaxY = Int.MIN_VALUE

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val src = floatArrayOf(x.toFloat(), y.toFloat())
                val dst = FloatArray(2)
                matrix.mapPoints(dst, src)

                val newX = dst[0].toInt()
                val newY = dst[1].toInt()

                newMinX = minOf(newMinX, newX)
                newMaxX = maxOf(newMaxX, newX)
                newMinY = minOf(newMinY, newY)
                newMaxY = maxOf(newMaxY, newY)
            }
        }

        val result = Bitmap.createBitmap(newMaxX - newMinX, newMaxY - newMinY, bitmap.config)

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val src = floatArrayOf(x.toFloat(), y.toFloat())
                val dst = FloatArray(2)
                matrix.mapPoints(dst, src)

                val newX = max(min(dst[0].toInt() - newMinX, result.width - 1), 0)
                val newY = max(min(dst[1].toInt() - newMinY, result.height - 1), 0)

                val pixel = bitmap.getPixel(x, y)
                result.setPixel(newX, newY, pixel)
            }
        }

        return result
    }
}

class Dot(
    private val x: Float,
    private val y: Float
) {

    fun getX() : Float { return x }
    fun getY() : Float { return y}
}
