package com.gxtc.huchuan.ui.mine.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.IssueDynamicAdapter;
import com.gxtc.huchuan.bean.IssueDynamicBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.dynamic.CirclePhothViewActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.CircleRecyclerView;
import com.gxtc.huchuan.widget.MyGridView;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

/**
 * Created by zzg on 2017/9/20.
 */

public class ReportActivity extends BaseTitleActivity {
    @BindView(R.id.et_issue_dynamic)
    EditText mEtContent;
    @BindView(R.id.rv_issue_img)
    CircleRecyclerView mRecyclerView;
    private IssueDynamicAdapter mAdapter;
    private ArrayList<String> pathList = new ArrayList<>();         //图片选择器选择之后的图片路径

    private List<IssueDynamicBean> mDatas     = new ArrayList<>();  //未压缩前
    private List<IssueDynamicBean> mCompDatas = new ArrayList<>();  //压缩后
    private List<String>           picUrls    = new ArrayList<>();  //后台返回的图片地址
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private int allowMaxNum;        //还可以选多少张
    private final int MaxNum = 9;;//最多可以选9张
    private String reportedId;
    private String type = "";
    private int count = 0;

    public  static  void jumptoReportActivity(Context context,String reportedId,String type){
        Intent intent = new Intent(context,ReportActivity.class);
        intent.putExtra("reportedId",reportedId);
        intent.putExtra("type",type);//0，文章，1，文章评论，2，直播间，3，直播评论，4，交易，5，圈子动态，6，圈子，7、用户
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report2);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle("举报");
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightButton("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        reportedId = getIntent().getStringExtra("reportedId");
        initRecyclerView();
    }

    private void report(){
        if (TextUtils.isEmpty(mEtContent.getText().toString())) {
            ToastUtil.showShort(this,"举报内容不能为空");
            return;
        }
        if (mCompDatas.size() > 0){
            uploadingFile(mCompDatas);
        }else {
            issueDynamic(picUrls, 0, 0, "img");
        }
    }

