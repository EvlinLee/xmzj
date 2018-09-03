package com.gxtc.huchuan.ui.special

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.R.id.play_news_video_cover
import com.gxtc.huchuan.adapter.SpecialVideoAdater
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.NewsBean
import com.gxtc.huchuan.data.ArticleSpecialBean
import com.gxtc.huchuan.data.SpecialRepository
import com.gxtc.huchuan.data.SpecialSource
import com.gxtc.huchuan.data.deal.VideoNewsRepository
import com.gxtc.huchuan.http.ApiCallBack
import kotlinx.android.synthetic.main.special_video_fragment_layout.*
import java.util.HashMap

/**
 * Created by zzg on 2018/5/12
 */
class SpecialVideoFragment : BaseTitleFragment() {
    var mSpecialVideoAdater: SpecialVideoAdater? = null
    var datas: SpecialSource? = null
    var start = 0
    var map: HashMap<String, String>? = null
    var specialId: String? = null
    var isPay: String? = null
    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        return inflater?.inflate(R.layout.special_video_fragment_layout, container, false)
    }

    override fun onGetBundle(bundle: Bundle?) {
        super.onGetBundle(bundle)
        specialId = bundle?.getString("specialId")
        isPay = bundle?.getString("isPay")
    }

    override fun initData() {
        super.initData()
        map = hashMapOf()
        refreshlayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)

        mSpecialVideoAdater = SpecialVideoAdater(activity, mutableListOf(), R.layout.item_special_video_fragment, this!!.isPay!!)
        recyclerview.adapter = mSpecialVideoAdater
        recyclerview.layoutManager = LinearLayoutManager(activity)

        mSpecialVideoAdater?.seeListener = object : View.OnClickListener {

            override fun onClick(v: View?) {
                ToastUtil.showShort(MyApplication.getInstance(), "可试看")
            }

        }
        datas = SpecialRepository()
        baseLoadingView.showLoading()
        getData(false);
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                recyclerview.reLoadFinish()
                getData(false)
            }

        })

        recyclerview.setOnLoadMoreListener(object : LoadMoreWrapper.OnLoadMoreListener {
            override fun onLoadMoreRequested() {
                getData(true)
            }

        })
    }

    fun getData(isLoadMore: Boolean) {
        if (!isLoadMore) {
            start = 0
        } else {
            start = start + 15
        }
        map?.put("id", specialId!!)
        map?.put("type", "2") //0=全部，1=文章，2=视频
        map?.put("start", start?.toString())
        datas?.getSpecialType(map, object : ApiCallBack<MutableList<ArticleSpecialBean>>() {
            override fun onSuccess(data: MutableList<ArticleSpecialBean>?) {
                refreshlayout.isRefreshing = false
                baseLoadingView.hideLoading()
                if (data != null && data.size > 0) {
                    showData(data)
                } else {
                    if (start == 0) {
                        baseEmptyView.showEmptyContent()
                        return
                    }
                    recyclerview.loadFinish()
                }

            }

            override fun onError(errorCode: String?, message: String?) {
                refreshlayout.isRefreshing = false
                ToastUtil.showShort(MyApplication.getInstance(), message)
            }

        })
    }

    fun showData(data: MutableList<ArticleSpecialBean>?) {
        if (start == 0) {
            recyclerview.notifyChangeData(data, mSpecialVideoAdater)
        } else {
            recyclerview.changeData(data, mSpecialVideoAdater)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        datas?.destroy()
    }
}