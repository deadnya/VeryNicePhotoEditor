package com.example.verynicephotoeditor.basicEditingAlgorithms

import android.graphics.Bitmap
import android.graphics.Matrix

class rotationAlgorithm {

    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }
}