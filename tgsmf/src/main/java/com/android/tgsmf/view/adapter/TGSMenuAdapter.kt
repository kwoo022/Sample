package com.android.tgsmf.view.adapter


import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.tgsmf.R
import com.android.tgsmf.data.TGSMenu
import com.android.tgsmf.databinding.*
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont


class TGSMenuAdapter(private val context: Context, private val itemList: MutableList<TGSMenu>, private val onClickListener:OnClickListener? = null): BaseExpandableListAdapter(){


    interface OnClickListener {
        fun onClick(menuName:String, clickEvent:String)
    }

    //---------------------------------------------------------------
    data class ViewHolderLv1Item (
        var layoutItem: ConstraintLayout,
        var textviewName: TextView,
        var imageArrow: ImageView
    )
    data class ViewHolderLv2Item (
        var layoutItem: ConstraintLayout,
        var textviewName: TextView,
        var imageArrow: ImageView
    )
    data class ViewHolderLv3Item (
        var layoutItem: LinearLayout,
        var imageIcon: ImageView,
        var textviewName: TextView,
        var viewHeader: View,
        var viewFooter: View

    )


    //---------------------------------------------------------------
    override fun getGroupCount(): Int = itemList.size
    override fun getGroup(groupPosition: Int): TGSMenu = itemList[groupPosition]
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {

        var viewRoot = convertView
        var viewHolder:ViewHolderLv1Item? = null

        if(viewRoot == null || viewRoot.tag == null || !(viewRoot.tag is ViewHolderLv1Item)) {
            val binding = TgsViewMenuLv1ItemBinding.inflate(LayoutInflater.from(context), parent, false)
            viewRoot = binding.root
            viewHolder =  ViewHolderLv1Item(binding.tgsLayoutMenuGroupItem, binding.tgsTextviewMenuGroupItemName, binding.tgsImageMenuGroupItemArrow)
            viewRoot.tag = viewHolder

        } else {
            viewHolder = viewRoot.tag as ViewHolderLv1Item
        }

        var groupInfo : TGSMenu = getGroup(groupPosition)
        viewHolder.textviewName.text = groupInfo.menuName

        if(isExpanded) {
            viewHolder.imageArrow.setImageResource(R.drawable.gnb_d1on_ic1)
            viewHolder.textviewName.setTextColor(context.getColor(R.color.tgsfw_side_menu_step1_on_text))
            TGSFont.setFont(FONT_TYPE.GMARKET_BOLD, viewHolder.textviewName!!)
        } else {
            viewHolder.imageArrow.setImageResource(R.drawable.gnb_d1_ic1)
            viewHolder.textviewName!!.setTextColor(context.getColor(R.color.tgsfw_side_menu_step1_off_text))
            TGSFont.setFont(FONT_TYPE.GMARKET_MEDIUM, viewHolder.textviewName!!)
        }
        return viewRoot
    }

