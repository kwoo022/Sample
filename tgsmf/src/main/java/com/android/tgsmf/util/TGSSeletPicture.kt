package com.android.tgsmf.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * 사진 선택 기능을 사용하기 위해서는 AnaroidManifest.xml에 아래 권한을 요청해야함.
 *
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> <!-- 파일 읽기 권한 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
 */

/**
 *
 * 선택한 사진 파일의 공유를 위해 fileprovider 설정이 필요하다
 *
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> <!-- 파일 읽기 권한 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
 */


object TGSSeletPicture {
    val TAG = javaClass.simpleName

    //---------------------------------------------------------------------------------
    //Path(파일경로) -> Uri
    inline fun UriFromFilepath(_context:Context, _path:String):Uri? {

        var file = File(_path)
        if(!file.exists())
            return null

        val uri: Uri = FileProvider.getUriForFile(_context, _context.packageName.toString() + ".fileprovider",
            file
        )
        return uri
    }
    //Uri -> Path(파일경로)
    @SuppressLint("Range")
    inline fun FilepathFromUri(_context:Context, _uri:Uri):String? {
        try {
            if (_uri.path != null && (_uri.path!!.startsWith("/storage") || _uri.path!!.startsWith("/data"))) {
                return _uri.path!!
            }


            val proj = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = _context.contentResolver.query(_uri, proj, null, null, null);
            cursor?.let { _cursor ->
                _cursor.moveToNext()
                val path = _cursor.getString(_cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                //val path = _cursor.getString(_cursor.getColumnIndex("cache"))
                //val uri = Uri.fromFile(File(path))
                cursor!!.close()
                return path
            } ?: run {
                return null
            }
        } catch (e:Exception) {
            return null
        }
    }

    inline fun FilenameFromPath(_path:String) : String? {
        return _path.substring(_path.lastIndexOf("/")+1)
    }
    inline fun FilenameFromUri(_context:Context, _uri:Uri) : String? {
        val path = FilepathFromUri(_context, _uri)
        if(path != null) {
            var name = FilenameFromPath(path)
            if(name != null)
                return name
        }

        val extension = FileExtension(_context, _uri)
        if(extension == null)
            return _uri.lastPathSegment
        else
            return "${_uri.lastPathSegment}.${extension}"
    }

    inline fun FileExtension(_context:Context, _uri: Uri) :String? {
        val extension = (
                    if (_uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                        val mime = MimeTypeMap.getSingleton()
                        mime.getExtensionFromMimeType(_context.getContentResolver().getType(_uri))
                    } else {
                        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(_uri.getPath())).toString())
                    }
                )
        return extension
    }

    //---------------------------------------------------------------------------------
    inline fun FileSize(_path:String):Long {
        var file = File(_path)
        if(!file.exists())
            return 0

        return file.length()
    }



    //---------------------------------------------------------------------------------
    fun checkSelectPicturePermission(_activity: Activity):Boolean {

        if(_activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            && _activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && _activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true

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
            .withContext(_activity)
            .withTitle("Camera & Gellery 접근 permission")
            .withMessage("[사진 가져오기] 기능을 사용하기 위해 카메라 및 갤러리 접근 권한이 필요합니다.")
            .withButtonText("OK")
            //.withIcon(R.drawable.ic_menu_camera)
            .build()

        val compositePermissionsListener: MultiplePermissionsListener = CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, baseMultiplePermissionsListener)
        Dexter.withContext(_activity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(compositePermissionsListener)
            //.onSameThread()
            .check()

        return false
    }

    //---------------------------------------------------------------
    // 카메라로 이미지 가져오기를 했을 경우
    fun onTakeCamera(_activity: Activity): Intent? {
        TGSLog.d("${TAG} _onTakeCamera]")

        try {
            val values = ContentValues().apply {
                val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
                val imageFileName = "Capture_" + timeStamp + ".jpg"

                put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val contentResolver: ContentResolver = _activity.contentResolver
            val imageUri: Uri? = contentResolver.insert(collection, values)!!

            imageUri?.let {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                return intent
            }
        } catch (e: IOException) {
            //에러 로그는 이렇게 관리하는 편이 좋다.
            TGSLog.d("${TAG} _onTakeCamera] 파일 생성 에러 : ${e.toString()}")
        }

        return null
    }
    fun onTakeCameraA(_activity: Activity): Intent? {
        TGSLog.d("${TAG} _onTakeCamera]")

        // 촬영한 사진을 저장할 파일 생성
//        var photoFile: File? = null
        try {
//            //임시로 사용할 파일이므로 경로는 캐시폴더로
//            val tempDir: File = _activity.cacheDir

            //임시촬영파일 세팅
//            val timeStamp: String = SimpleDateFormat("yyyyMMdd").format(Date())
//            val imageFileName = "Capture_" + timeStamp + "_" //ex) Capture_20201206_
//            val tempImage: File = File.createTempFile(
//                imageFileName,  /* 파일이름 */
//                ".jpg",  /* 파일형식 */
//                tempDir /* 경로 */
//            )
//            photoFile = tempImage
            val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val imageFileName = "Capture_" + timeStamp + ".jpg"

            //imageUri = createImageUri(imageFileName, "image/jpeg")
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName) // 확장자가 붙어있는 파일명 ex) sample.jpg

            //values.put(MediaStore.Images.Media.MIME_TYPE, mimeType) // ex) image/jpeg
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // ex) image/jpeg

            val contentResolver: ContentResolver = _activity.contentResolver
            val imageUri: Uri? = contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values)

            imageUri?.let {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                return intent
            }


        } catch (e: IOException) {
            //에러 로그는 이렇게 관리하는 편이 좋다.
            TGSLog.d("${TAG} _onTakeCamera] 파일 생성 에러 : ${e.toString()}")
        }

        //파일이 정상적으로 생성되었다면 계속 진행
//        if (photoFile != null) {
//
//            TGSLog.d("${TAG} _onTakeCamera] FilePath : ${photoFile.absolutePath}")
//            val photoURI: Uri? = UriFromFilepath(_activity, photoFile.absolutePath)
//
//            //인텐트에 Uri담기
//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            return takePictureIntent
//        }

        return null
    }

    //---------------------------------------------------------------
    // 갤러리에서 이미지 가져오기를 했을 경우
    fun onTakeGallery(): Intent? {
        TGSLog.d("${TAG} _onTakeGallery]")
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type ="image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT // ACTION_PICK은 사용하지 말것, deprecated + formally
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        val chooserIntent =  Intent.createChooser(intent, "사진첩가져오기")
        return chooserIntent
    }

}