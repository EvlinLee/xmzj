package com.gxtc.huchuan.ui.mine.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe:意见反馈
 * Created by ALing on 2017/4/7 .
 */

public class FeedBackActivity extends BaseTitleActivity implements View.OnClickListener,FeedBackContract.View {
    @BindView(R.id.et_feed_back)
    EditText mEtFeedBack;
    private FeedBackContract.Presenter mPresenter;
    private HashMap<String,String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_feed_back));
    }

    @Override
    public void initData() {
        super.initData();
        new FeedBackPersenter(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showBackButton(this);
    }

    @OnClick(R.id.btn_feed_back)
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //保存
            case R.id.btn_feed_back:
                save();
                break;
        }
    }

    private void save() {
        if (TextUtils.isEmpty(mEtFeedBack.getText().toString())){
            ToastUtil.showShort(this,getString(R.string.toast_empty_feed_back));
            return;
        }else {
            map = new HashMap<>();
            map.put("token", UserManager.getInstance().getToken());
            map.put("content",mEtFeedBack.getText().toString());
            mPresenter.feeBack(map);

        }

    }

    @Override
    public void showFeedBackResult(Object object) {
        ToastUtil.showShort(this,getString(R.string.toast_feed_back));
        this.finish();
    }

    @Override
    public void setPresenter(FeedBackContract.Presenter presenter) {
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
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this,getString(R.string.empty_net_error));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
