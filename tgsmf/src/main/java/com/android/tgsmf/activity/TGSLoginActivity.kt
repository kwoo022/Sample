package com.android.tgsmf.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import com.android.tgsmf.activity.base.TGSBaseLoginActivity
import com.android.tgsmf.databinding.TgsActivityLoginBinding
import com.android.tgsmf.network.restapi.*
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*********************************************************************
 *
 *
 *********************************************************************/
open class TGSLoginActivity() : TGSBaseLoginActivity<TgsActivityLoginBinding>(TgsActivityLoginBinding::inflate) {

    //-----------------------------------------------------------------
    override fun initView() {
        mEditId = binding.tgsEditLoginId
        mEditPassword = binding.tgsEditLoginPw
        mButtonLogin = binding.tgsButtonLogin
        mCheckBoxSaveAccount = binding.tgsCheckboxLoginSave

        super.initView()

        binding.tgsTextLoginFindId.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TGSRestInterface.TGS_URL_FIND_ID)))
        }
        binding.tgsTextLoginFindPw.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TGSRestInterface.TGS_URL_FIND_PW)))
        }
    }

    //-----------------------------------------------------------------
    override fun initFont() {
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, binding.tgsTextLoginId)
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, binding.tgsEditLoginId)
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, binding.tgsTextLoginPw)
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, binding.tgsEditLoginPw)
        TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, binding.tgsCheckboxLoginSave)
        TGSFont.setFont(FONT_TYPE.GMARKET_MEDIUM, binding.tgsButtonLogin)
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, binding.tgsTextLoginFindId)
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, binding.tgsTextLoginFindPw)
    }

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        mUseSoftKeyboard = true
    }

    //-----------------------------------------------------------------
    override fun onSoftKeyboardShow() {
        TGSLog.d("[${TAG} _onSoftKeyboardShow]")
        binding.tgsImageLoginLogo.visibility = View.GONE
        binding.tgsLayoutLoginBottom.visibility = View.GONE
    }

    //-----------------------------------------------------------------
    override fun onSoftKeyboardHide() {
        TGSLog.d("[${TAG} _onSoftKeyboardHide]")
        binding.tgsImageLoginLogo.visibility = View.VISIBLE
        binding.tgsLayoutLoginBottom.visibility = View.VISIBLE
    }

    //-----------------------------------------------------------------
    override fun onRequestLogin(_id:String, _password: String) {
        showLoading()

        var isSaveLoginAccount = false
        if(mCheckBoxSaveAccount != null) isSaveLoginAccount =  mCheckBoxSaveAccount!!.isChecked
        onSaveAccount(isSaveLoginAccount, _id, _password)

//        moveNextActivity()

        var serviceCall = TGSRestApi.getApiService(
                        this,
                                TGSRestInterface.TGS_REST_BASE_URL,
                                TGSRestInterface::class.java)
            //.login<TGSRestModel.TGSRestResult>(_id, _password)
            .login(_id, _password)

        //val serviceCall = serviceNew.login<TGSRestModel.TGSRestResult>(_id, _password)
        TGSRestApi.request(serviceCall, object: TGSRestApiListener<TGSRestModel.TGSRestResult> {
            override fun onResult(code: TGSRestApiResultCode, msg: String, result: TGSRestModel.TGSRestResult?) {
                hideLoading()

                TGSLog.d("[${TAG} _onRequestLogin] onResult - code : ${code.ordinal}, msg : ${msg}, code : ${result?.let { it.toString()} ?: kotlin.run { "null" }}")
                if(code == TGSRestApiResultCode.success) {
                    moveNextActivity()
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        TGSToast.show(this@TGSLoginActivity, msg)
                    }
                }
            }
        })

    }
    //-----------------------------------------------------------------
    override fun moveNextActivity() {
        var intent = Intent(this, TGSNaviWebActivity::class.java)
        //includePushData(intent)
        startActivity(intent)
        finish()
    }

}