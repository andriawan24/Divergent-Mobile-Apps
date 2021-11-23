package com.andriawan.divergent_mobile_apps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriawan.divergent_mobile_apps.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.initialize(this)
    }
}