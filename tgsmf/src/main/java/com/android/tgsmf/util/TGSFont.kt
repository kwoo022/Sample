package com.android.tgsmf.util

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.android.tgsmf.R


enum class FONT_TYPE {NOTO_BLACK, NOTO_BOLD, NOTO_LIGHT, NOTO_MEDIUM, NOTO_REGULAR, NOTO_THIN, GMARKET_BOLD, GMARKET_LIGHT, GMARKET_MEDIUM}

class TGSFont(context: Context) {

    //---------------------------------------------------------------
    companion object {
        private var instance : TGSFont? = null

        @Synchronized
        fun getInstance(context: Context) : TGSFont? {
            if(instance == null) {
                instance = TGSFont(context)
            }
            return instance
        }

        fun setFont(font_type:FONT_TYPE, view: View) {
            instance?.setCustomFont(font_type, view)
        }
    }

    //---------------------------------------------------------------
    private val  mMapFont:HashMap<FONT_TYPE, Typeface>
    init {
        mMapFont = hashMapOf(
            FONT_TYPE.NOTO_BLACK to context.resources.getFont(R.font.notosanscjkkr_black),
            FONT_TYPE.NOTO_BOLD to context.resources.getFont(R.font.notosanscjkkr_bold),
            FONT_TYPE.NOTO_LIGHT to context.resources.getFont(R.font.notosanscjkkr_light),
            FONT_TYPE.NOTO_MEDIUM to context.resources.getFont(R.font.notosanscjkkr_medium),
            FONT_TYPE.NOTO_REGULAR to context.resources.getFont(R.font.notosanscjkkr_regular),
            FONT_TYPE.NOTO_THIN to context.resources.getFont(R.font.notosanscjkkr_thin),
            FONT_TYPE.GMARKET_BOLD to context.resources.getFont(R.font.gmarketsans_bold),
            FONT_TYPE.GMARKET_LIGHT to context.resources.getFont(R.font.gmarketsans_light),
            FONT_TYPE.GMARKET_MEDIUM to context.resources.getFont(R.font.gmarketsans_medium)
        )
    }

    //---------------------------------------------------------------
    private fun getTypeface(font_type:FONT_TYPE):Typeface? {
        var font:Typeface? = null

        if(mMapFont.containsKey(font_type) ) {
            font = mMapFont.get(font_type)
        }
        return font
    }

    //---------------------------------------------------------------
    fun setCustomFont(font_type:FONT_TYPE, view: View) {
        var type:Typeface? = getTypeface(font_type)
        type?.let {
            when(view) {
                is TextView -> {
                    (view as TextView).setTypeface(type)
                }
                is Button -> {
                    (view as Button).setTypeface(type)
                }
                is EditText -> {
                    (view as EditText).setTypeface(type)
                }
                is CheckBox -> {
                    (view as CheckBox).setTypeface(type)
                }
            }

        }
    }

}