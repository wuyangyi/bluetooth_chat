package com.zz.bluetooth_chat.base.base_adapter

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

interface ItemViewDelegate<T> {

    fun getItemViewLayoutId(): Int

    fun isForViewType(item: T, position: Int): Boolean

    fun convert(holder: ViewHolder, t: T, lastT: T, position: Int, itemCounts: Int)

}
