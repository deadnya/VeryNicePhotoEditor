package com.example.verynicephotoeditor.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.activities.MainActivity
import com.example.verynicephotoeditor.algorithms.task2.Filters
import kotlinx.coroutines.launch

class Decode1Fragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageButton9 = view.findViewById<ImageButton>(R.id.imageButton9)
        imageButton9.setOnClickListener {

            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

            val bitmap = sharedViewModel.bitmap.value
            if (bitmap != null) {
                lifecycleScope.launch {
                    val filteredBitmap = Filters().decodeSteganography50x50(bitmap)
                    sharedViewModel.setBitmap(filteredBitmap)
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
        return inflater.inflate(R.layout.fragment_decode1, container, false)
    }
}