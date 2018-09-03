package com.gxtc.huchuan.ui.live.hostpage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.FileUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant.requestCode.REQUEST_CODE_AVATAR
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CreateLiveBean
import com.gxtc.huchuan.bean.LiveRoomBean
import com.gxtc.huchuan.bean.UploadResult
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.LoadHelper
import com.gxtc.huchuan.http.service.LiveApi
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.JumpPermissionManagement
import com.gxtc.huchuan.utils.LoginErrorCodeUtil
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.model.FunctionConfig
import com.luck.picture.lib.model.FunctionOptions
import com.luck.picture.lib.model.PictureConfig
import kotlinx.android.synthetic.main.activity_edit_live_host_introduce.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File

/**
 * Created by zzg on 2017/12/15
 */
class EditLiveHostIntroduceActivity : BaseTitleActivity(), PictureConfig.OnSelectResultCallback {


    var bean: LiveRoomBean? = null
    var data: LiveHostPageResposery? = null
    var map: HashMap<String, String>? = null
    private var mAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_live_host_introduce)
    }

    override fun initView() {
        super.initView()
        bean = intent.getSerializableExtra("bean") as LiveRoomBean
        tv_introduce_content.text = bean?.introduce;
        baseHeadView.showBackButton {
            finish()
        }
        baseHeadView.showTitle("编辑主页")
                .showHeadRightButton("预览", object : View.OnClickListener {
            override fun onClick(v: View?) {
                LiveHostPageActivity.startActivity(this@EditLiveHostIntroduceActivity, "1", bean?.id)
            }

        })
        ImageHelper.loadImage(MyApplication.getInstance(), iv_series_head, bean?.bakpic)

        //修改背景
        rl_avatar.setOnClickListener {
            chooseImg()
        }

        //修改课堂介绍
        rl_introduce.setOnClickListener {
            showNameDialog()
        }
    }


    private fun showNameDialog() {
        DialogUtil.showTopicInputDialog(this, "课堂介绍", "编辑课堂介绍",
                bean?.introduce
        ) { v ->
            val str = v.tag as String
            if (TextUtils.isEmpty(str)) {

            } else {
                if(!str.equals(bean?.introduce))
                   getdata(str)
            }
        }
    }

    private fun getdata(str : String) {
        data = LiveHostPageResposery()
        map = HashMap()
        if (TextUtils.isEmpty(str.trim())) {
            ToastUtil.showShort(MyApplication.getInstance(), "编辑内容不为空")
            return
        }
        map?.put("token", UserManager.getInstance().token)
        map?.put("chatTypeId", bean!!.getChatTypeId())
        map?.put("property", bean!!.getProperty())
        map?.put("roomname", bean!!.getRoomname())
        map?.put("introduce", str)
        data?.saveChatRoom(map!!, object : ApiCallBack<CreateLiveBean>() {
            override fun onSuccess(data: CreateLiveBean?) {
                var intent = Intent()
                intent.putExtra("data", data?.introduce)
                setResult(Activity.RESULT_OK, intent)
                tv_introduce_content.text = str;
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showShort(MyApplication.getInstance(), message)
            }

        })
    }

    private fun chooseImg() {
        val pers = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, REQUEST_CODE_AVATAR,
                object : BaseTitleActivity.PermissionsResultListener {
                    override fun onPermissionGranted() {
                        val options = FunctionOptions.Builder()
                                .setType(FunctionConfig.TYPE_IMAGE)
                                .setSelectMode(FunctionConfig.MODE_SINGLE)
                                .setImageSpanCount(3)
                                .setEnableQualityCompress(false) //是否启质量压缩
                                .setEnablePixelCompress(false) //是否启用像素压缩
                                .setEnablePreview(true) // 是否打开预览选项
                                .setShowCamera(true)
                                .setPreviewVideo(true)
                                .setIsCrop(true)
                                .setAspectRatio(15, 8)
                                .create()
                        PictureConfig.getInstance().init(options).openPhoto(this@EditLiveHostIntroduceActivity, this@EditLiveHostIntroduceActivity)
                    }

                    override fun onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(this@EditLiveHostIntroduceActivity, false,
                                null, getString(R.string.pre_scan_notice_msg)
                        ) { v ->
                            if (v.id == R.id.tv_dialog_confirm) {
                                JumpPermissionManagement.GoToSetting(
                                        this@EditLiveHostIntroduceActivity)
                            }
                            mAlertDialog?.dismiss()
                        }
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        data?.destroy()
    }

    companion object {
        @JvmStatic
        fun jumpToEditLiveHostIntroduceActivity(activity: Activity, bean: LiveRoomBean) {
            val intent = Intent(activity, EditLiveHostIntroduceActivity::class.java)
            intent.putExtra("bean", bean)
            activity.startActivityForResult(intent, 1000)
        }
    }


    override fun onSelectSuccess(resultList: MutableList<LocalMedia>?) {}

    override fun onSelectSuccess(media: LocalMedia?) {
        baseLoadingView.showLoading()
        uploadPic(media?.getPath())
    }

    fun showPic(url: String) {
        Observable.just(url).flatMap { s ->
            val map = java.util.HashMap<String, String>()
            map.put("token", UserManager.getInstance().token)
            map.put("chatTypeId", bean?.chatTypeId.toString())
            map.put("property", bean?.property.toString())
            map.put("roomname", bean?.roomname.toString())
            map.put("introduce", bean?.introduce.toString())
            map.put("bakpic", s)
            LiveApi.getInstance().saveChatRoom(map)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                ApiObserver(object : ApiCallBack<CreateLiveBean>() {
                    override fun onSuccess(data: CreateLiveBean) {
                        baseLoadingView.hideLoading()
                        ImageHelper.loadImage(MyApplication.getInstance(), iv_series_head, data.bakpic)

                    }

                    override fun onError(errorCode: String, message: String) {
                        baseLoadingView.hideLoading()
                        LoginErrorCodeUtil.showHaveTokenError(this@EditLiveHostIntroduceActivity, errorCode,
                                message)
                    }
                }))
    }

    fun uploadPic(path: String?) {
        if (path.isNullOrEmpty()) return

        val file = File(path)
        Luban.get(MyApplication.getInstance())
                .load(file)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .map({ compressFile ->
                    if (FileUtil.getSize(file) > 1024 * 200) compressFile else file
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uploadFile ->
                    LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE,
                            object : LoadHelper.UploadCallback {
                                override fun onUploadSuccess(result: UploadResult?) {

                                    result?.let {
                                        showPic(result.url)

                                    }
                                }

                                override fun onUploadFailed(errorCode: String?, msg: String?) {
                                    baseLoadingView.hideLoading()
                                }

                            }, null, uploadFile)
                })
    }


}
