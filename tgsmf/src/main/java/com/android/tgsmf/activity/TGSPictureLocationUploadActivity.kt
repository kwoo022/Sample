package com.android.tgsmf.activity

import com.android.tgsmf.activity.base.TGSBasePictureLocationUploadActivity
import com.android.tgsmf.databinding.TgsActivityPictureLocationUploadBinding
import com.android.tgsmf.network.restapi.*
import com.android.tgsmf.network.restapi.fileupload.TGSFileUploadProgressRequestBody
import com.android.tgsmf.util.*
import okhttp3.MultipartBody


class TGSPictureLocationUploadActivity : TGSBasePictureLocationUploadActivity<TgsActivityPictureLocationUploadBinding>(TgsActivityPictureLocationUploadBinding::inflate) {
    override var TAG: String =javaClass.simpleName


    //---------------------------------------------------------------------------------
    override fun initView() {
        super.initView()
        mButtonCamera = binding.tgsButtonUploadPictureDialogCamera
        mButtonGallery = binding.tgsButtonUploadPictureDialogGallery
        mListView = binding.tgsListviewUploadPictureDialogPicture
        mCheckboxShareLocation =binding.tgsCheckboxUploadPictureDialogLocation
        mButtonClose = binding.tgsButtonUploadPictureDialogClose
        mButtonUpload = binding.tgsButtonUploadPictureDialogOk
    }

    //---------------------------------------------------------------------------------
    // 서버로 사진과 위치 데이터를 전송한다.
    override fun onRequestPictureLocationUpload() {

        showLoading()

        val arrayFilePart = arrayListOf<MultipartBody.Part>()
        var uploadTotalSize  = 0L
        for(picture in mArrayPicture) {
            val path = picture.path?.let { it } ?: kotlin.run {  picture.uri.path }
            val fileName = picture.name!!

            val filePart: MultipartBody.Part = TGSRestApi.getMultipartBody("images", path!!, fileName,
                object: TGSFileUploadProgressRequestBody.FileUploaderCallback {
                    override fun onProgress(fileName:String, uploaded: Long, total: Long) {
                        mLoadingBar?.let {
                            it.setProgress(uploaded.toInt(), total.toInt(),fileName)
                        }
                        TGSLog.d("[${TAG} _onRequestPictureLocationUpload] ${fileName} : ${uploaded} / ${total}")
                    }
                }
            )
            arrayFilePart.add(filePart)

            uploadTotalSize += TGSSeletPicture.FileSize(path)
            //uploadSize += filePart.body.contentLength()
        }

        var serviceCall = TGSRestApi.getApiService(this,
                            TGSRestInterface.TGS_REST_BASE_URL,
                            TGSRestInterface::class.java
                            ).uploadFile(arrayFilePart, uploadTotalSize.toString(), mLon.toString(), mLat.toString())
        TGSRestApi.request(serviceCall, object: TGSRestApiListener<TGSRestModel.TGSRestResult> {
            override fun onResult(
                code: TGSRestApiResultCode,
                msg: String,
                result: TGSRestModel.TGSRestResult?
            ) {
                hideLoading()

                if(code == TGSRestApiResultCode.success) {
                    result?.let { _result->
                        if(_result.success) {
                            onUploadResult(true)
                        } else {
                            onUploadResult(false)
                        }
                    }

                } else {
                    onUploadResult(false)
                }
            }
        })
    }

}