package com.zz.bluetooth_chat.base.base_adapter

import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : 添加头布局和为布局的adapter
 */
class HeaderAndFooterWrapper(private val mInnerAdapter: RecyclerView.Adapter<ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var mHeaderViews : SparseArrayCompat<View> = SparseArrayCompat<View>()
    private var mFootViews : SparseArrayCompat<View> = SparseArrayCompat<View>()

    private val realItemCount: Int
        get() = mInnerAdapter.itemCount

    val headersCount: Int
        get() = mHeaderViews.size()

    val footersCount: Int
        get() = mFootViews.size()

    override fun getItemCount(): Int {
        return headersCount + footersCount + realItemCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (mHeaderViews.get(viewType) != null) {
            return ViewHolder.createViewHolder(parent.context, mHeaderViews.get(viewType)!!)

        } else if (mFootViews.get(viewType) != null) {
            return ViewHolder.createViewHolder(parent.context, mFootViews.get(viewType)!!)
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position)
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - headersCount - realItemCount)
        }
        return mInnerAdapter.getItemViewType(position - headersCount)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }
        mInnerAdapter.onBindViewHolder(holder as ViewHolder, position - headersCount)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        WrapperUtils.onAttachedToRecyclerView(
            mInnerAdapter,
            recyclerView,
            object : WrapperUtils.SpanSizeCallback {
                override fun getSpanSize(
                    layoutManager: GridLayoutManager,
                    oldLookup: GridLayoutManager.SpanSizeLookup,
                    position: Int
                ): Int {
                    val viewType = getItemViewType(position)
                    if (mHeaderViews.get(viewType) != null) {
                        return layoutManager.spanCount
                    } else if (mFootViews.get(viewType) != null) {
                        return layoutManager.spanCount
                    }
                    return if (oldLookup != null) oldLookup!!.getSpanSize(position) else 1
                }
            })
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        mInnerAdapter.onViewAttachedToWindow(holder as ViewHolder)
        val position = holder.layoutPosition
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder)
        }
    }

    private fun isHeaderViewPos(position: Int): Boolean {
        return position < headersCount
    }

    private fun isFooterViewPos(position: Int): Boolean {
        return position >= headersCount + realItemCount
    }

    fun addHeaderView(view: View) {
        view.tag = BASE_ITEM_TYPE_HEADER
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view)
    }

    fun addFootView(view: View) {
        view.tag = BASE_ITEM_TYPE_FOOTER
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view)
    }

    /**
     * 移除一个底布局
     * @param view
     */
    fun removeFootView(view: View) {
        view.tag = BASE_ITEM_TYPE_FOOTER
        var key = BASE_ITEM_TYPE_FOOTER
        //遍历获得view的key值
        for (i in BASE_ITEM_TYPE_FOOTER until mFootViews.size() + BASE_ITEM_TYPE_FOOTER) {
            if (mFootViews.get(i) === view) {
                key = i
            }
        }
        mFootViews.delete(key)
    }

    companion object {
        private val BASE_ITEM_TYPE_HEADER = 100000
        private val BASE_ITEM_TYPE_FOOTER = 200000
    }
}
