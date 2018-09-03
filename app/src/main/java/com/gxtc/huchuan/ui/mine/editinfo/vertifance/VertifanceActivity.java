package com.gxtc.huchuan.ui.mine.editinfo.vertifance;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.pay.AccountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.ui.live.apply.ApplyLecturerActivity;
import com.gxtc.huchuan.ui.mine.editinfo.BindEPayActivity;
import com.gxtc.huchuan.ui.mine.editinfo.EditCardNumberActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RegexUtils;
import com.gxtc.huchuan.utils.StringUtil;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

//实名认证
public class VertifanceActivity extends BaseTitleActivity implements View.OnClickListener,
        VertifanceContract.View, PictureConfig.OnSelectResultCallback {

    private static final String TAG = VertifanceActivity.class.getSimpleName();
    @BindView(R.id.tv_nikename)
    EditText tvNikename;
    @BindView(R.id.rl_nikename)
    RelativeLayout rlNikename;
    @BindView(R.id.rl_number)
    RelativeLayout rlNumber;
    @BindView(R.id.tv_number)
    EditText tvNumber;
    @BindView(R.id.rl_pic)
    RelativeLayout rlPic;
    @BindView(R.id.normal)
    ImageView ivNormal;
    @BindView(R.id.back)
    ImageView ivBack;
    @BindView(R.id.hold_card)
    ImageView ivHoldCard;
    @BindView(R.id.rl_area)
    RelativeLayout rlEpay;
    @BindView(R.id.epay_number)
    TextView ePayNumber;
    @BindView(R.id.rl_bind_card)
    RelativeLayout rlBindCard;
    @BindView(R.id.card_number)
    TextView tvBindCardNumber;
    @BindView(R.id.btn_commit)
    TextView tvBtnCommit;
    @BindView(R.id.tv_tell)
    TextView tvTell;      //注册手机号
    @BindView(R.id.tv_tellisweixin)
    CheckBox cbTellWx;   //微信和手机是否同号
    @BindView(R.id.weixin_account)
    EditText etWx;         //微信号

    public static final int REQUEST_CODE_AVATAR = 10000;
    public static final int REQUEST_CODE_IDCARD = 20000;

    private boolean isLogin;
    private Subscription sub;
    private String mIdCareUrl;      //身份证链接
    private String mIdCardUrlPos;   //正面链接
    private String mIdCardUrlOutSide;   //反面链接
    private String mHoldIdCardUrl;   //手持身份证链接
    private int mIDDirection;       //身份证正反面  0 表示正面，1 表示反面
    private VertifanceContract.Presenter mPresenter;
    private String mIdCard;
    private AlertDialog mAlertDialog;
    public static final int BIND_EPAY_CODE = 1000;
    public static final int BIND_CARD_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertifice_info);
    }


    @Override
    public void initData() {
        super.initData();
//        EventBusUtil.register(this);
        isLogin = UserManager.getInstance().isLogin();
        new VertifancePrenster(this);
        tvTell.setText(UserManager.getInstance().getPhone());
        VertifanceCard();
        VertifanceEpay();
    }

    //获取绑定的支付宝   账户类型。1：微信；2：支付宝；3：银行卡
    private void VertifanceEpay() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", "2");
        mPresenter.showVertifanceEpay(map);
    }

    //获取绑定的银行卡
    private void VertifanceCard() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", "3");
        mPresenter.showVertifanceCard(map);
    }


    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showTitle(getString(R.string.title_vertifive_info));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cbTellWx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbTellWx.setText("是");
                    etWx.setText(UserManager.getInstance().getPhone());
                    etWx.setEnabled(false);
                } else {
                    cbTellWx.setText("否");
                    etWx.setText("");
                    etWx.setEnabled(true);
                }
            }
        });
    }

    @OnClick({R.id.rl_nikename, R.id.rl_number, R.id.normal, R.id.back, R.id.rl_area, R.id.rl_bind_card, R.id.btn_commit, R.id.hold_card})
    public void onClick(View view) {
        switch (view.getId()) {
            //真实姓名
            case R.id.rl_nikename:
                break;
            //身份证号
            case R.id.rl_number:
                break;
            //正面
            case R.id.normal:
                mIDDirection = 0;
                chooseImg();
                break;
            //反面
            case R.id.back:
                mIDDirection = 1;
                chooseImg();
                break;
            //手持身份证
            case R.id.hold_card:
                mIDDirection = 2;
                chooseImg();
                break;
            //支付宝
            case R.id.rl_area:
                GotoUtil.goToActivityForResult(this, BindEPayActivity.class, BIND_EPAY_CODE);
                break;
            //银行卡
            case R.id.rl_bind_card:
                GotoUtil.goToActivityForResult(this, EditCardNumberActivity.class, BIND_CARD_CODE);
                break;
            case R.id.btn_commit:
                if ("0".equals(UserManager.getInstance().getIsRealAudit()) || "3".equals(
                        UserManager.getInstance().getIsRealAudit())) {
                    //未认证
                    commitData();
                } else if ("1".equals(UserManager.getInstance().getIsRealAudit())) {
                    ToastUtil.showShort(this, "审核通过，无需重新提交审核");
                } else {
                    ToastUtil.showShort(this, "已在在审核中，无需重新提交");
                }
                break;
        }

    }

    private void commitData() {
        String token = null;
        if (!UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        } else {
            token = UserManager.getInstance().getToken();
        }
        if (TextUtils.isEmpty(tvNikename.getText().toString())) {
            ToastUtil.showShort(this, "名字不能为空");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(tvNumber.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_idcard_cannot_empty));
            return;
        } else {
            mIdCard = tvNumber.getText().toString();
        }
        if (!RegexUtils.isIDCard18(tvNumber.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_idcard_not_right));
            return;
        }

        if (TextUtils.isEmpty(mIdCardUrlPos)) {
            ToastUtil.showShort(this, getString(R.string.toast_idcard_pos_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(mIdCardUrlOutSide)) {
            ToastUtil.showShort(this, getString(R.string.toast_idcard_outside_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(mHoldIdCardUrl)) {
            ToastUtil.showShort(this, getString(R.string.toast_idcard_hold_cannot_empty));
            return;
        }
        String tell = UserManager.getInstance().getPhone(); //手机号码
        String Wx = etWx.getText().toString();             //微信号
        if (tell == null || tell.equals("")) {
            ToastUtil.showShort(this, getString(R.string.count_canot_empty));
            return;
        }

        if (Wx == null || Wx.equals("")) {
            ToastUtil.showShort(this, getString(R.string.toast_wx_cannot_empty));
            return;
        }

        // phone  手机号
        //wxChat  微信号

        map.put("token", token);
        map.put("name", tvNikename.getText().toString());
        map.put("idNo", mIdCard);
        map.put("phone", tell);
        map.put("wxChat", Wx);
        map.put("idPic1", mIdCardUrlPos);
        map.put("idPic2", mIdCardUrlOutSide);
        map.put("idPic3", mHoldIdCardUrl);
        mPresenter.Vertifance(map);
    }

    //选择照片
    private void chooseImg() {

        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers,
                ApplyLecturerActivity.REQUEST_CODE_IDCARD, new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options =
                                new FunctionOptions.Builder()
                                        .setType(FunctionConfig.TYPE_IMAGE)
                                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                                        .setImageSpanCount(3)
                                        .setEnableQualityCompress(false) //是否启质量压缩
                                        .setEnablePixelCompress(false) //是否启用像素压缩
                                        .setEnablePreview(true) // 是否打开预览选项
                                        .setShowCamera(true)
                                        .setPreviewVideo(true)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(VertifanceActivity.this, VertifanceActivity.this);

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(VertifanceActivity.this, false,
                                null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    VertifanceActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
        mPresenter.destroy();
    }

    @Override
    public void showVertifanceResule(Object object) {
        mAlertDialog = DialogUtil.showDeportDialog(VertifanceActivity.this, true, "提交成功",
                getResources().getString(R.string.title_notice_message),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = UserManager.getInstance().getUser();
                        user.setIsRealAudit("2");
                        UserManager.getInstance().updataUser(user);
                        Intent intent = new Intent();
                        intent.putExtra("status", "2");
                        setResult(RESULT_OK, intent);
                        mAlertDialog.dismiss();
                        finish();
                    }
                });
    }

    @Override
    public void showCompressSuccess(File file) {
        mPresenter.uploadingFile(file);
    }

    @Override
    public void showCompressFailure() {
        ToastUtil.showShort(this, "压缩图片失败");
    }

    @Override
    public void showWatermarkImage(Bitmap watermarkBitmap) {
        //身份证正面
        if (mIDDirection == 0) {
            ivNormal.setImageBitmap(watermarkBitmap);
            //身份正反面
        } else if (mIDDirection == 1) {
            ivBack.setImageBitmap(watermarkBitmap);
            //手持身份证
        } else if (mIDDirection == 2) {
            ivHoldCard.setImageBitmap(watermarkBitmap);
        }
    }

    @Override
    public void showUploadingSuccess(String url) {
        if (mIDDirection == 0) {     //身份证正面
            mIdCardUrlPos = url;
            //ImageHelper.loadImage(VertifanceActivity.this, ivNormal, url);
        } else if (mIDDirection == 1) {
            mIdCardUrlOutSide = url;       //身份正反面
            //ImageHelper.loadImage(VertifanceActivity.this, ivBack, url);
        } else if (mIDDirection == 2) {
            mHoldIdCardUrl = url;       //手持身份证
            //ImageHelper.loadImage(VertifanceActivity.this, ivHoldCard, url);
        }

    }

    @Override
    public void showVertifanceCardSuccess(Object object) {
        List<AccountBean> lists = (List<AccountBean>) object;
        if (lists != null && lists.size() > 0) {
            tvBindCardNumber.setText(((List<AccountBean>) object).get(0).getUserAccount());
        }
    }

    @Override
    public void showVertifanceEpaySuccess(Object object) {
        List<AccountBean> lists = (List<AccountBean>) object;
        if (object != null && lists.size() > 0) {
            ePayNumber.setText(((List<AccountBean>) object).get(0).getUserAccount());
        }
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
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
        getBaseEmptyView().showEmptyView();
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
    public void setPresenter(VertifanceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        mPresenter.compressImg(media.getPath());
    }
}
