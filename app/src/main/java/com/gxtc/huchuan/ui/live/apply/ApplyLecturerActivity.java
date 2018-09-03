package com.gxtc.huchuan.ui.live.apply;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ApplyInfoBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.mine.audit.AuditActivity;
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RegexUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.gxtc.huchuan.R.id.iv_outside_idcard;

/**
 * Created by Gubr on 2017/3/7.
 * <p>
 * 申请成为主播
 */

public class ApplyLecturerActivity extends BaseTitleActivity implements View.OnClickListener,
        ApplyLectureContract.View, PictureConfig.OnSelectResultCallback {

    private static final String TAG                 = "ApplyLecturerActivity";
    public static final  int    REQUEST_CODE_IDCARD = 20000;

    @BindView(R.id.et_email)           EditText  mEtEmail;
    @BindView(R.id.et_idcard)          EditText  mEtIdcard;
    @BindView(R.id.iv_positive_idcard) ImageView mIvPositiveIdcard;
    @BindView(iv_outside_idcard)       ImageView mIvOutsideIdcard;

    @BindView(R.id.et_name)       EditText     mEtName;
    @BindView(R.id.et_weixin)     EditText     mEtWeixin;
    @BindView(R.id.tv_education)  TextView     mTvEducation;
    @BindView(R.id.tv_occupation) TextView     mTvOccupation;
    @BindView(R.id.et_phoneNum)   EditText     mEtphoneNum;
    @BindView(R.id.ll_et_area)    LinearLayout mLlEtArea;
    @BindView(R.id.btn_submit)    Button       mBtnSubmit;

    private ApplyLectureContract.Presenter mPresenter;
    private PopPosition                    mPopEducation;//选择学历
    private PopPosition                    mPopOccupation;//选择工作
    private String                         mIdCardUrlPos;   //正面链接
    private String                         mIdCardUrlOutSide;   //反面链接
    private int                            mIDDirection;       //身份证正反面  0 表示正面，1 表示反面
    private String                         mEmail;
    private String                         mIdCard;
    Subscription sub;
    private ApplyInfoBean infoBean;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applylecturer);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(getString(R.string.mine_application_lecturer));
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        super.initData();
        new ApplyLecturePresenter(this);
        if ("0".equals(UserManager.getInstance().getIsRealAudit())) {
            showDialog();

        } else if ("2".equals(UserManager.getInstance().getIsRealAudit())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setCancelable(false);
            dialog.setMessage("您已经提交实名认证申请还在审核中,实名审核通过后才能申请讲师！");
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApplyLecturerActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }
        if ("2".equals(UserManager.getInstance().getIsAnchor())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setCancelable(false);
            dialog.setMessage("您已经提交讲师申请，还在审核中！");
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApplyLecturerActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }
        getApplyInfo();
    }

    private void showDialog() {
        DialogUtil.VerificationDialog(this, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_issue) {
                    GotoUtil.goToActivityForResult(ApplyLecturerActivity.this,
                            VertifanceActivity.class, 100);
                    ApplyLecturerActivity.this.finish();
                } else {
                    ApplyLecturerActivity.this.finish();
                    ToastUtil.showShort(ApplyLecturerActivity.this, "没有实名认证不能申请讲师！");
                }
            }
        });
    }

    private void getApplyInfo() {
        sub = MineApi.getInstance().ApplyInfo(UserManager.getInstance().getToken()).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<ApplyInfoBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        infoBean = (ApplyInfoBean) data;
                        if (infoBean != null) {

                            mEtName.setText(infoBean.getRealName());
                            mEtWeixin.setText(infoBean.getWechat());
                            mEtphoneNum.setText(infoBean.getSkill());
                            mEtEmail.setText(infoBean.getEmail());
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(ApplyLecturerActivity.this, message);
                    }
                }));
    }

    private void submit() {
        String token = null;
        if (!UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        } else {
            token = UserManager.getInstance().getToken();
        }
        if (TextUtils.isEmpty(mEtName.getText().toString())) {
            ToastUtil.showShort(this, "名字不能为空");
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        if (!TextUtils.isEmpty(mEtWeixin.getText().toString())) {
            map.put("wechat", mEtWeixin.getText().toString());
        }

        if (!TextUtils.isEmpty(mEtphoneNum.getText().toString())) {
            map.put("skill", mEtphoneNum.getText().toString());
        }
        if (TextUtils.isEmpty(mEtphoneNum.getText().toString())) {
            ToastUtil.showShort(this, "手机号码不能为空！");
            return;
        }
        if (!RegexUtils.isMobileExact(mEtphoneNum.getText().toString())) {
            ToastUtil.showShort(this, "手机号格式不正确！");
            return;
        }


        if (TextUtils.isEmpty(mEtEmail.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_email_cannot_empty));
            return;
        } else {
            mEmail = mEtEmail.getText().toString();

        }
        if (!RegexUtils.isEmail(mEtEmail.getText().toString())) {
            Log.d(TAG, "submit: " + RegexUtils.isEmail(mEtEmail.getText().toString()));
            ToastUtil.showShort(this, getString(R.string.toast_email_not_right));
            return;
        }
        map.put("token", token);
        map.put("name", mEtName.getText().toString());
        map.put("email", mEmail);
        mPresenter.applayAuthor(map);
    }


    /**
     * 选择直播类型
     */
    private void showPositionPopOfEducation() {
        if (mPopEducation == null) {

            mPopEducation = new PopPosition(this, R.layout.pop_ts_position);
            final String[] stringArray = getResources().getStringArray(R.array.education);
            mPopEducation.setData(stringArray);
            mPopEducation.setTitle("选择分类");
            mPopEducation.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    mTvEducation.setText(stringArray[newVal]);
                }
            });
        }
        mPopEducation.showPopOnRootView(this);
    }


    /**
     * 选择直播类型
     */
    private void showPositionPopOfOccupation() {
        if (mPopOccupation == null) {
            mPopOccupation = new PopPosition(this, R.layout.pop_ts_position);
            final String[] stringArray = getResources().getStringArray(R.array.occupation);
            mPopOccupation.setData(stringArray);
            mPopOccupation.setTitle("选择分类");
            mPopOccupation.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    mTvOccupation.setText(stringArray[newVal]);
                }
            });
        }
        mPopOccupation.showPopOnRootView(this);
    }

    @OnClick({R.id.tv_education, R.id.tv_occupation, R.id.btn_submit, R.id.iv_positive_idcard, R.id.iv_outside_idcard,R.id.btn_speaker_protocol})
    public void onClick(View view) {
        WindowUtil.closeInputMethod(this);
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.tv_education:
                showPositionPopOfEducation();
                break;
            case R.id.tv_occupation:
                showPositionPopOfOccupation();
                break;
            //上传身份证正面
            case R.id.iv_positive_idcard:
                mIDDirection = 0;
                chooseImg();
                break;
            //上传身份证反面
            case iv_outside_idcard:
                mIDDirection = 1;
                chooseImg();
                break;
            case R.id.btn_submit:
                submit();
                break;
            //申请讲师协议
            case R.id.btn_speaker_protocol:
                goToProtocol();
                break;
        }
    }
    private void goToProtocol() {
        String url = "https://apps.xinmei6.com/publish/classroomRule.html";
        CommonWebViewActivity.startActivity(this,url,"");
    }
    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和存储权限", pers, REQUEST_CODE_IDCARD,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options =
                                new FunctionOptions.Builder()
                                        .setType(FunctionConfig.TYPE_IMAGE)
                                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                                        .setImageSpanCount(3)
                                        .setEnableQualityCompress(false)  //是否启质量压缩
                                        .setEnablePixelCompress(false)    //是否启用像素压缩
                                        .setEnablePreview(true)           // 是否打开预览选项
                                        .setShowCamera(true)
                                        .setPreviewVideo(true)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(ApplyLecturerActivity.this, ApplyLecturerActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(ApplyLecturerActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(ApplyLecturerActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    // 申请成功进入审核页面
    @Override
    public void showApplayResule(Object object) {
        User user = UserManager.getInstance().getUser();
        user.setIsAnchor("1");
        UserManager.getInstance().updataUser(user);
        EventBusUtil.post(new EventEditInfoBean(EventEditInfoBean.ISANCHOR));
        finish();
        AuditActivity.startActivity(this, "anchor");
    }

    @Override
    public void showCompressSuccess(File file) {
        mPresenter.uploadingFile(file);
    }

    @Override
    public void showCompressFailure() {
        ToastUtil.showShort(this, "压缩图片失败");
    }

    //上传图片成功
    @Override
    public void showUploadingSuccess(String url) {
        if (mIDDirection == 0) {     //身份证正面
            mIdCardUrlPos = url;
            ImageHelper.loadImage(ApplyLecturerActivity.this, mIvPositiveIdcard, url);
        } else {
            mIdCardUrlOutSide = url;       //身份正反面
            ImageHelper.loadImage(ApplyLecturerActivity.this, mIvOutsideIdcard, url);
        }
    }

    @Override
    public void setPresenter(ApplyLectureContract.Presenter presenter) {
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mAlertDialog = null;
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        mPresenter.compressImg(media.getPath());
    }
}
