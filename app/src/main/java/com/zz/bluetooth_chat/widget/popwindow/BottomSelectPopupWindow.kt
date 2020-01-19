package com.zz.bluetooth_chat.widget.popwindow

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.base_adapter.CommonAdapter

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 底部弹出的选择器
 */

class BottomSelectPopupWindow(builder: Builder) : PopupWindow() {

    protected var mIsOutsideTouch: Boolean = false
    protected var mIsFocus: Boolean = false
    protected var mAlpha: Float = 0.toFloat()
    private val mBackgroundDrawable = ColorDrawable(0x00000000)// 默认为透明;
    protected var mAnimationStyle: Int = 0

    protected var mActivity: Activity
    protected var mParentView: View? = null
    protected lateinit var mContentView: View

    protected var title: String? = null
    protected var desc: String? = null
    protected var adapter: CommonAdapter<*>? = null
    protected var scrollPosition: Int = 0

    protected val layoutId: Int
        get() = R.layout.view_pop_select

    init {
        this.mActivity = builder.mActivity
        this.mParentView = builder.parentView
        this.mAnimationStyle = builder.mAnimationStyle
        this.mAlpha = builder.mAlpha
        this.mIsFocus = builder.mIsFocus
        this.mIsOutsideTouch = builder.mIsOutsideTouch
        this.title = builder.title
        this.desc = builder.desc
        this.adapter = builder.adapter
        this.scrollPosition = builder.scrollPosition

        if (canInitView()) {
            initView()
        }
    }

    fun newBuilder(): Builder {
        return Builder(this)
    }

    /**
     * 设置屏幕的透明度
     *
     * @param alpha 需要设置透明度
     */
    protected fun setWindowAlpha(alpha: Float) {
        val params = mActivity.window.attributes
        params.alpha = alpha
        params.verticalMargin = 100f
        mActivity.window.attributes = params
    }

    protected fun initView() {
        initLayout()
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        isFocusable = mIsFocus
        isOutsideTouchable = mIsOutsideTouch
        setBackgroundDrawable(mBackgroundDrawable)
        contentView = mContentView
        if (mAnimationStyle == NO_ANIMATION) {
            return
        }
        animationStyle = if (mAnimationStyle > 0) mAnimationStyle else R.style.style_bottomAnimation
    }

    protected fun initLayout() {
        mContentView = LayoutInflater.from(mActivity).inflate(layoutId, null)
        val mTvTitle = mContentView.findViewById<TextView>(R.id.tv_title)
        mTvTitle.text = if (title == null) "" else title
        val mTvDesc = mContentView.findViewById<TextView>(R.id.tv_desc)
        mTvDesc.text = if (desc == null) "" else desc
        if (adapter != null) {
            val recyclerView = mContentView.findViewById<RecyclerView>(R.id.rv_list)
            val layoutManager = LinearLayoutManager(mActivity)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            if (scrollPosition >= 0 && scrollPosition < adapter!!.itemCount) {
                recyclerView.scrollToPosition(scrollPosition)
            }
        }
        mContentView.findViewById<View>(R.id.iv_close).setOnClickListener { hide() }

        setOnDismissListener { setWindowAlpha(1.0f) }
    }

    /**
     * 默认显示到底部
     */
    fun show() {
        setWindowAlpha(mAlpha)
        if (mParentView == null) {
            showAtLocation(mContentView, Gravity.BOTTOM, 0, 0)
        } else {
            showAtLocation(mParentView, Gravity.BOTTOM, 0, 0)
        }
    }

    override fun dismiss() {
        if (mAnimationStyle == NO_ANIMATION) {
            mContentView.clearAnimation()
        }
        super.dismiss()
    }

    /**
     * 隐藏popupwindow
     */
    fun hide() {
        dismiss()
    }


    /**
     * 子类重新调用initView
     *
     * @return
     */
    protected fun canInitView(): Boolean {
        return true
    }


    class Builder {
        lateinit var mActivity: Activity
        var parentView: View? = null
        var mAlpha = POPUPWINDOW_ALPHA
        var mIsOutsideTouch = true// 默认为true
        var mIsFocus = true// 默认为true
        var mAnimationStyle: Int = 0
        lateinit var title: String //标题
        lateinit var desc: String //副标题
        lateinit var adapter: CommonAdapter<*>
        var scrollPosition: Int = 0 //recyclerView默认滑动的位置

        constructor() {}

        constructor(popupWindow: BottomSelectPopupWindow) {
            this.mActivity = popupWindow.mActivity
            this.parentView = popupWindow.mParentView
            this.mAnimationStyle = popupWindow.mAnimationStyle
            this.mAlpha = popupWindow.mAlpha
            this.mIsFocus = popupWindow.mIsFocus
            this.mIsOutsideTouch = popupWindow.mIsOutsideTouch
            this.title = popupWindow.title.toString()
            this.desc = popupWindow.desc.toString()
            this.adapter = popupWindow.adapter!!
            this.scrollPosition = popupWindow.scrollPosition
        }

        fun with(activity: Activity): Builder {
            this.mActivity = activity
            return this
        }

        fun parentView(parentView: View): Builder {
            this.parentView = parentView
            return this
        }

        fun animationStyle(animationStyle: Int): Builder {
            this.mAnimationStyle = animationStyle
            return this
        }

        fun isOutsideTouch(isOutsideTouch: Boolean): Builder {
            this.mIsOutsideTouch = isOutsideTouch
            return this
        }

        fun isFocus(isFocus: Boolean): Builder {
            this.mIsFocus = isFocus
            return this
        }

        fun backgroundAlpha(alpha: Float): Builder {
            this.mAlpha = alpha
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun desc(desc: String): Builder {
            this.desc = desc
            return this
        }

        fun adapter(adapter: CommonAdapter<*>): Builder {
            this.adapter = adapter
            return this
        }

        fun scrollPosition(scrollPosition: Int): Builder {
            this.scrollPosition = scrollPosition
            return this
        }

        fun build(): BottomSelectPopupWindow {
            return BottomSelectPopupWindow(this)
        }
    }

    companion object {
        val POPUPWINDOW_ALPHA = .8f
        val NO_ANIMATION = -1

        fun builder(): Builder {
            return Builder()
        }
    }
}