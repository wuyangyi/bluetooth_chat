package com.zz.bluetooth_chat.bean

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 底部列表选择的数据
 */

class BottomListSelectBean {
    var title: String? = null
    var isSelect: Boolean = false

    companion object{
        fun getDateByString(s: ArrayList<String>, selectPosition: Int): ArrayList<BottomListSelectBean> {
            val data = ArrayList<BottomListSelectBean>()
            for (i in s.indices) {
                val selectBean = BottomListSelectBean()
                selectBean.title = s[i]
                selectBean.isSelect = (i == selectPosition)
                data.add(selectBean)
            }
            return data
        }
    }
}