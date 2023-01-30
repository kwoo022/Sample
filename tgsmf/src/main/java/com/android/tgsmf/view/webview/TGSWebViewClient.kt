package com.android.tgsmf.view.webview

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.webkit.*
import com.android.tgsmf.util.TGSLog

class TGSWebViewClient : WebViewClient() {
    private val TAG = javaClass.simpleName

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        TGSLog.d("[${TAG} _onPageStarted] ${url}")
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        TGSLog.d("[${TAG} _onPageFinished] ${url}")
        super.onPageFinished(view, url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        TGSLog.d("[${TAG} _onLoadResource] ${url}")
        super.onLoadResource(view, url)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        TGSLog.d("[${TAG} _onReceivedError] ${error?.description.toString()}")
        super.onReceivedError(view, request, error)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        TGSLog.d("[${TAG} _shouldInterceptRequest] ${request?.url.toString()}")
        return super.shouldInterceptRequest(view, request)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        TGSLog.d("[${TAG} _shouldOverrideUrlLoading] ${request?.url.toString()}")
        return super.shouldOverrideUrlLoading(view, request)
    }
}