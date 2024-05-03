package com.example.verynicephotoeditor

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.verynicephotoeditor.databinding.ActivityMainBinding
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.truncate
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.applyFilter.setOnClickListener {
            val bitmap = drawableToBitmap(binding.imageView.drawable)
            val grayscaledBitmap = applyGrayscaleFilter(bitmap)

            decodeSteganographyText(applySteganographyText(bitmap, "HELLO this is a text!!!"))
            binding.imageView.setImageBitmap(grayscaledBitmap)
        }

    }

    private fun getGrayscaleValue(red: Int, green: Int, blue: Int): Int {
        return (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
    }

    private fun applyGrayscaleFilter(bitmap: Bitmap): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {

            val pixel = srcPixels[i]

            val gray = getGrayscaleValue(
                Color.red(pixel),
                Color.green(pixel),
                Color.blue(pixel)
            )

            destPixels[i] = Color.argb(Color.alpha(pixel), gray, gray, gray)
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun applyContrastFilter(bitmap: Bitmap, value: Float = 100.0f): Bitmap {

        val factor = (259.0f * (value + 255.0f)) / (255.0f * (259.0f - value))

        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {
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

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun pixelateBitmap(bitmap: Bitmap, pixelSize: Int): Bitmap {
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
                            Math.min(width - 1, Math.max(0, Math.round(xx).toInt())),
                            Math.min(height - 1, Math.max(0, Math.round(yy).toInt())),
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

    private fun applySepia(bitmap: Bitmap, value: Double): Bitmap {

        //50-100 value is best

        val width = bitmap.width
        val height = bitmap.height

        val sepiaValue: Double = value * 255.0 / 100.0
        val sepiaValue_76 = 7.0 * sepiaValue / 6.0
        val sepiaValue_16 = sepiaValue / 6.0
        val sepiaValue_17 = sepiaValue / 7.0

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {

            val pixel = srcPixels[i]

            var tonality = 0.0

            val gray = getGrayscaleValue(
                Color.red(pixel),
                Color.green(pixel),
                Color.blue(pixel)
            )

            if (gray > sepiaValue) {
                tonality = 255.0
            } else {
                tonality = gray + 255.0 - sepiaValue;
            }
            val newRed = tonality

            if (gray > sepiaValue_76) {
                tonality = 255.0
            } else {
                tonality = gray + 255.0 - sepiaValue_76
            }
            var newGreen = tonality

            if (newGreen < sepiaValue_17) {
                newGreen = sepiaValue_17
            }

            if (gray < sepiaValue_16) {
                tonality = 0.0
            } else {
                tonality = gray - sepiaValue_16
            }
            var newBlue = tonality

            if (newBlue < sepiaValue_17) {
                newBlue = sepiaValue_17
            }

            destPixels[i] = Color.argb(
                Color.alpha(pixel),
                newRed.toInt(),
                newGreen.toInt(),
                newBlue.toInt()
            )
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyBasicSolarize(
        bitmap: Bitmap,
        valueRed: Int,
        valueGreen: Int,
        valueBlue: Int
    ): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {

            val pixel = srcPixels[i]

            var newRed = Color.red(pixel);
            var newGreen = Color.green(pixel);
            var newBlue = Color.blue(pixel);

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

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyParabolicSolarize(
        bitmap: Bitmap,
        valueRed: Int,
        valueGreen: Int,
        valueBlue: Int
    ): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {

            val pixel = srcPixels[i]

            val red = Color.red(pixel);
            val green = Color.green(pixel);
            val blue = Color.blue(pixel);

            val newRed = 255 * Math.pow(1 - (red / 128.0), 2.0)
            val newGreen = 255 * Math.pow(1 - (green / 128.0), 2.0)
            val newBlue = 255 * Math.pow(1 - (blue / 128.0), 2.0)

            destPixels[i] = Color.argb(
                Color.alpha(pixel),
                newRed.toInt(),
                newGreen.toInt(),
                newBlue.toInt()
            )
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyPaperize(bitmap: Bitmap): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        var paperBitmap =
            applyGrayscaleFilter(BitmapFactory.decodeResource(resources, R.drawable.paper))
        paperBitmap = Bitmap.createScaledBitmap(paperBitmap, width, height, false)

        val srcPixels = IntArray(width * height)
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val paperPixels = IntArray(width * height)
        paperBitmap.getPixels(paperPixels, 0, width, 0, 0, width, height)

        val destPixels = IntArray(width * height)

        for (i in srcPixels.indices) {

            val pixel = srcPixels[i]

            val red = Color.red(pixel);
            val green = Color.green(pixel);
            val blue = Color.blue(pixel);

            val paperPixel = paperPixels[i]

            val gray = getGrayscaleValue(
                Color.red(paperPixel),
                Color.green(paperPixel),
                Color.blue(paperPixel)
            )

            destPixels[i] = Color.argb(
                Color.alpha(pixel),
                (red * gray) / 256,
                (green * gray) / 256,
                (blue * gray) / 256
            )
        }

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyDither(bitmap: Bitmap): Bitmap {

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

        for (x in 0..<width) {
            for (y in 0..<height) {

                val pixel = grayscaledBitmap.getPixel(x, y)

                val gray = getGrayscaleValue(
                    Color.red(pixel),
                    Color.green(pixel),
                    Color.blue(pixel)
                )

                val matrixValue = ditherMatrix[(y % 4) * 4 + (x % 4)]

                var newVal = 0;

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

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyEdgerator(bitmap: Bitmap, minMagnitude: Int): Bitmap {

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

        for (x in 0..<width) {
            for (y in 0..<height) {

                val pixel = grayscaledBitmap.getPixel(x, y)

                var horizontalSum: Int = 0
                var verticalSum: Int = 0

                for (xx in x - 1..x + 1) {
                    for (yy in y - 1..y + 1) {

                        val currY = Math.max(Math.min(yy, height - 1), 0)
                        val currX = Math.max(Math.min(xx, width - 1), 0)

                        val currPixel = grayscaledBitmap.getPixel(currX, currY)

                        horizontalSum += horizontalMatrix[yy - y + 1][xx - x + 1] * Color.red(
                            currPixel
                        )
                        verticalSum += verticalMatrix[yy - y + 1][xx - x + 1] * Color.red(currPixel)

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

        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun gaussianFunction(x: Double, y: Double, sigma: Double = 1.0): Double {
        return exp(-((x * x + y * y) / (2 * sigma * sigma))) / (2 * Math.PI * Math.PI * sigma * sigma);
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

        return matrix;
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

    private fun applyGaussBlur(bitmap: Bitmap, kernelSize: Int = 3): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val destPixels = IntArray(width * height)

        val kernelMatrix = getKernelMatrix(kernelSize)
        val sum = getMatrixSum(kernelMatrix)

        for (x in 0..<width) {
            for (y in 0..<height) {

                val pixel = bitmap.getPixel(x, y)

                val bordDistY: Int = (kernelSize - 1) / 2
                val bordDistX: Int = (kernelSize - 1) / 2

                val sums = mutableListOf(0.0, 0.0, 0.0)

                for (j in y - bordDistY..y + bordDistY) {
                    for (i in x - bordDistX..x + bordDistX) {

                        val currX = Math.max(Math.min(bitmap.width - 1, i), 0)
                        val currY = Math.max(Math.min(bitmap.height - 1, j), 0)

                        sums[0] += kernelMatrix[j - (y - bordDistY)][i - (x - bordDistX)] / sum * Color.red(
                            pixel
                        )
                        sums[1] += kernelMatrix[j - (y - bordDistY)][i - (x - bordDistX)] / sum * Color.red(
                            pixel
                        )
                        sums[2] += kernelMatrix[j - (y - bordDistY)][i - (x - bordDistX)] / sum * Color.red(
                            pixel
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

        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyGlass(bitmap: Bitmap, diffusion: Double): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0..<width) {
            for (y in 0..<height) {

                val currX = Math.max(
                    Math.min(
                        Math.round(x + (Random.nextDouble() - 0.5) * diffusion),
                        (width - 1).toLong()
                    ), 0
                )
                val currY = Math.max(
                    Math.min(
                        Math.round(y + (Random.nextDouble() - 0.5) * diffusion),
                        (height - 1).toLong()
                    ), 0
                )

                destBitmap.setPixel(
                    x,
                    y,
                    bitmap.getPixel(currX.toInt(), currY.toInt())
                )
            }
        }

        return destBitmap
    }

    fun <T> mode(list: List<T>): T? {
        val frequencyMap = mutableMapOf<T, Int>()

        for (element in list) {
            val frequency = frequencyMap.getOrDefault(element, 0) + 1
            frequencyMap[element] = frequency
        }

        var mode: T? = null
        var maxFrequency = 0

        for ((element, frequency) in frequencyMap) {
            if (frequency > maxFrequency) {
                mode = element
                maxFrequency = frequency
            }
        }

        return mode
    }

    private fun applyOilPaint(bitmap: Bitmap, neighbourhood: Int): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val destPixels = IntArray(width * height)

        for (x in 0..<width) {
            for (y in 0..<height) {

                val pixel = bitmap.getPixel(x, y)

                val redList = MutableList((neighbourhood * 2 + 1) * (neighbourhood * 2 + 1)) { 0 }
                val greenList = MutableList((neighbourhood * 2 + 1) * (neighbourhood * 2 + 1)) { 0 }
                val blueList = MutableList((neighbourhood * 2 + 1) * (neighbourhood * 2 + 1)) { 0 }

                for (xx in x - neighbourhood..x + neighbourhood) {
                    for (yy in y - neighbourhood..y + neighbourhood) {

                        val currX = Math.max(Math.min(bitmap.width - 1, xx), 0)
                        val currY = Math.max(Math.min(bitmap.height - 1, yy), 0)

                        val currPixel = bitmap.getPixel(currX, currY)

                        redList[(yy - y + neighbourhood) * (neighbourhood * 2 + 1) + xx - x + neighbourhood] =
                            Color.red(currPixel)
                        greenList[(yy - y + neighbourhood) * (neighbourhood * 2 + 1) + xx - x + neighbourhood] =
                            Color.green(currPixel)
                        blueList[(yy - y + neighbourhood) * (neighbourhood * 2 + 1) + xx - x + neighbourhood] =
                            Color.blue(currPixel)
                    }
                }

                val (modeRed, _) = redList.groupingBy { it }.eachCount().maxByOrNull { it.value }!!
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

        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyEmbossFilter(bitmap: Bitmap): Bitmap {

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

        for (x in 0..<width) {
            for (y in 0..<height) {

                val pixel = grayscaledBitmap.getPixel(x, y)

                var sum = 0;

                for (xx in x - 1..x + 1) {
                    for (yy in y - 1..y + 1) {

                        val currX = Math.max(Math.min(bitmap.width - 1, xx), 0)
                        val currY = Math.max(Math.min(bitmap.height - 1, yy), 0)

                        val currPixel = grayscaledBitmap.getPixel(currX, currY)

                        sum += embossMatrix[yy - y + 1][xx - x + 1] * Color.red(currPixel)
                    }
                }

                sum = Math.max(Math.min(255, sum), 0)

                destPixels[y * width + x] = Color.argb(
                    Color.alpha(pixel),
                    sum,
                    sum,
                    sum
                )
            }
        }

        destBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)

        return destBitmap
    }

    private fun applyWave(bitmap: Bitmap): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        val destBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0..<width) {
            for (y in 0..<height) {

                val currX = Math.max(
                    Math.min(
                        Math.round(x + 20 * sin(2 * PI * y / 128)),
                        (width - 1).toLong()
                    ), 0
                )
                val currY = y

                destBitmap.setPixel(
                    x,
                    y,
                    bitmap.getPixel(currX.toInt(), currY)
                )
            }
        }

        return destBitmap
    }

    private fun createSteganography50x50(bitmap: Bitmap) : Bitmap {

        val secretImage = BitmapFactory.decodeResource(resources, R.drawable.snek)

        val secretImageResized = Bitmap.createScaledBitmap(secretImage, bitmap.width, bitmap.height, false)

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val coverPixel = bitmap.getPixel(x, y)
                val secretPixel = secretImageResized.getPixel(x, y)

                val rgbValues = mutableListOf(
                    addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel)), 8),
                    addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel)), 8),
                    addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)), 8)
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
                }

                else {
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

    private fun decodeSteganography50x50(bitmap: Bitmap) : Bitmap {

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val steganographyPixel = bitmap.getPixel(x, y)

                val encodedInfo = addLeadingZeros(Integer.toBinaryString(Color.blue(steganographyPixel)), 8)[7]

                val resultPixel = if (encodedInfo == '1') {
                    Color.WHITE
                }

                else {
                    Color.BLACK
                }

                resultImage.setPixel(x, y, resultPixel)
            }
        }

        return resultImage
    }

    private fun createSteganography(bitmap: Bitmap) : Bitmap{

        val secretImage = BitmapFactory.decodeResource(resources, R.drawable.snek)

        val secretImageResized = Bitmap.createScaledBitmap(secretImage, bitmap.width, bitmap.height, false)

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val coverPixel = bitmap.getPixel(x, y)
                val secretPixel = secretImageResized.getPixel(x, y)

                val rgbValues = mutableListOf(
                    addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel)), 8),
                    addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel)), 8),
                    addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)), 8)
                )

                val grayscale = getGrayscaleValue(
                    Color.red(secretPixel),
                    Color.green(secretPixel),
                    Color.blue(secretPixel)
                )

                val grayscaleBin = addLeadingZeros(Integer.toBinaryString(grayscale), 8)

                // rrrggbbb

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

    private fun decodeSteganography(bitmap: Bitmap) : Bitmap {

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val steganographyPixel = bitmap.getPixel(x, y)

                val red = addLeadingZeros(Integer.toBinaryString(Color.red(steganographyPixel)), 8)
                val green = addLeadingZeros(Integer.toBinaryString(Color.green(steganographyPixel)), 8)
                val blue = addLeadingZeros(Integer.toBinaryString(Color.blue(steganographyPixel)), 8)

                var grayscale = "00000000"

                grayscale = replaceChar(grayscale, red[5], 0)
                grayscale = replaceChar(grayscale, red[6], 1)
                grayscale = replaceChar(grayscale, red[7], 2)
                grayscale = replaceChar(grayscale, green[6], 3)
                grayscale = replaceChar(grayscale, green[7], 4)
                grayscale = replaceChar(grayscale, blue[5], 5)
                grayscale = replaceChar(grayscale, blue[6], 6)
                grayscale = replaceChar(grayscale, blue[7], 7)

                resultImage.setPixel(x, y, Color.argb(
                    Color.alpha(steganographyPixel),
                    grayscale.toInt(2),
                    grayscale.toInt(2),
                    grayscale.toInt(2)
                ))
            }
        }

        return resultImage
    }

    private fun applySteganographyText(bitmap: Bitmap, text: String) : Bitmap {

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        var currTextIndex = -1

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                currTextIndex++

                val currChar: String = if (currTextIndex >= text.length) {
                    "00000000"
                }

                else {
                    addLeadingZeros(Integer.toBinaryString(text[currTextIndex].toInt()), 8)
                }

                val coverPixel = bitmap.getPixel(x, y)

                val red = addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel)), 8)
                val green = addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel)), 8)
                val blue = addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)), 8)

                val resultPixel = Color.argb(
                    Color.alpha(coverPixel),

                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                replaceChar(
                                    red,
                                    currChar[0],
                                    5
                                ),
                                currChar[1],
                                6
                            ),
                            currChar[2],
                            7
                        )
                    ).toInt(2),

                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                green,
                                currChar[3],
                                6
                            ),
                            currChar[4],
                            7
                        )
                    ).toInt(2),

                    removeLeadingZeros(
                        replaceChar(
                            replaceChar(
                                replaceChar(
                                    blue,
                                    currChar[5],
                                    5
                                ),
                                currChar[6],
                                6
                            ),
                            currChar[7],
                            7
                        )
                    ).toInt(2)
                )

                resultImage.setPixel(x, y, resultPixel)

                if (currTextIndex >= text.length) {
                    return resultImage
                }
            }
        }

        return resultImage
    }

    private fun decodeSteganographyText(bitmap: Bitmap) : String {

        val resultImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        var decodedText = ""

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {

                val coverPixel = bitmap.getPixel(x, y)

                val red = addLeadingZeros(Integer.toBinaryString(Color.red(coverPixel)), 8)
                val green = addLeadingZeros(Integer.toBinaryString(Color.green(coverPixel)), 8)
                val blue = addLeadingZeros(Integer.toBinaryString(Color.blue(coverPixel)), 8)

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
                    Log.d("Decoded text", decodedText)
                    return decodedText
                }

                decodedText += Integer.parseInt(removeLeadingZeros(encodedChar), 2).toChar()
            }
        }

        Log.d("Decoded text", decodedText)

        return decodedText
    }
    private fun replaceChar(str: String, ch: Char, index: Int) : String {
        val sb = StringBuilder(str)
        sb.setCharAt(index, ch)
        return sb.toString()
    }
    private fun addLeadingZeros(str : String, len: Int) : String {

        var value = str

        for (i in 0..<len) {
            if (value.length < len) {
                value = "0$value"
            }
        }

        return value
    }

    private fun removeLeadingZeros(str : String) : String {

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
}
