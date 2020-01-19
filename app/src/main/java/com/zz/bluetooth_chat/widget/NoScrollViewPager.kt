package com.zz.bluetooth_chat.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/11
 * desc   : 禁止滑动的viewpage
 */
class NoScrollViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}