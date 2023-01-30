package com.android.tgsmf.util

import android.net.Uri
import java.io.File

object TGSFile {

    inline fun GetFileNameToFile(filePath:String):String {
        val file = File(filePath)   // "/storage/sdcard0/DCIM/Camera/hello_bryan.jpg"
        return file.getName()
    }

    inline fun GetFileNameToString(filePath:String):String {
        return filePath.substring(filePath.lastIndexOf("/")+1)
    }

    inline fun GetFileNameToUri(fileUri:Uri):String? {
        return fileUri.getLastPathSegment()
    }

}