package com.android.tgsmf.application

import android.app.Activity
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.tgsmf.activity.base.TGSBaseActivity

open class TGSApp : Application(), LifecycleObserver{

    companion object {
        var IsAppForeground = false
    }

    //---------------------------------------------------------------
    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    //---------------------------------------------------------------
    // 앱 내 메인 Activity 실행여부
    private var mIsRunningMainActivity:Activity? = null
    val IsRunningMain get() = (if(mIsRunningMainActivity != null) true else false)
    val RunningMain get() = mIsRunningMainActivity
    fun setIsMainActivity(mainActivity:Activity) {
        mIsRunningMainActivity = mainActivity
    }

    //---------------------------------------------------------------
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {IsAppForeground = false}
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {IsAppForeground = true}

    //---------------------------------------------------------------

}