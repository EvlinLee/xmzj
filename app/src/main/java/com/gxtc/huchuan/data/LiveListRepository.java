package com.gxtc.huchuan.data;

import com.gxtc.huchuan.bean.LiveListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.live.list.LiveListContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gubr on 2017/2/27.
 */

public class LiveListRepository implements LiveListContract.Source {
    @Override
    public void getLiveListDatas(ApiCallBack<List<LiveListBean>> callBack, int count, int id) {
        ArrayList<LiveListBean> objects = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            objects.add(new LiveListBean());
        }
        callBack.onSuccess(objects);
    }

    @Override
    public List<LiveListBean> getHistory(String currentType) {
        return null;
    }

    @Override
    public void saveHistory(int id, List<LiveListBean> data) {

    }

    @Override
    public void destroy() {

    }
}
