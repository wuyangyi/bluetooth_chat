package com.zz.bluetooth_chat.base

import android.animation.Animator
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK
import androidx.fragment.app.Fragment
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.bean.UserBean
import com.zz.bluetooth_chat.mvp.home.HomeActivity
import com.zz.bluetooth_chat.util.*
import com.zz.bluetooth_chat.widget.dialog.LoadingDialog
import de.greenrobot.event.EventBus

/**
 * Fragment的基类
 * @param <P>
</P> */
abstract class BaseFragment<P : IBasePresenter> : Fragment(), BaseView {
    protected lateinit var mRootView: View
    protected lateinit var mActivity: Activity
    protected lateinit var mPresenter: P
    protected lateinit var mLayoutInflater: LayoutInflater
    protected lateinit var mIbLeftImage: ImageButton //返回按钮
    protected lateinit var mTvCenterTitle: TextView //中间标题
    protected lateinit var mIbRightImage: ImageButton //右边的图片按钮
    protected lateinit var mLlTitle: View //标题栏布局
    protected lateinit var mLeftText: TextView //左边文字
    protected lateinit var mRightText: TextView //右边文字

    /**
     * 加载弹框
     */
    protected lateinit var mWaiteDialog: LoadingDialog
    /**
     * 当沉浸式状态栏时， 状态栏的占位控件
     */
    protected lateinit var mStatusPlaceholderView: View

    private var mAlertDialog: AlertDialog? = null

    /**
     * 加载
     */
    protected var mCenterLoadingView: View? = null

    /**
     * 蓝牙
     */
    public var mBluetoothAdapter : BluetoothAdapter? = null

