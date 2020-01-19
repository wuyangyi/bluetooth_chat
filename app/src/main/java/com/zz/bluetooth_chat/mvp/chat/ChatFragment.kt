package com.zz.bluetooth_chat.mvp.chat

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevin.crop.UCrop
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment
import com.zz.bluetooth_chat.base.base_adapter.CommonAdapter
import com.zz.bluetooth_chat.base.base_adapter.MultiItemTypeAdapter
import com.zz.bluetooth_chat.base.base_adapter.ViewHolder
import com.zz.bluetooth_chat.bean.ChatBean
import com.zz.bluetooth_chat.bean.ChatBottomMenuBean
import com.zz.bluetooth_chat.bean.UserBean
import com.zz.bluetooth_chat.util.DateUtil
import com.zz.bluetooth_chat.util.DeviceUtils
import com.zz.bluetooth_chat.util.DeviceUtils.bottomMenuHeight
import com.zz.bluetooth_chat.util.ToastUtils
import com.zz.bluetooth_chat.util.Utils
import com.zz.bluetooth_chat.widget.OnDownRecyclerView
import com.zz.bluetooth_chat.widget.SoftKeyBoardListener
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 聊天
 */

class ChatFragment : BaseFragment<ChatPresenter>(), ChatContract.View, ChatItem.OnViewLongClick, ChatItem.ImageClick, ChatItem.SoundClick {
    private var mAdapter: RecyclerView.Adapter<ViewHolder>? = null
    private var layoutManager: LinearLayoutManager? = null
    private var user : UserBean? = null //当前聊天的用户
    private val mListBean = ArrayList<ChatBean>()
    private var mSoundPlayerIsMe: Boolean = false //当前播放的是否是我的
    private var bottomMenuIsShow = false //底部菜单是否已显示
    private var bottomShowByEmil = false //底部菜单显示是通过表情按钮
    private var keyBorderIsShow = false //软键盘是否已显示
    private var isInput = true //是否是键盘输入



    private lateinit var valueAnimator: ValueAnimator
    private var fileName: String? = null

    private var adapter: CommonAdapter<ChatBottomMenuBean>? = null
    private var chatBottomMenuBeans = arrayListOf<ChatBottomMenuBean>(
        ChatBottomMenuBean("相册", R.mipmap.ico_photo),
        ChatBottomMenuBean("相机", R.mipmap.ico_photo_camera),
        ChatBottomMenuBean("名片", R.mipmap.ico_photo),
        ChatBottomMenuBean("位置", R.mipmap.ico_photo_camera),
        ChatBottomMenuBean("我的收藏", R.mipmap.ico_photo),
        ChatBottomMenuBean("气泡设置", R.mipmap.ico_photo_camera)
    )

    companion object{
        fun newInstance(bundle: Bundle): ChatFragment {
            val fragment = ChatFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments!!.getParcelable(ChatActivity.CHAT_USER_INFO)
    }

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_chat
    }

    override fun setCenterTitle(): String {
        if (user?.name!!.isEmpty()) {
            return user!!.deviceName
        }
        return user!!.name
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

    override fun initView(rootView: View) {
        layoutManager = LinearLayoutManager(context)
        layoutManager!!.orientation = LinearLayoutManager.VERTICAL
        layoutManager!!.stackFromEnd = true //列表再底部开始展示，反转后由上面开始展示
        layoutManager!!.reverseLayout = true //列表翻转
        rv_list.layoutManager = layoutManager
        mAdapter = getAdapter()
        rv_list.adapter = mAdapter
        initListener()

    }

    private fun initListener() {
        et_import.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showButton(et_import.text.toString().isNotEmpty())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //软键盘监听
        SoftKeyBoardListener.setListener(
            activity!!,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    Log.d("软键盘高度", "$height")
                    bottomMenuHeight = height
                    keyBorderIsShow = true

                    if(!bottomMenuIsShow) {
                        showAnimatorBottomMenu()
                    } else {
                        if(height > ll_menu.layoutParams.height) {
                            setBottomMenuHeight(height)
                        }
                    }
                    bottomMenuIsShow = false
                    rv_list.scrollToPosition(0)
                    iv_emil.setImageDrawable(resources.getDrawable(R.mipmap.ico_chat_face))
                    iv_more.setImageDrawable(resources.getDrawable(R.mipmap.ico_helper_more))
                }

                override fun keyBoardHide(height: Int) {
                    Log.d("软键盘高度1", "$height")
                    keyBorderIsShow = false
                    if (!bottomMenuIsShow) {
//                        hideAnimatorBottomMenu()
                        setBottomMenuHeight(0)
                    }
                }
            })

