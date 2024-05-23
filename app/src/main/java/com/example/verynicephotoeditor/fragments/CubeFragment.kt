package com.example.verynicephotoeditor.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.algorithms.task2.Filters
import com.example.verynicephotoeditor.algorithms.task9.Cube
import com.example.verynicephotoeditor.algorithms.task9.Dot
import com.example.verynicephotoeditor.algorithms.task9.SpinningCube
import com.example.verynicephotoeditor.databinding.FragmentCubeBinding

class CubeFragment : Fragment()  {

    private lateinit var binding: FragmentCubeBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val display = requireActivity().windowManager.defaultDisplay
        var size = Point()
        display.getSize(size)

        val layoutParams = binding.cubeCanvas.layoutParams
        layoutParams.width = size.x
        layoutParams.height = size.y
        binding.cubeCanvas.layoutParams = layoutParams

        val scale = 2
        val width = size.x / scale
        val height = size.y / scale

        size = Point(width, height)

        binding.cubeCanvas.setImageBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
        val bitmap = Filters().drawableToBitmap(binding.cubeCanvas.drawable)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val cube = Cube(
            Dot(-1.0, -1.0, -1.0),
            Dot(1.0, -1.0, -1.0),
            Dot(1.0, 1.0, -1.0),
            Dot(-1.0, 1.0, -1.0),
            Dot(-1.0, -1.0, 1.0),
            Dot(1.0, -1.0, 1.0),
            Dot(1.0, 1.0, 1.0),
            Dot(-1.0, 1.0, 1.0)
        )

        cube.rotateX(100.0)
        cube.rotateY(100.0)

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.rsz_1lucky)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val image = drawable?.let { Filters().drawableToBitmap(it) }

        if (image != null) {
            SpinningCube().drawCube(mutableBitmap, cube, size, image)
        }
        binding.cubeCanvas.setImageBitmap(mutableBitmap)

        var startX = 0.0
        var startY = 0.0

        var distX = 0.0
        var distY = 0.0

        binding.cubeCanvas.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    startX = (event.x / scale).toDouble()
                    startY = (event.y / scale).toDouble()
                }

                MotionEvent.ACTION_MOVE -> {
                    distX = (event.x / scale).toDouble() - startX
                    distY = (event.y / scale).toDouble() - startY

                    startX += distX
                    startY += distY

                    cube.rotateX(distY)
                    cube.rotateY(-distX)

                    mutableBitmap.eraseColor(Color.WHITE)
                    if (image != null) {
                        SpinningCube().drawCube(mutableBitmap, cube, size, image)
                    }
                    binding.cubeCanvas.setImageBitmap(mutableBitmap)
                }

                MotionEvent.ACTION_UP -> {

                }
            }
            true
        }

        binding.backButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.mainFrame, MainMenuFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCubeBinding.inflate(inflater, container, false)
        return binding.root
    }
}