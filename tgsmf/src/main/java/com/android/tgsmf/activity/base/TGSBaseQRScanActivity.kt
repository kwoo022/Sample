package com.android.tgsmf.activity.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import androidx.activity.result.ActivityResultLauncher
import androidx.viewbinding.ViewBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.*


abstract class TGSBaseQRScanActivity<VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) ,
    DecoratedBarcodeView.TorchListener {


    companion object {
        @JvmStatic
        fun <A, T>Builder(_activity:Class<A>, _launcher:ActivityResultLauncher<T>) {
            //val options = ScanOptions().setOrientationLocked(false).setCaptureActivity(TGSQRScanActivity::class.java)
            val options = ScanOptions().setOrientationLocked(false).setCaptureActivity(_activity).setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            _launcher.launch(options as T)
        }
    }

    protected lateinit  var mCaptureMgr: CaptureManager
    protected var mBarcodeScannerView: DecoratedBarcodeView? = null
    protected var mViewfinderView: ViewfinderView? = null

    //-----------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 세로 고정한다.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }


    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        if(mBarcodeScannerView != null) {
            mCaptureMgr = CaptureManager(this, mBarcodeScannerView)
            //mCaptureMgr.initializeFromIntent(intent, savedInstanceState)
            mCaptureMgr.setShowMissingCameraPermissionDialog(false)
            mCaptureMgr.decode()

            mBarcodeScannerView!!.barcodeView.decoderFactory = DefaultDecoderFactory(arrayListOf(BarcodeFormat.QR_CODE))
            mBarcodeScannerView!!.barcodeView.cameraSettings.isAutoFocusEnabled = true
        }
        mViewfinderView!!.setLaserVisibility(true)

    }

    //-----------------------------------------------------------------
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mCaptureMgr.onSaveInstanceState(outState)
    }
    //-----------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        mCaptureMgr.onResume()
    }
    //-----------------------------------------------------------------
    override fun onPause() {
        super.onPause()
        mCaptureMgr.onPause()
    }
    //-----------------------------------------------------------------
    override fun onDestroy() {
        super.onDestroy()
        mCaptureMgr.onDestroy()
    }

    //-----------------------------------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mCaptureMgr.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //-----------------------------------------------------------------
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return mBarcodeScannerView?.let {  it.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)}
            ?: run {super.onKeyDown(keyCode, event)}
    }

    //-----------------------------------------------------------------
    override fun onTorchOn() {
    }

    //-----------------------------------------------------------------
    override fun onTorchOff() {
    }
}
