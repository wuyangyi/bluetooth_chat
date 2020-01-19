package com.zz.bluetooth_chat.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zz.bluetooth_chat.R


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/11
 * desc   : Activity的基类
 */
abstract class BaseActivity<F : Fragment> : AppCompatActivity(), AnimationClick{
    protected var mApplication: BaseApplication? = null
    /**
     * 内容 fragment
     */
    protected var mContanierFragment: F? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
        initData()

    }

    protected open fun getLayoutId(): Int {
        return R.layout.activity_base
    }

    fun getContanierFragment(): F? {
        return mContanierFragment
    }

    /**
     * @return 当前页的Fragment
     */
    protected abstract fun getFragment(): F

    /**
     * view 初始化
     *
     */
    protected open fun initView() {
        // 添加fragment
        if (mContanierFragment == null) {
            mContanierFragment = getFragment()
            if (!mContanierFragment!!.isAdded) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment_container, mContanierFragment!!)
                transaction.commit()
            }
        }
    }

    /**
     * 数据初始化
     */
    protected open fun initData() {}

    override fun animation() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mContanierFragment!!.onActivityResult(requestCode, resultCode, data)
    }
}