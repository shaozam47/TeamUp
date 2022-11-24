package com.example.teamup.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.teamup.R
import com.example.teamup.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            var currentUserID: String = FireStoreClass().getCurrentUserId()
            if(currentUserID!="") {
                startActivity(Intent(this, MainActivity::class.java))
            }
            else {
                startActivity(Intent(this, Intro::class.java))
            }
                    finish()
        }, 2500)
    }
}