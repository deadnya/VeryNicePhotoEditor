package com.example.verynicephotoeditor.fragments

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.verynicephotoeditor.PermissionsViewModel
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.activities.AffineActivity
import com.example.verynicephotoeditor.activities.ImageStorageActivity
import com.example.verynicephotoeditor.activities.RetushActivity
import com.example.verynicephotoeditor.databinding.FragmentMainMenuBinding
import java.io.File
import java.io.FileOutputStream


class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    private lateinit var startingBitmap: Bitmap
    private lateinit var permissionsViewModel: PermissionsViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                var bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)

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
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private val pickImageForRetush =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    var bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        imageUri
                    )

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
                } else {
                    Toast.makeText(requireContext(), "Failed to pick image", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Image picking operation was not successful",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private val pickImageForAffines =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    var bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        imageUri
                    )

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
                } else {
                    Toast.makeText(requireContext(), "Failed to pick image", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Image picking operation was not successful",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    WRITE_EXTERNAL_STORAGE,
                    CAMERA
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
        permissionsViewModel =
            ViewModelProvider(requireActivity())[PermissionsViewModel::class.java]

        permissionsViewModel.permissionsGranted.observe(viewLifecycleOwner) { granted ->
            binding.goToAffines.isEnabled = granted
            binding.goToFilters.isEnabled = granted
            binding.goToRetush.isEnabled = granted
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