package com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DistributionAdapter;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Describe:课堂分销
 * Created by ALing on 2017/5/16 .
 */

public class ClassRoomDistributionFragment extends BaseTitleFragment implements ProfitContract.View, BaseRecyclerAdapter.OnReItemOnClickListener,View.OnClickListener {
    @BindView(R.id.rc_list)        RecyclerView       mRcList;
    @BindView(R.id.swipe_layout)   SwipeRefreshLayout mSwipeLayout;

    private ProfitContract.Presenter mPresenter;
    private List<DistributionBean> distributionList;
    private String type = "1";        //显示类型。默认1      1:课堂  2：圈子
    private DistributionAdapter adapter;
    private PopPosition         mPopPosition;
    private HashMap<String,String> map;
    private int dateType = 0;       //日期类型。 0：本日，1：本周，2：本月，3:本年，4：全部

    private TextView                    mTvShareCount;
    private TextView                    mTvAllCount;
    private TextView                    mTvDate;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_share_profit_list, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        new ProfitPresenter(this);
        map = new HashMap<>();

        initRecyclerView();
        getDistributionClass();
    }
    //获取课堂分销数据
    private void getDistributionClass(){
        mPresenter.getDistributionCount(UserManager.getInstance().getToken(),type,dateType+"");
        mPresenter.getDistributionList(type,dateType+"",false);

        map.put("token",UserManager.getInstance().getToken());
        map.put("dateType", String.valueOf(dateType));
        map.put("type",type);        //1  课堂   2 圈子
        map.put("showType",type);    //1 分销    2 圈子
        mPresenter.getIncomeStatistics(map);
    }

    @Override
    public void initListener() {
        super.initListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getDistributionCount(UserManager.getInstance().getToken(),type,dateType+"");
                mPresenter.getDistributionList(type,dateType+"",true);     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreDistribution(type,dateType+"");       //加载更多
            }
        });

    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        View view =LayoutInflater.from(getActivity()).inflate(R.layout.activity_share_profit_list_header,null);
        mTvShareCount = (TextView) view.findViewById(R.id.tv_share_count);
        mTvAllCount = (TextView) view.findViewById(R.id.tv_all_count);
        mTvDate = (TextView) view.findViewById(R.id.tv_date);
        mRcList.addHeadView(view);
        mTvDate.setOnClickListener(this);
        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.HORIZONTAL_LIST, 20,
                getResources().getColor(R.color.module_divide_line)));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        distributionList = new ArrayList<>();

        adapter = new DistributionAdapter(getActivity(), distributionList, R.layout.item_list_income);
        adapter.setOnReItemOnClickListener(this);
        mRcList.setAdapter(adapter);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                LiveIntroActivity.startActivity(getContext(),adapter.getList().get(position).getId());
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_date:
                showDatePop();
                break;
        }
    }

    /**
     * 选择时间
     */
    private void showDatePop() {
        final String[] dateaArr = getResources().getStringArray(R.array.mine_income_detail);
        if (mPopPosition == null) {
            mPopPosition = new PopPosition(getActivity(), R.layout.pop_ts_position);
            if (dateaArr.length != 0){
                mPopPosition.setData(dateaArr);
                mPopPosition.setTitle("选择日期");
                mPopPosition.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        mTvDate.setText(dateaArr[newVal]);
                        dateType = newVal/* + 1*/;
                        Log.d("dataType", "onValueChange: "+dateType);
                        getDistributionClass();
                    }
                });
            }
        }
        mPopPosition.showPopOnRootView(getActivity());
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showDistributList(List<DistributionBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showProfitList(List<DistributionBean> datas) {}

    @Override
    public void showDistributionCount(DistributionCountBean bean) {
        mTvShareCount.setText("共有"+bean.getCount()+"条分享记录   佣金总计：");
    }

    @Override
    public void showIncomeStatistics(DistributionCountBean bean) {
        mTvAllCount.setText("￥"+bean.getIncomeMoney());
    }

    @Override
    public void showRefreshFinish(List<DistributionBean> datas) {
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showLoadMoreProfit(List<DistributionBean> datas) {
    }

    @Override
    public void showLoadMoreDistribution(List<DistributionBean> datas) {
        mRcList.changeData(datas,adapter);
    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void setPresenter(ProfitContract.Presenter presenter) {
        this.mPresenter = presenter;
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
        mSwipeLayout.setRefreshing(false);
        distributionList.removeAll(distributionList);
        mRcList.notifyChangeData(distributionList,adapter);
    }

    @Override
    public void showReLoad() {
        mPresenter.getDistributionList(type,dateType+"",false);
    }

    @Override
    public void showError(String info) {
        mSwipeLayout.setRefreshing(false);
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onItemClick(View v, int position) {

    }
}
