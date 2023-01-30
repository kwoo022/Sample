package com.android.tgsmf.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.android.tgsmf.R
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont

/*********************************************************************
 *
 *
 *********************************************************************/
class TGSAlertDialog (private val context: Context) {

    companion object {
        enum class TGSDialogType {
            BASIC, SUCCESS, ERROR
        }
    }

    //-----------------------------------------------------------------
    private val builder: AlertDialog.Builder by lazy {
        TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, view.findViewById<TextView>(R.id.tgs_textview_dialog_alert_title))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<TextView>(R.id.tgs_textview_dialog_alert_message))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<Button>(R.id.tgs_button_alert_dialog_negative))
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, view.findViewById<Button>(R.id.tgs_button_alert_dialog_positive))

        AlertDialog.Builder(context, R.style.TGSDialog).setView(view)
    }

    //-----------------------------------------------------------------
    private val view: View by lazy {
        View.inflate(context, R.layout.tgs_dialog_alert, null)
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
    fun setType(dialogType:TGSDialogType): TGSAlertDialog {
        var imageIcon = view.findViewById<ImageView>(R.id.tgs_image_dialog_alert_icon)
        when(dialogType) {
            TGSDialogType.BASIC -> {
                imageIcon.setBackgroundResource(R.drawable.ic_msg1)
            }
            TGSDialogType.SUCCESS-> {
                imageIcon.setBackgroundResource(R.drawable.ic_ok1)
            }
            TGSDialogType.ERROR-> {
                imageIcon.setBackgroundResource(R.drawable.ic_no1)
            }
        }
        return this
    }

    //-----------------------------------------------------------------
    fun setTitle(@StringRes titleId: Int): TGSAlertDialog {
        var titleView = view.findViewById<TextView>(R.id.tgs_textview_dialog_alert_title)
        titleView.text = context.getText(titleId)
        return this
    }

    //-----------------------------------------------------------------
    fun setTitle(title: CharSequence): TGSAlertDialog {
        var titleView = view.findViewById<TextView>(R.id.tgs_textview_dialog_alert_title)
        titleView.text = title
        return this
    }

    //-----------------------------------------------------------------
    fun setMessage(@StringRes messageId: Int): TGSAlertDialog {
        var messageView = view.findViewById<TextView>(R.id.tgs_textview_dialog_alert_message)
        messageView.text = context.getText(messageId)
        //view.messageTextView.text = context.getText(messageId)
        return this
    }

    //-----------------------------------------------------------------
    fun setMessage(message: CharSequence): TGSAlertDialog {
        var messageView = view.findViewById<TextView>(R.id.tgs_textview_dialog_alert_message)
        messageView.text = message
        //view.messageTextView.text = message
        return this
    }

    //-----------------------------------------------------------------
    fun setPositiveButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): TGSAlertDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_alert_dialog_positive)
        Button.apply {
            text = context.getText(textId)
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    //-----------------------------------------------------------------
    fun setPositiveButton(text: CharSequence, listener: (view: View) -> (Unit)): TGSAlertDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_alert_dialog_positive)
        Button.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    //-----------------------------------------------------------------
    fun setNegativeButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): TGSAlertDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_alert_dialog_negative)
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
    fun setNegativeButton(text: CharSequence, listener: (view: View) -> (Unit)): TGSAlertDialog {
        var Button = view.findViewById<Button>(R.id.tgs_button_alert_dialog_negative)
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
        var negativeButton = view.findViewById<Button>(R.id.tgs_button_alert_dialog_negative)
        if(!negativeButton.hasOnClickListeners()){
            var positiveButton = view.findViewById<Button>(R.id.tgs_button_alert_dialog_positive)
            var layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f /* layout_weight */
            )
            positiveButton.layoutParams = layoutParams
            positiveButton.setBackgroundResource(R.drawable.hround_rect_blue_none)
            negativeButton.visibility = View.GONE
        }

        dialog = builder.create()
        dialog?.show()
    }

    //-----------------------------------------------------------------
    fun dismiss() {
        dialog?.dismiss()
    }
}