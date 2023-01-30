package com.example.sample

import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.android.tgsmf.activity.TGSNaviWebActivity
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSTabMenu
import com.android.tgsmf.network.restapi.TGSRestInterface

class MainActivity2 : TGSNaviWebActivity(){

    private lateinit var webView: WebView
    private lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(com.android.tgsmf.R.layout.tgs_view_webview)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        mProgressBar = findViewById(R.id.progress_webview)

        webView.apply {
            webViewClient = WebViewClientClass()

            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true

            settings.setSupportZoom(true)  //화면 줌 허용여부
            settings.builtInZoomControls = true // 화면 확대 축소 허용여부

            val url = "https://m.naver.com"
            webView.loadUrl(url)
            //webView.loadUrl("javascript:alert('123123123');")

        }

        //webView.addJavascriptInterface(AndroidBridge(),"Android")

    }


    inner class WebViewClientClass : WebViewClient(){
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            //mProgressBar.visibility=ProgressBar.GONE
            //webView.visibility= View.VISIBLE
            //mWebView.loadUrl("javascript:<함수명>('<arg>')");

            //initBottomNavInfo()
            println("onPageCommitVisibleonPageCommitVisible")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            mProgressBar.visibility=ProgressBar.GONE
            webView.visibility= View.VISIBLE

            initBottomNavInfo()
            if (view != null) {
                view.loadUrl("javascript:alert('fdsafsdf')")
                println("onPageFinishedonPageFinished")
            }

            println(url)
        }
    }

    override fun initBottomNavInfo() {
        super.initBottomNavInfo()

        setBottomNavMenu(R.menu.navi_menu)

        mArrayBottomNavInfo = arrayListOf (
            TGSTabMenu(R.id.main_bottom_navi_home, "com.android.tgsmf.fragment.TGSWebHomeFragment"
                , hashMapOf(TGSArgument.INIT_URL to TGSRestInterface.TGS_URL_MAIN_NAV_HOME, TGSArgument.TITLE_NAME to "홈")),
            TGSTabMenu(R.id.main_bottom_navi_setting, "com.android.tgsmf.fragment.TGSSettingFragment"
                ,hashMapOf(TGSArgument.TITLE_NAME to "설정", TGSArgument.TITLE_TYPE to "2"))
        )
    }
}