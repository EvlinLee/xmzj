package com.gxtc.huchuan.ui.circle.circleInfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.RefundsListDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 账号与安全
 * Created by zzg on 2017/7/3.
 */
//privacy
public class ApplyRefundsActivity extends BaseTitleActivity {

    @BindView(R.id.pic_head)
    ImageView ivPicHead;
    @BindView(R.id.username)
    TextView tvUserName;
    @BindView(R.id.refunds_num)
    TextView tvRefundsNum;
    @BindView(R.id.refunds_cause)
    TextView tvRefundsCause;
    @BindView(R.id.tv_edit_explain)
    EditText edEcplain;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    private CircleBean bean;
    private ChatInfosBean mChatInfosBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_refunds_layout);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("申请退款");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        bean = (CircleBean) getIntent().getSerializableExtra("data");
        mChatInfosBean = (ChatInfosBean) getIntent().getSerializableExtra("chatInfo");
        if (bean != null) {
            //圈子申请退款
            tvUserName.setText(bean.getName());
            tvRefundsNum.setText(bean.getFee() + "");
            ImageHelper.loadRound(this, ivPicHead, bean.getCover(), 2);
        } else {
            //课程申请退款
            tvUserName.setText(mChatInfosBean.getChatRoomName());
            tvRefundsNum.setText(mChatInfosBean.getFee() + "");
            ImageHelper.loadRound(this, ivPicHead, mChatInfosBean.getChatRoomHeadPic(), 2);
        }
    }

    RefundsListDialog mRefundsListDialog;

    @OnClick({R.id.refunds_cause, R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            //选择退款原因
            case R.id.refunds_cause:
                showDialog();
                break;
            //申请退款
            case R.id.tv_sure:
                applyRefunds();
                break;
        }
    }

    /**
     * 退款类型。
     * 0: 交易退款，
     * 1，课堂退款，
     * 2，圈子退款，
     * 3，商城退款
     */
    Subscription sub;

    void applyRefunds() {
        if (TextUtils.isEmpty(selectText)) {
            ToastUtil.showShort(this, "请选择退款原因");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        if (bean != null) {
            map.put("orderId", bean.getId() + "");//交易订单编号、圈子ID、课堂ID
            map.put("type", "2");//圈子
        } else {
            map.put("orderId", mChatInfosBean.getId());
            map.put("type", "1");//课堂
        }
        if (TextUtils.isEmpty(selectText)) {
            ToastUtil.showShort(this, "退款原因不能为空");
            return;
        }
        map.put("reason", selectText);

        if (!TextUtils.isEmpty(edEcplain.getText().toString())) {
            map.put("remark", edEcplain.getText().toString());
        }
        sub = DealApi.getInstance().saveUserRefund(map).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(ApplyRefundsActivity.this, "申请成功");
                        finish();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(ApplyRefundsActivity.this, message);
                    }
                }));
    }

    String selectText = "";

    private void showDialog() {
        if (mRefundsListDialog == null) {
            if (bean != null)//，，，
                mRefundsListDialog = new RefundsListDialog(this, new String[]{"圈子没有价值", "圈子内容低俗", "圈子不作为", "圈子太吵闹没规矩", "圈子广告太多"});
            else
                mRefundsListDialog = new RefundsListDialog(this, new String[]{"课堂没开课", "讲师水平差", "内容没价值", "课堂下架"});
        }
        mRefundsListDialog.setmOnItemClickListener(new RefundsListDialog.OnItemClickListener() {
            @Override
            public void selectByPosition(int position, String text) {
                selectText = text;
            }
        });
        mRefundsListDialog.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRefundsCause.setText(selectText);
            }
        });
        mRefundsListDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
