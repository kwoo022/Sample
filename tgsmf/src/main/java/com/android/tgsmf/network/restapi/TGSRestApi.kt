package com.android.tgsmf.network.restapi

import android.content.Context
import com.android.tgsmf.network.restapi.fileupload.TGSFileUploadProgressRequestBody
import com.android.tgsmf.util.TGSLog
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


enum class TGSRestApiResultCode {
    success, fail, error
}
interface TGSRestApiListener<R> {
    fun onResult(code:TGSRestApiResultCode, msg:String, result:R?)
}

class TGSRestApi(val _context: Context, val _baseUrl:String) {

    private val mRetrofit:Retrofit
    val RETROFIT get() = mRetrofit

    //-----------------------------------------------------------------
    companion object {

        @Volatile private var _instance: TGSRestApi? = null
        @JvmStatic
        fun instance(context: Context, baseUrl:String):TGSRestApi =
            _instance?: synchronized(this) {
                _instance ?: TGSRestApi(context, baseUrl).also {
                    _instance = it
                }
            }

        //-----------------------------------------------------------------
        @JvmStatic
        fun <T>getApiService(context: Context, baseUrl:String, service:Class<T>): T {
            return instance(context, baseUrl).RETROFIT.create(service)
        }

        //-----------------------------------------------------------------
        @JvmStatic
        fun getMultipartBody(requestKey:String, path:String, fileName:String, listener:TGSFileUploadProgressRequestBody.FileUploaderCallback): MultipartBody.Part {
            val fileBody: RequestBody = TGSFileUploadProgressRequestBody(path, listener)
            val filePart:MultipartBody.Part = MultipartBody.Part.createFormData(requestKey, fileName, fileBody)
            return filePart
        }

        //-----------------------------------------------------------------
        @JvmStatic
        fun <M>request(serviceCall: Call<M>, listener:TGSRestApiListener<M>) {
            serviceCall.enqueue(object :Callback<M> {
                override fun onResponse(call: Call<M>, response: Response<M>) {
                    TGSLog.d("[TGSRestApi_request] onResponse : "+response.body())

                    if(response.isSuccessful) {
                        val result = response.body()
                        listener.onResult(TGSRestApiResultCode.success, "success", result)

                    } else {
                        listener.onResult(TGSRestApiResultCode.fail, "fail", null)
                    }
                }

                override fun onFailure(call: Call<M>, t: Throwable) {

                    var msg = if(t.message == null) "fail" else t.message.toString()
                    listener.onResult(TGSRestApiResultCode.error, msg, null)
                }
            })
        }


    }

    //-----------------------------------------------------------------
    init {
        val client = OkHttpClient.Builder()
//            .cookieJar(JavaNetCookieJar(CookieManager()))
//            .addInterceptor(AddCookiesInterceptor(context))
//            .addInterceptor(ReceivedCookiesInterceptor(context))
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        mRetrofit = Retrofit.Builder()
            //.baseUrl("https://jsonplaceholder.typicode.com/")
            .baseUrl(_baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()

        //_restService = retrofit.create(_service)
    }



}
