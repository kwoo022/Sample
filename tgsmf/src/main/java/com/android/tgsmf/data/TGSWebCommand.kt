package com.android.tgsmf.data


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.android.tgsmf.R
import com.android.tgsmf.activity.base.TGSBaseActivity
import com.android.tgsmf.activity.TGSPictureLocationUploadActivity
import com.android.tgsmf.activity.TGSQRScanActivity
import com.android.tgsmf.activity.TGSWebActivity
import com.android.tgsmf.activity.base.TGSBaseQRScanActivity
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSToast
import com.android.tgsmf.view.dialog.TGSAlertDialog
import com.android.tgsmf.view.dialog.TGSOTPDialog
import com.android.tgsmf.view.dialog.TGSWebDialog
import com.android.tgsmf.view.webview.TGSWebview
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder


/*********************************************************************
 *
 *
 *********************************************************************/
enum class TGSWebCommandType {
    WCT_MOVE_HOME,
    WCT_MOVE_NOTICE,
    WCT_MOVE_ID_CARD,
    WCT_MOVE_SETTING,
    WCT_MOVE_QR_CHECK,
    WCT_MOVE_WEB_CLOSE,
    WCT_MOVE_WEB_BACK,
    WCT_SHOW_DIALOG_MSG,
    WCT_SHOW_DIALOG_WEB,
    WCT_SHOW_DIALOG_OTP,
    WCT_SHOW_DIALOG_PICTURE_UPLOAD,
    WCT_SHOW_TOAST,
}
//data class TGSCommInfo(val type:Int, val strCommand:String)

/*********************************************************************
 *
 *
 *********************************************************************/
object TGSWebCommand {
    private val TAG = "TGSWebCommand"

    private var rootCommand = "TGSFW_COMD://"
    private val webCommand : HashMap<Int, String>
    //private val webCommand : Set<TGSCommInfo>

    init {
        webCommand = hashMapOf(
            Pair(TGSWebCommandType.WCT_MOVE_HOME.ordinal, "${rootCommand}moveHome"),
            Pair(TGSWebCommandType.WCT_MOVE_NOTICE.ordinal, "${rootCommand}moveNotice"),
            Pair(TGSWebCommandType.WCT_MOVE_ID_CARD.ordinal, "${rootCommand}moveIdCard"),
            Pair(TGSWebCommandType.WCT_MOVE_SETTING.ordinal, "${rootCommand}moveSetting"),
            Pair(TGSWebCommandType.WCT_MOVE_QR_CHECK.ordinal, "${rootCommand}moveQRCheck"),

            Pair(TGSWebCommandType.WCT_MOVE_WEB_CLOSE.ordinal, "${rootCommand}webClose"),       //url
            Pair(TGSWebCommandType.WCT_MOVE_WEB_BACK.ordinal, "${rootCommand}webBack"),         //url

            Pair(TGSWebCommandType.WCT_SHOW_DIALOG_MSG.ordinal, "${rootCommand}dialog_msg"),
            Pair(TGSWebCommandType.WCT_SHOW_DIALOG_WEB.ordinal, "${rootCommand}dialog_web"),
            Pair(TGSWebCommandType.WCT_SHOW_DIALOG_OTP.ordinal, "${rootCommand}dialog_otp"),
            Pair(TGSWebCommandType.WCT_SHOW_DIALOG_PICTURE_UPLOAD.ordinal, "${rootCommand}dialog_picture_upload"),

            Pair(TGSWebCommandType.WCT_SHOW_TOAST.ordinal, "${rootCommand}show_toast"),     // message
        )
    }

    //-----------------------------------------------------------------
    fun addCommand(_type:Int, _command:String) {
        if(webCommand.contains(_type))
            return

        webCommand.put(_type, _command)
    }

    //-----------------------------------------------------------------
    fun convertWebCommand(strUrl:String):Int? {
        var command = strUrl
        val separated: List<String> = strUrl.split("?")
        if(separated.size > 1)
            command = separated[0]

        for(info in webCommand) {
            if(info.value.equals(command, true)) {
                return info.key
            }
        }
        return null
    }

    //-----------------------------------------------------------------
    // TGSFW_COMD://command?key=test&page=1
    // 커스텀커맨드에 전달된 인자를 Hashmap 형태로 리턴한다.
    fun convertWebCommandUrlParam(strUrl:String):HashMap<String, String> {
        var mapParams = hashMapOf<String, String>()
        val separated: List<String> = strUrl.split("?")
        if (separated.size > 1) {
            val query = separated[1]
            val params = query.split("&").toTypedArray()
            for (param in params) {
                val keyvalue = param.split("=").toTypedArray()
                val key: String = URLDecoder.decode(keyvalue[0])
                var value: String? = null
                if (keyvalue.size > 1) {
                    value = URLDecoder.decode(keyvalue[1])
                }
                if(value != null)
                    mapParams.put(key, value)
            }
        }
        return mapParams
    }

