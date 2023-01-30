package com.android.tgsmf.data

import androidx.navigation.NavDirections

data class TGSMenu(
    val depth:Int,
    val menuName:String,
    val clickEvent:String?,
    val arraySub:ArrayList<TGSMenu>?
)

data class TGSNaviMenu(
    val id:Int,
    val menuId:Int,
    val defaultArg: HashMap<String, String>?,
    val funDirections: NavDirections
)

data class TGSTabMenu(
    val menuId:Int,
    val classDest: String,
    val defaultArg: HashMap<String, String>? = null,
)
