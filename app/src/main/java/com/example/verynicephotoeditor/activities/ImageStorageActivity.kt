package com.example.verynicephotoeditor.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verynicephotoeditor.ButtonAdapter
import com.example.verynicephotoeditor.ButtonModel
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.fragments.RotateFragment
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.databinding.ActivityImageStorageBinding


class ImageStorageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageStorageBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val imagePath = intent.getStringExtra("imagePath")
        val bitmap = BitmapFactory.decodeFile(imagePath)

        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

        binding = ActivityImageStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainImage.setImageBitmap(bitmap)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.setBitmap(bitmap)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.bitmap.observe(this, { bitmap ->
            binding.mainImage.setImageBitmap(bitmap) // set the bitmap to the ImageView
        })

        supportFragmentManager.commit {
            replace(R.id.frame, RotateFragment::class.java, null)
        }

        val buttonList = listOf(
            ButtonModel("Rotate"),
            ButtonModel("Scale"),
            ButtonModel("Grayscale"),
            ButtonModel("Contrast"),
            ButtonModel("Pixelate"),
            ButtonModel("Sepia"),
            ButtonModel("Solarize"),
            ButtonModel("Dither"),
            ButtonModel("Edge"),
            ButtonModel("Blur"),
            ButtonModel("Glass"),
            ButtonModel("Oil"),
            ButtonModel("Emboss"),
            ButtonModel("Wave"),
            ButtonModel("Mask"),
            ButtonModel("Encode1"),
            ButtonModel("Decode1"),
            ButtonModel("Encode2"),
            ButtonModel("Decode2"),
            ButtonModel("Encode3"),
            ButtonModel("Decode3"),
            ButtonModel("")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ButtonAdapter(buttonList, supportFragmentManager)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}