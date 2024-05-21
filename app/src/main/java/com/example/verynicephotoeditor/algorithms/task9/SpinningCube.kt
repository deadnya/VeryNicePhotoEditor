package com.example.verynicephotoeditor.algorithms.task9

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Point
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class SpinningCube {

    fun drawCube(mutableBitmap: Bitmap, cube: Cube, size: Point, image: Bitmap) {

        val translatedCube = Cube(
            Dot(cube.dot1.x, cube.dot1.y, cube.dot1.z),
            Dot(cube.dot2.x, cube.dot2.y, cube.dot2.z),
            Dot(cube.dot3.x, cube.dot3.y, cube.dot3.z),
            Dot(cube.dot4.x, cube.dot4.y, cube.dot4.z),
            Dot(cube.dot5.x, cube.dot5.y, cube.dot5.z),
            Dot(cube.dot6.x, cube.dot6.y, cube.dot6.z),
            Dot(cube.dot7.x, cube.dot7.y, cube.dot7.z),
            Dot(cube.dot8.x, cube.dot8.y, cube.dot8.z),
        )

        val canvas = Canvas(mutableBitmap)

        val startDot1 = Dot(0.0, 0.0, 1.0)
        val startDot2 = Dot( 0.0, image.height - 1.0, 1.0)
        val startDot3 = Dot(image.width - 1.0, image.height - 1.0, 1.0)
        
        val list1 = listOf(startDot1, startDot2, startDot3, cube.dot1 * 100, cube.dot2 * 100, cube.dot3 * 100)
        val list2 = listOf(startDot1, startDot2, startDot3, cube.dot1 * 100, cube.dot2 * 100, cube.dot6 * 100)
        val list3 = listOf(startDot1, startDot2, startDot3, cube.dot2 * 100, cube.dot3 * 100, cube.dot7 * 100)
        val list4 = listOf(startDot1, startDot2, startDot3, cube.dot3 * 100, cube.dot4 * 100, cube.dot8 * 100)
        val list5 = listOf(startDot1, startDot2, startDot3, cube.dot4 * 100, cube.dot1 * 100, cube.dot5 * 100)
        val list6 = listOf(startDot1, startDot2, startDot3, cube.dot5 * 100, cube.dot6 * 100, cube.dot7 * 100)

        val image1 = affinTransmutation(image, list1)
        val image2 = affinTransmutation(image, list2)
        val image3 = affinTransmutation(image, list3)
        val image4 = affinTransmutation(image, list4)
        val image5 = affinTransmutation(image, list5)
        val image6 = affinTransmutation(image, list6)

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

    fun affinTransmutation(bitmap: Bitmap, dotsList: List<Dot>): Bitmap {

        val src = dotsList.subList(0, 3)
        val dst = dotsList.subList(3, 6)

        val matrix = calculateAffineTransformation(src, dst)
        val inverseMatrix = calculateInverse(matrix)

        val (scaleUpX, scaleUpY) = isImageBiggerOnXorYAxis(bitmap, matrix)

        var newMinX = Double.MAX_VALUE
        var newMaxX = Double.MIN_VALUE
        var newMinY = Double.MAX_VALUE
        var newMaxY = Double.MIN_VALUE

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val newX = (matrix[0] * x + matrix[1] * y + matrix[2])
                val newY = (matrix[3] * x + matrix[4] * y + matrix[5])

                newMinX = minOf(newMinX, newX)
                newMaxX = maxOf(newMaxX, newX)
                newMinY = minOf(newMinY, newY)
                newMaxY = maxOf(newMaxY, newY)
            }
        }

        val result = Bitmap.createBitmap((newMaxX - newMinX + 1).toInt(), (newMaxY - newMinY + 1).toInt(), bitmap.config)

        for (y in newMinY.toInt()..newMaxY.toInt()) {
            for (x in newMinX.toInt()..newMaxX.toInt()) {
                val oldX = (inverseMatrix[0] * x + inverseMatrix[1] * y + inverseMatrix[2]).toFloat()
                val oldY = (inverseMatrix[3] * x + inverseMatrix[4] * y + inverseMatrix[5]).toFloat()

                if (oldX.toInt() in 0 until bitmap.width && oldY.toInt() in 0 until bitmap.height) {
                    //val pixelX = if (scaleUpX) {
                    //bilinearInterpolation(oldX, oldY, bitmap)
                    //} else {
                    //trilinearInterpolation(oldX, oldY, 1f, bitmap)
                    //}

                    //val pixelY = if (scaleUpY) {
                    //bilinearInterpolation(oldX, oldY, bitmap)
                    //} else {
                    //trilinearInterpolation(oldX, oldY, 1f, bitmap)
                    //}

                    val pixel = bilinearInterpolation(oldX, oldY, bitmap)

                    val currX = max(min(x - newMinX.toInt(), result.width - 1), 0)
                    val currY = max(min(y - newMinY.toInt(), result.height - 1), 0)

                    result.setPixel(currX, currY, pixel)
                }
            }
        }

        return result
    }

    private fun isImageBiggerOnXorYAxis(bitmap: Bitmap, matrix: DoubleArray): Pair<Boolean, Boolean> {
        val corners = listOf(
            floatArrayOf(0f, 0f),
            floatArrayOf(bitmap.width.toFloat(), 0f),
            floatArrayOf(bitmap.width.toFloat(), bitmap.height.toFloat()),
            floatArrayOf(0f, bitmap.height.toFloat())
        )

        val transformedCorners = corners.map { corner ->
            val x = (matrix[0] * corner[0] + matrix[1] * corner[1] + matrix[2]).toFloat()
            val y = (matrix[3] * corner[0] + matrix[4] * corner[1] + matrix[5]).toFloat()
            floatArrayOf(x, y)
        }

        val transformedWidth = distance(transformedCorners[0], transformedCorners[1])
        val transformedHeight = distance(transformedCorners[1], transformedCorners[2])

        return Pair(transformedWidth > bitmap.width, transformedHeight > bitmap.height)
    }

    private fun distance(point1: FloatArray, point2: FloatArray): Float {
        val dx = point2[0] - point1[0]
        val dy = point2[1] - point1[1]
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    private fun calculateAffineTransformation(src: List<Dot>, dst: List<Dot>): DoubleArray {
        val matrix = DoubleArray(6)

        val denominator = (src[0].x - src[2].x) * (src[1].y - src[2].y) - (src[1].x - src[2].x) * (src[0].y - src[2].y)

        matrix[0] = (((dst[0].x - dst[2].x) * (src[1].y - src[2].y) - (dst[1].x - dst[2].x) * (src[0].y - src[2].y)) / denominator).toDouble()
        matrix[1] = (((dst[1].x - dst[2].x) * (src[0].x - src[2].x) - (dst[0].x - dst[2].x) * (src[1].x - src[2].x)) / denominator).toDouble()
        matrix[2] = ((src[2].x * (dst[1].x - dst[0].x) + src[2].y * (dst[0].x - dst[2].x) + src[0].x * dst[1].x - src[1].x * dst[0].x) / denominator).toDouble()

        matrix[3] = (((dst[0].y - dst[2].y) * (src[1].y - src[2].y) - (dst[1].y - dst[2].y) * (src[0].y - src[2].y)) / denominator).toDouble()
        matrix[4] = (((dst[1].y - dst[2].y) * (src[0].x - src[2].x) - (dst[0].y - dst[2].y) * (src[1].x - src[2].x)) / denominator).toDouble()
        matrix[5] = ((src[2].x * (dst[1].y - dst[0].y) + src[2].y * (dst[0].y - dst[2].y) + src[0].x * dst[1].y - src[1].x * dst[0].y) / denominator).toDouble()

        return matrix
    }

    private fun calculateInverse(matrix: DoubleArray): DoubleArray {
        val det = matrix[0] * matrix[4] - matrix[1] * matrix[3]

        return doubleArrayOf(
            matrix[4] / det,
            -matrix[1] / det,
            (matrix[1] * matrix[5] - matrix[2] * matrix[4]) / det,
            -matrix[3] / det,
            matrix[0] / det,
            (matrix[2] * matrix[3] - matrix[0] * matrix[5]) / det
        )
    }

    private fun bilinearInterpolation(x: Float, y: Float, bitmap: Bitmap): Int {
        val x1 = max(min(floor(x).toInt(), bitmap.width - 1), 0)
        val y1 = max(min(floor(y).toInt(), bitmap.height - 1), 0)
        val x2 = max(min(ceil(x).toInt(), bitmap.width - 1), 0)
        val y2 = max(min(ceil(y).toInt(), bitmap.height - 1), 0)

        val percentX = max(min(x - x1, 1f), 0f)
        val revPercentX = 1 - percentX
        val percentY = max(min(x - x1, 1f), 0f)
        val revPercentY = 1 - percentY

        val q11 = bitmap.getPixel(x1, y1)
        val q12 = bitmap.getPixel(x1, y2)
        val q21 = bitmap.getPixel(x2, y1)
        val q22 = bitmap.getPixel(x2, y2)

        val r1 = Color.argb(
            (percentX * Color.alpha(q11) + revPercentX * Color.alpha(q21)).toInt(),
            (percentX * Color.red(q11) + revPercentX * Color.red(q21)).toInt(),
            (percentX * Color.green(q11) + revPercentX * Color.green(q21)).toInt(),
            (percentX * Color.blue(q11) + revPercentX * Color.blue(q21)).toInt()
        )

        val r2 = Color.argb(
            (percentX * Color.alpha(q12) + revPercentX * Color.alpha(q22)).toInt(),
            (percentX * Color.red(q12) + revPercentX * Color.red(q22)).toInt(),
            (percentX * Color.green(q12) + revPercentX * Color.green(q22)).toInt(),
            (percentX * Color.blue(q12) + revPercentX * Color.blue(q22)).toInt()
        )

        val ans = Color.argb(
            (percentY * Color.alpha(r1) + revPercentY * Color.alpha(r2)).toInt(),
            (percentY * Color.red(r1) + revPercentY * Color.red(r2)).toInt(),
            (percentY * Color.green(r1) + revPercentY * Color.green(r2)).toInt(),
            (percentY * Color.blue(r1) + revPercentY * Color.blue(r2)).toInt()
        )

        return ans
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
) {
    operator fun times(i: Int): Dot {
        return Dot(x * i, y * i, z)
    }
}