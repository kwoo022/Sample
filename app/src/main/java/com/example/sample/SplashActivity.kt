package com.example.sample

import android.content.Intent
import com.android.tgsmf.activity.TGSSplashActivity

class SplashActivity : TGSSplashActivity() {
    override fun moveNextActivity() {
        var intent = Intent(this, MainActivity3::class.java)
        startActivity(intent)
        finish()
    }
}