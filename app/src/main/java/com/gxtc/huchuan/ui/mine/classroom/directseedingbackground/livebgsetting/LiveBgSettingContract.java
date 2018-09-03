package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/21 .
 */

public class LiveBgSettingContract {
    public interface View extends BaseUserView<LiveBgSettingContract.Presenter> {
        void showLiveManageData(LiveRoomBean bean);
        void showPicList(List<BgPicBean> picData);
        void showCompressSuccess(File file);
        void showCompressFailure();
        void showUploadingSuccess(String url);
        void showChatRoomSetting(LiveBgSettingBean bean);

        /**
         * 加载更多
         * @param datas
         */
        void showLoadMore(List<BgPicBean> datas);

        /**
         * 没有更多数据
         */
        void showNoMore();

        void showManagerList(LiveManagerBean bean);

        void showMoreManagerList(LiveManagerBean bean);
    }

    public interface Presenter extends BasePresenter {
        void getLiveManageData(HashMap<String,String> map);
        void getPicList(HashMap<String,String> map);
        void loadMrore();
        //压缩图片
        void compressImg(String s);

        //上传图片
        void uploadingFile(File file);
        //修改信息
        void saveChatRoomSetting(HashMap<String,String> map);

        void getManagerList(HashMap<String,String> map);

        void loadMoreManagers(HashMap<String,String> map);
    }

    public interface Source extends BaseSource {
        void getLiveManageData(HashMap<String,String> map, ApiCallBack<LiveRoomBean> callBack);
        //上传图标
        void saveChatRoomSetting(ApiCallBack<LiveBgSettingBean> callBack, HashMap<String,String> map);
        //获取图片列表
        void getPicList(ApiCallBack<List<BgPicBean>> callBack, HashMap<String,String> map);

        //获取管理员列表
        void getManagerList(ApiCallBack<LiveManagerBean> callBack, HashMap<String,String> map);

        void getChatJoinList(ApiCallBack<ArrayList<ChatJoinBean.MemberBean>> callBack, HashMap<String, String> map);

        void getChatJoinList1(ApiCallBack<ChatJoinBean> callBack, HashMap<String, String> map);

    }
}
