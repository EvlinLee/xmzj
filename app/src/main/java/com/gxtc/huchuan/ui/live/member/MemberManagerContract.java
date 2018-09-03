package com.gxtc.huchuan.ui.live.member;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.MemberBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/4/2.
 */

public interface MemberManagerContract {
    public interface View extends BaseUiView<Presenter> {
        void showDatas(ArrayList<ChatJoinBean.MemberBean> datas);
        void showLoadMoreDatas(ArrayList<ChatJoinBean.MemberBean> beens);
    }

    public interface Presenter extends BasePresenter {
        void getDatas(String chatinfoid ,String count,String searchKey, String type);

        void Search(String chatinfoid ,String count,String searchKey);
    }

    public interface Source extends BaseSource {
        void getDatas(HashMap<String,String> map,ApiCallBack<ArrayList<ChatJoinBean.MemberBean>> callBack);

    }
}
