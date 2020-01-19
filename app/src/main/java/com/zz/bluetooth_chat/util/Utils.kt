package com.zz.bluetooth_chat.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.zz.bluetooth_chat.BuildConfig
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment.Companion.REQUEST_STORAGE_CAMERA_TAKE_PHOTO
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 工具类
 */

object Utils {
    var APP_SYSTEM_PATH = Environment.getExternalStorageDirectory().toString() + "/趣聊/"//app文件目录
    var galleryPath = APP_SYSTEM_PATH + "Camera/" //图片保存目录
    val REQUEST_SELECT_PICTURE = 0x01 //相册选择图片
    val REQUEST_TAKE_PICTURE = 0x02 //相机拍照


    /**
     * 保存图片
     * @param bmp
     * @param picName
     * @param mContext
     */
    fun saveBmp2Gallery(bmp: Bitmap, picName: String, mContext: Context) {

        var fileName: String? = null

        // 声明文件对象
        var file: File? = null
        // 声明输出流
        var outStream: FileOutputStream? = null

        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = File(galleryPath, "$picName.jpg")

            // 获得文件相对路径
            fileName = file.toString()
            // 获得输出流，如果文件中有内容，追加内容
            outStream = FileOutputStream(fileName)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, outStream)
            ToastUtils.showToast("保存成功")
        } catch (e: Exception) {
            e.stackTrace
            ToastUtils.showToast("保存失败")
        } finally {
            try {
                outStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        //通知相册更新
        try {
            MediaStore.Images.Media.insertImage(
                mContext.contentResolver,
                bmp, fileName, null
            )
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(file)
            intent.data = uri
            mContext.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 选择图片
     */
    fun pickFromGallery(activity: Activity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        activity.startActivityForResult(
            Intent.createChooser(intent, "选择图片"),
            REQUEST_SELECT_PICTURE
        )
    }

    //拍照
    fun takePicture(activity: Activity): String {
        val state = Environment.getExternalStorageState()
        var picFileFullName = ""
        if (state == Environment.MEDIA_MOUNTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val outDir = File(galleryPath)
            if (!outDir.exists()) {
                outDir.mkdirs()
            }
            val outFile = File(outDir, System.currentTimeMillis().toString() + ".jpg")
            picFileFullName = outFile.absolutePath
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(
                    activity.applicationContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    outFile
                )
            )
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            activity.startActivityForResult(intent, REQUEST_STORAGE_CAMERA_TAKE_PHOTO)
        } else {
            /*Log.e(TAG, "请确认已经插入SD卡");*/
        }
        return picFileFullName
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        val sdkVersion = Build.VERSION.SDK_INT
        return if (sdkVersion >= 19) { // api >= 19
            getRealPathFromUriAboveApi19(context, uri)
        } else { // api < 19
            getRealPathFromUriBelowAPI19(context, uri)
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
        return getDataColumn(context, uri, null, null)
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                val id =
                    documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                filePath = getDataColumn(
                    context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selection,
                    selectionArgs
                )
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(documentId)
                )
                filePath = getDataColumn(context, contentUri, null, null)
            }
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null)
        } else if ("file" == uri.scheme) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.path
        }
        return filePath
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var path: String? = null

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            cursor?.close()
        }

        return path
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * 根据本地图片地址获取Bitmap
     * @param path
     * @return
     */
    fun getBitmapForPath(path: String): Bitmap {
        val bmpDefaultPic: Bitmap
        bmpDefaultPic = BitmapFactory.decodeFile(path, null)
        return bmpDefaultPic
    }

    /**
     * 根据图片名称获取bitmap
     * @param name
     * @return
     */
    fun getBitmapByName(context: Context, name: String): Bitmap {
        val resID = context.resources.getIdentifier(name, "mipmap", context.packageName)
        return BitmapFactory.decodeResource(context.resources, resID)
    }

    /**
     * 复制到剪切板
     */
    fun Copy(context: Context, content: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", content)
        clipboardManager.setPrimaryClip(clipData)
        ToastUtils.showToast("复制成功")

    }


    /**
     * 图片转为字符串
     */
    fun convertIconToString(bitmap: Bitmap): String {
        var baos = ByteArrayOutputStream() // outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos)
        var appicon = baos.toByteArray() // 转为byte数组 return
        return Base64.encodeToString(appicon, Base64.DEFAULT)
    }

    /**
     * 字符串转为bitmap
     */
    fun convertStringToIcon(st: String) : Bitmap? {
        var bitmap : Bitmap? = null
        return try {
            val bitmapArray: ByteArray = Base64.decode(st, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 设置用户头像
     */
    fun setUserHead(iv: ImageView, context: Context) {
        val bitmap = convertStringToIcon(AppConfig.user!!.logo)
        if (bitmap == null) {
            iv.setImageDrawable(context.resources.getDrawable(R.mipmap.pic_default_secret))
        } else {
            iv.setImageBitmap(bitmap)
        }
    }

    /**
     * 设置对方用户头像
     */
    fun setOtherUserHead(iv: ImageView, context: Context, image: String) {
        val bitmap = convertStringToIcon(image)
        if (bitmap == null) {
            iv.setImageDrawable(context.resources.getDrawable(R.mipmap.pic_default_secret))
        } else {
            iv.setImageBitmap(bitmap)
        }
    }
}