package com.gxtc.huchuan.ui.mine.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonIOException;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleSignAdater;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleSignBean;
import com.gxtc.huchuan.ui.mine.circle.statistic.DaysIncomeActivity;
import com.gxtc.huchuan.ui.mine.circle.statistic.StatisticActiveUserActivity;
import com.gxtc.huchuan.ui.mine.circle.statistic.StatisticPentActivity;
import com.gxtc.huchuan.ui.mine.circle.statistic.recent.RecentStatisticsActivity;
import com.gxtc.huchuan.ui.mine.circle.statistic.statisticvisitor.StatisticVisitorActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 圈子数据统计
 */
public class CircleStatisticActivity extends BaseTitleActivity implements
        CircleStatisticContract.View ,View.OnClickListener{

    @BindView(R.id.refreshlayout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)  RecyclerView       mListView;

    //private LineChart chart;
    private TextView  tvTotal;
    private TextView  tvDayMoney;
    private TextView  tvPayCount;
    private TextView  tvFreeCount;
    private TextView  tvWeekMoney;
    private TextView  tvMouthMouth;
    private TextView  tvNewUser;        //新增用户
    private TextView  tvNewPosts;       //新增帖子

    private TextView tvCircleHomeDayVisit;      //圈主页日访
    private TextView tvCircleHomeMonthVisit;    //圈主页月访
    private TextView tvCircleIntroDayVisit;     //圈介绍页日访
    private TextView tvCircleIntroMonthVisit;   //圈介绍页月访
    private TextView tvDayCommission;           //每日佣金
    private TextView tvDayActive;               //每日活跃用户
    private TextView tvMonthActive;             //每月活跃用户


    private int id;
    private String name;

    private CircleBean charData;

    private CircleSignAdater                  mAdapter;
    private CircleStatisticContract.Presenter mPresenter;
    private AlertDialog                       mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_statistic);
    }

    @Override
    public void initView() {
        name = getIntent().getStringExtra("name");
        getBaseHeadView().showTitle((TextUtils.isEmpty(name) ? "" : name ) + "统计");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View headView = View.inflate(this, R.layout.head_circle_statistics, null);
        //chart = (LineChart) headView.findViewById(R.id.linechart);
        tvTotal = (TextView) headView.findViewById(R.id.tv_total_money);
        tvDayMoney = (TextView) headView.findViewById(R.id.tv_day_money);
        tvPayCount = (TextView) headView.findViewById(R.id.tv_pay_count);
        tvFreeCount = (TextView) headView.findViewById(R.id.tv_free_count);
        tvWeekMoney = (TextView) headView.findViewById(R.id.tx_income_with_week);
        tvMouthMouth = (TextView) headView.findViewById(R.id.tx_income_with_mouth);
        tvNewUser = (TextView) headView.findViewById(R.id.tv_new_user);
        tvNewPosts = (TextView) headView.findViewById(R.id.tv_new_posts);
        headView.findViewById(R.id.btn_intro_day_visit).setOnClickListener(this);
        headView.findViewById(R.id.btn_home_month_visit).setOnClickListener(this);
        headView.findViewById(R.id.btn_intro_month_visit).setOnClickListener(this);
        headView.findViewById(R.id.btn_home_day_visit).setOnClickListener(this);
        headView.findViewById(R.id.btn_new_add_more).setOnClickListener(this);
        headView.findViewById(R.id.layout_new_add).setOnClickListener(this);
        headView.findViewById(R.id.layout_new_dynamic).setOnClickListener(this);
        headView.findViewById(R.id.layout_day_income).setOnClickListener(this);
        headView.findViewById(R.id.layout_income_with_mouth).setOnClickListener(this);
        headView.findViewById(R.id.layout_all_income).setOnClickListener(this);
        headView.findViewById(R.id.pay_layout).setOnClickListener(this);
        headView.findViewById(R.id.free_layout).setOnClickListener(this);
        headView.findViewById(R.id.layout_income_with_week).setOnClickListener(this);
        headView.findViewById(R.id.layout_income_with_mouth).setOnClickListener(this);
        headView.findViewById(R.id.active_user_layout).setOnClickListener(this);
        headView.findViewById(R.id.pent_layout).setOnClickListener(this);
        headView.findViewById(R.id.layout_active).setOnClickListener(this);

        tvCircleHomeDayVisit = headView.findViewById(R.id.tv_home_day_visit);
        tvCircleHomeMonthVisit = headView.findViewById(R.id.tv_home_month_visit);
        tvCircleIntroDayVisit = headView.findViewById(R.id.tv_intro_day_visit);
        tvCircleIntroMonthVisit = headView.findViewById(R.id.tv_intro_month_visit);
        tvDayCommission = headView.findViewById(R.id.tv_day_commission);
        tvDayActive = headView.findViewById(R.id.tv_day_active);
        tvMonthActive = headView.findViewById(R.id.tv_month_active);


        refreshLayout.setColorSchemeResources(Constant.REFRESH_COLOR);
        refreshLayout.setEnabled(false);

        mAdapter = new CircleSignAdater(this, new ArrayList<CircleSignBean>(), R.layout.item_circle_prople_statistics,true);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListView.addHeadView(headView);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void initData() {
        new CircleStatisticPresenter(this);
        hideContentView();
        id = getIntent().getIntExtra(Constant.INTENT_DATA, 0);
        mPresenter.getData(id);
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(id);
            }
        });
    }

    @Override
    public void showData(CircleBean chartData, List<CircleSignBean> usersDatas) {
        showContentView();
        this.charData = chartData;
        refreshLayout.setRefreshing(false);
        tvCircleHomeDayVisit.setText(chartData.getDayGroupHomeBrowse());
        tvCircleHomeMonthVisit.setText(chartData.getSomeDaysGroupHomeBrowse());
        tvCircleIntroDayVisit.setText(chartData.getDayGroupIntroBrowse());
        tvCircleIntroMonthVisit.setText(chartData.getSomeDaysGroupIntroBrowse());
        tvDayActive.setText(chartData.getDayActiveUser());
        tvMonthActive.setText(chartData.getLastSomeDaysActiveMember());
        tvDayCommission.setText(chartData.getSaleFee());

        tvNewUser.setText(chartData.getLastSomeDaysJoinMember());
        tvNewPosts.setText(charData.getLastSomeDaysGroupInfo());

        String total = String.format(Locale.CHINA, "%.2f", charData.getTotalEarnings());
        tvTotal.setText(total);

        String earnings = String.format(Locale.CHINA, "%.2f", charData.getEarnings());
        tvDayMoney.setText(earnings);

        String weekEarnings = String.format(Locale.CHINA, "%.2f", charData.getWeekEarnings());
        tvWeekMoney.setText(weekEarnings);

        String mouthEarnings = String.format(Locale.CHINA, "%.2f", charData.getMonthEarnings());
        tvMouthMouth.setText(mouthEarnings);

        tvPayCount.setText(charData.getCharge() + "");
        tvFreeCount.setText(charData.getFree() + "");


        refreshLayout.setVisibility(View.VISIBLE);
        mListView.notifyChangeData(usersDatas, mAdapter);
    }



    @Override
    public void showRefreshData(List<CircleSignBean> datas) {
        refreshLayout.setRefreshing(false);
        mListView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMoreData(List<CircleSignBean> datas) {
        mListView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoLoadMore() {
        mListView.loadFinish();
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(CircleStatisticActivity.this);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(CircleStatisticContract.Presenter presenter) {
        mPresenter = presenter;
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
        if (charData == null) {
            getBaseEmptyView().showEmptyContent();
        } else {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseEmptyView().hideEmptyView();
                mPresenter.getData(id);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pay_layout:
                JoinCountActivity.jumpToJoinCountActivity(this, JoinCountActivity.Companion.getINSTANT_TYPE_PAY(), id+"");
                break;
            case R.id.free_layout:
                JoinCountActivity.jumpToJoinCountActivity(this, JoinCountActivity.Companion.getINSTANT_TYPE_FREE(), id+"");
                break;

            //日收入
            case R.id.layout_day_income:
                DaysIncomeActivity.jumpToDaysIncomeActivity(this,DaysIncomeActivity.getINCOME_WITH_DAYS(),id,0,null,null,name);
                break;

            //周收入
            case R.id.layout_income_with_week:
                DaysIncomeActivity.jumpToDaysIncomeActivity(this,DaysIncomeActivity.getINCOME_WITH_WEEK(),id,0,charData.getSevenRealIncomeCount(),charData.getWeekEarnings(),name);
                break;

            //月收入
            case R.id.layout_income_with_mouth:
                DaysIncomeActivity.jumpToDaysIncomeActivity(this,DaysIncomeActivity.getINCOME_WITH_MOUTH(),id,0,charData.getThirtyRealIncomeCount(),charData.getMonthEarnings(),name);
                break;

            //每日佣金
            case R.id.pent_layout:
                StatisticPentActivity.Companion.jumpToStatisticPentActivity(this,id);
                break;

            //活跃用户
            case R.id.active_user_layout:
                StatisticActiveUserActivity.Companion.jumpToStatisticActiveUserActivity(this,id);
                break;

            //总收入
            case R.id.layout_all_income:
                DaysIncomeActivity.jumpToDaysIncomeActivity(this,DaysIncomeActivity.getINCOME_WITH_TOATAL(),id,0,null,null,name);
                break;

            //新增用户
            case R.id.layout_new_add:
                RecentStatisticsActivity.startAcitivity(this, id, 0);
                break;

            //新增动态
            case R.id.layout_new_dynamic:
                RecentStatisticsActivity.startAcitivity(this, id, 1);
                break;

            //活跃量
            case R.id.layout_active:
                RecentStatisticsActivity.startAcitivity(this, id, 2);
                break;

            //新增用户更多
            case R.id.btn_new_add_more:
                JoinCountActivity.jumpToJoinCountActivity(this, JoinCountActivity.INSTANT_TYPE_ALL, id+"");
            break;

            //日访主页
            case R.id.btn_home_day_visit:
                StatisticVisitorActivity.jumpToStatisticVisitorActivity(this,StatisticVisitorActivity.getTIME_OF_DAY(),StatisticVisitorActivity.getCIRCLE_OF_HOME(),id);
                break;

            //日访介绍页
            case R.id.btn_intro_day_visit:
                StatisticVisitorActivity.jumpToStatisticVisitorActivity(this,StatisticVisitorActivity.getTIME_OF_DAY(),StatisticVisitorActivity.getCIRCLE_OF_INTRUDUCE(),id);
                break;

            //月访主页
            case R.id.btn_home_month_visit:
                StatisticVisitorActivity.jumpToStatisticVisitorActivity(this,StatisticVisitorActivity.getTIME_OF_MOUTH(),StatisticVisitorActivity.getCIRCLE_OF_HOME(),id);
                break;

            //月访介绍页
            case R.id.btn_intro_month_visit:
                StatisticVisitorActivity.jumpToStatisticVisitorActivity(this,StatisticVisitorActivity.getTIME_OF_MOUTH(),StatisticVisitorActivity.getCIRCLE_OF_INTRUDUCE(),id);
                break;
        }
    }

    public static void jumpToCircleStatistic(Activity activity,int circleId,String circleName){
        Intent intent = new Intent(activity,CircleStatisticActivity.class);
        intent.putExtra("data",circleId);
        intent.putExtra("name",circleName);
        activity.startActivityForResult(intent,0);
    }
}
