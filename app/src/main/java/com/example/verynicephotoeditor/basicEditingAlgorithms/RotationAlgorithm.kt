package com.example.verynicephotoeditor.basicEditingAlgorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

class RotationAlgorithm {
    private var newWidth = 0
    private var newHeight = 0

    suspend fun rotateBitmap(source: Bitmap, degrees: Double): Bitmap = coroutineScope {
        val angleInRadians = Math.toRadians(degrees)
        val cos = cos(angleInRadians)
        val sin = sin(angleInRadians)

        val width = source.width
        val height = source.height

        if (newWidth == 0 && newHeight == 0 || newWidth != height && newHeight != width) {
            newWidth = (width * abs(cos) + height * abs(sin)).roundToInt()
            newHeight = (width * abs(sin) + height * abs(cos)).roundToInt()
        }

        val rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, source.config)

        val px = width / 2.0
        val py = height / 2.0

        val numCoroutines = 4
        val partHeight = newHeight / numCoroutines

        for (part in 0 until numCoroutines) {
            launch(Dispatchers.Default) {
                val startY = part * partHeight
                val endY = if (part == numCoroutines - 1) newHeight else startY + partHeight

                for (xCoordinate in 0 until newWidth) {
                    for (yCoordinate in startY until endY) {
                        val x = (xCoordinate - newWidth / 2.0) * cos + (yCoordinate - newHeight / 2.0) * sin + px
                        val y = -(xCoordinate - newWidth / 2.0) * sin + (yCoordinate - newHeight / 2.0) * cos + py

                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            val x1 = x.toInt()
                            val y1 = y.toInt()
                            val x2 = (x + 1).toInt()
                            val y2 = (y + 1).toInt()

                            val pixel1 =
                                if (x1 in 0 until width && y1 in 0 until height) source.getPixel(
                                    x1,
                                    y1
                                ) else Color.TRANSPARENT
                            val pixel2 =
                                if (x2 in 0 until width && y1 in 0 until height) source.getPixel(
                                    x2,
                                    y1
                                ) else Color.TRANSPARENT
                            val pixel3 =
                                if (x1 in 0 until width && y2 in 0 until height) source.getPixel(
                                    x1,
                                    y2
                                ) else Color.TRANSPARENT
                            val pixel4 =
                                if (x2 in 0 until width && y2 in 0 until height) source.getPixel(
                                    x2,
                                    y2
                                ) else Color.TRANSPARENT

                            val red = bilinearInterpolation(
                                Color.red(pixel1),
                                Color.red(pixel2),
                                Color.red(pixel3),
                                Color.red(pixel4),
                                x - x1,
                                y - y1
                            )
                            val green = bilinearInterpolation(
                                Color.green(pixel1),
                                Color.green(pixel2),
                                Color.green(pixel3),
                                Color.green(pixel4),
                                x - x1,
                                y - y1
                            )
                            val blue = bilinearInterpolation(
                                Color.blue(pixel1),
                                Color.blue(pixel2),
                                Color.blue(pixel3),
                                Color.blue(pixel4),
                                x - x1,
                                y - y1
                            )

                            rotatedBitmap.setPixel(xCoordinate, yCoordinate, Color.rgb(red, green, blue))
                        }
                    }
                }
            }
        }

        return@coroutineScope rotatedBitmap
    }

    private fun bilinearInterpolation(
        q11: Int,
        q12: Int,
        q21: Int,
        q22: Int,
        x: Double,
        y: Double
    ): Int {
        return ((q11 * (1 - x) * (1 - y) + q21 * x * (1 - y) + q12 * (1 - x) * y + q22 * x * y) + 0.5).toInt()
    }
}