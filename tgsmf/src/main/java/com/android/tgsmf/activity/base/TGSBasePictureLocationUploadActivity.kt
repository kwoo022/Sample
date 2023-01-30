package com.android.tgsmf.activity.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.util.TGSLocation
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSSeletPicture
import com.android.tgsmf.view.adapter.TGSPictureSelectAdapter
import com.android.tgsmf.view.dialog.TGSLoadingBar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

abstract class TGSBasePictureLocationUploadActivity<VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) {

    protected var mTempCameraUri: Uri? = null

    data class TGSPictureInfo (
        val path:String?,
        val name:String?,
        val uri: Uri,
        val bitmap: Bitmap?
    )
    protected val mArrayPicture = arrayListOf<TGSPictureInfo>()
    protected var mLon:Double? = null       // 위도
    protected var mLat:Double? = null       // 경도

    protected var mListView : RecyclerView? = null
    protected var mAdapter: TGSPictureSelectAdapter? = null

    protected var mButtonCamera:ImageButton? = null
    protected var mButtonGallery:ImageButton? = null
    protected var mCheckboxShareLocation:CheckBox? = null
    protected var mButtonClose:ImageButton? = null
    protected var mButtonUpload: Button? = null

    //---------------------------------------------------------------------------------
    override fun initData() {
        super.initData()

        mListView?.let {
            mAdapter = TGSPictureSelectAdapter(
                    this,
                            mArrayPicture,
                            object : TGSPictureSelectAdapter.OnClickListener{
                                override fun onDelete(position: Int) {
                                    delPictureInfo(position)
                                }
                            })
            it.adapter = mAdapter
        }

        // 선택 사진 리스트뷰 가로 스크롤 설정
        mListView?.let { it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) }

