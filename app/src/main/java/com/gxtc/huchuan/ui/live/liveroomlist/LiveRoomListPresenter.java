package com.gxtc.huchuan.ui.live.liveroomlist;

import android.text.TextUtils;
import android.util.Log;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.FollowLecturerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.im.adapter.MyConversationListAdapter;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 */

public class LiveRoomListPresenter implements LiveRoomListContract.Presenter {


    private LiveRoomListContract.View mView;
    private LiveRoomListSource mLiveRoomListSource;

    public LiveRoomListPresenter(LiveRoomListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mLiveRoomListSource = new LiveRoomListSource();

    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mLiveRoomListSource.destroy();
        mView = null;
    }

    @Override
    public void getDatas(String start, String pageSize) {
        HashMap<String, String> map = new HashMap<>();

        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
        }
        map.put("start", start);
        if (!TextUtils.isEmpty(pageSize)) {
            map.put("pageSize", pageSize);
        }
        mLiveRoomListSource.getDats(map, new ApiCallBack<List<LiveRoomBean>>() {
            @Override
            public void onSuccess(List<LiveRoomBean> data) {
//                if(mView == null)   return;
//
//                if (data.size() > 0) {
//                    mView.showDatas(data);
//                } else {
//                    mView.showLoadFinish();
//                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

        @Override
    public void getFollowDatas(String start, String pageSize) {
        HashMap<String, String> map = new HashMap<>();

        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
        }
        map.put("start", start);
        if (!TextUtils.isEmpty(pageSize)) {
            map.put("pageSize", pageSize);
        }
        mLiveRoomListSource.getFollowDats(map, new ApiCallBack<List<FollowLecturerBean>>() {
            @Override
            public void onSuccess(List<FollowLecturerBean> data) {
                if(mView == null)   return;
                if (data.size()>0){
                    mView.showDatas(data);
                }else {
                    mView.showLoadFinish();
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);

            }
        });
    }

    @Override
    public void changeFollow(String token, final String id, final int position) {
        mLiveRoomListSource.changeFolow(token, id, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null)   return;
                mView.updateFollow(id,position);
            }

            @Override
            public void onError(String errorCode, String message) {

            }
        });
    }
}
