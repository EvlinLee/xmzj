package com.gxtc.huchuan.ui.mine.incomedetail.distribute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.NewDistributionAdapter;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Describe:课堂分销
 * Created by ALing on 2017/5/16 . ProfitContract
 */

public class DistributionFragment extends BaseTitleFragment implements DistributeContract.View,View.OnClickListener {
    @BindView(R.id.rc_list)        RecyclerView       mRcList;
    @BindView(R.id.swipe_layout)   SwipeRefreshLayout mSwipeLayout;

    private DistributeContract.Presenter mPresenter;
    private String type = "0";        //显示类型。默认0    ，0 =（话题，系列课，圈子）
    private NewDistributionAdapter adapter;
    private PopPosition            mPopPosition;
    private HashMap<String,String> map;
    private int dateType = 4;       //日期类型。 0：本日，1：本周，2：本月，3:本年，4：全部
    private boolean isChooseDateType ;
    private TextView                    mTvShareCount;
    private TextView                    mTvAllCount;
    private TextView                    mTvDate;
    private int start = 0;
    private boolean isRefresh = true;
    private AlertDialog mAlertDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_share_profit_list, container, false);
        return view;
    }


    @Override
    public void initData() {
        super.initData();
        getBaseHeadView().showTitle("佣金收益").showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        new DistributePresenter(this);
        map = new HashMap<>();
        initRecyclerView();
        setDateData();
        getDistribution(true);
    }

    //获取分销数据
    private void getDistribution(boolean isRefresh){
        if(isRefresh){
            start = 0;
            map.put("start",start+"");
            mPresenter.getDistributionIncomeSum(UserManager.getInstance().getToken(),type,dateType+"");
            showLoad();
        }else {
            start = start + 15;
            map.put("start",start+"");
        }
        map.put("token",UserManager.getInstance().getToken());
        map.put("dateType", String.valueOf(dateType));
        map.put("type",type);
        mPresenter.getIncomeStatistics(map);
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

    @Override
    public void initListener() {
        super.initListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                mRcList.reLoadFinish();
                getDistribution(true);
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isRefresh = false;
                getDistribution(false);
            }
        });

    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_share_profit_list_header,null);
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

        adapter = new NewDistributionAdapter(getActivity(), new ArrayList<DistributionBean>(), R.layout.new_item_list_income);
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
                        //圈子
                        case "3":
                            if(!TextUtils.isEmpty(bean.getId().trim())){
                                Intent intent = new Intent(getContext(), CircleMainActivity.class);
                                intent.putExtra("groupId",Integer.parseInt(bean.getId()));
                                startActivity(intent);
                            }
                            break;
                    }
                }else {
                    ToastUtil.showShort(MyApplication.getInstance(),"该条记录已被删除，仅供浏览");
                }

            }
        });
        adapter.setOnClickClassBottomLayoutListenr(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistributionBean bean = (DistributionBean) v.getTag();
                if("0".equals(bean.getIsDr())){
                    DistributeDetailActivity.jumpToIncomeInfoActivity(getActivity(),bean);
                }else {
                    ToastUtil.showShort(MyApplication.getInstance(),"该条记录已被删除，仅供浏览");
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
                        isChooseDateType = true;
                        mTvDate.setText(dateaArr[newVal]);
                        dateType = newVal/* + 1*/;
                        Log.d("dataType", "onValueChange: "+dateType);
                        getDistribution(true);
                    }
                });
            }
        }
        mPopPosition.showPopOnRootView(getActivity());
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(getActivity());
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void showDistributList(List<DistributionBean> datas) {
        getBaseLoadingView().hideLoading();
        if(isRefresh){
            mSwipeLayout.setRefreshing(false);
            mRcList.notifyChangeData(datas,adapter);
        }else {
            mRcList.changeData(datas,adapter);
        }
    }

    @Override
    public void showProfitList(List<DistributionBean> datas) {}

    @Override
    public void showDistributionCount(DistributionCountBean bean) {}

    @Override
    public void showIncomeStatistics(DistributionCountBean bean) {
        mTvShareCount.setText("共有"+bean.getCount()+"条分享记录   佣金总计：");
        mTvAllCount.setText("￥"+ StringUtil.formatMoney(2,bean.getSum()));
    }

    @Override
    public void showRefreshFinish(List<DistributionBean> datas) {
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showLoadMoreProfit(List<DistributionBean> datas) {}

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
        if(isChooseDateType){
            ToastUtil.showShort(MyApplication.getInstance(),"暂无数据");
            getBaseLoadingView().hideLoading();
            isChooseDateType = false;
        }else{
            if(start == 0){
                getBaseEmptyView().showEmptyContent();
            }else {
                mRcList.loadFinish();
            }
        }
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
    protected void onGetBundle(Bundle bundle) {
        super.onGetBundle(bundle);
        dateType = bundle.getInt("dateType");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

}
