package com.android.tgsmf.data

data class TGSPushMessage(
    var id:Int,
    var title:String,
    var message:String,
    var clickCommand:String,
    var insertDate:String,  //yyyy-MM-dd HH:mm:ss
    var isRead:Boolean = false,
    var readDate:String = ""
)
