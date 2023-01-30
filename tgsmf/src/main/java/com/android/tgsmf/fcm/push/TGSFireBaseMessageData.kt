package com.android.tgsmf.fcm.push

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.android.tgsmf.R
import com.android.tgsmf.network.restapi.TGSRestApi
import com.android.tgsmf.view.dialog.TGSAlertDialog
import com.android.tgsmf.view.dialog.TGSWebDialog
import org.json.JSONException
import org.json.JSONObject

/***************************************************************************************
 *
 *
 *
 **************************************************************************************/
enum class TGS_PUSH_CLICK_ACTTION_TYPE(val typename:String) {ACTIVITY("ACTIVITY"), DIALOG("DIALOG"), VIEW("ACTION_VIEW")}
enum class TGS_PUSH_SHOW_ACTTION_TYPE(val typename:String) {ALERT_OK("alert_ok"), ALERT_OKCANCEL("alert_okCancel"), WEB_ALERT_OK("web_alert_ok"), WEB_ALERT_OKCANCEL("web_alert_okCancel")}
enum class TGS_PUSH_SHOW_ACTTION_PARAM_TYPE(val typename:String) {TITLE("title"), MESSAGE("message"), URL("url")}

class TGSFireBaseMessageData {

    /**
    clickAction: String [ACTIVITY | DIALOG | ACTION_VIEW],
    clickActionParam: String [실행할 activity 패키지명.클래스명 | Uri],
    showAction: String [alert_ok, alert_okCancel, web_alert_ok, web_alert_okCancel, activity],
    showActionParam: String [], title, message, url, move_url
     *
     * case 1) "clickAction": "ACTION_VIEW", "clickActionParam" = "tel:010-0000-0000"
     * case 2) "clickAction": "DIALOG", "clickActionParam" = "activity.TGSSplashActivity", "showAction"="alert_ok", "showActionParam"="{\"title\": \"타이틀명\",\"message\": \"메시지입니다.\"}"
     * case 3) "clickAction": "ACTIVITY", "clickActionParam" = "activity.TGSSplashActivity", "showAction"="web_alert_ok", "showActionParam"="{\"title\": \"타이틀명\",\"url\": \"https://m.naver.com\"}"
     *
     */
    private var tgsIsPushData:Boolean = false
    private var tgsPushClickAction = ""
    private var tgsPushClickActionParam = ""
    private var tgsPushShowAction = ""
    private var tgsPushShowActionParam = ""

    //-----------------------------------------------------------------
    companion object {
        // 앱이 백그라운드 상태일때 내부적으로 notification을 띄우기 때문에 DB에 저장하기위한 메시지를 전달
        val TGS_FCM_NOTI_DATA_TITLE = "title"
        val TGS_FCM_NOTI_DATA_MESSAGE = "message"

        val TGS_FCM_NOTI_DATA_CLICK_ACTION = "clickAction"
        val TGS_FCM_NOTI_DATA_CLICK_ACTION_PARAM = "clickActionParam"
        val TGS_FCM_NOTI_DATA_SHOW_ACTION = "showAction"
        val TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM = "showActionParam"

        @Volatile private var _instance: TGSFireBaseMessageData? = null
        @JvmStatic
        fun instance(): TGSFireBaseMessageData =
            _instance?: synchronized(this) {
                _instance ?: TGSFireBaseMessageData().also {
                    _instance = it
                }
            }

        val IS_PUSHDATA get() = instance().tgsIsPushData

        val CLICK_ACTION get() = instance().tgsPushClickAction
        val CLICK_ACTION_PARAM get() = instance().tgsPushClickActionParam
        val SHOW_ACTION get() = instance().tgsPushShowAction
        val SHOW_ACTION_PARAM get() = instance().tgsPushShowActionParam
    }

    //-----------------------------------------------------------------
    fun setPushMessageData(intent:Intent) {
        tgsPushClickAction = if(intent.hasExtra(TGS_FCM_NOTI_DATA_CLICK_ACTION)) intent.getStringExtra(TGS_FCM_NOTI_DATA_CLICK_ACTION)!! else ""
        tgsPushClickActionParam = if(intent.hasExtra(TGS_FCM_NOTI_DATA_CLICK_ACTION)) intent.getStringExtra(TGS_FCM_NOTI_DATA_CLICK_ACTION_PARAM)!! else ""
        tgsPushShowAction = if(intent.hasExtra(TGS_FCM_NOTI_DATA_SHOW_ACTION)) intent.getStringExtra(TGS_FCM_NOTI_DATA_SHOW_ACTION)!! else ""
        tgsPushShowActionParam = if(intent.hasExtra(TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM)) intent.getStringExtra(TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM)!! else ""

        if(tgsPushClickAction.isEmpty() && tgsPushClickActionParam.isEmpty() && tgsPushShowAction.isEmpty() && tgsPushShowActionParam.isEmpty() )
            tgsIsPushData = false
        else
            tgsIsPushData = true
    }
    //-----------------------------------------------------------------
    fun clearPushMessageData() {
        tgsIsPushData = false
        tgsPushClickAction = ""
        tgsPushClickActionParam = ""
        tgsPushShowAction = ""
        tgsPushShowActionParam = ""
    }