    private void initRecyclerView() {
        getBaseLoadingView().hideLoading();
        if (mDatas.size() < 9) {
            IssueDynamicBean bean = new IssueDynamicBean();
            bean.setType("img");
            mDatas.add(bean);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new IssueDynamicAdapter(this, mDatas, R.layout.item_issue_dynamic);
        mRecyclerView.setAdapter(mAdapter);

        initRecyclerViewListener();
    }

    private void initRecyclerViewListener() {
        allowMaxNum = MaxNum - mAdapter.getList().size() + 1; //设置可以选多少张图一片
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (mDatas.size() == 9) {
                    if (position == mDatas.size() - 1 && mDatas.get(position).getFile() == null) {
                        gotoImg();
                    } else {
                        Intent intent = new Intent(ReportActivity.this, CirclePhothViewActivity.class);
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).getFile() != null) {
                                if (!"".equals(mDatas.get(i).getFile().toString()))
                                    uris.add(Uri.parse(mDatas.get(i).getFile().toString()));
                            }
                        }
                        intent.putExtra("photo", uris);
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 666);
                    }
                } else if (mDatas.size() < 9 && position != mDatas.size() - 1 && mDatas.size() != 1) {
                    Intent intent = new Intent(ReportActivity.this, CirclePhothViewActivity.class);
                    ArrayList<Uri> uris = new ArrayList<>();
                    for (int i = 0; i < mDatas.size() - 1; i++) {
                        if (mDatas.get(i).getFile() != null) {
                            if (!"".equals(mDatas.get(i).getFile().toString()))
                                uris.add(Uri.parse(mDatas.get(i).getFile().toString()));
                        }

                    }
                    intent.putExtra("photo", uris);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 666);
                } else if (mDatas.size() >= 1) {
                    gotoImg();
                }
            }
        });
    }

    private void gotoImg() {
        FunctionOptions options =
                new FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_IMAGE)
                        .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                        .setMaxSelectNum(allowMaxNum)
                        .setImageSpanCount(3)
                        .setEnableQualityCompress(false) //是否启质量压缩
                        .setEnablePixelCompress(false) //是否启用像素压缩
                        .setEnablePreview(true) // 是否打开预览选项
                        .setShowCamera(true)
                        .setPreviewVideo(true)
                        .create();
        PictureConfig.getInstance().init(options).openPhoto(this, resultCallback);
    }

    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            // 多选回调
            selectMedia.clear();
            selectMedia = resultList;
            pathList.clear();
            for (LocalMedia media : selectMedia) {
                pathList.add(media.getPath());
            }
            if (mDatas.size() == 1) {
                mDatas.clear();
            } else if (mDatas.size() <= 9 && mDatas.size() > 1 && mDatas.get(
                    mDatas.size() - 1).getFile() == null) {
                mDatas.remove(mDatas.size() - 1);
            }
            for (String s : pathList) {
                IssueDynamicBean bean = new IssueDynamicBean();
                bean.setFile(new File(s));
                if (mDatas.size() <= 9) mDatas.add(bean);
            }
            //获取图片路径进行压缩
            getBaseLoadingView().showLoading();
            compressionImg(0);

        }

        @Override
        public void onSelectSuccess(LocalMedia media) {}
    };
    /**
     * 压缩图片
     */
    public void compressionImg(int position) {
        getBaseHeadView().getHeadRightButton().setFocusable(false);
        getBaseHeadView().getHeadRightButton().setEnabled(false);
        //将图片进行压缩
        final File file = new File(pathList.get(position));
        Luban.get(MyApplication.getInstance().getApplicationContext()).load(file) .putGear
                (Luban.THIRD_GEAR)
             .asObservable().subscribeOn(Schedulers.newThread()).
                     observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<File>() {
            @Override
            public void call(File compressFile) {
                if(FileUtil.getSize(file) > maxLen_500k ){
                    setFileList(compressFile);
                }else {
                    setFileList(file);
                }
            }
        });
    }

    public void setFileList(File file){
        IssueDynamicBean bean = new IssueDynamicBean();
        bean.setFile(file);
        bean.setType("img");

        mCompDatas.add(bean);
        if (count == pathList.size() - 1) {
            count = 0;
            mDatas.clear();
            mDatas.addAll(mCompDatas);
            initRecyclerView();
            getBaseHeadView().getHeadRightButton().setFocusable(true);
            getBaseHeadView().getHeadRightButton().setEnabled(true);
            return;
        } else {
            count++;
            compressionImg(count);
        }

    }

    /**
     * 上传图片
     *
     * @param mCompDatas
     */
    BitmapFactory.Options options = new BitmapFactory.Options();
    private void uploadingFile(final List<IssueDynamicBean> mCompDatas) {
        getBaseHeadView().getHeadRightButton().setFocusable(false);
        getBaseHeadView().getHeadRightButton().setEnabled(false);
        getBaseLoadingView().showLoading(true);

        List<File> list = new ArrayList<>();
        for (IssueDynamicBean bean : mCompDatas) {
            list.add(bean.getFile());
        }

        LoadHelper.uploadFiles(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if(picUrls == null) return;

                for (String s : result.getUrls()) {
                    picUrls.add(s);
                }
                if (picUrls != null && picUrls.size() == 1) {
                    if (mCompDatas != null && mCompDatas.size() == 1) {
                        options.inJustDecodeBounds = true;
                        IssueDynamicBean bean = mCompDatas.get(0);
                        //ios那边不能自适配，得把宽高给他们
                        BitmapFactory.decodeFile(bean.getFile().getPath(), options);
                        bean.setImgWide(options.outWidth);
                        bean.setImgHeight(options.outHeight);

                        issueDynamic(picUrls, bean.getImgWide(), bean.getImgHeight(), "img");
                    }
                } else {
                    issueDynamic(picUrls, 0, 0, "img");
                }
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ToastUtil.showShort(ReportActivity.this, msg);
                getBaseLoadingView().hideLoading();
            }
        }, null, list.toArray(new File[list.size()]));
    }


    /**
     * 举报
     */
    private void issueDynamic(List<String> datas, int wide, int height, String upType) {
        getBaseHeadView().getHeadRightButton().setFocusable(false);
        getBaseHeadView().getHeadRightButton().setEnabled(false);
        getBaseLoadingView().showLoading(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", type);//0，文章，1，文章评论，2，直播间，3，直播评论，4，交易，5，圈子动态，6，圈子，7、用户
        map.put("reportedId", reportedId);//文章ID，文章评论ID，直播间ID，直播评论ID，交易信息ID，圈子动态ID
        map.put("content", mEtContent.getText().toString());
        if (datas.size() > 0 && "img".equals(upType)) {
            if (datas.size() == 1) {
                String substring = datas.toString().substring(1, datas.toString().length() - 1);
                map.put("pics", substring + "?" + wide + "*" + height);
            } else {
                String substring = datas.toString().substring(1, datas.toString().length() - 1);
                map.put("pics", substring);
            }
        }
       Subscription subIssue = AllApi.getInstance().complaintArticle(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(getBaseLoadingView() == null) return;
                        getBaseLoadingView().hideLoading();
                        ToastUtil.showShort(ReportActivity.this,"举报成功");
                        finish();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(getBaseLoadingView() == null) return;
                        LoginErrorCodeUtil.showHaveTokenError(ReportActivity.this, errorCode, message);
                        getBaseLoadingView().hideLoading();
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subIssue);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            List<Uri> datas = (List<Uri>) data.getSerializableExtra(Constant.INTENT_DATA);
            //删除图片之后更新数据
            if(datas.size() != 0){
                for (int i = 0; i < datas.size(); i++) {
                    for (int j = 0; j < mDatas.size() ; j++) {
                        if (mDatas.get(j).getFile() != null && mDatas.get(j).getFile().toString().equals(datas.get(i).toString())) {
                            mAdapter.removeData(j);
                            mCompDatas.remove(j);
                        }
                    }
                }

                //判断图片是否够9张，不够的话添加添加按钮
                List<IssueDynamicBean> beans = mAdapter.getList();
                if(beans.size() == 0 || (beans.size() <= MaxNum && beans.get(beans.size() - 1).getFile() != null)){
                    IssueDynamicBean bean = new IssueDynamicBean();
                    bean.setType("img");
                    mDatas.add(bean);
                }
                mAdapter.notifyDataSetChanged();
                allowMaxNum = MaxNum - mAdapter.getList().size() + 1;       //设置可以选多少张图一片
            }

        }

    }

}
