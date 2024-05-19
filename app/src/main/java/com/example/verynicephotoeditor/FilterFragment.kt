package com.example.verynicephotoeditor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.example.verynicephotoeditor.algorithms.task2.Filters

class FilterFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button1 = view.findViewById<ImageButton>(R.id.rotate_button)

        button1.setOnClickListener {
            val rotateFragment = RotateFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.frame, rotateFragment)
                .addToBackStack(null)
                .commit()
        }

        val button2 = view.findViewById<ImageButton>(R.id.size_button)

        button2.setOnClickListener {
            val sizeFragment = SizeFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.frame, sizeFragment)
                .addToBackStack(null)
                .commit()
        }

        val button3 = view.findViewById<ImageButton>(R.id.draw_button)

        button3.setOnClickListener {
            val sizeFragment = DrawFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.frame, sizeFragment)
                .addToBackStack(null)
                .commit()
        }

        val button4 = view.findViewById<ImageButton>(R.id.face_button)

        button4.setOnClickListener {
            val faceFragment = FaceFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.frame, faceFragment)
                .addToBackStack(null)
                .commit()
        }

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {

            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

            val bitmap = sharedViewModel.bitmap.value
            if (bitmap != null) {
                val filteredBitmap = Filters().applyGrayscaleFilter(bitmap)
                sharedViewModel.setBitmap(filteredBitmap)
            }

            Log.d("AAAA", "AAAA")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }
}