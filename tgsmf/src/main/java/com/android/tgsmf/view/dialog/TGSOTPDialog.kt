package com.android.tgsmf.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.tgsmf.R
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TGSOTPDialog (private val context: Context) {
    val TAG = javaClass.simpleName

    private var mResultListener:OnResultListener? = null
    interface OnResultListener {
        fun onSuccess(otp:String)
        fun onCancel()
    }

    //-----------------------------------------------------------------
    private val view: View by lazy {
        View.inflate(context, R.layout.tgs_dialog_otp, null)
    }

    private var dialog: AlertDialog? = null
    // 터치 리스너 구현
    private val onTouchListener = View.OnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            Handler().postDelayed({
                dismiss()
            }, 5)
        }
        false
    }

    //-----------------------------------------------------------------
    private val builder: AlertDialog.Builder by lazy {
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, view.findViewById<TextView>(R.id.tgs_textview_otp_dialog_title))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<TextView>(R.id.tgs_textview_otp_dialog_guide))
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num1))
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num2))
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num3))
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num4))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<TextView>(R.id.tgs_textview_otp_dialog_limit_time))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<Button>(R.id.tgs_button_otp_dialog_ok))

        var buttonClose = view.findViewById<ImageButton>(R.id.tgs_button_otp_dialog_close)
        buttonClose.setOnTouchListener(onTouchListener)
        buttonClose.setOnClickListener {
            mResultListener?.let { it.onCancel() }
        }

        var buttonOk = view.findViewById<Button>(R.id.tgs_button_otp_dialog_ok)
        // buttonOk.setOnTouchListener(onTouchListener)
        buttonOk.setOnClickListener {
            var num1 = getEdittextNumber(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num1))
            if(num1 == null)
                return@setOnClickListener
            var num2 = getEdittextNumber(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num2))
            if(num2 == null)
                return@setOnClickListener
            var num3 = getEdittextNumber(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num3))
            if(num3 == null)
                return@setOnClickListener
            var num4 = getEdittextNumber(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num4))
            if(num4 == null)
                return@setOnClickListener

            var strNumber:String = num1+num2+num3+num4
            TGSLog.d("[${TAG} _buttonOk] otpNumber : ${strNumber}" )
            mResultListener?.let { it.onSuccess(strNumber) }

            dismiss()
        }

        view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num1).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if(it.length >= 1) {
                            setEdittextFocus(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num2))
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num2).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if(it.length >= 1) {
                            setEdittextFocus(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num3))
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num3).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let {
                        if(it.length >= 1) {
                            setEdittextFocus(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num4))
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

        AlertDialog.Builder(context, R.style.TGSDialog).setView(view).setCancelable(false)
    }

    //-----------------------------------------------------------------
    private  fun getEdittextNumber(editText: EditText):String? {
        if(editText.text.isEmpty()) {
            setEdittextFocus(editText)
            return null
        } else {
            return editText.text.toString()
        }
    }
    private fun setEdittextFocus(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        var imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText,0);
    }


    //-----------------------------------------------------------------
    fun setOtpResultListener(listener: OnResultListener):TGSOTPDialog {
        mResultListener = listener
        return this
    }

    //-----------------------------------------------------------------
    private var mLimitTime = 0
    private var mTimer:Timer? = null
    private var mStartLimitTime:Long = 0
    private var mCurrLimitTime:Long = 0
    fun setLimitTime(time:Int):TGSOTPDialog {
        mLimitTime = time
        return this
    }
    private fun updateLimitTime(_limittime:Int) {
        if(mLimitTime == 0)
            (view.findViewById<LinearLayout>(R.id.tgs_layout_otp_dialog_limit_time)).visibility = View.GONE
        else {
            val textviewLimitTime = view.findViewById<TextView>(R.id.tgs_textview_otp_dialog_limit_time)
            var strHtmlLimitTime = String.format(context.getString(R.string.otp_dialog_limit_time), _limittime)

            textviewLimitTime.text =  if(Build.VERSION.SDK_INT < 24) @Suppress("DEPRECATION") Html.fromHtml(strHtmlLimitTime)
                                    else Html.fromHtml(strHtmlLimitTime, Html.FROM_HTML_MODE_COMPACT)

            (view.findViewById<LinearLayout>(R.id.tgs_layout_otp_dialog_limit_time)).visibility = View.VISIBLE
        }
    }

    //-----------------------------------------------------------------
    fun show() {

        updateLimitTime(mLimitTime)
        mStartLimitTime = System.currentTimeMillis()
        mCurrLimitTime = mStartLimitTime
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mCurrLimitTime = System.currentTimeMillis()
                var elapsedTime = ((mCurrLimitTime - mStartLimitTime) / 1000).toInt()
                var limittime = Math.max(mLimitTime - elapsedTime, 0)
                CoroutineScope(Dispatchers.Main).launch {
                    updateLimitTime(limittime)
                }


                if(limittime <= 0)
                    mTimer!!.cancel()
            }
        }, 0, 1000)

        setEdittextFocus(view.findViewById<EditText>(R.id.tgs_edittext_otp_dialog_num1))

        dialog = builder.create()
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.show()
    }

    //-----------------------------------------------------------------
    fun dismiss() {
        dialog?.dismiss()
        mTimer?.let { it.cancel() }
    }


}