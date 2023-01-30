package com.android.tgsmf.fcm.push


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.tgsmf.R
import com.android.tgsmf.activity.TGSSplashActivity
import com.android.tgsmf.database.TGSDatabase
import com.android.tgsmf.database.entity.TGSPushMessageEntity
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.view.dialog.TGSAlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


/**
 * FCM 수신
 * 앱상태 : 포그라운드
 *      - 알림 :onMessageReceived
 *      - 데이터 : onMessageReceived
 *앱상태 : 백그라운드
 *      - 알림 : Notification 값을 전달 할경우 notification 출력 / Notification 값이 없을 경우 onMessageReceived
 *      - 데이터 : 앱 런처 화면 인텐트 부가 정보  / onMessageReceived
 */

/***************************************************************************************
 * 메시지 전송 규약
{
    notification:{
        title:String[알림노티제목],
        body:String[알림노티내용],
        click_action:String[클릭시액션]
    },
    data: {
        clickAction: String [ACTIVITY | DIALOG | ACTION_VIEW],
        clickActionParam: String [실행할 activity 패키지명.클래스명 | Uri],
        showAction: String [alert_ok, alert_okCancel, web_alert_ok, web_alert_okCancel, activity],
        showActionParam: String [], title, message, url, move_url
    }
}
 *
 * case 1) "clickAction": "ACTION_VIEW", "clickActionParam" = "tel:010-0000-0000"
 * case 2) "clickAction": "DIALOG", "clickActionParam" = "", "showAction"="alert_ok", "showActionParam"="{\"title\": \"타이틀명\",\"message\": \"메시지입니다.\"}"
 * case 3) "clickAction": "ACTIVITY", "clickActionParam" = "com.android.tgsmf.activity.TGSWebActivity", "showAction"="activity", "showActionParam"="{\"title\": \"타이틀명\",\"url\": \"https://m.naver.com\"}"
 *
 ***************************************************************************************/
open class TGSFireBaseMessagingService : FirebaseMessagingService() {

    companion object {
        val TAG = "TGSFireBaseMessagingService"

        private var _isUsingPush: Boolean = false
        @JvmStatic fun IS_USING_PUSH() = _isUsingPush

        @JvmStatic
        fun USING_PUSH() {
            _isUsingPush =  true

            TGSFireBaseMessageData.instance()
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    TGSLog.d("[${TAG}_USING_PUSH] getInstanceId faild - ${task.exception}")
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                TGSLog.d("[${TAG}_USING_PUSH] FCM 토근 - ${token}")
            })
        }


        val TGS_NOTIFICATION_CHANNEL_ID = "default_channel"
        val TGS_NOTIFICATION_CHANNEL_NAME = "기본"
        @JvmStatic
        // importance : NotificationManager.IMPORTANCE_HIGH
        fun CREATE_NOTIFICATION_CHANNEL(context: Context, channelId:String, channelName:String, importance:Int ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(NotificationChannel(channelId, channelName, importance))
            }
        }
    }

    //---------------------------------------------------------------
    override fun onNewToken(token: String) {
        TGSLog.d("[${TAG}_onNewToken] Refreshed token - ${token}")
    }

    //---------------------------------------------------------------
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var title = ""
        var message = ""

        var clickAction = ""
        var clickActionParam = ""
        var showAction = ""
        var showActionParam = ""

        if (remoteMessage.notification != null) {
            TGSLog.d("[${TAG}_onMessageReceived] notification : {\"title\":\"${remoteMessage.notification!!.title}\", \"body\":\"${remoteMessage.notification!!.body}\"")
            remoteMessage.notification!!.title?.let { title = it }
            remoteMessage.notification!!.body?.let { message = it }
        }

        if(remoteMessage.data != null && !remoteMessage.data.isEmpty()) {
            TGSLog.d("[${TAG}_onMessageReceived] data : ${remoteMessage.data}")
            if(title.isEmpty()) {
                if(remoteMessage.data.containsKey(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_TITLE))
                    title = remoteMessage.data.get(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_TITLE)!!
            }
            if(message.isEmpty()) {
                if(remoteMessage.data.containsKey(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_MESSAGE))
                    message = remoteMessage.data.get(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_MESSAGE)!!
            }

            if(remoteMessage.data.containsKey(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_CLICK_ACTION))
                clickAction = remoteMessage.data.get(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_CLICK_ACTION)!!
            if(remoteMessage.data.containsKey(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_CLICK_ACTION_PARAM))
                clickActionParam = remoteMessage.data.get(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_CLICK_ACTION_PARAM)!!
            if(remoteMessage.data.containsKey(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_SHOW_ACTION))
                showAction = remoteMessage.data.get(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_SHOW_ACTION)!!
            if(remoteMessage.data.containsKey(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM))
                showActionParam = remoteMessage.data.get(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM)!!
        } else {
            // 임시 정보 입력
            clickAction = "ACTIVITY"
            clickActionParam = "alert_ok"
            showAction = "{     \"title\": \"푸시테스트\",     \"message\": \"메시지 푸시 내용입니다. 클릭 시 메시지 다이얼로그가 나타나야 합니다.\" }"
            showActionParam = "activity.SplashActivity11"
        }

        if(!title.isEmpty() && !message.isEmpty() && !clickAction.isEmpty() && !clickActionParam.isEmpty()) {
            showNotification(title, message, clickAction,clickActionParam,  showAction, showActionParam)
        }

        if(!title.isEmpty() && !message.isEmpty())
            saveMessageInDB(title, message)
    }

