package com.example.verynicephotoeditor.fragments

import android.app.Activity
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
import com.example.verynicephotoeditor.algorithms.task3.ScalingAlgorithms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScaleFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                sharedViewModel.setBitmap(imageBitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val bitmap = sharedViewModel.bitmap.value!!
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar_b)
        val seekBarValue = view.findViewById<TextView>(R.id.seekBarValue_a)
        val imageButton8 = view.findViewById<ImageButton>(R.id.imageButton8)

        imageButton8.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue.text = "Scaling factor: ${
                    String.format("%.1f", progress * 0.1).toDouble()
                }" + "\n" + "Current size: ${bitmap.width} x ${bitmap.height}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

            val bitmap = sharedViewModel.bitmap.value!!

            lifecycleScope.launch(Dispatchers.Default) {
                val scalingFactor = seekBar.progress * 0.1
                val scalingAlgorithms = ScalingAlgorithms()
                val scaledBitmap = if (scalingFactor < 1) {
                    scalingAlgorithms.scaleImageTrilinear(bitmap, scalingFactor)
                } else {
                    scalingAlgorithms.scaleImage(bitmap, scalingFactor)
                }

                withContext(Dispatchers.Main) {
                    sharedViewModel.setBitmap(scaledBitmap)
                    seekBarValue.text = "Scaling factor: ${
                        String.format("%.1f", seekBar.progress * 0.1).toDouble()
                    }" + "\n" + "Current size: ${scaledBitmap.width} x ${scaledBitmap.height}"
                }
            }
        }
        view.findViewById<ImageButton>(R.id.backPanel).setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return inflater.inflate(R.layout.fragment_scale, container, false)
    }
}