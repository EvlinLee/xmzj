package com.gxtc.huchuan.ui.mine.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventCircleMemberShipBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/5/22.
 * 成员加入方式
 */

public class MembershipApproachActivity extends BaseTitleActivity {


    @BindView(R.id.cb_menber_free)
    CheckBox cbMenberFree;
    @BindView(R.id.cb_menber_nofree)
    CheckBox cbMenberNofree;

    Subscription subChange;
    @BindView(R.id.tv_member_app_money)
    TextView tvMemberAppMoney;
    private int circleId;
    private int isFree;        //是否收费。0：免费、1：收费
    private double fee;//费用
    private int pent;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menber_ship_approach);
    }


    @Override
    public void initData() {
        Intent intent = getIntent();
        circleId = intent.getIntExtra("circle_id", -1);
        isFree = intent.getIntExtra("isFree", -1);
        fee = intent.getDoubleExtra("fee", -1);
        pent = intent.getIntExtra("pent", -1);

        builder = new AlertDialog.Builder(this);

        if (0 == isFree) {
            cbMenberFree.setChecked(true);
            cbMenberNofree.setChecked(false);
        }
        if (1 == isFree) {
            cbMenberFree.setChecked(false);
            cbMenberNofree.setChecked(true);
            tvMemberAppMoney.setText("¥ " + fee + " " + "分成: " + pent + "%");

        }
        cbMenberFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbMenberNofree.setChecked(false);
                    tvMemberAppMoney.setVisibility(View.GONE);
                }
            }
        });

        cbMenberNofree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if("1".equals(UserManager.getInstance().getUser().getIsRealAudit())){
                        showFreeChangeDialog();
                        cbMenberFree.setChecked(false);
                    }else {
                        cbMenberNofree.setChecked(false);
//                        cbMenberNofree.setClickable(false);
                        ToastUtil.showShort(MembershipApproachActivity.this,"请先实名认证，才可以修改为收费的圈子");
                    }
                }
            }
        });

        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MembershipApproachActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_member));
        getBaseHeadView().showHeadRightButton("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }


    TextView rightButton;
    AlertDialog dialog;
    EditText etFree;
    EditText etFen;

    /**
     * 展示收费标准
     */
    private void showFreeChangeDialog() {
        dialog = new AlertDialog.Builder(this).create();
        //自定义布局
        View view = this.getLayoutInflater().inflate(R.layout.dialog_circle_setting, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);


        etFree = (EditText) view.findViewById(R.id.et_circle_setting_free);
        etFen = (EditText) view.findViewById(R.id.et_circle_setting_fencheng);


        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_cancel);
        //确定
        rightButton = (TextView) view.findViewById(R.id.tv_confirm);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etFen.getText().toString().trim())){
                    if (Float.valueOf(etFen.getText().toString()) > 70 || Float.valueOf(etFen.getText().toString()) < 5) {
                        ToastUtil.showShort(MembershipApproachActivity.this, "分成需在5-70之间");
                        return;
                    }
                }
                if (TextUtils.isEmpty(etFree.getText().toString().trim())) {
                    ToastUtil.showShort(MembershipApproachActivity.this, "请填写收费标准");
                    return;
                }
                if (Float.valueOf(etFree.getText().toString()) <= 0) {
                    ToastUtil.showShort(MembershipApproachActivity.this, "圈子收费标准需大于1");
                    return;
                }
                tvMemberAppMoney.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(etFen.getText().toString())){
                    etFen.setText("0");
                }
                tvMemberAppMoney.setText("¥ " + etFree.getText().toString() + " " + "分成: " + etFen.getText().toString() + "%");
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    AlertDialog sureDialog;

    /**
     * 最后确认对话框
     */
    private void showDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("您关于圈子的收费设置将会更改，是否确定？");


        //监听下方button点击事件
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cbMenberFree.isChecked()) {
                    changeFee("0", "0", "0");
                    EventBusUtil.post(new EventCircleMemberShipBean(0, "0", "0"));
                    sureDialog.dismiss();
                    MembershipApproachActivity.this.finish();
                } else if (cbMenberNofree.isChecked()) {
                    if(etFree == null)  return;
                    if (String.valueOf(fee).equals(etFree.getText().toString())
                            && String.valueOf(pent).equals(etFen.getText().toString())) {
                        sureDialog.dismiss();
                        MembershipApproachActivity.this.finish();
                    } else {
                        changeFee("1", etFree.getText().toString(), etFen.getText().toString());
                        EventBusUtil.post(new EventCircleMemberShipBean(1, etFree.getText().toString(),
                                etFen.getText().toString()));
                        sureDialog.dismiss();
                        MembershipApproachActivity.this.finish();
                    }
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sureDialog.dismiss();
            }
        });

        //设置对话框是可取消的
        builder.setCancelable(true);
        sureDialog = builder.create();
        sureDialog.show();
    }

    /**
     * 修改费用
     *
     * @param feeType 0免费 1收费
     * @param fee
     * @param pent
     */
    private void changeFee(final String feeType, String fee, String pent) {
        if (UserManager.getInstance().isLogin()) {
            subChange = CircleApi.getInstance().changeFee(UserManager.getInstance().getToken(), circleId, feeType, fee, pent)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            ToastUtil.showShort(MembershipApproachActivity.this, "修改成功");
                            MembershipApproachActivity.this.finish();
                            sureDialog.dismiss();
                        }

                        @Override
                        public void onError(String errorCode, String message) {

                            ToastUtil.showShort(MembershipApproachActivity.this,
                                    message );
                            sureDialog.dismiss();
                        }
                    }));
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subChange != null && subChange.isUnsubscribed()) {
            subChange.unsubscribe();
        }
    }
}
