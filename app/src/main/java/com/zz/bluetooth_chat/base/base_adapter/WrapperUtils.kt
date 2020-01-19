package com.zz.bluetooth_chat.base.base_adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */

object WrapperUtils {
    interface SpanSizeCallback {
        fun getSpanSize(
            layoutManager: GridLayoutManager,
            oldLookup: GridLayoutManager.SpanSizeLookup,
            position: Int
        ): Int
    }

    fun onAttachedToRecyclerView(
        innerAdapter: RecyclerView.Adapter<ViewHolder>,
        recyclerView: RecyclerView,
        callback: SpanSizeCallback
    ) {
        innerAdapter.onAttachedToRecyclerView(recyclerView)

        val layoutManager = recyclerView.getLayoutManager()
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager as GridLayoutManager
            val spanSizeLookup = gridLayoutManager.getSpanSizeLookup()

            gridLayoutManager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return callback.getSpanSize(gridLayoutManager, spanSizeLookup, position)
                }
            })
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount())
        }
    }

    fun setFullSpan(holder: RecyclerView.ViewHolder) {
        val lp = holder.itemView.getLayoutParams()

        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {

            val p = lp as StaggeredGridLayoutManager.LayoutParams

            p!!.isFullSpan = true
        }
    }
}
