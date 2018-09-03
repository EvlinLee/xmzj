package com.gxtc.huchuan.ui.live.hostpage.newhostpage;

import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by zzg on 2017/12/18.
 */

public class ClassAndSeriseSourse  {
    public interface Source extends BaseSource {
        void getData(String chatRoomId,String token,String start, ApiCallBack<List<ChatInfosBean>> callBack);
    }
}
