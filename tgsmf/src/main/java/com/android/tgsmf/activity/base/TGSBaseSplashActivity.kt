package com.android.tgsmf.activity.base

import android.os.Handler
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.util.TGSFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


/*********************************************************************
 *
 *
 *********************************************************************/
abstract class TGSBaseSplashActivity<VB: ViewBinding>(_inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) {

    protected var mEnableCutomFont = true
    protected var mIsInitFont = false

    protected var isTimeShow = false
    protected var showTime = 1500   // 밀리초기준


    //-----------------------------------------------------------------
    abstract fun moveNextActivity()

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()


        // 최소 시간동안 스플래시 화면이 보이도록 설정한다
        if(!isTimeShow) {
            Handler().postDelayed(
                Runnable {
                    isTimeShow = true
                    onNextActivity()
                }, showTime.toLong())
        } else {
            onNextActivity()
        }
        setupFont()
    }


    //-----------------------------------------------------------------
    protected open fun setupFont() {
        if(!mEnableCutomFont)
            return

        mIsInitFont = false

        CoroutineScope(Dispatchers.IO).launch {
            var fontManager = TGSFont.getInstance(this@TGSBaseSplashActivity)
            if(fontManager != null) {
                mIsInitFont = true
                onNextActivity()
            }
        }
    }

    //-----------------------------------------------------------------
    protected open fun setShowTimeInfo(_useShowTime:Boolean, _showTime:Int) {
        isTimeShow = !_useShowTime
        showTime = _showTime
    }

    //-----------------------------------------------------------------
    private fun onNextActivity() {
        if(mEnableCutomFont && !mIsInitFont)
            return

        if(isTimeShow) {
            moveNextActivity()
        }
    }


}