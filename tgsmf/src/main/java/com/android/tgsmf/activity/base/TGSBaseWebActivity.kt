package com.android.tgsmf.activity.base

import androidx.viewbinding.ViewBinding
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSTitleType
import com.android.tgsmf.data.TGSWebCommand
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.view.webview.TGSJavascriptListener
import com.android.tgsmf.view.webview.TGSWebview

/*********************************************************************
 *
 *
 *********************************************************************/
abstract class TGSBaseWebActivity <VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) {
    override var TAG = javaClass.simpleName

//    companion object {
//        val TGS_ARG_INIT_URL = "init_url"
//        val TGS_ARG_TITLE_NAME = "title"
//        val TGS_ARG_JAVASCRIPT_LISTENER = "js_listener"
//    }


    protected var mUrl:String? = null
    protected var mWebview : TGSWebview? = null
    protected var mScriptListener: TGSJavascriptListener? = object : TGSJavascriptListener {
        override fun onMessage(msg: String) {
            TGSLog.d("[${TAG}_TGSJavascriptListener] showMessage : ${msg}" )
            var command : Int? = TGSWebCommand.convertWebCommand(msg)
            if(command != null) {
                runOnUiThread(Runnable {
                    var mapParams = TGSWebCommand.convertWebCommandUrlParam(msg)
                    onWebCommand(command, mapParams)
                })
            }
        }
        override fun onTitle(title: String) {
            if(mTitleName == null || mTitleName!!.isEmpty()) {
                mTitleName = title
                setCustomTitle(mTitleName!!)
            }
        }
    }


    //-----------------------------------------------------------------
    protected open fun onWebCommand(command:Int, mapParams:HashMap<String,String>?=null) {
        TGSWebCommand.onBasicCommand(this, mWebview, command, mapParams)
    }

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()
    }
    //-----------------------------------------------------------------
    override fun initFont() {
        super.initFont()
        mTextviewTitle?.let {  TGSFont.setFont(FONT_TYPE.NOTO_BOLD, it) }
    }

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        intent.getStringExtra(TGSArgument.INIT_URL)?.let {
            mUrl = it
            mWebview?.let { it.loadUrl(mUrl!!) }
        }
        intent.getParcelableExtra<TGSJavascriptListener>(TGSArgument.JAVASCRIPT_LISTENER)?.let {
            setJavascriptListener(it)
        } ?:run {
            if(mScriptListener != null)
                setJavascriptListener(mScriptListener!!)
        }
    }


    //-----------------------------------------------------------------
    open fun setJavascriptListener(scriptListener: TGSJavascriptListener) {
        mScriptListener = scriptListener
        mWebview?.run {
            mJavascriptListener = mScriptListener
        }
    }

    //-----------------------------------------------------------------
    override fun onBackPressed() {
        if(!onBackPressedWebview()) {
            super.onBackPressed()

            if(mTitleType and TGSTitleType.BACK.value > 0 ) {
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
            }
        }
    }

    //-----------------------------------------------------------------
    //웹뷰 내에서 뒤로가기 기능을 사용한다.
    open fun onBackPressedWebview() :Boolean {
        if(mWebview != null) {
            return mWebview!!.onGoBack()
        }
        return false
    }

    //-----------------------------------------------------------------
    override fun onClickTitleBackButton() {
        onBackPressed()
    }
}