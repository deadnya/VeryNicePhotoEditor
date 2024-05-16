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
import kotlinx.coroutines.runBlocking
import java.util.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


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
        var size = Point()
        display.getSize(size)

        val layoutParams = binding.imageView.layoutParams
        layoutParams.width = size.x
        layoutParams.height = size.y
        binding.imageView.layoutParams = layoutParams

        val scale = 2
        val width = size.x / scale
        val height = size.y / scale

        size = Point(width, height)

        binding.imageView.setImageBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
        val bitmap = drawableToBitmap(binding.imageView.drawable)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val cube = Cube(
            Dot(-1.0, -1.0, -1.0),
            Dot(1.0, -1.0, -1.0),
            Dot(1.0, 1.0, -1.0),
            Dot(-1.0, 1.0, -1.0),
            Dot(-1.0, -1.0, 1.0),
            Dot(1.0, -1.0, 1.0),
            Dot(1.0, 1.0, 1.0),
            Dot(-1.0, 1.0, 1.0)
        )

        drawCube(mutableBitmap, cube, size)
        binding.imageView.setImageBitmap(mutableBitmap)

        var startX = 0.0
        var startY = 0.0

        var distX = 0.0
        var distY = 0.0

        binding.imageView.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    startX = (event.x / scale).toDouble()
                    startY = (event.y / scale).toDouble()
                }

                MotionEvent.ACTION_MOVE -> {
                    distX = (event.x / scale).toDouble() - startX
                    distY = (event.y / scale).toDouble() - startY

                    startX += distX
                    startY += distY

                    cube.rotateX(distY)
                    cube.rotateY(-distX)

                    mutableBitmap.eraseColor(Color.WHITE);
                    drawCube(mutableBitmap, cube, size)
                    binding.imageView.setImageBitmap(mutableBitmap)
                }

                MotionEvent.ACTION_UP -> {

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

    private fun drawCube(mutableBitmap: Bitmap, cube: Cube, size: Point) {

        val translatedCube = Cube(
            Dot(cube.dot1.x, cube.dot1.y, cube.dot1.z + 1),
            Dot(cube.dot2.x, cube.dot2.y, cube.dot2.z + 1),
            Dot(cube.dot3.x, cube.dot3.y, cube.dot3.z + 1),
            Dot(cube.dot4.x, cube.dot4.y, cube.dot4.z + 1),
            Dot(cube.dot5.x, cube.dot5.y, cube.dot5.z + 1),
            Dot(cube.dot6.x, cube.dot6.y, cube.dot6.z + 1),
            Dot(cube.dot7.x, cube.dot7.y, cube.dot7.z + 1),
            Dot(cube.dot8.x, cube.dot8.y, cube.dot8.z + 1),
        )

        drawDot(mutableBitmap, 10, translatedCube.dot1, size)
        drawDot(mutableBitmap, 10, translatedCube.dot2, size)
        drawDot(mutableBitmap, 10, translatedCube.dot3, size)
        drawDot(mutableBitmap, 10, translatedCube.dot4, size)
        drawDot(mutableBitmap, 10, translatedCube.dot5, size)
        drawDot(mutableBitmap, 10, translatedCube.dot6, size)
        drawDot(mutableBitmap, 10, translatedCube.dot7, size)
        drawDot(mutableBitmap, 10, translatedCube.dot8, size)

        val stroke = 2

        drawLine(mutableBitmap, translatedCube.dot1, translatedCube.dot5, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot2, translatedCube.dot6, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot3, translatedCube.dot7, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot4, translatedCube.dot8, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot1, translatedCube.dot2, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot2, translatedCube.dot3, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot3, translatedCube.dot4, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot4, translatedCube.dot1, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot5, translatedCube.dot6, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot6, translatedCube.dot7, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot7, translatedCube.dot8, stroke, size)
        drawLine(mutableBitmap, translatedCube.dot8, translatedCube.dot5, stroke, size)
    }

    private fun drawDot(mutableBitmap: Bitmap, dotSize: Int, dot: Dot, size: Point) {

        val x = (size.x / 2 + dot.x * 100).toInt()
        val y = (size.y / 2 + dot.y * 100).toInt()

        for (xx in x - dotSize + 1..<x + dotSize) {
            for (yy in y - dotSize + 1..<y + dotSize) {

                val currX = Math.max(Math.min(xx, mutableBitmap.width - 1), 0)
                val currY = Math.max(Math.min(yy, mutableBitmap.height - 1), 0)

                if (dist(
                        xx.toDouble(),
                        yy.toDouble(),
                        x.toDouble(),
                        y.toDouble()
                    ) < dotSize) {

                    mutableBitmap.setPixel(currX, currY, Color.argb(
                        255,
                        0,
                        0,
                        0
                    ))
                }
            }
        }
    }

    private fun drawLine(mutableBitmap: Bitmap, dot1: Dot, dot2: Dot, strokeSize: Int, size: Point) {

        val x1 = (size.x / 2 + dot1.x * 100).toInt()
        val y1 = (size.y / 2 + dot1.y * 100).toInt()
        val x2 = (size.x / 2 + dot2.x * 100).toInt()
        val y2 = (size.y / 2 + dot2.y * 100).toInt()

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

    private fun dist(x1: Double, y1: Double, x2: Double, y2: Double) : Double {
        return sqrt(((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0)))
    }

    private fun dist3d(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) : Double {
        return sqrt(((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0) + (z1 - z2).pow(2.0)))
    }
}

class Cube(
    var dot1 : Dot,
    var dot2 : Dot,
    var dot3 : Dot,
    var dot4 : Dot,
    var dot5 : Dot,
    var dot6 : Dot,
    var dot7 : Dot,
    var dot8 : Dot
) {

    // 2000px swipe = full rotation
    private val distToAngle = 2 * PI / 2000.0

    // to prevent cube from scaling on
    private val maxDist = 1.73205080757

    var m = mutableListOf(
        mutableListOf(0.0, 0.0, 0.0, 0.0),
        mutableListOf(0.0, 0.0, 0.0, 0.0),
        mutableListOf(0.0, 0.0, 0.0, 0.0),
        mutableListOf(0.0, 0.0, 0.0, 0.0)
    )

    fun rotateX(distance: Double) {

        val angle = distToAngle * distance

        val matrix = m.toMutableList()

        matrix[0][0] = 1.0
        matrix[1][1] = cos(angle * 0.5)
        matrix[1][2] = sin(angle * 0.5)
        matrix[2][1] = -sin(angle * 0.5)
        matrix[2][2] = cos(angle * 0.5)
        matrix[3][3] = 1.0

        dot1 = matrixMultiplication(dot1, matrix)
        dot2 = matrixMultiplication(dot2, matrix)
        dot3 = matrixMultiplication(dot3, matrix)
        dot4 = matrixMultiplication(dot4, matrix)
        dot5 = matrixMultiplication(dot5, matrix)
        dot6 = matrixMultiplication(dot6, matrix)
        dot7 = matrixMultiplication(dot7, matrix)
        dot8 = matrixMultiplication(dot8, matrix)
    }

    fun rotateY(distance: Double) {

        val angle = distToAngle * distance

        val matrix = m.toMutableList()

        matrix[0][0] = cos(angle * 0.5)
        matrix[0][2] = -sin(angle * 0.5)
        matrix[2][0] = sin(angle * 0.5)
        matrix[1][1] = 1.0
        matrix[2][2] = cos(angle * 0.5)
        matrix[3][3] = 1.0

        dot1 = matrixMultiplication(dot1, matrix)
        dot2 = matrixMultiplication(dot2, matrix)
        dot3 = matrixMultiplication(dot3, matrix)
        dot4 = matrixMultiplication(dot4, matrix)
        dot5 = matrixMultiplication(dot5, matrix)
        dot6 = matrixMultiplication(dot6, matrix)
        dot7 = matrixMultiplication(dot7, matrix)
        dot8 = matrixMultiplication(dot8, matrix)
    }

    fun rotateZ(distance: Double) {

        val angle = distToAngle * distance

        val matrix = m.toMutableList()

        matrix[0][0] = cos(angle * 0.5)
        matrix[0][1] = sin(angle * 0.5)
        matrix[1][0] = -sin(angle * 0.5)
        matrix[1][1] = cos(angle * 0.5)
        matrix[2][2] = 1.0
        matrix[3][3] = 1.0

        dot1 = matrixMultiplication(dot1, matrix)
        dot2 = matrixMultiplication(dot2, matrix)
        dot3 = matrixMultiplication(dot3, matrix)
        dot4 = matrixMultiplication(dot4, matrix)
        dot5 = matrixMultiplication(dot5, matrix)
        dot6 = matrixMultiplication(dot6, matrix)
        dot7 = matrixMultiplication(dot7, matrix)
        dot8 = matrixMultiplication(dot8, matrix)
    }

    fun matrixMultiplication(dot: Dot, matrix: MutableList<MutableList<Double>>) : Dot {

        val result = Dot(
            dot.x * matrix[0][0] + dot.y * matrix[1][0] + dot.z * matrix[2][0] + matrix[3][0],
            dot.x * matrix[0][1] + dot.y * matrix[1][1] + dot.z * matrix[2][1] + matrix[3][1],
            dot.x * matrix[0][2] + dot.y * matrix[1][2] + dot.z * matrix[2][2] + matrix[3][2]
        )

        val w = dot.x * matrix[0][3] + dot.y * matrix[1][3] + dot.z * matrix[2][3] + matrix[3][3]

        if (w != 0.0) {
            result.x /= w
            result.y /= w
            result.z /= w
        }

        return result
    }
}

open class Dot(
    var x: Double,
    var y: Double,
    var z: Double
)