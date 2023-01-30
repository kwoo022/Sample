package com.android.tgsmf.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.android.tgsmf.R
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.view.webview.TGSWebview

class TGSWebDialog (private val context: Context) {

    var mUrl:String = ""

    //-----------------------------------------------------------------
    private val view: View by lazy {
        View.inflate(context, R.layout.tgs_dialog_web, null)
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
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<Button>(R.id.tgs_button_web_dialog_positive))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<Button>(R.id.tgs_button_web_dialog_negative))

        AlertDialog.Builder(context, R.style.TGSDialog).setView(view).setCancelable(false)
    }

    //-----------------------------------------------------------------
    fun setUrl(url:String):TGSWebDialog {
        var webview = view.findViewById<TGSWebview>(R.id.tgs_webview_dialog_web)
        webview.loadUrl(url)
        return this
    }


    //-----------------------------------------------------------------
    fun setPositiveButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): TGSWebDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_web_dialog_positive)
        Button.apply {
            text = context.getText(textId)
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    //-----------------------------------------------------------------
    fun setPositiveButton(text: CharSequence, listener: (view: View) -> (Unit)): TGSWebDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_web_dialog_positive)
        Button.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    //-----------------------------------------------------------------
    fun setNegativeButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): TGSWebDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_web_dialog_negative)
        Button.apply {
            text = context.getText(textId)
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    //-----------------------------------------------------------------
    fun setNegativeButton(text: CharSequence, listener: (view: View) -> (Unit)): TGSWebDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_web_dialog_negative)
        Button.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    //-----------------------------------------------------------------
    fun show() {
        var negativeButton = view.findViewById<Button>(R.id.tgs_button_web_dialog_negative)
        if(!negativeButton.hasOnClickListeners()){
            var positiveButton = view.findViewById<Button>(R.id.tgs_button_web_dialog_positive)
            var layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f /* layout_weight */
            )
            positiveButton.layoutParams = layoutParams
            //positiveButton.setBackgroundResource(R.drawable.hround_rect_blue_none)
            negativeButton.visibility = View.GONE
        }

        dialog = builder.create()
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.show()
    }

    //-----------------------------------------------------------------
    fun dismiss() {
        dialog?.dismiss()
    }


}