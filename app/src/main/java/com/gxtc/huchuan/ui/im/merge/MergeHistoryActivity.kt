package com.gxtc.huchuan.ui.im.merge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import cn.jzvd.JZMediaManager
import cn.jzvd.JZVideoPlayer
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MergeMessageBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MessageApi
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/30.
 * 合并消息记录列表
 */
class MergeHistoryActivity: BaseTitleActivity() {

    private var id: String ? = null
    private lateinit var mAdapter: MergeHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_swipe_recyclerview)
    }

    override fun initView() {
        baseHeadView.showTitle(intent.getStringExtra("title")).showBackButton { finish() }

        swipeLayout.isEnabled = false

        mAdapter = MergeHistoryAdapter(this, mutableListOf())
        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MergeHistoryActivity, LinearLayoutManager.VERTICAL, false)
            setLoadMoreView(R.layout.model_footview_loadmore)
            setBackgroundResource(R.color.common_background)
            adapter = mAdapter
        }
    }

    override fun initListener() {
        recyclerview.setOnLoadMoreListener {
            getData(mAdapter.datas.size, object : ApiCallBack<MutableList<MergeMessageBean>>(){
                override fun onSuccess(data: MutableList<MergeMessageBean>?) {
                    if(data == null || data.size == 0){
                        recyclerview?.loadFinish()
                        return
                    }
                    recyclerview?.changeData(data, mAdapter)
                }

                override fun onError(errorCode: String?, message: String?) {
                    ToastUtil.showShort(this@MergeHistoryActivity, message)
                }
            })
        }
    }

    override fun initData() {
        id = intent.getStringExtra("id")

        baseLoadingView.showLoading()
        getData(0, object : ApiCallBack<MutableList<MergeMessageBean>>(){
            override fun onSuccess(data: MutableList<MergeMessageBean>?) {
                baseLoadingView?.hideLoading()
                if(data == null || data.size == 0){
                    baseEmptyView?.showEmptyView()
                    return
                }

                recyclerview.notifyChangeData(data, mAdapter)
            }

            override fun onError(errorCode: String?, message: String?) {
                if(ApiObserver.NET_ERROR == errorCode){
                    baseEmptyView?.showNetWorkView {
                        baseEmptyView?.hideEmptyView()
                        getData(0, this)
                    }

                }else{
                    ToastUtil.showShort(this@MergeHistoryActivity, message)
                }
            }
        })
    }

    private fun getData(start: Int, callBack: ApiCallBack<MutableList<MergeMessageBean>>){
        if(start == 0){
            baseLoadingView.showLoading()
        }

        val map = hashMapOf<String, String>()
        map["token"] = UserManager.getInstance().token
        map["id"] = id?:""
        map["start"] = start.toString()

        MessageApi.getInstance()
                .getMergeMessage(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<List<MergeMessageBean>>>(callBack))
                .run {
                    RxTaskHelper.getInstance().addTask(this@MergeHistoryActivity, this)
                }
    }

    override fun onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JZVideoPlayer.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
        JZMediaManager.instance().releaseMediaPlayer()     //释放视频资源
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, id: String?, title: String?){
            Intent(context, MergeHistoryActivity::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                context.startActivity(this)
            }
        }
    }

}