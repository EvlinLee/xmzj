package com.gxtc.huchuan.ui.special

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.adapter.MySpecialListAdapter
import com.gxtc.huchuan.data.SpecialBean
import com.gxtc.huchuan.data.SpecialRepository
import com.gxtc.huchuan.data.SpecialSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import org.xml.sax.ErrorHandler

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/12.
 */
class MySpecialListPresenter(var mView: MySpecialListContract.View?, var mAdapter: MySpecialListAdapter)
    : MySpecialListContract.Presenter {

    private var mData: SpecialSource ? = null

    init {
        mData = SpecialRepository()
        mView?.setPresenter(this)
    }

    override fun getData() {
        val map = hashMapOf<String, String>()
        map.put("token", UserManager.getInstance().token)
        map.put("start", 0.toString())

        mView?.showLoad()
        mData?.getMySpecialList(map, object : ApiCallBack<MutableList<SpecialBean>>(){
            override fun onSuccess(data: MutableList<SpecialBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }
        })
    }

    override fun refreshData() {
        val map = hashMapOf<String, String>()
        map.put("token", UserManager.getInstance().token)
        map.put("start", 0.toString())

        mData?.getMySpecialList(map, object : ApiCallBack<MutableList<SpecialBean>>(){
            override fun onSuccess(data: MutableList<SpecialBean>?) {
                if(data == null || data.size == 0){
                    mView?.showRefreshData(null)
                    return
                }
                mView?.showRefreshData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }
        })
    }

    override fun loadMoreData() {
        val start = mAdapter.itemCount + 15
        val map = hashMapOf<String, String>()
        map.put("token", UserManager.getInstance().token)
        map.put("start", start.toString())

        mData?.getMySpecialList(map, object : ApiCallBack<MutableList<SpecialBean>>(){
            override fun onSuccess(data: MutableList<SpecialBean>?) {
                if(data == null || data.size == 0){
                    mView?.showNoLoadMore()
                    return
                }
                mView?.showLoadMoreData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }
        })
    }

    override fun start() = Unit

    override fun destroy() {
        mView = null
        mData?.destroy()
    }

}