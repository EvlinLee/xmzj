package com.gxtc.huchuan.ui.message

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener
import com.gxtc.commlibrary.utils.FileUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.bean.UploadFileBean
import com.gxtc.huchuan.bean.UploadResult
import com.gxtc.huchuan.data.MessageRepository
import com.gxtc.huchuan.data.MessageSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.LoadHelper
import com.gxtc.huchuan.http.service.MineApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File
import java.util.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/13.
 */
class GroupInfoPresenter(view: GroupInfoContract.View) : GroupInfoContract.Presenter {
    private var mView: GroupInfoContract.View? = null
    private var mData: MessageSource? = null
    private var loadTime = ""
    init {
        mView = view
        mView?.setPresenter(this)
        mData = MessageRepository()
        loadTime = System.currentTimeMillis().toString()
    }
    override fun uploadPic(path: String?) {
        if(path.isNullOrEmpty()) return

        val file = File(path)
        Luban.get(MyApplication.getInstance())
                .load(file)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .map({  compressFile ->
                    if(FileUtil.getSize(file) > 1024 * 200 ) compressFile else file
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uploadFile ->
                    LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE,
                            object: LoadHelper.UploadCallback{
                                override fun onUploadSuccess(result: UploadResult?) {
                                    mView?.showLoadFinish()
                                    result?.let {
                                        mView?.showPic(result.url)
                                    }
                                }

                                override fun onUploadFailed(errorCode: String?, msg: String?) {
                                    mView?.showLoadFinish()
                                    mView?.showError(msg)
                                }

                            }, null, uploadFile)
                })
    }

    override fun getMembers(id: String) {
        mView?.showLoad()
        mData?.getGroupMembers(id, 0, 30, loadTime, object : ApiCallBack<MutableList<MessageBean>>() {
            override fun onSuccess(data: MutableList<MessageBean>?) {
                mView?.showLoadFinish()
                if(data != null){
                   mView?.showMembers(data)
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })
    }

    override fun getRoleType(id: String) {
        val token = UserManager.getInstance().token
        mData?.getGroupRole(token,id,object :ApiCallBack<MessageBean>(){

            override fun onSuccess(data: MessageBean?) {
                mView?.showRoleType(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
                mView?.showLoadFinish()
            }
        })
    }

    override fun start() {

    }

    override fun destroy() {
        mData?.destroy()
        mView = null
    }
}