//    //---------------------------------------------------------------
//    open fun getNotificationIntent(clickAction:String, clickActionParam:String, showAction:String= "", showActionParam:String = ""):Intent? {
//        var intent:Intent? = null
//        when(clickAction) {
//            TGS_FCM_NOTI_CLICK_ACTTION_TYPE.VIEW.typename ->{
//                intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickActionParam))
//            }
//            TGS_FCM_NOTI_CLICK_ACTTION_TYPE.ACTIVITY.typename ->{
//                intent = Intent()
//                //intent.setClassName(applicationContext, "${clickActionParam}")
//                intent.setClassName(this, "com.android.mobilefw.activity.MainActivity")
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//                if(!showAction.isEmpty())
//                    intent.putExtra(TGS_FCM_NOTI_DATA_SHOW_ACTION, showAction)
//                if(!showActionParam.isEmpty())
//                    intent.putExtra(TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM, showActionParam)
//            }
//        }
//        return intent
//    }

    //---------------------------------------------------------------
    open fun getNotificationIntent(clickAction:String, clickActionParam:String, showAction:String, showActionParam:String):Intent {
        val intent = Intent()
        intent.setAction("FCM_ACTIVITY")

        intent.putExtra(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_CLICK_ACTION, clickAction)
        intent.putExtra(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_CLICK_ACTION_PARAM, clickActionParam)
        intent.putExtra(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_SHOW_ACTION, showAction)
        intent.putExtra(TGSFireBaseMessageData.TGS_FCM_NOTI_DATA_SHOW_ACTION_PARAM, showActionParam)

        return intent
    }

    open fun getNotificationIconRes():Int  = R.mipmap.ic_launcher

    //---------------------------------------------------------------
    open fun showNotification(title:String, message:String, clickAction:String, clickActionParam:String, showAction:String, showActionParam:String) {

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, TGS_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(getNotificationIconRes())
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(message))

        val intent = getNotificationIntent(clickAction, clickActionParam, showAction, showActionParam)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)

        CREATE_NOTIFICATION_CHANNEL(applicationContext, TGS_NOTIFICATION_CHANNEL_ID, TGS_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    //---------------------------------------------------------------
    open fun saveMessageInDB(title:String, message:String) {
        CoroutineScope(Dispatchers.IO).launch {
            TGSDatabase.getInstance(applicationContext)?.let { database ->

                var dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
                var strNow =dateFormat.format(Calendar.getInstance().time)

                database.pushMessageDao().insert(
                    TGSPushMessageEntity(0,
                        title,
                        message,
                        strNow,
                    0,
                    ""))
            }
        }
    }
}

