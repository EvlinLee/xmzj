package com.gxtc.huchuan.ui.live.hostpage.newhostpage;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by zzg on 2017/12/18.
 */

public class ClassAndSerisePresenter implements LiveAndSeriseContract.Presenter {
    private  ClassAndSeriseResposery data;
    private  LiveAndSeriseContract.View view;

    public ClassAndSerisePresenter(LiveAndSeriseContract.View view) {
        this.view = view;
        data = new ClassAndSeriseResposery();
        this.view.setPresenter(this);
    }

    @Override
    public void start() {}

    @Override
    public void getData(String chatRoomId, String token, String start) {
        data.getData(chatRoomId, token, start, new ApiCallBack<List<ChatInfosBean>>() {
            @Override
            public void onSuccess(List<ChatInfosBean> data) {
                if (view == null) return;
                if(data == null || data.size() == 0){
                    view.showEmpty();
                    return;
                }
                view.showDatat(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }

    @Override
    public void destroy() {
        data.destroy();
    }
}
