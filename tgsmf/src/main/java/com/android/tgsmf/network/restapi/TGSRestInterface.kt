package com.android.tgsmf.network.restapi

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*




interface TGSRestInterface {
    companion object{
        // 10.0.0.2
        val TGS_REST_BASE_URL: String ="http://118.47.55.173:3002"
        //val TGS_REST_BASE_URL: String ="http://10.0.1.104:8080"


        // 링크 주소
        var TGS_URL_LOGIN = ""
        var TGS_URL_FIND_ID ="https://nid.naver.com/user2/help/idInquiry.nhn?menu=idinquiry"
        var TGS_URL_FIND_PW ="https://nid.naver.com/user2/help/pwInquiry?menu=pwinquiry"//"https://m.naver.com"

        var TGS_URL_MAIN_NAV_HOME = "file:///android_asset/tgs_main_home.html"
        var TGS_URL_MAIN_NAV_NOTICE = "file:///android_asset/tgs_main_notice.html"
        var TGS_URL_MAIN_NAV_ID_CARD = "file:///android_asset/tgs_main_id_card.html"
    }

//    @FormUrlEncoded
//    @JvmSuppressWildcards
//    @POST("/login")
//    fun <T>login(@Field("id") id:String, @Field("password") password:String):Call<T>

    @FormUrlEncoded
    @POST("/login")
    fun login(@Field("id") id:String, @Field("password") password:String):Call<TGSRestModel.TGSRestResult>

    @GET("/qrCheckIn")
    fun qrCheckIn(@Query("qrcode")qrcode:String) : Call<TGSRestModel.TGSRestResult>

    @Multipart
    @POST("/checkOutside")
    fun uploadFile(
        @Part listFile: List<MultipartBody.Part>,
        @Header("Content-Length") filesize: String?,
        @Query("lon")lon:String,
        @Query("lat")crse:String
    ): Call<TGSRestModel.TGSRestResult>


    /**
     * @DELETE("favorite.php") // @Delete Annotation
    Call<CheckSuccess> deleteFavorite(
    @Query("id") String id

    );
     */


    //https://velog.io/@hoyaho/Retrofit2%EC%97%90-%EB%8C%80%ED%95%B4-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90
}