package com.example.verynicephotoeditor.basicEditingAlgorithms

import android.graphics.Bitmap
import android.graphics.Color

class ScalingAlgorithms {
    fun scaleImage(bitmap: Bitmap, scaleFactor: Double): Bitmap {
        val width = (bitmap.width * scaleFactor).toInt()
        val height = (bitmap.height * scaleFactor).toInt()
        val newBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
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

        return newBitmap
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