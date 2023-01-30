package com.android.tgsmf.activity

import com.android.tgsmf.activity.base.TGSBaseWebActivity
import com.android.tgsmf.databinding.TgsActivityWebBinding

class TGSWebActivity : TGSBaseWebActivity<TgsActivityWebBinding>(TgsActivityWebBinding::inflate) {

    override var TAG = javaClass.simpleName

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()
println("fdsafsdafsdafsdafsdafsda")
        mLayoutTitlebar = binding.layoutTitleBar.tgsLayoutTitleBar
        mTextviewTitle =  binding.layoutTitleBar.tgsTextviewSubTitle
        mButtonTitleClose = binding.layoutTitleBar.tgsButtonSubClose
        mButtonTitleBack = binding.layoutTitleBar.tgsButtonSubBack
        mWebview =binding.tgsWebviewWeb
    }

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()
    }

}