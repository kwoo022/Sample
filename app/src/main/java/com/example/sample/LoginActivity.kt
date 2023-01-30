package com.example.sample

import android.content.Intent
import com.android.tgsmf.activity.TGSLoginActivity

class LoginActivity : TGSLoginActivity() {

    override fun moveNextActivity() {

        // var intent = Intent(this, BiometricActivity::class.java)
        var intent = Intent(this, MainActivity2::class.java)
        //includePushData(intent)
        startActivity(intent)
        finish()
    }

}