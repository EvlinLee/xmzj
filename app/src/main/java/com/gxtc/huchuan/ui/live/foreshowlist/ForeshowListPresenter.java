package com.gxtc.huchuan.ui.live.foreshowlist;

import android.util.Log;

import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.live.hotlist.HotListContract;
import com.gxtc.huchuan.ui.live.hotlist.HotListSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/30.
 */

public class ForeshowListPresenter implements ForeshowListContract.Presenter {
    private static final String TAG = "ForeshowListPresenter";

    private ForeshowListContract.View mView;
    private ForeshowListSource        source;


    private boolean flag = false;
    private int mStart;

    public ForeshowListPresenter(ForeshowListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        source = new ForeshowListSource();
    }

    @Override
    public void getData(boolean isloadmroe, final int start) {
        if (flag) {
            return;
        }
        if (start == 0) mView.showLoad();
        mStart = start;
        HashMap<String, String> map = new HashMap<>();
        map.put("start", String.valueOf(start));


        source.getData(map, new ApiCallBack<ArrayList<ChatInfosBean>>() {
            @Override
            public void onSuccess(ArrayList<ChatInfosBean> data) {
                if(mView == null)    return;

                flag = false;
                if (mStart == 0) {
                    mView.showData(data);
                    mView.showLoadFinish();
                } else {
                    if (data.size() == 0) {
                        mView.loadFinish();
                    } else {
                        mView.showLoMore(data);
                    }

                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)    return;
                flag = false;
                if (mStart == 0) {
                    mView.showLoadFinish();
                }
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        source.destroy();
        mView = null;
    }
}