    //-----------------------------------------------------------------
    fun onBasicCommand(activity: TGSBaseActivity<*>, webview: TGSWebview?, command:Int, mapParams:HashMap<String,String>?=null):Boolean {
        when(command) {
            TGSWebCommandType.WCT_SHOW_DIALOG_MSG.ordinal->{
                var msgType = TGSAlertDialog.Companion.TGSDialogType.BASIC
                var strTitle = ""
                var strMessage = ""
                if(mapParams != null) {
                    mapParams.get("type")?.let {
                        if(it.equals("error")) { msgType = TGSAlertDialog.Companion.TGSDialogType.ERROR }
                        else if(it.equals("success")) { msgType = TGSAlertDialog.Companion.TGSDialogType.SUCCESS }
                    }
                    mapParams.get("title")?.let {strTitle = it}
                    mapParams.get("message")?.let {strMessage = it}
                }
                TGSAlertDialog(activity)
                    .setType(msgType)
                    .setTitle(strTitle)
                    .setMessage(strMessage)
                    .setPositiveButton(activity.getString(R.string.dialog_button_yes)) {
                    }
                    .show()
                return true
            }
            TGSWebCommandType.WCT_SHOW_DIALOG_WEB.ordinal->{
                var url = mapParams?.get("url")?.let {it} ?: kotlin.run { "" }
                TGSWebDialog(activity)
                    .setUrl(url)
                    .setPositiveButton(activity.getString(R.string.dialog_button_ok)) {
                    }
                    .show()
                return true
            }
            TGSWebCommandType.WCT_SHOW_DIALOG_OTP.ordinal->{
                var limitTime:Int? = null
                if(mapParams != null) {
                    mapParams.get("limit_time")?.let {
                        limitTime = it.toInt()
                    }
                }

                var dialog = TGSOTPDialog(activity)
                    .setOtpResultListener(object :TGSOTPDialog.OnResultListener{
                        override fun onSuccess(otp: String) {
                            webview?.let { webview.loadUrl("javascript:requestAttend(${otp})") }
                        }
                        override fun onCancel() {
                        }
                    })
                limitTime?.let { dialog.setLimitTime(it) }
                dialog.show()
                return true
            }
            TGSWebCommandType.WCT_SHOW_DIALOG_PICTURE_UPLOAD.ordinal->{
                var intent = Intent(activity, TGSPictureLocationUploadActivity::class.java)
                activity.onActivityResult(intent, object : TGSBaseActivity.ActivityResultLisener{
                    override fun onResult(result: ActivityResult) {
                        if(result.resultCode == RESULT_OK) {
                            result.data?.let {
                                val strResult: String? = it.getStringExtra("test")
                                if(strResult != null)
                                    TGSLog.d("[${TAG} _TGSWebCommandType.WCT_SHOW_DIALOG_PICTURE_UPLOAD] onResult :  ${strResult}")
                            }
                            webview?.let { webview.loadUrl("javascript:requestAttend()") }
                        } else if(result.resultCode == RESULT_CANCELED) {
                        }
                    }
                })
                return true
            }
            TGSWebCommandType.WCT_MOVE_QR_CHECK.ordinal->{
                var intent = Intent(activity, TGSQRScanActivity::class.java)
                activity.onActivityResult(intent, object : TGSBaseActivity.ActivityResultLisener{
                    override fun onResult(result: ActivityResult) {
                        val scanResult = ScanIntentResult.parseActivityResult(result.resultCode, result.data)
                        if (scanResult.contents == null) {
                            val originalIntent = scanResult.originalIntent
                            if (originalIntent == null) {
                                TGSLog.d("[${TAG}_TGSWebCommandType.WCT_MOVE_QR_CHECK] onResult : QR 코드 스캔 취소")
                                activity.onQRScanResult(false, "QR 코드 스캔 취소")
                            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                                TGSLog.d("[${TAG}_TGSWebCommandType.WCT_MOVE_QR_CHECK] onResult : 카메라 사용 권한 없음")
                                activity.onQRScanResult(false, "카메라 사용 권한 없음")
                            }
                        } else {
                            TGSLog.d("[${TAG}_TGSWebCommandType.WCT_MOVE_QR_CHECK] onResult : QR 코드 - ${ scanResult.contents}")
                            activity.onQRScanResult(true, "성공", scanResult.contents)
                            //TGSToast.show(activity, "QR 코드 : ${ scanResult.contents}")
                        }
                    }
                })
                return true
            }
            TGSWebCommandType.WCT_MOVE_WEB_CLOSE.ordinal->{
                var intent = Intent(activity, TGSWebActivity::class.java)
                if(mapParams != null) {
                    mapParams.get(TGSArgument.INIT_URL)?.let {intent.putExtra(TGSArgument.INIT_URL, it)}
                    mapParams.get(TGSArgument.TITLE_NAME)?.let {intent.putExtra(TGSArgument.TITLE_NAME, it)}
                }
                intent.putExtra(TGSArgument.TITLE_TYPE, "10")
                activity.startActivity(intent)
                return true
            }
            TGSWebCommandType.WCT_MOVE_WEB_BACK.ordinal->{
                var intent = Intent(activity, TGSWebActivity::class.java)
                if(mapParams != null) {
                    mapParams.get(TGSArgument.INIT_URL)?.let {intent.putExtra(TGSArgument.INIT_URL, it)}
                    mapParams.get(TGSArgument.TITLE_NAME)?.let {intent.putExtra(TGSArgument.TITLE_NAME, it)}
                }
                intent.putExtra(TGSArgument.TITLE_TYPE, "6")
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                return true
            }
            TGSWebCommandType.WCT_SHOW_TOAST.ordinal-> {
                var message = mapParams?.get("message")?.let {it} ?: kotlin.run { "" }
                TGSToast.show(activity, message)
            }
        }
        return false
    }
}

