package com.gxtc.huchuan.ui.message

import android.util.Log
import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.commlibrary.utils.FileUtil
import com.gxtc.commlibrary.utils.GsonUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.data.MessageRepository
import com.gxtc.huchuan.data.MessageSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.LoadHelper
import com.gxtc.huchuan.http.service.MessageApi
import com.gxtc.huchuan.http.service.MineApi
import com.gxtc.huchuan.ui.mine.focus.FocusContract
import com.gxtc.huchuan.ui.mine.focus.FocusRepository
import jp.wasabeef.richeditor.RichEditor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File
import java.util.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/7.
 */
class InviteChatPresenter(view : InviteChatContract.View,type:Int) : InviteChatContract.Presenter{
    var mView : InviteChatContract.View ? = null
    var mData : MessageSource ? = null
    var focusData : FocusContract.Source ? = null

    var start = 0
    var mType = 1  //1 拉人 2 踢人
    var groupChatId = ""
    var loadTime : Long = 0
    init {
        mView = view
        mType = type;
        mView?.setPresenter(this)
        mData = MessageRepository()
        focusData = FocusRepository()
    }

    override fun refreshData() {
        start = 0
        loadTime = System.currentTimeMillis()
        if(mType == 2){
            /**踢人*/
            val sub =  MessageApi.getInstance()
                    .getlistMember(start,groupChatId, loadTime)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MutableList<FocusBean>>>(object :ApiCallBack<MutableList<FocusBean>>(){
                        override fun onSuccess(data: MutableList<FocusBean>?) {
                            if(data == null || data.size == 0){
                                mView?.showRefreshData(null)
                                return
                            }
                            mView?.showRefreshData(data)
                        }

                        override fun onError(errorCode: String?, message: String?) {
                            ErrorCodeUtil.handleErr(mView,errorCode,message)
                        }

                    }))
            RxTaskHelper.getInstance().addTask(this,sub)
        }else{
            /**拉人*/
            focusData?.getData(start,"3",groupChatId,object : ApiCallBack<MutableList<FocusBean>>() {

                override fun onSuccess(data: MutableList<FocusBean>?) {
                    if(data == null || data.size == 0){
                        mView?.showRefreshData(null)
                        return
                    }
                    mView?.showRefreshData(data)
                }

                override fun onError(errorCode: String?, message: String?) {
                    mView?.showRefreshData(null)
                    mView?.showError(message)
                }
            })
        }
    }

    override fun loadMoreData() {
        start += 15
        if(mType == 2){
            /**踢人*/
            val sub =  MessageApi.getInstance()
                    .getlistMember(start,groupChatId, loadTime)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MutableList<FocusBean>>>(object :ApiCallBack<MutableList<FocusBean>>(){
                        override fun onSuccess(data: MutableList<FocusBean>?) {
                            if(data == null || data.size == 0){
                                mView?.showNoLoadMore()
                                return
                            }
                            mView?.showLoadMoreData(data)
                        }

                        override fun onError(errorCode: String?, message: String?) {
                            ErrorCodeUtil.handleErr(mView,errorCode,message)
                        }

                    }))
            RxTaskHelper.getInstance().addTask(this,sub)
        }else{
            /**拉人*/
            focusData?.getData(start,"3",groupChatId,object : ApiCallBack<MutableList<FocusBean>>() {

                override fun onSuccess(data: MutableList<FocusBean>?) {
                    if(data == null || data.size == 0){
                        mView?.showNoLoadMore()
                        return
                    }
                    mView?.showLoadMoreData(data)
                }

                override fun onError(errorCode: String?, message: String?) {
                    mView?.showError(message)
                    start -= 15
                }
            })
        }
    }

    override fun getData(groupChatId:String) {
        mView?.showLoad()
        start = 0
        this.groupChatId = groupChatId
        focusData?.getData(start,"3",groupChatId,object : ApiCallBack<MutableList<FocusBean>>() {

            override fun onSuccess(data: MutableList<FocusBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView,errorCode,message)
            }
        })
    }

    override fun getGroupMeamberData(star:Int,groupChatId: String) {
        if(start == 0)
            loadTime = System.currentTimeMillis()
       this.groupChatId = groupChatId
       val sub =  MessageApi.getInstance()
                .getlistMember(star,groupChatId, loadTime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<FocusBean>>>(object :ApiCallBack<MutableList<FocusBean>>(){
                    override fun onSuccess(data: MutableList<FocusBean>?) {
                        if(data == null || data.size == 0){
                            mView?.showEmpty()
                            return
                        }
                        mView?.showData(data)
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ErrorCodeUtil.handleErr(mView,errorCode,message)
                    }

                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }


    override fun queryUser(name: String) {
        val map = HashMap<String,String>()
        map.put("token",UserManager.getInstance().token)
        map.put("start","0")
        map.put("searchKey",name)
        map.put("type","0")
        map.put("pageSize","1000")
        mData?.searchFriends(map,object: ApiCallBack<MutableList<SearchChatBean>>(){

            override fun onSuccess(data: MutableList<SearchChatBean>?) {
                if(data != null && data.size > 0){
                    val focuBeans = mutableListOf<FocusBean>()
                    for(bean in data){
                        val foc = FocusBean()
                        foc.userName = bean.name
                        foc.userHeadPic = bean.pic
                        foc.userCode = bean.code
                        focuBeans.add(foc)
                    }
                    mView?.showQuery(focuBeans)
                }else{
                    mView?.showError("暂无搜索结果")
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun uploadPic(path: String?) {
        if(path.isNullOrEmpty()) return

        val file = File(path)
        Luban.get(MyApplication.getInstance())
                .load(file)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .map({  compressFile ->
                    if(FileUtil.getSize(file) > Constant.COMPRESS_VALUE ) compressFile else file
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

    override fun createGroup(name: String?, picUrl: String?, userCodes: MutableList<FocusBean>) {
        mView?.showLoad()
        val map = HashMap<String,String>()
        map.put("token",UserManager.getInstance().token)
        map.put("groupName",name!!)
        map.put("groupPic",picUrl!!)

        val sb = StringBuilder()
        for(bean in userCodes){
            sb.append(bean.userCode).append(",")
        }
        if(sb.isNotEmpty()){
            sb.deleteCharAt(sb.length - 1)
            map.put("userCode",sb.toString())
        }

        mData?.createGroup(map,object :ApiCallBack<MessageBean>(){
            override fun onSuccess(data: MessageBean?) {
                mView?.showLoadFinish()
                mView?.showCreateResult(true,data,null)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showCreateResult(false,null,message)
            }
        })
    }

    override fun start() {
    }

    override fun destroy() {
        mData?.destroy()
        mView = null
        RxTaskHelper.getInstance().cancelTask(this)
    }
}