    // 是否添加和状态栏等高的占位 View
    // 在需要显示toolbar时，进行添加
    // 状态栏顶上去
    // 状态栏不顶上去
    // 是否设置状态栏文字图标灰色，对 小米、魅族、Android 6.0 及以上系统有效
    // 加载动画
    protected fun getContentView(): View {
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        if (setUseStatusBar() && setUseStatusView()) {
            mStatusPlaceholderView = View(context)
            mStatusPlaceholderView.layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    DeviceUtils.getStatuBarHeight(context!!, activity!!)
                )
            if (!(StatusBarUtils.intgetType(activity?.window) != 0 && ContextCompat.getColor(
                    this.context!!,
                    setToolBarBackGround()
                ) != Color
                    .WHITE)
            ) {
                mStatusPlaceholderView.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.main_color
                    )
                )
            } else {
                mStatusPlaceholderView.setBackgroundColor(
                    ContextCompat.getColor(
                        this.context!!,
                        setToolBarBackGround()
                    )
                )
            }
            linearLayout.addView(mStatusPlaceholderView)
        }
        if (showToolbar()) {
            val toolBarContainer = mLayoutInflater.inflate(getToolBarLayoutId(), null)
            initDefaultToolBar(toolBarContainer)
            linearLayout.addView(toolBarContainer)
        }
        if (setUseStatusBar()) {
            StatusBarUtils.transparencyBar(activity!!)
            linearLayout.fitsSystemWindows = false
        } else {
            StatusBarUtils.setStatusBarColor(activity!!, setToolBarBackGround())
            linearLayout.fitsSystemWindows = true
        }
        if (setStatusBarGrey()) {
            StatusBarUtils.statusBarLightMode(activity!!)
        } else {
            StatusBarUtils.statusBarLightModeWhile(activity!!)
        }
        val bodyContainer = mLayoutInflater.inflate(getBodyLayoutId(), null)
        bodyContainer.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        if (setUseCenterLoading()) {
            val frameLayout = FrameLayout(mActivity)
            frameLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            frameLayout.addView(bodyContainer)

            mCenterLoadingView = mLayoutInflater.inflate(R.layout.view_center_loading, null)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            if (!showToolbar()) {
                params.setMargins(0, getstatusbarAndToolbarHeight(), 0, 0)
            }
            mCenterLoadingView!!.layoutParams = params
            if (setUseCenterLoadingAnimation()) {
                ((mCenterLoadingView!!.findViewById(R.id.iv_center_load) as ImageView).drawable as AnimationDrawable).start()
            }
            frameLayout.addView(mCenterLoadingView)
            linearLayout.addView(frameLayout)

        } else {
            linearLayout.addView(bodyContainer)
        }
        mWaiteDialog = LoadingDialog(activity)
        return linearLayout
    }


    /**
     * 获取toolbar的布局文件,如果需要返回自定义的toolbar布局，重写该方法；否则默认返回缺省的toolbar
     */
    protected open fun getToolBarLayoutId(): Int {
        return R.layout.fragment_title
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(registerEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mLayoutInflater = inflater
        mRootView = getContentView()
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(mRootView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (registerEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 获取状态栏和操作栏的高度
     *
     * @return
     */
    protected open fun getstatusbarAndToolbarHeight(): Int {
        return 0
    }

    /**
     * @return 是否需要中心加载动画，对应
     */
    protected open fun setUseCenterLoadingAnimation(): Boolean {
        return true
    }

    /**
     * 是否开启中心加载动画
     * @return
     */
    protected open fun setUseCenterLoading(): Boolean {
        return false
    }

    /**
     * 关闭中心放大缩小加载动画
     */
    protected fun closeLoadingView() {
        if (mCenterLoadingView == null) {
            return
        }
        if (mCenterLoadingView!!.visibility == View.VISIBLE) {
            ((mCenterLoadingView!!.findViewById(R.id.iv_center_load) as ImageView).drawable as AnimationDrawable).stop()
            mCenterLoadingView!!.animate().alpha(0.3f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {

                    }

                    override fun onAnimationEnd(animator: Animator) {
                        if (mCenterLoadingView != null) {
                            mCenterLoadingView!!.visibility = View.GONE
                        }
                    }

                    override fun onAnimationCancel(animator: Animator) {

                    }

                    override fun onAnimationRepeat(animator: Animator) {

                    }
                })
        }
    }

    /**
     * 开启中心放大缩小加载动画
     */
    protected fun showLoadingView() {
        if (mCenterLoadingView == null) {
            return
        }
        if (mCenterLoadingView!!.visibility == View.GONE) {
            (mCenterLoadingView!!.findViewById(
                R.id.iv_center_load
            ) as ImageView).visibility = View.VISIBLE
            ((mCenterLoadingView!!.findViewById(
                R.id.iv_center_load
            ) as ImageView).drawable as AnimationDrawable).start()
            mCenterLoadingView!!.alpha = 1f
            mCenterLoadingView!!.visibility = View.VISIBLE
        }
    }

    /**
     * 初始化标题栏
     * @param toolBarContainer
     */
    protected fun initDefaultToolBar(toolBarContainer: View) {
        mIbLeftImage = toolBarContainer.findViewById(R.id.ib_left_image)
        mTvCenterTitle = toolBarContainer.findViewById(R.id.tv_center_title)
        mIbRightImage = toolBarContainer.findViewById(R.id.ib_right_image)
        mLlTitle = toolBarContainer.findViewById(R.id.ll_title)
        mLeftText = toolBarContainer.findViewById(R.id.tv_left_text)
        mRightText = toolBarContainer.findViewById(R.id.tv_right_text)
        if (setLeftImage() == 0) {
            mIbLeftImage.visibility = View.GONE
        } else {
            mIbLeftImage.visibility = View.VISIBLE
            mIbLeftImage.setImageDrawable(resources.getDrawable(setLeftImage()))
        }
        if (setRightImage() == 0) {
            mIbRightImage.visibility = View.GONE
        } else {
            mIbRightImage.visibility = View.VISIBLE
            mIbRightImage.setImageDrawable(resources.getDrawable(setRightImage()))
            mIbRightImage.setOnClickListener(View.OnClickListener { v ->
                if (AntiShakeUtils.isInvalidClick(v)) {
                    return@OnClickListener
                }
                setRightImageClick()
            })
        }
        //标题栏背景
        mLlTitle.setBackgroundColor(resources.getColor(setTitleBg()))
        mTvCenterTitle.text = setCenterTitle()
        mTvCenterTitle.setTextColor(resources.getColor(setCenterTitleColor()))
        mIbLeftImage.setOnClickListener { setLeftImageClickListener() }
        if (setLeftText() == "") {
            mLeftText.visibility = View.GONE
        } else {
            mLeftText.text = setLeftText()
            mLeftText.setTextColor(resources.getColor(setLeftTextColor()))
            mLeftText.setOnClickListener {
                onLeftTextClick()
            }
        }
        if (setRightText() == "") {
            mRightText.visibility = View.GONE
        } else {
            mRightText.text = setRightText()
            mRightText.setTextColor(resources.getColor(setRightTextColor()))
            mRightText.setOnClickListener {
                onRightTextClick()
            }
        }
    }

    protected abstract fun initView(rootView: View)

    protected abstract fun initData()

    /**
     * 获取toolbar下方的布局文件
     */
    protected abstract fun getBodyLayoutId(): Int

    /**
     * 是否显示toolbar,默认显示
     */
    protected open fun showToolbar(): Boolean {
        return true
    }

    /**
     * 设置中间标题
     * @return
     */
    protected open fun setCenterTitle(): String {
        return ""
    }

    protected fun setCenterTitle(title: String) {
        mTvCenterTitle.text = title
    }

    /**
     * 设置左边图片
     * @return
     */
    protected open fun setLeftImage(): Int {
        return R.mipmap.ic_go_back_white
    }

    /**
     * 设置左边文字
     */
    protected open fun setLeftText(): String {
        return ""
    }

    /**
     * 设置左边文字颜色
     */
    protected open fun setLeftTextColor(): Int {
        return R.color.white
    }

    /**
     * 左边文字点击事件
     */
    protected open fun onLeftTextClick() {

    }

    /**
     * 标题栏的背景色
     * @return
     */
    protected open fun setTitleBg(): Int {
        return R.color.main_color
    }

    protected open fun setCenterTitleColor(): Int {
        return R.color.white
    }

    /**
     * 设置右边图片
     * @return
     */
    protected open fun setRightImage(): Int {
        return 0
    }

    /**
     * 设置右边文字
     */
    protected open fun setRightText(): String {
        return ""
    }

    /**
     * 设置右边文字颜色
     */
    protected open fun setRightTextColor(): Int {
        return R.color.white
    }

    /**
     * 右边文字点击事件
     */
    protected open fun onRightTextClick() {

    }

    /**
     * 设置右边图片点击事件
     */
    protected open fun setRightImageClick() {

    }

    /**
     * 左边图片的点击事件
     */
    protected open fun setLeftImageClickListener() {
        mActivity.finish()
    }

    /**
     * 状态栏是否可用
     *
     * @return 默认不可用
     */
    protected open fun setUseStatusBar(): Boolean {
        return false //Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    /**
     * 设置是否需要添加和状态栏等高的占位 view
     *
     * @return
     */
    protected open fun setUseStatusView(): Boolean {
        return true
    }

    /**
     * @return 状态栏 背景
     */
    protected open fun setToolBarBackGround(): Int {
        return R.color.main_color
    }

    /**
     * 状态栏字体默认为白色
     * 支持小米、魅族以及 6.0 以上机型
     *
     * @return
     */
    protected open fun setStatusBarGrey(): Boolean {
        return false
    }


    /**
     * 申请权限
     */
    protected fun requestPermission(
        permission: Array<String>,
        rationale: String,
        requestCode: Int
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission[0])) {
            showAlertDialog("提示", rationale,
                DialogInterface.OnClickListener { _, _ ->
                    ActivityCompat.requestPermissions(
                        activity!!,
                        permission, requestCode
                    )
                }, "确定", null, "取消"
            )
        } else {
            ActivityCompat.requestPermissions(activity!!, permission, requestCode)
        }
    }

    /**
     * 再次确定
     */
    private fun showAlertDialog(
        @Nullable title: String, @Nullable message: String,
        @Nullable onPositiveButtonClickListener: DialogInterface.OnClickListener,
        @NonNull positiveText: String,
        @Nullable onNegativeButtonClickListener: DialogInterface.OnClickListener?,
        @NonNull negativeText: String
    ) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener)
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener)
        mAlertDialog = builder.show()
    }

    //使用动画跳转
    protected open fun useAnimationIntent() {
        if (activity is AnimationClick) {
            (activity as AnimationClick).animation()
        }
    }

    protected fun goHome(isFinish: Boolean) {
        startActivity(Intent(mActivity, HomeActivity::class.java))
        if (isFinish) {
            mActivity.finish()
        }
    }

    protected open fun dissMissPop(popupWindow: PopupWindow?) {
        if (popupWindow != null && popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * 检测设备是否支持蓝牙
     */
    protected fun checkSupportBlue() : Boolean {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        }
        if (mBluetoothAdapter == null){
            ToastUtils.showToast("该设备不支持蓝牙~")
            return false
        }
        return true
    }

    /**
     * 打开蓝牙
     */
    protected fun openBlue() {
        if (!checkSupportBlue()) {
            return
        }
        if (!checkBlueIsOpen()){ //蓝牙未开启
            var blueIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(blueIntent, REQUEST_ENABLE_BT)
        }
    }

    /**
     * 检测蓝牙是否打开
     */
    protected fun checkBlueIsOpen() : Boolean {
        if (!checkSupportBlue()) {
            return false
        }
        if(mBluetoothAdapter!!.isEnabled) {
            initUserInfo()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == -1) { //已开启蓝牙
                openBlueSuccess()
            } else {
                openBlueFail()
            }
        }
    }

    //打开蓝牙成功的回调
    protected open fun openBlueSuccess(){
        initUserInfo()
    }

    //初始化(必须打开蓝牙后才能调用)
    private fun initUserInfo() {
        var myName = mBluetoothAdapter!!.name
        Log.i("蓝牙名称", myName?:"")
        if (AppConfig.user == null || AppConfig.user?.deviceId == "") {
            var myAddress = mBluetoothAdapter!!.address
            Log.i("蓝牙地址", myAddress)
            var nameLength = context!!.resources.getInteger(R.integer.name_length)
            val name = if (myName.length > nameLength) myName.substring(0, nameLength) else myName
            Log.i("昵称", name)
            var user = UserBean(deviceId = myAddress, deviceName = myName, onLine = true, name = name)
            user.saveUserData(activity!!)
            AppConfig.user = user
        }
        if (AppConfig.user?.deviceName != myName){
            AppConfig.user?.deviceName = myName
            AppConfig.user!!.saveUserData(activity!!)
        }
    }

    /**
     * 是否在此页面注册EventBus
     * 注意：这个只需要在有onEvent接收消息的地方使用
     * 在EventBus.getDefault().post()地方不用使用
     */
    protected open fun registerEventBus(): Boolean {
        return false
    }

    protected fun upBlueName() {

    }

    //打开蓝牙失败的回调
    protected open fun openBlueFail(){
        ToastUtils.showToast("蓝牙打开失败！")
    }

    companion object {

        public val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
        public val REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102
        public val REQUEST_RECORD_AUDIO = 103
        val REQUEST_STORAGE_CAMERA_TAKE_PHOTO = 104 //相机
        val REQUEST_ENABLE_BT = 105 //蓝牙
    }

}
