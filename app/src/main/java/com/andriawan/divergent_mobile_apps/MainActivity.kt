package com.andriawan.divergent_mobile_apps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.andriawan.divergent_mobile_apps.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.initialize(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e("Fetching token failed, ${task.exception}")
            }

            val token = task.result

            Timber.d("Success token fetching $token")
        }

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }
}