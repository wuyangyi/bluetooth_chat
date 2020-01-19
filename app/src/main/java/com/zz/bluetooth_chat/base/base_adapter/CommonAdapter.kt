package com.zz.bluetooth_chat.base.base_adapter

import android.content.Context
import android.view.LayoutInflater

/**
 * author: wuyangyi
 * date  : 2020-01-15
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

abstract class CommonAdapter<T>(context: Context, layoutId : Int, datas: ArrayList<T>) : MultiItemTypeAdapter<T>(context, datas) {
    protected var mLayoutId: Int = layoutId
    protected lateinit var mInflater: LayoutInflater


    init {
        addItemViewDelegate(object : ItemViewDelegate<T> {
            override fun getItemViewLayoutId(): Int {
                return layoutId
            }

            override fun isForViewType(item: T, position: Int): Boolean {
                return true
            }

            override fun convert(holder: ViewHolder, t: T, lastT: T, position: Int, itemCounts: Int) {
                this@CommonAdapter.convert(holder, t, position)
            }


        })
    }

    protected abstract fun convert(holder: ViewHolder, t: T, position: Int)
}