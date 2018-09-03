package com.gxtc.huchuan.ui.live.conversation

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.data.MallSeachSource
import com.gxtc.huchuan.data.MallSearchRepository
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.data.deal.DealRepository
import com.gxtc.huchuan.data.deal.DealSource
import com.gxtc.huchuan.data.deal.MineArticleRepository
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.AllApi
import com.gxtc.huchuan.ui.live.hostpage.newhostpage.ClassAndSeriseResposery
import com.gxtc.huchuan.ui.live.hostpage.newhostpage.ClassAndSeriseSourse
import com.gxtc.huchuan.ui.live.search.NewSearchActivity
import com.gxtc.huchuan.ui.mine.news.MineArticleContract
import com.gxtc.huchuan.utils.ImMessageUtils
import com.gxtc.huchuan.utils.RIMErrorCodeUtil
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/5.
 */
class LiveInsertChoosePresenter: LiveInsertChooseContract.Presenter {

    private var mView: LiveInsertChooseContract.View ? = null

    private var start = 0

    private var type = 1

    private var mArticleData: MineArticleContract.Source ? = null

    private var mClassData: ClassAndSeriseSourse.Source? = null

    private var mDealData: DealSource ? = null

    private var mallData: MallSeachSource ? = null

    constructor(type: Int, view: LiveInsertChooseContract.View){
        mView = view
        mView?.setPresenter(this)
        this.type = type

        mArticleData = MineArticleRepository()
        mClassData = ClassAndSeriseResposery()
        mDealData = DealRepository()
        mallData = MallSearchRepository()
    }


    override fun getData() {
        when(type){
            LiveInsertChooseActivity.TYPE_CIRCLE-> getCircleData(false)

            LiveInsertChooseActivity.TYPE_ARTICLE-> getArticleData(false)

            LiveInsertChooseActivity.TYPE_CLASS-> getClassData(false)

            LiveInsertChooseActivity.TYPE_DEAL-> getDealData(false)
        }
    }


    override fun refreshData() {
        start = 0
        when(type){
            LiveInsertChooseActivity.TYPE_CIRCLE-> getCircleData(true)

            LiveInsertChooseActivity.TYPE_ARTICLE-> getArticleData(true)

            LiveInsertChooseActivity.TYPE_CLASS-> getClassData(true)

            LiveInsertChooseActivity.TYPE_DEAL-> getDealData(true)
        }
    }


    override fun loadMoreData() {
        start += 15
        when(type){
            LiveInsertChooseActivity.TYPE_CIRCLE-> getCircleData(false)

            LiveInsertChooseActivity.TYPE_ARTICLE-> getArticleData(false)

            LiveInsertChooseActivity.TYPE_CLASS-> getClassData(false)

            LiveInsertChooseActivity.TYPE_DEAL-> getDealData(false)
        }
    }

    override fun searchData(key: String?) {
        val map = hashMapOf<String, String>()
        val token = UserManager.getInstance().token
        var searchType = ""
        when(type){
            LiveInsertChooseActivity.TYPE_CIRCLE-> searchType = NewSearchActivity.TYPE_CIRCLE

            LiveInsertChooseActivity.TYPE_ARTICLE-> searchType = NewSearchActivity.TYPE_NEWS

            LiveInsertChooseActivity.TYPE_CLASS-> searchType = NewSearchActivity.TYPE_LIVE

            LiveInsertChooseActivity.TYPE_DEAL-> searchType = NewSearchActivity.TYPE_DEAL

            LiveInsertChooseActivity.TYPE_MALL-> {
                searchMallData(key)
                return
            }
        }

        map.put("type", searchType)
        map.put("start", "0")
        map.put("pageSize", "200")
        token?.let { map.put("token", token) }
        key?.let { map.put("searchKey", key) }
        commonSearch(map)
    }


