package com.gxtc.huchuan.ui.im.redPacket;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.RedPacketBean;

import java.util.Date;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/7.
 */

public interface RedPacketDetailedContract {

    interface View extends BaseUserView<RedPacketDetailedContract.Presenter>,BaseListUiView<RedPacketBean>{
        void showRedInfo(RedPacketBean infoBean);
        void showData(List<RedPacketBean> datas);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getData(String id,String loadTime);
        void getRedInfo(String id);
    }

}
