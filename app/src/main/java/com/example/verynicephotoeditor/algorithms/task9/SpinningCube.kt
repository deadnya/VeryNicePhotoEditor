package com.example.verynicephotoeditor.algorithms.task9

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class SpinningCube {

    fun drawCube(mutableBitmap: Bitmap, cube: Cube, size: Point, image: Bitmap) {

        val translatedCube = Cube(
            Dot(cube.dot1.x, cube.dot1.y, cube.dot1.z + 100),
            Dot(cube.dot2.x, cube.dot2.y, cube.dot2.z + 100),
            Dot(cube.dot3.x, cube.dot3.y, cube.dot3.z + 100),
            Dot(cube.dot4.x, cube.dot4.y, cube.dot4.z + 100),
            Dot(cube.dot5.x, cube.dot5.y, cube.dot5.z + 100),
            Dot(cube.dot6.x, cube.dot6.y, cube.dot6.z + 100),
            Dot(cube.dot7.x, cube.dot7.y, cube.dot7.z + 100),
            Dot(cube.dot8.x, cube.dot8.y, cube.dot8.z + 100),
        )

        val canvas = Canvas(mutableBitmap)

        val image1 = affinTransmutation(image, cube.dot1, cube.dot2, cube.dot3)
        val image2 = affinTransmutation(image, cube.dot1, cube.dot2, cube.dot6)
        val image3 = affinTransmutation(image, cube.dot2, cube.dot3, cube.dot7)
        val image4 = affinTransmutation(image, cube.dot3, cube.dot4, cube.dot8)
        val image5 = affinTransmutation(image, cube.dot4, cube.dot1, cube.dot5)
        val image6 = affinTransmutation(image, cube.dot5, cube.dot6, cube.dot7)

        val z1 = (cube.dot1.z + cube.dot3.z) / 2
        val z2 = (cube.dot1.z + cube.dot6.z) / 2
        val z3 = (cube.dot2.z + cube.dot7.z) / 2
        val z4 = (cube.dot3.z + cube.dot8.z) / 2
        val z5 = (cube.dot8.z + cube.dot1.z) / 2
        val z6 = (cube.dot5.z + cube.dot7.z) / 2

        val z = mutableListOf(
            z1,
            z2,
            z3,
            z4,
            z5,
            z6,
        )

        z.sort()

        val zDots = mutableListOf(
            translatedCube.dot1.z,
            translatedCube.dot2.z,
            translatedCube.dot3.z,
            translatedCube.dot4.z,
            translatedCube.dot5.z,
            translatedCube.dot6.z,
            translatedCube.dot7.z,
            translatedCube.dot8.z,
        )

        zDots.sort()

        if (image1 != null && (z1 == z[0] || z1 == z[1] || z1 == z[2])) {
            val minX = min(min(cube.dot1.x, cube.dot2.x), min(cube.dot3.x, cube.dot4.x))
            val minY = min(min(cube.dot1.y, cube.dot2.y), min(cube.dot3.y, cube.dot4.y))
            canvas.drawBitmap(image1, size.x / 2 + minX.toFloat() * 100, size.y / 2 + minY.toFloat() * 100, null)
        }

        if (image2 != null && (z2 == z[0] || z2 == z[1] || z2 == z[2])) {
            val minX = min(min(cube.dot1.x, cube.dot2.x), min(cube.dot6.x, cube.dot5.x))
            val minY = min(min(cube.dot1.y, cube.dot2.y), min(cube.dot6.y, cube.dot5.y))
            canvas.drawBitmap(image2, size.x / 2 + minX.toFloat() * 100, size.y / 2 + minY.toFloat() * 100, null)
        }

        if (image3 != null && (z3 == z[0] || z3 == z[1] || z3 == z[2])) {
            val minX = min(min(cube.dot2.x, cube.dot3.x), min(cube.dot7.x, cube.dot6.x))
            val minY = min(min(cube.dot2.y, cube.dot3.y), min(cube.dot7.y, cube.dot6.y))
            canvas.drawBitmap(image3, size.x / 2 + minX.toFloat() * 100, size.y / 2 + minY.toFloat() * 100, null)
        }

        if (image4 != null && (z4 == z[0] || z4 == z[1] || z4 == z[2])) {
            val minX = min(min(cube.dot3.x, cube.dot4.x), min(cube.dot8.x, cube.dot7.x))
            val minY = min(min(cube.dot3.y, cube.dot4.y), min(cube.dot8.y, cube.dot7.y))
            canvas.drawBitmap(image4, size.x / 2 + minX.toFloat() * 100, size.y / 2 + minY.toFloat() * 100, null)
        }

        if (image5 != null && (z5 == z[0] || z5 == z[1] || z5 == z[2])) {
            val minX = min(min(cube.dot4.x, cube.dot1.x), min(cube.dot5.x, cube.dot8.x))
            val minY = min(min(cube.dot4.y, cube.dot1.y), min(cube.dot5.y, cube.dot8.y))
            canvas.drawBitmap(image5, size.x / 2 + minX.toFloat() * 100, size.y / 2 + minY.toFloat() * 100, null)
        }

        if (image6 != null && (z6 == z[0] || z6 == z[1] || z6 == z[2])) {
            val minX = min(min(cube.dot5.x, cube.dot6.x), min(cube.dot7.x, cube.dot8.x))
            val minY = min(min(cube.dot5.y, cube.dot6.y), min(cube.dot7.y, cube.dot8.y))
            canvas.drawBitmap(image6, size.x / 2 + minX.toFloat() * 100, size.y / 2 + minY.toFloat() * 100, null)
        }

        if (translatedCube.dot1.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot1, size)
        if (translatedCube.dot2.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot2, size)
        if (translatedCube.dot3.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot3, size)
        if (translatedCube.dot4.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot4, size)
        if (translatedCube.dot5.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot5, size)
        if (translatedCube.dot6.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot6, size)
        if (translatedCube.dot7.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot7, size)
        if (translatedCube.dot8.z != zDots[7]) drawDot(mutableBitmap, 10, translatedCube.dot8, size)

        val stroke = 2

        if (translatedCube.dot1.z != zDots[7] && translatedCube.dot5.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot1, translatedCube.dot5, stroke, size)

        if (translatedCube.dot2.z != zDots[7] && translatedCube.dot6.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot2, translatedCube.dot6, stroke, size)

        if (translatedCube.dot3.z != zDots[7] && translatedCube.dot7.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot3, translatedCube.dot7, stroke, size)

        if (translatedCube.dot4.z != zDots[7] && translatedCube.dot8.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot4, translatedCube.dot8, stroke, size)

        if (translatedCube.dot1.z != zDots[7] && translatedCube.dot2.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot1, translatedCube.dot2, stroke, size)

        if (translatedCube.dot2.z != zDots[7] && translatedCube.dot3.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot2, translatedCube.dot3, stroke, size)

        if (translatedCube.dot3.z != zDots[7] && translatedCube.dot4.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot3, translatedCube.dot4, stroke, size)

        if (translatedCube.dot4.z != zDots[7] && translatedCube.dot1.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot4, translatedCube.dot1, stroke, size)

        if (translatedCube.dot5.z != zDots[7] && translatedCube.dot6.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot5, translatedCube.dot6, stroke, size)

        if (translatedCube.dot6.z != zDots[7] && translatedCube.dot7.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot6, translatedCube.dot7, stroke, size)

        if (translatedCube.dot7.z != zDots[7] && translatedCube.dot8.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot7, translatedCube.dot8, stroke, size)

        if (translatedCube.dot8.z != zDots[7] && translatedCube.dot5.z != zDots[7])
            drawLine(mutableBitmap, translatedCube.dot8, translatedCube.dot5, stroke, size)
    }

    fun affinTransmutation(bitmap: Bitmap, dot1: Dot, dot2: Dot, dot3: Dot): Bitmap? {

        val src = floatArrayOf(
            0.0f, 0.0f,
            0.0f, bitmap.height.toFloat() - 1.0f,
            bitmap.width.toFloat() - 1.0f, bitmap.height.toFloat() - 1.0f
        )

        val dst = floatArrayOf(
            dot1.x.toFloat() * 100, dot1.y.toFloat() * 100,
            dot2.x.toFloat() * 100, dot2.y.toFloat() * 100,
            dot3.x.toFloat() * 100, dot3.y.toFloat() * 100
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

        if (newMaxX - newMinX <= 0 || newMaxY - newMinY <= 0) return null

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

    fun drawDot(mutableBitmap: Bitmap, dotSize: Int, dot: Dot, size: Point) {

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

    fun drawLine(mutableBitmap: Bitmap, dot1: Dot, dot2: Dot, strokeSize: Int, size: Point) {

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