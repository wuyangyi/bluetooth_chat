package com.zz.bluetooth_chat.widget.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.zz.bluetooth_chat.R

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 底部弹出相册相机的dialog
 */

class SelectionDialogFragment : DialogFragment, AdapterView.OnItemClickListener {
    private var menus: Array<String>? = null
    private var title: String? = null
    private lateinit var mListView: ListView
    private lateinit var titleView: TextView
    private var selectedIndex: Int = 0

    private var listener: OnMenuSelectedListener? = null

    constructor() {
        setStyle(STYLE_NO_TITLE,  R.style.info_dialog)
    }

    fun setTitle(title: String): SelectionDialogFragment {
        this.title = title
        return this
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val params = window!!.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = params
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setMenusWithMenuSelectedListener(
        menus: Array<String>,
        listener: OnMenuSelectedListener
    ): SelectionDialogFragment {
        this.menus = menus
        this.listener = listener
        return this
    }

    fun setMenusWithMenuSelectedListener(
        menus: Array<String>,
        selectedIndex: Int,
        listener: OnMenuSelectedListener
    ): SelectionDialogFragment {
        this.menus = menus
        this.selectedIndex = selectedIndex
        this.listener = listener
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_selection_dialog_layout, null)
        init(rootView)
        return rootView
    }

    private fun init(rootView: View) {
        mListView = rootView.findViewById<View>(R.id.menu_list_id) as ListView
        mListView.adapter = MenuAdapter()
        mListView.onItemClickListener = this
        titleView = rootView.findViewById<View>(R.id.title_view_id) as TextView
        mListView.setSelection(selectedIndex)
        titleView.text = this.title
        rootView.findViewById<View>(R.id.tv_cancel).setOnClickListener { dismiss() }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.dismiss()
        mListView.setSelection(selectedIndex)
        if (listener != null) {
            listener!!.onMenuSelected(position, dialog!!)
        }
    }

    interface OnMenuSelectedListener {
        fun onMenuSelected(index: Int, dialogInterface: DialogInterface)
    }


    inner class MenuAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return if (menus == null) {
                0
            } else menus!!.size
        }

        override fun getItem(position: Int): Any {
            return menus!![position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView =
                    LayoutInflater.from(activity).inflate(R.layout.selection_item_layout, null)
            }
            val titleView = convertView!!.findViewById<View>(R.id.title_text_id) as TextView
            titleView.text = getItem(position).toString()
            if (position != count - 1) {
                convertView.setBackgroundResource(R.drawable.selection_item_bg)
            } else {
                convertView.setBackgroundResource(R.drawable.selection_end_item_bg)
            }
            convertView.isSelected = position == selectedIndex
            return convertView
        }
    }
}