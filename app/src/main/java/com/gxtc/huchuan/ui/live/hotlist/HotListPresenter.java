package com.gxtc.huchuan.ui.live.hotlist;

import android.text.TextUtils;

import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.MainActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/30.
 *
 */
public class HotListPresenter implements HotListContract.Presenter {
    private static final String TAG = "HotListPresenter";

    private HotListContract.View mView;
    private HotListSource        hotListSource;


    private boolean flag = false;
    private int mStart;

    public HotListPresenter(HotListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        hotListSource = new HotListSource();
    }

    @Override
    public void getData(boolean isloadmroe, final int start, String type) {
        if (flag) {
            return;
        }
        if (start == 0) mView.showLoad();
        mStart = start;
        HashMap<String, String> map = new HashMap<>();
        map.put("start", String.valueOf(start));
        map.put("type", type);
        map.put("clickType ", MainActivity.STATISTICS);
        String token = UserManager.getInstance().getToken();
        if(!TextUtils.isEmpty(token)) map.put("token", token);

        flag = true;
        hotListSource.getData(map, new ApiCallBack<List<UnifyClassBean>>() {
            @Override
            public void onSuccess(List<UnifyClassBean> data) {
                if(mView == null)   return;
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
                if(mView == null)   return;
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
        hotListSource.destroy();
        mView = null;
    }
}
