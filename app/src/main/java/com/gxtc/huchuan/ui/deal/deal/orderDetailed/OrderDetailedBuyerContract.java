package com.gxtc.huchuan.ui.deal.deal.orderDetailed;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;

import java.util.HashMap;

/**
 * Created by Steven on 17/4/6.
 */

public interface OrderDetailedBuyerContract {

    interface View extends BaseUserView<OrderDetailedBuyerContract.Presenter>{
        void showData(OrderDetailedBean bean);
        void showCancelSuccress();
        void showCancelFailed(String message);
        void showEndTime(boolean isEnd,String time);
        void showConfirmSuccess();
        void showConfirmFailed(String message);
        void showChatRoom(ChatRoomBean bean);
        void showGetChatRoomFailed(String info);
        void showSaveChatSuccess();
        void showSaveChatFailed(String info);
    }

    interface Presenter extends BasePresenter{
        void getData(String id);
        void cancelOrder(String id);
        void computeEndTime(long endTime);
        void confirmDeal(HashMap<String,String> map);
        void getChatRoom(String token,String tradeInfoId);
        void saveChatRoom(HashMap<String,String> map);
    }
}
