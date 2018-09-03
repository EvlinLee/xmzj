package com.gxtc.huchuan.ui.im.redPacket;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.RpListAdapter;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.account.UsableAccountActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 红包详情页面
 */
public class RedPacketDetailedActivity extends BaseTitleActivity implements RedPacketDetailedContract.View, View.OnClickListener {

    @BindView(R.id.layout_actionbar) View         actionBar;
    @BindView(R.id.recyclerview)     RecyclerView mRecyclerView;

    private View      headView;
    private ImageView imgHead;
    private TextView  tvName;
    private TextView  tvMessage;
    private TextView  tvLabel;
    private TextView  tvMoney;
    private TextView  tvMoneyLabel;
    private TextView  tvRemainMoney;

    private String redId;
    private String loadFirstTime;
    private RedPacketBean                       bean;
    private RpListAdapter                       mAdapter;
    private RedPacketDetailedContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packet_detailed);
    }

    @Override
    public void initView() {
//        setImmerse();
        setActionBarTopPadding(actionBar, true);
        bean = (RedPacketBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        redId = getIntent().getStringExtra("id");

        headView = View.inflate(this, R.layout.head_red_packet_detailed, null);
        imgHead = (ImageView) headView.findViewById(R.id.img_head);
        tvName = (TextView) headView.findViewById(R.id.tv_name);
        tvMessage = (TextView) headView.findViewById(R.id.tv_message);
        tvLabel = (TextView) headView.findViewById(R.id.tv_label);
        tvMoney = (TextView) headView.findViewById(R.id.tv_money);
        tvMoneyLabel = (TextView) headView.findViewById(R.id.tv_money_label);
        tvRemainMoney = (TextView) headView.findViewById(R.id.tv_remain_money);

        tvLabel.setOnClickListener(this);
        loadFirstTime = String.valueOf(System.currentTimeMillis());
        mAdapter = new RpListAdapter(this, new ArrayList<RedPacketBean>(), R.layout.item_rp_detailed);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.addHeadView(headView);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

        if(bean != null){
            mAdapter.setType(bean.getAllotType());
            fillData();
        }


    }

    @OnClick({R.id.img_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;

            //去提现
            case R.id.tv_label:
                GotoUtil.goToActivity(this, UsableAccountActivity.class);
                break;
        }
    }

    @Override
    public void initListener() {
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreData();
            }
        });
    }

    @Override
    public void initData() {
        bean = (RedPacketBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        new RedPacketDetailedPresenter(this);
        if(bean == null){
            mPresenter.getRedInfo(redId);
        }else{
            mPresenter.getData(bean.getId(), loadFirstTime);
        }
    }

    private void fillData(){
        String img = bean.getUserPic();
        ImageHelper.loadRound(this,imgHead,img,2);
        tvName.setText(bean.getUserName());
        tvMessage.setText(bean.getMessage());

        //如果是自己发给别人的个人红包 则不显示金额
        String userCode = UserManager.getInstance().getUserCode();
        if(bean.getType() == 0 && userCode.equals(bean.getUserCode())){
            tvMoney.setVisibility(View.GONE);
            tvMoneyLabel.setVisibility(View.GONE);
            tvLabel.setVisibility(View.GONE);
        }else{
            tvMoney.setText(new BigDecimal(bean.getAmt()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + "");
        }

        //如果自己没领到红包而且红包也领完了，隐藏钱数
        if(bean.getIsFinish() == 1 && bean.getIsSnatch() == 0){
            tvMoney.setVisibility(View.GONE);
            tvMoneyLabel.setVisibility(View.GONE);
            tvLabel.setVisibility(View.GONE);
        }

        String temp;
        long endTime = bean.getFinishTime();
        if(endTime == 0){
            temp = bean.getTotalNum() + "个红包共" + new BigDecimal(bean.getTotalAmt()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + "元，剩余" + bean.getNum() + "个";
        }else{
            long startTime = bean.getCreateTime();
            String time = DateUtil.getDatePoor(endTime,startTime);
            temp = bean.getTotalNum()
                    + "个红包共"
                    + new BigDecimal(bean.getTotalAmt()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()
                    + "元，"
                    + time + "被抢光" ;
        }
        tvRemainMoney.setText(temp);
        if(bean.getAllotType() == 1){
            tvName.setCompoundDrawables(null,null,null,null);
        }

        mRecyclerView.setAdapter(mAdapter);
    }

    public void setActionBarTopPadding(View v, boolean changeHeight) {
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int stataHeight = getStatusBarHeight();
                if (changeHeight) {
                    v.getLayoutParams().height = v.getLayoutParams().height + stataHeight;
                }
                v.setPadding(v.getPaddingLeft(),
                        stataHeight,
                        v.getPaddingRight(),
                        v.getPaddingBottom());

            }
        }
    }

    @Override
    public void showRedInfo(RedPacketBean infoBean) {
        bean = infoBean;
        mAdapter.setType(infoBean.getAllotType());
        fillData();
        mPresenter.getData(bean.getId(),loadFirstTime);
    }

    @Override
    public void showData(List<RedPacketBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showRefreshData(List<RedPacketBean> datas) {

    }

    @Override
    public void showLoadMoreData(List<RedPacketBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoLoadMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, 0);
    }

    @Override
    public void setPresenter(RedPacketDetailedContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseEmptyView().hideEmptyView();
                mPresenter.getData(bean.getId(),loadFirstTime);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
