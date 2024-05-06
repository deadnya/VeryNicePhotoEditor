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
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.verynicephotoeditor.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


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

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val layoutParams = binding.imageView.layoutParams
        layoutParams.width = size.x
        layoutParams.height = size.y
        binding.imageView.layoutParams = layoutParams

        val scale = 1
        val width = size.x / scale
        val height = size.y / scale

        binding.imageView.setImageBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
        val bitmap = drawableToBitmap(binding.imageView.drawable)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val dotsList = mutableListOf<Dot>()

        var startX : Float
        var startY : Float

        var endX : Float
        var endY : Float

        var traversedDist = 0.0f

        binding.imageView.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    startX = event.x / scale
                    startY = event.y / scale

                    dotsList.add(Dot(startX, startY))

                    drawDot(mutableBitmap, 20, startX.toInt(), startY.toInt())

                    updateBitmap(dotsList, mutableBitmap)
                }

                MotionEvent.ACTION_MOVE -> {

                }

                MotionEvent.ACTION_UP -> {
                    endX = event.x
                    endY = event.y
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

    private fun drawDot(mutableBitmap: Bitmap, dotSize: Int, x: Int, y: Int) {

        for (xx in x - dotSize + 1..<x + dotSize) {
            for (yy in y - dotSize + 1..<y + dotSize) {

                val currX = Math.max(Math.min(xx, mutableBitmap.width - 1), 0)
                val currY = Math.max(Math.min(yy, mutableBitmap.height - 1), 0)

                if (dist(xx, yy, x, y) < dotSize) {
                    mutableBitmap.setPixel(currX, currY, Color.argb(
                        255,
                        0,
                        0,
                        0
                    ))
                }
            }
        }

        binding.imageView.setImageBitmap(mutableBitmap)
    }

    private fun dist(x1: Int, y1: Int, x2: Int, y2: Int) : Double {
        return sqrt(((x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0)))
    }

    private fun updateBitmap(dotsList: MutableList<Dot>, mutableBitmap: Bitmap) {

        if (dotsList.size > 3 && dotsList.size % 3 == 1) {

            for (i in dotsList.indices step 3) {

                if (i == dotsList.size - 1) break

                val dot0 = dotsList[i]
                val dot1 = dotsList[i + 1]
                val dot2 = dotsList[i + 2]
                val dot3 = dotsList[i + 3]

                var prevDot = dotsList[i]

                val iterations = 100

                for (time in 0..iterations) {

                    val t = time.toFloat() / iterations

                    val sqrDot0 = getDotAt(dot0, dot1, t)
                    val sqrDot1 = getDotAt(dot1, dot2, t)
                    val sqrDot2 = getDotAt(dot2, dot3, t)

                    val cubDot0 = getDotAt(sqrDot0, sqrDot1, t)
                    val cubDot1 = getDotAt(sqrDot1, sqrDot2, t)

                    val dot = getDotAt(cubDot0, cubDot1, t)

                    drawLine(mutableBitmap, prevDot, dot, 2)
                    prevDot = dot
                }
            }

        }

        binding.imageView.setImageBitmap(mutableBitmap)
    }

    private fun getDotAt(d1: Dot, d2: Dot, pos: Float) : Dot{
        return Dot (
            d1.getX() + (d2.getX() - d1.getX()) * pos,
            d1.getY() + (d2.getY() - d1.getY()) * pos
        )
    }

    private fun drawLine(mutableBitmap: Bitmap, dot1: Dot, dot2: Dot, strokeSize: Int) {

        val x1 = dot1.getX().toInt()
        val y1 = dot1.getY().toInt()
        val x2 = dot2.getX().toInt()
        val y2 = dot2.getY().toInt()

        val dx = Math.abs(x2 - x1)
        val dy = Math.abs(y2 - y1)

        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1

        var err = dx - dy
        var e2: Int

        var x = x1
        var y = y1

        while (true) {
            for (i in -strokeSize..strokeSize) {
                for (j in -strokeSize..strokeSize) {
                    if (x + i in 0 until mutableBitmap.width && y + j in 0 until mutableBitmap.height) {
                        mutableBitmap.setPixel(x + i, y + j, Color.BLACK)
                    }
                }
            }

            if (x == x2 && y == y2) break

            e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x += sx
            }
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }
}

class Dot(private val x: Float, private val y: Float) {

    fun getX() : Float {
        return x
    }

    fun getY() : Float {
        return y
    }
}
