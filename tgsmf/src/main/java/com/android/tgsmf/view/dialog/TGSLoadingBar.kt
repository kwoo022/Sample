package com.android.tgsmf.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.android.tgsmf.R
import com.android.tgsmf.util.TGSLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TGSLoadingBar(context: Context, private val isInfinity:Boolean = true) :Dialog(context) {

    private var mProgressBarCircle : ProgressBar? = null

    private var mLayoutLine : LinearLayout? = null
    private var mTextviewLine : TextView? = null
    private var mProgressBarLine : ProgressBar? = null

    //-----------------------------------------------------------------
    private val view: View by lazy {
        View.inflate(context, R.layout.tgs_layout_loading_bar, null)
    }

    //-----------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 다이얼 로그 제목을 안보이게...
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view)

        setCancelable(false)

        // background를 투명하게 만듦
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mProgressBarCircle = view.findViewById<ProgressBar>(R.id.tgs_progress_loading_bar_circle)
        mLayoutLine = view.findViewById<LinearLayout>(R.id.tgs_layout_loading_bar_line)
        mTextviewLine = view.findViewById<TextView>(R.id.tgs_textview_loading_bar_line)
        mProgressBarLine = view.findViewById<ProgressBar>(R.id.tgs_progress_loading_bar_line)
        if(isInfinity) {
            mProgressBarCircle
        }

        setLoadingType(isInfinity)
    }

    //-----------------------------------------------------------------
    fun setLoadingType(_isInfinity:Boolean) {
        if(_isInfinity) {
            mProgressBarCircle?.visibility = View.VISIBLE
            mLayoutLine?.visibility = View.GONE
        } else {
            mProgressBarCircle?.visibility = View.GONE
            mLayoutLine?.visibility = View.VISIBLE
        }
    }

    //-----------------------------------------------------------------
    fun setProgress(curr:Int, total:Int, fileName:String? = null) {
        setLoadingType(false);

        CoroutineScope(Dispatchers.Main).launch {
            if(fileName != null) {
                mTextviewLine?.visibility = View.VISIBLE
                mTextviewLine?.setText(fileName)
            } else {
                mTextviewLine?.visibility = View.GONE
            }

            val percent = ((curr.toFloat()/total.toFloat()) * 100)
            mProgressBarLine!!.progress =percent.toInt()
        }

    }
}