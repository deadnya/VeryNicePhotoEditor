package com.example.verynicephotoeditor.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.activities.MainActivity
import com.example.verynicephotoeditor.algorithms.task2.Filters

class WaveFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            sharedViewModel.setBitmap(imageBitmap)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageButton8 = view.findViewById<ImageButton>(R.id.imageButton8)

        imageButton8.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        }

        val seekBar_a = view.findViewById<SeekBar>(R.id.seekBar_a)
        val seekBarValue_a = view.findViewById<TextView>(R.id.seekBarValue_a)

        val seekBar_b = view.findViewById<SeekBar>(R.id.seekBar_b)
        val seekBarValue_b = view.findViewById<TextView>(R.id.seekBarValue_b)

        seekBar_a.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue_a.text = "a: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBar_b.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue_b.text = "b: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {

            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

            val bitmap = sharedViewModel.bitmap.value
            if (bitmap != null) {
                val filteredBitmap = Filters().applyWave(bitmap, seekBar_a.progress, seekBar_b.progress)
                sharedViewModel.setBitmap(filteredBitmap)
            }

            Log.d("AAAA", "AAAAAAAAAAA")
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
        return inflater.inflate(R.layout.fragment_wave, container, false)
    }
}