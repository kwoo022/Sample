package com.android.tgsmf.network.restapi

import com.google.gson.annotations.SerializedName
import org.json.JSONException
import org.json.JSONObject


object TGSRestModel {

    class TGSRestResult(_success:Boolean, _message:String?=null, _result:String? = null) {
        @SerializedName("success")
        var success : Boolean = false
        @SerializedName("message")
        var message : String? = null
        @SerializedName("result")
        var result : String? = null

        init {
            success = _success
            message = _message
            result = _result
        }

        override fun toString(): String {
            return """
                TGSRestResult {
                    "success":"${if(success==true) "true" else "false"}",
                    "message":"${message?.let { it }?: kotlin.run {"null"}}",
                    "result":"${result?.let { it }?: kotlin.run {"null"}}",
            """
        }

        fun getResultJsonObject() : JSONObject? {
            if(result == null) {return null}

            try {
                val json = JSONObject(result)
                return json
            } catch (e: JSONException) {
                return null
            }
        }
        fun getResultString(key:String) : String? {
            val json : JSONObject? = getResultJsonObject()
            if(json == null) return null

            if(json.has(key)) {
                return json.getString(key)
            }
            return null
        }
        fun getResultInt(key:String) : Int? {
            val json : JSONObject? = getResultJsonObject()
            if(json == null) return null

            if(json.has(key)) {
                return json.getInt(key)
            }
            return null
        }
        fun getResultBoolean(key:String) : Boolean? {
            val json : JSONObject? = getResultJsonObject()
            if(json == null) return null

            if(json.has(key)) {
                return json.getBoolean(key)
            }
            return null
        }

    }
}