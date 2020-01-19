package com.zz.bluetooth_chat.mvp.mine.user_info

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.kevin.crop.UCrop
import com.zz.bluetooth_chat.BuildConfig
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment
import com.zz.bluetooth_chat.bean.BottomListSelectBean
import com.zz.bluetooth_chat.event.UpdateEvent
import com.zz.bluetooth_chat.mvp.crop.CropActivity
import com.zz.bluetooth_chat.util.AppConfig
import com.zz.bluetooth_chat.util.DeviceUtils
import com.zz.bluetooth_chat.util.ToastUtils
import com.zz.bluetooth_chat.util.Utils
import com.zz.bluetooth_chat.util.Utils.REQUEST_SELECT_PICTURE
import com.zz.bluetooth_chat.widget.adapter.BottomListSelectAdapter
import com.zz.bluetooth_chat.widget.dialog.LoadingDialog
import com.zz.bluetooth_chat.widget.dialog.SelectionDialogFragment
import com.zz.bluetooth_chat.widget.popwindow.BottomSelectPopupWindow
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.fragment_user_info.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.collections.ArrayList

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 账号信息
 */

class UserInfoFragment : BaseFragment<UserInfoPresenter>(), UserInfoContract.View, LoadingDialog.LoadingCallback {
    private var headImage: String = ""
    private var mSex: Int = 0

    private var fileName: String? = null

    private var loadSuccess = false
    private var sexPopupWindow: BottomSelectPopupWindow? = null

