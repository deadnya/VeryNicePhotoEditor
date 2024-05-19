package com.example.verynicephotoeditor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.graphics.Bitmap

class SharedViewModel : ViewModel() {
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = _bitmap

    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }
}