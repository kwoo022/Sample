package com.android.tgsmf.activity.base

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSNaviMenu
import com.android.tgsmf.data.TGSTabMenu
import com.android.tgsmf.data.TGSWebCommand
import com.android.tgsmf.databinding.TgsActivityNaviWebHomeBinding
import com.android.tgsmf.fragment.TGSBaseFragment
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.util.TGSToast
import com.android.tgsmf.view.TGSSideMenuList
import com.android.tgsmf.view.bottomtab.TGSBottomTabManager
import com.android.tgsmf.view.bottomtab.TGSTabManager
import com.android.tgsmf.view.webview.TGSJavascriptListener
import com.google.android.material.bottomnavigation.BottomNavigationView

/*********************************************************************
 *
 *
 *********************************************************************/
abstract class TGSBaseNaviWebActivity<VB: ViewBinding>(private val _inflate: Inflate<VB>) : TGSBaseActivity<VB>(_inflate), TGSBaseFragment.OnFragmentMsgListener {
    override var TAG = javaClass.simpleName

    // 하단탭 정보
    protected var mBottomNavigationView: BottomNavigationView? = null
    //protected var mFragmentView: FrameLayout? = null
    protected var mFragmentViewId: Int? = null
    protected var mArrayBottomNavInfo:ArrayList<TGSTabMenu>? = null
    protected var mBottomNavStartTabId: Int? = null

    protected var mBottomTabManager: TGSTabManager?  = null

    //protected var mBottomNaviController: NavController? = null
    //protected var mArrayBottomNavInfo:ArrayList<TGSNaviMenu>? = null
    //protected var mBottomNavGraphResId: Int? = null


    // 사이드 메뉴 정보
    protected var mDrawerToggle: ActionBarDrawerToggle? = null
    protected var mDrawerLayout: DrawerLayout? = null
    protected var mSideMenuView : TGSSideMenuList? = null

    //-----------------------------------------------------------------
    // 현재 보여지고 있는 Fragment
    protected var mCurrFragment : TGSBaseFragment<*>? = null
    protected var mScriptListener: TGSJavascriptListener? = object : TGSJavascriptListener {
        override fun onMessage(msg: String) {
            TGSLog.d("[${TAG}_TGSJavascriptListener] showMessage : ${msg}" )
            var command : Int? = TGSWebCommand.convertWebCommand(msg)
            if(command != null) {
                runOnUiThread(Runnable {
                    var mapParams = TGSWebCommand.convertWebCommandUrlParam(msg)
                    onWebCommand(command, mapParams)
                })
            }
        }
        override fun onTitle(title: String) {
//            if(mTitle == null || mTitle!!.isEmpty()) {
//                mTitle = title
//            }
        }
    }
    fun setJavascriptListener(scriptListener: TGSJavascriptListener) {
        mScriptListener = scriptListener
    }


    //-----------------------------------------------------------------
    override fun onStart() {
        super.onStart()
        initBottomNavInfo()
        initSideMenuInfo()

        initBottomNav()
        initSideMenu()

        if(mBottomNavigationView!=null && mBottomNavStartTabId!=null)
            mBottomNavigationView!!.selectedItemId =  mBottomNavStartTabId!!
    }

    private fun initBottomNav() {
        if(mBottomNavigationView == null || mFragmentViewId == null || mArrayBottomNavInfo==null
            || mBottomNavStartTabId== null)
            return

        mBottomTabManager = TGSTabManager(
            mBottomNavigationView!!,
            mArrayBottomNavInfo!!,
            mBottomNavStartTabId!!,
            object : TGSTabManager.Companion.OnTabChangedListener {
                override fun onTabChanged(tabId: Int, info: TGSTabMenu) {
                    if(mFragmentViewId != null) {
                        val temp = Class.forName(info.classDest)
                        val newFragment:TGSBaseFragment<*> = temp.newInstance() as TGSBaseFragment<*>
                        var bundle = Bundle()
                        info.defaultArg?.let {
                            for((key, value) in it)
                                bundle.putString(key, value)
                            newFragment.arguments = bundle
                        }
                        bundle.putParcelable(TGSArgument.JAVASCRIPT_LISTENER, mScriptListener)
                        mCurrFragment = newFragment
                        println("onTabChanged::::::::::::::"+newFragment.toString())
                        supportFragmentManager.beginTransaction().replace(mFragmentViewId!!, newFragment).commit()
                    }
                }
            },
            true
        )
    }
    protected fun setBottomNavMenu(_menuResId : Int) {
        if(mBottomNavigationView == null)
            return

        mBottomNavigationView!!.menu.clear()
        mBottomNavigationView!!.inflateMenu(_menuResId)
    }
    //-----------------------------------------------------------------
    // 하단네비게이션 정보를 세팅한다.
    abstract fun initBottomNavInfo()
    //-----------------------------------------------------------------
    // 사이드 메뉴 정보를 세팅한다.
    abstract fun initSideMenuInfo()
    private fun initSideMenu() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(mDrawerLayout == null || mSideMenuView == null)
            return

        mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout!!,
            com.android.tgsmf.R.string.app_name,com.android.tgsmf.R.string.app_name
        )
        mDrawerToggle?.let { _drawerToggle->
            mDrawerLayout!!.addDrawerListener(_drawerToggle)
            _drawerToggle.syncState()
        }

        mSideMenuView?.let { _sideMenuView->
            _sideMenuView.setOnClickListener(object : TGSSideMenuList.OnClickListener {
                override fun onSelectMenu(_clickEvent:String) {
                    TGSLog.d("[${TAG}_SideMenuClick] onSelectMenu - ${_clickEvent}" )
                    var command : Int? = TGSWebCommand.convertWebCommand(_clickEvent)
                    if(command != null) {
                        var mapParams = TGSWebCommand.convertWebCommandUrlParam(_clickEvent)
                        onWebCommand(command, mapParams)
                    }

                    closeSideMenu()
                }

                override fun onCloseMenu() {
                    TGSLog.d("[${TAG}_SideMenuClick] onCloseMenu" )
                    closeSideMenu()
                }
            })
        }
    }

    //-----------------------------------------------------------------
    protected open fun onWebCommand(command:Int, mapParams:HashMap<String,String>?=null) {
        TGSLog.i("[${TAG} _onCommand] command : ${command}, params : ${mapParams.toString()}")
        TGSWebCommand.onBasicCommand(this, null, command, mapParams)
    }

    //-----------------------------------------------------------------
    // 사이드 메뉴 오픈
    protected fun openSideMenu() {
        TGSLog.d("[TGSNaviWebHomeActivity_openSideMenu]")
        if( mDrawerLayout == null || mSideMenuView == null)
            return

        mSideMenuView?.let {
            if(it.isEmptyMenu()) {
                TGSToast.show(this, getString(R.string.side_menu_not_using))
                return
            }
        }

        if(!mDrawerLayout!!.isOpen) {
            mDrawerLayout!!.openDrawer(mSideMenuView!!)
        }
    }
    //-----------------------------------------------------------------
    // 사이드 메뉴 닫기
    protected fun closeSideMenu() {
        TGSLog.d("[TGSNaviWebHomeActivity_closeSideMenu]")
        println("3333333333::::::::::::::"+mDrawerLayout)
        println("4444444444::::::::::::::"+mSideMenuView)
        if( mDrawerLayout == null || mSideMenuView == null)
            return
        if(mDrawerLayout!!.isOpen) {
            mDrawerLayout!!.closeDrawer(mSideMenuView!!)
        }
    }

    //-----------------------------------------------------------------
    //사이드 메뉴가 열려 있을 경우 사이드 메뉴를 닫는다.
    protected fun onBackSideMenu() :Boolean  {
        if(mDrawerLayout != null) {
            if(mDrawerLayout!!.isOpen) {
                closeSideMenu()
                return true
            }
        }
        return false
    }
    //-----------------------------------------------------------------
    // 프레그먼트 내에서 Back 버튼 처리를 한다.
    protected fun onBackFragment() :Boolean {
        if(mCurrFragment != null) {
            if(mCurrFragment!!.onBackPressed())
                return true
        }
        return false
    }
    //-----------------------------------------------------------------
    // Tab History에 쌓인 데이터가 있을 경우
    protected fun onBackTabHistory() :Boolean {
        if(mBottomTabManager != null) {
            if(mBottomTabManager!!.onBackPressed())
                return true
        }
        return false
    }

    //-----------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()

        // 1. 사이드 메뉴가 열려 있을 경우 사이드 메뉴를 닫는다.
        if(onBackSideMenu())
            return

        // 2. 프레그먼트 내에서 Back 버튼 처리를 한다.
        if(onBackFragment())
            return

        // 3. 프레그먼트 스택이 쌓여 있을 경우 프레그먼트를 닫는다.
        if(onBackTabHistory())
            return
    }


    //-----------------------------------------------------------------
    protected fun getCurrFragment(): TGSBaseFragment<*>? {
        val navHostFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.tgs_fragment_navi_home)
        if(navHostFragment != null && navHostFragment?.childFragmentManager != null
            && navHostFragment?.childFragmentManager?.fragments != null && navHostFragment?.childFragmentManager?.fragments.size > 0)
            return navHostFragment?.childFragmentManager?.fragments?.get(0) as TGSBaseFragment<*>
        return null
    }

}