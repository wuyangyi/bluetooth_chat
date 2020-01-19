package com.zz.bluetooth_chat.base

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/10
 * desc   :
 */

interface BaseListView<B> : BaseView {
    /**
     * 加载成功
     * @param data
     * @param isLoadMore
     */
    fun onNetSuccess(data: List<B>, isLoadMore: Boolean)

    /**
     * 加载失败
     */
    fun onNetFailing()

    /**
     * 手动刷新
     */
    fun startRefresh()
}