    //---------------------------------------------------------------
    override fun getChildrenCount(groupPosition: Int): Int  {
        var groupInfo :TGSMenu = getGroup(groupPosition)
        groupInfo.arraySub?.let { return  it.size}
        return 0
    }
    override fun getChild(groupPosition: Int, childPosition: Int): TGSMenu? {
        var groupInfo :TGSMenu = getGroup(groupPosition)

        var index:Int = 0
        groupInfo.arraySub?.let {
            for(menuInfo in it) {
                if(index == childPosition)
                    return menuInfo
                ++index

                if(menuInfo.arraySub != null) {
                    for(subMenuInfo in menuInfo.arraySub) {
                        if(index == childPosition)
                            return subMenuInfo
                        ++index
                    }
                }
            }
        }
        return null
    }
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {

        var childMenuInfo : TGSMenu? = getChild(groupPosition, childPosition)
        if(childMenuInfo != null && childMenuInfo.arraySub != null && childMenuInfo.arraySub!!.size > 0) {

            val listviewSub = TGSSubMenuExpandableListView(context)
            listviewSub.setAdapter(TGSSubMenuAdapter(context, childMenuInfo, (childPosition==0), isLastChild))
            listviewSub.setGroupIndicator(null)
            listviewSub.isNestedScrollingEnabled = true
            listviewSub.divider = null
            listviewSub.expandGroup(0)


            listviewSub.setOnGroupExpandListener ( object : ExpandableListView.OnGroupExpandListener {
                var previousGroup = -1
                override fun onGroupExpand(groupPosition: Int) {
                    if(groupPosition != previousGroup)
                        listviewSub.collapseGroup(previousGroup)
                    previousGroup = groupPosition
                }
            })
            return listviewSub

        } else {

            var viewRoot = convertView
            var viewHolder:ViewHolderLv2Item? = null

            if(viewRoot == null || viewRoot.tag == null || !(viewRoot.tag is ViewHolderLv2Item)) {
                val binding = TgsViewMenuLv2ItemBinding.inflate(LayoutInflater.from(context), parent, false)
                viewRoot = binding.root
                viewHolder =  ViewHolderLv2Item(binding.tgsLayoutMenuItem, binding.tgsTextviewMenuItemName, binding.tgsImageMenuItemArrow)
                viewRoot.tag = viewHolder

            } else {
                viewHolder = viewRoot.tag as ViewHolderLv2Item
            }

            // header, footer margin 적용
            var paddingTop = 0
            var paddingBottom = 0
            if(childPosition == 0)
                paddingTop = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()
            if(isLastChild)
                paddingBottom = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()
            viewHolder.layoutItem!!.setPadding(0, paddingTop, 0, paddingBottom)


            var menuInfo : TGSMenu? = getChild(groupPosition, childPosition)
            menuInfo?.let {
                TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, viewHolder.textviewName!!)
                viewHolder.textviewName!!.text = it.menuName
                viewHolder.imageArrow!!.visibility = View.GONE

                onClickListener?.let{ listener:OnClickListener ->
                    viewHolder.layoutItem!!.tag = menuInfo
                    viewHolder.layoutItem!!.setOnClickListener {
                        println("123123123123123")
                        println("111111111111111")
                        var info : TGSMenu? = it.tag as TGSMenu?
                        if(info != null)
                            info.clickEvent?.let { it1 -> listener.onClick(info.menuName, it1) }
                    }
                }
            }
            return viewRoot
        }
    }

    //---------------------------------------------------------------
    override fun hasStableIds(): Boolean = false
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true



    /*********************************************************************
     *
     *
     *********************************************************************/
    inner class TGSSubMenuExpandableListView(_context: Context?) : ExpandableListView(_context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
            var heightMeasureSpec = heightMeasureSpec
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
    /*********************************************************************
     *
     *
     *********************************************************************/
    inner class TGSSubMenuAdapter(private val context: Context, private val subItemInfo: TGSMenu,
                                  private val isFirstPos:Boolean =false, private val isLastPos:Boolean =false): BaseExpandableListAdapter(){

        //---------------------------------------------------------------
        override fun getGroup(groupPosition: Int): TGSMenu {return subItemInfo }
        override fun getGroupCount(): Int = 1
        override fun getGroupId(groupPosition: Int): Long { return groupPosition.toLong() }
        override fun getGroupView(
            groupPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            parent: ViewGroup?,
        ): View {
            var viewRoot = convertView
            var viewHolder:ViewHolderLv2Item? = null

            if(viewRoot == null || viewRoot.tag == null || !(viewRoot.tag is ViewHolderLv2Item)) {
                val binding = TgsViewMenuLv2ItemBinding.inflate(LayoutInflater.from(context), parent, false)
                viewRoot = binding.root
                viewHolder =  ViewHolderLv2Item(binding.tgsLayoutMenuItem, binding.tgsTextviewMenuItemName, binding.tgsImageMenuItemArrow)
                viewRoot.tag = viewHolder

            } else {
                viewHolder = viewRoot.tag as ViewHolderLv2Item
            }

            // header, footer margin 적용
            var paddingTop = 0
            var paddingBottom = 0
            if(isFirstPos)
                paddingTop = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()
            if(isLastPos)
                paddingBottom = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()
            viewHolder.layoutItem!!.setPadding(0, paddingTop, 0, paddingBottom)

            var menuInfo : TGSMenu? = getGroup(groupPosition)
            menuInfo?.let {
                viewHolder.textviewName!!.text = it.menuName

                if(isExpanded) {
                    TGSFont.setFont(FONT_TYPE.NOTO_BOLD, viewHolder.textviewName)
                    viewHolder.textviewName.setTextColor(context.getColor(R.color.tgsfw_side_menu_step2_on_text))
                    viewHolder.imageArrow.setImageResource(R.drawable.gnb_d2on_ic1)
                } else {
                    TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, viewHolder.textviewName)
                    viewHolder.textviewName.setTextColor(context.getColor(R.color.tgsfw_side_menu_step2_off_text))
                    viewHolder.imageArrow.setImageResource(R.drawable.gnb_d2_ic1)
                }
            }

            return viewRoot
        }

        //---------------------------------------------------------------
        override fun getChildrenCount(groupPosition: Int): Int {
            return subItemInfo.arraySub?.let { it.size } ?: run { 0 }
        }
        override fun getChild(groupPosition: Int, childPosition: Int): TGSMenu {
            return subItemInfo.arraySub!!.get(childPosition)
        }
        override fun getChildId(groupPosition: Int, childPosition: Int): Long {return childPosition.toLong()}
        override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup?,
        ): View {
            var viewRoot = convertView
            var viewHolder:ViewHolderLv3Item? = null

            if(viewRoot == null || viewRoot.tag == null || !(viewRoot.tag is ViewHolderLv3Item)) {
                val binding = TgsViewMenuLv3ItemBinding.inflate(LayoutInflater.from(context), parent, false)
                viewRoot = binding.root
                viewHolder =  ViewHolderLv3Item(binding.tgsLayoutMenuItem, binding.tgsImageMenuItemIcon, binding.tgsTextviewMenuItemName
                , binding.tgsViewMenuItemHeader, binding.tgsViewMenuItemFooter)
                viewRoot.tag = viewHolder

            } else {
                viewHolder = viewRoot.tag as ViewHolderLv3Item
            }

            var menuInfo = getChild(groupPosition, childPosition)
            viewHolder.textviewName.setText(menuInfo.menuName)
            TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, viewHolder.textviewName)

            viewHolder.viewHeader.visibility = (if(childPosition == 0) View.VISIBLE else View.GONE)
            viewHolder.viewFooter.visibility = (if(isLastChild) View.VISIBLE else View.GONE)

            return viewRoot
        }

        //---------------------------------------------------------------
        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
        override fun hasStableIds(): Boolean  = true


    }
}