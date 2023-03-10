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
        //?????? ??????????????? ?????? attrs ??? ?????????
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.TGSMF_MENU_LIST)
        setTypeArray(typedArray)
    }
    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TGSMF_MENU_LIST, defStyle, 0)
        setTypeArray(typedArray)
    }

    //-----------------------------------------------------------------
    //????????? ??????
    private fun setTypeArray(typedArray : TypedArray){
        //??????????????? ??????, LoginButton ???????????? ?????? attrs.xml ????????? bgColor ??? ?????????
        val bgResId = typedArray.getColor(R.styleable.TGSMF_MENU_LIST_bg, 0)
        this.setBackgroundColor(bgResId)
//
//        //???????????? ??????, LoginButton ???????????? ?????? attrs.xml ????????? imgColor ??? ?????????
//        val imgResId = typedArray.getResourceId(R.styleable.LoginButton_imgColor,R.drawable.ic_launcher_foreground)
//        imgIcon.setBackgroundResource(imgResId)
//
//        //????????? ???, LoginButton ???????????? ?????? attrs.xml ????????? textColor ??? ?????????
//        val textColor = typedArray.getColor(R.styleable.LoginButton_textColor,0)
//        tvName.setTextColor(textColor)
//
//        //????????? ??????, LoginButton ???????????? ?????? attrs.xml ????????? text ??? ?????????
//        val text = typedArray.getText(R.styleable.LoginButton_text)
//        tvName.text = text

        typedArray.recycle()
    }


    //-----------------------------------------------------------------
    // ?????? ????????? ?????? ??????
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