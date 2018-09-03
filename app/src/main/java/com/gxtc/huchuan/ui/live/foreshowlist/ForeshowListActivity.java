package com.gxtc.huchuan.ui.live.foreshowlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.HotListAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/30.
 *
 */

public class ForeshowListActivity extends BaseTitleActivity implements ForeshowListContract.View, View
        .OnClickListener, BaseRecyclerAdapter.OnReItemOnClickListener {
    private static final String TAG = "ForeshowListActivity";

    public static final  int    LOGINREQUEST = 1;
    private final int GOTO_CIRCLE_REQUESTCODE = 1 << 3;


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private String title;
    private ForeshowListContract.Presenter presenter;
    private HotListAdapter hotListAdapter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlist);
    }

    @Override
    public void initView() {
        new ForeshowListPresenter(this);
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(title != null ? title : "热门课程");

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        recyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getData(true, hotListAdapter.getItemCount());
            }
        });
    }

    @Override
    public void initData() {
        presenter.getData(false, 0);
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
    public void showData(final List<ChatInfosBean> datas) {
        if (hotListAdapter == null) {
            hotListAdapter = new HotListAdapter(this, datas, R.layout.item_live_list);
            recyclerview.setAdapter(hotListAdapter);
            hotListAdapter.setOnReItemOnClickListener(this);
        }
        hotListAdapter.setCollectListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().isLogin()) {
                    int position = (int) v.getTag();
                    collect(UserManager.getInstance().getToken(),datas.get(position),position);
                } else {
                    GotoUtil.goToActivityForResult(ForeshowListActivity.this, LoginAndRegisteActivity.class,
                            LOGINREQUEST);
                }
            }
        });
    }
    private void collect(String token, final ChatInfosBean mChatInfosBean,final int position) {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "2");
        map.put("bizId", mChatInfosBean.getId());

        Subscription subCollect =
                AllApi.getInstance()
                        .saveCollection(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                switch (mChatInfosBean.getIsCollect()){
                                    case "0":
                                        mChatInfosBean.setIsCollect("1");
                                        break;
                                    case "1":
                                        mChatInfosBean.setIsCollect("0");
                                        break;
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
    public void showLoMore(List<ChatInfosBean> datas) {
        if (hotListAdapter != null) {
            recyclerview.changeData(datas, hotListAdapter);
        }
    }

    @Override
    public void setPresenter(ForeshowListContract.Presenter presenter) {
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
    public void loadFinish(){
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
        if (hotListAdapter != null) {
            String id = hotListAdapter.getList().get(position).getId();
            ChatInfosBean bean = hotListAdapter.getList().get(position);
            chatItemClick(bean);
//            LiveIntroActivity.startActivity(this, id);
        }
    }

    private void chatItemClick(ChatInfosBean bean) {
        if (bean.isSingUp()) {
            LiveIntroActivity.startActivity(ForeshowListActivity.this, bean.getId());
        } else if (!"0".equals(bean.getChatSeries())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.isSingUp())) {
                    LiveIntroActivity.startActivity(ForeshowListActivity.this, bean.getId());
                } else {
                    //这里提示先购买系列课
                    SeriesActivity.startActivity(ForeshowListActivity.this, bean.getChatSeries(), true);

                }
            } else {
                GotoUtil.goToActivityForResult(ForeshowListActivity.this, LoginAndRegisteActivity.class,
                        LOGINREQUEST);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(ForeshowListActivity.this, bean.getId());
                } else {
                    //这里提示加入圈子
                    Intent intent = new Intent(ForeshowListActivity.this, CircleJoinActivity.class);
                    intent.putExtra("byLiveId", (int) bean.getGroupId());
                    startActivityForResult(intent, GOTO_CIRCLE_REQUESTCODE);
                }
            } else {
                GotoUtil.goToActivityForResult(ForeshowListActivity.this, LoginAndRegisteActivity.class,
                        LOGINREQUEST);
            }
        } else {
            LiveIntroActivity.startActivity(ForeshowListActivity.this, bean.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
