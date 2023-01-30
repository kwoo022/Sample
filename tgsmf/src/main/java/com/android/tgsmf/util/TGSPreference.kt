package com.android.tgsmf.util

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.TextView

/*********************************************************************
 *
 *
 *********************************************************************/
class TGSPreference(val context: Context, val preferName:String) {

    //---------------------------------------------------------------
    companion object {
        @Volatile private var instance: TGSPreference? = null

        @JvmStatic fun getInstance(_context: Context, _prefername:String): TGSPreference =
            instance?: synchronized(this) {
                instance?: TGSPreference(_context, _prefername).also {
                    instance = it
                }
            }

        fun <T:Any>setPrefer(key:String, value: T) {
            if(instance == null)
                return
            when(value) {
                is String -> instance!!.setStringValue(key, value)
                is Boolean -> instance!!.setBooleanValue(key, value)
                is Int -> instance!!.setIntValue(key, value)
            }
        }

        fun getPrefer(key:String) : Any? {
            if(instance == null)
                return null

            return instance!!.getValue(key)
//
//            var result1 = instance!!.getStringValue(key)
//            if(result1 != null) return result1
//
//            var result2 = instance!!.getIntValue(key)
//            if(result2 != null) return result2
//
//            var result3 = instance!!.getBooleanValue(key)
//            if(result3 != null) return result3
//
//            return null
        }
    }

    //---------------------------------------------------------------
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor

    init {
        //this.context = context
        this.pref = this.context.getSharedPreferences(preferName, Context.MODE_PRIVATE)
        this.editor = this.pref.edit()
    }


    //---------------------------------------------------------------
    fun getValue(key:String) : Any? {
        for(prefer in pref.all) {
            if(prefer.key.equals(key))
                return prefer.value
        }
        return null
    }

    //---------------------------------------------------------------
    fun setStringValue(key:String, value:String) {
        editor?.putString(key, value)
        editor?.apply()
    }
    fun getStringValue(key:String): String? {
        if(!pref.contains(key)) return null
        return pref.getString(key, "")
    }

    //---------------------------------------------------------------
    fun setBooleanValue(key:String, value:Boolean) {
        editor?.putBoolean(key, value)
        editor?.apply()
    }
    fun getBooleanValue(key:String): Boolean? {
        if(!pref.contains(key)) return null
        return pref.getBoolean(key, false)
    }

    //---------------------------------------------------------------
    fun setIntValue(key:String, value:Int) {
        editor?.putInt(key, value)
        editor?.apply()
    }
    fun getIntValue(key:String): Int? {
        if(!pref.contains(key)) return null
        return pref.getInt(key, 0)
    }


}