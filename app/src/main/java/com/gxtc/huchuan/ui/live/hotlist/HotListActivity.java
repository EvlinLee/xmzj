package com.gxtc.huchuan.ui.live.hotlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.UnifyClassAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/30.
 */

public class HotListActivity extends BaseTitleActivity implements HotListContract.View,
        View.OnClickListener, BaseRecyclerAdapter.OnReItemOnClickListener {

    private static final String TAG                     = "HotDealListActivity";
    public static final  int    LOGINREQUEST            = 1;
    private final        int    GOTO_CIRCLE_REQUESTCODE = 1 << 3;

    @BindView(R.id.recyclerview) RecyclerView recyclerview;

    private String                    title;
    private HotListContract.Presenter presenter;
    //private HotListAdapter            hotListAdapter;
    //private LiveRoomNewAdapter        hotListAdapter;
    private UnifyClassAdapter         adapter;
    private String                    type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlist);
    }

    @Override
    public void initView() {
        new HotListPresenter(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        type = intent.getStringExtra("type");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(title != null ? title : "排行榜");


        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        recyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getData(true, adapter.getItemCount(), type);
            }
        });
    }

    @Override
    public void initData() {
        presenter.getData(false, 0, type);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.HeadRightImageButton:

                break;
        }
    }


    @Override
    public void showData(final List<UnifyClassBean> datas) {
        if (adapter == null) {
            //hotListAdapter = new HotListAdapter(this, datas, R.layout.item_live_list);
            adapter = new UnifyClassAdapter(this, datas, R.layout.item_live_new_room);
            recyclerview.setAdapter(adapter);
            adapter.setOnReItemOnClickListener(this);
        }
    }


    private void collect(String token, final ChatInfosBean mChatInfosBean,final int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        if("0".equals(mChatInfosBean.getChatSeries())){
            map.put("bizType", "2");//话题
            map.put("bizId", mChatInfosBean.getId());
        }else {
            map.put("bizType", "9");//系列课
            map.put("bizId", mChatInfosBean.getChatSeriesData().getId());
        }
        Subscription subCollect =
                AllApi.getInstance()
                        .saveCollection(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if("0".equals(mChatInfosBean.getChatSeries())){
                                    switch (mChatInfosBean.getIsCollect()){
                                        case "0":
                                            mChatInfosBean.setIsCollect("1");
                                            break;
                                        case "1":
                                            mChatInfosBean.setIsCollect("0");
                                            break;
                                    }
                                }else {
                                    switch (mChatInfosBean.getChatSeriesData().getIsCollect()){
                                        case 0:
                                            mChatInfosBean.getChatSeriesData().setIsCollect(1);
                                            break;
                                        case 1:
                                            mChatInfosBean.getChatSeriesData().setIsCollect(0);
                                            break;
                                    }
                                }
                                recyclerview.notifyItemChanged(position);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(getApplication(), message);
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this,subCollect);
    }

    @Override
    public void showLoMore(List<UnifyClassBean> datas) {
        if (adapter != null) {
            recyclerview.changeData(datas, adapter);
        }
    }

    @Override
    public void setPresenter(HotListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void loadFinish() {
        if (recyclerview != null) {
            recyclerview.loadFinish();
        }
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (adapter != null) {
            UnifyClassBean bean = adapter.getList().get(position);
            if(bean.getType() == 1){
                LiveIntroActivity.startActivity(HotListActivity.this, bean.getData().getId() + "");
            }else{
                SeriesActivity.startActivity(HotListActivity.this, bean.getData().getId() + "", true);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
