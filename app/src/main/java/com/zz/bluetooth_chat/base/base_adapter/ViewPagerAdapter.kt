package com.zz.bluetooth_chat.base.base_adapter

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * author: wuyangyi
 * date  : 2020-01-15
 * e-mail: wuyangyi@haitou.cc
 * desc  : Viewpage的adapter
 */

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private lateinit var list: ArrayList<Fragment>
    private lateinit var mLitles: Array<String>


    fun bindData(list: ArrayList<Fragment>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun bindData(list: ArrayList<Fragment>, titles: Array<String>) {
        this.list = list
        this.mLitles = titles
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (mLitles != null) {
            return mLitles[position]
        }
        return super.getPageTitle(position)
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (list.contains(`object`)) {
            PagerAdapter.POSITION_UNCHANGED
        } else {
            PagerAdapter.POSITION_NONE
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var f : Fragment = super.instantiateItem(container, position) as Fragment
        var view : View? = f.view
        if (view != null) {
            container.addView(view)
        }
        return f
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        var view : View? = null
        if (`object` is Fragment) {
            view = `object`.view
        }
        if (view == null && position < list.size) {
            view = list[position].view
        }
        if (view != null && container != null) {
            container.removeView(view)
        }
    }

}