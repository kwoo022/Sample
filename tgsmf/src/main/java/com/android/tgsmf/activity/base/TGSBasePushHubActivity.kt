package com.android.tgsmf.activity.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.tgsmf.application.TGSApp
import com.android.tgsmf.database.TGSDatabase
import com.android.tgsmf.database.entity.TGSPushMessageEntity
import com.android.tgsmf.databinding.TgsActivityPushHubBinding
import com.android.tgsmf.fcm.push.TGSFireBaseMessageData
import com.android.tgsmf.util.TGSLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class TGSBasePushHubActivity : AppCompatActivity() {
    protected open var TAG = javaClass.simpleName

    private var _binding: TgsActivityPushHubBinding? = null
    val binding get() = _binding!!


    //-----------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = TgsActivityPushHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent != null)
            onPushMessage(intent)
    }

    //-----------------------------------------------------------------
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent != null)
            onPushMessage(intent)
    }

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
    //-----------------------------------------------------------------
    private fun onPushMessage(intent:Intent) {

        TGSLog.d("[${TAG}_onPushMessage] onPushMessage Intent : ${intent.extras.toString()}")
        TGSFireBaseMessageData.instance().setPushMessageData(intent)
        when(TGSFireBaseMessageData.CLICK_ACTION) {
            "ACTION_VIEW" -> { onActionView(TGSFireBaseMessageData.CLICK_ACTION_PARAM)}
            "DIALOG" -> { onDialogView()}
            "ACTIVITY" -> { onActivityView() }
        }
    }

    //-----------------------------------------------------------------
    open fun onActionView(_uri:String) {
        TGSFireBaseMessageData.instance().setPushMessageData(Intent())

        val intent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(_uri))
        startActivity(intent)
        finish()
    }

    //-----------------------------------------------------------------
    open fun onActivityView() {
        // 현재 메인 Activity가 실행 중일 경우 바로 메인 Activity에 전달
        if((application as TGSApp).IsRunningMain != null) {
            var intent = Intent(this, (application as TGSApp).RunningMain!!::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName ?: "")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(launchIntent)
            }
        }
        finish()
    }

    //-----------------------------------------------------------------
    open fun onDialogView() {
        TGSLog.d("[${TAG} _onDialogView] IsRunningMain : ${(application as TGSApp).IsRunningMain}")
        if((application as TGSApp).IsRunningMain) {
            TGSFireBaseMessageData.instance().onShowDialog(this, {finish()}, {finish()})
        } else {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName ?: "")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(launchIntent)
            }
            finish()
        }
    }

}