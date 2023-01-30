package com.android.tgsmf.activity

import com.android.tgsmf.R
import com.android.tgsmf.activity.base.TGSBaseQRScanActivity
import com.android.tgsmf.databinding.TgsActivityQrScanBinding
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.*

open class TGSQRScanActivity : TGSBaseQRScanActivity<TgsActivityQrScanBinding>(TgsActivityQrScanBinding::inflate){
    override var TAG = javaClass.simpleName

    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()

        mTextviewTitle = binding.layoutTitleBar.tgsTextviewSubTitle
        mButtonTitleBack =binding.layoutTitleBar.tgsButtonSubBack

        mBarcodeScannerView = binding.tgsZxingQrScanScanner
        mViewfinderView = binding.tgsZxingQrScanScanner.viewFinder

        setCustomTitle(getString(R.string.qr_scan_title))
    }

    //-----------------------------------------------------------------
    override fun initFont() {
        super.initFont()
        binding.tgsTextviewQrScanMsg01?.let {  TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, it) }
        binding.tgsTextviewQrScanMsg02?.let {  TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, it) }
    }

}