        //录制完成回调
        tvDownSpeck.setOnWellFinish { path, time ->
            sendMessage(path, time, ChatBean.SEND_MESSAGE_SOUND)
        }
        
        rv_list.setOnRecyclerDownClick(object : OnDownRecyclerView.OnRecyclerDownClick{
            override fun onDownClickListener() {
                if (keyBorderIsShow) {
                    bottomMenuIsShow = false
                    DeviceUtils.hideSoftKeyboard(context!!, et_import)
                } else {
                    if (bottomMenuIsShow) {
                        hideAnimatorBottomMenu()
                    }
                }
            }
        })

        iv_emil.setOnClickListener {
            keyBorderAndSpeak(true)
            showMenu(true)
        }

        iv_more.setOnClickListener {
            keyBorderAndSpeak(true)
            showMenu(false)
        }

        ivDown.setOnClickListener {
            keyBorderAndSpeak(!isInput)
        }

        //发送文本信息
        bt_send.setOnClickListener {
            sendMessage(et_import.text.toString(),  0f, ChatBean.SEND_MESSAGE_TEXT)
        }
    }

    //点击表情或者更多
    private fun showMenu(emil : Boolean) {
        rvMenu.adapter = initMenuAdapter()
        if (bottomMenuIsShow) {
            if (!keyBorderIsShow && if (emil) bottomShowByEmil else !bottomShowByEmil) {
                hideAnimatorBottomMenu()
            } else {
                bottomShowByEmil = emil
            }
            DeviceUtils.hideSoftKeyboard(context!!, et_import)
        } else {
            bottomShowByEmil = emil
            if (!keyBorderIsShow) {
                showAnimatorBottomMenu()
            } else {
                bottomMenuIsShow = true
            }
            DeviceUtils.hideSoftKeyboard(context!!, et_import)

        }
        iv_emil.setImageDrawable(resources.getDrawable(if (bottomShowByEmil && bottomMenuIsShow) R.mipmap.ico_chat_face_select else R.mipmap.ico_chat_face))
        iv_more.setImageDrawable(resources.getDrawable(if (!bottomShowByEmil && bottomMenuIsShow) R.mipmap.ico_helper_more_select else R.mipmap.ico_helper_more))
    }

    /**
     * 语音和键盘输入切换
     */
    private fun keyBorderAndSpeak(input: Boolean){
        isInput = input
        if (isInput) {
            et_import.visibility = View.VISIBLE
            tvDownSpeck.visibility = View.GONE
        } else {
            if (keyBorderIsShow) {
                bottomMenuIsShow = false
                DeviceUtils.hideSoftKeyboard(context!!, et_import)
            } else {
                if (bottomMenuIsShow) {
                    hideAnimatorBottomMenu()
                }
            }
            et_import.visibility = View.GONE
            tvDownSpeck.visibility = View.VISIBLE
        }
        ivDown.setImageDrawable(resources.getDrawable(if (isInput) R.mipmap.ico_speck else R.mipmap.ico_chat_keyborder))
    }

    /**
     * 发布按钮的显示
     * @param isCanSend
     */
    private fun showButton(isCanSend: Boolean) {
        bt_send.visibility = if (isCanSend) View.VISIBLE else View.GONE
        iv_more.visibility = if (isCanSend) View.GONE else View.VISIBLE
        iv_emil.visibility = if (isCanSend) View.GONE else View.VISIBLE
    }

    //判断所有子控件的高度是否大于RecyclerView的高度
    private fun childBigToList(): Boolean {
        var childHeight = 0
        for (i in mListBean.indices) {
            childHeight += rv_list.getChildAt(i).height
            if (childHeight >= rv_list.height) {
                return true
            }
        }
        return false
    }

    private fun getAdapter() : RecyclerView.Adapter<ViewHolder> {
        var adapter = MultiItemTypeAdapter<ChatBean>(context!!, mListBean)
        var chatItem = ChatItem(context!!, mListBean)
        chatItem.setImageClick(this)
        chatItem.setOnViewLongClick(this)
        chatItem.setSoundClick(this)
        adapter.addItemViewDelegate(chatItem)
        var chatOtherItem = ChatOtherItem(context!!, mListBean)
        chatOtherItem.setImageClick(this)
        chatOtherItem.setOnViewLongClick(this)
        chatOtherItem.setSoundClick(this)
        adapter.addItemViewDelegate(chatOtherItem)
        return adapter
    }

    /**
     * 设置底部菜单栏的高度
     */
    private fun setBottomMenuHeight(height: Int) {
        var params = ll_menu.layoutParams
        params.height = height
        ll_menu.layoutParams = params
    }

    /**
     * 动画显示底部菜单
     */
    private fun showAnimatorBottomMenu() {
        bottomMenuIsShow = true
        valueAnimator = ValueAnimator.ofInt(0, bottomMenuHeight)
        valueAnimator.addUpdateListener {
            setBottomMenuHeight(it.animatedValue as Int)
            rv_list.scrollToPosition(0)
        }
        valueAnimator.duration = 100
        valueAnimator.start()
    }

    /**
     * 动画隐藏底部菜单
     */
    private fun hideAnimatorBottomMenu() {
        bottomMenuIsShow = false
        valueAnimator = ValueAnimator.ofInt(bottomMenuHeight, 0)
        valueAnimator.addUpdateListener {
            setBottomMenuHeight(it.animatedValue as Int)
        }
        valueAnimator.duration = 100
        valueAnimator.start()
        iv_emil.setImageDrawable(resources.getDrawable(R.mipmap.ico_chat_face))
        iv_more.setImageDrawable(resources.getDrawable(R.mipmap.ico_helper_more))
    }

    override fun initData() {
    }

    override fun onViewLongClickListener(x: Int, y: Int, position: Int, isMe: Boolean) {
    }

    override fun onImageClickListener(path: String) {
    }

    override fun OnSoundClickListener(path: String, imageView: ImageView, isMe: Boolean) {
    }

    //发送消息
    private fun sendMessage(content: String, soundTime: Float, type: Int) {
        val chatBean = ChatBean(
            id = 0,
            user = user,
            isMeSend = true,
            content = content,
            sendTime = DateUtil.getNowTime(),
            create_time = DateUtil.getCurrentTimeMillis(),
            soundTime = soundTime,
            type = type)
        mListBean.add(0, chatBean)
        mAdapter!!.notifyDataSetChanged()
        et_import.text.clear()
    }

    /**
     * 初始化底部菜单适配器
     */
    private fun initMenuAdapter() : RecyclerView.Adapter<ViewHolder> {
        adapter = object : CommonAdapter<ChatBottomMenuBean>(
            context!!, R.layout.item_chat_botton_menu,
            chatBottomMenuBeans
        ) {
            override fun convert(holder: ViewHolder, t: ChatBottomMenuBean, position: Int) {
                holder.getImageViwe(R.id.iv_menu)
                    .setImageDrawable(resources.getDrawable(t.image))
                holder.getTextView(R.id.tv_title).text = t.title
            }
        }

        val layoutManager = GridLayoutManager(
            context,
            4
        )
        rvMenu.layoutManager = layoutManager
        adapter!!.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener{
            override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                when (position) {
                    0 -> { //相册
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermission(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), "关闭该权限后部分功能将无法使用，是否继续？", REQUEST_STORAGE_READ_ACCESS_PERMISSION
                            )
                        } else {
                            Utils.pickFromGallery(activity!!)
                        }
                    }
                    1 -> { //相机
                        verifyStoragePermissions(activity!!)
                    }
                    2 -> { //名片
                        ToastUtils.showToast("正在建设中~")
                    }
                    3 -> { //位置
                        ToastUtils.showToast("正在建设中~")

                    }
                    4 -> { //我的收藏
                        ToastUtils.showToast("正在建设中~")

                    }
                    5 -> { //气泡设置
                        ToastUtils.showToast("正在建设中~")

                    }
                }
            }

            override fun onItemLongClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ): Boolean {
                return false
            }
        })
        return adapter!!
    }

    //相机
    fun verifyStoragePermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), "您已禁用相机权限，当前无法打开相机，是否打开权限？", REQUEST_STORAGE_CAMERA_TAKE_PHOTO
            )
        } else {
            fileName = Utils.takePicture(activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var selectedUri :Uri? = null
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Utils.REQUEST_SELECT_PICTURE) { //相册
                selectedUri = data?.data
            }  else if (requestCode == REQUEST_STORAGE_CAMERA_TAKE_PHOTO) { //相机
                selectedUri = Uri.fromFile(File(fileName))
            }
        }

        if (selectedUri != null) {
            var bmp = MediaStore.Images.Media.getBitmap(
                activity!!.contentResolver,
                selectedUri
            )
            sendMessage(Utils.convertIconToString(bmp), 0f, ChatBean.SEND_MESSAGE_IMAGE)
        } else {
            ToastUtils.showToast("未找到图片~")
        }
    }


}