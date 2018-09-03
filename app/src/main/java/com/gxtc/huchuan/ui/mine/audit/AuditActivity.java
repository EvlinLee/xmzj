package com.gxtc.huchuan.ui.mine.audit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AuditBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gubr on 2017/3/20.
 * <p>
 * 申请进度页面
 */

public class AuditActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.v_shape_one)         View      mVShapeOne;
    @BindView(R.id.v_shape_second)      View      mVShapeSecond;
    @BindView(R.id.iv_audit_submitting) ImageView mIvAuditSubmitting;
    @BindView(R.id.v_shape_third)       View      mVShapeThird;
    @BindView(R.id.iv_audit_submitted)  ImageView mIvAuditSubmitted;
    @BindView(R.id.tv_audit_submit)     TextView  mTvAuditSubmit;
    @BindView(R.id.tv_audit_submitting) TextView  mTvAuditSubmitting;
    @BindView(R.id.tv_audit_submitted)  TextView  mTvAuditSubmitted;
    @BindView(R.id.v_shape_fourth)      View      mVShapeFourth;
    @BindView(R.id.btn_submit)          Button    mBtnSubmit;

    CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private String apply;
    private boolean applyFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle("审核");
    }

    @Override
    public void initData() {
        super.initData();
        apply = getIntent().getStringExtra("apply");
        if (TextUtils.isEmpty(apply)) {
            return;
        }
        if ("anchor".equals(apply)) {        //申请主播进度
            AnchorAudit();
        } else {                             //申请作者
            AuthorAudit();
        }

    }

    private void AnchorAudit() {
        if (UserManager.getInstance().isLogin()) {

            mCompositeSubscription.add(LiveApi.getInstance().getAnchorInfo(
                    UserManager.getInstance().getToken()).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<AuditBean>>(new ApiCallBack<AuditBean>() {
                        @Override
                        public void onSuccess(AuditBean data) {
                            switch (data.getAudit()) {
                                case "0":
                                    submitting(data, "0");
                                    break;
                                case "1":
                                    submitSuccessful(data, "0");
                                    break;
                                case "2":
                                    submitMistake(data, "0");
                                    break;
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {

                        }
                    })));
        }
    }

    private void AuthorAudit() {
        if (UserManager.getInstance().isLogin()) {

            mCompositeSubscription.add(MineApi.getInstance().getAuthorInfo(
                    UserManager.getInstance().getToken()).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<AuditBean>>(new ApiCallBack<AuditBean>() {
                        @Override
                        public void onSuccess(AuditBean data) {
                            switch (data.getAudit()) {
                                case "0":
                                    submitting(data, "1");
                                    break;
                                case "1":
                                    submitSuccessful(data, "1");
                                    break;
                                case "2":
                                    submitMistake(data, "1");
                                    break;
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {

                        }
                    })));
        }
    }

    private void submitting(AuditBean data, String auditType) {
        mIvAuditSubmitting.setImageResource(R.drawable.audit_icon_underway_1);
        //        mTvAuditSubmitting.setText("审核中");
        mIvAuditSubmitted.setImageResource(R.drawable.audit_icon_triangle);
        mVShapeThird.setBackgroundColor(getResources().getColor(R.color.greyd1d1d1));
        mTvAuditSubmitted.setVisibility(View.INVISIBLE);
        mVShapeFourth.setVisibility(View.INVISIBLE);
        mBtnSubmit.setVisibility(View.INVISIBLE);
        EventBusUtil.post(new AuditBean(data.getAudit(), auditType));
    }

    private void submitMistake(AuditBean data, String auditType) {
        mIvAuditSubmitting.setImageResource(R.drawable.audit_icon_underway_2);
        //        mTvAuditSubmitting.setText("审核中");
        mIvAuditSubmitted.setImageResource(R.drawable.audit_icon_mistake);
        mVShapeThird.setBackgroundColor(getResources().getColor(R.color.greyd1d1d1));
        mTvAuditSubmitted.setVisibility(View.VISIBLE);
        mTvAuditSubmitted.setText("审核失败");
        mVShapeFourth.setVisibility(View.VISIBLE);
        mBtnSubmit.setVisibility(View.VISIBLE);
        EventBusUtil.post(new AuditBean(data.getAudit(), auditType));
    }

    private void submitSuccessful(AuditBean data, String auditType) {
        applyFlag = true;
        mIvAuditSubmitting.setImageResource(R.drawable.audit_icon_underway_2);
        //        mTvAuditSubmitting.setText("审核中");
        mIvAuditSubmitted.setImageResource(R.drawable.audit_icon_pass);
        mVShapeThird.setBackgroundColor(getResources().getColor(R.color.btn_nornal));
        mTvAuditSubmitted.setVisibility(View.VISIBLE);
        mTvAuditSubmitted.setText("审核通过");
        mVShapeFourth.setVisibility(View.INVISIBLE);
        mBtnSubmit.setVisibility(View.VISIBLE);
        if ("0".equals(auditType)) {
            mBtnSubmit.setText("我的直播间");
        } else {
            mBtnSubmit.setText("发布文章");
        }
        EventBusUtil.post(new AuditBean(data.getAudit(), auditType));

    }

    @OnClick(R.id.btn_submit)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.btn_submit:
                //这里重新申请
                reApply();
                break;
        }

    }

    private void reApply() {
        //主播
        if ("anchor".equals(apply)) {
            if (applyFlag) {
                String chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
                LiveHostPageActivity.startActivity(this,"1", chatRoomId);
            } else {
                AnchorAudit();
            }

        } else {
            if (applyFlag) {

                GotoUtil.goToActivity(this, ArticleResolveActivity.class);
            } else {
                AuthorAudit();
            }
        }
    }


    @Override
    protected void onDestroy() {
        mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }

    public static void startActivity(Context activity, String applay) {
        Intent intent = new Intent(activity, AuditActivity.class);
        intent.putExtra("apply", applay);
        activity.startActivity(intent);

    }
}
