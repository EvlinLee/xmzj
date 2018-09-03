package com.gxtc.huchuan.ui.mine.classroom.history;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ClassRoomHistoryAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.bean.event.EventClassHistoryBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.live.LiveActivity;
import com.gxtc.huchuan.ui.live.conversation.LiveConversationActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe:
 * Created by ALing on 2017/3/13 .
 */

public class MyHistoryActivity extends BaseTitleActivity implements View.OnClickListener,
        MyHistoryContract.View, BaseRecyclerAdapter.OnReItemOnClickListener {

    @BindView(R.id.rc_list)                RecyclerView       mRcList;
    @BindView(R.id.swipe_layout)           SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.tv_choose_all)          TextView           mTvChooseAll;
    @BindView(R.id.tv_del)                 TextView           mTvDel;
    @BindView(R.id.ll_edit_bottom)         LinearLayout       mLlEditBottom;

    private ClassRoomHistoryAdapter adapter;
    //private ParicipationAdaspter                 adapter;

    private MyHistoryContract.Presenter          mPresenter;
    private ArrayList<ChatInfosBean>             list;
    private boolean                              isEditable;     //是否可编辑
    private boolean                              isCheck;
    private List<BaseRecyclerAdapter.ViewHolder> holders;
    private List<String>                         chatInfoIdList;
    int count = 0;
    String clickType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
    }

    @Override
    public void initView() {
        super.initView();
        String title = getIntent().getStringExtra("title");
        clickType = getIntent().getStringExtra(MainActivity.STATISTICS_STATUS);
        getBaseHeadView().showTitle(TextUtils.isEmpty(title) ? getString(R.string.title_personal_history) : title);
        getBaseHeadView().showBackButton(this);

        mSwipeLayout.setColorSchemeResources(Constant.REFRESH_COLOR);
        View view  = getLayoutInflater().inflate(R.layout.activity_my_histoty_header,null);
        view.findViewById(R.id.class_home_page_button).setOnClickListener(this);
        mRcList.addHeadView(view);
        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);     //刷新重新获取数据
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();       //加载更多
            }
        });
        //暂时改成跟我观看的历史列表一样
        adapter = new ClassRoomHistoryAdapter(this, new ArrayList<UnifyClassBean>(), R.layout.item_myparticipation);
        //adapter = new ParicipationAdaspter(this, new ArrayList<ChatInfosBean>(), R.layout.item_myparticipation);
        adapter.setOnReItemOnClickListener(this);
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        new MyHistoryPresenter(this,clickType);
        mPresenter.getData(false);

        chatInfoIdList = new ArrayList<>();

        mRcList.notifyChangeData(list, adapter);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View v, int position) {
        UnifyClassBean bean = adapter.getList().get(position);
        if(bean.getType() == 1){
            mPresenter.getChatInfos(bean.getData().getId());
        }else{
            SeriesActivity.startActivity(this, bean.getData().getId() + "");
        }
    }


    @OnClick({R.id.tv_choose_all, R.id.tv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //热门课程
            case R.id.class_home_page_button:
                GotoUtil.goToActivity(this, LiveActivity.class);
                break;
        }
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<UnifyClassBean> datas) {
        mRcList.notifyChangeData(datas, adapter);
        mRcList.setAdapter(adapter);
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<UnifyClassBean> datas) {
        mRcList.notifyChangeData(datas, adapter);
        count = 0;
    }


    //加载更多
    @Override
    public void showLoadMore(List<UnifyClassBean> datas) {
        mRcList.changeData(datas, adapter);
    }

    //没有加载更多数据
    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }


    @Override
    public void showDelResult() {
        mRcList.notifyChangeData(list, adapter);
    }

    @Override
    public void showChatInfoSuccess(ChatInfosBean infosBean) {
        LiveConversationActivity.startActivity(this, infosBean);
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mSwipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {
        mPresenter.getData(false);
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    @Override
    public void setPresenter(MyHistoryContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }


    /**
     * 删除按钮
     */
    @Subscribe
    public void onEvent(EventClassHistoryBean bean) {
        String chatInfoId = bean.getChatInfoId();

        if (bean.isSelected()) {
            count++;
            chatInfoIdList.add(chatInfoId);
        } else {
            count--;
            if (count < 0) {
                count = 0;
            }
            chatInfoIdList.remove(chatInfoId);
        }

        mTvDel.setText("删除(" + count + ")");
    }

}
