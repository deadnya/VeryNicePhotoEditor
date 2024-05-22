package com.example.verynicephotoeditor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.verynicephotoeditor.algorithms.task2.Filters

class Decode3Fragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {

            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

            val bitmap = sharedViewModel.bitmap.value
            if (bitmap != null) {
                Toast.makeText(requireContext(), Filters().decodeSteganographyText(bitmap), Toast.LENGTH_SHORT).show()
            }

            Log.d("AAAA", "AAAA")
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
        return inflater.inflate(R.layout.fragment_decode3, container, false)
    }
}