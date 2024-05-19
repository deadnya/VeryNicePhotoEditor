package com.example.verynicephotoeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView




class DrawFragment : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cornersIndicator = view.findViewById<ImageView>(R.id.imageButton14)
        val curveIndicator = view.findViewById<ImageView>(R.id.imageButton15)

        cornersIndicator.setOnClickListener {
            cornersIndicator.setBackgroundResource(R.drawable.white_border)

            curveIndicator.setBackgroundResource(0)
        }

        curveIndicator.setOnClickListener {
            curveIndicator.setBackgroundResource(R.drawable.white_border)

            cornersIndicator.setBackgroundResource(0)
        }

        super.onViewCreated(view, savedInstanceState)

        val button1 = view.findViewById<ImageButton>(R.id.rotate_button)

        button1.setOnClickListener {
            val rotateFragment = RotateFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, rotateFragment)
                .addToBackStack(null)
                .commit()
        }

        val button2 = view.findViewById<ImageButton>(R.id.size_button)

        button2.setOnClickListener {
            val sizeFragment = SizeFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, sizeFragment)
                .addToBackStack(null)
                .commit()
        }

        val button3 = view.findViewById<ImageButton>(R.id.filter_button)

        button3.setOnClickListener {
            val filterFragment = FilterFragment()

            val fragmentManager = parentFragmentManager

            // Заменяем RotateFragment на FilterFragment
            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, filterFragment)
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_draw, container, false)
    }


}