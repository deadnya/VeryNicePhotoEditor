package com.example.verynicephotoeditor.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.activities.MainActivity
import com.example.verynicephotoeditor.algorithms.task2.Filters

class MaskFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seekBar_str = view.findViewById<SeekBar>(R.id.seekBar_str)
        val seekBarValue_str = view.findViewById<TextView>(R.id.seekBarValue_str)

        val seekBar_rad = view.findViewById<SeekBar>(R.id.seekBar_rad)
        val seekBarValue_rad = view.findViewById<TextView>(R.id.seekBarValue_rad)
        var actualProgress = 3

        seekBar_str.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue_str.text = "Strength: ${progress / 100.0f}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBar_rad.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                actualProgress = progress

                if (progress % 2 == 0) {
                    actualProgress++
                }

                seekBarValue_rad.text = "Radius: $actualProgress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {

            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

            val bitmap = sharedViewModel.bitmap.value
            if (bitmap != null) {
                val filteredBitmap = Filters().applyUnsharpMask(bitmap, seekBar_str.progress / 100.0, actualProgress)
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
        return inflater.inflate(R.layout.fragment_mask, container, false)
    }
}