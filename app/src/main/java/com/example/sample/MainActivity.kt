package com.example.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.android.tgsmf.activity.TGSNaviWebActivity
import com.android.tgsmf.activity.TGSWebActivity
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSMenu
import com.android.tgsmf.data.TGSTabMenu
import com.android.tgsmf.data.TGSWebCommandType
import com.android.tgsmf.network.restapi.TGSRestInterface
import com.android.tgsmf.util.TGSLog
import java.net.URLEncoder

class MainActivity : TGSNaviWebActivity() {

    override fun initBottomNavInfo() {
        super.initBottomNavInfo()
        TGSRestInterface.TGS_URL_MAIN_NAV_HOME = "${TGSRestInterface.TGS_REST_BASE_URL}/mainView"

        //super.initBottomNavInfo()
        setBottomNavMenu(R.menu.activity_navi_home_bottom)
        mArrayBottomNavInfo = arrayListOf(
            TGSTabMenu(
                R.id.main_bottom_navi_home,
                "com.android.tgsmf.fragment.TGSWebHomeFragment",
                hashMapOf(
                    TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_HOME,
                    TGSArgument.TITLE_NAME to "홈"
                )
            ),
            TGSTabMenu(
                R.id.main_bottom_navi_setting,
                "com.android.tgsmf.fragment.TGSSettingFragment",
                hashMapOf(TGSArgument.TITLE_NAME to "설정", TGSArgument.TITLE_TYPE to "2")
            )
        )
        mBottomNavStartTabId = R.id.main_bottom_navi_home
    }

    override fun initSideMenuInfo() {
        super.initSideMenuInfo()

        val menuList: MutableList<TGSMenu> =  arrayListOf()
        var listMenu = arrayListOf<TGSMenu>(
            TGSMenu(0, "홈2페이지", "TGSFW_COMD://webClose?title=아이디노&url=${URLEncoder.encode("https://www.idino.co.kr/www/aboutus#", "UTF-8")}", null)
        )
        menuList.add(TGSMenu(0, "아이디노", null, listMenu))
        mSideMenuView?.let {
            it.setMenuInfo(menuList)
        }
    }

    override fun onWebCommand(command: Int, mapParams: HashMap<String, String>?) {
        super.onWebCommand(command, mapParams)
        TGSLog.i("55555555555555555555555555555555555555")
        when(command) {
            TGSWebCommandType.WCT_MOVE_WEB_CLOSE.ordinal->{
                println("333333333333333333333333333333333333333333");
              /*  mBottomTabManager?.let { it.switchTab(com.android.tgsmf.R.id.tgs_bottom_navi_home)}
                binding.tgsBottomnaviNaviHome.menu.findItem(com.android.tgsmf.R.id.tgs_bottom_navi_home)?.isChecked = true
*/

                /*var intent = Intent(activity, TGSWebActivity::class.java)
                if(mapParams != null) {
                    mapParams.get(TGSArgument.INIT_URL)?.let {intent.putExtra(TGSArgument.INIT_URL, it)}
                    mapParams.get(TGSArgument.TITLE_NAME)?.let {intent.putExtra(TGSArgument.TITLE_NAME, it)}
                }
                intent.putExtra(TGSArgument.TITLE_TYPE, "10")
                activity.startActivity(intent)
                return true*/
            }


//            TGSWebCommandType.WCT_MOVE_QR_CHECK.ordinal->{
//                TGSBaseQRScanActivity.Builder(TGSQRScanActivity::class.java, mQRCodeResultLauncher)
//            }
        }
    }
}