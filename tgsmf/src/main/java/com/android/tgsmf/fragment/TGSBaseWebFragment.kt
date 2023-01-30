package com.android.tgsmf.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.view.webview.TGSWebview
import com.android.tgsmf.view.webview.TGSJavascriptListener as TGSJavascriptListener

/*********************************************************************
 *
 *
 *********************************************************************/
open class TGSBaseWebFragment<VB: ViewBinding>(private val _inflate: Inflate<VB>): TGSBaseFragment<VB>(_inflate) {


    protected var mUrl:String? = null

    protected var mScriptListener: TGSJavascriptListener? = null
    protected var mWebview : TGSWebview? = null

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()
    }

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        if(arguments != null) {
            requireArguments().getString(TGSArgument.INIT_URL)?.let {  mUrl = it}
            if(requireArguments().containsKey(TGSArgument.JAVASCRIPT_LISTENER)) {
                mScriptListener = requireArguments().getParcelable(TGSArgument.JAVASCRIPT_LISTENER)
            }
        }
    }

    //-----------------------------------------------------------------
    override fun initFont() {
        mTextviewTitle?.let {  TGSFont.setFont(FONT_TYPE.NOTO_BOLD, it) }
    }


    //-----------------------------------------------------------------
    override fun onStart() {
        super.onStart()

        if(mScriptListener != null)
            setJavascriptListener(mScriptListener!!)
        println("start::::::::::::::::::::::::::::::")
        loadUrl(mUrl)
    }

    //-----------------------------------------------------------------
    override fun onBackPressed(): Boolean {
        if(mWebview != null)
            return mWebview!!.onGoBack()
        return false
    }

    //-----------------------------------------------------------------
    open fun loadUrl(_url:String?) {
        mUrl = _url
        TGSLog.d("[${TAG} _loadUrl] ${mUrl}")

        if(mUrl == null || mWebview == null) return
        if(mUrl!!.isEmpty()) return

        mWebview?.let{
            it.loadUrl(mUrl!!)
        }
    }
    //-----------------------------------------------------------------
    open fun refresh() {
        loadUrl(mUrl)
    }

    //-----------------------------------------------------------------
    open fun setJavascriptListener(scriptListener: TGSJavascriptListener) {
        mScriptListener = scriptListener
        mWebview?.run {
            mJavascriptListener  = mScriptListener
        }
    }

}