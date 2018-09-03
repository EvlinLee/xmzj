package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.helper.PermissionsHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.CreateLiveTopicRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.live.apply.ApplyLecturerActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroSettingActivity;
import com.gxtc.huchuan.ui.live.intro.introresolve.IntroResolveActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.widget.MultiRadioGroup;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.greenrobot.eventbus.Subscribe;
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
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import cn.jzvd.JZVideoPlayer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;


/**
 * Describe:我的 > 课堂 > 课堂后台 > 新建课程
 * Created by ALing on 2017/3/15 .
 */
public class CreateTopicActivity extends BaseTitleActivity implements View.OnClickListener,
        TimePickerView.OnTimeSelectListener, MultiRadioGroup.OnCheckedChangeListener,
        PictureConfig.OnSelectResultCallback, LoadHelper.UploadCallback {
    private static final String TAG = "CreateTopicActivity";

    private static final int SETTING_INTRO_REQUESTCODE = 1 << 5;

    @BindView(R.id.et_live_topic)
    TextInputEditText mEtLiveTopic;
    @BindView(R.id.edit_teacher)
    TextInputEditText editTeacher;
    @BindView(R.id.iv_live_intro_setting_head)
    ImageView imgFace;
    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.btn_next)
    Button mBtnNext;
    @BindView(R.id.radioGroup)
    MultiRadioGroup mRadioGroup;
    @BindView(R.id.rb_lecture)
    RadioButton mRbLecture;
    @BindView(R.id.rb_slide)
    RadioButton mRbSlide;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_label)
    TextView label;
    @BindView(R.id.tv_teacher_info)
    TextView tvTeacherInfo;
    @BindView(R.id.tv_live_info)
    TextView tvLiveInfo;
    @BindView(R.id.rl_live_intro_video)
    RelativeLayout rlTopicVideo;
    @BindView(R.id.iv_live_intro_video)
    ImageView ivVideoCover;

    private String live_form = "0";     //课堂形式
    private String intro;
    private String teacherInfo;
    private TimePickerView timePickerView;
    private String time;
    private String mNewFileUrl;
    private boolean isCheck;
    private Date mDate;
    private SimpleDateFormat sdf;
    private String chatRoom;      //课堂间ID


    PopPosition mPopPosition;
    private List<ChatInfosBean> mData;
    private String[] mTitles;
    private String classTypeId = "";
    private String chatSeries;
    private CreateLiveTopicRepository source;

    private AlertDialog mAlertDialog;

    private int selectType = -1;  // 1为选择图片，2为选择视频
    private String videoUrl = "";  // 视频地址
    private String videoPic = "";  // 视频封面地址
    private String videoPath = ""; //  视频本地地址
    private boolean isUploadVideo = false; // 是否上传视频
    private boolean isUploadFinish = false; // 视频是否上传成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);

    }

    @Override
    public void initView() {
        super.initView();
        mRadioGroup.setOrientation(LinearLayout.VERTICAL);
        getBaseHeadView().showTitle(getString(R.string.title_create_topic));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        super.initData();
        EventBusUtil.register(this);
        source = new CreateLiveTopicRepository();
        chatRoom = UserManager.getInstance().getUser().getChatRoomId();
        Intent intent = getIntent();
        chatSeries = intent.getStringExtra("chatSeries");
        if (chatSeries != null) {
            mTvType.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
        } else {
            classTypeId = intent.getStringExtra("chatInfoTypeId");
            mTvType.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
        }
        if ("0".equals(UserManager.getInstance().getIsAnchor())) {
            showDialog();
        } else if ("2".equals(UserManager.getInstance().getIsAnchor())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setMessage("您现在还不是讲师不能新建课程，您提交的申请在审核中！");
            dialog.setCancelable(false);
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreateTopicActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }
    }

    private AlertDialog mDialog;

    private void showDialog() {
        mDialog = DialogUtil.createDialog(this, "温馨提示", "您还不是主播不能创建课堂，请申请讲师！", "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        CreateTopicActivity.this.finish();
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        GotoUtil.goToActivity(CreateTopicActivity.this,
                                ApplyLecturerActivity.class);
                        CreateTopicActivity.this.finish();
                        mDialog.dismiss();
                    }

                });
        mDialog.show();

    }

    private void getTypeData() {
        Subscription sub = AllApi.getInstance().getLiveType().observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(
                        new ApiCallBack<List<ChatInfosBean>>() {
                            @Override
                            public void onSuccess(List<ChatInfosBean> data) {
                                if (data != null && data.size() > 0) {
                                    mData = data;
                                    mTitles = new String[data.size()];
                                    for (int i = 0; i < data.size(); i++) {
                                        mTitles[i] = data.get(i).getTypeName();
                                    }
                                }
                                mPopPosition.setData(mTitles);
                                mPopPosition.setTitle("选择分类");
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                getBaseLoadingView().hideLoading();
                                ToastUtil.showShort(MyApplication.getInstance(), message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void initListener() {
        super.initListener();
        mRadioGroup.setOnCheckedChangeListener(this);
        mEtLiveTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnNext.setEnabled(true);
                } else {
                    mBtnNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.btn_next, R.id.tv_start_time, R.id.btn_class_protocol, R.id.tv_type, R.id.tv_teacher_info, R.id.tv_live_info,
            R.id.rl_live_intro_setting_head, R.id.rl_live_intro_video})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //选择时间
            case R.id.tv_start_time:
                showTimePop();
                break;

            //下一步
            case R.id.btn_next:
                gotoNext();
                break;

            //创建课堂协议
            case R.id.btn_class_protocol:
                goToProtocol();
                break;

            //选择课程类型
            case R.id.tv_type:
                showTypePop();
                break;

            //主讲人信息
            case R.id.tv_teacher_info:
                showSpeakerIntroDialog();
                break;

            //课程信息
            case R.id.tv_live_info:
                showLiveIntroDialog();
                break;

            //选择封面
            case R.id.rl_live_intro_setting_head:
                selectType = 1;
                chooseImg();
                break;
            //选择视频
            case R.id.rl_live_intro_video:
                selectType = 2;
                if (isUploadVideo) {
                    showDeleteDialog();
                } else {
                    showVideoDialog();
                }
                break;
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
                PictureConfig.getInstance().init(options).openPhoto(CreateTopicActivity.this, CreateTopicActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(CreateTopicActivity.this, false,
                        null, getString(R.string.pre_scan_notice_msg), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(CreateTopicActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });

            }
        });
    }


    private void showLiveIntroDialog() {
        Intent intent = new Intent(CreateTopicActivity.this, IntroResolveActivity.class);
        intent.putExtra("intro", intro);
        startActivityForResult(intent, SETTING_INTRO_REQUESTCODE);
    }


    private void showSpeakerIntroDialog() {
        DialogUtil.showTopicInputDialog(this, "填写简介", "请填写主讲人简介",
                tvTeacherInfo.getText().toString(),
                -1,
                5,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        teacherInfo = str;
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            tvTeacherInfo.setText(str);
                        }
                    }
                });
    }


    private void goToProtocol() {
        String url = "https://app.xinmei6.com/publish/chatRule.html";
        CommonWebViewActivity.startActivity(this, url, "");
    }

    private void gotoNext() {
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(mEtLiveTopic.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_topic_subject));
            return;
        }
        if (TextUtils.isEmpty(mTvStartTime.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_create_topic_time));
            return;
        }

        if (!(mRbLecture.isChecked() | mRbSlide.isChecked())) {
            ToastUtil.showShort(this, getString(R.string.toast_choose_live_type));
            return;
        }

        if (TextUtils.isEmpty(editTeacher.getText().toString())) {
            ToastUtil.showShort(this, "请输入主讲人");
            return;
        }

        if (TextUtils.isEmpty(teacherInfo)) {
            ToastUtil.showShort(this, "请输入主讲人信息");
            return;
        }

        if (TextUtils.isEmpty(intro)) {
            ToastUtil.showShort(this, "请输入课程简介");
            return;
        }

        if (TextUtils.isEmpty(mNewFileUrl)) {
            ToastUtil.showShort(this, "请选择课程封面");
            return;
        }

        nextStep();

    }

    /**
     * 点击跳转下一步
     */
    private void nextStep() {
        long times = mDate.getTime();
        HashMap<String, String> map = new HashMap<>();
        map.put("chatRoom", chatRoom);
        map.put("subtitle", mEtLiveTopic.getText().toString());
        map.put("starttime", String.valueOf(times));
        map.put("chatway", live_form);
        map.put("facePic", mNewFileUrl);                            //课程封面
        map.put("mainSpeaker", editTeacher.getText().toString());   //主讲人名字
        map.put("speakerIntroduce", teacherInfo);                   //主讲人介绍
        map.put("desc", intro);                                     //课程简介
        if (isUploadVideo) {
            if (isUploadFinish) {
                map.put("videoPic", videoPic);//视频封面
                map.put("videoText", videoUrl);//视频链接
            } else {
                ToastUtil.showShort(this, "请等待视频上传成功");
                return;
            }
        }
        if (chatSeries != null) {
            map.put("token", UserManager.getInstance().getToken());
            map.put("chatSeries", chatSeries);
            map.put("fee", "0");
            map.put("pent", "0");
            map.put("chattype", "2");
            map.put("chatInfoTypeId", classTypeId);

            createLiveTopic(map, new ApiCallBack<CreateLiveTopicBean>() {
                @Override
                public void onSuccess(CreateLiveTopicBean bean) {
                    ToastUtil.showShort(CreateTopicActivity.this, "创建课程成功");     //进入课程主页
                    EventBusUtil.post(new CreateLiveTopicBean());
                    finish();
                    LiveIntroActivity.startActivity(CreateTopicActivity.this, bean.getId());//这里 要传的是 课程ID  不是课堂间id
                }

                @Override
                public void onError(String errorCode, String message) {
                    Toast.makeText(CreateTopicActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        int groupId = getIntent().getIntExtra("groupIds", -1);
        if (TextUtils.isEmpty(classTypeId)) {
            ToastUtil.showShort(this, "请选择课程类型");
            return;
        }

        map.put("chatInfoTypeId", classTypeId.trim());
//        if (groupId != -1) {
        map.put("groupIds", "" + groupId);
//            GotoUtil.goToActivityWithData(this, CreateTopicNextByCircleActivity.class, map);
//        } else {
        GotoUtil.goToActivityWithData(this, CreateTopicNextActivity.class, map);
//        }
    }


    private void createLiveTopic(HashMap<String, String> map, ApiCallBack<CreateLiveTopicBean> callback) {
        source.createLiveTopic(map, callback);
    }

    @Override
    public void onCheckedChanged(MultiRadioGroup group, int checkedId) {
        switch (checkedId) {
            //讲座
            case R.id.rb_lecture:
                isCheck = true;
                live_form = "0";
                break;
            //幻灯片
            case R.id.rb_slide:
                isCheck = true;
                live_form = "1";
                break;
        }
    }

    /**
     * 选择课程类型
     */
    private void showTypePop() {
        WindowUtil.closeInputMethod(this);
        if (mPopPosition == null) {
            mPopPosition = new PopPosition(this, R.layout.pop_ts_position);
        }
        if (mTitles != null && mTitles.length > 0) {
            mPopPosition.setData(mTitles);
        } else {
            getTypeData();
        }
        mPopPosition.setTitle("选择分类");
        mPopPosition.showPopOnRootView(this);
        mPopPosition.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                if (mTitles != null && newVal < mTitles.length) {
                    classTypeId = mData.get(newVal).getId();
                    mTvType.setText(mTitles[newVal]);
                }
            }
        });
    }

    //选择日期
    private void showTimePop() {
        WindowUtil.closeInputMethod(this);
        if (timePickerView == null) {
            TimePickerView.Builder builder = new TimePickerView.Builder(this, this).setType(
                    TimePickerView.Type.ALL).setDate(new Date()).setOutSideCancelable(true);

            timePickerView = new TimePickerView(builder);
        }
        timePickerView.show();
    }

    @Override
    public void onTimeSelect(Date date, View v) {
        mDate = date;
        sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);//yyyy.MM.dd.HH.mm.ss

        time = sdf.format(date);

        mDate = date;
        sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);//yyyy.MM.dd.HH.mm.ss
        if (mDate.getTime() < System.currentTimeMillis()) {
            ToastUtil.showShort(CreateTopicActivity.this, "设置的时间必须是未来时间");
            return;
        } else {
            time = sdf.format(date);
            mTvStartTime.setText(time);
        }
    }

    @Subscribe
    public void onEvent(CreateLiveTopicBean bean) {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_INTRO_REQUESTCODE && resultCode == RESULT_OK) {
            intro = data.getStringExtra("intro");
            if (!TextUtils.isEmpty(intro)) {
                String text = Jsoup.parse(intro).body().text();
                tvLiveInfo.setText(text);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }


    public static void startActivity(Context activity, int GroupId) {
        Intent intent = new Intent(activity, CreateTopicActivity.class);
        intent.putExtra("groupIds", GroupId);
        activity.startActivity(intent);
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        if (selectType == 1) {
            compressImg(media.getPath());
        } else {
            isUploadVideo = true;
            isUploadFinish = false;
            String file = media.getPath();
            videoPath = file;
            File videoFile = new File(file);
            if (!TextUtils.isEmpty(file) && file.endsWith(".mp4")) {
                String a = "a" + System.currentTimeMillis();
                LoadHelper.UpyunUploadVideo(videoFile, this, new UIProgressListener() {
                    @Override
                    public void onUIFailed(String errorCode, String msg) {
                        super.onUIFailed(errorCode, msg);
                    }
                });
            } else {
                ToastUtil.showShort(this, "请选择mp4格式的视频");
            }
        }
    }


    public void compressImg(String s) {

        //将图片进行压缩
        final File file = new File(s);
        Luban.get(MyApplication.getInstance()).load(file).launch().asObservable().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<File>(new ApiCallBack<File>() {
                    @Override
                    public void onSuccess(File compressFile) {
                        if (FileUtil.getSize(file) > maxLen_500k) {
                            uploadingFile(compressFile);
                        } else {
                            uploadingFile(file);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));
    }

    public void uploadingFile(File file) {
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if (getBaseLoadingView() == null) return;
                showUploadingSuccess(result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ToastUtil.showShort(CreateTopicActivity.this, msg);
            }
        }, null, file);
    }

    private void showUploadingSuccess(String fileUrl) {
        mNewFileUrl = fileUrl;
        ImageHelper.loadImage(CreateTopicActivity.this, imgFace, fileUrl);

    }

    /**********************************************分割线************************************************/

    //选择视频弹窗
    private ListDialog mListDialog;
    private ListDialog mDeleteDialog;

    private void showVideoDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this, new String[]{"拍摄", "从本地选择"});
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //使用相机拍摄
                    if (position == 0) {
                        gotoSelectFile(2);
                        //从文件选择
                    } else {
                        gotoSelectFile(1);
                    }
                }
            });
        }
        mListDialog.show();
    }

    private void showDeleteDialog() {
        if (mDeleteDialog == null) {
            mDeleteDialog = new ListDialog(this, new String[]{"删除", "重新选择"});
            mDeleteDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //使用相机拍摄
                    if (position == 0) {
                        isUploadVideo = false;
                        isUploadFinish = false;
                        ivVideoCover.setImageResource(R.drawable.live_list_place_holder);
                        //从文件选择
                    } else {
                        if (isUploadFinish) {
                            gotoSelectFile(1);
                        } else {
                            ToastUtil.showShort(CreateTopicActivity.this, "请等待上一段视频上传结束再重新选择");
                        }
                    }
                }
            });
        }
        mDeleteDialog.show();
    }

    //去文件夹选择文件
    private void gotoSelectFile(final int type) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionsHelper.getInstance(this).performRequestPermissions(getString(R.string.txt_card_permission), pers, 10010, new PermissionsHelper.PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                FunctionOptions options = new FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_VIDEO)
                        .setRecordVideoDefinition(1)
                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                        .setShowCamera(true)
                        .setImageSpanCount(3)
                        .setPreviewVideo(true)
                        .create();
                if (type == 1) {
                    PictureConfig.getInstance().init(options).openPhoto(CreateTopicActivity.this, CreateTopicActivity.this);
                } else if (type == 2) {
                    PictureConfig.getInstance().init(options).startOpenCamera(CreateTopicActivity.this, CreateTopicActivity.this);
                }
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(CreateTopicActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(CreateTopicActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });
    }

    @Override
    public void onUploadSuccess(UploadResult result) {
        isUploadVideo = true;
        isUploadFinish = true;
        videoUrl = result.getUrls().get(0);
        videoPic = result.getUrls().get(1);
        Bitmap bitmap = ImageUtils.getVideoFirstFrame(videoPath);
        ivVideoCover.setImageBitmap(bitmap);
    }

    @Override
    public void onUploadFailed(String errorCode, String msg) {
        isUploadVideo = false;
        isUploadFinish = false;
    }
}
