package com.example.verynicephotoeditor

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.verynicephotoeditor.algorithms.task9.SpinningCube
import com.example.verynicephotoeditor.databinding.FragmentMainMenuBinding
import java.io.File
import java.io.FileOutputStream


class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    private lateinit var startingBitmap : Bitmap

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri!!)
                var bitmap = ImageDecoder.decodeBitmap(source)

                // Check if the bitmap is hardware accelerated
                if (bitmap.config == Bitmap.Config.HARDWARE) {
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                }

                val savedBitmap = bitmap.copy(bitmap.config, true)

                startingBitmap = savedBitmap

                val file = File(requireContext().cacheDir, "image")
                val out = FileOutputStream(file)
                startingBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()

                val intent = Intent(requireContext(), ImageStorageActivity::class.java)
                intent.putExtra("imagePath", file.absolutePath)
                startActivity(intent)

                Log.d("AAA", "AAA")
            }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImageForRetush =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
                    var bitmap = ImageDecoder.decodeBitmap(source)

                    // Check if the bitmap is hardware accelerated
                    if (bitmap.config == Bitmap.Config.HARDWARE) {
                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    }

                    val savedBitmap = bitmap.copy(bitmap.config, true)

                    startingBitmap = savedBitmap

                    val file = File(requireContext().cacheDir, "image")
                    val out = FileOutputStream(file)
                    startingBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                    out.close()

                    val intent = Intent(requireContext(), RetushActivity::class.java)
                    intent.putExtra("imagePath", file.absolutePath)
                    startActivity(intent)

                    Log.d("AAA", "AAA")
                } else {
                    // Handle the case where the imageUri is null
                    Log.d("AAA", "Image URI is null")
                }
            } else {
                // Handle the case where the result is not OK
                Log.d("AAA", "Image picking operation was not successful")
            }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImageForAffines =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
                    var bitmap = ImageDecoder.decodeBitmap(source)

                    // Check if the bitmap is hardware accelerated
                    if (bitmap.config == Bitmap.Config.HARDWARE) {
                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    }

                    val savedBitmap = bitmap.copy(bitmap.config, true)

                    startingBitmap = savedBitmap

                    val file = File(requireContext().cacheDir, "image")
                    val out = FileOutputStream(file)
                    startingBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                    out.close()

                    val intent = Intent(requireContext(), AffineActivity::class.java)
                    intent.putExtra("imagePath", file.absolutePath)
                    startActivity(intent)

                    Log.d("AAA", "AAA")
                } else {
                    // Handle the case where the imageUri is null
                    Log.d("AAA", "Image URI is null")
                }
            } else {
                // Handle the case where the result is not OK
                Log.d("AAA", "Image picking operation was not successful")
            }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        super.onCreate(savedInstanceState)

        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->

            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED,
                    WRITE_EXTERNAL_STORAGE
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

        binding.goToFilters.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        binding.goToSplines.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.mainFrame, SplinesFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }

        binding.goToCube.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.mainFrame, CubeFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }

        binding.goToRetush.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageForRetush.launch(intent)
        }

        binding.goToAffines.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageForAffines.launch(intent)
        }

        return binding.root
    }
}