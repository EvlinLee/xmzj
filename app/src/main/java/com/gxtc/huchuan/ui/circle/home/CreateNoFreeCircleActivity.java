package com.gxtc.huchuan.ui.circle.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
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
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity;
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
 * Created by sjr on 2017/4/27.
 * 创建付费圈子
 */

public class CreateNoFreeCircleActivity extends BaseTitleActivity implements
        PictureConfig.OnSelectResultCallback {


    @BindView(R.id.iv_create_nofree_name)             ImageView      ivCreateNofreeName;
    @BindView(R.id.iv_create_nofree_circle_selectimg) ImageView      ivCreateNofreeCircleSelectimg;
    @BindView(R.id.rl_create_nofree_circle)           RelativeLayout rlCreateNofreeCircle;
    @BindView(R.id.et_create_nofree_circle_name)      EditText       etCreateNofreeCircleName;
    @BindView(R.id.et_create_nofree)                  EditText       etCreateNofree;
    @BindView(R.id.et_create_nofree_circle_content)   EditText       etCreateNofreeCircleContent;
    @BindView(R.id.et_fenxiao)                        EditText       etFenxiao;

    private String circleId = "";//默认创建圈子时为空
    private String imgUrl;
    AlertDialog alertDialog;
    private MineCircleBean mineCircleBean;
    private int isPubluc;
    private int groupType;
    private AlertDialog mAlertDialog;
    public int createGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_nofree_circle);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNoFreeCircleActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_create_nofree_circle));
        getBaseHeadView().showHeadRightButton("审核", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etCreateNofreeCircleName.getText())) {
                    ToastUtil.showShort(CreateNoFreeCircleActivity.this, "圈子名称不能为空");
                    return;
                }

                if (etCreateNofreeCircleName.getText().toString().length() > 8) {
                    ToastUtil.showShort(CreateNoFreeCircleActivity.this, "圈子名称不能超过8个字");
                    return;
                }

                if (TextUtils.isEmpty(etCreateNofree.getText())) {
                    ToastUtil.showShort(CreateNoFreeCircleActivity.this, "请填写收费标准");
                    return;
                }

                if (TextUtils.isEmpty(imgUrl)) {
                    ToastUtil.showShort(CreateNoFreeCircleActivity.this, "请上传封面");
                    return;
                }

                if(!TextUtils.isEmpty(etFenxiao.getText().toString())){
                    if ((Double.valueOf(etFenxiao.getText().toString()) > 70 || Double.valueOf(etFenxiao.getText().toString()) < 5)) {
                        ToastUtil.showShort(CreateNoFreeCircleActivity.this, "分销比例不能小于5或是超过70");
                        return;
                    }
                }
                alertDialog = DialogUtil.showCircleDialog(CreateNoFreeCircleActivity.this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createChatroom();
                            }
                        });
            }
        });

    }

    @Override
    public void initData() {
        mineCircleBean=getIntent().getParcelableExtra("data");
        isPubluc = getIntent().getIntExtra("isPubluc",-1);
        groupType = getIntent().getIntExtra("groupType",-1);
        createGroupChat = getIntent().getIntExtra("createGroupChat",0);
        if(mineCircleBean != null){
            circleId = mineCircleBean.getId() + "";
            setData();
        }else {
            circleId = "";
            etCreateNofreeCircleName.addTextChangedListener(new CircleNameWatcher());
        }
        if ("0".equals(UserManager.getInstance().getIsRealAudit())) {
            showDialog();
        } else if ("2".equals(UserManager.getInstance().getIsRealAudit())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setCancelable(false);
            dialog.setMessage("您提交的实名认证还在审核中，不能创建付费圈子！");
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreateNoFreeCircleActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }
    }

    private void setData() {
        etCreateNofreeCircleName.setText(mineCircleBean.getGroupName());
        etCreateNofreeCircleContent.setText(mineCircleBean.getContent());
        etCreateNofree.setText(mineCircleBean.getFee()+"");
        etCreateNofree.setText(mineCircleBean.getFee()+"");
        etFenxiao.setText(mineCircleBean.getPent()+"");
        imgUrl = mineCircleBean.getCover();
        ImageHelper.loadImage(this,ivCreateNofreeName,imgUrl);
    }

    private void showDialog() {
        DialogUtil.VerificationDialog(this, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_issue) {
                    GotoUtil.goToActivityForResult(CreateNoFreeCircleActivity.this,
                            VertifanceActivity.class, 100);
                    CreateNoFreeCircleActivity.this.finish();
                } else {
                    CreateNoFreeCircleActivity.this.finish();
                    ToastUtil.showShort(CreateNoFreeCircleActivity.this, "没有实名认证不能创建收费圈子！");
                }
            }
        });

    }

    @Override
    public void initListener() {
        etCreateNofree.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                    int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index   = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});
    }

    /**
     * 创建讨论组
     */
    private void createChatroom() {
        saveCircle("");
        /*String       name   = etCreateNofreeCircleName.getText().toString();
        List<String> userId = new ArrayList<>();
        userId.add(UserManager.getInstance().getUserCode());
        userId.add("10086");
        RongIM.getInstance().createDiscussion(name, userId,
                new RongIMClient.CreateDiscussionCallback() {
                    @Override
                    public void onSuccess(String s) {
                        //创建圈子
                        saveCircle(s);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        if (errorCode == RongIMClient.ErrorCode.RC_CONN_USER_OR_PASSWD_ERROR) {
                            UserManager.getInstance().isLogin(CreateNoFreeCircleActivity.this);
                        }
                    }
                });*/


    }

    String fenxiao;

    private void saveCircle(String chatId) {

        if (UserManager.getInstance().isLogin()) {

            getBaseLoadingView().showLoading();
            if (!TextUtils.isEmpty(etFenxiao.getText().toString().trim())){
                fenxiao = etFenxiao.getText().toString();
            }else {
                fenxiao = "0";
            }
            Subscription sub = AllApi.getInstance().saveCircle(UserManager.getInstance().getToken(),circleId,isPubluc,groupType,
                    etCreateNofreeCircleName.getText().toString(),
                    etCreateNofreeCircleContent.getText().toString(), imgUrl, 1,
                    Double.valueOf(etCreateNofree.getText().toString()), fenxiao,
                    chatId,createGroupChat).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<CreateCircleBean>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(alertDialog == null) return;
                            CreateCircleBean bean = (CreateCircleBean) data;
                            getBaseLoadingView().hideLoading();
                            alertDialog.dismiss();
                            Intent inten = new Intent(CreateNoFreeCircleActivity.this,
                                    EditCircleInfoActivity.class);
                            inten.putExtra("groupId", bean.getGroupId());
                            inten.putExtra("isMy", 1);
                            inten.putExtra("flag", true);
                            startActivity(inten);
                            CreateNoFreeCircleActivity.this.finish();
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if(getBaseLoadingView() == null)    return;
                            getBaseLoadingView().hideLoading();
                            if ("10128".equals(errorCode)) {
                                ToastUtil.showShort(CreateNoFreeCircleActivity.this, "您创建圈子的数量超出系统允许的最大数");
                            } else LoginErrorCodeUtil.showHaveTokenError(
                                    CreateNoFreeCircleActivity.this, errorCode, message);
                            alertDialog.dismiss();
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this,sub);
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);


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
                            ToastUtil.showLong(CreateNoFreeCircleActivity.this, "\"" + s.toString() + "\"" + "已经存在");
                        } else {
                            ToastUtil.showShort(CreateNoFreeCircleActivity.this, message);
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @OnClick(R.id.rl_create_nofree_circle)
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
                        PictureConfig.getInstance().init(options).openPhoto(CreateNoFreeCircleActivity.this, CreateNoFreeCircleActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CreateNoFreeCircleActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CreateNoFreeCircleActivity.this);
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
        Luban.get(MyApplication.getInstance())
             .load(file)                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
             .setCompressListener(new OnCompressListener() {

                 // 压缩开始前调用，可以在方法内启动 loading UI
                 @Override
                 public void onStart() {}

                 // 压缩成功后调用，返回压缩后的图片文件
                 @Override
                 public void onSuccess(File compressFile) {
                     if (UserManager.getInstance().isLogin()) {
                         File uploadFile = FileUtils.getFileSize(file) > Constant.COMPRESS_VALUE ? compressFile : file;

                         LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
                             @Override
                             public void onUploadSuccess(UploadResult result) {
                                 if(getBaseLoadingView() == null) return;
                                 Uri uri = Uri.fromFile(file);
                                 ivCreateNofreeName.setImageURI(uri);
                                 imgUrl = result.getUrl();
                                 getBaseLoadingView().hideLoading();
                             }

                             @Override
                             public void onUploadFailed(String errorCode, String msg) {
                                 if(getBaseLoadingView() == null) return;
                                 LoginErrorCodeUtil.showHaveTokenError(CreateNoFreeCircleActivity.this, errorCode, msg);
                                 getBaseLoadingView().hideLoading();
                             }
                         }, null, uploadFile);


                     } else {
                         GotoUtil.goToActivity(CreateNoFreeCircleActivity.this, LoginAndRegisteActivity.class);
                         getBaseLoadingView().hideLoading();
                     }


                 }

                 //  当压缩过去出现问题时调用
                 @Override
                 public void onError(Throwable e) {
                     getBaseLoadingView().hideLoading();
                     ToastUtil.showShort(CreateNoFreeCircleActivity.this, e.toString());
                 }
             }).launch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
    }
}
