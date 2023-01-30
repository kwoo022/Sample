package com.android.tgsmf.network.restapi.fileupload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSSeletPicture
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.*

/**
 *  파일 업로드 진행률을 전달 받을 수 있는 RequestBody 확장 클래스
 */
class TGSFileUploadProgressRequestBody(filePath: String, callback:FileUploaderCallback?) : RequestBody() {

    private val mFilePath: String
    private val mFileName: String
    private var mCallback:FileUploaderCallback? = null

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    interface FileUploaderCallback {
        fun onProgress(fileName:String, uploaded:Long, total:Long)
    }

    //---------------------------------------------------------------
    init {
        mFilePath = filePath
        mFileName = TGSSeletPicture.FilenameFromPath(filePath)!!
        mCallback = callback
    }

    //---------------------------------------------------------------
    override fun contentType(): MediaType? {
        return "multipart/form-data".toMediaTypeOrNull()
        //return MediaType.parse("image/*")
    }

    //---------------------------------------------------------------
//    @Throws(IOException::class)
//    override fun contentLength(): Long {
//        //return mRotateFileByte.size as Long
//        return mFile.length()
//    }

    //---------------------------------------------------------------
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        var inStream: FileInputStream? = null
        var inBuf: BufferedInputStream? = null
        var outStream: ByteArrayOutputStream? = null

        try {
            // 사진 회전값을 얻어온다
            var angle: Float = 0.0f
            val ei: ExifInterface = ExifInterface(mFilePath);
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90.0f
                ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180.0f
                ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270.0f
            }

            inStream = FileInputStream(mFilePath)
            inBuf = BufferedInputStream(inStream)
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

            var fileByteArray =  byteArrayOf()
            val fileLength:Long = File(mFilePath).length()
            var uploaded: Long = 0

            var read: Int
            while (inBuf.read(buffer).also { read = it } != -1) {
                mCallback?.let {
                    it.onProgress(mFileName, uploaded, fileLength)
                }

                fileByteArray += buffer
                uploaded += read.toLong()

                //sink.write(buffer, 0, read)
            }

            var bitmap = BitmapFactory.decodeByteArray(fileByteArray, 0, fileLength.toInt())
            bitmap?.let {
                val matrix = Matrix()
                matrix.postRotate(angle)
                val rotatedBitmap: Bitmap = Bitmap.createBitmap( it, 0, 0, it.width, it.height,matrix, true)

                outStream = ByteArrayOutputStream()
                rotatedBitmap.compress( Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream?.let {
                    sink.write(it.toByteArray(), 0, it.size())
                }
            }
        } catch (e: Exception) {
            TGSLog.e("[TGSProgressRequestBody_writeTo] error - "+e.message)
        } finally {
            if ( inStream != null)
                inStream.close()
            if ( inBuf != null)
                inBuf.close()
            if (outStream!= null)
                outStream!!.close()
        }
    }
}