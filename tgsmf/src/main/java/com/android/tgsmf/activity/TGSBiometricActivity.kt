package com.android.tgsmf.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.tgsmf.activity.base.TGSBaseBiometricActivity
import com.android.tgsmf.activity.base.TGSBaseLoginActivity
import com.android.tgsmf.databinding.TgsActivityBiometricBinding
import com.android.tgsmf.databinding.TgsActivityLoginBinding
import com.android.tgsmf.util.TGSToast

open class TGSBiometricActivity() : TGSBaseBiometricActivity<TgsActivityBiometricBinding>(TgsActivityBiometricBinding::inflate) {

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()

        mTextviewTitle = binding.tgsTextviewBiometricTitle
        mTextviewMessage = binding.tgsTextviewBiometricMessage
    }

    //-----------------------------------------------------------------
    override fun onAuthenticationResult(success: Boolean) {
        if(success) {
            TGSToast.show(this, "지문 인식 성공")
            moveNextActivity()
        } else {
            TGSToast.show(this, "지문 인식 실패")
            finish()
        }
    }

    //-----------------------------------------------------------------
    open fun moveNextActivity() {
        var intent = Intent(this, TGSNaviWebActivity::class.java)
        startActivity(intent)
        finish()
    }
}