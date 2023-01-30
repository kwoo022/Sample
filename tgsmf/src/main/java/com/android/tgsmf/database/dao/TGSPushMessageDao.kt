package com.android.tgsmf.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.tgsmf.database.entity.TGSPushMessageEntity

@Dao
interface TGSPushMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: TGSPushMessageEntity)

    @Query("SELECT COUNT(*) FROM ${TGSPushMessageEntity.TABLE_NAME}")
    fun getAllCount(): Int

    @Query("SELECT COUNT(*) FROM ${TGSPushMessageEntity.TABLE_NAME} where IS_READ= :isRead")
    fun getAllCountByRead(isRead:Int): Int

    @Query("Select * From ${TGSPushMessageEntity.TABLE_NAME} ORDER BY INSERT_DT DESC")
    fun getAllData() : List<TGSPushMessageEntity>

    @Query("Select * From ${TGSPushMessageEntity.TABLE_NAME} where IS_READ= :isRead ORDER BY INSERT_DT DESC")
    fun getAllDataByRead(isRead:Int) : List<TGSPushMessageEntity>

    @Query("Select * From ${TGSPushMessageEntity.TABLE_NAME} where PUSH_ID= :id")
    fun getInfoById(id:Int) : TGSPushMessageEntity

    @Query("UPDATE ${TGSPushMessageEntity.TABLE_NAME} SET IS_READ = :isRead, READ_DT = strftime(\"%Y%m%d%H%M%S\",'now','localtime') WHERE PUSH_ID = :pushId")
    fun updateDataReaded(pushId:Int, isRead: Int)

    @Query("UPDATE ${TGSPushMessageEntity.TABLE_NAME} SET IS_READ = :isRead, READ_DT = strftime(\"%Y%m%d%H%M%S\",'now','localtime') WHERE IS_READ != :isRead")
    fun updateAllDataReaded(isRead: Int)

    @Query("DELETE FROM ${TGSPushMessageEntity.TABLE_NAME}")
    fun deleteAll()


//    @Update
//    public void updateUsers(User... users);
//    @Delete
//    public void deleteusers(User... users);

}