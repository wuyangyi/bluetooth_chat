package com.zz.bluetooth_chat.widget.imageview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.zz.bluetooth_chat.R

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  : 自定义 ImageView 控件，实现了圆角和边框
 */

@SuppressLint("AppCompatCustomView")
class MLImageView : ImageView {
    // 图片按下的画笔
    private var pressPaint: Paint? = null
    // 图片的宽高
    internal var width: Int = 0
    internal var height: Int = 0

    // 边框颜色
    private var borderColor: Int = 0
    // 边框宽度
    private var borderWidth: Int = 0
    // 圆角半径
    private var radius: Int = 0
    // 图片类型（矩形，圆形）
    private var shapeType: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        //初始化默认值
        borderWidth = 6
        borderColor = -0x22000001
        radius = 16
        shapeType = 2

        // 获取控件的属性值
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.MLImageView)
            borderColor = array.getColor(R.styleable.MLImageView_ml_border_color, borderColor)
            borderWidth =
                array.getDimensionPixelOffset(R.styleable.MLImageView_ml_border_width, borderWidth)
            radius = array.getDimensionPixelOffset(R.styleable.MLImageView_ml_radius, radius)
            shapeType = array.getInteger(R.styleable.MLImageView_ml_shape_type, shapeType)
            array.recycle()
        }

        // 按下的画笔设置
        pressPaint = Paint()
        pressPaint!!.isAntiAlias = true
        pressPaint!!.style = Paint.Style.FILL
        pressPaint!!.alpha = 0
        pressPaint!!.flags = Paint.ANTI_ALIAS_FLAG

        //        setClickable(true);
        isDrawingCacheEnabled = true
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {

        if (shapeType == 0) {
            super.onDraw(canvas)
            return
        }
        // 获取当前控件的 drawable
        val drawable = drawable ?: return
        // 这里 get 回来的宽度和高度是当前控件相对应的宽度和高度（在 xml 设置）
        if (getWidth() == 0 || getHeight() == 0) {
            return
        }
        // 获取 bitmap，即传入 imageview 的 bitmap
        // Bitmap bitmap = ((BitmapDrawable) ((SquaringDrawable)
        // drawable).getCurrent()).getBitmap();
        // 这里参考赵鹏的获取 bitmap 方式，因为上边的获取会导致 Glide 加载的drawable 强转为 BitmapDrawable 出错
        val bitmap = getBitmapFromDrawable(drawable)
        drawDrawable(canvas, bitmap)
        drawBorder(canvas)
    }

    /**
     * 实现圆角的绘制
     *
     * @param canvas
     * @param bitmap
     */
    @SuppressLint("WrongConstant")
    private fun drawDrawable(canvas: Canvas, bitmap: Bitmap?) {
        var bitmap = bitmap
        // 画笔
        val paint = Paint()
        // 颜色设置
        paint.color = -0x1
        // 抗锯齿
        paint.isAntiAlias = true
        //Paint 的 Xfermode，PorterDuff.Mode.SRC_IN 取两层图像的交集部门, 只显示上层图像。
        val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        // 标志
        val saveFlags = Canvas.ALL_SAVE_FLAG
        canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, saveFlags)

        if (shapeType == 1) {
            // 画遮罩，画出来就是一个和空间大小相匹配的圆（这里在半径上 -1 是为了不让图片超出边框）
            canvas.drawCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (width / 2 - 1).toFloat(),
                paint
            )
        } else if (shapeType == 2) {
            // 当ShapeType == 2 时 图片为圆角矩形 （这里在宽高上 -1 是为了不让图片超出边框）
            val rectf = RectF(1f, 1f, (getWidth() - 1).toFloat(), (getHeight() - 1).toFloat())
            canvas.drawRoundRect(rectf, (radius + 1).toFloat(), (radius + 1).toFloat(), paint)
        }

        paint.xfermode = xfermode

        // 空间的大小 / bitmap 的大小 = bitmap 缩放的倍数
        val scaleWidth = getWidth().toFloat() / bitmap!!.width
        val scaleHeight = getHeight().toFloat() / bitmap.height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        //bitmap 缩放
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        //draw 上去
        canvas.drawBitmap(bitmap!!, 0f, 0f, paint)
        canvas.restore()
    }

    /**
     * 绘制自定义控件边框
     *
     * @param canvas
     */
    private fun drawBorder(canvas: Canvas) {
        if (borderWidth > 0) {
            val paint = Paint()
            paint.strokeWidth = borderWidth.toFloat()
            paint.style = Paint.Style.STROKE
            paint.color = borderColor
            paint.isAntiAlias = true
            // 根据控件类型的属性去绘制圆形或者矩形
            if (shapeType == 1) {
                canvas.drawCircle(
                    (width / 2).toFloat(),
                    (height / 2).toFloat(),
                    ((width - borderWidth) / 2).toFloat(),
                    paint
                )
            } else if (shapeType == 2) {
                // 当ShapeType = 1 时 图片为圆角矩形
                val rectf = RectF(
                    (borderWidth / 2).toFloat(),
                    (borderWidth / 2).toFloat(),
                    (getWidth() - borderWidth / 2).toFloat(),
                    (getHeight() - borderWidth / 2).toFloat()
                )
                canvas.drawRoundRect(rectf, radius.toFloat(), radius.toFloat(), paint)
            }
        }
    }

    /**
     * 重写父类的 onSizeChanged 方法，检测控件宽高的变化
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
    }

    /**
     * 这里是参考其他开发者获取Bitmap内容的方法， 之前是因为没有考虑到 Glide 加载的图片
     * 导致drawable 类型是属于 SquaringDrawable 类型，导致强转失败
     * 这里是通过drawable不同的类型来进行获取Bitmap
     *
     * @param drawable
     * @return
     */
    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        try {
            val bitmap: Bitmap
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            } else if (drawable is ColorDrawable) {
                bitmap = Bitmap.createBitmap(
                    COLORDRAWABLE_DIMENSION,
                    COLORDRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth, drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 设置边框颜色
     *
     * @param borderColor
     */
    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        invalidate()
    }

    /**
     * 设置边框宽度
     *
     * @param borderWidth
     */
    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = borderWidth
    }

    /**
     * 设置倒角半径
     *
     * @param radius
     */
    fun setRadius(radius: Int) {
        this.radius = radius
        invalidate()
    }

    /**
     * 设置形状类型
     *
     * @param shapeType
     */
    fun setShapeType(shapeType: Int) {
        this.shapeType = shapeType
        invalidate()
    }

    companion object {

        // 定义 Bitmap 的默认配置
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val COLORDRAWABLE_DIMENSION = 1
    }
}