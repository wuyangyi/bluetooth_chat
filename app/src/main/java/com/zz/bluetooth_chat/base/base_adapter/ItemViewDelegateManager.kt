package com.zz.bluetooth_chat.base.base_adapter

import androidx.collection.SparseArrayCompat

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

class ItemViewDelegateManager<T> {
    var delegates : SparseArrayCompat<ItemViewDelegate<T>> = SparseArrayCompat()

    public fun getItemViewDelegateCount(): Int {
        return delegates.size()
    }

    public fun addDelegate(delegate: ItemViewDelegate<T>) : ItemViewDelegateManager<T> {
        var viewType: Int = delegates.size()
        if (delegate != null) {
            delegates.put(viewType, delegate)
            viewType++
        }
        return this
    }

     fun addDelegate(viewType:Int, delegate:ItemViewDelegate<T>):ItemViewDelegateManager<T> {
         when {
             delegates.get(viewType) != null -> throw IllegalArgumentException(
                 "An ItemViewDelegate is already registered for the viewType = "
                         + viewType
                         + ". Already registered ItemViewDelegate is "
                         + delegates.get(viewType)
             )
             else -> {
                 delegates.put(viewType, delegate)
                 return this
             }
         }
     }

    fun removeDelegate(delegate: ItemViewDelegate<T>?): ItemViewDelegateManager<T> {
        if (delegate == null) {
            throw NullPointerException("ItemViewDelegate is null")
        }
        val indexToRemove = delegates.indexOfValue(delegate)

        if (indexToRemove >= 0) {
            delegates.removeAt(indexToRemove)
        }
        return this
    }

    fun removeDelegate(itemType: Int): ItemViewDelegateManager<T> {
        val indexToRemove = delegates.indexOfKey(itemType)

        if (indexToRemove >= 0) {
            delegates.removeAt(indexToRemove)
        }
        return this
    }

    fun getItemViewType(item: T, position: Int): Int {
        val delegatesCount = delegates.size()
        for (i in delegatesCount - 1 downTo 0) {
            val delegate = delegates.valueAt(i)
            if (delegate.isForViewType(item, position)) {
                return delegates.keyAt(i)
            }
        }
        throw IllegalArgumentException(
            "No ItemViewDelegate added that matches position=$position in data source"
        )
    }

    fun convert(holder: ViewHolder, item: T, lastItem: T, position: Int, itemCounts: Int) {
        val delegatesCount = delegates.size()
        for (i in 0 until delegatesCount) {
            val delegate = delegates.valueAt(i)

            if (delegate.isForViewType(item, position)) {
                delegate.convert(holder, item, lastItem, position, itemCounts)
                return
            }
        }
        throw IllegalArgumentException(
            "No ItemViewDelegateManager added that matches position=$position in data source"
        )
    }

    fun getItemViewDelegate(viewType: Int): ItemViewDelegate<T> {
        return delegates.get(viewType)!!
    }

    fun getItemViewLayoutId(viewType: Int): Int {
        return getItemViewDelegate(viewType).getItemViewLayoutId()
    }

    fun getItemViewType(itemViewDelegate: ItemViewDelegate<T>): Int {
        return delegates.indexOfValue(itemViewDelegate)
    }

}