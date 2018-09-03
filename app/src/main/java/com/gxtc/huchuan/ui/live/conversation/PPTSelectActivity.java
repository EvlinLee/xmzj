package com.gxtc.huchuan.ui.live.conversation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PPTSelectAdapter;
import com.gxtc.huchuan.bean.UploadPPTFileBean;
import com.gxtc.huchuan.bean.event.EventPPTBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.widget.DefaultItemTouchHelpCallback;
import com.gxtc.huchuan.widget.DefaultItemTouchHelper;
import com.gxtc.huchuan.widget.RecyclerSpace;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by sjr on 2017/4/10.
 * PPT素材选择
 * 2017/4/11
 * 进来先从服务器获取展示然后保存然后选择本地的时候跟保存的一起传到服务器
 */

public class PPTSelectActivity extends BaseTitleActivity implements
        PictureConfig.OnSelectResultCallback {
    private static final String TAG          = "PPTSelectActivity";
    public static final  int    REQUEST_IMAG = 2333;

    @BindView(R.id.headBackButton) ImageButton  headBackButton;
    @BindView(R.id.cb_editor)      CheckBox     cbEditor;
    @BindView(R.id.rl_ppt_select)  RecyclerView mRecyclerView;
    @BindView(R.id.btn_ppt_select) Button       btnPptSelect;
    @BindView(R.id.ll_ppt_select)  LinearLayout llPptSelect;
    @BindView(R.id.tv_ppt_intro)   TextView     tvIntro;


    public static final int REQUEST_CODE_IMG = 10000;

    private ArrayList<String> pathList = new ArrayList<>();//图片选择器选择之后的图片路径
    private List<File>        files    = new ArrayList<>();//图片压缩后的文件
    private List<String>      fileUrl  = new ArrayList<>();//上传成功后服务器返回的图片列表

    private String chatInfoId;//直播课程id
    private String token;


    private PPTSelectAdapter mAdapter;

    private List<UploadPPTFileBean> mDatas;

    //滑动拖拽的帮助类
    private DefaultItemTouchHelper itemTouchHelper;
    private boolean                isPPTModel;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppt_select);
    }

    @Override
    public void initData() {
        mDatas = new ArrayList<>();

        Intent intent = getIntent();
        chatInfoId = intent.getStringExtra("chatInfoId");
        String model = intent.getStringExtra("model");
        if (model != null) {
            isPPTModel = Boolean.parseBoolean(model);
        }
        token = UserManager.getInstance().getToken();
        if (!"".equals(chatInfoId) && !"".equals(token))
            getChatInfoSlideList();
        else
            GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, Constant.requestCode.UPLOAD_KEJIAN);
    }

    /**
     * 获取服务器上的课件数据
     */
    private void getChatInfoSlideList() {
        getBaseLoadingView().showLoading(true);
        Subscription sub =
            LiveApi.getInstance()
                   .getChatInfoSlideList(token, chatInfoId)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new ApiObserver<ApiResponseBean<List<UploadPPTFileBean>>>(
                        new ApiCallBack<List<UploadPPTFileBean>>() {
                            @Override
                            public void onSuccess(List<UploadPPTFileBean> data) {
                                if(getBaseLoadingView() == null)    return;

                                getBaseLoadingView().hideLoading();
                                setData(data);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(getBaseLoadingView() == null)    return;
                                ToastUtil.showShort(PPTSelectActivity.this, message);
                                getBaseLoadingView().hideLoading();
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void setData(List<UploadPPTFileBean> data) {
        if (data != null/* && data.size() > 0*/) {
            mDatas.addAll(data);
            initRecyclerView(mDatas);
            setEdit(mDatas);
            for (UploadPPTFileBean bean : data) {
                fileUrl.add(bean.getPicUrl());
            }

            if (isPPTModel) {

            }
            mAdapter.setOnDelItemListener(
                    new PPTSelectAdapter.OnDelItemListener() {
                        @Override
                        public void onDel(
                                BaseRecyclerAdapter.ViewHolder holder,
                                int position, UploadPPTFileBean bean) {
                            removeData(holder, position);

                        }
                    });
            mAdapter.setOnReItemOnClickListener(
                    new BaseRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            if (!mAdapter.isShowRemoveIcon()) {
                                UploadPPTFileBean uploadPPTFileBean = mDatas.get(position);
                                String picUrl = uploadPPTFileBean.getPicUrl();
                                Intent intent = new Intent();
                                intent.putExtra("clickImage", picUrl);
                                setResult(RESULT_OK, intent);

                                finish();
                            }
                        }
                    });
        }

    }

    private AlertDialog mDialog;

    private void removeData(final BaseRecyclerAdapter.ViewHolder holder, final int position) {
        mDialog = DialogUtil.showInputDialog(this, false, "", "确定删除吗", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUrl.clear();
                Log.d(TAG, mAdapter.getList().toString());
                Log.d(TAG, mDatas.toString());
                mDatas.remove(position);
//                mAdapter.removeData(holder, position);
                //这个方法好
                mAdapter.notifyItemRemoved(position);
                for (int i = 0; i < mDatas.size(); i++) {
                    fileUrl.add(mDatas.get(i).getPicUrl());
                }
                uploadCourseware(fileUrl);

                cbEditor.setChecked(false);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    private void setEdit(final List<UploadPPTFileBean> datas) {
        cbEditor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.setShowRemoveIcon(isChecked);
                if (isChecked) {
                    cbEditor.setText("完成");
                    tvIntro.setText("长按拖动图片调整顺序");
                    llPptSelect.setVisibility(View.GONE);


                    itemTouchHelper.setDragEnable(true);
                    itemTouchHelper.setSwipeEnable(true);


                } else {
                    cbEditor.setText("编辑");
                    tvIntro.setText("点击编辑可以对素材进行处理");
                    llPptSelect.setVisibility(View.VISIBLE);

                    itemTouchHelper.setDragEnable(false);
                    itemTouchHelper.setSwipeEnable(false);
                }
                mAdapter.notifyDataSetChanged();

            }
        });
    }


    @OnClick({R.id.headBackButton, R.id.btn_ppt_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                this.finish();
                break;
            case R.id.btn_ppt_select:
                pathList.clear();
                files.clear();
                chooseImg();
                break;
        }
    }

    private int count = 0;

    /**
     * 压缩图片
     */
    public void compression(final int position) {
        //将图片进行压缩
        File file = new File(pathList.get(position));

        Luban.get(MyApplication.getInstance()).load(file)                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
             .setCompressListener(new OnCompressListener() {

                 // 压缩开始前调用，可以在方法内启动 loading UI
                 @Override
                 public void onStart() {}

                 // 压缩成功后调用，返回压缩后的图片文件
                 @Override
                 public void onSuccess(File file) {
//                     getBaseLoadingView().hideLoading();
                     files.add(file);

                     if (count == pathList.size() - 1) {
                         count = 0;
                         uploadingFile(files);

                     } else {
                         count++;
                         compression(count);
                     }

                 }

                 //  当压缩过去出现问题时调用
                 @Override
                 public void onError(Throwable e) {
                     getBaseLoadingView().hideLoading();
                     ToastUtil.showShort(PPTSelectActivity.this, e.toString());
                 }
             }).launch();
    }

    /**
     * 上传IM文件
     */
    public void uploadingFile(List<File> files) {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);

        Observable<ApiResponseBean<List<String>>> observable = LoadHelper.loadImFile(map, "image/png",
                new UIProgressListener() {
                    @Override
                    public void onUIStart(long currentBytes, long contentLength, boolean done) {
//                        Log.d(TAG, "开始上传");
//                        if (mProgressDialog != null) {
//                            mProgressDialog.dismiss();
//                        }
//                        mProgressDialog = new ProgressDialog(PPTSelectActivity.this);
//                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                        mProgressDialog.setMessage("文件正在努力上传中，请稍等...");
//                        mProgressDialog.setProgress(100);
//                        mProgressDialog.setCancelable(true);
//                        mProgressDialog.show();
                    }

                    @Override
                    public void onUIProgress(long currentBytes, long contentLength, boolean done) {

                    }

                    @Override
                    public void onUIFinish(long currentBytes, long contentLength, boolean done) {

                    }
                }, files.toArray(new File[]{}));


        Subscription sub =
            observable.subscribe(
                new ApiObserver<ApiResponseBean<List<String>>>(new ApiCallBack<List<String>>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        if(getBaseLoadingView() == null)    return;
                        for (String s : data) {
                            fileUrl.add(s);
                        }
                        uploadCourseware(fileUrl);

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(getBaseLoadingView() == null)    return;
                        ToastUtil.showShort(PPTSelectActivity.this, message);
                        getBaseLoadingView().hideLoading();
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);

    }


    /**
     * 保存直播课程课件接口
     */
    private void uploadCourseware(List<String> data) {

        String substring = data.toString().substring(1, data.toString().length() - 1);

        RequestBody requestBody = new MultipartBody.Builder().setType(
                MultipartBody.FORM).addFormDataPart("token", token)
                .addFormDataPart("chatInfoId", chatInfoId)
                .addFormDataPart("picUrls", substring).build();

        Subscription sub =
            LiveApi.getInstance()
                   .uploadCourseware(requestBody)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new ApiObserver<ApiResponseBean<List<UploadPPTFileBean>>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(mDatas == null)  return;
                            mDatas.clear();
                            mDatas = (List<UploadPPTFileBean>) data;
                            getBaseLoadingView().hideLoading();

                           /* if (mDatas.size() > 0)*/ mAdapter.notifyChangeData(mDatas);
                            EventBusUtil.post(new EventPPTBean(mDatas));
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if(getBaseLoadingView() == null)    return;
                            ToastUtil.showShort(PPTSelectActivity.this, message + "错误码：" + errorCode);
                            getBaseLoadingView().hideLoading();
                        }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);

    }

    private void initRecyclerView(List<UploadPPTFileBean> datas) {
        mAdapter = new PPTSelectAdapter(this, datas, R.layout.item_ppt_select);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new RecyclerSpace(20, Color.WHITE));
        mRecyclerView.setAdapter(mAdapter);

        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    /**
     * 选择图片
     */
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_permission), pers, REQUEST_CODE_IMG,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options =
                                new FunctionOptions.Builder()
                                        .setType(FunctionConfig.TYPE_IMAGE)
                                        .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                                        .setImageSpanCount(3)
                                        .setEnableQualityCompress(false) //是否启质量压缩
                                        .setEnablePixelCompress(false) //是否启用像素压缩
                                        .setEnablePreview(true) // 是否打开预览选项
                                        .setShowCamera(true)
                                        .setPreviewVideo(true)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(PPTSelectActivity.this, PPTSelectActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(PPTSelectActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(PPTSelectActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }


    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {

        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (mDatas != null) {
                for (int i = 0; i < mDatas.size(); i++) {
                    mDatas.get(i).setIsClick("0");
                }
                // 更换数据源中的数据Item的位置
                Collections.swap(mDatas, srcPosition, targetPosition);

                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mAdapter.notifyItemMoved(srcPosition, targetPosition);
                List<String> newDatas = new ArrayList<>();
                for (UploadPPTFileBean bean : mDatas) {
                    newDatas.add(bean.getPicUrl());
                }
                uploadCourseware(newDatas);
                return true;
            }
            return false;
        }

        @Override
        public boolean onSelected(int adapterPosition) {
            return false;
        }
    };


    @Override
    protected void onDestroy() {

        if (fileUrl != null && fileUrl.size() > 0) fileUrl.clear();
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
        super.onDestroy();
    }


    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {
        getBaseLoadingView().showLoading();
        for(LocalMedia media: resultList){
            pathList.add(media.getPath());
        }
        compression(0);
    }

    @Override
    public void onSelectSuccess(LocalMedia media) {

    }
}
