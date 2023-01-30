package com.android.tgsmf.activity.base

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSConst
import com.android.tgsmf.util.TGSPreference
import com.android.tgsmf.util.TGSToast


/*********************************************************************
 *
 *
 *********************************************************************/
abstract class TGSBaseLoginActivity<VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) {

    protected var mIsSaveAccount:Boolean = true

    protected var mEditId:EditText? = null
    protected var mEditPassword:EditText? = null
    protected var mButtonLogin: Button? = null
    protected var mCheckBoxSaveAccount:CheckBox? = null


    abstract fun onRequestLogin(_id:String, _password: String)
    abstract fun moveNextActivity()


    //-----------------------------------------------------------------
    override fun initView() {
        mButtonLogin?.apply { setOnClickListener(mLoginClickListener) }
    }
    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        mUseSoftKeyboard = true

        // 저장된 아이디와 비번이 있을 경우 View에 적용한다.
        TGSPreference.getInstance(this, TGSConst.TGS_PREFER_NAME)
        var isSavedAccount = false
        TGSPreference.getPrefer(TGSConst.TGS_PREFER_KEY_SAVE_LOGIN_ACCOUNT)?.let {
            isSavedAccount = it as Boolean
            if(mCheckBoxSaveAccount != null)
                mCheckBoxSaveAccount!!.isChecked = isSavedAccount
        }
        if(isSavedAccount) {
            TGSPreference.getPrefer(TGSConst.TGS_PREFER_KEY_LOGIN_ID)?.let {
                var strId = it as String
                if(mEditId != null)
                    mEditId!!.setText(strId)
            }

            TGSPreference.getPrefer(TGSConst.TGS_PREFER_KEY_LOGIN_PW)?.let {
                var strId = it as String
                if(mEditPassword != null)
                    mEditPassword!!.setText(strId)
            }
        }
    }

    //-----------------------------------------------------------------
    private var mTimeBack:Long = 0
    override fun onBackPressed() {
        if(System.currentTimeMillis() - mTimeBack > 2000) {
            mTimeBack = System.currentTimeMillis()
            TGSToast.show(this, getString(R.string.msg_app_exit))
            //Toast.makeText(applicationContext, getString(R.string.msg_app_exit), Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }

    //-----------------------------------------------------------------
    protected open fun onSaveAccount(_isSave:Boolean, _id:String, _password:String) {
        TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_SAVE_LOGIN_ACCOUNT, _isSave)

        if(_isSave) {
            TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_LOGIN_ID, _id)
            TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_LOGIN_PW, _password)
        } else {
            TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_LOGIN_ID, "")
            TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_LOGIN_PW, "")
        }
    }
    //-----------------------------------------------------------------
    protected open fun onClickLogin() {
        var strId:String = ""
        var strPw:String = ""
        if(mEditId != null) {
            strId = mEditId!!.text.trim().toString()
            if(strId.isEmpty()) {
                Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_LONG).show()
                return
            }
        }
        if(mEditPassword != null) {
            strPw = mEditPassword!!.text.trim().toString()
            if(strPw.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_LONG).show()
                return
            }
        }
        onRequestLogin(strId, strPw)
    }
    //-----------------------------------------------------------------
    private val mLoginClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            onClickLogin()
        }

    }


}