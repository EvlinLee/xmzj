package com.gxtc.huchuan.ui.live.intro;

import android.app.Activity;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.ChatInFoStatusBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.ui.live.list.LiveListContract;

/**
 * Created by Gubr on 2017/3/21.
 */

public interface LiveIntroContract {

    interface View extends BaseUserView<Presenter> {
        void showChatInfoData(ChatInfosBean infosBean);

        void showChatInFoStatusBean(ChatInFoStatusBean statusBean);

        void showEnrollSuccess();

        void showEnrollSeriesSuccess();

        void showCollectResult();

        void showFollowSuccess();
    }


    interface Presenter extends BasePresenter{

        void getData(String id);

        void collect(String classId);

        void follow(String id);

        void enrollSeries(Activity activity, ChatInfosBean bean, String shareUserCode, String freeSign);    //报名系列课

        void enrollClassroom(String id, String joinGroup, String shareUserCode);    //报名课堂

        void payEnrollClassroom(Activity activity, ChatInfosBean bean, String shareUserCode, String freeSign);  //付费报名课堂

        void freeEnroll(String id);                                                 //免费报名，不走支付接口

        void freeEnrollByPay(String id, String shareUserCode);                      //免费报名，走支付接口

        void freeInviteJoin(String id, String shareUserCode, String freeSign);      //免费邀请加入课堂
    }
}
