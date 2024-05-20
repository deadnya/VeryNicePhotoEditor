package com.example.verynicephotoeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

class SizeFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val textView = view.findViewById<TextView>(R.id.textView)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textView.text = (progress / 10.0).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        val button1 = view.findViewById<ImageButton>(R.id.rotate_button)

        button1.setOnClickListener {
            val rotateFragment = RotateFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, rotateFragment)
                .addToBackStack(null)
                .commit()
        }

        super.onViewCreated(view, savedInstanceState)

        val button2 = view.findViewById<ImageButton>(R.id.filter_button)

        button2.setOnClickListener {
            val filterFragment = FilterFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, filterFragment)
                .addToBackStack(null)
                .commit()
        }

        val button3 = view.findViewById<ImageButton>(R.id.draw_button)

        button3.setOnClickListener {
            val sizeFragment = DrawFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, sizeFragment)
                .addToBackStack(null)
                .commit()
        }

        val button4 = view.findViewById<ImageButton>(R.id.face_button)

        button4.setOnClickListener {
            val faceFragment = FaceFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, faceFragment)
                .addToBackStack(null)
                .commit()
        }

        val button5 = view.findViewById<ImageButton>(R.id.backPanel)

        button5.setOnClickListener {
            val panelFragment = PanelFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, panelFragment)
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_size, container, false)
    }
}