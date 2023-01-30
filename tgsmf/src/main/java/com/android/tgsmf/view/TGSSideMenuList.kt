package com.android.tgsmf.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSMenu
import com.android.tgsmf.databinding.TgsViewMenuListBinding
import com.android.tgsmf.util.TGSLog
import com.android.tgsmf.view.adapter.TGSMenuAdapter


/*********************************************************************
 *
 *
 *********************************************************************/
class TGSSideMenuList : LinearLayout {
    val TAG = javaClass.simpleName

    private lateinit var mArrayMenu: ArrayList<TGSMenu>
    private lateinit var mListAdapter : TGSMenuAdapter
    private lateinit var mList: ExpandableListView

    private var mClickListener:OnClickListener? = null
    interface OnClickListener {
        fun onSelectMenu(_clickEvent:String)
        fun onCloseMenu()
    }

    //-----------------------------------------------------------------
    constructor(context: Context): super(context) {
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView(context)
        getAttrs(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs) {
        initView(context)
        getAttrs(attrs, defStyle)
    }


    //-----------------------------------------------------------------
    private fun initView(context:Context){
        val binding = TgsViewMenuListBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

//        val view = LayoutInflater.from(context).inflate(R.layout.tgs_view_menu_list,this,false)
//        addView(view)

        var buttonClose : ImageButton = binding.tgsButtonMenuClose
        buttonClose.setOnClickListener {
            mClickListener?.let {
                println("111111111111111111111111111111111111111111111111111")
                it.onCloseMenu()
            }
        }

        mList = binding.tgsListviewMenu

        mArrayMenu = arrayListOf()
        mListAdapter = TGSMenuAdapter(context, mArrayMenu, mMenuListClickListener)
        mList.setAdapter(mListAdapter)
    }

    //-----------------------------------------------------------------
    private fun getAttrs(attrs:AttributeSet){
        //아까 만들어뒀던 속성 attrs 를 참조함
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.TGSMF_MENU_LIST)
        setTypeArray(typedArray)
    }
    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TGSMF_MENU_LIST, defStyle, 0)
        setTypeArray(typedArray)
    }

    //-----------------------------------------------------------------
    //디폴트 설정
    private fun setTypeArray(typedArray : TypedArray){
        //레이아웃의 배경, LoginButton 이름으로 만든 attrs.xml 속성중 bgColor 를 참조함
        val bgResId = typedArray.getColor(R.styleable.TGSMF_MENU_LIST_bg, 0)
        this.setBackgroundColor(bgResId)
//
//        //이미지의 배경, LoginButton 이름으로 만든 attrs.xml 속성중 imgColor 를 참조함
//        val imgResId = typedArray.getResourceId(R.styleable.LoginButton_imgColor,R.drawable.ic_launcher_foreground)
//        imgIcon.setBackgroundResource(imgResId)
//
//        //텍스트 색, LoginButton 이름으로 만든 attrs.xml 속성중 textColor 를 참조함
//        val textColor = typedArray.getColor(R.styleable.LoginButton_textColor,0)
//        tvName.setTextColor(textColor)
//
//        //텍스트 내용, LoginButton 이름으로 만든 attrs.xml 속성중 text 를 참조함
//        val text = typedArray.getText(R.styleable.LoginButton_text)
//        tvName.text = text

        typedArray.recycle()
    }


    //-----------------------------------------------------------------
    // 메뉴 리스트 정보 입력
    fun setMenuInfo(menuList: MutableList<TGSMenu>) {
        mArrayMenu.clear()
        mArrayMenu.addAll(menuList)
        mListAdapter.notifyDataSetInvalidated()

        for(i in 0..(mListAdapter.groupCount-1)) {
            mList.expandGroup(i)
        }
    }
    //-----------------------------------------------------------------
    fun isEmptyMenu() : Boolean {
        return mArrayMenu.isEmpty()
    }
    //-----------------------------------------------------------------
    private val mMenuListClickListener = object: TGSMenuAdapter.OnClickListener {
        override fun onClick(menuName: String, clickEvent: String) {
            TGSLog.d("[${TAG}_onClick] ${menuName} : ${clickEvent}")

            mClickListener?.let {
                it.onSelectMenu(clickEvent)
            }
        }

    }

    //-----------------------------------------------------------------
    fun setOnClickListener(_lisener:OnClickListener) {
        mClickListener = _lisener
    }


}