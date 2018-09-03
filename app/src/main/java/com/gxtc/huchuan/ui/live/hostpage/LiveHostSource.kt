package com.gxtc.huchuan.ui.live.hostpage

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.CreateLiveBean
import com.gxtc.huchuan.bean.LiveRoomBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/12/15.
 */
interface LiveHostSource : BaseSource{
    fun saveChatRoom(map:HashMap<String,String>,callBack:ApiCallBack<CreateLiveBean>)
}