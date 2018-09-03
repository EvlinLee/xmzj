package com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom;

import android.os.Bundle;
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
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ProfitAdapter;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.incomedetail.distribute.ClassProfitDetailActivity;
import com.gxtc.huchuan.ui.mine.incomedetail.distribute.DistributeContract;
import com.gxtc.huchuan.ui.mine.incomedetail.distribute.DistributePresenter;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Describe:课堂收益
 * Created by ALing on 2017/5/16 .
 */

public class ClassRoomProfitFragment extends BaseTitleFragment implements DistributeContract.View,View.OnClickListener{
    @BindView(R.id.rc_list)        RecyclerView       mRcList;
    @BindView(R.id.swipe_layout)   SwipeRefreshLayout mSwipeLayout;

    private DistributeContract.Presenter mPresenter;
    private List<DistributionBean>   profitList;
    private String type = "1";        //显示类型。默认1      1:课堂  2：圈子
    private ProfitAdapter          adapter;
    private HashMap<String,String> map;
    private int                    dateType = 0;       //日期类型。 0：本日，1：本周，2：本月，3:本年，4：全部
    private PopPosition            mPopPosition;

    private TextView                    mTvShareCount;
    private TextView                    mTvAllCount;
    private TextView                    mTvDate;
    private boolean isChoose;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_share_profit_list, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        new DistributePresenter(this);
        map = new HashMap<>();

        initRecyclerView();
        setDateData();
        getProfitClass();
    }

    private void setDateData() {
        switch (dateType){
            case 0:
                mTvDate.setText("本日");
                break;
            case 1:
                mTvDate.setText("本周");
                break;
            case 2:
                mTvDate.setText("本月");
                break;
            case 3:
                mTvDate.setText("本年");
                break;
            case 4:
                mTvDate.setText("全部");
                break;
        }
    }

    ///获取收益数据
    private void getProfitClass() {
        mPresenter.getDistributionCount(UserManager.getInstance().getToken(),type,dateType+"");
        mPresenter.getProfitList(type,dateType+"",false);
    }

    @Override
    public void initListener() {
        super.initListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getProfitList(type,dateType+"",true);     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreProfit(type,dateType+"");       //加载更多
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

        profitList = new ArrayList<>();

        adapter = new ProfitAdapter(getActivity(), profitList, R.layout.item_new_list_income);
        mRcList.setAdapter(adapter);
        adapter.setOnClickClassLayoutListenr(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistributionBean bean = (DistributionBean) v.getTag();
                if("0".equals(bean.getIsDr())){
                    switch (bean.getType()){
                        //话题
                        case "1":
                            LiveIntroActivity.startActivity(getContext(),bean.getId());
                            break;
                        //系列课
                        case "2":
                            SeriesActivity.startActivity(getContext(),bean.getId());
                            break;
                    }
                }else {
                    ToastUtil.showShort(MyApplication.getInstance(),"该条数据已被删除，仅供浏览");
                }

            }
        });

        adapter.setOnClickClassBottomLayoutListenr(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistributionBean bean = (DistributionBean) v.getTag();
                if("0".equals(bean.getIsDr())){
                    ClassProfitDetailActivity.jumpToIncomeInfoActivity(getActivity(),bean,dateType);
                }else {
                    ToastUtil.showShort(MyApplication.getInstance(),"该条数据已被删除，仅供浏览");
                }

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
                        isChoose = true;
                        Log.d("dateType", "onValueChange: "+dateType);
                        getProfitClass();
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

    }

    @Override
    public void showProfitList(List<DistributionBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showDistributionCount(DistributionCountBean bean) {
        mTvShareCount.setText("佣金总计：");
        mTvAllCount.setText("￥"+bean.getIncomeMoney());
    }

    @Override
    public void showIncomeStatistics(DistributionCountBean bean) {}

    @Override
    public void showRefreshFinish(List<DistributionBean> datas) {
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showLoadMoreProfit(List<DistributionBean> datas) {
        mRcList.changeData(datas,adapter);
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
    public void setPresenter(DistributeContract.Presenter presenter) {
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
        if(isChoose){
            ToastUtil.showShort(MyApplication.getInstance(),"暂无数据");
            isChoose = false;
        }else {
            getBaseEmptyView().showEmptyContent();
        }
    }

    @Override
    public void showReLoad() {
        mPresenter.getProfitList(type,dateType+"",false);
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
    protected void onGetBundle(Bundle bundle) {
        super.onGetBundle(bundle);
        dateType = bundle.getInt("dateType");
    }
}
