package com.android.tgsmf.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.tgsmf.activity.TGSPictureLocationUploadActivity
import com.android.tgsmf.activity.base.TGSBasePictureLocationUploadActivity
import com.android.tgsmf.data.TGSPushMessage
import com.android.tgsmf.databinding.TgsLayoutListItemPushChildBinding
import com.android.tgsmf.databinding.TgsLayoutListItemPushParentBinding
import com.android.tgsmf.databinding.TgsLayoutListItemSelectPictureBinding
import com.android.tgsmf.util.FONT_TYPE
import com.android.tgsmf.util.TGSFont


class TGSPictureSelectAdapter(
                private val context: Context,
                private val arrayPictureList: MutableList<TGSBasePictureLocationUploadActivity.TGSPictureInfo>,
                private val listener:OnClickListener? = null)
    : RecyclerView.Adapter<TGSPictureSelectAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onDelete(position: Int)
    }

    //---------------------------------------------------------------------------------
    inner class ViewHolder(_binding: TgsLayoutListItemSelectPictureBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        val imagePicture: ImageView
        val buttonDelete: ImageButton

        init {
            imagePicture = _binding.tgsImageListItemSelectPicture
            buttonDelete = _binding.tgsButtonListItemSelectPictureDelete
        }
    }

    //---------------------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TgsLayoutListItemSelectPictureBinding.inflate(LayoutInflater.from(context), parent, false)

        val viewHolder = ViewHolder(binding)
        viewHolder.buttonDelete.setOnClickListener(mDeleteClickListener)
        return viewHolder
    }

    //---------------------------------------------------------------------------------
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: TGSBasePictureLocationUploadActivity.TGSPictureInfo = arrayPictureList.get(position)

        holder.imagePicture.setImageURI(item.uri)
        holder.buttonDelete.tag = position

    }

    //---------------------------------------------------------------------------------
    override fun getItemCount(): Int =arrayPictureList.size

    //---------------------------------------------------------------------------------
    private val mDeleteClickListener = View.OnClickListener {
        val position = it.tag as Int

        if(listener != null)
            listener.onDelete(position)
    }
}