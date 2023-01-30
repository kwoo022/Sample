package com.android.tgsmf.view.bottomtab


import android.os.Bundle
import androidx.navigation.*
import com.android.tgsmf.data.TGSNaviMenu
import com.google.android.material.bottomnavigation.BottomNavigationView


/*********************************************************************
 *
 *
 *********************************************************************/
class TGSBottomTabManager(private val _bottomNaviMenu: BottomNavigationView,
                          private val _naviController: NavController,
                          private val _navGraphResId: Int,
                          private val _arrayMenuInfo:ArrayList<TGSNaviMenu>,
                          private val _navStartNavId : Int,
                          private var _tabChangedListener:OnTabChangedListener? = null) {

    //-----------------------------------------------------------------
    companion object {
        private const val KEY_TAB_HISTORY = "key_tab_history"

        interface OnTabChangedListener {
            fun onTabChanged(controller: NavController, tabId:Int)
        }
    }

    //-----------------------------------------------------------------
    private var currentTabId: Int = 0
    private var tabHistory = TGSBottomTabHistory()//TGSBottomTabHistory().apply { push(_navStartNavId) }

    //-----------------------------------------------------------------
    val navController: NavController by lazy {
        _naviController.apply {
            graph = navInflater.inflate(_navGraphResId).apply {
                getBottomNavInfo(_navStartNavId)?.let {
                    if(it.defaultArg != null) {
                        for(agr in it.defaultArg) {
                            this.addArgument(agr.key, NavArgument.Builder().setDefaultValue(agr.value).build())
                        }
                    }
                }
                this.setStartDestination(_navStartNavId)
            }

            addOnDestinationChangedListener(object:NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    if(_tabChangedListener != null) {
                        _tabChangedListener!!.onTabChanged(controller, destination.id)
                    }
                }
            })

        }

    }

    //-----------------------------------------------------------------
    fun getBottomNavInfo(navId:Int):TGSNaviMenu? {
        for(info in _arrayMenuInfo) {
            if(navId == info.id)
                return info
        }
        return null
    }



    //-----------------------------------------------------------------
    fun onBackPressed():Int {
        navController?.let {
            if (tabHistory.size > 1) {
                it.popBackStack()
                val tabId = tabHistory.popPrevious()
                _bottomNaviMenu.menu.findItem(tabId)?.isChecked = true
                currentTabId = tabId
                return tabId
            }
        }
        return -1
    }

    //-----------------------------------------------------------------
    fun switchTab(tabId: Int, addToHistory: Boolean = true):Boolean {
        if(currentTabId == tabId)
            return false

        currentTabId = tabId

        getBottomNavInfo(tabId)?.let {
            val action = it.funDirections
            navController?.let {
                it.navigate(action)
            }
        }

        if (addToHistory) {
            tabHistory.push(tabId)
        }
        return true
    }



}


