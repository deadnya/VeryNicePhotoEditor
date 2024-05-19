package com.example.verynicephotoeditor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.verynicephotoeditor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(R.id.mainFrame, MainMenuFragment::class.java, null)
        }
    }
}