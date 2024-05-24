package com.example.verynicephotoeditor.algorithms.task6

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

class Retush {

    fun applyEditing(bitmap: Bitmap, x: Int, y: Int, brushSize: Double, strength: Double): Bitmap {

        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var pixelCount = 0

        for (xx in x - Math.round(brushSize).toInt() + 1..(x + Math.round(brushSize).toInt() + 1)) {
            for (yy in y - Math.round(brushSize).toInt() + 1..(y + Math.round(brushSize)
                .toInt() + 1)) {

                val currX = xx.coerceAtMost(bitmap.width - 1).coerceAtLeast(0)
                val currY = yy.coerceAtMost(bitmap.height - 1).coerceAtLeast(0)

                val dist = dist(x, y, xx, yy)

                if (dist <= brushSize) {

                    val pixel = bitmap.getPixel(currX, currY)

                    redSum += Color.red(pixel)
                    greenSum += Color.green(pixel)
                    blueSum += Color.blue(pixel)
                    pixelCount++
                }
            }
        }

        val newRed = redSum / pixelCount
        val newGreen = greenSum / pixelCount
        val newBlue = blueSum / pixelCount

        for (xx in x - Math.round(brushSize).toInt() + 1..(x + Math.round(brushSize).toInt() + 1)) {
            for (yy in y - Math.round(brushSize).toInt() + 1..(y + Math.round(brushSize)
                .toInt() + 1)) {

                val currX = xx.coerceAtMost(bitmap.width - 1).coerceAtLeast(0)
                val currY = yy.coerceAtMost(bitmap.height - 1).coerceAtLeast(0)

                val dist = dist(x, y, xx, yy)

                if (dist <= brushSize) {

                    val pixel = bitmap.getPixel(currX, currY)

                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    val redDiff =
                        Math.round((newRed - red) * (brushSize - dist) / brushSize * strength)
                            .toInt()
                    val greenDiff =
                        Math.round((newGreen - green) * (brushSize - dist) / brushSize * strength)
                            .toInt()
                    val blueDiff =
                        Math.round((newBlue - blue) * (brushSize - dist) / brushSize * strength)
                            .toInt()

                    val newPixel = Color.argb(
                        255,
                        red + redDiff,
                        green + greenDiff,
                        blue + blueDiff
                    )

                    mutableBitmap.setPixel(
                        currX,
                        currY,
                        newPixel
                    )
                }
            }
        }

        return mutableBitmap
    }

    private fun dist(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return sqrt(((x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0)))
    }
}