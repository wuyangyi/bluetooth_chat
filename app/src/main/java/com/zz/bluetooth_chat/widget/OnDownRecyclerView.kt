package com.zz.bluetooth_chat.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * author: wuyangyi
 * date  : 2020-01-18
 * e-mail: wuyangyi@haitou.cc
 * desc  : 按下具有回调的RecyclerView
 */
class OnDownRecyclerView : RecyclerView {
    private var onRecyclerDownClick: OnRecyclerDownClick? = null

    constructor(context: Context):super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, default: Int) : super(context,attributeSet, default)


    override fun onTouchEvent(e: MotionEvent?): Boolean {
        if (onRecyclerDownClick != null) {
            onRecyclerDownClick!!.onDownClickListener()
        }
        return super.onTouchEvent(e)
    }

    fun setOnRecyclerDownClick(onRecyclerDownClick: OnRecyclerDownClick) {
        this.onRecyclerDownClick = onRecyclerDownClick
    }

    interface OnRecyclerDownClick {
        fun onDownClickListener()
    }
}