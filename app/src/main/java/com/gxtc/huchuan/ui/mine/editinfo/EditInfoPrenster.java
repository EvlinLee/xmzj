package com.gxtc.huchuan.ui.mine.editinfo;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.EditInfoRepository;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.MainActivity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by ALing on 2017/2/22 .
 */

public class EditInfoPrenster implements EditInfoContract.Presenter{

    EditInfoContract.Source mData;
    EditInfoContract.View mView;

    public EditInfoPrenster(EditInfoContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new EditInfoRepository();
    }
    @Override
    public Notification sendNotification(Context mContext, String title, String contenttext) {
        Intent intent  = new Intent(mContext, MainActivity.class);
        PendingIntent pi      = PendingIntent.getActivity(mContext, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        builder.setContentText(contenttext);
        return builder.build();
    }

    @Override
    public void getUserInfo(String token) {
        mView.showLoad();
        mData.getUsetInfo(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.getUserSuccess(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },token);
    }

    @Override
    public void getEditInfo(Map<String, String> map) {
        mView.showLoad();
        mData.getEditInfo(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.EditInfoSuccess(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);
    }

    @Override
    public void uploadAvatar(final RequestBody body) {
        if(mView == null) return;
        mView.showLoad();
        mData.uploadAvatar(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showUploadResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },body);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }
}
