package com.gxtc.huchuan.ui.live.intro;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.live.intro.introresolve.IntroResolveActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.seriesclassify.SeriesClassifyActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RegexUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.jsoup.Jsoup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;


/**
 * Created by Gubr on 2017/3/22.
 */

public class LiveIntroSettingActivity extends BaseTitleActivity implements View.OnClickListener,
        TimePickerView.OnTimeSelectListener, PictureConfig.OnSelectResultCallback {
    private static final String TAG                       = "LiveIntroSettingActivit";
    private static final int    SETTING_INTRO_REQUESTCODE = 1 << 5;
    private static       int    REQUEST_CODE_IMAGE        = 10000;

    @BindView(R.id.iv_live_intro_setting_head)              ImageView      mIvLiveIntroSettingIcon;
    @BindView(R.id.tv_live_intro_setting_subtitle)          TextView       mTvLiveIntroSettingSubtitle;
    @BindView(R.id.tv_live_intro_setting_intro_starttime)   TextView       mTvLiveIntroSettingIntroStarttime;
    @BindView(R.id.tv_live_intro_setting_mainspeaker_name)  TextView       mTvLiveIntroSettingMainspeakerName;
    @BindView(R.id.tv_live_intro_setting_mainspeaker_intro) TextView       mTvLiveIntroSettingMainspeakerIntro;
    @BindView(R.id.tv_live_intro_setting_live_intro)        TextView       mTvLiveIntroSettingLiveIntro;
    @BindView(R.id.cb_live_intro_setting_sharelist)         CheckBox       mCbLiveIntroSettingSharelist;
    @BindView(R.id.cb_live_intro_setting_singup)            CheckBox       mCbLiveIntroSettingSingup;
    @BindView(R.id.cb_live_intro_setting_intro)             CheckBox       mCbLiveIntroSettingIntro;
    @BindView(R.id.tv_live_intro_setting_intro_price)       TextView       mTvLiveIntroSettingIntroPrice;
    @BindView(R.id.rl_live_intro_setting_intro_price)       RelativeLayout mRlLiveIntroSettingIntroPrice;
    @BindView(R.id.tv_live_intro_setting_intro_password)    TextView       mTvLiveIntroSettingIntroPassword;
    @BindView(R.id.rl_live_intro_setting_intro_password)    RelativeLayout mRlLiveIntroSettingIntroPassword;
    @BindView(R.id.tv_live_intro_setting_series)            TextView       mTvLiveIntroSettingSeries;

    CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    @BindView(R.id.tv_divided_proportion)       TextView       mTvDividedProportion;                      //分成比例
    @BindView(R.id.rl_divided_proportion)       RelativeLayout mRlDividedProportion;


    private ChatInfosBean mBean;


    private String intro;
    private String                   newFacePricPath;
    private TimePickerView           timePickerView;
    private Date                     mDate;
    private SimpleDateFormat         sdf;
    private String                   time;
    private Subscription             uploadSub;
    private String                   mNewFileUrl;
    private PopPosition              mPopPosition;
    private List<ChooseClassifyBean> mSeriesTypes;
    private String[]                 series;
    private String                   seriesId;
    private long                     mStartTime;
    private AlertDialog              mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_intro_setting);
    }


    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle("设置信息");
        getBaseHeadView().showHeadRightButton("确定", this);
    }

    @Override
    public void initData() {
        mBean = (ChatInfosBean) getIntent().getSerializableExtra("bean");
        intro = mBean.getDesc();
        seriesId = mBean.getChatSeries();
        getTypeData();
        setData();
    }


    private void setData() {
        if (mBean != null) {
            ImageHelper.loadImage(this, mIvLiveIntroSettingIcon, mBean.getFacePic(),
                    R.drawable.live_host_defaual_bg);
            mTvLiveIntroSettingMainspeakerIntro.setText(mBean.getSpeakerIntroduce());
            mTvLiveIntroSettingMainspeakerName.setText(mBean.getMainSpeaker());
            mTvLiveIntroSettingSubtitle.setText(mBean.getSubtitle());
            mTvLiveIntroSettingIntroStarttime.setText(DateUtil.stampToDate(mBean.getStarttime()));

            try {
                if (!TextUtils.isEmpty(intro)) {
                    String text = Jsoup.parse(intro).body().text();
                    mTvLiveIntroSettingLiveIntro.setText(text);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            mTvDividedProportion.setText(mBean.getPent());

            switch (mBean.getChattype()) {
                case "0":
                    mRlDividedProportion.setVisibility(View.GONE);
                    break;

                case "1":
                    mRlLiveIntroSettingIntroPassword.setVisibility(View.VISIBLE);
                    mTvLiveIntroSettingIntroPassword.setText(mBean.getPassword());
                    break;

                case "2":
                    mRlDividedProportion.setVisibility(View.VISIBLE);
                    mRlLiveIntroSettingIntroPrice.setVisibility(View.VISIBLE);
                    mTvLiveIntroSettingIntroPrice.setText(mBean.getFee());
                    break;
            }

            mCbLiveIntroSettingSingup.setChecked(mBean.isShowSignUp());
            mCbLiveIntroSettingSharelist.setChecked(mBean.isShowShare());

            if(!"0".equals(seriesId)){
                mRlLiveIntroSettingIntroPrice.setVisibility(View.GONE);
                mRlDividedProportion.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.rl_live_intro_setting_head,
            R.id.rl_live_intro_setting_subtitle,
            R.id.rl_live_intro_setting_intro_starttime,
            R.id.rl_live_intro_setting_mainspeaker_name,
            R.id.rl_live_intro_setting_mainspeaker_intro,
            R.id.rl_live_intro_setting_live_intro,
            R.id.rl_live_intro_setting_intro_price,
            R.id.rl_live_intro_setting_intro_password,
            R.id.rl_live_intro_setting_series,
            R.id.rl_divided_proportion})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_live_intro_setting_head:
                //跳到去设置头像
                chooseImg();
                break;

            case R.id.rl_live_intro_setting_subtitle:
                showTheamDialog();
                break;

            case R.id.rl_live_intro_setting_intro_starttime:
                showTimeDialog();
                break;

            case R.id.rl_live_intro_setting_intro_price:
                //显示价格输入
                showPriceDialog();
                break;

            case R.id.rl_live_intro_setting_intro_password:
                //显示密码设置
                showPasswordDialog();
                break;

            case R.id.rl_live_intro_setting_mainspeaker_name:
                //输入主讲人名字
                showNameDialog();
                break;

            case R.id.rl_live_intro_setting_mainspeaker_intro:
                //输入主讲人的介绍
                showSpeakerIntroDialog();
                break;

            case R.id.rl_live_intro_setting_live_intro:
                //输入课堂的概要
                showLiveIntroDialog();
                break;

            case R.id.rl_live_intro_setting_series:
                showSeriesDialog();
                break;

            case R.id.headBackButton:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.headRightButton:
                submit();
                break;

            case R.id.rl_divided_proportion:
                showDividedProportion();
                break;
        }
    }

    AlertDialog mDialog;

    private void showTheamDialog() {
        mDialog = DialogUtil.showInputDialog(this, false, "", "你只有一次修改主题，确定要修改吗",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSubtitleDialog();
                        mDialog.dismiss();
                    }
                });
    }

    AlertDialog timeDialog;

    private void showTimeDialog() {
        timeDialog = DialogUtil.showInputDialog(this, false, "", "你只有一次修改开播的时间，确定要修改吗",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePop();
                        timeDialog.dismiss();
                    }
                });

    }

    private void submit() {
        HashMap<String, String> map = new HashMap<>();
        if (!UserManager.getInstance().isLogin()) {
            ToastUtil.showShort(this, "请先登录");
            return;
        }

        map.put("token", UserManager.getInstance().getToken());
        map.put("id", mBean.getId());

        if (TextUtils.isEmpty(mTvLiveIntroSettingSubtitle.getText().toString())) {
            ToastUtil.showShort(this, "主题不能为空");
            return;
        }

        mBean.setSubtitle(mTvLiveIntroSettingSubtitle.getText().toString());
        map.put("subtitle", mTvLiveIntroSettingSubtitle.getText().toString());
        if (!TextUtils.isEmpty(mTvLiveIntroSettingMainspeakerName.getText().toString())) {
            mBean.setMainSpeaker(mTvLiveIntroSettingMainspeakerName.getText().toString());
            map.put("mainSpeaker", mTvLiveIntroSettingMainspeakerName.getText().toString());
        }

        if (mStartTime != 0) {
            mBean.setStarttime(String.valueOf(mStartTime));
            map.put("starttime", String.valueOf(mStartTime));
        }

        if (!TextUtils.isEmpty(mTvDividedProportion.getText().toString())) {
            mBean.setPent(mTvDividedProportion.getText().toString());
            map.put("pent", mTvDividedProportion.getText().toString());
        }

        if (!TextUtils.isEmpty(mTvLiveIntroSettingMainspeakerIntro.getText().toString())) {
            mBean.setSpeakerIntroduce(mTvLiveIntroSettingMainspeakerIntro.getText().toString());
            map.put("speakerIntroduce", mTvLiveIntroSettingMainspeakerIntro.getText().toString());
        }

        if (!TextUtils.isEmpty(intro)) {
            mBean.setDesc(intro);
            map.put("desc", intro);
        }

        if (mNewFileUrl != null) {
            mBean.setFacePic(mNewFileUrl);
            map.put("facePic", mNewFileUrl);
        }

        if (TextUtils.isEmpty(seriesId) && !mBean.getChatSeries().equals(seriesId)) {
            mBean.setChatSeries(seriesId);
            map.put("chatSeries", seriesId);
        }


        switch (mBean.getChattype()) {
            case "1":
                if (TextUtils.isEmpty(
                        mTvLiveIntroSettingIntroPassword.getText().toString()) || mTvLiveIntroSettingIntroPassword.getText().toString().length() < 6) {
                    ToastUtil.showShort(this, "密码不能为空,最少需要6个字符");
                    return;
                } else if (!RegexUtils.isMathPsw(mTvLiveIntroSettingIntroPassword.getText())) {
                    ToastUtil.showShort(this, "密码只能用字母或数字");
                    return;
                } else {
                    mBean.setPassword(mTvLiveIntroSettingIntroPassword.getText().toString());
                    map.put("password", mTvLiveIntroSettingIntroPassword.getText().toString());
                }
                break;
            case "2":
                if (!mBean.getFee().equals(mTvLiveIntroSettingIntroPrice.getText().toString())) {
                    mBean.setFee(mTvLiveIntroSettingIntroPrice.getText().toString());
                    map.put("fee", mTvLiveIntroSettingIntroPrice.getText().toString());
                }
                break;
        }

        if (mRlLiveIntroSettingIntroPrice.isShown() && !mBean.getFee().equals(mTvLiveIntroSettingIntroPrice.getText().toString())) {
            mBean.setFee(mTvLiveIntroSettingIntroPrice.getText().toString());
            map.put("fee", mTvLiveIntroSettingIntroPrice.getText().toString());
        }

        mBean.setShowShare(mCbLiveIntroSettingSharelist.isChecked() ? "0" : "1");
        mBean.setShowSignup(mCbLiveIntroSettingSingup.isChecked() ? "0" : "1");
        map.put("showSignup", mCbLiveIntroSettingSingup.isChecked() ? "0" : "1");
        map.put("showShare", mCbLiveIntroSettingSharelist.isChecked() ? "0" : "1");
        map.put("isPublish",mBean.getIsPublish());
        /*
            提交逻辑改成修改课程资料，课程不变成审核状态，审核修改的资料，审核后才更新
        */
        mCompositeSubscription.add(LiveApi.getInstance().saveChatInfoIntroduction(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        //Intent intent = new Intent();
                        //intent.putExtra("bean", mBean);
                        //setResult(RESULT_OK, intent);
                        //finish();
                        showSaveDialog();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(LiveIntroSettingActivity.this, message);
                    }
                })));
    }

    private AlertDialog saveDialog;

    private void showSaveDialog(){
        saveDialog = DialogUtil.showInputDialog(this, false, "", "修改成功，内容审核后才能生效", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDialog.dismiss();
                finish();
            }
        });
    }

    private void showSeriesDialog() {
        if (mPopPosition == null) {
            if (series == null) {
                ToastUtil.showShort(this, "数据加载出错 请稍后再试");
                getTypeData();
                return;
            }

            //没有系列课 去选择系列课
            if (series.length == 0) {
                GotoUtil.goToActivityForResult(this, SeriesClassifyActivity.class, 200);
                return;
            }

            mPopPosition = new PopPosition(this, R.layout.pop_ts_position);

            mPopPosition.setData(series);
            mPopPosition.setTitle("选择要移动到的系列课");
            mPopPosition.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    mTvLiveIntroSettingSeries.setText(series[newVal]);
                    seriesId = mSeriesTypes.get(newVal).getId();
                }
            });
        }
        mPopPosition.showPopOnRootView(this);

    }

    private void showPasswordDialog() {
        DialogUtil.showTopicInputDialog(this, "修改密码", "请设置密码",
                mTvLiveIntroSettingIntroPassword.getText().toString(), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            mTvLiveIntroSettingIntroPassword.setText(str);
                        }
                    }
                });
    }

    private void showPriceDialog() {
        DialogUtil.showTopicInputDialog(this, "修改价格", "",
                mTvLiveIntroSettingIntroPrice.getText().toString(),
                8194, 1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        try {
                            double integer = Double.valueOf(str);
                            if (integer < 1 || integer > 99999) {
                                ToastUtil.showShort(LiveIntroSettingActivity.this, "价格范围是1-99999");
                            } else {
                                mTvLiveIntroSettingIntroPrice.setText(String.valueOf(integer));
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            ToastUtil.showShort(LiveIntroSettingActivity.this, "请输入正确的格式");
                        }
                    }
                });
    }


    private void showLiveIntroDialog() {
        Intent intent = new Intent(LiveIntroSettingActivity.this, IntroResolveActivity.class);
        intent.putExtra("intro", intro);
        startActivityForResult(intent, SETTING_INTRO_REQUESTCODE);
    }


    private void showSpeakerIntroDialog() {
        DialogUtil.showTopicInputDialog(this, "填写简介", "请填写主讲人简介",
                mTvLiveIntroSettingMainspeakerIntro.getText().toString(),
                -1,
                5,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            mTvLiveIntroSettingMainspeakerIntro.setText(str);
                        }
                    }
                });
    }

    private void showNameDialog() {
        DialogUtil.showTopicInputDialog(this, "填写主讲人", "请填写主讲人名称，多个主讲人请用空格隔开",
                mTvLiveIntroSettingMainspeakerName.getText().toString(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            mTvLiveIntroSettingMainspeakerName.setText(str);
                        }
                    }
                });
    }

    private void showDividedProportion() {
        DialogUtil.showTopicInputDialog(this, "修改分成比例", "请设置分成比例(5%~70%)",
                mTvDividedProportion.getText().toString(), EditorInfo.TYPE_CLASS_NUMBER, 0,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        int    num = 0;
                        if (!TextUtils.isEmpty(str)) {
                            try {
                                num = Integer.valueOf(str);
                                if (num < 5 || num > 70) {
                                    ToastUtil.showShort(LiveIntroSettingActivity.this,
                                            "请设置正确分成比例(5~70)");
                                } else {
                                    mTvDividedProportion.setText(num + "");
                                }
                            } catch (Exception e) {
                                ToastUtil.showShort(LiveIntroSettingActivity.this, "请输入正确数值");
                            }
                        }
                    }
                });
    }

    private void showSubtitleDialog() {
        DialogUtil.showTopicInputDialog(this, "填写标题", "请输入课程标题",
                mTvLiveIntroSettingSubtitle.getText().toString(), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {
                            ToastUtil.showShort(LiveIntroSettingActivity.this, "标题不能为空");
                        } else {
                            mTvLiveIntroSettingSubtitle.setText(str);
                        }
                    }
                });
    }


    //选择日期
    private void showTimePop() {
        try {
            long endTime = Long.valueOf(mBean.getStarttime());
            if (endTime < System.currentTimeMillis()) {
                ToastUtil.showShort(this, "直播时间已结束，不能修改时间");

            } else {
                if (timePickerView == null) {
                    TimePickerView.Builder builder = new TimePickerView.Builder(this, this).setType(
                            TimePickerView.Type.ALL).setOutSideCancelable(true);

                    timePickerView = new TimePickerView(builder);
                }
                timePickerView.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, 1011, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                int height = (int) getResources().getDimension(R.dimen.px500dp);
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
                                .setIsCrop(true)
                                .setAspectRatio(4, 3)
                                .setMaxHeight(height)
                                .create();
                PictureConfig.getInstance().init(options).openPhoto(LiveIntroSettingActivity.this, LiveIntroSettingActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(LiveIntroSettingActivity.this, false,
                        null, getString(R.string.pre_scan_notice_msg), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(
                                            LiveIntroSettingActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });

            }
        });
    }

    @Override
    public void onTimeSelect(Date date, View v) {
        mDate = date;
        sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);//yyyy.MM.dd.HH.mm.ss
        if (mDate.getTime() < System.currentTimeMillis()) {
            ToastUtil.showShort(LiveIntroSettingActivity.this, "设置的时间必须是未来时间");
            return;
        }
        mStartTime = mDate.getTime();
        time = sdf.format(date);
        mTvLiveIntroSettingIntroStarttime.setText(time);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTING_INTRO_REQUESTCODE && resultCode == RESULT_OK) {
            intro = data.getStringExtra("intro");
            if (!TextUtils.isEmpty(intro)) {
                String text = Jsoup.parse(intro).body().text();
                mTvLiveIntroSettingLiveIntro.setText(text);
            }
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {
            getTypeData();
        }

    }


    private void showLoad() {
        getBaseLoadingView().showLoading();
    }

    private void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }


    public void compressImg(String s) {
        showLoad();

        //将图片进行压缩
        final File file = new File(s);
        Luban.get(MyApplication.getInstance()).load(file).launch().asObservable().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<File>(new ApiCallBack<File>() {
                    @Override
                    public void onSuccess(File compressFile) {
                        if(FileUtil.getSize(file) > maxLen_500k ){
                            uploadingFile(compressFile);
                        }else {
                            uploadingFile(file);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
    }


    public void uploadingFile(File file) {
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if (getBaseLoadingView() == null) return;
                showLoadFinish();
                showUploadingSuccess(result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                showLoadFinish();
                ToastUtil.showShort(LiveIntroSettingActivity.this, msg);
            }
        }, null, file);
    }


    private void showUploadingSuccess(String fileUrl) {
        mNewFileUrl = fileUrl;
        ImageHelper.loadImage(LiveIntroSettingActivity.this, mIvLiveIntroSettingIcon, fileUrl);

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.unsubscribe();
        if (uploadSub != null) {
            uploadSub.unsubscribe();
        }
        mAlertDialog = null;
        super.onDestroy();
    }

    private void getTypeData() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("chatRoomId", mBean.getChatRoom());
        mCompositeSubscription.add(MineApi.getInstance().getChatSeriesTypeList(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(
                        new ApiCallBack<List<ChooseClassifyBean>>() {
                            @Override
                            public void onSuccess(List<ChooseClassifyBean> data) {
                                if (data != null && mTvLiveIntroSettingSeries != null) {
                                    mSeriesTypes = data;
                                    series = new String[mSeriesTypes.size()];
                                    for (int i = 0; i < mSeriesTypes.size(); i++) {
                                        series[i] = mSeriesTypes.get(i).getTypeName();
                                    }
                                    for (ChooseClassifyBean seriesType : mSeriesTypes) {
                                        if (seriesType.getId().equals(seriesId)) {
                                            mTvLiveIntroSettingSeries.setText(
                                                    seriesType.getTypeName());
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(LiveIntroSettingActivity.this,
                                        errorCode, message);
                            }
                        })));
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        compressImg(media.getPath());
    }
}
