package com.zz.bluetooth_chat.mvp.home

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment
import com.zz.bluetooth_chat.mvp.group.GroupFragment
import com.zz.bluetooth_chat.mvp.message.MessageFragment
import com.zz.bluetooth_chat.mvp.mine.MineFragment
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */
class HomeFragment : BaseFragment<HomePresenter>(), HomeContract.View {

    companion object {
        val HOME_MESSAGE = 0
        val HOME_GROUP = 1
        val HOME_MINE = 2
    }


    private lateinit var listFragment: List<Fragment>

    override fun initView(rootView: View) {
        initViewPage()
        selectItem(HOME_MESSAGE)
        initListener()
        v_message_red.visibility = View.VISIBLE
        v_message_red.text = "9"
    }

    private fun initViewPage(){
        listFragment = ArrayList()
        (listFragment as ArrayList<Fragment>).add(MessageFragment())
        (listFragment as ArrayList<Fragment>).add(GroupFragment())
        (listFragment as ArrayList<Fragment>).add(MineFragment())
        vp_home.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return listFragment[position]
            }

            override fun getCount(): Int {
                return listFragment.size
            }

        }
    }

    private fun initListener() {
        ll_message.setOnClickListener {
            selectItem(HOME_MESSAGE)
        }

        ll_group.setOnClickListener {
            selectItem(HOME_GROUP)
        }

        ll_mine.setOnClickListener {
            selectItem(HOME_MINE)
        }
    }

    override fun initData() {
    }

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun showToolbar(): Boolean {
        return false
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

    override fun setUseStatusView(): Boolean {
        return false
    }


    override fun setStatusBarGrey(): Boolean {
        return true
    }

    /**
     * 选择viewpage的页面
     */
    public fun selectItem(page: Int) {
        tv_message.setTextColor(resources.getColor(if (page == HOME_MESSAGE) R.color.home_bottom else R.color.home_bottom_text_normal))
        iv_message.setImageDrawable(resources.getDrawable(if(page == HOME_MESSAGE) R.mipmap.ic_home_select else R.mipmap.ic_home))

        tv_group.setTextColor(resources.getColor(if (page == HOME_GROUP) R.color.home_bottom else R.color.home_bottom_text_normal))
        iv_group.setImageDrawable(resources.getDrawable(if(page == HOME_GROUP) R.mipmap.ic_group_select else R.mipmap.ic_group))

        tv_mine.setTextColor(resources.getColor(if (page == HOME_MINE) R.color.home_bottom else R.color.home_bottom_text_normal))
        iv_mine.setImageDrawable(resources.getDrawable(if(page == HOME_MINE) R.mipmap.ic_mine_select else R.mipmap.ic_mine))

        vp_home.setCurrentItem(page, false)
    }
}
