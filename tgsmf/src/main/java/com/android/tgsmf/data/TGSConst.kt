package com.android.tgsmf.data

object TGSConst {
    // 프리퍼런스 저장 정보
    var TGS_PREFER_NAME = "TGSFW_PERFER"
    val TGS_PREFER_KEY_SAVE_LOGIN_ACCOUNT = "tgs_save_login_account"
    val TGS_PREFER_KEY_LOGIN_ID = "tgs_login_id"
    val TGS_PREFER_KEY_LOGIN_PW = "tgs_login_pw"
    val TGS_PREFER_KEY_IS_PUSH_SEND = "tgs_is_push_send"       // 푸시 수신 여부
    val TGS_PREFER_KEY_PUSH_DENY_DATE = "tgs_push_deny_date"    // 푸시 수신 거절 날짜
}

// 타이틀바 유형(공통으로 사용하는 activity, fragment에서 타이틀바 유형을 변경하기 위해 사용가능)
enum class TGSTitleType(val value:Int) {
    NONE(1),
    TITLE(2),
    BACK(4),
    CLOSE(8);
}

// 프레그먼트 호출 시 전송되는 Argument Key 값
object TGSArgument{
    val TITLE_TYPE = "title_type"
    val TITLE_NAME = "title"
    val INIT_URL = "url"
    val JAVASCRIPT_LISTENER = "js_listener"
}

//val TGS_ARG_INIT_URL = "init_url"
//val TGS_ARG_TITLE_NAME = "title"
//val TGS_ARG_JAVASCRIPT_LISTENER = "js_listener"
