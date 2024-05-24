package com.example.verynicephotoeditor.algorithms.task8

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Affines {

    private var idkSmallX = 0.0
    private var idkSmallY = 0.0
    private var idkBigX = 0.0
    private var idkBigY = 0.0
    private var iter = 0

    fun affinTransmutation(bitmap: Bitmap, dotsList: List<Dot>): Bitmap {

        val src = dotsList.subList(0, 3)
        val dst = dotsList.subList(3, 6)

        listOf(
            Dot(
                bitmap.width / 2 + (dotsList[0].getX() - bitmap.width / 2) / 100,
                bitmap.height / 2 + (dotsList[0].getY() - bitmap.height / 2) / 100
            ),

            Dot(
                bitmap.width / 2 + (dotsList[1].getX() - bitmap.width / 2) / 100,
                bitmap.height / 2 + (dotsList[1].getY() - bitmap.height / 2) / 100
            ),

            Dot(
                bitmap.width / 2 + (dotsList[2].getX() - bitmap.width / 2) / 100,
                bitmap.height / 2 + (dotsList[2].getY() - bitmap.height / 2) / 100
            )
        )

        listOf(
            Dot(
                bitmap.width / 2 - (dotsList[0].getX() - bitmap.width / 2) / 100,
                bitmap.height / 2 - (dotsList[0].getY() - bitmap.height / 2) / 100
            ),

            Dot(
                bitmap.width / 2 - (dotsList[1].getX() - bitmap.width / 2) / 100,
                bitmap.height / 2 - (dotsList[1].getY() - bitmap.height / 2) / 100
            ),

            Dot(
                bitmap.width / 2 - (dotsList[2].getX() - bitmap.width / 2) / 100,
                bitmap.height / 2 - (dotsList[2].getY() - bitmap.height / 2) / 100
            )
        )

        val matrix = calculateAffineTransformation(src, dst)

        val (scaleUpX, scaleUpY) = isImageBiggerOnXorYAxis(bitmap, matrix)

        return if (!scaleUpX && !scaleUpY) {

            getBilinearTransformedBitmap(bitmap, matrix)
        } else {
            getBilinearTransformedBitmap(bitmap, matrix)
        }
    }

    private fun getBilinearTransformedBitmap(bitmap: Bitmap, matrix: DoubleArray): Bitmap {

        val inverseMatrix = calculateInverse(matrix)

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

        if (iter == 0) {
            idkSmallX = newMinY
            idkSmallY = newMinY
            iter++
        } else {
            idkBigX = newMinY
            idkBigY = newMinY
            iter--
        }

        val result = Bitmap.createBitmap(
            (newMaxX - newMinX + 1).toInt(),
            (newMaxY - newMinY + 1).toInt(),
            bitmap.config
        )

        for (y in newMinY.toInt()..newMaxY.toInt()) {
            for (x in newMinX.toInt()..newMaxX.toInt()) {
                val oldX =
                    (inverseMatrix[0] * x + inverseMatrix[1] * y + inverseMatrix[2]).toFloat()
                val oldY =
                    (inverseMatrix[3] * x + inverseMatrix[4] * y + inverseMatrix[5]).toFloat()

                if (oldX.toInt() in 0 until bitmap.width && oldY.toInt() in 0 until bitmap.height) {
                    val pixel = bilinearInterpolation(oldX, oldY, bitmap)

                    val safeY = min(y - newMinY.toInt(), bitmap.height - 1)
                    result.setPixel(x - newMinX.toInt(), safeY, pixel)
                }
            }
        }

        return result
    }

    private fun isImageBiggerOnXorYAxis(
        bitmap: Bitmap,
        matrix: DoubleArray
    ): Pair<Boolean, Boolean> {
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
        return sqrt(dx * dx + dy * dy)
    }

    private fun calculateAffineTransformation(src: List<Dot>, dst: List<Dot>): DoubleArray {
        val matrix = DoubleArray(6)

        val denominator =
            (src[0].getX() - src[2].getX()) * (src[1].getY() - src[2].getY()) - (src[1].getX() - src[2].getX()) * (src[0].getY() - src[2].getY())

        matrix[0] =
            (((dst[0].getX() - dst[2].getX()) * (src[1].getY() - src[2].getY()) - (dst[1].getX() - dst[2].getX()) * (src[0].getY() - src[2].getY())) / denominator).toDouble()
        matrix[1] =
            (((dst[1].getX() - dst[2].getX()) * (src[0].getX() - src[2].getX()) - (dst[0].getX() - dst[2].getX()) * (src[1].getX() - src[2].getX())) / denominator).toDouble()
        matrix[2] =
            ((src[2].getX() * (dst[1].getX() - dst[0].getX()) + src[2].getY() * (dst[0].getX() - dst[2].getX()) + src[0].getX() * dst[1].getX() - src[1].getX() * dst[0].getX()) / denominator).toDouble()

        matrix[3] =
            (((dst[0].getY() - dst[2].getY()) * (src[1].getY() - src[2].getY()) - (dst[1].getY() - dst[2].getY()) * (src[0].getY() - src[2].getY())) / denominator).toDouble()
        matrix[4] =
            (((dst[1].getY() - dst[2].getY()) * (src[0].getX() - src[2].getX()) - (dst[0].getY() - dst[2].getY()) * (src[1].getX() - src[2].getX())) / denominator).toDouble()
        matrix[5] =
            ((src[2].getX() * (dst[1].getY() - dst[0].getY()) + src[2].getY() * (dst[0].getY() - dst[2].getY()) + src[0].getX() * dst[1].getY() - src[1].getX() * dst[0].getY()) / denominator).toDouble()

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

    fun drawDot(mutableBitmap: Bitmap, dotSize: Int, x: Int, y: Int, color: Int) {

        for (xx in x - dotSize + 1..<x + dotSize) {
            for (yy in y - dotSize + 1..<y + dotSize) {

                val currX = xx.coerceAtMost(mutableBitmap.width - 1).coerceAtLeast(0)
                val currY = yy.coerceAtMost(mutableBitmap.height - 1).coerceAtLeast(0)

                if (dist(
                        xx.toDouble(),
                        yy.toDouble(),
                        x.toDouble(),
                        y.toDouble()
                    ) < dotSize
                ) {

                    mutableBitmap.setPixel(currX, currY, color)
                }
            }
        }
    }

    private fun dist(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt(((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0)))
    }
}

class Dot(
    private val x: Float,
    private val y: Float
) {

    fun getX(): Float {
        return x
    }

    fun getY(): Float {
        return y
    }
}