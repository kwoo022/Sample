package com.android.tgsmf.view.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.android.tgsmf.R
import com.android.tgsmf.activity.base.Inflate
import com.android.tgsmf.data.TGSPushMessage
import com.android.tgsmf.databinding.TgsLayoutListItemPushChildBinding
import com.android.tgsmf.databinding.TgsLayoutListItemPushParentBinding
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont


class TGSPushMessageAdapter(private val context: Context, private val arrayPushList: MutableList<TGSPushMessage>)
    : BaseExpandableListAdapter() {

    private var mlayoutParentId:Int = R.layout.tgs_layout_list_item_push_parent
    private var mlayoutChildId:Int = R.layout.tgs_layout_list_item_push_child

    //-----------------------------------------------------------------
    fun setItemLayoutId(parentId:Int, childId:Int) {
        mlayoutParentId = parentId
        mlayoutChildId = childId
    }

    inner class ParentViewHolder(_view: View) {
        val layoutItem: ConstraintLayout
        val imageIcon: ImageView
        val textviewTitle: TextView
        //val buttonExpand: ImageButton

        init {
            layoutItem = _view.findViewById(R.id.tgs_layout_list_item_push_parent) //    _binding.tgsLayoutListItemPushParent
            imageIcon = _view.findViewById(R.id.tgs_image_list_item_push_parent_icon) // _binding.tgsImageListItemPushParentIcon
            textviewTitle = _view.findViewById(R.id.tgs_textview_list_item_push_parent)   // _binding.tgsTextviewListItemPushParent

            TGSFont.setFont(FONT_TYPE.NOTO_MEDIUM, textviewTitle)
        }
    }

    inner class ChildViewHolder(_view: View) {
        val textviewMessage: TextView

        init {
            textviewMessage = _view.findViewById(R.id.tgs_textview_list_item_push_child) //_binding.tgsTextviewListItemPushChild

            TGSFont.setFont(FONT_TYPE.NOTO_REGULAR, textviewMessage)
        }
    }


    //-----------------------------------------------------------------
    override fun getGroupCount(): Int = arrayPushList.size
    //-----------------------------------------------------------------
    override fun getChildrenCount(groupPosition: Int): Int = 1
    //-----------------------------------------------------------------
    override fun getGroup(groupPosition: Int): TGSPushMessage {
        return  arrayPushList.get(groupPosition)
    }
    //-----------------------------------------------------------------
    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return  arrayPushList.get(groupPosition).message
    }
    //-----------------------------------------------------------------
    override fun getGroupId(groupPosition: Int): Long  = groupPosition.toLong()
    //-----------------------------------------------------------------
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()
    //-----------------------------------------------------------------
    override fun hasStableIds(): Boolean = true

    //-----------------------------------------------------------------
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        var viewRoot = convertView
        var viewHolder:ParentViewHolder? = null

        if(viewRoot == null) {
            var inflater = LayoutInflater.from(context)
            viewRoot = inflater.inflate(mlayoutParentId, null, false)

//            val binding = TgsLayoutListItemPushParentBinding.inflate(LayoutInflater.from(context))
//            viewRoot = binding.root

            viewHolder = ParentViewHolder(viewRoot)
            viewRoot.tag = viewHolder

        } else {
            viewHolder = viewRoot.tag as ParentViewHolder
        }

        var pushInfo:TGSPushMessage = getGroup(groupPosition)
        if(viewHolder != null) {
            viewHolder.textviewTitle.setText(pushInfo.title)
        }

        return viewRoot!!
    }
    //-----------------------------------------------------------------
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        var viewRoot = convertView
        var viewHolder:ChildViewHolder? = null

        if(viewRoot == null) {
            var inflater = LayoutInflater.from(context)
            viewRoot = inflater.inflate(mlayoutChildId, null, false)

//            val binding = TgsLayoutListItemPushChildBinding.inflate(LayoutInflater.from(context))
//            viewRoot = binding.root

            viewHolder = ChildViewHolder(viewRoot)
            viewRoot.tag = viewHolder

        } else {
            viewHolder = viewRoot.tag as ChildViewHolder
        }

        var message:String = getChild(groupPosition, childPosition)
        if(viewHolder != null) {
            viewHolder.textviewMessage.setText(message)
        }

        return viewRoot!!
    }
    //-----------------------------------------------------------------
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

}