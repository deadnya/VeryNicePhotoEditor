package com.example.verynicephotoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.verynicephotoeditor.algorithms.task2.Filters
import com.example.verynicephotoeditor.algorithms.task6.Retush
import com.example.verynicephotoeditor.algorithms.task8.Affines
import com.example.verynicephotoeditor.algorithms.task8.Dot
import com.example.verynicephotoeditor.databinding.ActivityAffinesBinding

class AffineActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityAffinesBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath = intent.getStringExtra("imagePath")
        val bitmap = BitmapFactory.decodeFile(imagePath)

        binding = ActivityAffinesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView.setImageBitmap(bitmap)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var dotsList = mutableListOf<Dot>()

        binding.imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                    val bitmap = Filters().drawableToBitmap(binding.imageView.drawable)
                    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                    val scaleFactorX = bitmap.width.toFloat() / binding.imageView.width
                    val scaleFactorY = bitmap.height.toFloat() / binding.imageView.height

                    val x = (event.x * scaleFactorX)
                    val y = (event.y * scaleFactorY)

                    dotsList.add(Dot(x, y))

                    if (dotsList.size >= 6) {
                        binding.imageView.setImageBitmap(Affines().affinTransmutation(mutableBitmap, dotsList))
                        dotsList = mutableListOf<Dot>()
                    }
                }
            }
            true
        }

    }
}