package com.example.sample

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.*
import com.android.tgsmf.R
import com.android.tgsmf.activity.TGSNaviWebActivity
import com.android.tgsmf.activity.base.TGSBaseNaviWebActivity
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSMenu
import com.android.tgsmf.data.TGSTabMenu
import com.android.tgsmf.databinding.TgsActivityNaviWebHomeBinding
import com.android.tgsmf.fragment.TGSWebHomeFragment
import com.android.tgsmf.network.restapi.TGSRestInterface
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.view.webview.TGSWebViewClient
import com.android.tgsmf.view.webview.TGSWebview
import java.net.URLEncoder

class MainActivity3 : TGSNaviWebActivity() {

    //protected var mWebview : TGSWebHomeFragment? = null

    private lateinit var webView: TGSWebview

    override fun initView() {
        super.initView()

        mDrawerLayout = binding.tgsLayoutNaviHome
        mSideMenuView = binding.tgsLayoutNaviHomeSideMenu

        mBottomNavigationView = binding.tgsBottomnaviNaviHome

        //var test :TGSWebHomeFragment



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

    override fun initBottomNavInfo() {
        super.initBottomNavInfo()
        mArrayBottomNavInfo = arrayListOf (
            TGSTabMenu(
                R.id.tgs_bottom_navi_home, "com.android.tgsmf.fragment.TGSWebHomeFragment"
                , hashMapOf(
                    TGSArgument.INIT_URL to "http://192.168.20.219:8080/", TGSArgument.TITLE_NAME to getString(
                        R.string.home_fragment_title))),
            TGSTabMenu(
                R.id.tgs_bottom_navi_notice, "com.android.tgsmf.fragment.TGSWebFragment"
                , hashMapOf(
                    TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_NOTICE, TGSArgument.TITLE_NAME to getString(
                        R.string.notice_fragment_title), TGSArgument.TITLE_TYPE to "2")),
            TGSTabMenu(
                R.id.tgs_bottom_navi_id_card, "com.android.tgsmf.fragment.TGSWebFragment"
                ,hashMapOf(
                    TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_ID_CARD,  TGSArgument.TITLE_NAME to getString(
                        R.string.id_card_fragment_title), TGSArgument.TITLE_TYPE to "2")),
            TGSTabMenu(
                R.id.tgs_bottom_navi_setting, "com.android.tgsmf.fragment.TGSSettingFragment"
                ,hashMapOf(TGSArgument.TITLE_NAME to getString(R.string.setting_fragment_title), TGSArgument.TITLE_TYPE to "2"))
        )
        mFragmentViewId = R.id.tgs_fragment_navi_home
        mBottomNavStartTabId = R.id.tgs_bottom_navi_home
    }

    override fun onWebCommand(command: Int, mapParams: HashMap<String, String>?) {
        super.onWebCommand(command, mapParams)
        println("onWebCommand:::::::::::::::::::::::::::")
    }

    override fun showLoading(){
        super.showLoading()
        println("showLoading::::::::::::::::::::")
    }



}