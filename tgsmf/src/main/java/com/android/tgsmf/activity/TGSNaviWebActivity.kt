package com.android.tgsmf.activity


import android.content.Intent
import android.widget.Toast
import com.android.tgsmf.R
import com.android.tgsmf.activity.base.TGSBaseNaviWebActivity
import com.android.tgsmf.data.*
import com.android.tgsmf.databinding.TgsActivityNaviWebHomeBinding
import com.android.tgsmf.fcm.push.TGSFireBaseMessagingService
import com.android.tgsmf.fragment.*
import com.android.tgsmf.network.restapi.*
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*********************************************************************
 *
 *
 *********************************************************************/
open class TGSNaviWebActivity : TGSBaseNaviWebActivity<TgsActivityNaviWebHomeBinding>(TgsActivityNaviWebHomeBinding::inflate){//, TGSBaseFragment.OnFragmentMsgListener {
    override var TAG = javaClass.simpleName

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()

        mDrawerLayout = binding.tgsLayoutNaviHome
        mSideMenuView = binding.tgsLayoutNaviHomeSideMenu

        mBottomNavigationView = binding.tgsBottomnaviNaviHome
    }


    //-----------------------------------------------------------------
    override fun initBottomNavInfo() {

        mArrayBottomNavInfo = arrayListOf (
            TGSTabMenu(R.id.tgs_bottom_navi_home, "com.android.tgsmf.fragment.TGSWebHomeFragment"
                , hashMapOf(TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_HOME, TGSArgument.TITLE_NAME to getString(R.string.home_fragment_title))),
            TGSTabMenu(R.id.tgs_bottom_navi_notice, "com.android.tgsmf.fragment.TGSWebFragment"
                , hashMapOf(TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_NOTICE, TGSArgument.TITLE_NAME to getString(R.string.notice_fragment_title), TGSArgument.TITLE_TYPE to "2")),
            TGSTabMenu(R.id.tgs_bottom_navi_id_card, "com.android.tgsmf.fragment.TGSWebFragment"
                ,hashMapOf(TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_ID_CARD,  TGSArgument.TITLE_NAME to getString(R.string.id_card_fragment_title), TGSArgument.TITLE_TYPE to "2")),
            TGSTabMenu(R.id.tgs_bottom_navi_setting, "com.android.tgsmf.fragment.TGSSettingFragment"
                ,hashMapOf(TGSArgument.TITLE_NAME to getString(R.string.setting_fragment_title), TGSArgument.TITLE_TYPE to "2"))
        )
        mFragmentViewId = R.id.tgs_fragment_navi_home
        mBottomNavStartTabId = R.id.tgs_bottom_navi_home
    }

    //-----------------------------------------------------------------
    override fun initSideMenuInfo() { }


    //-----------------------------------------------------------------
    private var mTimeBack:Long = 0
    override fun onBackPressed() {
        //super.onBackPressed()

        // 1. 사이드 메뉴가 열려 있을 경우 사이드 메뉴를 닫는다.
        if(onBackSideMenu())
            return

        // 2. 프레그먼트 내에서 Back 버튼 처리를 한다.
        if(onBackFragment())
            return

        // 3. 프레그먼트 스택이 쌓여 있을 경우 프레그먼트를 닫는다.
        if(onBackTabHistory())
            return

        if(System.currentTimeMillis() - mTimeBack > 2000) {
            mTimeBack = System.currentTimeMillis()
            Toast.makeText(applicationContext, getString(R.string.msg_app_exit), Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }


    //-----------------------------------------------------------------
    override fun onWebCommand(command: Int, mapParams: HashMap<String, String>?) {
        super.onWebCommand(command, mapParams)
        TGSLog.i("[${TAG} _onCommand] command : ${command}, params : ${mapParams.toString()}")

        when(command) {
            TGSWebCommandType.WCT_MOVE_HOME.ordinal->{
                mBottomTabManager?.let { it.switchTab(R.id.tgs_bottom_navi_home)}
                binding.tgsBottomnaviNaviHome.menu.findItem(R.id.tgs_bottom_navi_home)?.isChecked = true
            }
            TGSWebCommandType.WCT_MOVE_NOTICE.ordinal->{
                mBottomTabManager?.let { it.switchTab(R.id.tgs_bottom_navi_notice)}
                binding.tgsBottomnaviNaviHome.menu.findItem(R.id.tgs_bottom_navi_notice)?.isChecked = true
            }
            TGSWebCommandType.WCT_MOVE_ID_CARD.ordinal->{
                mBottomTabManager?.let { it.switchTab(R.id.tgs_bottom_navi_id_card)}
                binding.tgsBottomnaviNaviHome.menu.findItem(R.id.tgs_bottom_navi_id_card)?.isChecked = true
            }
            TGSWebCommandType.WCT_MOVE_SETTING.ordinal->{
                mBottomTabManager?.let { it.switchTab(R.id.tgs_bottom_navi_setting)}
                binding.tgsBottomnaviNaviHome.menu.findItem(R.id.tgs_bottom_navi_setting)?.isChecked = true
            }

//            TGSWebCommandType.WCT_MOVE_QR_CHECK.ordinal->{
//                TGSBaseQRScanActivity.Builder(TGSQRScanActivity::class.java, mQRCodeResultLauncher)
//            }
        }
    }

    //-----------------------------------------------------------------
    override fun onRequestQRCheckIn(qrCode: String) {

        var serviceCall = TGSRestApi.getApiService(
            this,
            TGSRestInterface.TGS_REST_BASE_URL,
            TGSRestInterface::class.java)
            .qrCheckIn(qrCode)
        TGSRestApi.request(serviceCall, object: TGSRestApiListener<TGSRestModel.TGSRestResult> {
            override fun onResult(code: TGSRestApiResultCode, msg: String, result: TGSRestModel.TGSRestResult?) {
                TGSLog.d("[${TAG} _onRequestLogin] onResult - code : ${code.ordinal}, msg : ${msg}, code : ${result?.let { it.toString()} ?: kotlin.run { "null" }}")
                if(code == TGSRestApiResultCode.success) {
                    result?.let { _result->
                        CoroutineScope(Dispatchers.Main).launch {
                            if (_result.success) {
                                TGSToast.show(this@TGSNaviWebActivity,
                                    _result.result?.let { it } ?: kotlin.run { "success" })
                            } else {
                                TGSToast.show(this@TGSNaviWebActivity,
                                    _result.message?.let { it } ?: kotlin.run { "error" })
                            }
                        }
                    }

                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        TGSToast.show(this@TGSNaviWebActivity, msg)
                    }
                }
            }
        })
    }

    //-----------------------------------------------------------------
    private fun movePushListActivity() {
        if(!TGSFireBaseMessagingService.IS_USING_PUSH()) {
            TGSToast.show(this, getString(R.string.push_service_not_using))
            return
        }
        var intent = Intent(this, TGSPushMessageActivity::class.java)
        startActivity(intent)
    }

    //-----------------------------------------------------------------
    private fun moveLoginActivity() {
        var intent = Intent(this, TGSLoginActivity::class.java)
        startActivity(intent)

        finish()
    }

    //-----------------------------------------------------------------
    // Fragment에서 Activity로 메시지 전달
    // TGSBaseFragment.OnFragmentMsgListener 구현체
    override fun onMessage(_type: Int, _message: Any?) {
        when(_type) {
            TGSBaseFragment.OnFragmentMsgListener.TYPE.SHOW_SIDE_MENU.value -> {openSideMenu()}
            TGSBaseFragment.OnFragmentMsgListener.TYPE.SHOW_PUSH_LIST.value-> {movePushListActivity()}
            TGSBaseFragment.OnFragmentMsgListener.TYPE.BACK_PRESSED.value -> {mBottomTabManager?.let { it.onBackPressed()}}
            TGSBaseFragment.OnFragmentMsgListener.TYPE.MOVE_LOGIN.value -> { moveLoginActivity() }
        }
    }

}