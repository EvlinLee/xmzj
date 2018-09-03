package com.gxtc.huchuan.ui.mine.setting.dustbin;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DustbinListAdapter;
import com.gxtc.huchuan.bean.DustbinListBean;
import com.gxtc.huchuan.bean.event.EventDustbinRecoverBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DustbinListActivity extends BaseTitleActivity implements DustbinListContract.View,View.OnClickListener{
    @BindView(R.id.swipelayout)          SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.recyclerView)         RecyclerView        listView;

    private DustbinListContract.Presenter mPresenter;
    private DustbinListAdapter adapter;
    private List<EventDustbinRecoverBean>     HuiFuList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbin_list);

    }

    @Override
    public void initView(){
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_huishou_list));
        getBaseHeadView().showBackButton(this);
        refreshLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        adapter = new DustbinListAdapter(this,new ArrayList<DustbinListBean>(),R.layout.item_dustbin_list);
        listView.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                listView.reLoadFinish();

            }
        });
          getBaseHeadView().showBackButton(this);
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
        adapter.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        new DustbinListPersenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<DustbinListBean> datas) {
        listView.notifyChangeData(datas,adapter);
    }

    @Override
    public void showRefreshFinish(List<DustbinListBean> datas) {
        listView.notifyChangeData(datas,adapter);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showLoadMore(List<DustbinListBean> datas) {
        listView.changeData(datas,adapter);
    }

    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showHuifuSuccess(int id) {
        ToastUtil.showShort(this,getString(R.string.toast_recover_success));
        for(int i = 0 ; i < adapter.getList().size(); i++){
            DustbinListBean temp = adapter.getList().get(i);
            if(id == temp.getId()){
                listView.removeData(adapter,i);
                return;
            }
        }
    }



    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        refreshLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {
        mPresenter.getData(false);
    }

    @Override
    public void showError(String info) {
        if(adapter == null){
            getBaseEmptyView().showEmptyContent(info);
        }else{
            ToastUtil.showShort(this,info);
        }
    }

    @Override
    public void showNetError() {

    }

    @Override
    public void setPresenter(DustbinListContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    //刷新回调
    @Override
    public void showRefreshData(List<DustbinListBean> datas) {

    }

    //加载更多回调
    @Override
    public void showLoadMoreData(List<DustbinListBean> datas) {

    }

    //没有更多加载
    @Override
    public void showNoLoadMore() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.tv_huifu:
                DustbinListBean bean = (DustbinListBean) v.getTag();
                mPresenter.HuiFuDustbin(bean.getId(),bean.getType());
                break;
        }
    }

    @Subscribe
    public void onEvent(EventDustbinRecoverBean bean) {

    }

}
