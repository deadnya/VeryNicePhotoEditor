package com.example.verynicephotoeditor.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.activities.MainActivity
import com.example.verynicephotoeditor.algorithms.task1.RotationAlgorithm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var sharedViewModel: SharedViewModel


class RotateFragment : Fragment() {

    private var isOtherAlgorithmUsed = true
    private var rotationDegrees = 0.0
    private var originalBitmap: Bitmap? = null
    override fun onDestroyView() {
        super.onDestroyView()
        if (originalBitmap != null) {
            recyclerBitmap(originalBitmap!!)
        }
        if (isRemoving || requireActivity().isFinishing) {
            isOtherAlgorithmUsed = true
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                sharedViewModel.setBitmap(imageBitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val seekBar = view.findViewById<SeekBar>(R.id.seekBar_b)
        val seekBarValue = view.findViewById<TextView>(R.id.seekBarValue_a)
        val imageButton8 = view.findViewById<ImageButton>(R.id.imageButton8)

        imageButton8.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue.text = "Degree: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {
            val bitmap: Bitmap

            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            if (isOtherAlgorithmUsed) {
                bitmap = sharedViewModel.bitmap.value!!
                originalBitmap = bitmap.copy(bitmap.config, true)
                rotationDegrees = seekBar.progress.toDouble()
            } else {
                bitmap = originalBitmap!!
                rotationDegrees += seekBar.progress.toDouble()
                rotationDegrees %= 360
            }

            lifecycleScope.launch(Dispatchers.Default) {

                val rotatedBitmap =
                    RotationAlgorithm().rotateBitmap(bitmap, rotationDegrees)
                isOtherAlgorithmUsed = false

                withContext(Dispatchers.Main) {
                    sharedViewModel.setBitmap(rotatedBitmap)
                }
            }
        }

        view.findViewById<ImageButton>(R.id.backPanel).setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun recyclerBitmap(bitmap: Bitmap) {
        bitmap.recycle()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return inflater.inflate(R.layout.fragment_rotate, container, false)
    }

}