package com.example.verynicephotoeditor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionsViewModel : ViewModel() {
    private val _permissionsGranted = MutableLiveData<Boolean>()
    val permissionsGranted: LiveData<Boolean> get() = _permissionsGranted

    fun setPermissionsGranted(granted: Boolean) {
        _permissionsGranted.value = granted
    }
}