package com.gxtc.huchuan.ui.circle.homePage;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.UMShareUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子公告详情
 */
public class CircleNoticeActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)   TextView     mTvTitle;
    @BindView(R.id.tv_name)    TextView     mTvName;
    @BindView(R.id.tv_content) TextView     mTvContent;
    @BindView(R.id.root_view)  LinearLayout mRootView;

    private int id;
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_notice);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("公告详情");
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }


    @Override
    public void initData() {
        id = getIntent().getIntExtra(Constant.INTENT_DATA, 0);

        getBaseLoadingView().showLoading();
        Subscription sub = CircleApi.getInstance().getNoticeDetailed(id).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(mTvTitle == null)    return;
                        getBaseLoadingView().hideLoading();
                        CircleBean bean = (CircleBean) data;

                        mTvTitle.setText(bean.getTitle());

                        String name = bean.getUserName();
                        String time = DateUtil.showTimeAgo(bean.getCreateTime()+"");

                        String temp = name + "   " + time;
                        mTvName.setText(temp);

                        content = bean.getContext();
                        mTvContent.setText(bean.getContext());

                        mRootView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(getBaseEmptyView() == null) return;
                        getBaseEmptyView().showEmptyContent(message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
