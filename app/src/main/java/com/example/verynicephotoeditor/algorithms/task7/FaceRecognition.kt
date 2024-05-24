package com.example.verynicephotoeditor.algorithms.task7

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.verynicephotoeditor.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class FaceRecognition(private val context: Context) {

    fun processImage(bitmap: Bitmap): Bitmap? {
        val image = TensorImage.fromBitmap(bitmap)
        val imageProcessor =
            ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR))
                .build()
        val input = imageProcessor.process(image)

        val model = Model.newInstance(context)

        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.UINT8)
        inputFeature0.loadBuffer(input.buffer)

        val outputs = model.process(inputFeature0)
        val locations = outputs.outputFeature0AsTensorBuffer.floatArray
        val scores = outputs.outputFeature2AsTensorBuffer.floatArray
        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val height = mutable.height
        val width = mutable.width
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
        paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)

        scores.forEachIndexed { index, fl ->
            if (fl > 0.5) {
                val yMin = locations[index * 4] * height
                val xMin = locations[index * 4 + 1] * width
                val yMax = locations[index * 4 + 2] * height
                val xMax = locations[index * 4 + 3] * width

                val rect = RectF(xMin, yMin, xMax, yMax)
                for (y in rect.top.toInt() until rect.bottom.toInt()) {
                    for (x in rect.left.toInt() until rect.right.toInt()) {
                        val blurredColor = getBlurredPixelColor(mutable, x, y)
                        mutable.setPixel(x, y, blurredColor)
                    }
                }
            }
        }

        model.close()

        return mutable
    }

    private fun getBlurredPixelColor(bitmap: Bitmap, x: Int, y: Int): Int {
        val radius = 8
        var red = 0
        var green = 0
        var blue = 0
        var pixelCount = 0

        for (i in -radius..radius) {
            for (j in -radius..radius) {
                val newX = x + i
                val newY = y + j
                if (newX >= 0 && newY >= 0 && newX < bitmap.width && newY < bitmap.height) {
                    val pixel = bitmap.getPixel(newX, newY)
                    red += Color.red(pixel)
                    green += Color.green(pixel)
                    blue += Color.blue(pixel)
                    ++pixelCount
                }
            }
        }

        return Color.rgb(red / pixelCount, green / pixelCount, blue / pixelCount)
    }
}