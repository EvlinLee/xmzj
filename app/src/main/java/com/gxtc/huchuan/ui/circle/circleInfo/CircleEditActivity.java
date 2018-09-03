package com.gxtc.huchuan.ui.circle.circleInfo;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import top.zibin.luban.Luban;


/**
 * 编辑圈子资料
 */
public class CircleEditActivity extends BaseTitleActivity implements View.OnClickListener,
        CircleInfoContract.View, PictureConfig.OnSelectResultCallback {

    public static final  int    REQUEST_CODE_CIRCLE_FACE = 10000;
    @BindView(R.id.headArea)            RelativeLayout layoutHead;
    @BindView(R.id.headBackButton)      ImageButton    btnBack;
    @BindView(R.id.headRightButton)     Button         btnHeadRight;
    @BindView(R.id.tv_intro)            TextView       tvIntro;
    @BindView(R.id.headTitle)           TextView       tvTitle;
    @BindView(R.id.edit_name)           TextView       editName;
    @BindView(R.id.img_header_bg)       ImageView      imgHeaderBg;
    @BindView(R.id.img_icon)            ImageView      imgIcon;
    @BindView(R.id.img_camera)          ImageView      imgCamera;
    @BindView(R.id.tv_label_chang_face) TextView       tvLabelChangeFace;
    @BindView(R.id.rl_chang_face)       RelativeLayout rlChangeFace;

    private int                          mGroupID;       //圈子Id
    private int                          isMy;              //是否是圈主，只有圈主和管理员才能修改资料，只有圈主才能删除圈子，其他成员（包括管理员）都只能退出圈子
    private int                          mMenberType;        //成员类型。0:普通用户，1：管理员，2：圈主
    private boolean                      isHead;
    private CircleBean                   bean;
    private String                       mGroupName;     //圈子名称
    private ArrayList<String>            pathList;
    private CircleInfoContract.Presenter mPresenter;
    private String                       cover;
    private String                       backgCover;
    private HashMap<String, Object>      map;
    private android.support.v7.app.AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_edit);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        bean = (CircleBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        if (bean != null) {
            mGroupID = bean.getId();
            mGroupName = bean.getName();
            mMenberType = bean.getMemberType();
            isMy = bean.getIsMy();
        }
        //普通用户   不能编辑圈子资料
        if (mMenberType == 0) {
            tvTitle.setText(getString(R.string.title_check_circle_info));

            tvTitle.setText(getString(R.string.title_circle_info));
            btnHeadRight.setVisibility(View.GONE);
            tvLabelChangeFace.setVisibility(View.GONE);
            tvIntro.setCompoundDrawables(null, null, null, null);

        //管理员      可以编辑圈子资料
        } else if (mMenberType == 1) {
            tvTitle.setText(getString(R.string.title_circle_edit));

            btnHeadRight.setVisibility(View.VISIBLE);
            btnHeadRight.setTextColor(getResources().getColor(R.color.white));
            btnHeadRight.setText("完成");
            btnHeadRight.setOnClickListener(this);
            rlChangeFace.setOnClickListener(this);
            imgHeaderBg.setOnClickListener(this);
            tvIntro.setOnClickListener(this);

        //圈主        可以编辑圈子资料并删除圈子
        } else {
            tvTitle.setText(getString(R.string.title_circle_edit));

            btnHeadRight.setVisibility(View.VISIBLE);
            btnHeadRight.setTextColor(getResources().getColor(R.color.white));
            btnHeadRight.setText("完成");
            btnHeadRight.setOnClickListener(this);

            rlChangeFace.setOnClickListener(this);
            imgHeaderBg.setOnClickListener(this);
            tvIntro.setOnClickListener(this);
        }

        setActionBarTopPadding(layoutHead, true);
        layoutHead.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvTitle.setTextColor(getResources().getColor(R.color.white));

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnBack.setImageResource(R.drawable.navigation_icon_back_white);

        cover = bean.getCover();
        imgCamera.setVisibility(View.GONE);
        editName.setText(bean.getName());
        tvIntro.setText(bean.getContent());
        ImageHelper.loadImage(CircleEditActivity.this, imgIcon, bean.getCover());
        if(TextUtils.isEmpty(bean.getBackgCover())){
            ImageHelper.loadImage(CircleEditActivity.this, imgHeaderBg, bean.getCover());
        }else{
            backgCover = bean.getBackgCover();
            ImageHelper.loadImage(CircleEditActivity.this, imgHeaderBg, bean.getBackgCover());
        }
    }

    @Override
    public void initData() {
        new CircleInfoPresenter(this);
        EventBusUtil.register(this);
        map = new HashMap<>();
        mSubscriptions = new CompositeSubscription();

        //mPresenter.getCircleInfo(UserManager.getInstance().getToken(), mGroupID);

    }

    @OnClick({R.id.headBackButton, R.id.headRightButton})
    @Override
    public void onClick(View v) {
        WindowUtil.closeInputMethod(this);
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //完成
            case R.id.headRightButton:
                editComplete();
                break;

            //换圈子头像
            case R.id.rl_chang_face:
                isHead = true;
                chooseImg();
                break;

            //换圈子背景
            case R.id.img_header_bg:
                isHead = false;
                chooseImg();
                break;

            //简介
            case R.id.tv_intro:
                gotoIntro();
                break;
        }
    }

    private void editComplete() {
        map.put("token", UserManager.getInstance().getToken());
        map.put("id", mGroupID);
        map.put("groupName", mGroupName);
        if (!(TextUtils.isEmpty(cover))) {
            map.put("cover", cover);
        }
        if (!(TextUtils.isEmpty(backgCover))) {
            map.put("backgCover", backgCover);
        }
        if (!(TextUtils.isEmpty(tvIntro.getText().toString()))) {
            map.put("content", tvIntro.getText().toString());
        }
        mPresenter.editCircleInfo(map);

    }

    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和存储权限", pers, REQUEST_CODE_CIRCLE_FACE,
                new PermissionsResultListener() {
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
                                        .setIsCrop(true)
                                        .setAspectRatio(1, 1)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(CircleEditActivity.this, CircleEditActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CircleEditActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CircleEditActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    private void gotoIntro() {
        GotoUtil.goToActivity(this, InputIntroActivity.class, 0, bean);
    }

    private CompositeSubscription mSubscriptions;

    private void uploadingFile(String path){

        //将图片进行压缩
        final File file = new File(path);
        Subscription sub =
                    Luban.get(MyApplication.getInstance()).load(file)                     //传人要压缩的图片
                         .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                         .asObservable()
                         .subscribeOn(Schedulers.io())
                         .map(new Func1<File, File>() {
                             @Override
                             public File call(File compressFile) {
                                 return FileUtil.getSize(file) > Constant.COMPRESS_VALUE ? compressFile : file;
                             }
                         })
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new Action1<File>() {
                             @Override
                             public void call(File uploadFile) {
                                 LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE,
                                         new LoadHelper.UploadCallback() {
                                             @Override
                                             public void onUploadSuccess(UploadResult result) {
                                                 showUploadingSuccess(result.getUrl());
                                             }

                                             @Override
                                             public void onUploadFailed(String errorCode, String msg) {
                                                 ToastUtil.showShort(CircleEditActivity.this, msg);
                                             }
                                         }, null, uploadFile);
                             }
                         });

        mSubscriptions.add(sub);
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showMemberList(List<CircleMemberBean> datas) {}

    @Override
    public void showRefreshFinish(List<CircleMemberBean> datas) {}

    @Override
    public void showLoadMore(List<CircleMemberBean> datas) {}

    @Override
    public void showNoMore() {}

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
        if(isHead){
            cover = url;
            imgCamera.setVisibility(View.GONE);
            ImageHelper.loadImage(CircleEditActivity.this, imgIcon, cover);
        }else{
            backgCover = url;
            ImageHelper.loadImage(CircleEditActivity.this, imgHeaderBg, backgCover);
        }

    }

    @Override
    public void showCircleInfo(CircleBean bean) {

    }



    @Override
    public void showEditCircle(Object o) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        //延迟400毫秒在弹窗
        Subscription showSub = Observable.timer(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mAlertDialog =  DialogUtil.showDeportDialog(CircleEditActivity.this, false, null, "修改资料已提交，审核后才更新", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        });
                    }
                });
        RxTaskHelper.getInstance().addTask(this,showSub);
//        EventBusUtil.postStickyEvent(new EventImgBean(cover));
    }

    //---------------- 在圈子成员管理用到的

    @Override
    public void removeMember(CircleMemberBean circleMemberBean) {

    }

    @Override
    public void transCircle(CircleMemberBean circleMemberBean) {

    }

    @Override
    public void showChangeMemberTpye(CircleMemberBean circleMemberBean) {

    }

    //---------------- 在圈子成员管理用到的


    @Override
    public void setPresenter(CircleInfoContract.Presenter presenter) {
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

    //圈子介绍
    @Subscribe()
    public void onEvent(EventCircleIntro bean) {
        bean.setIntro(bean.getIntro());
        tvIntro.setText(bean.getIntro());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        uploadingFile(media.getPath());
    }
}
