package com.gxtc.huchuan.ui.live.hostpage.newhostpage;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.ChatInfosBean;

import java.util.List;

/**
 * Created by zzg on 2017/12/18.
 */

public class LiveAndSeriseContract {
    public interface View extends BaseUiView<Presenter>{
        void showDatat(List<ChatInfosBean> data);
    }
    public interface Presenter extends BasePresenter{
        void  getData(String chatRoomId,String token,String start);
    }
}
