package com.gxtc.huchuan.ui.circle.home;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CreateCircleBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.circle.EditCircleInfoActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.common.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by sjr on 2017/4/26.
 * 创建免费圈子
 */

public class CreateFreeCircleActivity extends BaseTitleActivity implements
        PictureConfig.OnSelectResultCallback {

    @BindView(R.id.iv_create_free_circle_selectimg) ImageView      ivCreateFreeCircleSelectimg;
    @BindView(R.id.rl_create_free_circle)           RelativeLayout rlCreateFreeCircle;
    @BindView(R.id.et_create_free_circle_name)      EditText       etCreateFreeCircleName;
    @BindView(R.id.et_create_free_circle_content)   EditText       etCreateFreeCircleContent;
    @BindView(R.id.iv_create_free_name)             ImageView      ivCreateFreeName;

    String circleId = "";//默认创建圈子时为空

    private String imgUrl;
    AlertDialog alertDialog;
    MineCircleBean mineCircleBean;
    private int isPubluc;
    private int groupType;
    private AlertDialog mAlertDialog;
    public int createGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_free_circle);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateFreeCircleActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_create_free_circle));
        getBaseHeadView().showHeadRightButton("审核", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etCreateFreeCircleName.getText())) {
                    ToastUtil.showShort(CreateFreeCircleActivity.this, "圈子名称不能为空");
                    return;
                }
                if (etCreateFreeCircleName.getText().toString().length() > 8) {
                    ToastUtil.showShort(CreateFreeCircleActivity.this, "圈子名称不能超过8个字");
                    return;
                }

                if (TextUtils.isEmpty(imgUrl)) {
                    ToastUtil.showShort(CreateFreeCircleActivity.this, "请上传封面");
                    return;
                }
                alertDialog = DialogUtil.showCircleDialog(CreateFreeCircleActivity.this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //这里先创建融云讨论组，然后在创建圈子
                                createChatroom();
                            }
                        });
            }
        });
    }

    @Override
    public void initData() {
         mineCircleBean = getIntent().getParcelableExtra("data");
        isPubluc = getIntent().getIntExtra("isPubluc",-1);
        groupType = getIntent().getIntExtra("groupType",-1);
        createGroupChat = getIntent().getIntExtra("createGroupChat",0);
//        etCreateFreeCircleName.addTextChangedListener(new CircleNameWatcher());
        if(mineCircleBean != null){
            circleId = mineCircleBean.getId() + "" ;
            setData();
        }else {
            circleId = "";
            etCreateFreeCircleName.addTextChangedListener(new CircleNameWatcher());
        }
    }

    /**
     * 创建讨论组
     */
    private void createChatroom() {
        saveCircle("");
    }


    /**
     * 获取默认用户数据
     */
    private void saveCircle(String chatId) {
        if (UserManager.getInstance().isLogin()) {
            getBaseLoadingView().showLoading();
            Subscription sub = AllApi.getInstance().saveCircle(UserManager.getInstance().getToken(),circleId,isPubluc,groupType,
                    etCreateFreeCircleName.getText().toString(),
                    etCreateFreeCircleContent.getText().toString(), imgUrl, 0, 0.00, "0",
                    chatId,createGroupChat).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<CreateCircleBean>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(alertDialog == null) return;
                            CreateCircleBean bean = (CreateCircleBean) data;
                            alertDialog.dismiss();
                            getBaseLoadingView().hideLoading();
                            Intent inten = new Intent(CreateFreeCircleActivity.this, EditCircleInfoActivity.class);
                            inten.putExtra("groupId", bean.getGroupId());
                            inten.putExtra("isMy", 1);
                            inten.putExtra("flag", true);
                            startActivity(inten);
                            CreateFreeCircleActivity.this.finish();
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if(alertDialog == null) return;
                            LoginErrorCodeUtil.showHaveTokenError(CreateFreeCircleActivity.this, errorCode, message);
                            alertDialog.dismiss();
                            getBaseLoadingView().hideLoading();
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this,sub);

        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);


    }


    @OnClick(R.id.rl_create_free_circle)
    public void onClick() {
        chooseImg();
    }

    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_permission), pers, 10010,
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
                        PictureConfig.getInstance().init(options).openPhoto(CreateFreeCircleActivity.this, CreateFreeCircleActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CreateFreeCircleActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CreateFreeCircleActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });


                    }
                });
    }


    public void compression(String path) {
        //将图片进行压缩
        final File file = new File(path);
        Luban.get(MyApplication.getInstance()).load(file)                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
             .setCompressListener(new OnCompressListener() {

                 // 压缩开始前调用，可以在方法内启动 loading UI
                 @Override
                 public void onStart() {}

                 // 压缩成功后调用，返回压缩后的图片文件
                 @Override
                 public void onSuccess(final File compressFile) {
                     if (UserManager.getInstance().isLogin()) {
                         File uploadFile = FileUtils.getFileSize(file) > Constant.COMPRESS_VALUE ? compressFile : file;

                         LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
                             @Override
                             public void onUploadSuccess(UploadResult result) {
                                 if(getBaseLoadingView() == null) return;
                                 Uri uri = Uri.fromFile(file);
                                 ivCreateFreeName.setImageURI(uri);
                                 imgUrl = result.getUrl();
                                 getBaseLoadingView().hideLoading();
                             }

                             @Override
                             public void onUploadFailed(String errorCode, String msg) {
                                 if(getBaseLoadingView() == null) return;
                                 LoginErrorCodeUtil.showHaveTokenError(CreateFreeCircleActivity.this, errorCode, msg);
                                 getBaseLoadingView().hideLoading();
                             }
                         }, null, uploadFile);

                     } else {
                         GotoUtil.goToActivity(CreateFreeCircleActivity.this, LoginAndRegisteActivity.class);
                         getBaseLoadingView().hideLoading();
                     }
                 }

                 //  当压缩过去出现问题时调用
                 @Override
                 public void onError(Throwable e) {
                     getBaseLoadingView().hideLoading();
                     ToastUtil.showShort(CreateFreeCircleActivity.this, e.toString());
                 }
             }).launch();
    }


    public void setData(){
        etCreateFreeCircleName.setText(mineCircleBean.getGroupName());
        etCreateFreeCircleContent.setText(mineCircleBean.getContent());
        imgUrl = mineCircleBean.getCover();
        ImageHelper.loadImage(this,ivCreateFreeName,imgUrl);
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        getBaseLoadingView().showLoading();
        compression(media.getPath());
    }

    /**
     * 圈子名称监听
     */
    class CircleNameWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkOutCircleName(s);
        }
    }

    private void checkOutCircleName(final Editable s) {

        Subscription sub = AllApi.getInstance().checkOutCircleName(s.toString()).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if ("10138".equals(errorCode)) {
                            ToastUtil.showLong(CreateFreeCircleActivity.this, "\"" + s.toString() + "\"" + "已经存在");
                            return;
                        } else {
                            ToastUtil.showShort(CreateFreeCircleActivity.this, message);
                        }
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
    }
}
