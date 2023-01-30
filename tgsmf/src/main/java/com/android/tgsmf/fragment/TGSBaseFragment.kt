package com.android.tgsmf.fragment

import android.content.ComponentCallbacks
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.data.TGSArgument
import com.android.tgsmf.data.TGSTitleType

/*********************************************************************
 *
 *
 *********************************************************************/
typealias Inflate<T> = (LayoutInflater) -> T
abstract class TGSBaseFragment<VB: ViewBinding>(private val _inflate: Inflate<VB>): Fragment() {
    protected open var TAG = javaClass.simpleName

    private var _binding: VB? = null
    val binding get() = _binding!!


    protected var mTitleType:Int = 0
    protected var mTitleName:String = ""
    protected var mLayoutTitlebar:ViewGroup? = null
    protected var mTextviewTitle:TextView? = null
    protected var mButtonTitleBack: ImageButton? = null
    protected var mButtonTitleClose:ImageButton? = null

    //-----------------------------------------------------------------
    // Fragment에서 Activity로 메시지 전달 리스너
    interface OnFragmentMsgListener {
        enum class TYPE(val value:Int) {SHOW_SIDE_MENU(1), SHOW_PUSH_LIST(2), BACK_PRESSED(3), MOVE_LOGIN(4)}
        fun onMessage(_type:Int, _message:Any?=null)
    }
    private var mFragmentMsgListener:OnFragmentMsgListener? = null
    protected fun onMsgToActivity(_type:Int, _message:Any?=null) {
        if(mFragmentMsgListener == null) return
        mFragmentMsgListener!!.onMessage(_type, _message)
    }

    //-----------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //-----------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = _inflate.invoke(layoutInflater)

        initView()
        initFont()
        initData()
        return binding.root
    }

    //-----------------------------------------------------------------
    protected open fun initView() {}
    protected open fun initFont() {}
    protected open fun initData() {
        if(arguments != null ) {
            requireArguments().getString(TGSArgument.TITLE_TYPE)?.let { mTitleType = it.toInt() }
            requireArguments().getString(TGSArgument.TITLE_NAME)?.let { mTitleName = it }
        }
        if(mTextviewTitle != null)
            mTextviewTitle!!.setText(mTitleName)

        mButtonTitleBack?.let {
            it.setOnClickListener { onClickBackButton() }
        }
        mButtonTitleClose?.let {
            it.setOnClickListener { onClickCloseButton() }
        }

        if(mTitleType != 0) {
            if(mTitleType and TGSTitleType.NONE.value > 0 ) {
                mLayoutTitlebar?.let { it.visibility = View.GONE }
            } else {
                mLayoutTitlebar?.let { it.visibility = View.VISIBLE }
                mTextviewTitle?.let { it.visibility = (if(mTitleType and TGSTitleType.TITLE.value > 0 ) {View.VISIBLE} else {View.GONE}) }
                mButtonTitleBack?.let { it.visibility = (if(mTitleType and TGSTitleType.BACK.value > 0 ) {View.VISIBLE} else {View.GONE}) }
                mButtonTitleClose?.let { it.visibility = (if(mTitleType and TGSTitleType.CLOSE.value > 0 ) {View.VISIBLE} else {View.GONE}) }
            }
        }
    }

    //-----------------------------------------------------------------
    // Back 버튼 처리 리스너
    open fun onBackPressed():Boolean {return false}
    open fun onClickBackButton() {
        onMsgToActivity(OnFragmentMsgListener.TYPE.BACK_PRESSED.value)
    }
    open fun onClickCloseButton() {
        onMsgToActivity(OnFragmentMsgListener.TYPE.BACK_PRESSED.value)
    }

    //-----------------------------------------------------------------
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(requireActivity() != null && requireActivity() is  OnFragmentMsgListener)
            mFragmentMsgListener = requireActivity() as OnFragmentMsgListener
    }

    //-----------------------------------------------------------------
    override fun onDetach() {
        super.onDetach()

    }

    //-----------------------------------------------------------------
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}