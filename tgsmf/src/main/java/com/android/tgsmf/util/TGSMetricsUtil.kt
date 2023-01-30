package com.android.tgsmf.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue


object TGSMetricsUtil {


    //-----------------------------------------------------------------
    // DP를 pixel 값으로 변환
    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    //-----------------------------------------------------------------
    // pixel를 DP 값으로 변환
    fun pxToDp(context: Context, pixel: Float): Int {
        return (pixel / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
}