    private fun searchMallData(key: String?){
        mView?.showLoad()
        val map = hashMapOf<String, String>()
        val token = UserManager.getInstance().token
        token?.let { map.put("token", token) }
        key?.let { map.put("searchValue", key) }
        map.put("start", "0")
        map.put("pageSize", "200")
        mallData?.getSearchData(map, object : ApiCallBack<ArrayList<CategoryBean>>(){
            override fun onSuccess(data: ArrayList<CategoryBean>?) {
                mView?.showLoadFinish()
                val list = mutableListOf<LiveInsertBean>()
                if(data != null && !data.isEmpty()){
                    data.mapTo(list){
                        LiveInsertBean.converByMall(it)
                    }
                }
                mView?.showSearchData(list)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })
    }


    private fun commonSearch(map: HashMap<String, String>){
        mView?.showLoad()
        AllApi.getInstance()
                .searchList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<List<SearchBean>>>(object : ApiCallBack<List<SearchBean>>(){
                    override fun onSuccess(data: List<SearchBean>?) {
                        mView?.showLoadFinish()
                        mView?.showSearchData(converSearchData(data))
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        mView?.showLoadFinish()
                        mView?.showError(message)
                    }
                }))
    }


    private fun converSearchData(data: List<SearchBean>?): MutableList<LiveInsertBean>{
        val list = mutableListOf<LiveInsertBean>()
        if(data == null || data.isEmpty()){
            return list
        }else{
            when(type){
                LiveInsertChooseActivity.TYPE_CIRCLE-> {
                    val circles = data.get(0).getDatas<CircleBean>()
                    circles.mapTo(list){
                        val insertBean = LiveInsertBean()
                        insertBean.id = it.id
                        insertBean.cover = it.cover
                        insertBean.content = it.content
                        insertBean.title = it.groupName
                        insertBean.infoNum = it.infoNum
                        insertBean.attention = it.attention
                        insertBean
                    }
                }

                LiveInsertChooseActivity.TYPE_ARTICLE-> {
                    val articles = data.get(0).getDatas<NewsBean>()
                    articles.mapTo(list){
                        LiveInsertBean.converByArticle(it)
                    }
                }

                LiveInsertChooseActivity.TYPE_CLASS-> {
                    val classes = data.get(0).getDatas<ChatInfosBean>()
                    classes.mapTo(list){
                        LiveInsertBean.converByClass(it)
                    }
                }

                LiveInsertChooseActivity.TYPE_DEAL-> {
                    val deals = data.get(0).getDatas<DealListBean>()
                    deals.mapTo(list){
                        LiveInsertBean.converByDeal(it)
                    }
                }
            }

            return list
        }
    }


