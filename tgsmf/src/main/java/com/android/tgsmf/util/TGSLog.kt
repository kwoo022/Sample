package com.android.tgsmf.util

/**
 * log 남기는 기능을 지원한다.
 * WRITE_LOG 플래그를 조작하여 전체 로그 쓰기 여부를 설정한다.
 */
object TGSLog {
    private val WRITE_LOG:Boolean = true
    private val TAG:String = "tgsmf"

    private fun log(type:String, tag: String, msg:String) {
        if (!WRITE_LOG)
            return

        var newMsg = msg
        while (newMsg.length > 0 ) {
            if(newMsg.length > 2000) {
                logWrite(type, tag, newMsg.substring(0, 2000))
                newMsg = newMsg.substring(2000)
            } else {
                logWrite(type, tag, newMsg)
                break
            }
        }
    }
    private fun logWrite(type:String, tag: String, msg:String) {
        if(type.equals("d", true))
            android.util.Log.d(tag, msg)
        else if(type.equals("e", true))
            android.util.Log.e(tag, msg)
        else if(type.equals("i", true))
            android.util.Log.i(tag, msg)
        else if(type.equals("w", true))
            android.util.Log.w(tag, msg)
    }

    /**
     * debug 로그 출력
     */
    @JvmStatic fun d(msg:String) { d(TAG, msg) }
    @JvmStatic fun d(tag: String, msg:String) {
        if (!WRITE_LOG)
            return
        log("d", tag, msg)
    }

    /**
     *  error 로그 출력
     */
    @JvmStatic fun e(msg:String) { e(TAG, msg) }
    @JvmStatic fun e(tag: String, msg:String) {
        if (!WRITE_LOG)
            return
        log("e", tag, msg)
    }

    /**
     * info  로그 출력
     */
    @JvmStatic fun i(msg:String) { i(TAG, msg) }
    @JvmStatic fun i(tag: String, msg:String) {
        if (!WRITE_LOG)
            return
        log("i", tag, msg)
    }

    /**
     * warm  로그 출력
     */
    @JvmStatic fun w(msg:String) { w(TAG, msg) }
    @JvmStatic fun w(tag: String, msg:String) {
        if (!WRITE_LOG)
            return
        log("w", tag, msg)
    }
}