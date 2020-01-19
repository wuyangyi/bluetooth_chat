package com.zz.bluetooth_chat.base


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/11
 * desc   :
 */
 interface IBaseListPresenter : IBasePresenter {
    /**
     * 加载数据
     * @param maxId
     * @param isLoadMore
     */
    fun requestNetData(maxId: Int, page: Int, isLoadMore: Boolean)
}