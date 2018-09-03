package com.gxtc.huchuan.ui.common.reprot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe:举报
 */

public class ReportActivity extends BaseTitleActivity implements View.OnClickListener,
        ReportContract.View {

    @BindView(R.id.et_report) EditText mEtReport;

    private ReportContract.Presenter mPresenter;
    private String                   mType;
    private String                   mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_report));
    }

    @Override
    public void initData() {
        mType = getIntent().getStringExtra("type");
        mId = getIntent().getStringExtra("id");
        new ReportPersenter(this);
    }

    @Override
    public void initListener() {
        getBaseHeadView().showBackButton(this);
    }

    @OnClick(R.id.btn_report)
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //保存
            case R.id.btn_report:
                save();
                break;
        }
    }

    private void save() {
        if (TextUtils.isEmpty(mEtReport.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_empty_report));
            return;
        } else {
            mPresenter.report(mEtReport.getText().toString(), mType, mId);
        }

    }

    @Override
    public void showReportResult(Object object) {
        ToastUtil.showShort(this, getString(R.string.toast_report));
        this.finish();
    }

    @Override
    public void setPresenter(ReportContract.Presenter presenter) {
        this.mPresenter = presenter;
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
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    public static void startActivity(Context context, @NonNull String type, @NonNull String id) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }
}