    // 剪切后图像文件
    private var mDestinationUri: Uri? = null

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_user_info
    }

    override fun setCenterTitle(): String {
        return "账号信息"
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

    override fun setRightText(): String {
        return "保存"
    }

    override fun onRightTextClick() {
        mWaiteDialog.showStateIng("正在保存")
        AppConfig.user?.logo = headImage
        AppConfig.user?.sex = mSex
        AppConfig.user?.name = edit_name.getEdtCenter().text.toString()
        AppConfig.user?.synopsis = edt_synopsis.text.toString()
        AppConfig.user?.saveUserData(activity!!)
        loadSuccess = true
        EventBus.getDefault().post(UpdateEvent(true, UpdateEvent.USER_INFO_UPDATE))
        mWaiteDialog.showStateSuccess("保存成功")
    }

    override fun initView(rootView: View) {
        if (AppConfig.user != null) {
            headImage = AppConfig.user?.logo ?: ""
            mSex = AppConfig.user?.sex ?: 0
        }

        if (headImage.isNotEmpty()) {
            var bitmap = Utils.convertStringToIcon(headImage)
            if (bitmap != null) {
                iv_head.setImageBitmap(bitmap)
            }
        }

        edit_name.getEdtCenter().setText(AppConfig.user?.name ?: "")
        initSex()
        edt_synopsis.setText(AppConfig.user?.synopsis ?: "")
        edt_synopsis.setSelection(edt_synopsis.editableText.length)

        mWaiteDialog.setCallBackClick(this)

        ib_sex.setOnClickListener{
            showSexSelector()
        }

        iv_head.setOnClickListener {
            showHeadSelector()
        }
        mDestinationUri = Uri.fromFile(File(activity!!.cacheDir, "cropImage.jpeg"))
    }

    private fun initSex() {
        var res : Int = R.mipmap.ic_privary
        when (mSex) {
            1 -> {
                ib_sex.getIvLeft().visibility = View.VISIBLE
                res = R.mipmap.ico_man
                ib_sex.getTvLeft().text = "男"
            }
            2 -> {
                ib_sex.getIvLeft().visibility = View.VISIBLE
                res = R.mipmap.ico_woman
                ib_sex.getTvLeft().text = "女"
            }
            else -> {
                ib_sex.getIvLeft().visibility = View.VISIBLE
                res = R.mipmap.ic_privary
                ib_sex.getTvLeft().text = "保密"
            }
        }
        var drawable = resources.getDrawable(res)
//        drawable.setBounds(0, 0, DeviceUtils.dp2px(context!!, 18f), DeviceUtils.dp2px(context!!, 18f))
        ib_sex.getIvLeft().setImageDrawable(drawable)
    }

    override fun initData() {

    }

    override fun loadCallback() {
        if (loadSuccess) {
            activity!!.finish()
        }
    }

    /**
     * 性别选择
     */
    private fun showSexSelector() {
        if(sexPopupWindow == null) {
            var selectPosition = mSex
            val menus = resources.getStringArray(R.array.sex_menus)
            var adapter = BottomListSelectAdapter(context!!, R.layout.item_bottom_select_list, BottomListSelectBean.getDateByString(
                menus.toList() as ArrayList<String>, selectPosition))
            adapter.setEducationSelectClick(object: BottomListSelectAdapter.BottomListSelectClick{
                override fun onBottomListSelectClickListener(index: Int) {
                    val sex = menus[index]
                    mSex = index
                    initSex()
                    adapter.notifyDataSetChanged()
                    sexPopupWindow!!.hide()
                }
            })

            sexPopupWindow = BottomSelectPopupWindow.builder()
                .isOutsideTouch(true)
                .isFocus(true)
                .with(activity!!)
                .title("性别选择")
                .desc("请选择您的性别")
                .adapter(adapter)
                .build()
        }
        sexPopupWindow!!.show()
    }

    /**
     * 头像选择
     */
    private fun showHeadSelector() {
        val menus = resources.getStringArray(R.array.head_menus)
        var fragment = SelectionDialogFragment().setTitle("头像选择")
            .setMenusWithMenuSelectedListener(menus, object : SelectionDialogFragment.OnMenuSelectedListener{
                override fun onMenuSelected(index: Int, dialogInterface: DialogInterface) {
                    when(index){
                        0 -> {
                            headImage = ""
                            iv_head.setImageDrawable(resources.getDrawable(R.mipmap.pic_default_secret))
                        }
                        1 -> {
                            requestTakePicture(activity!!)
                        }
                        2 -> {
                            verifyStoragePermissions(activity!!)
                        }
                    }
                }
            })
        fragment.show(childFragmentManager, "showHeadSelector")
    }


    //相册
    private fun requestTakePicture(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), "您已禁用内存读写权限，当前无法打开相册，是否打开权限？", REQUEST_STORAGE_READ_ACCESS_PERMISSION
            )
        } else {
            Utils.pickFromGallery(activity)
        }
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) { //相册
                val selectedUri = data?.data
                if (selectedUri != null) {
                    startCropActivity(selectedUri)
                } else {
                    ToastUtils.showToast("无法剪切选择图片")
                }
            } else if (requestCode == UCrop.REQUEST_CROP) { //图片裁剪
                handleCropResult(data!!)
            } else if (requestCode == REQUEST_STORAGE_CAMERA_TAKE_PHOTO) { //相机
                val selectedUri = Uri.fromFile(File(fileName))
                if (selectedUri != null) {
                    startCropActivity(selectedUri)
                } else {
                    ToastUtils.showToast("无法剪切选择图片")
                }
            }
        }
    }

    /**
     * 开始剪切图片
     * @param uri
     */
    private fun startCropActivity(uri: Uri) {
        UCrop.of(uri, mDestinationUri!!)
            .withTargetActivity(CropActivity::class.java)
            .withAspectRatio(1f, 1f)
            //                .withMaxResultSize(300, 300)
            .start(activity!!)
    }

    /**
     * 处理剪切后的返回值
     * @param result
     */
    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            val bmp: Bitmap
            try {
                bmp = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, resultUri)
                iv_head.setImageBitmap(bmp)
                headImage = Utils.convertIconToString(bmp)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(activity, "无法剪切选择图片", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dissMissPop(sexPopupWindow)
    }

}