    private fun getDealData(isRefresh: Boolean) {
        val token = UserManager.getInstance().token
        val userCode = UserManager.getInstance().userCode
        mView?.showLoad()

        mDealData?.getUserDealList(token, userCode, start, object : ApiCallBack<List<DealListBean>>(){
            override fun onSuccess(data: List<DealListBean>?) {
                mView?.showLoadFinish()
                if(data != null && data.isNotEmpty()){
                    val list = mutableListOf<LiveInsertBean>()

                    data.mapTo(list){
                        LiveInsertBean.converByDeal(it)
                    }

                    if(start != 0){
                        mView?.showLoadMoreData(list)
                    } else if (isRefresh){
                        mView?.showRefreshData(list)
                    } else {
                        mView?.showData(list)
                    }

                }else{
                    if(start != 0){
                        mView?.showNoLoadMore()
                    } else if(isRefresh){
                        mView?.showRefreshData(null)
                    }else{
                        mView?.showEmpty()
                    }
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                if(!isRefresh && start == 0){
                    ErrorCodeUtil.handleErr(mView, errorCode, message)
                }else{
                    mView?.showLoadFinish()
                    mView?.showError(message)
                }
            }
        })
    }

    private fun getClassData(isRefresh: Boolean) {
        val token = UserManager.getInstance().token
        val liveId = UserManager.getInstance().chatRoomId
        mView?.showLoad()
        mClassData?.getData(liveId, token, start.toString(), object: ApiCallBack<List<ChatInfosBean>>(){
            override fun onSuccess(data: List<ChatInfosBean>?) {
                mView?.showLoadFinish()
                if(data != null && data.isNotEmpty()){
                    val list = mutableListOf<LiveInsertBean>()
                    for(classBean in data){

                        if(classBean.audit == null || classBean.audit.toInt() == 1){
                            list.add(LiveInsertBean.converByClass(classBean))
                        }
                    }

                    if(start != 0){
                        mView?.showLoadMoreData(list)
                    } else if(isRefresh){
                        mView?.showRefreshData(list)
                    }else{
                        mView?.showData(list)
                    }

                }else{
                    if(start != 0){
                        mView?.showNoLoadMore()
                    } else if(isRefresh){
                        mView?.showRefreshData(null)
                    }else{
                        mView?.showEmpty()
                    }
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                if(!isRefresh && start == 0){
                    ErrorCodeUtil.handleErr(mView, errorCode, message)
                }else{
                    mView?.showLoadFinish()
                    mView?.showError(message)
                }
            }
        })
    }


    private fun getArticleData(isRefresh: Boolean) {
        mView?.showLoad()
        mArticleData?.getData(start, object : ApiCallBack<List<NewsBean>>() {
            override fun onSuccess(data: List<NewsBean>?) {
                mView?.showLoadFinish()
                if(data != null && data.isNotEmpty()){
                    val list = mutableListOf<LiveInsertBean>()
                    for(newsBean in data){
                        if(newsBean.audit?.toInt() == 1){
                            list.add(LiveInsertBean.converByArticle(newsBean))
                        }
                    }

                    if(start != 0){
                        mView?.showLoadMoreData(list)
                    } else if (isRefresh){
                        mView?.showRefreshData(list)
                    } else {
                        mView?.showData(list)
                    }

                }else{
                    if(start != 0){
                        mView?.showNoLoadMore()
                    } else if(isRefresh){
                        mView?.showRefreshData(null)
                    }else{
                        mView?.showEmpty()
                    }
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                if(!isRefresh && start == 0){
                    ErrorCodeUtil.handleErr(mView, errorCode, message)
                }else{
                    mView?.showLoadFinish()
                    mView?.showError(message)
                }
            }
        })
    }


    private fun getCircleData(isRefresh: Boolean){
        mView?.showLoad()
        val token = UserManager.getInstance().token
        val sub =
                AllApi.getInstance()
                        .listByUser(token, "0")             //关注的圈子
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ApiObserver<ApiResponseBean<List<MineCircleBean>>>(object : ApiCallBack<List<MineCircleBean>>(){
                            override fun onSuccess(data: List<MineCircleBean>?) {
                                mView?.showLoadFinish()

                                if(data != null && data.isNotEmpty()){
                                    val list = mutableListOf<LiveInsertBean>()
                                    data.mapTo(list) {
                                        LiveInsertBean.converByCirlce(it)
                                    }

                                    if(start != 0){
                                        mView?.showLoadMoreData(list)
                                    } else if (isRefresh){
                                        mView?.showRefreshData(list)
                                    } else {
                                        mView?.showData(list)
                                    }

                                }else{
                                    if(start != 0){
                                        mView?.showNoLoadMore()
                                    } else if(isRefresh){
                                        mView?.showRefreshData(null)
                                    }else{
                                        mView?.showEmpty()
                                    }
                                }
                            }

                            override fun onError(errorCode: String?, message: String?) {
                                if(!isRefresh && start == 0){
                                    ErrorCodeUtil.handleErr(mView, errorCode, message)
                                }else{
                                    mView?.showError(message)
                                }
                            }
                        }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    override fun sendShareMessage(infosBean: ChatInfosBean, bean: LiveInsertBean) {
        // 0.圈子 1：文章，2：课程，3：系列课，4：交易，5：商品，6：个人名片
        if(type == LiveInsertChooseActivity.TYPE_CLASS){        //因为课程还分系列课
            if(bean.type == 1){
                bean.infoType = LiveInsertChooseActivity.TYPE_CLASS.toString()
            }else{
                bean.infoType = LiveInsertChooseActivity.TYPE_SERISE.toString()
            }
        }else{
            bean.infoType = type.toString()
        }

        ImMessageUtils.sendShareMessage(if(infosBean.isSelff) "3" else "1", infosBean.id, bean, object : IRongCallback.ISendMessageCallback {
            override fun onAttached(message: Message) = Unit

            override fun onSuccess(message: Message) {
                mView?.showShareSuccess(message)
            }

            override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                mView?.showError(RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    }

    override fun start() = Unit

    override fun destroy() {
        mView = null
        mArticleData?.destroy()
        mDealData?.destroy()
        mClassData?.destroy()
        mallData?.destroy()

        RxTaskHelper.getInstance().cancelTask(this)
    }
}