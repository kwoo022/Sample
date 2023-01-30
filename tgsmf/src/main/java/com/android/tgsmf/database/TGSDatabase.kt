package com.android.tgsmf.database

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.tgsmf.database.dao.TGSPushMessageDao
import com.android.tgsmf.database.entity.TGSPushMessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TGSPushMessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TGSConverters::class)
abstract class TGSDatabase  : RoomDatabase() {
    val TAG = javaClass.simpleName

    abstract fun pushMessageDao(): TGSPushMessageDao                    // 사용자 정보 테이블

    companion object {
        private val TGS_DATABASE_NAME: String = "tgsData.db"

        //---------------------------------------------------------------
        private var instance: TGSDatabase? = null
        @Synchronized
        fun getInstance(context: Context): TGSDatabase? {
            if (instance == null) {
                synchronized(TGSDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TGSDatabase::class.java,
                        TGS_DATABASE_NAME
                    ).addCallback(object:RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // DB 생성 시 초기 데이터를 생성하여 넣는다
//                            instance?.let{ database ->
//                                CoroutineScope(Dispatchers.IO).launch {
//
//                                    var dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
//                                    (0..10).forEach {
//                                        var strNow =dateFormat.format(Calendar.getInstance().time)
//                                        database.pushMessageDao().insert(
//                                            TGSPushMessageEntity(0, "푸시 알림 타이틀 $it", "$it 설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명", strNow, 0, "")
//                                        )
//                                    }
//                                }
//                            }
                        }
                    })
                        //.fallbackToDestructiveMigration()       // database 에서 스키마의 변화가 생길 때 모든 데이터를 drop 하고 새로 생성
                        //.addMigrations(MIGRATION_1_2)
                        .build()
                }
            }
            return instance
        }
        //---------------------------------------------------------------

    }
}