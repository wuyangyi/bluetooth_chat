package com.zz.bluetooth_chat.widget.adapter

import android.content.Context
import android.view.View
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.base_adapter.CommonAdapter
import com.zz.bluetooth_chat.base.base_adapter.ViewHolder
import com.zz.bluetooth_chat.bean.BottomListSelectBean

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 底部列表选择的adapter
 */

class BottomListSelectAdapter : CommonAdapter<BottomListSelectBean> {

    private var mEducationSelectClick: BottomListSelectClick? = null

    constructor(context: Context, layoutId: Int, datas: ArrayList<BottomListSelectBean>): super(context, layoutId, datas)

    override fun convert(holder: ViewHolder, data: BottomListSelectBean, position: Int) {
        holder.getView<View>(R.id.v_driver).visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.getImageViwe(R.id.iv_select).setImageDrawable(
            if (data.isSelect)
                getContext().resources.getDrawable(R.mipmap.ico_selected)
            else
                getContext().resources.getDrawable(R.mipmap.ico_no_select)
        )
        holder.setText(R.id.tv_edu, data.title ?: "")
        holder.getTextView(R.id.tv_edu).setTextColor(
            getContext().resources.getColor(
                if (data.isSelect)
                    R.color.select_color
                else
                    R.color.title_color
            )
        )
        holder.getView<View>(R.id.rv_parent).setOnClickListener {
            initSelect(position)
            if (mEducationSelectClick != null) {
                mEducationSelectClick!!.onBottomListSelectClickListener(position)
            }
        }
    }

    //设置选中的item
    private fun initSelect(position: Int) {
        for (i in 0 until mDatas.size) {
            mDatas[i].isSelect = (position == i)
        }
    }

    fun setEducationSelectClick(mEducationSelectClick: BottomListSelectClick) {
        this.mEducationSelectClick = mEducationSelectClick
    }

    interface BottomListSelectClick {
        fun onBottomListSelectClickListener(index: Int)
    }

}