package com.android.tgsmf.activity

import android.content.Intent
import com.android.tgsmf.activity.base.TGSBaseSplashActivity
import com.android.tgsmf.databinding.TgsActivitySplashBinding


/*********************************************************************
 *
 *
 *********************************************************************/
open class TGSSplashActivity : TGSBaseSplashActivity<TgsActivitySplashBinding>(TgsActivitySplashBinding::inflate) {
    //-----------------------------------------------------------------
    override fun moveNextActivity() {
        var intent = Intent(this, TGSLoginActivity::class.java)
        //includePushData(intent)
        startActivity(intent)
        finish()
    }
}