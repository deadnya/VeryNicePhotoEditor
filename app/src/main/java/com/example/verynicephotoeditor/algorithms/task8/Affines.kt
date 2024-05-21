package com.example.verynicephotoeditor.algorithms.task8

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Affines {

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

                    result.setPixel(x - newMinX.toInt(), y - newMinY.toInt(), pixel)
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

        val denominator = (src[0].getX() - src[2].getX()) * (src[1].getY() - src[2].getY()) - (src[1].getX() - src[2].getX()) * (src[0].getY() - src[2].getY())

        matrix[0] = (((dst[0].getX() - dst[2].getX()) * (src[1].getY() - src[2].getY()) - (dst[1].getX() - dst[2].getX()) * (src[0].getY() - src[2].getY())) / denominator).toDouble()
        matrix[1] = (((dst[1].getX() - dst[2].getX()) * (src[0].getX() - src[2].getX()) - (dst[0].getX() - dst[2].getX()) * (src[1].getX() - src[2].getX())) / denominator).toDouble()
        matrix[2] = ((src[2].getX() * (dst[1].getX() - dst[0].getX()) + src[2].getY() * (dst[0].getX() - dst[2].getX()) + src[0].getX() * dst[1].getX() - src[1].getX() * dst[0].getX()) / denominator).toDouble()

        matrix[3] = (((dst[0].getY() - dst[2].getY()) * (src[1].getY() - src[2].getY()) - (dst[1].getY() - dst[2].getY()) * (src[0].getY() - src[2].getY())) / denominator).toDouble()
        matrix[4] = (((dst[1].getY() - dst[2].getY()) * (src[0].getX() - src[2].getX()) - (dst[0].getY() - dst[2].getY()) * (src[1].getX() - src[2].getX())) / denominator).toDouble()
        matrix[5] = ((src[2].getX() * (dst[1].getY() - dst[0].getY()) + src[2].getY() * (dst[0].getY() - dst[2].getY()) + src[0].getX() * dst[1].getY() - src[1].getX() * dst[0].getY()) / denominator).toDouble()

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

    private fun trilinearInterpolation(x: Float, y: Float, z: Float, bitmap: Bitmap): Float {
        val x1 = x.toInt()
        val y1 = y.toInt()
        val z1 = z.toInt()
        val x2 = x1 + 1
        val y2 = y1 + 1
        val z2 = z1 + 1

        val c000 = bitmap.getPixel(x1, y1)
        val c001 = bitmap.getPixel(x1, y1)
        val c010 = bitmap.getPixel(x1, y2)
        val c011 = bitmap.getPixel(x1, y2)
        val c100 = bitmap.getPixel(x2, y1)
        val c101 = bitmap.getPixel(x2, y1)
        val c110 = bitmap.getPixel(x2, y2)
        val c111 = bitmap.getPixel(x2, y2)

        val c00 = ((x2 - x) / (x2 - x1)) * c000 + ((x - x1) / (x2 - x1)) * c100
        val c01 = ((x2 - x) / (x2 - x1)) * c001 + ((x - x1) / (x2 - x1)) * c101
        val c10 = ((x2 - x) / (x2 - x1)) * c010 + ((x - x1) / (x2 - x1)) * c110
        val c11 = ((x2 - x) / (x2 - x1)) * c011 + ((x - x1) / (x2 - x1)) * c111

        val c0 = ((y2 - y) / (y2 - y1)) * c00 + ((y - y1) / (y2 - y1)) * c10
        val c1 = ((y2 - y) / (y2 - y1)) * c01 + ((y - y1) / (y2 - y1)) * c11

        return ((z2 - z) / (z2 - z1)) * c0 + ((z - z1) / (z2 - z1)) * c1
    }
}

class Dot(
    private val x: Float,
    private val y: Float
) {

    fun getX() : Float { return x }
    fun getY() : Float { return y}
}