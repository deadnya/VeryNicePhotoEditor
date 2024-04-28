package com.example.verynicephotoeditor.basicEditingAlgorithms

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ScalingAlgorithms {
    suspend fun scaleImageBicubic(bitmap: Bitmap, scaleFactor: Double): Bitmap = coroutineScope {
        val width = (bitmap.width * scaleFactor).toInt()
        val height = (bitmap.height * scaleFactor).toInt()
        val newBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        val numCoroutines = 4
        val partHeight = height / numCoroutines

        for (part in 0 until numCoroutines) {
            launch(Dispatchers.Default) {
                val startY = part * partHeight
                val endY = if (part == numCoroutines - 1) height else startY + partHeight

                for (xCoordinate in 0 until width) {
                    for (yCoordinate in startY until endY) {
                        val srcX = xCoordinate / scaleFactor
                        val srcY = yCoordinate / scaleFactor

                        val xFloor = srcX.toInt().coerceIn(1, bitmap.width - 3)
                        val yFloor = srcY.toInt().coerceIn(1, bitmap.height - 3)

                        val pixels = Array(4) { IntArray(4) }
                        for (i in -1..2) {
                            for (j in -1..2) {
                                pixels[i + 1][j + 1] = bitmap.getPixel(xFloor + i, yFloor + j)
                            }
                        }

                        val red = bicubicInterpolation(
                            pixels.map { it.map { pixel -> Color.red(pixel) }.toIntArray() }.toTypedArray(),
                            srcX - xFloor,
                            srcY - yFloor
                        )
                        val green = bicubicInterpolation(
                            pixels.map { it.map { pixel -> Color.green(pixel) }.toIntArray() }.toTypedArray(),
                            srcX - xFloor,
                            srcY - yFloor
                        )
                        val blue = bicubicInterpolation(
                            pixels.map { it.map { pixel -> Color.blue(pixel) }.toIntArray() }.toTypedArray(),
                            srcX - xFloor,
                            srcY - yFloor
                        )

                        newBitmap.setPixel(xCoordinate, yCoordinate, Color.rgb(red, green, blue))
                    }
                }
            }
        }

        return@coroutineScope newBitmap
    }

suspend fun scaleImage(bitmap: Bitmap, scaleFactor: Double): Bitmap = coroutineScope {
    val width = (bitmap.width * scaleFactor).toInt()
    val height = (bitmap.height * scaleFactor).toInt()
    val newBitmap = Bitmap.createBitmap(width, height, bitmap.config)

    val numCoroutines = 4
    val partHeight = height / numCoroutines

    for (part in 0 until numCoroutines) {
        launch(Dispatchers.Default) {
            val startY = part * partHeight
            val endY = if (part == numCoroutines - 1) height else startY + partHeight

            for (x in 0 until width) {
                for (y in startY until endY) {
                    val srcX = x / scaleFactor
                    val srcY = y / scaleFactor
                    val xWeight = srcX - srcX.toInt()
                    val yWeight = srcY - srcY.toInt()

                    val xFloor = srcX.toInt().coerceIn(0, bitmap.width - 1)
                    val yFloor = srcY.toInt().coerceIn(0, bitmap.height - 1)
                    val xCeil = (xFloor + 1).coerceIn(0, bitmap.width - 1)
                    val yCeil = (yFloor + 1).coerceIn(0, bitmap.height - 1)

                    val pixel1 = bitmap.getPixel(xFloor, yFloor)
                    val pixel2 = bitmap.getPixel(xCeil, yFloor)
                    val pixel3 = bitmap.getPixel(xFloor, yCeil)
                    val pixel4 = bitmap.getPixel(xCeil, yCeil)

                    val red = bilinearInterpolation(
                        Color.red(pixel1),
                        Color.red(pixel2),
                        Color.red(pixel3),
                        Color.red(pixel4),
                        xWeight,
                        yWeight
                    )
                    val green = bilinearInterpolation(
                        Color.green(pixel1),
                        Color.green(pixel2),
                        Color.green(pixel3),
                        Color.green(pixel4),
                        xWeight,
                        yWeight
                    )
                    val blue = bilinearInterpolation(
                        Color.blue(pixel1),
                        Color.blue(pixel2),
                        Color.blue(pixel3),
                        Color.blue(pixel4),
                        xWeight,
                        yWeight
                    )

                    newBitmap.setPixel(x, y, Color.rgb(red, green, blue))
                }
            }
        }
    }

    return@coroutineScope newBitmap
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
    private fun cubicInterpolation(v0: Int, v1: Int, v2: Int, v3: Int, x: Double): Int {
        val P = (v3 - v2) - (v0 - v1)
        val Q = (v0 - v1) - P
        val R = v2 - v0
        val S = v1

        return ((P * x * x * x + Q * x * x + R * x + S) + 0.5).toInt()
    }
    private fun bicubicInterpolation(p: Array<IntArray>, x: Double, y: Double): Int {
        val arr = IntArray(4)
        arr[0] = cubicInterpolation(p[0][0], p[1][0], p[2][0], p[3][0], y)
        arr[1] = cubicInterpolation(p[0][1], p[1][1], p[2][1], p[3][1], y)
        arr[2] = cubicInterpolation(p[0][2], p[1][2], p[2][2], p[3][2], y)
        arr[3] = cubicInterpolation(p[0][3], p[1][3], p[2][3], p[3][3], y)

        return cubicInterpolation(arr[0], arr[1], arr[2], arr[3], x)
    }
}