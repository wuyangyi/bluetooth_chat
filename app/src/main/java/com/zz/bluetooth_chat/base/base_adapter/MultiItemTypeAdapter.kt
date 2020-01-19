package com.zz.bluetooth_chat.base.base_adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

open class MultiItemTypeAdapter<T>(context: Context, datas: ArrayList<T>) : RecyclerView.Adapter<ViewHolder>() {

    protected var mContext: Context = context
    protected open var mDatas: ArrayList<T> = datas

    protected var mItemViewDelegateManager: ItemViewDelegateManager<T>
    protected var mOnItemClickListener: OnItemClickListener? = null

    init {
        mItemViewDelegateManager = ItemViewDelegateManager()
    }

    override fun getItemViewType(position: Int): Int {
        return if (!useItemViewDelegateManager()) {
            super.getItemViewType(position)
        } else mItemViewDelegateManager.getItemViewType(mDatas[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType)
        val layoutId = itemViewDelegate.getItemViewLayoutId()
        val holder = ViewHolder.createViewHolder(mContext, parent, layoutId)
        onViewHolderCreated(holder, holder.getConvertView())
        setListener(parent, holder, viewType)
        return holder
    }

    fun onViewHolderCreated(holder: ViewHolder, itemView: View) {

    }

    fun convert(holder: ViewHolder, t: T, lastT: T?) {
        mItemViewDelegateManager.convert(holder, t, lastT!!, holder.adapterPosition, itemCount)
    }

    protected open fun isEnabled(viewType: Int): Boolean {
        return true
    }


    protected open fun setListener(parent: ViewGroup, viewHolder: ViewHolder, viewType: Int) {
        if (!isEnabled(viewType)) {
            return
        }
        viewHolder.getConvertView().setOnClickListener(View.OnClickListener { v ->
            if (mOnItemClickListener != null) {
                val position = viewHolder.adapterPosition
                // @see http://android.xsoftlab.net/reference/android/support/v7/widget/RecyclerView.ViewHolder.html#getAdapterPosition()
                // Note that if you've called notifyDataSetChanged(), until the next layout pass,
                // the return value of this method will be NO_POSITION.
                if (position == RecyclerView.NO_POSITION) {
                    return@OnClickListener
                }
                mOnItemClickListener!!.onItemClick(v, viewHolder, position)
            }
        })

        viewHolder.getConvertView().setOnLongClickListener(View.OnLongClickListener { v ->
            if (mOnItemClickListener != null) {
                val position = viewHolder.adapterPosition
                return@OnLongClickListener position != RecyclerView.NO_POSITION && mOnItemClickListener!!.onItemLongClick(
                    v,
                    viewHolder,
                    position
                )
            }
            false
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lastData = mDatas[position]
        convert(holder, mDatas[position], lastData)
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    fun getDatas(): List<T> {
        return mDatas
    }

    fun refreshData(datas: ArrayList<T>) {
        mDatas = datas
        notifyDataSetChanged()
    }

    /**
     * 新增清除数据
     */
    fun clear() {
        mDatas.clear()
    }

    /**
     * 新增数据添加
     *
     * @param datas
     */
    fun addAllData(datas: List<T>) {
        mDatas.addAll(datas)
    }

    fun getItem(position: Int): T {
        return mDatas.get(position)
    }


    fun addItemViewDelegate(itemViewDelegate: ItemViewDelegate<T>): MultiItemTypeAdapter<*> {
        mItemViewDelegateManager.addDelegate(itemViewDelegate)
        return this
    }

    fun addItemViewDelegate(
        viewType: Int,
        itemViewDelegate: ItemViewDelegate<T>
    ): MultiItemTypeAdapter<*> {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate)
        return this
    }

    protected fun useItemViewDelegateManager(): Boolean {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int)

        fun onItemLongClick(view: View, holder: RecyclerView.ViewHolder, position: Int): Boolean
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

    fun getContext(): Context {
        return mContext
    }

    //添加单个在指定位置
    fun addItem(content: T, position: Int) {
        mDatas.add(position, content)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    //添加单个数据在最后
    fun addItem(content: T) {
        mDatas.add(content)
        notifyItemInserted(mDatas.size - 1)
        notifyDataSetChanged()
    }

    //移除单个
    fun removeItem(content: T) {
        val position = mDatas.indexOf(content)
        removeItem(position)
    }

    fun removeItem(position: Int) {
        mDatas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mDatas.size)
    }

    //改变所有
    fun dataChange(content: List<T>?) {
        mDatas.clear()
        notifyDataSetChanged()

        if (content != null) {
            mDatas.addAll(content)
        }
        notifyDataSetChanged()

    }

    //清除所有
    fun dataClear() {
        mDatas.clear()
        notifyDataSetChanged()
    }

    //添加list
    fun dataAdd(content: List<T>?) {
        if (mDatas != null && content != null) {
            mDatas.addAll(content)
            notifyDataSetChanged()
        }
    }

    //往前添加list
    fun dataAddForward(content: List<T>?) {
        if (content != null) {
            mDatas.addAll(0, content)
            notifyDataSetChanged()
        }
    }

    //添加不刷新
    fun justDataAdd(content: List<T>?) {
        if (content != null) {
            mDatas.addAll(content)
        }
    }

    //改变单个
    fun updateOne(position: Int, content: T) {
        mDatas.set(position, content)
        notifyItemChanged(position)
    }

}