package com.android.tgsmf.fragment

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.View
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSConst
import com.android.tgsmf.fcm.push.TGSFireBaseMessagingService
import com.android.tgsmf.util.TGSPreference
import com.android.tgsmf.view.dialog.TGSAlertDialog


/*********************************************************************
 *
 *
 *********************************************************************/
open class TGSBaseSettingFragment <VB: ViewBinding>(private val _inflate: Inflate<VB>): TGSBaseFragment<VB>(_inflate) {

    protected var mLayoutPush: LinearLayout? = null
    protected var mLayoutSaveAccount: LinearLayout? = null



    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()
        TGSPreference.getInstance(requireContext(), TGSConst.TGS_PREFER_NAME)
    }

    //-----------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        updateSendPushUI()
        updateSaveLoginAccountUI()
    }

    //-----------------------------------------------------------------
    protected fun getSendPushIsOn():Boolean = TGSPreference.getPrefer(TGSConst.TGS_PREFER_KEY_IS_PUSH_SEND)?.let {  it as Boolean } ?: true
    protected fun getSendPushDenyDate():String = TGSPreference.getPrefer(TGSConst.TGS_PREFER_KEY_PUSH_DENY_DATE)?.let {  it as String } ?: ""
    protected fun setSendPushInfo(isOn: Boolean) {
        TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_IS_PUSH_SEND, isOn)
        if(!isOn) {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var strNow =dateFormat.format(Calendar.getInstance().time)
            TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_PUSH_DENY_DATE, strNow)
        }
        updateSendPushUI()
    }
    open protected fun updateSendPushUI() {
        mLayoutPush?.let {
            it.visibility = if(TGSFireBaseMessagingService.IS_USING_PUSH()) View.VISIBLE else View.GONE
        }
    }

    //-----------------------------------------------------------------
    protected fun getSaveLoginAccountIsOn():Boolean = TGSPreference.getPrefer(TGSConst.TGS_PREFER_KEY_SAVE_LOGIN_ACCOUNT)?.let {  it as Boolean } ?: false
    protected fun setSaveLoginAccountInfo(isOn: Boolean) {
        TGSPreference.setPrefer(TGSConst.TGS_PREFER_KEY_SAVE_LOGIN_ACCOUNT, isOn)
        updateSaveLoginAccountUI()
    }
    open protected fun updateSaveLoginAccountUI() { }

    //-----------------------------------------------------------------
    open protected fun onShowLogoutDialog() {
        TGSAlertDialog(requireContext())
            .setType(TGSAlertDialog.Companion.TGSDialogType.BASIC)
            .setTitle(getString(R.string.setting_fragment_logout))
            .setMessage(getString(R.string.setting_fragment_logout_msg))
            .setPositiveButton(getString(R.string.dialog_button_yes)) {
                onMsgToActivity(OnFragmentMsgListener.TYPE.MOVE_LOGIN.value)
            }
            .setNegativeButton(getString(R.string.dialog_button_no)) {
            }
            .show()
    }
}