package com.example.verynicephotoeditor.algorithms.task3

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ScalingAlgorithms {
    suspend fun scaleImage(bitmap: Bitmap, scaleFactor: Double): Bitmap = coroutineScope {
        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()
        val newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565)

        val colors = Array(bitmap.width) { IntArray(bitmap.height) }

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                colors[x][y] = bitmap.getPixel(x, y)
            }
        }

        val numCoroutines = 4
        val partHeight = newHeight / numCoroutines

        val originalXValues = DoubleArray(newWidth) { it * bitmap.width.toDouble() / newWidth }
        val originalYValues = DoubleArray(newHeight) { it * bitmap.height.toDouble() / newHeight }

        for (part in 0 until numCoroutines) {
            launch(Dispatchers.Default) {
                val startY = part * partHeight
                val endY = if (part == numCoroutines - 1) newHeight else startY + partHeight

                for (xCoordinate in 0 until newWidth) {
                    val originalX = originalXValues[xCoordinate]
                    for (yCoordinate in startY until endY) {
                        val originalY = originalYValues[yCoordinate]

                        val nearestPixels = findNearestPixels(colors, originalX, originalY)

                        val newPixelValue = interpolate(nearestPixels, originalX % 1, originalY % 1)

                        newBitmap.setPixel(xCoordinate, yCoordinate, newPixelValue)
                    }
                }
            }
        }

        return@coroutineScope newBitmap
    }

    suspend fun scaleImageTrilinear(bitmap: Bitmap, scaleFactor: Double): Bitmap = coroutineScope {

        val blurredBitmap = applySelectiveBlur(bitmap)

        val targetWidth = (blurredBitmap.width * scaleFactor).toInt()
        val targetHeight = (blurredBitmap.height * scaleFactor).toInt()


        val bitmap1 = scaleImage(blurredBitmap, scaleFactor - 0.001)
        val bitmap2 = scaleImage(blurredBitmap, scaleFactor + 0.001)


        val finalBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.RGB_565)


        val colors1 = Array(bitmap1.width) { IntArray(bitmap1.height) }
        val colors2 = Array(bitmap2.width) { IntArray(bitmap2.height) }


        for (x in 0 until bitmap1.width) {
            for (y in 0 until bitmap1.height) {
                colors1[x][y] = bitmap1.getPixel(x, y)
                colors2[x][y] = bitmap2.getPixel(x, y)
            }
        }

        val numCoroutines = 8
        val partHeight = targetHeight / numCoroutines

        val originalXValues =
            DoubleArray(targetWidth) { it * bitmap1.width.toDouble() / targetWidth }
        val originalYValues =
            DoubleArray(targetHeight) { it * bitmap1.height.toDouble() / targetHeight }

        for (part in 0 until numCoroutines) {
            launch(Dispatchers.Default) {
                val startY = part * partHeight
                val endY = if (part == numCoroutines - 1) targetHeight else startY + partHeight

                for (xCoordinate in 0 until targetWidth) {
                    val originalX = originalXValues[xCoordinate]
                    for (yCoordinate in startY until endY) {
                        val originalY = originalYValues[yCoordinate]


                        val finalPixelValue = interpolateTwo(colors1, colors2, originalX, originalY)

                        finalBitmap.setPixel(xCoordinate, yCoordinate, finalPixelValue)
                    }
                }
            }
        }

        return@coroutineScope finalBitmap
    }

    private fun applySelectiveBlur(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blurredBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        val grayscaleBitmap = convertToGrayscale(bitmap)
        val edgeBitmap = detectEdges(grayscaleBitmap)

        val dilatedEdgeBitmap = dilate(edgeBitmap)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = bitmap.getPixel(x, y)
                val edgeColor = dilatedEdgeBitmap.getPixel(x, y)
                if (edgeColor == Color.BLACK) {
                    val blurredColor = applyBlur(bitmap, x, y)
                    blurredBitmap.setPixel(x, y, blurredColor)
                } else {
                    blurredBitmap.setPixel(x, y, color)
                }
            }
        }

        return blurredBitmap
    }

    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val gray = (red + green + blue) / 3
                val newPixel = Color.rgb(gray, gray, gray)
                grayscaleBitmap.setPixel(x, y, newPixel)
            }
        }

        return grayscaleBitmap
    }

    private fun detectEdges(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val edgeBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val gx = (Color.red(bitmap.getPixel(x + 1, y - 1)) + 2 * Color.red(
                    bitmap.getPixel(
                        x + 1,
                        y
                    )
                ) + Color.red(bitmap.getPixel(x + 1, y + 1))) -
                        (Color.red(
                            bitmap.getPixel(
                                x - 1,
                                y - 1
                            )
                        ) + 2 * Color.red(
                            bitmap.getPixel(
                                x - 1,
                                y
                            )
                        ) + Color.red(bitmap.getPixel(x - 1, y + 1)))

                val gy = (Color.red(bitmap.getPixel(x - 1, y + 1)) + 2 * Color.red(
                    bitmap.getPixel(
                        x,
                        y + 1
                    )
                ) + Color.red(bitmap.getPixel(x + 1, y + 1))) -
                        (Color.red(bitmap.getPixel(x - 1, y - 1)) + 2 * Color.red(
                            bitmap.getPixel(
                                x,
                                y - 1
                            )
                        ) + Color.red(bitmap.getPixel(x + 1, y - 1)))

                val magnitude = Math.sqrt((gx * gx + gy * gy).toDouble()).toInt()

                val edgeColor = if (magnitude > 128) Color.BLACK else Color.WHITE
                edgeBitmap.setPixel(x, y, edgeColor)
            }
        }

        return edgeBitmap
    }

    private fun dilate(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val dilatedBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                var isEdge = false

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        val nx = x + dx
                        val ny = y + dy

                        if (nx in 0 until width && ny in 0 until height) {
                            val neighborColor = bitmap.getPixel(nx, ny)
                            if (neighborColor == Color.BLACK) {
                                isEdge = true
                                break
                            }
                        }
                    }

                    if (isEdge) {
                        break
                    }
                }

                dilatedBitmap.setPixel(x, y, if (isEdge) Color.BLACK else Color.WHITE)
            }
        }

        return dilatedBitmap
    }

    private fun applyBlur(bitmap: Bitmap, x: Int, y: Int): Int {
        var red = 0
        var green = 0
        var blue = 0
        var count = 0

        for (dx in -1..1) {
            for (dy in -1..1) {
                val nx = x + dx
                val ny = y + dy

                if (nx in 0 until bitmap.width && ny in 0 until bitmap.height) {
                    val color = bitmap.getPixel(nx, ny)
                    red += Color.red(color)
                    green += Color.green(color)
                    blue += Color.blue(color)
                    count++
                }
            }
        }

        red /= count
        green /= count
        blue /= count

        return Color.rgb(red, green, blue)
    }


    private fun findNearestPixels(colors: Array<IntArray>, x: Double, y: Double): List<Int> {
        val xFloor = x.toInt().coerceIn(0, colors.size - 1)
        val yFloor = y.toInt().coerceIn(0, colors[0].size - 1)
        val xCeil = (xFloor + 1).coerceIn(0, colors.size - 1)
        val yCeil = (yFloor + 1).coerceIn(0, colors[0].size - 1)

        return listOf(
            colors[xFloor][yFloor],
            colors[xCeil][yFloor],
            colors[xFloor][yCeil],
            colors[xCeil][yCeil]
        )
    }

    private fun interpolateTwo(
        colors1: Array<IntArray>,
        colors2: Array<IntArray>,
        x: Double,
        y: Double
    ): Int {
        val nearestPixels1 = findNearestPixels(colors1, x, y)
        val nearestPixels2 = findNearestPixels(colors2, x, y)

        val interpolatedPixels = nearestPixels1.zip(nearestPixels2) { pixel1, pixel2 ->
            interpolatePixels(pixel1, pixel2, 0.5)
        }
        return interpolatePixels(interpolatedPixels[0], interpolatedPixels[1], x % 1)
    }

    private fun interpolatePixels(pixel1: Int, pixel2: Int, weight: Double): Int {
        val a = Color.alpha(pixel1)
        val r = Color.red(pixel1)
        val g = Color.green(pixel1)
        val b = Color.blue(pixel1)

        val a2 = Color.alpha(pixel2)
        val r2 = Color.red(pixel2)
        val g2 = Color.green(pixel2)
        val b2 = Color.blue(pixel2)

        return Color.argb(
            (a + (a2 - a) * weight).toInt(),
            (r + (r2 - r) * weight).toInt(),
            (g + (g2 - g) * weight).toInt(),
            (b + (b2 - b) * weight).toInt()
        )
    }

    private fun interpolate(nearestPixels: List<Int>, x: Double, y: Double): Int {
        val xWeight = x - x.toInt()
        val yWeight = y - y.toInt()

        val red = bilinearInterpolation(
            Color.red(nearestPixels[0]),
            Color.red(nearestPixels[1]),
            Color.red(nearestPixels[2]),
            Color.red(nearestPixels[3]),
            xWeight,
            yWeight
        )
        val green = bilinearInterpolation(
            Color.green(nearestPixels[0]),
            Color.green(nearestPixels[1]),
            Color.green(nearestPixels[2]),
            Color.green(nearestPixels[3]),
            xWeight,
            yWeight
        )
        val blue = bilinearInterpolation(
            Color.blue(nearestPixels[0]),
            Color.blue(nearestPixels[1]),
            Color.blue(nearestPixels[2]),
            Color.blue(nearestPixels[3]),
            xWeight,
            yWeight
        )

        return Color.rgb(red, green, blue)
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