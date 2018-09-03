package com.gxtc.huchuan.ui.circle.circleInfo;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/8 .
 */

public class CircleInfoContract {
    public interface View extends BaseUserView<Presenter> {
        void showMemberList(List<CircleMemberBean> datas);
        void showRefreshFinish(List<CircleMemberBean> datas);
        void showLoadMore(List<CircleMemberBean> datas);
        void showNoMore();
        void showCompressSuccess(File file);
        void showCompressFailure();
        void showUploadingSuccess(String url);
        void showCircleInfo(CircleBean bean);
        void showEditCircle(Object o);

        void removeMember(CircleMemberBean circleMemberBean);

        void transCircle(CircleMemberBean circleMemberBean);

        void showChangeMemberTpye(CircleMemberBean circleMemberBean);
    }

    public interface Presenter extends BasePresenter {
        void getMemberList(int groupId,int type,boolean isRefresh, String onlyLook);
        void loadMore(int groupId,int type);
        //压缩图片
        void compressImg(String s);
        //上传图片
        void uploadingFile(File file);
        void getCircleInfo(String token,int groupId);
        void editCircleInfo(HashMap<String,Object> map);
        void removeMember(String token, CircleMemberBean circleMemberBean);
        void transCircle(String token, CircleMemberBean circleMemberBean);
        void changeMemberTpye(String token,CircleMemberBean circleMemberBean,int type);

    }

    public interface Source extends BaseSource {
        void getMemberList(int groupId,int type,int start, String onlyLook,ApiCallBack<List<CircleMemberBean>> callBack);
        void getCircleInfo(String token,int groupId,ApiCallBack<CircleBean> callBack);
        void editCircleInfo(HashMap<String,Object> map,ApiCallBack<Object> callBack);

        void removeMember(String token, int groupId, String userCode, ApiCallBack<Void> apiCallBack);

        void transCircle(String token, int groupId, String userCode, ApiCallBack<Void> apiCallBack);

        void changeMemberTpye(String token,int groupId,String userCode,int i,ApiCallBack<Void> apiCallBack);
    }
}
