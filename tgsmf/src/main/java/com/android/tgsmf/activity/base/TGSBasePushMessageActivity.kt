package com.android.tgsmf.activity.base

import android.view.Gravity
import android.widget.ExpandableListView
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.data.TGSPushMessage
import com.android.tgsmf.database.TGSDatabase
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSMetricsUtil
import com.android.tgsmf.view.adapter.TGSPushMessageAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


abstract class TGSBasePushMessageActivity<VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate) {

    protected var mArrayPushInfo = arrayListOf<TGSPushMessage>()
    var mListView: ExpandableListView? = null
    var mAdapter: TGSPushMessageAdapter? = null

    protected var mIsAutoReaded:Boolean = true  // 푸시 화면에 입장 시 읽지 않은 알림을 자동읽음처리 한다.


    //-----------------------------------------------------------------
    override fun initView() {
        super.initView()
    }
    //-----------------------------------------------------------------
    override fun initFont() {
        super.initFont()
        mTextviewTitle?.let {  TGSFont.setFont(FONT_TYPE.NOTO_BOLD, it) }
    }
    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()

        mAdapter = TGSPushMessageAdapter(this, mArrayPushInfo)

        if(mListView != null) {
            mAdapter?.let {  mListView!!.setAdapter(it) }
        }
    }

    //-----------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        getPushMessageInfo()
    }

    //-----------------------------------------------------------------
    // 확장 Indicator 위치를 우측으로 변경
    protected fun setIndicatorGravity(gravity: Int) {
        when(gravity) {
            Gravity.RIGHT-> {
                if(mListView != null) {

                    var width = getWindowManager().getDefaultDisplay().getWidth()
                    mListView!!.setIndicatorBounds(width - TGSMetricsUtil.dpToPx(this, 58.0f), width)
                }
            }
        }
    }

    //-----------------------------------------------------------------
    // 푸시 메시지를 읽은 상태로 변경한다.
    // pushId가 -1일 경우 푸시 메시지 전체를 읽은 상태로 변경한다.
    open fun setPushMessageReaded(pushId:Int=-1) {
        CoroutineScope(Dispatchers.IO).launch {
            TGSDatabase.getInstance(applicationContext)?.let { database ->
                if(pushId == -1)
                    database.pushMessageDao().updateAllDataReaded(1)
                else
                    database.pushMessageDao().updateDataReaded(pushId, 1)
            }
        }
    }

    //-----------------------------------------------------------------
    // DB를 검색하여 푸시 메시지 정보를 얻어온다.
    protected fun getPushMessageInfo(){
        mArrayPushInfo.clear()

        // DB 조회
        CoroutineScope(Dispatchers.IO).launch {
            TGSDatabase.getInstance(applicationContext)?.let { database ->
                //database.pushMessageDao().insert(TGSPushMessageEntity(0, "푸시 알림 타이틀 1", "설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명설명", "20220915191212", 0, ""))

                var arrayPushEntity = database.pushMessageDao().getAllData()

                synchronized(mArrayPushInfo) {
                    for (pushEntity in arrayPushEntity) {
                        var pushInfo = TGSPushMessage(
                            pushEntity.Id,
                            pushEntity.title,
                            pushEntity.message,
                            "",
                            pushEntity.insertDt,
                            (if(pushEntity.isRead==0) false else true),
                            pushEntity.readDt
                        )
                        TGSLog.d("${pushEntity.Id} | ${pushEntity.readDt}")
                        mArrayPushInfo.add(pushInfo)
                    }
                }
                runOnUiThread {
                    if(mAdapter!=null) {
                        mListView?.let {
                            for(i in 0..mArrayPushInfo.size-1) {
                                if(!mArrayPushInfo.get(i).isRead)
                                    it.expandGroup(i)
                            }
                        }
                        mAdapter!!.notifyDataSetInvalidated()
                    }

                    if(mIsAutoReaded)
                        setPushMessageReaded()
                }
            }
        }

    }

}