package com.example.verynicephotoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
        var bitmap = BitmapFactory.decodeFile(imagePath)
        var mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

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
                MotionEvent.ACTION_DOWN -> {

                    val scaleFactorX = bitmap.width.toFloat() / binding.imageView.width
                    val scaleFactorY = bitmap.height.toFloat() / binding.imageView.height

                    val x = (event.x * scaleFactorX)
                    val y = (event.y * scaleFactorY)

                    dotsList.add(Dot(x, y))

                    Affines().drawDot(mutableBitmap, 10, x.toInt(), y.toInt(), Color.RED)
                    binding.imageView.setImageBitmap(mutableBitmap)

                    if (dotsList.size >= 6) {

                        val newBitmap = Affines().affinTransmutation(bitmap, dotsList)
                        bitmap = newBitmap
                        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                        binding.imageView.setImageBitmap(newBitmap)
                        dotsList = mutableListOf<Dot>()
                    }
                }
            }
            true
        }

    }
}