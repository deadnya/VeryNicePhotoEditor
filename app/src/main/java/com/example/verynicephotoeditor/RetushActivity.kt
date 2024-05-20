package com.example.verynicephotoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.verynicephotoeditor.algorithms.task2.Filters
import com.example.verynicephotoeditor.algorithms.task6.Retush
import com.example.verynicephotoeditor.databinding.ActivityRetushBinding

class RetushActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRetushBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath = intent.getStringExtra("imagePath")
        val bitmap = BitmapFactory.decodeFile(imagePath)

        binding = ActivityRetushBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView.setImageBitmap(bitmap)

        binding.radiusSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radius = progress + 3
                binding.radiusValue.text = "Radius: $radius"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.strengthSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val strength = progress / 100.0 + 0.1
                binding.strengthValue.text = "Strength: %.2f".format(strength)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                    val bitmap = Filters().drawableToBitmap(binding.imageView.drawable)
                    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                    val scaleFactorX = bitmap.width.toFloat() / binding.imageView.width
                    val scaleFactorY = bitmap.height.toFloat() / binding.imageView.height

                    val x = (event.x * scaleFactorX).toInt()
                    val y = (event.y * scaleFactorY).toInt()

                    if (x in 0 until mutableBitmap.width && y in 0 until mutableBitmap.height) {

                        val radius = binding.radiusSlider.progress + 3
                        val strength = binding.strengthSlider.progress / 100.0 + 0.1

                        binding.imageView.setImageBitmap(Retush().applyEditing(bitmap, x, y, radius.toDouble(), strength))
                    }
                }
            }
            true
        }
    }
}