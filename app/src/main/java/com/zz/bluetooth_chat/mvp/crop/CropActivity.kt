package com.zz.bluetooth_chat.mvp.crop

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kevin.crop.UCrop
import com.kevin.crop.util.BitmapLoadUtils
import com.kevin.crop.view.*
import com.zz.bluetooth_chat.R
import java.io.OutputStream

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 裁剪的界面
 */
class CropActivity : AppCompatActivity() {
    private val TAG = "CropActivity"

    lateinit var mUCropView: UCropView
    lateinit var mGestureCropImageView: GestureCropImageView
    lateinit var mOverlayView: OverlayView
    lateinit var saveTv: TextView
    lateinit var mTvCancel: TextView

    private var mOutputUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        initView()
        val intent = intent
        setImageData(intent)
        initEvent()
    }

    private fun setImageData(intent: Intent) {
        val inputUri = intent.getParcelableExtra<Uri>(UCrop.EXTRA_INPUT_URI)
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI)

        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri)
            } catch (e: Exception) {
                setResultException(e)
                finish()
            }

        } else {
            setResultException(NullPointerException("Both input and output Uri must be specified"))
            finish()
        }

        // 设置裁剪宽高比
        if (intent.getBooleanExtra(UCrop.EXTRA_ASPECT_RATIO_SET, false)) {
            val aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0f)
            val aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0f)

            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView.targetAspectRatio = aspectRatioX / aspectRatioY
            } else {
                mGestureCropImageView.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
            }
        }

        // 设置裁剪的最大宽高
        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            val maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0)
            val maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0)

            if (maxSizeX > 0 && maxSizeY > 0) {
                mGestureCropImageView.setMaxResultImageSizeX(maxSizeX)
                mGestureCropImageView.setMaxResultImageSizeY(maxSizeY)
            } else {
                Log.w(TAG, "EXTRA_MAX_SIZE_X and EXTRA_MAX_SIZE_Y must be greater than 0")
            }
        }
    }

    private fun initView() {
        mUCropView = findViewById(R.id.ucv_crop)
        saveTv = findViewById(R.id.tv_sure)
        mGestureCropImageView = mUCropView.cropImageView
        mOverlayView = mUCropView.overlayView
        mTvCancel = findViewById(R.id.tv_cancel)

        // 设置允许缩放
        mGestureCropImageView.isScaleEnabled = true
        // 设置禁止旋转
        mGestureCropImageView.isRotateEnabled = false
        // 设置剪切后的最大宽度
        mGestureCropImageView.setMaxResultImageSizeX(300)
        // 设置剪切后的最大高度
        mGestureCropImageView.setMaxResultImageSizeY(300)

        // 设置外部阴影颜色
        mOverlayView.setDimmedColor(Color.parseColor("#AA000000"))
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView.setOvalDimmedLayer(false)
        // 设置显示裁剪边框
        mOverlayView.setShowCropFrame(true)
        // 设置不显示裁剪网格
        mOverlayView.setShowCropGrid(true)
    }

    private fun initEvent() {
        mGestureCropImageView.setTransformImageListener(mImageListener)
        saveTv.setOnClickListener { cropAndSaveImage() }
        mTvCancel.setOnClickListener { finish() }
    }

    private fun cropAndSaveImage() {
        var outputStream: OutputStream? = null
        try {
            val croppedBitmap = mGestureCropImageView.cropImage()
            if (croppedBitmap != null) {
                outputStream = contentResolver.openOutputStream(mOutputUri!!)
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                croppedBitmap.recycle()

                setResultUri(mOutputUri, mGestureCropImageView.targetAspectRatio)
                finish()
            } else {
                setResultException(NullPointerException("CropImageView.cropImage() returned null."))
            }
        } catch (e: Exception) {
            setResultException(e)
            finish()
        } finally {
            BitmapLoadUtils.close(outputStream)
        }
    }

    private val mImageListener = object : TransformImageView.TransformImageListener {
        override fun onRotate(currentAngle: Float) {
            //            setAngleText(currentAngle);
        }

        override fun onScale(currentScale: Float) {
            //            setScaleText(currentScale);
        }

        override fun onLoadComplete() {
            val fadeInAnimation =
                AnimationUtils.loadAnimation(applicationContext, R.anim.ucrop_fade_in)
            fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    mUCropView.visibility = View.VISIBLE
                    mGestureCropImageView.setImageToWrapCropBounds()
                }

                override fun onAnimationEnd(animation: Animation) {

                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            mUCropView.startAnimation(fadeInAnimation)
        }

        override fun onLoadFailure(e: Exception) {
            setResultException(e)
            finish()
        }

    }

    private fun setResultUri(uri: Uri?, resultAspectRatio: Float) {
        setResult(
            RESULT_OK, Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
        )
    }

    private fun setResultException(throwable: Throwable) {
        setResult(UCrop.RESULT_ERROR, Intent().putExtra(UCrop.EXTRA_ERROR, throwable))
    }
}