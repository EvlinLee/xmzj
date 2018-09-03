package com.gxtc.huchuan.ui.mine.editinfo;

import android.app.Notification;
import android.content.Context;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by ALing on 2017/2/22.
 */

public class EditInfoContract {
    public interface View extends BaseUserView<EditInfoContract.Presenter> {
        void getUserSuccess(User object);
        void EditInfoSuccess(Object object);
        void showUploadResult(User bean);
        //压缩图片
        void compression(String path);
    }

    public interface Presenter extends BasePresenter {
        void getUserInfo(String token);
        void getEditInfo(Map<String, String> map);
        void uploadAvatar(RequestBody body);
        Notification sendNotification(Context mContext, String title, String contenttext);
    }

    public interface Source extends BaseSource {

        void getUsetInfo(ApiCallBack<User> callBack, String token);

        //编辑用户资料
        void getEditInfo(ApiCallBack<Object> callBack, Map<String,String> map);
        //上传头像
        void uploadAvatar(ApiCallBack<User> callBack, RequestBody body);

    }
}
