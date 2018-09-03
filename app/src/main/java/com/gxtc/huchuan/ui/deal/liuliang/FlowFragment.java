package com.gxtc.huchuan.ui.deal.liuliang;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FlowListAdapter;
import com.gxtc.huchuan.bean.FlowListBean;
import com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder.DispatchOrderActivity;
import com.gxtc.huchuan.ui.deal.liuliang.profit.ProfitActivity;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.AccountInfoActivity;
import com.gxtc.huchuan.ui.deal.liuliang.takeOrder.TakeSettingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流量
 * Created by Steven on 17/2/13.
 */
public class FlowFragment extends BaseTitleFragment implements FlowContract.View, View.OnClickListener {

    @BindView(R.id.swipe_flow)      SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.rl_flow)         RecyclerView        listView;
    @BindView(R.id.btn_flow_next)   TextView            btnNext;

    private View headView;

    private View btnPaidan;
    private View btnJiedan;
    private View btnWenzhang;
    private View btnXinxi;

    private FlowListAdapter        adapter;
    private FlowContract.Presenter mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_flow,container,false);
        return view;
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });

        adapter = new FlowListAdapter(getContext(),new ArrayList<FlowListBean>(),R.layout.item_list_flow);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                for (int i = 0; i < adapter.getList().size(); i++) {
                    //取消当前选择
                    if(i == position && adapter.getList().get(i).isSelect()){
                        adapter.getList().get(i).setSelect(false);
                        listView.notifyItemChanged(i);
                        btnNext.setVisibility(View.INVISIBLE);
                        return ;
                    }

                    //取消所有选择
                    if(adapter.getList().get(i).isSelect()){
                        adapter.getList().get(i).setSelect(false);
                        listView.notifyItemChanged(i);
                    }
                }
                btnNext.setVisibility(View.VISIBLE);
                adapter.getList().get(position).setSelect(true);
                listView.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void initData() {
        refreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        initListView();

        new FlowPresenter(this);
        mPresenter.getData(false);
    }

    private void initListView(){
        headView = View.inflate(getContext(),R.layout.head_flow,null);
        btnPaidan = headView.findViewById(R.id.btn_deal_tab_paidan);
        btnJiedan = headView.findViewById(R.id.btn_deal_tab_jiedan);
        btnWenzhang = headView.findViewById(R.id.btn_deal_tab_wenzhang);
        btnXinxi = headView.findViewById(R.id.btn_deal_tab_info);

        btnPaidan.setOnClickListener(this);
        btnJiedan.setOnClickListener(this);
        btnWenzhang.setOnClickListener(this);
        btnXinxi.setOnClickListener(this);

        setMargin(btnPaidan,true);
        setMargin(btnJiedan,false);
        setMargin(btnWenzhang,false);
        setMargin(btnXinxi,false);

        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false));
        listView.addHeadView(headView);
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    private void setMargin(View view,boolean flag){
        int topMargin = WindowUtil.dip2px(getContext(),10);
        int screenWidth = WindowUtil.getScreenWidth(getContext());
        int width = (screenWidth - topMargin * 5) / 4;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = width;
        params.topMargin = topMargin;
        params.bottomMargin = topMargin;
        params.rightMargin = topMargin;

        if(flag) params.leftMargin = topMargin;

        view.setLayoutParams(params);
    }

    @OnClick(R.id.btn_flow_next)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //派单推广
            case R.id.btn_deal_tab_paidan:
                GotoUtil.goToActivity(getActivity(), DispatchOrderActivity.class);
                break;

            //接单盈利
            case R.id.btn_deal_tab_jiedan:
                GotoUtil.goToActivity(getActivity(), ProfitActivity.class);
                break;

            //文章管理
            case R.id.btn_deal_tab_wenzhang:
                break;

            //公众号信息
            case R.id.btn_deal_tab_info:
                GotoUtil.goToActivity(getActivity(), AccountInfoActivity.class);
                break;

            //下一步
            case R.id.btn_flow_next:
                GotoUtil.goToActivity(getActivity(), TakeSettingActivity.class);
                break;
        }
    }

    @Override
    public void showData(List<FlowListBean> datas) {
        listView.notifyChangeData(datas,adapter);
        listView.setAdapter(adapter);
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<FlowListBean> datas) {
        listView.notifyChangeData(datas,adapter);
    }


    //加载更多
    @Override
    public void showLoadMore(List<FlowListBean> datas) {
        listView.changeData(datas,adapter);
    }

    //没有加载更多数据
    @Override
    public void showNoMore() {
        listView.loadFinish();
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
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_net_error));
    }

    @Override
    public void setPresenter(FlowContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }


}
