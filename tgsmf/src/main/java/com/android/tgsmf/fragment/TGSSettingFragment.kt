package com.android.tgsmf.fragment

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSConst
import com.android.tgsmf.databinding.TgsFragmentSettingBinding
import com.android.tgsmf.fcm.push.TGSFireBaseMessagingService
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSPreference
import com.android.tgsmf.view.dialog.TGSAlertDialog
import com.android.tgsmf.view.dialog.TGSWebDialog
import com.android.tgsmf.view.toggleswitch.TGSToggleSwitch
import kotlinx.coroutines.flow.combineTransform


/*********************************************************************
 *
 *
 *********************************************************************/
class TGSSettingFragment : TGSBaseSettingFragment<TgsFragmentSettingBinding>(TgsFragmentSettingBinding::inflate) {

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()

        mLayoutTitlebar = binding.layoutTitleBar.tgsLayoutTitleBar
        mTextviewTitle = binding.layoutTitleBar.tgsTextviewSubTitle
        mButtonTitleBack = binding.layoutTitleBar.tgsButtonSubBack
        mButtonTitleClose = binding.layoutTitleBar.tgsButtonSubClose

        mLayoutPush = binding.tgsLayoutSettingPush
        mLayoutSaveAccount = binding.tgsLayoutSettingSaveAccrount


        binding.tgsSwitchSettingPush.mToggledListener = object : TGSToggleSwitch.OnToggledListener {
            override fun onSwitched(toggleableView: TGSToggleSwitch, isOn: Boolean) {
                setSendPushInfo(isOn)
            }
        }
        binding.tgsSwitchSettingSaveAccount.mToggledListener = object : TGSToggleSwitch.OnToggledListener {
            override fun onSwitched(toggleableView: TGSToggleSwitch, isOn: Boolean) {
                setSaveLoginAccountInfo(isOn)
            }
        }
        binding.tgsButtonSettingLogout.setOnClickListener {
            onShowLogoutDialog()
        }
    }

    //-----------------------------------------------------------------
    override fun initFont() {
        mTextviewTitle?.let {  TGSFont.setFont(FONT_TYPE.NOTO_BOLD, it) }
        TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, binding.tgsTextviewSettingPushTitle)
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, binding.tgsTextviewSettingPushTime)
        TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, binding.tgsTextviewSettingSaveAccrountTitle)
        TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, binding.tgsButtonSettingLogout)
    }

    //-----------------------------------------------------------------
    override fun updateSendPushUI() {
        super.updateSendPushUI()

        var isSendPush = getSendPushIsOn()
        binding.tgsSwitchSettingPush.setOn(isSendPush)
        if(isSendPush) {
            binding.tgsTextviewSettingPushTime.visibility = View.GONE
        } else {
            var strPushDenyDate =getSendPushDenyDate()
            if(strPushDenyDate.isEmpty())
                binding.tgsTextviewSettingPushTime.visibility = View.GONE
            else {
                binding.tgsTextviewSettingPushTime.setText(String.format(getString(R.string.setting_fragment_push_time), strPushDenyDate))
                binding.tgsTextviewSettingPushTime.visibility = View.VISIBLE
            }
        }
    }

    //-----------------------------------------------------------------
    override fun updateSaveLoginAccountUI() {
        super.updateSaveLoginAccountUI()
        var isSaveAccrount = getSaveLoginAccountIsOn()
        binding.tgsSwitchSettingSaveAccount.setOn(isSaveAccrount)
    }
}