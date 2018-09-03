package com.gxtc.huchuan.ui.live.series.count

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.ChatJoinBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.http.ApiCallBack
import retrofit2.http.Field
import java.util.ArrayList

/**
 * Created by zzg on 2017/12/12.
 */
interface SeriseSignCountSource :BaseSource{
    fun getSeriseSignCount(token:String,id:String,type:String,start:Int,searchKey:String,mCallBack:ApiCallBack<ArrayList<SeriseCountBean>>)

    fun getlistJoinMember(map :HashMap<String, String>,mCallBack:ApiCallBack<ArrayList<ChatJoinBean.MemberBean>>)
}