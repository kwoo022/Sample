package com.android.tgsmf.fragment

import android.view.ViewGroup
import com.android.tgsmf.databinding.TgsFragmentWebBinding
import com.android.tgsmf.databinding.TgsTitleBarBinding
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont

class TGSWebFragment :TGSBaseWebFragment<TgsFragmentWebBinding>(TgsFragmentWebBinding::inflate) {

    //-----------------------------------------------------------------
    override fun initView() {
        mLayoutTitlebar = binding.layoutTitleBar.tgsLayoutTitleBar
        mTextviewTitle = binding.layoutTitleBar.tgsTextviewSubTitle
        mButtonTitleBack = binding.layoutTitleBar.tgsButtonSubBack
        mButtonTitleClose = binding.layoutTitleBar.tgsButtonSubClose

        mWebview = binding.tgsWebviewWebClose
    }



}