package com.android.tgsmf.view.bottomtab


import android.widget.FrameLayout
import androidx.navigation.*
import com.android.tgsmf.data.TGSTabMenu
import com.android.tgsmf.fragment.TGSBaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


//val t = Class.forName(it.classDest)
//val new:TGSBaseFragment<*> = t.newInstance() as TGSBaseFragment<*>

/*********************************************************************
 *
 *
 *********************************************************************/
class TGSTabManager(
    private val _bottomNaviMenu: BottomNavigationView,
    private val _arrayMenuInfo: ArrayList<TGSTabMenu>,
    private val _startTabId: Int,
    private var _tabChangedListener: OnTabChangedListener? = null,
    private val _isUseHistory:Boolean = true
) {

    //-----------------------------------------------------------------
    companion object {
        private const val KEY_TAB_HISTORY = "key_tab_history"

        interface OnTabChangedListener {
            fun onTabChanged(tabId:Int, info:TGSTabMenu)
        }
    }

    //-----------------------------------------------------------------
    private var currentTabId: Int = 0
    private var tabHistory = TGSBottomTabHistory()//TGSBottomTabHistory().apply { push(_navStartNavId) }

    init {
        _bottomNaviMenu.setOnItemSelectedListener { item ->
            switchTab(item.itemId, _isUseHistory)
            true
        }
        // 메뉴 선택 시 컬러가 변경됨을 방지 (아이콘의 원래색으로 표현됨)
        _bottomNaviMenu.itemIconTintList = null

        switchTab(_startTabId, _isUseHistory)
    }

    //-----------------------------------------------------------------
    private fun getTabInfo(tabId:Int):TGSTabMenu? {
        for(info in _arrayMenuInfo) {
            if(tabId == info.menuId)
                return info
        }
        return null
    }



    //-----------------------------------------------------------------
    fun onBackPressed():Boolean {
        if(_isUseHistory && tabHistory.size > 1) {
            var tabId = tabHistory.popPrevious()
            _bottomNaviMenu.menu.findItem(tabId)?.isChecked = true

            switchTab(tabId, false)
            return true
        }
        return false
    }

    //-----------------------------------------------------------------
    fun switchTab(tabId: Int, addToHistory: Boolean = true):Boolean {
        println("cccccccccccccccccccccccccccccccccccccccccccccc")
        if(currentTabId == tabId)
            return false

        currentTabId = tabId

        _tabChangedListener?.let { _listener->
            val tabInfo = getTabInfo(tabId)
            tabInfo?.let {
                _listener.onTabChanged(tabId, it)
            }
        }
        if(addToHistory) {
            tabHistory.push(tabId)
        }
        return true
    }



}


