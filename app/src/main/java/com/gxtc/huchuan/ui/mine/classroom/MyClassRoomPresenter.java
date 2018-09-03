package com.gxtc.huchuan.ui.mine.classroom;

import com.gxtc.huchuan.data.MyClassRoomRepository;

/**
 * Created by ALing on 2017/3/9 .
 */

public class MyClassRoomPresenter implements MyClassRoomContract.Presenter {
    private MyClassRoomContract.View mView;
    private MyClassRoomContract.Source mData;

    private int start = 0;

    public MyClassRoomPresenter(MyClassRoomContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new MyClassRoomRepository();
    }
    @Override
    public void start() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void getData(boolean isRefresh) {

    }

    @Override
    public void loadMore() {

    }
}
