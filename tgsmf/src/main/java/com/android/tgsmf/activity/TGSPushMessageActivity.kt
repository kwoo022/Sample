package com.android.tgsmf.activity

import android.view.Gravity
import com.android.tgsmf.R
import com.android.tgsmf.activity.base.TGSBasePushMessageActivity
import com.android.tgsmf.databinding.TgsActivityPushMessageBinding

class TGSPushMessageActivity : TGSBasePushMessageActivity<TgsActivityPushMessageBinding>(TgsActivityPushMessageBinding::inflate) {
    override var TAG = javaClass.simpleName

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()

        mTextviewTitle = binding.layoutTitleBar.tgsTextviewSubTitle
        mButtonTitleBack = binding.layoutTitleBar.tgsButtonSubBack
        mListView = binding.tgsListviewPushMessage



        setCustomTitle(getString(R.string.push_message_title))
        setIndicatorGravity(Gravity.RIGHT)
    }

}