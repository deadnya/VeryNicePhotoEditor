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
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.algorithms.task5.MainDot
import com.example.verynicephotoeditor.algorithms.task5.Spline
import com.example.verynicephotoeditor.databinding.FragmentSplinesBinding
import com.example.verynicephotoeditor.algorithms.task2.Filters
import com.example.verynicephotoeditor.algorithms.task5.AnchorDot

class SplinesFragment : Fragment() {

    private lateinit var binding: FragmentSplinesBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modeSwitch: Switch = view.findViewById(R.id.modeSwitch)
        val fxaaSwitch: Switch = view.findViewById(R.id.fxaaSwitch)

        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val layoutParams = binding.splineCanvas.layoutParams
        layoutParams.width = size.x
        layoutParams.height = size.y
        binding.splineCanvas.layoutParams = layoutParams

        val spaceLookaround = 50
        val scale = 2
        val width = size.x / scale
        val height = size.y / scale

        binding.splineCanvas.setImageBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
        val bitmap = Filters().drawableToBitmap(binding.splineCanvas.drawable)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val mainDotsList = mutableListOf<MainDot>()

        var startX = 0.0f
        var startY = 0.0f

        var mainDotsFirstIndex = -1
        var anchorPrevDotFirstIndex = -1
        var anchorNextDotFirstIndex = -1

        var isMoving = false

        binding.splineCanvas.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    startX = event.x / scale
                    startY = event.y / scale

                    mainDotsFirstIndex = -1
                    anchorPrevDotFirstIndex = -1
                    anchorNextDotFirstIndex = -1

                    mainDotsFirstIndex = Spline().getIndexOfDot(mainDotsList, startX, startY, Spline().mainDotSize, false, false)
                    anchorPrevDotFirstIndex = Spline().getIndexOfDot(mainDotsList, startX, startY, Spline().anchorDotSize, true, false)
                    anchorNextDotFirstIndex = Spline().getIndexOfDot(mainDotsList, startX, startY, Spline().anchorDotSize, false, true)
                }

                MotionEvent.ACTION_MOVE -> {

                    isMoving = true

                    if (mainDotsFirstIndex != -1) {
                        mainDotsList[mainDotsFirstIndex].changeX(event.x / scale)
                        mainDotsList[mainDotsFirstIndex].changeY(event.y / scale)
                    }

                    else if (anchorPrevDotFirstIndex != -1) {
                        mainDotsList[anchorPrevDotFirstIndex].getPrevDot()?.changeX(event.x / scale)
                        mainDotsList[anchorPrevDotFirstIndex].getPrevDot()?.changeY(event.y / scale)
                    }

                    else if (anchorNextDotFirstIndex != -1) {
                        mainDotsList[anchorNextDotFirstIndex].getNextDot()?.changeX(event.x / scale)
                        mainDotsList[anchorNextDotFirstIndex].getNextDot()?.changeY(event.y / scale)
                    }

                    val isModeSwitchChecked = modeSwitch.isChecked
                    val isFxaaSwitchChecked = fxaaSwitch.isChecked

                    mutableBitmap.eraseColor(Color.WHITE);
                    Spline().update(mainDotsList, mutableBitmap, binding, isModeSwitchChecked, isFxaaSwitchChecked)
                }

                MotionEvent.ACTION_UP -> {

                    val dist = Spline().dist(
                        (event.x / scale).toDouble(), (event.y / scale).toDouble(),
                        startX.toDouble(), startY.toDouble())

                    val isClick = dist < 10.0

                    if (isClick) {
                        isMoving = false
                    }

                    if (
                        !isMoving &&
                        mainDotsFirstIndex == -1 &&
                        anchorPrevDotFirstIndex == -1 &&
                        anchorNextDotFirstIndex == -1) {

                        val mainDot = MainDot(startX, startY, null, null)

                        val prevDot = AnchorDot(
                            Math.max(Math.min(startX - spaceLookaround, (mutableBitmap.width - spaceLookaround).toFloat()), spaceLookaround.toFloat()),
                            Math.max(Math.min(startY, (mutableBitmap.height - spaceLookaround).toFloat()), spaceLookaround.toFloat()),
                            mainDot
                        )

                        val nextDot = AnchorDot(
                            Math.max(Math.min(startX + spaceLookaround, (mutableBitmap.width - spaceLookaround).toFloat()), spaceLookaround.toFloat()),
                            Math.max(Math.min(startY, (mutableBitmap.height - spaceLookaround).toFloat()), spaceLookaround.toFloat()),
                            mainDot
                        )

                        mainDot.setAnchorDots(prevDot, nextDot)

                        mainDotsList.add(mainDot)
                    }

                    if (!isMoving && (
                                mainDotsFirstIndex != -1 ||
                                        anchorPrevDotFirstIndex != -1 ||
                                        anchorNextDotFirstIndex != -1)) {

                        if (mainDotsFirstIndex != -1) {
                            mainDotsList.removeAt(mainDotsFirstIndex)
                        }

                        else if (anchorPrevDotFirstIndex != -1) {
                            mainDotsList.removeAt(anchorPrevDotFirstIndex)
                        }

                        else if (anchorNextDotFirstIndex != -1) {
                            mainDotsList.removeAt(anchorNextDotFirstIndex)
                        }
                    }

                    val isModeSwitchChecked = modeSwitch.isChecked
                    val isFxaaSwitchChecked = fxaaSwitch.isChecked

                    mutableBitmap.eraseColor(Color.WHITE);
                    Spline().update(mainDotsList, mutableBitmap, binding, isModeSwitchChecked, isFxaaSwitchChecked)

                    isMoving = false
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
        binding = FragmentSplinesBinding.inflate(inflater, container, false)
        return binding.root
    }
}