        if(mButtonClose != null) mButtonClose!!.setOnClickListener {  onClickClose() }
        if(mButtonCamera != null) mButtonCamera!!.setOnClickListener {  onClickCamera() }
        if(mButtonGallery != null) mButtonGallery!!.setOnClickListener {  onClickGallery() }
        if(mCheckboxShareLocation != null) mCheckboxShareLocation!!.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckedChangeLocation(isChecked)
        }
        if(mButtonUpload != null) mButtonUpload!!.setOnClickListener {  onClickOK() }

    }

    //---------------------------------------------------------------------------------
    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    //---------------------------------------------------------------------------------
    // 필요권한을 체크 한다.
    protected open fun checkPermission() {
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return

        mCheckboxShareLocation?.let { _checkbox->
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                _checkbox.isChecked = true
            } else {
                _checkbox.isChecked = false
            }
        }

        val baseMultiplePermissionsListener: MultiplePermissionsListener = object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if(report.areAllPermissionsGranted()) {
                        //showTakePictureFragment()
                    }
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }
        }

        val dialogMultiplePermissionsListener: MultiplePermissionsListener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
            .withContext(this)
            .withTitle("Camera & Gellery & 위치 접근 permission")
            .withMessage("[사진 및 위치 업로드] 기능을 사용하기 위해 카메라 및 갤러리, 위치 접근 권한이 필요합니다.")
            .withButtonText("OK")
            //.withIcon(R.drawable.ic_menu_camera)
            .build()

        val compositePermissionsListener: MultiplePermissionsListener = CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, baseMultiplePermissionsListener)
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(compositePermissionsListener)
            //.onSameThread()
            .check()
    }

    //---------------------------------------------------------------------------------
    // 닫기 버튼을 눌렀을때 화면을 닫는다.
    open fun onClickClose() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    //---------------------------------------------------------------------------------
    // 카메라 버튼을 눌렀을때 카메라를 오픈한다.
    open fun onClickCamera() {
        if(!TGSSeletPicture.checkSelectPicturePermission(this))
            return

        val intent:Intent? = TGSSeletPicture.onTakeCamera(this)
        intent?.let {_intent->
            var pictureUri: Uri? = _intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
            if(pictureUri != null) {
                mTempCameraUri = pictureUri
                TGSLog.d("[${TAG}] pictureUri - ${pictureUri.toString()}")
                TGSLog.d("[${TAG}] pictureUri - ${TGSSeletPicture.FilepathFromUri(this, pictureUri)}")
                mCameraResultLaunch.launch(_intent)
            }
        }
    }
    //---------------------------------------------------------------------------------
    // 사진첩 버튼을 눌렀을때 사진선택앱을 오픈한다.
    open fun onClickGallery() {
        if(!TGSSeletPicture.checkSelectPicturePermission(this))
            return

        val intent:Intent? = TGSSeletPicture.onTakeGallery()
        intent?.let {_intent->
            mGalleryResultLaunch.launch(_intent)
        }
    }
    //---------------------------------------------------------------------------------
    // 위치 공유 체크박스를 선택했을때 위치 접근 권한을 체크하도록 한다.
    open fun onCheckedChangeLocation(_isChecked:Boolean) {
        if(_isChecked) {
            TGSLocation.checkLocationPermission(this)
        }
    }

    //---------------------------------------------------------------------------------
    open fun onClickOK() {
        onPictureLocationUpload()
    }

    //---------------------------------------------------------------------------------
    open fun onPictureLocationUpload() {
        //위치정보 확인
        if(mCheckboxShareLocation != null) {
            if(mCheckboxShareLocation!!.isChecked) {
                if(!TGSLocation.checkLocationPermission(this)) {
                    mCheckboxShareLocation!!.isChecked = false
                    return
                }

                if(mLat == null || mLon == null) {
                    // 현재 위치 정보를 얻어온 뒤 업로드를 진행한다.
                    TGSLocation.getCurrentLocation(this, ::getLocation)
                    return
                }
            }
        }

        TGSLog.d("[${TAG}_onPictureLocationUpload] 사진 : ${mArrayPicture.size}장, 위도 : ${mLon}, 경도 : ${mLat}")
        onRequestPictureLocationUpload()
//
//        val intent = Intent()
//        intent.putExtra("test", "intent test")
//        setResult(RESULT_OK, intent)
//        finish()
    }

    //---------------------------------------------------------------------------------
    open fun onRequestPictureLocationUpload() {
    }

    //---------------------------------------------------------------------------------
    open fun onUploadResult(isSuccess:Boolean) {
        val intent = Intent()
        if(isSuccess) {
            intent.putExtra("test", "intent test")
        } else {

        }
        setResult(RESULT_OK, intent)
        finish()
    }

    //---------------------------------------------------------------------------------
    open protected fun getLocation(lon:Double, lat:Double) {
        TGSLog.d("[${TAG}_getLocation] 위도 : ${lon}, 경도 : ${lat}")

        mLon = lon
        mLat = lat
        onPictureLocationUpload()
    }

    //---------------------------------------------------------------------------------
    protected fun addPictureInfo(picture:TGSPictureInfo) {
        synchronized(mArrayPicture) {
            for(item in mArrayPicture) {
                if(item.uri == picture.uri)
                    return
            }
            mArrayPicture.add(picture)
        }

        mAdapter?.let {
            it.notifyDataSetChanged()
        }
    }
    //---------------------------------------------------------------------------------
    protected fun delPictureInfo(index:Int) {
        synchronized(mArrayPicture) {
            if(index < mArrayPicture.size) {
                mArrayPicture.removeAt(index)
            }
        }
        mAdapter?.let {
            it.notifyDataSetChanged()
        }
    }



    //---------------------------------------------------------------------------------
    protected val mCameraResultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            result.data?.extras?.let { _extras->
                if(_extras.get("data") != null){
                    val bitmap = _extras.get("data") as Bitmap
                    TGSLog.d("[${TAG}_mCameraResultLaunch] bitmap - ${bitmap.byteCount}")
                }
            } ?: run {
                if(mTempCameraUri != null) {
                    addPictureInfo(TGSPictureInfo(
                        TGSSeletPicture.FilepathFromUri(this, mTempCameraUri!!)
                        , TGSSeletPicture.FilenameFromUri(this, mTempCameraUri!!)
                        , mTempCameraUri!!, null))
                }

            }
        }
    }

    protected val mGalleryResultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            result.data?.clipData?.let {_clipdata->
                for(i in 0 .._clipdata.itemCount-1) {
                    val uri = _clipdata.getItemAt(i).uri as Uri

                    addPictureInfo(TGSPictureInfo(
                        TGSSeletPicture.FilepathFromUri(this, uri)
                        , TGSSeletPicture.FilenameFromUri(this, uri)
                        , uri, null))

                    TGSLog.d("[${TAG} _GalleryResultLaunch] clipData uri  - ${uri.toString()}")
                    TGSLog.d("[${TAG} _GalleryResultLaunch] clipData path - ${TGSSeletPicture.FilepathFromUri(this, uri)}")
                    TGSLog.d("[${TAG} _GalleryResultLaunch] clipData name - ${TGSSeletPicture.FilenameFromPath( TGSSeletPicture.FilepathFromUri(this, uri)!! )}")
                }
            } ?: kotlin.run {
                val uri = result.data?.data as Uri

                addPictureInfo(TGSPictureInfo(
                    TGSSeletPicture.FilepathFromUri(this, uri)
                    , TGSSeletPicture.FilenameFromUri(this, uri)
                    , uri, null))

                TGSLog.d("[${TAG} _GalleryResultLaunch] data uri  - ${uri.toString()}")
                TGSLog.d("[${TAG} _GalleryResultLaunch] data path - ${TGSSeletPicture.FilepathFromUri(this, uri)}")
            }
        }
    }


}