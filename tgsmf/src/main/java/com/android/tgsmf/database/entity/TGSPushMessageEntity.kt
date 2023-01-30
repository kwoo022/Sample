package com.android.tgsmf.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName =  TGSPushMessageEntity.TABLE_NAME)
data class TGSPushMessageEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PUSH_ID") val Id:Int=0,                   //ID
    @ColumnInfo(name = "TITLE") var title:String = "",          //푸시제목
    @ColumnInfo(name = "MESSAGE") var message:String = "",      //푸시메시지
    @ColumnInfo(name = "INSERT_DT") var insertDt:String = "",   //알림수신일자(YYYYMMDDHHmmss)
    @ColumnInfo(name = "IS_READ") var isRead:Int = 0,       //알림확인여부(0:미확인/1:확인)
    @ColumnInfo(name = "READ_DT") var readDt:String = "",       //알림확인일자(YYYYMMDDHHmmss)
) : TGSBaseEntity() {
    constructor():this(0, "", "", "", 0, "")

    companion object {
        const val TABLE_NAME: String="TB_PUSH_MESSAGE"

        fun getColumnToArray():Array<String> {
            return arrayOf(
                "PUSH_ID",
                "TITLE",
                "MESSAGE",
                "INSERT_DT",
                "IS_READ",
                "READ_DT")
        }
    }

    //---------------------------------------------------------------
    override fun getDataToArray():Array<String> {
        return arrayOf(Id.toString(), title, message, insertDt, isRead.toString(), readDt)
    }
}