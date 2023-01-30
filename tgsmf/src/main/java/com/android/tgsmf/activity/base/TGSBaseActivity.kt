package com.android.tgsmf.activity.base

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.R
import com.android.tgsmf.application.TGSApp
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSTitleType
import com.android.tgsmf.data.TGSWebCommand
import com.android.tgsmf.fcm.push.*
import com.android.tgsmf.fragment.TGSBaseFragment
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSSoftKeyboard
import com.android.tgsmf.util.TGSToast
import com.android.tgsmf.view.dialog.TGSAlertDialog
import com.android.tgsmf.view.dialog.TGSLoadingBar
import com.android.tgsmf.view.dialog.TGSWebDialog
import org.json.JSONException
import org.json.JSONObject


/*********************************************************************
 *
 *
 *********************************************************************/
//typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
//_binding = FragmentBringInBinding.inflate(inflater, container, false)
typealias Inflate<T> = (LayoutInflater) -> T
abstract class TGSBaseActivity <VB: ViewBinding>(private val _inflate: Inflate<VB>) : AppCompatActivity() {
    protected open var TAG = javaClass.simpleName

    private var _binding: VB? = null
    val binding get() = _binding!!
    
    // 메인 Activity 여부
    protected var mIsMain : Boolean = false


    // 결과 리턴 화면 이동
    interface ActivityResultLisener {
        fun onResult(result:ActivityResult)
    }
    private var mActivityResultLisener:ActivityResultLisener? = null
    private var mActivityResultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        mActivityResultLisener?.let { it.onResult(result) }
    }
    fun onActivityResult(intent: Intent, lisener: ActivityResultLisener) {
        mActivityResultLaunch.launch(intent)
        mActivityResultLisener = lisener
    }

    // 로딩바
    protected var mLoadingBar :TGSLoadingBar? = null
    
    // 소프트키보드 관련
    protected var mUseSoftKeyboard:Boolean = false
    private var mSoftKeyboard : TGSSoftKeyboard? = null

    // 타이틀바 관련 정보
    protected var mTitleName:String = ""
    protected var mTitleType:Int = 0
    protected var mLayoutTitlebar:ViewGroup? = null
    protected var mTextviewTitle: TextView? = null
    protected var mButtonTitleBack: ImageButton? = null
    protected var mButtonTitleClose: ImageButton? = null

    //-----------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = _inflate.invoke(layoutInflater)
        setContentView(binding.root)

        initView()
        initFont()
        initData()

        if(mIsMain)
            (application as TGSApp).setIsMainActivity(this)

        initTitlebarInfo(intent)
    }

    //-----------------------------------------------------------------
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        initTitlebarInfo(intent)
    }

    //-----------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        initSoftkeyboard()
        initPushInfo()
    }

    //-----------------------------------------------------------------
    override fun onPause() {
        super.onPause()

        mSoftKeyboard?.let { it.unRegisterSoftKeyboardCallback() }
        mSoftKeyboard = null
    }

    //-----------------------------------------------------------------
    protected open fun initView() {}
    protected open fun initData() {
        mButtonTitleBack?.let {
            it.setOnClickListener { onClickTitleBackButton() }
        }
        mButtonTitleClose?.let {
            it.setOnClickListener { onClickTitleCloseButton() }
        }
    }
    protected open fun initFont() {}


    //-----------------------------------------------------------------
    protected open fun initTitlebarInfo(intent: Intent?) {
        if(intent == null) return

        if(intent.hasExtra(TGSArgument.TITLE_TYPE)) {
            mTitleType = intent.getStringExtra(TGSArgument.TITLE_TYPE)!!.toInt()
        }
        if(intent.hasExtra(TGSArgument.TITLE_NAME)) {
            mTitleName = intent.getStringExtra(TGSArgument.TITLE_NAME)!!

            if(mTextviewTitle != null)
                mTextviewTitle!!.setText(mTitleName)
        }

        if(mTitleType != 0) {
            if(mTitleType and TGSTitleType.NONE.value > 0 ) {
                mLayoutTitlebar?.let { it.visibility = View.GONE }
            } else {
                mLayoutTitlebar?.let { it.visibility = View.VISIBLE }
                mTextviewTitle?.let { it.visibility = (if(mTitleType and TGSTitleType.TITLE.value > 0 ) {View.VISIBLE} else {View.GONE}) }
                mButtonTitleBack?.let { it.visibility = (if(mTitleType and TGSTitleType.BACK.value > 0 ) {View.VISIBLE} else {View.GONE}) }
                mButtonTitleClose?.let { it.visibility = (if(mTitleType and TGSTitleType.CLOSE.value > 0 ) {View.VISIBLE} else {View.GONE}) }
            }
        }
    }
    //-----------------------------------------------------------------
    protected fun setCustomTitle(title:String) {
        if(mTextviewTitle != null)
            mTextviewTitle!!.setText(title)
    }

    //-----------------------------------------------------------------
    protected open fun initPushInfo() {
        if(!mIsMain || !TGSFireBaseMessageData.IS_PUSHDATA)
            return

        when(TGSFireBaseMessageData.CLICK_ACTION) {
            TGS_PUSH_CLICK_ACTTION_TYPE.DIALOG.typename -> {
                TGSFireBaseMessageData.instance().onShowDialog(this, {

                }, {

                })
            }
            TGS_PUSH_CLICK_ACTTION_TYPE.ACTIVITY.typename -> {
                TGSFireBaseMessageData.instance().onShowActivity(this)
            }
        }
        TGSFireBaseMessageData.instance().clearPushMessageData()
    }


    //-----------------------------------------------------------------
    private fun initSoftkeyboard() {
        if(!mUseSoftKeyboard)
            return

        val controlManager: InputMethodManager = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        mSoftKeyboard = TGSSoftKeyboard(binding.root as ViewGroup, controlManager)
        mSoftKeyboard!!.setSoftKeyboardCallback(object : TGSSoftKeyboard.Companion.TGSSoftKeyboardChanged {
            override fun onKeyboardHide() {
                TGSLog.d("[${TAG} _hide]")
                Handler(Looper.getMainLooper()).post(Runnable {
                    onSoftKeyboardHide()
                    //Toast.makeText(getApplication(),"키보드 내려감",Toast.LENGTH_SHORT).show();
                })
            }
            override fun onKeyboardShow() {
                TGSLog.d("[${TAG} _show]")
                Handler(Looper.getMainLooper()).post(Runnable {
                    onSoftKeyboardShow()
                    //Toast.makeText(getApplication(),"키보드 올라감",Toast.LENGTH_SHORT).show();
                })
            }
        })
    }
    protected open fun onSoftKeyboardShow() {}
    protected open fun onSoftKeyboardHide() {}
    protected open fun showSoftKeyboard(visible:Boolean) {
        if(!mUseSoftKeyboard || mSoftKeyboard==null)
            return

        if(visible) {
            mSoftKeyboard!!.openSoftKeyboard()
        } else {
            mSoftKeyboard!!.closeSoftKeyboard()
        }
    }


    //-----------------------------------------------------------------
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mSoftKeyboard = null
    }


    //-----------------------------------------------------------------s
    open fun onClickTitleBackButton(){ finish() }
    open fun onClickTitleCloseButton() { finish() }

    //---------------------------------------------------------------------------------
    protected open fun showLoading() {
        println("showLoading::::::::::::::::::::2")
        if(mLoadingBar == null)
            mLoadingBar = TGSLoadingBar(this, false)

        if(!mLoadingBar!!.isShowing)
            mLoadingBar!!.show()
    }
    protected fun hideLoading() {
        if(mLoadingBar != null) {
            mLoadingBar!!.dismiss()
            mLoadingBar = null
        }
    }

    //-----------------------------------------------------------------
    open fun onQRScanResult(success:Boolean, msg:String, qrCode:String? = null) {
        if(success) {
            qrCode?.let {
                //TGSToast.show(this, "QR 체크인 성공 : ${it}")
                onRequestQRCheckIn(it)
            }
        } else {
            //TGSToast.show(this, "QR 체크인 실패 : ${ msg}")
        }
    }
    open fun onRequestQRCheckIn(qrCode:String) {}

}