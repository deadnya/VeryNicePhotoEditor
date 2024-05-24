package com.example.verynicephotoeditor.algorithms.task2

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.truncate
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class Filters {

    private fun getGrayscaleValue(red: Int, green: Int, blue: Int): Int {
        return (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
    }

    suspend fun applyGrayscaleFilter(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        coroutineScope {
            for (i in srcPixels.indices) {
                launch {
                    val pixel = srcPixels[i]

                    val gray = getGrayscaleValue(
                        Color.red(pixel),
                        Color.green(pixel),
                        Color.blue(pixel)
                    )

                    destPixels[i] = Color.argb(Color.alpha(pixel), gray, gray, gray)
                }
            }
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return@withContext destBitmap
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1000
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 667

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    suspend fun applyContrastFilter(bitmap: Bitmap, value: Float = 100.0f): Bitmap =
        withContext(Dispatchers.Default) {
            val factor = (259.0f * (value + 255.0f)) / (255.0f * (259.0f - value))

            val width = bitmap.width
            val height = bitmap.height

            val srcPixels = IntArray(width * height)
            bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

            val destPixels = IntArray(width * height)

            val jobs = List(srcPixels.size) { i ->
                async {
                    val pixel = srcPixels[i]
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)

                    destPixels[i] = Color.argb(
                        Color.alpha(pixel),
                        0.coerceAtLeast(255.coerceAtMost(truncate(factor * (r - 128) + 128).toInt())),
                        0.coerceAtLeast(255.coerceAtMost(truncate(factor * (g - 128) + 128).toInt())),
                        0.coerceAtLeast(255.coerceAtMost(truncate(factor * (b - 128) + 128).toInt()))
                    )
                }
            }

            jobs.awaitAll()

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
            destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

            return@withContext destBitmap
        }

    fun pixelateBitmap(bitmap: Bitmap, pixelSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width step pixelSize) {
            for (y in 0 until height step pixelSize) {

                val pixel = bitmap.getPixel(x, y)

                var xx = 0.0.coerceAtLeast(x - pixelSize / 2.0)

                while (xx <= x + pixelSize / 2.0) {

                    var yy = 0.0.coerceAtLeast(y - pixelSize / 2.0)

                    while (yy <= y + pixelSize / 2.0) {
                        newBitmap.setPixel(
                            (width - 1).coerceAtMost(0.coerceAtLeast(Math.round(xx).toInt())),
                            (height - 1).coerceAtMost(0.coerceAtLeast(Math.round(yy).toInt())),
                            pixel
                        )

                        yy++
                    }

                    xx++
                }
            }
        }

        return newBitmap
    }

    suspend fun applySepia(bitmap: Bitmap, value: Double): Bitmap =
        withContext(Dispatchers.Default) {

            val width = bitmap.width
            val height = bitmap.height

            val sepiaValue: Double = value * 255.0 / 100.0
            val sepiaValue76 = 7.0 * sepiaValue / 6.0
            val sepiaValue16 = sepiaValue / 6.0
            val sepiaValue17 = sepiaValue / 7.0

            val srcPixels = IntArray(width * height)
            bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

            val destPixels = IntArray(width * height)

            val jobs = List(srcPixels.size) { i ->
                async {
                    val pixel = srcPixels[i]

                    var tonality: Double

                    val gray = getGrayscaleValue(
                        Color.red(pixel),
                        Color.green(pixel),
                        Color.blue(pixel)
                    )

                    tonality = if (gray > sepiaValue) {
                        255.0
                    } else {
                        gray + 255.0 - sepiaValue
                    }
                    val newRed = tonality

                    tonality = if (gray > sepiaValue76) {
                        255.0
                    } else {
                        gray + 255.0 - sepiaValue76
                    }
                    var newGreen = tonality

                    if (newGreen < sepiaValue17) {
                        newGreen = sepiaValue17
                    }

                    tonality = if (gray < sepiaValue16) {
                        0.0
                    } else {
                        gray - sepiaValue16
                    }
                    var newBlue = tonality

                    if (newBlue < sepiaValue17) {
                        newBlue = sepiaValue17
                    }

                    destPixels[i] = Color.argb(
                        Color.alpha(pixel),
                        newRed.toInt(),
                        newGreen.toInt(),
                        newBlue.toInt()
                    )
                }
            }

            jobs.forEach { it.await() }

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
            destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

            return@withContext destBitmap
        }

    suspend fun applySolarize(
        bitmap: Bitmap,
        valueRed: Int,
        valueGreen: Int,
        valueBlue: Int
    ): Bitmap = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        val jobs = List(srcPixels.size) { i ->
            async {
                val pixel = srcPixels[i]

                var newRed = Color.red(pixel)
                var newGreen = Color.green(pixel)
                var newBlue = Color.blue(pixel)

                if (newRed > valueRed) {
                    newRed = 255 - newRed
                }

                if (newGreen > valueGreen) {
                    newGreen = 255 - newGreen
                }

                if (newBlue > valueBlue) {
                    newBlue = 255 - newBlue
                }

                destPixels[i] = Color.argb(
                    Color.alpha(pixel),
                    newRed,
                    newGreen,
                    newBlue
                )
            }
        }

        jobs.forEach { it.await() }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return@withContext destBitmap
    }

    suspend fun applyDither(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val ditherMatrix = arrayOf(
            96, 128, 160, 16,
            32, 144, 224, 80,
            64, 208, 128, 48,
            0, 176, 240, 112
        )

        val grayscaledBitmap = applyGrayscaleFilter(bitmap)

        val width = grayscaledBitmap.width
        val height = grayscaledBitmap.height

        val destPixels = IntArray(width * height)

        val jobs = List(width * height) { index ->
            async {
                val x = index % width
                val y = index / width

                val pixel = grayscaledBitmap.getPixel(x, y)

                val gray = getGrayscaleValue(
                    Color.red(pixel),
                    Color.green(pixel),
                    Color.blue(pixel)
                )

                val matrixValue = ditherMatrix[(y % 4) * 4 + (x % 4)]

                var newVal = 0

                if (gray > matrixValue) {
                    newVal = gray
                }

                destPixels[y * width + x] = Color.argb(
                    Color.alpha(pixel),
                    newVal,
                    newVal,
                    newVal
                )
            }
        }

        jobs.forEach { it.await() }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return@withContext destBitmap
    }

    suspend fun applyEdgerator(bitmap: Bitmap, minMagnitude: Int): Bitmap =
        withContext(Dispatchers.Default) {
            val grayscaledBitmap = applyGaussBlur(applyGrayscaleFilter(bitmap), 3)

            val width = grayscaledBitmap.width
            val height = grayscaledBitmap.height

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
            val destPixels = IntArray(width * height)

            val horizontalMatrix = arrayOf(
                arrayOf(-1, 0, 1),
                arrayOf(-2, 0, 2),
                arrayOf(-1, 0, 1)
            )

            val verticalMatrix = arrayOf(
                arrayOf(1, 2, 1),
                arrayOf(0, 0, 0),
                arrayOf(-1, -2, -1)
            )

            val jobs = List(width * height) { index ->
                async {
                    val x = index % width
                    val y = index / width

                    val pixel = grayscaledBitmap.getPixel(x, y)

                    var horizontalSum = 0
                    var verticalSum = 0

                    for (xx in x - 1..x + 1) {
                        for (yy in y - 1..y + 1) {

                            val currY = yy.coerceAtMost(height - 1).coerceAtLeast(0)
                            val currX = xx.coerceAtMost(width - 1).coerceAtLeast(0)

                            val currPixel = grayscaledBitmap.getPixel(currX, currY)

                            horizontalSum += horizontalMatrix[yy - y + 1][xx - x + 1] * Color.red(
                                currPixel
                            )
                            verticalSum += verticalMatrix[yy - y + 1][xx - x + 1] * Color.red(
                                currPixel
                            )

                        }
                    }

                    val magnitude = sqrt(
                        horizontalSum.toDouble().pow(2.0)
                                + verticalSum.toDouble().pow(2.0)
                    )

                    if (magnitude > minMagnitude) {

                        destPixels[y * height + x] = Color.argb(
                            Color.alpha(pixel),
                            Color.red(pixel),
                            Color.green(pixel),
                            Color.blue(pixel)
                        )
                    } else {

                        destPixels[y * width + x] = Color.argb(
                            Color.alpha(pixel),
                            (Color.red(pixel) * (magnitude / minMagnitude / 5)).toInt(),
                            (Color.red(pixel) * (magnitude / minMagnitude / 5)).toInt(),
                            (Color.red(pixel) * (magnitude / minMagnitude / 5)).toInt()
                        )
                    }
                }
            }

            jobs.forEach { it.await() }

            destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

            return@withContext destBitmap
        }

    private fun gaussianFunction(x: Double, y: Double, sigma: Double = 1.0): Double {
        return exp(-((x * x + y * y) / (2 * sigma * sigma))) / (2 * Math.PI * Math.PI * sigma * sigma)
    }

    private fun getKernelMatrix(kernelSize: Int): MutableList<MutableList<Double>> {

        val matrix = MutableList(kernelSize) { MutableList(kernelSize) { 0.0 } }

        for (i in 0..<kernelSize) {

            for (j in 0..<kernelSize) {

                matrix[i][j] = gaussianFunction(
                    ((2 * j) / (kernelSize - 1) - 1).toDouble(),
                    ((2 * i) / (kernelSize - 1) - 1).toDouble()
                )
            }
        }

        return matrix
    }

    private fun getMatrixSum(matrix: MutableList<MutableList<Double>>): Double {

        var sum = 0.0

        for (i in matrix) {
            for (j in i) {
                sum += j
            }
        }

        return sum
    }

    suspend fun applyGaussBlur(bitmap: Bitmap, kernelSize: Int = 3): Bitmap =
        withContext(Dispatchers.Default) {
            val width = bitmap.width
            val height = bitmap.height

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
            val destPixels = IntArray(width * height)

            val kernelMatrix = getKernelMatrix(kernelSize)
            val sum = getMatrixSum(kernelMatrix)

            val jobs = List(width * height) { index ->
                async {
                    val x = index % width
                    val y = index / width

                    val pixel = bitmap.getPixel(x, y)

                    val bordDistY: Int = (kernelSize - 1) / 2
                    val bordDistX: Int = (kernelSize - 1) / 2

                    val sums = mutableListOf(0.0, 0.0, 0.0)

                    for (j in y - bordDistY..y + bordDistY) {
                        for (i in x - bordDistX..x + bordDistX) {

                            val currX = (bitmap.width - 1).coerceAtMost(i).coerceAtLeast(0)
                            val currY = (bitmap.height - 1).coerceAtMost(j).coerceAtLeast(0)

                            sums[0] += kernelMatrix[j - (y - bordDistY)][i - (x - bordDistX)] / sum * Color.red(
                                bitmap.getPixel(currX, currY)
                            )
                            sums[1] += kernelMatrix[j - (y - bordDistY)][i - (x - bordDistX)] / sum * Color.green(
                                bitmap.getPixel(currX, currY)
                            )
                            sums[2] += kernelMatrix[j - (y - bordDistY)][i - (x - bordDistX)] / sum * Color.blue(
                                bitmap.getPixel(currX, currY)
                            )
                        }
                    }

                    destPixels[y * width + x] = Color.argb(
                        Color.alpha(pixel),
                        sums[0].toInt(),
                        sums[1].toInt(),
                        sums[2].toInt()
                    )
                }
            }

            jobs.awaitAll()

            destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

            return@withContext destBitmap
        }

    suspend fun applyGlass(bitmap: Bitmap, diffusion: Double): Bitmap =
        withContext(Dispatchers.Default) {
            val width = bitmap.width
            val height = bitmap.height

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)

            val jobs = List(width * height) { index ->
                async {
                    val x = index % width
                    val y = index / width

                    val currX = Math.round(x + (Random.nextDouble() - 0.5) * diffusion)
                        .coerceAtMost((width - 1).toLong()).coerceAtLeast(0)
                    val currY = Math.round(y + (Random.nextDouble() - 0.5) * diffusion)
                        .coerceAtMost((height - 1).toLong()).coerceAtLeast(0)

                    destBitmap.setPixel(
                        x,
                        y,
                        bitmap.getPixel(currX.toInt(), currY.toInt())
                    )
                }
            }

            jobs.awaitAll()

            return@withContext destBitmap
        }

    suspend fun applyOilPaint(bitmap: Bitmap, neighbourhood: Int): Bitmap =
        withContext(Dispatchers.Default) {
            val width = bitmap.width
            val height = bitmap.height

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
            val destPixels = IntArray(width * height)

            coroutineScope {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        launch {
                            val pixel = bitmap.getPixel(x, y)

                            val redList =
                                MutableList((neighbourhood * 2 + 1) * (neighbourhood * 2 + 1)) { 0 }
                            val greenList =
                                MutableList((neighbourhood * 2 + 1) * (neighbourhood * 2 + 1)) { 0 }
                            val blueList =
                                MutableList((neighbourhood * 2 + 1) * (neighbourhood * 2 + 1)) { 0 }

                            for (xx in x - neighbourhood..x + neighbourhood) {
                                for (yy in y - neighbourhood..y + neighbourhood) {

                                    val currX = (bitmap.width - 1).coerceAtMost(xx).coerceAtLeast(0)
                                    val currY =
                                        (bitmap.height - 1).coerceAtMost(yy).coerceAtLeast(0)

                                    val currPixel = bitmap.getPixel(currX, currY)

                                    redList[(yy - y + neighbourhood) * (neighbourhood * 2 + 1) + xx - x + neighbourhood] =
                                        Color.red(currPixel)
                                    greenList[(yy - y + neighbourhood) * (neighbourhood * 2 + 1) + xx - x + neighbourhood] =
                                        Color.green(currPixel)
                                    blueList[(yy - y + neighbourhood) * (neighbourhood * 2 + 1) + xx - x + neighbourhood] =
                                        Color.blue(currPixel)
                                }
                            }

                            val (modeRed, _) = redList.groupingBy { it }.eachCount()
                                .maxByOrNull { it.value }!!
                            val (modeGreen, _) = greenList.groupingBy { it }.eachCount()
                                .maxByOrNull { it.value }!!
                            val (modeBlue, _) = blueList.groupingBy { it }.eachCount()
                                .maxByOrNull { it.value }!!

                            destPixels[y * width + x] = Color.argb(
                                Color.alpha(pixel),
                                modeRed,
                                modeGreen,
                                modeBlue
                            )
                        }
                    }
                }
            }

            destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

            return@withContext destBitmap
        }

    suspend fun applyEmbossFilter(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height

        val grayscaledBitmap = applyGrayscaleFilter(bitmap)

        val embossMatrix = arrayOf(
            arrayOf(1, 0, -1),
            arrayOf(2, 0, -2),
            arrayOf(1, 0, -1)
        )

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val destPixels = IntArray(width * height)

        coroutineScope {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    launch {
                        val pixel = grayscaledBitmap.getPixel(x, y)

                        var sum = 0

                        for (xx in x - 1..x + 1) {
                            for (yy in y - 1..y + 1) {

                                val currX = (bitmap.width - 1).coerceAtMost(xx).coerceAtLeast(0)
                                val currY = (bitmap.height - 1).coerceAtMost(yy).coerceAtLeast(0)

                                val currPixel = grayscaledBitmap.getPixel(currX, currY)

                                sum += embossMatrix[yy - y + 1][xx - x + 1] * Color.red(currPixel)
                            }
                        }

                        sum = 255.coerceAtMost(sum).coerceAtLeast(0)

                        destPixels[y * width + x] = Color.argb(
                            Color.alpha(pixel),
                            sum,
                            sum,
                            sum
                        )
                    }
                }
            }
        }

        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return@withContext destBitmap
    }

    suspend fun applyWave(bitmap: Bitmap, a: Int, b: Int): Bitmap =
        withContext(Dispatchers.Default) {
            val width = bitmap.width
            val height = bitmap.height

            val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)

            coroutineScope {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        launch {
                            val currX = Math.round(x + a * sin(2 * PI * y / b))
                                .coerceAtMost((width - 1).toLong()).coerceAtLeast(0)

                            destBitmap.setPixel(
                                x,
                                y,
                                bitmap.getPixel(currX.toInt(), y)
                            )
                        }
                    }
                }
            }

            return@withContext destBitmap
        }

    fun createSteganography50x50(bitmap: Bitmap, bitmapToEncrypt: Bitmap): Bitmap {

        val secretImageResized =
            Bitmap.createScaledBitmap(bitmapToEncrypt, bitmap.width, bitmap.height, false)

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)


        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val coverPixel = bitmap.getPixel(x, y)
                val secretPixel = secretImageResized.getPixel(x, y)

                val rgbValues = mutableListOf(
                    addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel))),
                    addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel))),
                    addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)))
                )

                val grayscale = getGrayscaleValue(
                    Color.red(secretPixel),
                    Color.green(secretPixel),
                    Color.blue(secretPixel)
                )

                val isCloserToWhite = grayscale > 128

                val resultPixel = if (isCloserToWhite) {
                    Color.argb(
                        Color.alpha(coverPixel),
                        removeLeadingZeros(rgbValues[0]).toInt(2),
                        removeLeadingZeros(rgbValues[1]).toInt(2),
                        removeLeadingZeros(replaceChar(rgbValues[2], '1', 7)).toInt(2)
                    )
                } else {
                    Color.argb(
                        Color.alpha(coverPixel),
                        removeLeadingZeros(rgbValues[0]).toInt(2),
                        removeLeadingZeros(rgbValues[1]).toInt(2),
                        removeLeadingZeros(replaceChar(rgbValues[2], '0', 7)).toInt(2)
                    )
                }

                resultImage.setPixel(x, y, resultPixel)
            }
        }

        return resultImage
    }

    fun decodeSteganography50x50(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val resultImage = Bitmap.createBitmap(width, height, bitmap.config)


        for (x in 0 until width) {
            for (y in 0 until height) {

                val steganographyPixel = bitmap.getPixel(x, y)

                val encodedInfo =
                    addLeadingZeros(
                        Integer.toBinaryString(Color.blue(steganographyPixel))
                    )[7]

                val resultPixel = if (encodedInfo == '1') {
                    Color.WHITE
                } else {
                    Color.BLACK
                }

                resultImage.setPixel(x, y, resultPixel)
            }

        }

        return resultImage
    }

    fun createSteganography(bitmap: Bitmap, bitmapToEncrypt: Bitmap): Bitmap {

        val secretImageResized =
            Bitmap.createScaledBitmap(bitmapToEncrypt, bitmap.width, bitmap.height, false)

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)


        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val coverPixel = bitmap.getPixel(x, y)
                val secretPixel = secretImageResized.getPixel(x, y)

                val rgbValues = mutableListOf(
                    addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel))),
                    addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel))),
                    addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)))
                )

                val grayscale = getGrayscaleValue(
                    Color.red(secretPixel),
                    Color.green(secretPixel),
                    Color.blue(secretPixel)
                )

                val grayscaleBin = addLeadingZeros(Integer.toBinaryString(grayscale))

                val resultPixel = Color.argb(
                    Color.alpha(coverPixel),

                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                replaceChar(
                                    rgbValues[0],
                                    grayscaleBin[0],
                                    5
                                ),
                                grayscaleBin[1],
                                6
                            ),
                            grayscaleBin[2],
                            7
                        )
                    ).toInt(2),

                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                rgbValues[1],
                                grayscaleBin[3],
                                6
                            ),
                            grayscaleBin[4],
                            7
                        )
                    ).toInt(2),

                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                replaceChar(
                                    rgbValues[2],
                                    grayscaleBin[5],
                                    5
                                ),
                                grayscaleBin[6],
                                6
                            ),
                            grayscaleBin[7],
                            7
                        )
                    ).toInt(2)
                )

                resultImage.setPixel(x, y, resultPixel)
            }
        }

        return resultImage
    }

    fun decodeSteganography(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultImage = Bitmap.createBitmap(width, height, bitmap.config)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val steganographyPixel = bitmap.getPixel(x, y)

                val red = addLeadingZeros(
                    Integer.toBinaryString(Color.red(steganographyPixel))
                )
                val green = addLeadingZeros(
                    Integer.toBinaryString(Color.green(steganographyPixel))
                )
                val blue = addLeadingZeros(
                    Integer.toBinaryString(Color.blue(steganographyPixel))
                )

                var grayscale = "00000000"

                grayscale = replaceChar(grayscale, red[5], 0)
                grayscale = replaceChar(grayscale, red[6], 1)
                grayscale = replaceChar(grayscale, red[7], 2)
                grayscale = replaceChar(grayscale, green[6], 3)
                grayscale = replaceChar(grayscale, green[7], 4)
                grayscale = replaceChar(grayscale, blue[5], 5)
                grayscale = replaceChar(grayscale, blue[6], 6)
                grayscale = replaceChar(grayscale, blue[7], 7)

                resultImage.setPixel(
                    x, y, Color.argb(
                        Color.alpha(steganographyPixel),
                        grayscale.toInt(2),
                        grayscale.toInt(2),
                        grayscale.toInt(2)
                    )
                )
            }
        }

        return resultImage
    }

    fun applySteganographyText(bitmap: Bitmap, text: String): Bitmap {
        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        var currTextIndex = -1
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                currTextIndex++
                if (currTextIndex > text.length) {
                    resultImage.setPixel(x, y, bitmap.getPixel(x, y))
                    continue
                }
                val currChar: String = if (currTextIndex == text.length) {
                    "00000000"
                } else {
                    addLeadingZeros(Integer.toBinaryString(text[currTextIndex].code))
                }
                val coverPixel = bitmap.getPixel(x, y)
                val red = addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel)))
                val green = addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel)))
                val blue = addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)))
                val resultPixel = Color.argb(
                    Color.alpha(coverPixel),
                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                replaceChar(
                                    red,
                                    currChar[0], 5
                                ), currChar[1],
                                6
                            ),
                            currChar[2], 7
                        )
                    ).toInt(2),
                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                green, currChar[3],
                                6
                            ),
                            currChar[4], 7
                        )
                    ).toInt(2),
                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                replaceChar(
                                    blue,
                                    currChar[5], 5
                                ), currChar[6],
                                6
                            ),
                            currChar[7], 7
                        )
                    ).toInt(2)
                )
                resultImage.setPixel(x, y, resultPixel)
            }
        }
        return resultImage
    }

    fun decodeSteganographyText(bitmap: Bitmap): String {
        Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        var decodedText = ""


        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val coverPixel = bitmap.getPixel(x, y)

                val red = addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel)))
                val green =
                    addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel)))
                val blue =
                    addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)))

                var encodedChar = "00000000"

                encodedChar = replaceChar(encodedChar, red[5], 0)
                encodedChar = replaceChar(encodedChar, red[6], 1)
                encodedChar = replaceChar(encodedChar, red[7], 2)
                encodedChar = replaceChar(encodedChar, green[6], 3)
                encodedChar = replaceChar(encodedChar, green[7], 4)
                encodedChar = replaceChar(encodedChar, blue[5], 5)
                encodedChar = replaceChar(encodedChar, blue[6], 6)
                encodedChar = replaceChar(encodedChar, blue[7], 7)

                if (encodedChar == "00000000") {
                    return decodedText
                }

                decodedText += Integer.parseInt(removeLeadingZeros(encodedChar), 2).toChar()
            }
        }

        return decodedText
    }

    private fun replaceChar(str: String, ch: Char, index: Int): String {
        val sb = StringBuilder(str)
        sb.setCharAt(index, ch)
        return sb.toString()
    }

    private fun addLeadingZeros(str: String): String {

        var value = str

        for (i in 0..<8) {
            if (value.length < 8) {
                value = "0$value"
            }
        }

        return value
    }

    private fun removeLeadingZeros(str: String): String {

        var value = str

        for (i in str.indices) {

            if (value[0] == '1') {
                return value
            }

            val stringBuilder = StringBuilder(value)
            value = stringBuilder.deleteAt(0).toString()
        }

        return "0"
    }

    suspend fun applyUnsharpMask(bitmap: Bitmap, strength: Double, radius: Int): Bitmap =
        withContext(Dispatchers.Default) {
            val blurredBitmap = applyGaussBlur(bitmap, radius)
            val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

            coroutineScope {
                for (x in 0 until bitmap.width) {
                    for (y in 0 until bitmap.height) {
                        launch {
                            val pixel = bitmap.getPixel(x, y)
                            val red = Color.red(pixel)
                            val green = Color.green(pixel)
                            val blue = Color.blue(pixel)

                            val blurredPixel = blurredBitmap.getPixel(x, y)
                            val blurredRed = Color.red(blurredPixel)
                            val blurredGreen = Color.green(blurredPixel)
                            val blurredBlue = Color.blue(blurredPixel)

                            val newPixel = Color.argb(
                                255,
                                (red + (red - blurredRed) * strength).toInt().coerceAtMost(255)
                                    .coerceAtLeast(0),
                                (green + (green - blurredGreen) * strength).toInt()
                                    .coerceAtMost(255)
                                    .coerceAtLeast(0),
                                (blue + (blue - blurredBlue) * strength).toInt().coerceAtMost(255)
                                    .coerceAtLeast(0)
                            )

                            resultImage.setPixel(x, y, newPixel)
                        }
                    }
                }
            }

            return@withContext resultImage
        }
}