package com.gxtc.huchuan.ui.deal.deal.orderDetailed;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;

import java.util.HashMap;

/**
 * Created by Steven on 17/4/6.
 */

public interface OrderDetailedContract {

    interface View extends BaseUserView<OrderDetailedContract.Presenter>{
        void showData(OrderDetailedBean bean);
        void showAgreeOrRejectSuccess();
        void showAgreeOrRejectFailed(String message);
        void showConfirmSuccess();
        void showConfirmFailed(String message);
        void showChatRoom(ChatRoomBean bean);
        void showGetChatRoomFailed(String info);
        void showSaveChatSuccess();
        void showSaveChatFailed(String info);
        void showEndTime(boolean isEnd,String time);
    }

    interface Presenter extends BasePresenter{
        void getData(String id);
        void submitAgreeOrReject(HashMap<String,String> map);
        void confirmDeal(HashMap<String,String> map);
        void getChatRoom(String token,String tradeInfoId);
        void saveChatRoom(HashMap<String,String> map);
        void computeEndTime(long endTime);
    }
}