    //-----------------------------------------------------------------
    fun parseShowActionParam(key:String):String? {
        try {
            val jsonObject = JSONObject(tgsPushShowActionParam)
            if(jsonObject.has(key))
                return jsonObject.getString(key)
        } catch (e : JSONException) {
        }
        return null
    }

    //-----------------------------------------------------------------
    open fun onShowDialog(_activity:Activity, positiveListener: (() -> Unit)? = null, negativeListener:(()->Unit)? = null) {
        if(!tgsPushClickAction.equals(TGS_PUSH_CLICK_ACTTION_TYPE.DIALOG.typename, true))
            return

        when(tgsPushShowAction) {
            TGS_PUSH_SHOW_ACTTION_TYPE.ALERT_OK.typename -> {
                val strTitle = parseShowActionParam(TGS_PUSH_SHOW_ACTTION_PARAM_TYPE.TITLE.typename) ?: ""
                val strMessage = parseShowActionParam(TGS_PUSH_SHOW_ACTTION_PARAM_TYPE.MESSAGE.typename) ?: ""
                TGSAlertDialog(_activity)
                    .setType(TGSAlertDialog.Companion.TGSDialogType.BASIC)
                    .setTitle(strTitle)
                    .setMessage(strMessage)
                    .setPositiveButton(_activity.getString(R.string.dialog_button_yes)) {
                        positiveListener?.let { _listener -> _listener() }
                    }
                    .show()
            }
            TGS_PUSH_SHOW_ACTTION_TYPE.ALERT_OKCANCEL.typename-> {
                val strTitle = parseShowActionParam(TGS_PUSH_SHOW_ACTTION_PARAM_TYPE.TITLE.typename) ?: ""
                val strMessage = parseShowActionParam(TGS_PUSH_SHOW_ACTTION_PARAM_TYPE.MESSAGE.typename) ?: ""
                TGSAlertDialog(_activity)
                    .setType(TGSAlertDialog.Companion.TGSDialogType.BASIC)
                    .setTitle(strTitle!!)
                    .setMessage(strMessage!!)
                    .setPositiveButton(_activity.getString(R.string.dialog_button_yes)) {
                        positiveListener?.let { _listener -> _listener() }
                    }
                    .setNegativeButton(_activity.getString(R.string.dialog_button_no)) {
                        negativeListener?.let { _listener -> _listener() }
                    }
                    .show()
            }

            TGS_PUSH_SHOW_ACTTION_TYPE.WEB_ALERT_OK.typename->{
                val strUrl = parseShowActionParam(TGS_PUSH_SHOW_ACTTION_PARAM_TYPE.URL.typename) ?: ""
                TGSWebDialog(_activity)
                    .setUrl(strUrl!!)
                    .setPositiveButton(_activity.getString(R.string.dialog_button_yes)) {
                        positiveListener?.let { _listener -> _listener() }
                    }
                    .show()
            }
            TGS_PUSH_SHOW_ACTTION_TYPE.WEB_ALERT_OKCANCEL.typename->{
                val strUrl = parseShowActionParam(TGS_PUSH_SHOW_ACTTION_PARAM_TYPE.URL.typename) ?: ""
                TGSWebDialog(_activity)
                    .setUrl(strUrl!!)
                    .setPositiveButton(_activity.getString(R.string.dialog_button_yes)) {
                        positiveListener?.let { _listener -> _listener() }
                    }
                    .setNegativeButton(_activity.getString(R.string.dialog_button_no)) {
                        negativeListener?.let { _listener -> _listener() }
                    }
                    .show()
            }
        }

        clearPushMessageData()
    }

    //-----------------------------------------------------------------
    open fun onShowActivity(_activity:Activity) {
        if(!tgsPushClickAction.equals(TGS_PUSH_CLICK_ACTTION_TYPE.ACTIVITY.typename, true))
            return

        // 액티비티 이름으로 실행함
        //인텐트에 값을 넣어줌

    }

}