package com.zz.bluetooth_chat.base.base_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.util.Linkify
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.recyclerview.widget.RecyclerView


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */
class ViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var mViews: SparseArray<View>? = null
    private var mConvertView: View? = null
    private var mContext: Context? = null

    companion object {
        fun createViewHolder(context: Context, itemView: View): ViewHolder {
            return ViewHolder(context, itemView)
        }

        fun createViewHolder(
            context: Context,
            parent: ViewGroup, layoutId: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(
                layoutId, parent,
                false
            )
            return ViewHolder(context, itemView)
        }
    }

    init {
        mContext = context
        mConvertView = itemView
        mViews = SparseArray()
    }

    /**
     * 通过viewId获取控件
     */
    fun <T : View> getView(viewId : Int) : T {
        var view: View? = mViews?.get(viewId)
        if(view == null) {
            view = mConvertView?.findViewById(viewId)
            if (view == null) {
                val parentName = mContext?.resources?.getResourceName(mConvertView?.id!!)
                val resName = mContext?.resources?.getResourceName(viewId)
                throw RuntimeException(
                    " \t the \t" + resName + " \tview of  " + parentName + "  view not found!!! please check your view-Type has repeated ? " +
                    "or make sure your view has declared in your view"
                )
            }
            mViews?.put(viewId, view)
        }
        return view as T
    }

    fun getImageViwe(id: Int): ImageView {
        return getView(id)
    }

    fun getTextView(id: Int): TextView {
        return getView(id)
    }

    /****以下为辅助方法*****/

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    fun setText(viewId: Int, text: String): ViewHolder {
        val tv = getView<TextView>(viewId)
        tv.text = text
        return this
    }

    fun setText(viewId: Int, text: CharSequence): ViewHolder {
        val tv = getView<TextView>(viewId)
        tv.text = text
        return this
    }

    fun setImageResource(viewId: Int, resId: Int): ViewHolder {
        val view = getView<ImageView>(viewId)
        view.setImageResource(resId)
        return this
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap): ViewHolder {
        val view = getView<ImageView>(viewId)
        view.setImageBitmap(bitmap)
        return this
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable): ViewHolder {
        val view = getView<ImageView>(viewId)
        view.setImageDrawable(drawable)
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): ViewHolder {
        val view = getView<View>(viewId)
        view.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(viewId: Int, backgroundRes: Int): ViewHolder {
        val view = getView<View>(viewId)
        view.setBackgroundResource(backgroundRes)
        return this
    }

    fun setTextColor(viewId: Int, textColor: Int): ViewHolder {
        val view = getView<TextView>(viewId)
        view.setTextColor(textColor)
        return this
    }

    fun setTextColorRes(viewId: Int, textColorRes: Int): ViewHolder {
        val view = getView<TextView>(viewId)
        view.setTextColor(mContext?.resources?.getColor(textColorRes)!!)
        return this
    }

    @SuppressLint("NewApi")
    fun setAlpha(viewId: Int, value: Float): ViewHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId).alpha = value
        } else {
            // Pre-honeycomb hack to set Alpha value
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId).startAnimation(alpha)
        }
        return this
    }

    fun setVisible(viewId: Int, visible: Int): ViewHolder {
        val view = getView<View>(viewId)
        view.visibility = visible
        return this
    }

    fun linkify(viewId: Int): ViewHolder {
        val view = getView<TextView>(viewId)
        Linkify.addLinks(view, Linkify.ALL)
        return this
    }

    fun setTypeface(typeface: Typeface, vararg viewIds: Int): ViewHolder {
        for (viewId in viewIds) {
            val view = getView<TextView>(viewId)
            view.typeface = typeface
            view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    fun setProgress(viewId: Int, progress: Int): ViewHolder {
        val view = getView<ProgressBar>(viewId)
        view.progress = progress
        return this
    }

    fun setProgress(viewId: Int, progress: Int, max: Int): ViewHolder {
        val view = getView<ProgressBar>(viewId)
        view.max = max
        view.progress = progress
        return this
    }

    fun setMax(viewId: Int, max: Int): ViewHolder {
        val view = getView<ProgressBar>(viewId)
        view.max = max
        return this
    }

    fun setRating(viewId: Int, rating: Float): ViewHolder {
        val view = getView<RatingBar>(viewId)
        view.rating = rating
        return this
    }

    fun setRating(viewId: Int, rating: Float, max: Int): ViewHolder {
        val view = getView<RatingBar>(viewId)
        view.max = max
        view.rating = rating
        return this
    }

    fun setTag(viewId: Int, tag: Any): ViewHolder {
        val view = getView<View>(viewId)
        view.tag = tag
        return this
    }

    fun setTag(viewId: Int, key: Int, tag: Any): ViewHolder {
        val view = getView<View>(viewId)
        view.setTag(key, tag)
        return this
    }

    fun setChecked(viewId: Int, checked: Boolean): ViewHolder {
        val view = getView<View>(viewId) as Checkable
        view.isChecked = checked
        return this
    }

    /**
     * 关于事件的
     */
    fun setOnClickListener(
        viewId: Int,
        listener: View.OnClickListener
    ): ViewHolder {
        val view = getView<View>(viewId)
        view.setOnClickListener(listener)
        return this
    }

    fun setOnTouchListener(
        viewId: Int,
        listener: View.OnTouchListener
    ): ViewHolder {
        val view = getView<View>(viewId)
        view.setOnTouchListener(listener)
        return this
    }

    fun setOnLongClickListener(
        viewId: Int,
        listener: View.OnLongClickListener
    ): ViewHolder {
        val view = getView<View>(viewId)
        view.setOnLongClickListener(listener)
        return this
    }

    fun getConvertView(): View {
        return mConvertView!!
    }

}