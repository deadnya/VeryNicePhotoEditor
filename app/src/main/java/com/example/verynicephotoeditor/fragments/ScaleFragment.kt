package com.example.verynicephotoeditor.fragments

import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val bitmap = sharedViewModel.bitmap.value!!
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar_b)
        val seekBarValue = view.findViewById<TextView>(R.id.seekBarValue_a)




        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue.text = "Scaling factor: ${ String.format("%.1f", progress * 0.1).toDouble()}"+"\n"+"Current size: ${bitmap.width} x ${bitmap.height}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

            val bitmap = sharedViewModel.bitmap.value!!
            val scalingFactor = String.format("%.1f",seekBar.progress * 0.1).toDouble()


            lifecycleScope.launch(Dispatchers.Default) {
                val scalingAlgorithms = ScalingAlgorithms()
                val scaledBitmap = if (scalingFactor < 1) {
                    scalingAlgorithms.scaleImageTrilinear(bitmap, scalingFactor)
                } else {
                    scalingAlgorithms.scaleImage(bitmap, scalingFactor)
                }

                withContext(Dispatchers.Main) {
                    sharedViewModel.setBitmap(scaledBitmap)

                }
                seekBarValue.text = "Scaling factor: ${ String.format("%.1f", seekBar.progress * 0.1).toDouble()}"+"\n"+"Current size: ${bitmap.width} x ${bitmap.height}"
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
        return inflater.inflate(R.layout.fragment_scale, container, false)
    }
}