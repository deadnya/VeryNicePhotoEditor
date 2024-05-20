package com.example.verynicephotoeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

class FaceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_face, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button2 = view.findViewById<ImageButton>(R.id.size_button)

        button2.setOnClickListener {
            val sizeFragment = SizeFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, sizeFragment)
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
}