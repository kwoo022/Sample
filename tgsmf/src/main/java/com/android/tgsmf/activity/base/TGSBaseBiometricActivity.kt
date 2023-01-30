package com.android.tgsmf.activity.base


import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.viewbinding.ViewBinding
import android.provider.Settings
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.content.ContextCompat
import com.android.tgsmf.R
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSToast
import com.android.tgsmf.view.dialog.TGSAlertDialog
import java.util.concurrent.Executor

abstract class TGSBaseBiometricActivity<VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) {

    //protected var mTextviewTitle : TextView? = null
    protected var mTextviewMessage : TextView? = null

    protected  var mBiometricPrompt: BiometricPrompt? = null
    protected  var mBiometricPromptInfo: BiometricPrompt.PromptInfo? = null

    protected val mBiometricLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
            } else {
                TGSToast.show(this@TGSBaseBiometricActivity, getString(R.string.biometric_dialog_not_register_msg))
            }
            checkUseBiometric()  //생체 인증 가능 여부확인 다시 호출
        }

    //-----------------------------------------------------------------
    override fun initFont() {
        super.initFont()
        mTextviewTitle?.let {  TGSFont.setFont(FONT_TYPE.NOTO_BOLD, it) }
        mTextviewMessage?.let {  TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, it) }
    }

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        var executor: Executor = ContextCompat.getMainExecutor(this)
        mBiometricPrompt = BiometricPrompt(this, executor!!, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onAuthenticationResult(false)
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthenticationResult(true)

            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onAuthenticationResult(false)
            }
        } )


        val promptBuilder: BiometricPrompt.PromptInfo.Builder = BiometricPrompt.PromptInfo.Builder()
        promptBuilder.setTitle("지문 입력")
        promptBuilder.setSubtitle("지문 센서에 손가락을 올려주세요.")
        promptBuilder.setNegativeButtonText("취소")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //  안면인식 ap사용 android 11부터 지원
            promptBuilder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }
        mBiometricPromptInfo = promptBuilder.build()
    }

    //-----------------------------------------------------------------
    override fun onResume() {
        super.onResume()

        checkUseBiometric()
    }

    //-----------------------------------------------------------------
    //생체 인증 가능 여부확인
    fun checkUseBiometric() {

        val biometricManager = BiometricManager.from(this@TGSBaseBiometricActivity)
//        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            //생체 인증 가능
            BiometricManager.BIOMETRIC_SUCCESS -> {
                //인증 실행하기
                startAuthenticate()
            }

            //기기에서 생체 인증을 지원하지 않는 경우
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            //현재 생체 인증을 사용할 수 없는 경우
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE-> {
                val strTitle = getString(R.string.biometric_dialog_unavailable_title)
                val strMessage = getString(R.string.biometric_dialog_unavailable_msg)
                TGSAlertDialog(this@TGSBaseBiometricActivity)
                    .setType(TGSAlertDialog.Companion.TGSDialogType.ERROR)
                    .setTitle(strTitle)
                    .setMessage(strMessage)
                    .setPositiveButton(getString(R.string.dialog_button_ok)) {
                        finish()
                    }
                    .show()
            }

            //생체 인식 정보가 등록되어 있지 않은 경우
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val strTitle = getString(R.string.biometric_dialog_register_title)
                val strMessage = getString(R.string.biometric_dialog_register_msg)
                TGSAlertDialog(this@TGSBaseBiometricActivity)
                    .setType(TGSAlertDialog.Companion.TGSDialogType.BASIC)
                    .setTitle(strTitle)
                    .setMessage(strMessage)
                    .setPositiveButton(getString(R.string.dialog_button_yes)) {
                        moveBiometricSettings()
                    }
                    .setNegativeButton(getString(R.string.dialog_button_no)) {
                        finish()
                    }
                    .show()
            }

            //기타 실패
            else ->  {
                val strTitle = getString(R.string.biometric_dialog_unavailable_title)
                val strMessage = getString(R.string.biometric_dialog_fail_msg)
                TGSAlertDialog(this@TGSBaseBiometricActivity)
                    .setType(TGSAlertDialog.Companion.TGSDialogType.ERROR)
                    .setTitle(strTitle)
                    .setMessage(strMessage)
                    .setPositiveButton(getString(R.string.dialog_button_ok)) {
                        finish()
                    }
                    .show()
            }

        }

    }


    //-----------------------------------------------------------------
    // 생체 인식 인증 실행
    fun startAuthenticate() {
        mBiometricPromptInfo?.let {
            mBiometricPrompt?.authenticate(it);  //인증 실행
        }
    }

    //-----------------------------------------------------------------
    // 생체 인식 결과
    open abstract fun onAuthenticationResult(success:Boolean)

    //-----------------------------------------------------------------
    open fun moveBiometricSettings() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra( Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }
        mBiometricLauncher.launch(enrollIntent)
    }

}