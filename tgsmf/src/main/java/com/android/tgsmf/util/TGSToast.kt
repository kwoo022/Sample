package com.android.tgsmf.util

import android.app.Activity
import android.widget.Toast

object TGSToast {
    var mToast: Toast? = null

    fun show(activiy: Activity, msg:String) {
        activiy.runOnUiThread {
            if(mToast != null)
                mToast!!.cancel()
            mToast = Toast.makeText(activiy, msg, Toast.LENGTH_SHORT)
            mToast!!.show()
        }
    }
}