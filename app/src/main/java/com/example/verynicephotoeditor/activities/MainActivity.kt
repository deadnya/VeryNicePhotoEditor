package com.example.verynicephotoeditor.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.verynicephotoeditor.PermissionsViewModel
import com.example.verynicephotoeditor.fragments.MainMenuFragment
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.SharedViewModel
import com.example.verynicephotoeditor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionsViewModel: PermissionsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsViewModel = ViewModelProvider(this)[PermissionsViewModel::class.java]

        enableEdgeToEdge()
        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                val allPermissionsGranted = results.all { it.value }
                permissionsViewModel.setPermissionsGranted(allPermissionsGranted)
                if (!allPermissionsGranted) {
                    Toast.makeText(
                        this,
                        "All permissions are required for the app to function properly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.CAMERA
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.CAMERA
                )
            )
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))

        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(R.id.mainFrame, MainMenuFragment::class.java, null)
        }
    }
}