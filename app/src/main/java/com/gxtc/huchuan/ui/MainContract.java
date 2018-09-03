package com.gxtc.huchuan.ui;

import com.gxtc.huchuan.bean.UpdataBean;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/5.
 */

public interface MainContract {

    interface View{
        void showUnreadMsg(int count);
        void showUpdata(UpdataBean updataInfo);
    }

    interface Presenter{
        void checkUpdata();
        void connectRongIm();
        void getConversationList();
        void destroy();
    }

}
