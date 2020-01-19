package com.zz.bluetooth_chat.base

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.base_adapter.HeaderAndFooterWrapper
import com.zz.bluetooth_chat.base.base_adapter.ViewHolder
import com.zz.bluetooth_chat.bean.BaseListBean
import com.zz.bluetooth_chat.util.ToastUtils
import java.util.ArrayList

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : List加载数据的Fragment封装
 */
abstract class BaseListFragment<P : IBaseListPresenter, B : BaseListBean> : BaseFragment<P>(), BaseListView<B> {
    val PAGE_LIST_NUMBER = 15 //加载数据的条数
    protected lateinit var mSrlLayout: SwipeRefreshLayout
    protected lateinit var mRvList: RecyclerView
    protected lateinit var mListData: ArrayList<B>
    protected lateinit var mLayoutManager: LinearLayoutManager
    protected lateinit var mAdapter: RecyclerView.Adapter<ViewHolder>
    //头布局和尾布局adapter
    protected var mHeaderAndFooterWrapper: HeaderAndFooterWrapper? = null

    protected var mAnimationDrawable: AnimationDrawable? = null//加载动画
    protected lateinit var mIvLoad: ImageView //加载动画的图片
    protected lateinit var mTvText: TextView //加载的文字

    protected var isHaveMoreData = true //是否还有更多数据
    /**
     * 列表脚视图
     */
    protected lateinit var mFooterView: View

    /**
     * 数据的条数
     */
    protected var mMaxId = 0

    /**
     * 分页页数
     */
    protected var mPage = 1

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_base_list
    }

    override fun initView(rootView: View) {
        mRvList = rootView.findViewById(R.id.rv_list)
        mSrlLayout = rootView.findViewById(R.id.srl_refresh_layout)
        mListData = ArrayList()
        mAdapter = getAdapter()
        mHeaderAndFooterWrapper = HeaderAndFooterWrapper(mAdapter)
        if(isNeedLoadMore()){
            mHeaderAndFooterWrapper?.addFootView(getFooterView())
        }
        mRvList.adapter = mHeaderAndFooterWrapper
        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvList.layoutManager = mLayoutManager
        //下拉刷新背景色
        mSrlLayout.setProgressBackgroundColorSchemeResource(R.color.white)
        //下拉刷新设置进度条的颜色
        mSrlLayout.setColorSchemeResources(R.color.main_color, R.color.home_bottom)
        mSrlLayout.isEnabled = isNeedRefresh() //下拉刷新的开启和关闭
        mSrlLayout.setOnRefreshListener {
            mPage = 1
            mMaxId = 0
            mPresenter.requestNetData(mMaxId, mPage, false)
        }
        mRvList.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (isNeedLoadMore() && isHaveMoreData && !mRvList.canScrollVertically(1)){
                        mTvText.text = context!!.getString(R.string.loading_data)
                        mIvLoad.visibility = View.VISIBLE
                        handleAnimation(true)
                        mPresenter.requestNetData(mMaxId, mPage, true)
                    }
                }
            }
        )
    }


    /**
     * 获取adapter
     * @return
     */
    protected abstract fun getAdapter(): RecyclerView.Adapter<ViewHolder>


    /**
     * 是否需要下拉刷新功能
     * @return
     */
    protected open fun isNeedRefresh(): Boolean {
        return true
    }

    /**
     * 是否需要上拉加载功能
     * @return
     */
    protected open fun isNeedLoadMore(): Boolean {
        return true
    }

    /**
     * 关闭加载动画
     */
    protected open fun hideRefreshState(isLoadMore: Boolean) {
        if (isLoadMore) {

        } else {
            closeRefresh()
        }
    }

    /**
     * 关闭下拉刷新动画
     */
    protected open fun closeRefresh() {
        mSrlLayout.post { mSrlLayout.isRefreshing = false }
    }

    /**
     * 获取更多的View
     *
     * @return
     */
    protected open fun getFooterView(): View {
        // 添加加载更多没有了的提示
        mFooterView = LayoutInflater.from(context).inflate(R.layout.view_footer_no_more, null)
        mTvText = mFooterView.findViewById(R.id.tv_no_more_data_text)
        mIvLoad = mFooterView.findViewById(R.id.iv_load)
        mAnimationDrawable = mIvLoad.drawable as AnimationDrawable
        mFooterView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        return mFooterView
    }

    override fun onNetSuccess(data: List<B>, isLoadMore: Boolean) {
        if(isLoadMore) { //加载更多
            mListData.addAll(data)
        } else { //刷新
            if (!mListData.isNullOrEmpty()) {
                mListData.clear()
            }
            mListData.addAll(data)
        }
        mMaxId = getMaxId()
        mPage = getPage(data)
        mAdapter.notifyDataSetChanged()
        hideRefreshState(isLoadMore)
        if (data.isEmpty() || data.size < PAGE_LIST_NUMBER) {
            mFooterView.visibility = View.VISIBLE
            mTvText.text = context!!.getString(R.string.no_more_data)
            mIvLoad.visibility = View.GONE
            handleAnimation(false)
            isHaveMoreData = false
        } else {
            mFooterView.visibility = View.VISIBLE
            mTvText.text = context!!.getString(R.string.load_more_data)
            mIvLoad.visibility = View.GONE
            handleAnimation(false)
            isHaveMoreData = true
        }
    }

    override fun onNetFailing() {
        ToastUtils.showToast("加载失败")
    }

    protected open fun getMaxId(): Int {
        return mListData.size
    }

    protected open fun getPage(data: List<B>): Int {
        if (data.isNotEmpty()) {
            mPage++
        }
        return mPage
    }

    /**
     * 手动刷新
     */
    override fun startRefresh() {
        mSrlLayout.post {
            mSrlLayout.isRefreshing = true
            mPresenter.requestNetData(mMaxId, mPage,false)
        }
    }


    /**
     * 处理动画
     *
     * @param status true 开启动画，false 关闭动画
     */
    open fun handleAnimation(status: Boolean) {
        requireNotNull(mAnimationDrawable) { "load animation not be null" }
        if (status) {
            if (!mAnimationDrawable!!.isRunning) {
                mIvLoad.visibility = View.VISIBLE
                mAnimationDrawable!!.start()
            }
        } else {
            if (mAnimationDrawable!!.isRunning) {
                mAnimationDrawable!!.stop()
                mIvLoad.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mAnimationDrawable != null && mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.stop()
        }
    }

    open fun adapterNotifyDataSetChanged() {
        mAdapter.notifyDataSetChanged()
        mHeaderAndFooterWrapper!!.notifyDataSetChanged()
    }

}