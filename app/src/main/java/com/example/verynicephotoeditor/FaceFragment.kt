package com.example.verynicephotoeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FaceFragment : Fragment() {

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_face, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button5 = view.findViewById<ImageButton>(R.id.backPanel)

        button5.setOnClickListener {
            val panelFragment = PanelFragment()

            val fragmentManager = parentFragmentManager

            fragmentManager.beginTransaction()
                .replace(R.id.framelayout, panelFragment)
                .addToBackStack(null)
                .commit()
        }

        val buttonList = listOf(
            ButtonModel("Rotate"),
            ButtonModel("Filter"),
            ButtonModel("Size"),
            ButtonModel("Draw"),
            ButtonModel("Face"),
            ButtonModel("Cube"),
            ButtonModel("Masking")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ButtonAdapter(buttonList, parentFragmentManager)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }
}

