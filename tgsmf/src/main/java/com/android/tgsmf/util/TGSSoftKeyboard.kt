package com.android.tgsmf.util;

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.children
import okhttp3.internal.notify
import okhttp3.internal.wait
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


/*********************************************************************
 * 화면상에 존재하는 Editbox의 Focus를 인식하여 소프트키보드 show/hide 이벤트를 발생
 *
 *********************************************************************/
class TGSSoftKeyboard(private val _rootLayout:ViewGroup, private val  _im:InputMethodManager) : View.OnFocusChangeListener {
    //---------------------------------------------------------------
    companion object {
//        private var instance : TGSSoftKeyboard? = null
//        @Synchronized
//        fun getInstance(layout:ViewGroup, im:InputMethodManager) : TGSSoftKeyboard? {
//            if(TGSSoftKeyboard.instance == null) {
//                TGSSoftKeyboard.instance = TGSSoftKeyboard(layout, im)
//            }
//            return TGSSoftKeyboard.instance
//        }

        interface TGSSoftKeyboardChanged {
            fun onKeyboardHide()
            fun onKeyboardShow()
        }
    }

    //---------------------------------------------------------------
    private val CLEAR_FOCUS:Int = 0

    //private var layout:ViewGroup
    private var layoutBottom:Int? = null

    //private var im:InputMethodManager
    private var coords:IntArray = IntArray(2)
    private var isKeyboardShow:Boolean
    private var softKeyboardThread :SoftKeyboardChangesThread
    private var editTextList:ArrayList<EditText>? = null

    private var tempView:View? = null // reference to a focused EditText

    //---------------------------------------------------------------
    init {
        keyboardHideByDefault()
        initEditTexts(_rootLayout)

        isKeyboardShow = false
        softKeyboardThread = SoftKeyboardChangesThread()
        softKeyboardThread.start()
    }

    //---------------------------------------------------------------
    fun keyboardHideByDefault()
    {
        _rootLayout.setFocusable(true);
        _rootLayout.setFocusableInTouchMode(true);
    }
    /*
     * InitEditTexts now handles EditTexts in nested views
     * Thanks to Francesco Verheye (verheye.francesco@gmail.com)
     */
    //---------------------------------------------------------------
    fun initEditTexts(viewgroup:ViewGroup) {
        if(editTextList == null)
            editTextList =  arrayListOf<EditText>()

        var childCount = viewgroup.getChildCount();
        for(view in viewgroup.children) {
            if(view is ViewGroup) {
                initEditTexts(view as ViewGroup)
            } else if(view is EditText) {
                var editText:EditText = view as EditText
                editText.onFocusChangeListener = this
                editText.isCursorVisible = true
                editTextList!!.add(editText)
            }
        }
    }

    //---------------------------------------------------------------
    fun openSoftKeyboard() {
        if(!isKeyboardShow) {
            layoutBottom = getLayoutCoordinates()
            _im.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
            softKeyboardThread.keyboardOpened()
            isKeyboardShow = true;
        }
    }

    //---------------------------------------------------------------
    fun closeSoftKeyboard() {
        if(isKeyboardShow) {
            _im.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            isKeyboardShow = false;
        }
    }

    //---------------------------------------------------------------
    fun setSoftKeyboardCallback(callback:TGSSoftKeyboardChanged) {
        softKeyboardThread.mCallback = callback
    }

    //---------------------------------------------------------------
    fun unRegisterSoftKeyboardCallback() {
        softKeyboardThread.stopThread()
        softKeyboardThread.interrupt()
    }

    //---------------------------------------------------------------
    fun getLayoutCoordinates() :Int {
        _rootLayout.getLocationOnScreen(coords)
        return coords[1] + _rootLayout.getHeight()
    }

    /*
     * OnFocusChange does update tempView correctly now when keyboard is still shown
     * Thanks to Israel Dominguez (dominguez.israel@gmail.com)
     */
    //---------------------------------------------------------------
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if(hasFocus && view != null) {
            tempView = view!!
            if(!isKeyboardShow) {
                layoutBottom = getLayoutCoordinates()
                softKeyboardThread.keyboardOpened()
                isKeyboardShow = true
            }
        }
    }

    //---------------------------------------------------------------
    private final val mHandler:Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                CLEAR_FOCUS->{
                    if(tempView != null) {
                        tempView!!.clearFocus()
                        tempView = null
                    }
                }
            }
        }
    }


    /*********************************************************************
     *
     *
     *********************************************************************/
    inner class SoftKeyboardChangesThread : Thread() {

        private var mObject:Object
        private var mStarted:AtomicBoolean
        var mCallback:TGSSoftKeyboardChanged? = null
            set(_callback) {field = _callback}

        //---------------------------------------------------------------
        init {
            mObject = Object()
            mStarted = AtomicBoolean(true)
        }

        //---------------------------------------------------------------
        override fun run() {
            while(!isInterrupted() && mStarted.get()) {
                try {
                    // Wait until keyboard is requested to open
                    synchronized(mObject) {
                        try {
                            mObject.wait()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }

                    var currentBottomLocation = getLayoutCoordinates()
                    while (currentBottomLocation == layoutBottom && mStarted.get()) {
                        currentBottomLocation = getLayoutCoordinates()
                    }

                    if (mStarted.get()) {
                        mCallback!!.onKeyboardShow()
                    }


                    // When keyboard is opened from EditText, initial bottom location is greater than layoutBottom
                    // and at some moment equals layoutBottom.
                    // That broke the previous logic, so I added this new loop to handle this.
                    while (currentBottomLocation >= layoutBottom!! && mStarted.get()) {
                        currentBottomLocation = getLayoutCoordinates()
                    }


                    // Now Keyboard is shown, keep checking layout dimensions until keyboard is gone
                    while (currentBottomLocation != layoutBottom && mStarted.get()) {
                        synchronized(mObject) {
                            try {
                                mObject.wait(500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                        currentBottomLocation = getLayoutCoordinates()
                    }

                    if (mStarted.get()) {
                        mCallback!!.onKeyboardHide()
                    }
                    if (isKeyboardShow && mStarted.get()) isKeyboardShow = false
                    if (mStarted.get())
                        mHandler.obtainMessage(CLEAR_FOCUS).sendToTarget()
                } catch (e:InterruptedException) {
                    Thread.currentThread( ).interrupt( )
                } catch (e:Exception) {
                    e.printStackTrace( )
                }
            }
        }

        fun keyboardOpened() {
            synchronized(mObject) { mObject.notify() }
        }

        fun stopThread() {
            synchronized(mObject) {
                mStarted.set(false)
                mObject.notify()
            }
        }

    }
}