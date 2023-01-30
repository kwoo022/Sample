package com.android.tgsmf.fragment


import android.view.View
import com.android.tgsmf.R
import com.android.tgsmf.database.TGSDatabase
import com.android.tgsmf.databinding.TgsFragmentWebHomeBinding
import com.android.tgsmf.fcm.push.TGSFireBaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*********************************************************************
 *
 *
 *********************************************************************/
class  TGSWebHomeFragment() :TGSBaseWebFragment<TgsFragmentWebHomeBinding>(TgsFragmentWebHomeBinding::inflate) {

    //-----------------------------------------------------------------
    override fun initView() {
        mWebview = binding.tgsWebviewWebHome

        binding.tgsButtonWebHomeMenu.setOnClickListener {
            onMsgToActivity(OnFragmentMsgListener.TYPE.SHOW_SIDE_MENU.value)
        }
        binding.tgsButtonWebHomeNoti.setOnClickListener {
            onMsgToActivity(OnFragmentMsgListener.TYPE.SHOW_PUSH_LIST.value)
        }
    }

    //-----------------------------------------------------------------
    override fun initData() {
        super.initData()
    }

    //-----------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        if(TGSFireBaseMessagingService.IS_USING_PUSH()) {
            binding.tgsButtonWebHomeNoti.visibility = View.VISIBLE
            updatePushNotReadIcon()
        } else {
            binding.tgsButtonWebHomeNoti.visibility = View.GONE
        }
    }

    //-----------------------------------------------------------------
    // 읽지 않은 푸시메시지가 있을 경우 푸시알림 아이콘을 변경한다.
    open fun updatePushNotReadIcon() {
        CoroutineScope(Dispatchers.IO).launch {
            var db = TGSDatabase.getInstance(requireContext())
            db?.let { database ->
                var notReadCnt = database.pushMessageDao().getAllCountByRead(0)
                requireActivity().runOnUiThread {
                    if(notReadCnt > 0) {
                        binding.tgsButtonWebHomeNoti.setImageResource(R.drawable.alarm1on_ic1)
                    } else {
                        binding.tgsButtonWebHomeNoti.setImageResource(R.drawable.alarm1_ic1)
                    }
                }
            }
        }
    }

}