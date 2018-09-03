package com.gxtc.huchuan.ui.circle.dynamic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.IssueDynamicAdapter;
import com.gxtc.huchuan.bean.IssueDynamicBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventCircleIssueDeletePhotoBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.customemoji.fragment.EmotionMainFragment;
import com.gxtc.huchuan.customemoji.utils.GlobalOnItemClickManagerUtils;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.CircleRecyclerView;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.UpImageService.ACTION_UPLOAD_IMG;
import static com.gxtc.huchuan.ui.circle.dynamic.UpImageService.EXTRA_IMG_PATH;
import static com.gxtc.huchuan.ui.circle.dynamic.UpImageService.EXTRA_TYPE;

/**
 * Created by 宋家任 on 2017/5/14.
 * 发布动态
 * 逻辑有些乱 代码也待优化
 * 2017/5/20删除图片有bug bug已解决
 * 2017/5/25 代码优化了一丢丢 其实图片选择框架已经集成了鲁班的压缩，但是原先没用，代码暂时不改后期优化再说
 * 2018/5/28 优化代码逻辑，删掉本地压缩
 */

public class IssueDynamicActivity extends BaseTitleActivity {

    @BindView(R.id.et_issue_dynamic)
    EditText etContent;
    @BindView(R.id.rv_issue_img)
    CircleRecyclerView mRecyclerView;
    @BindView(R.id.cb_issue_dynamic)
    CheckBox cbFriend;
    @BindView(R.id.tv_issue_circle)
    TextView tvIssueCircle;
    @BindView(R.id.tv_issue_tongbu)
    TextView tvIssueTongbu;
    @BindView(R.id.fl_emotionview_main)
    View emotionview;
    @BindView(R.id.rootview)
    RelativeLayout rootview;

    @BindView(R.id.layout_share)
    View layoutShare;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.tv_share)
    TextView tvShareTitle;
    @BindView(R.id.tx_note)
    TextView tvNote;
    @BindView(R.id.tv_note_status)
    TextView tvNoteStatus;


    private String type;            //0:从圈子进来的，1：从个人动态进来的
    private String selectType;      //1拍摄视频页回退 2照相 3直接文字进来的
    private int id = -1;            //圈子id
    private int allowMaxNum;        //还可以选多少张
    private final int MaxNum = 9;
    ;  //最多可以选9张
    private boolean isSource = false;  //是否发送原图
    private Intent intent;
    private Intent intentService;

    Subscription subIssue;
    Subscription sub;
    public static final long maxLen_500k = 1 * 1024 * 1024 / 2;
    public static final String FIRST_SENT_DYNAMIC = "first_sent_dynamic";
    private ArrayList<String> pathList = new ArrayList<>();         //图片选择器选择之后的图片路径

    private ArrayList<IssueDynamicBean> mDatas = new ArrayList<>();  //未压缩前
        private ArrayList<IssueDynamicBean> mCompDatas = new ArrayList<>();  //压缩后
    private ArrayList<String> picUrls = new ArrayList<>();  //后台返回的图片地址
    private ArrayList<Integer> mGroupIds = new ArrayList<>();  //同步的圈子id
    private IssueDynamicAdapter mAdapter;
    private EmotionMainFragment emotionMainFragment;

    private List<LocalMedia> selectMedia = new ArrayList<>();

    private String defaultUrl;          //链接显示不同颜色
    public String onlyGroupLook = "0";
    public String friend = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_issue_dynamic);
        AndroidBug5497Workaround.assistActivity(this);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_issue_dynamic));

        if (etContent == null) return;

        etContent.setFocusable(true);
        etContent.setFocusableInTouchMode(true);
        etContent.requestFocus();
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(etContent, 0);
    }


    @Override
    public void initData() {
        initEmotionMainFragment();
        initRecyclerView();
        selectType = intent.getStringExtra("select_type");

        //录制短视频
        if ("1".equals(selectType)) {
            gotoVideo();

            //拍摄或相册
        } else if ("2".equals(selectType)) {
            gotoImg();

            //直接文字
        } else if ("3".equals(selectType)) {
            defaultUrl = intent.getStringExtra("default_url");

            //圈内分享图片/二维码
        } else if ("5".equals(selectType)) {
            mRecyclerView.setVisibility(View.VISIBLE);
            layoutShare.setVisibility(View.GONE);
            String imgUrl = getIntent().getStringExtra("picUrl");
            pathList.clear();
            mDatas.clear();
            pathList.add(imgUrl);
            IssueDynamicBean bean = new IssueDynamicBean();
            bean.setFile(new File(imgUrl));
            mDatas.add(bean);
            compressionImg(0);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            layoutShare.setVisibility(View.VISIBLE);
            View linearlayoutShare = findViewById(R.id.linear_lauout_share);
            FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams) linearlayoutShare.getLayoutParams();
            parms.leftMargin = getResources().getDimensionPixelSize(R.dimen.margin_middle);
            parms.rightMargin = getResources().getDimensionPixelSize(R.dimen.margin_middle);
            String title = getIntent().getStringExtra("title");
            String imgUrl = getIntent().getStringExtra("picUrl");
            ImageHelper.loadImage(this, imgShare, imgUrl, R.drawable.live_list_place_holder_120);
            tvShareTitle.setText(title);
        }


        type = intent.getStringExtra("type");
        tvNoteStatus.setText("仅好友可见");
        tvNote.setText("选择仅好友可见，该动态只有你的好友能看到，你的粉丝将看不到.");
        //从圈子主页跳转过来
        if ("0".equals(type)) {
            id = getIntent().getIntExtra(Constant.INTENT_DATA, -1);
            tvIssueCircle.setVisibility(View.VISIBLE);
            tvIssueCircle.setText("同步到:" + getIntent().getStringExtra("data_name"));
            mGroupIds.add(id);
            tvNoteStatus.setText("仅圈友可见");
            tvNote.setText("选择仅圈友可见，该动态只有圈友可见，你的好友和粉丝将看不到");
        }

        if (!"".equals(defaultUrl)) {
            etContent.setText(defaultUrl);
        }
    }

    /**
     * 初始化表情面板
     */
    public void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT, false);
        //隐藏控件
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN, true);
        //替换fragment
        //创建修改实例
        emotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class, bundle);
        emotionMainFragment.bindToContentView(etContent);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in thefragment_container view with this fragment,
        // and add the transaction to the backstack
        transaction.replace(R.id.fl_emotionview_main, emotionMainFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }


    private void gotoVideo() {
        FunctionOptions options =
                new FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_VIDEO)
                        .setRecordVideoDefinition(1)
                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                        .setShowCamera(true)
                        .setImageSpanCount(3)
                        .setPreviewVideo(true)
                        .create();
        PictureConfig.getInstance().init(options).openPhoto(this, resultCallback);
    }

    private void gotoImg() {
        FunctionOptions options =
                new FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_IMAGE)
                        .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                        .setMaxSelectNum(allowMaxNum)
                        .setImageSpanCount(3)
                        .setEnableQualityCompress(true) //是否启质量压缩
                        .setEnablePixelCompress(true) //是否启用像素压缩
                        .setMaxB(Constant.COMPRESS_VALUE)//超过这个值才压缩 1m
                        .setCompressFlag(2)//使用鲁班
                        .setCompress(true)//压缩
                        .setEnablePreview(true) // 是否打开预览选项
                        .setEnableSource(true)  //显示勾选原图
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
                isSource = media.isSource();
                pathList.add(media.getPath());
            }
            if (mDatas.size() == 1) {
                mDatas.clear();
            } else if (mDatas.size() <= 9 && mDatas.size() > 1 && mDatas.get(mDatas.size() - 1).getFile() == null) {
                mDatas.remove(mDatas.size() - 1);
            }
            for (String s : pathList) {
                IssueDynamicBean bean = new IssueDynamicBean();
                bean.setFile(new File(s));
                if (mDatas.size() <= 9)
                    mDatas.add(bean);
            }
            //获取图片路径进行压缩
            getBaseLoadingView().showLoading();
            compressionImg(0);
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            if (media != null) {
                String filepath = media.getPath();
                File file = new File(filepath);
                IssueDynamicBean bean = new IssueDynamicBean();
                bean.setFile(file);
                bean.setType("video");
                mDatas.clear();
                mDatas.add(bean);
                initRecyclerView();
            }
        }
    };


    @Override
    public void initListener() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCloseDialog();
            }
        });
        getBaseHeadView().showHeadRightButton("发表", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick()) return;
                WindowUtil.closeInputMethod(IssueDynamicActivity.this);
                boolean isFirstSentDynamic = SpUtil.getBoolean(IssueDynamicActivity.this, FIRST_SENT_DYNAMIC);
                if (isFirstSentDynamic) {
                    mAlertDialog = DialogUtil.showNoticeDialog(IssueDynamicActivity.this, true, "温馨提示",
                            getResources().getString(R.string.dynamic_rule),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                    //防止因手抖多点一次
                                    if (!ClickUtil.isFastClick()) {
                                        sentDynimicByTime();
                                    }
                                }
                            });
                } else {
                    sentDynimicByTime();
                }
            }
        });
    }

    private void initRecyclerView() {
        getBaseLoadingView().hideLoading();
        if (mDatas.size() < 9 && !("1".equals(selectType))) {
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
        allowMaxNum = MaxNum - mAdapter.getList().size() + 1;       //设置可以选多少张图一片
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (ClickUtil.isFastClick()) return;
                //发表的是图片
                if (!"1".equals(selectType)) {
                    if (mDatas.size() == 9) {
                        if (position == mDatas.size() - 1 && mDatas.get(position).getFile() == null) {
                            gotoImg();
                        } else {
                            Intent intent = new Intent(IssueDynamicActivity.this, CirclePhothViewActivity.class);
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
                        Intent intent = new Intent(IssueDynamicActivity.this, CirclePhothViewActivity.class);
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
                    } else if (mDatas.size() > 1) {
                        gotoImg();
                    } else {
                        showSelectDialog();
                    }
                } else {
                    if (mDatas.size() == 1) {
                        if (mDatas.get(0).getFile() != null)
                            PictureConfig.getInstance().externalPictureVideo(IssueDynamicActivity.this, mDatas.get(0).getFile().getPath());
                        else {
                            selectType = "2";
                            showSelectDialog();
                        }
                    }
                }
            }
        });
    }

    private AlertDialog mAlertDialog;

    //返回上一个界面
    private void showCloseDialog() {
        String text = "";
        if (intentService == null) {
            text = "退出此次编辑?";
        } else {
            text = "图片还在上传，确定退出此次编辑?";
        }
        mAlertDialog = DialogUtil.showDeportDialog(this, false, null, text,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            if (intentService != null) {
                                stopService(intentService);
                            }
                            IssueDynamicActivity.this.finish();
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }

    private ListDialog mListDialog;

    public void showSelectDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this, new String[]{"图片", "视频"});
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        //拍摄或相册
                        case 0:
                            gotoImg();
                            break;

                        //录像或本地视频
                        case 1:
                            selectType = "1";
                            gotoVideo();
                            break;
                    }
                }
            });
        }
        mListDialog.show();
    }


    private int count = 0;


    /**
     * 压缩图片
     */
    public void compressionImg(int position) {
        getBaseHeadView().getHeadRightButton().setFocusable(false);
        getBaseHeadView().getHeadRightButton().setEnabled(false);

        //将图片进行压缩
        final File file = new File(pathList.get(position));
        Subscription sub =
            Luban.get(MyApplication.getInstance().getApplicationContext())
                 .load(file)
                 .putGear(Luban.THIRD_GEAR)
                 .asObservable()
                 .subscribeOn(Schedulers.newThread())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Action1<File>() {
                        @Override
                        public void call(File compressFile) {
                          if(!isSource && FileUtil.getSize(file) > Constant.COMPRESS_VALUE ){
                              setFileList(compressFile);
                          }else {
                              setFileList(file);
                          }
                        }
                    });
        RxTaskHelper.getInstance().addTask(this, sub);
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

    BitmapFactory.Options options = new BitmapFactory.Options();


    //间隔一分钟发一次动态
    public void sentDynimicByTime() {
        if ((System.currentTimeMillis() - SpUtil.getTime(getApplicationContext(), "current_time")) > 1000 * 60) {
            if (mAdapter.getItemCount() <= 9) {
                //拍视频
                if ("1".equals(selectType)) {
                    uploadFile(mDatas.get(0).getFile());
                    return;
                }
                if (mCompDatas.size() > 0) {
                    getBaseLoadingView().showLoading();
                    startUploadImg(this, mCompDatas, selectType);    //现在改为后台服务上传图片
                } else
                    issueDynamic(new ArrayList<String>(), 0, 0, "img");
            } else {
                ToastUtil.showShort(MyApplication.getInstance(), "图片最多可以发9张");
            }
        } else {
            ToastUtil.showShort(MyApplication.getInstance(), "一分钟内只能发一次动态");
        }
    }

    public void startUploadImg(Context context, ArrayList<IssueDynamicBean> paths, String type) {
        intentService = new Intent(context, UpImageService.class);
        intentService.setAction(ACTION_UPLOAD_IMG);
        intentService.putExtra(EXTRA_IMG_PATH, paths);
        intentService.putExtra(EXTRA_TYPE, type);
        context.startService(intentService);
    }


    /**
     * 发表
     */
    private void issueDynamic(List<String> datas, int wide, int height, String upType) {
        if (TextUtils.isEmpty(etContent.getText().toString()) && datas.size() == 0 && !"4".equals(selectType)) {
            ToastUtil.showShort(MyApplication.getInstance(), "动态内容不能为空");
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        //圈子ID不空，friend就传0（仅圈友可见）
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
            if (cbFriend.isChecked()) {
                onlyGroupLook = "1";
            } else {
                onlyGroupLook = "0";
            }
            friend = "0";
            map.put("friend", friend);
            map.put("onlyGroupLook", onlyGroupLook);
        } else {
            //圈子ID为空，onlyGroupLook就传0（仅好友可见）
            if (cbFriend.isChecked()) {
                friend = "1";
            } else {
                friend = "0";
            }
            onlyGroupLook = "0";
            map.put("friend", friend);
            map.put("onlyGroupLook", onlyGroupLook);
        }

        if ("video".equals(upType)) {
            if (datas.size() >= 2) {
                String videoUrl = datas.get(0);
                map.put("videoUrl", videoUrl);

                String videoPic = datas.get(1);
                map.put("videoPic", videoPic);
            }
        }
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", type);
        map.put("title", "");

        String content = etContent.getText().toString();
        if (!TextUtils.isEmpty(content) && content.length() > 1500) {
            ToastUtil.showShort(this, "发表内容不能超过1500字");
            return;
        }
        map.put("content", content);

        //圈内分享
        if ("4".equals(selectType)) {
            String infoType = getIntent().getStringExtra("infoType");
            String typeId = getIntent().getStringExtra("typeId");
            map.put("infoType", infoType);
            map.put("typeId", typeId);
        }

        if (datas.size() > 0 && "img".equals(upType)) {
            if (datas.size() == 1) {
                String substring = datas.toString().substring(1, datas.toString().length() - 1);
                map.put("picUrls", substring + "?" + wide + "*" + height);
            } else {
                String substring = datas.toString().substring(1, datas.toString().length() - 1);
                map.put("picUrls", substring);
            }
        }

        getBaseHeadView().getHeadRightButton().setFocusable(false);
        getBaseHeadView().getHeadRightButton().setEnabled(false);
        getBaseLoadingView().showLoading();

        subIssue =
                CircleApi.getInstance()
                        .issueDynamic(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if (getBaseLoadingView() == null) return;
                                SpUtil.putTime(getApplicationContext(), "current_time", System.currentTimeMillis());
                                SpUtil.putBoolean(IssueDynamicActivity.this, FIRST_SENT_DYNAMIC, false);
                                setResult(Constant.ResponseCode.CIRCLE_ISSUE);
                                getBaseLoadingView().hideLoading();
                                if ("5".equals(selectType))
                                    EventBusUtil.post(new EventLoginBean(EventLoginBean.LOGIN, "", ""));
                                IssueDynamicActivity.this.finish();
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if (getBaseLoadingView() == null) return;
                                LoginErrorCodeUtil.showHaveTokenError(IssueDynamicActivity.this, errorCode, message);
                                getBaseLoadingView().hideLoading();
                                getBaseHeadView().getHeadRightButton().setFocusable(true);
                                getBaseHeadView().getHeadRightButton().setEnabled(true);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this, subIssue);
    }

    @OnClick(R.id.tv_issue_tongbu)
    public void onViewClicked() {
        Intent intent = new Intent(this, SyncIssueInCircleActivity.class);
        intent.putExtra("default", id);
        intent.putExtra(Constant.INTENT_DATA, mGroupIds);
        this.startActivityForResult(intent, 666);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            ArrayList<MineCircleBean> selectData = data.getParcelableArrayListExtra("select_data");
            mGroupIds.clear();
            if (selectData.size() > 0) {
                tvNoteStatus.setText("仅圈友可见");
                tvNote.setText("选择仅圈友可见，该动态只有圈友可见，你的好友和粉丝将看不到.");
                ArrayList<String> listGroupName = new ArrayList<>();
                for (MineCircleBean bean1 : selectData) {
                    if (!mGroupIds.contains(bean1.getId())) {
                        mGroupIds.add(bean1.getId());
                        listGroupName.add(bean1.getGroupName());
                    }
                }
                String substring = listGroupName.toString().substring(1, listGroupName.toString().length() - 1);
                tvIssueCircle.setVisibility(View.VISIBLE);
                tvIssueCircle.setText("同步到:" + substring);
            }
            if (selectData.size() == 0) {
                tvNoteStatus.setText("仅好友可见");
                tvNote.setText("选择仅好友可见，该动态只有你的好友能看到，你的粉丝将看不到.");
                tvIssueCircle.setVisibility(View.GONE);
            }
        }

        if (resultCode == RESULT_OK && data != null) {
            List<Uri> datas = (List<Uri>) data.getSerializableExtra(Constant.INTENT_DATA);
            //删除图片之后更新数据
            if (datas.size() != 0) {
                for (int i = 0; i < datas.size(); i++) {
                    for (int j = 0; j < mDatas.size(); j++) {
                        if (mDatas.get(j).getFile() != null && mDatas.get(j).getFile().toString().equals(datas.get(i).toString())) {
                            mAdapter.removeData(j);
                            mCompDatas.remove(j);
                        }
                    }
                }

                //判断图片是否够9张，不够的话添加添加按钮
                List<IssueDynamicBean> beans = mAdapter.getList();
                if (beans.size() == 0 || (beans != null && beans.size() <= MaxNum && beans.get(beans.size() - 1).getFile() != null)) {
                    IssueDynamicBean bean = new IssueDynamicBean();
                    bean.setType("img");
                    mDatas.add(bean);
                }
                mAdapter.notifyDataSetChanged();
                allowMaxNum = MaxNum - mAdapter.getList().size() + 1;       //设置可以选多少张图一片
            }
        }
    }


    private ProgressDialog mProgressDialog;

    /**
     * 这里上传视频 要在上传之前获取第一帧 并且把宽高也一起带过去 跟图片一起上传
     */
    private void uploadFile(File file) {
        getBaseHeadView().getHeadRightButton().setFocusable(false);
        getBaseHeadView().getHeadRightButton().setEnabled(false);

        if (UserManager.getInstance().isLogin() && file != null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("文件正在努力上传中，请稍等...");
            mProgressDialog.setProgress(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();


            LoadHelper.UpyunUploadVideo(file, new LoadHelper.UploadCallback() {
                @Override
                public void onUploadSuccess(UploadResult result) {
                    issueDynamic(result.getUrls(), 0, 0, "video");
                }

                @Override
                public void onUploadFailed(String errorCode, String msg) {
                    ToastUtil.showShort(IssueDynamicActivity.this, msg);
                }
            }, mProgressListener);
        }
    }

    @Subscribe
    public void onEvent(EventCircleIssueDeletePhotoBean bean) {
        List<Uri> data = bean.datas;
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < mDatas.size(); j++) {
                if (mDatas.get(j).getFile().toString().equals(data.get(i).toString())) {
                    mAdapter.removeData(j);
//                    mCompDatas.remove(j);
                    mDatas.remove(j);

                }
            }
        }
    }


    @Subscribe
    public void onEvent(ArrayList<String> datas) {
        //通过启动后台服务上传图片成功后进行回调
        if (picUrls == null) return;
        picUrls.clear();
        for (String s : datas) {
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
    public void onBackPressed() {
        if (!emotionMainFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCloseDialog();
            return false;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (pathList != null && pathList.size() > 0) {
            Luban.get(MyApplication.getInstance().getApplicationContext()).setCompressListener(null);
            pathList = null;
        }
        resultCallback = null;
        GlobalOnItemClickManagerUtils.getInstance().attachToEditText(null);
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }


    /**
     * 圈内分享
     *
     * @param targetId 对应类型的数据id
     * @param infoType 类型 0:普通动态，1：新闻连接，2：课程，3：交易 4 圈子  5 邀请管理员  6: 系列课   7: 商品详情  8: 课堂主页  9: 专题
     */
    public static void share(Activity activity, String targetId, String infoType, String title, String picUrl) {
        Intent intent = new Intent(activity, IssueDynamicActivity.class);
        intent.putExtra("select_type", "4");
        intent.putExtra("type", "1");
        intent.putExtra("infoType", infoType);
        intent.putExtra("typeId", targetId);
        intent.putExtra("title", title);
        intent.putExtra("picUrl", picUrl);
        activity.startActivityForResult(intent, 0);
    }


    private UIProgressListener mProgressListener = new UIProgressListener() {
        @Override
        public void onUIProgress(long currentBytes, long contentLength, boolean done, int position) {
            if (position == 0) {
                mProgressDialog.setProgress((int) ((100 * currentBytes) / contentLength));
                if (done) {
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                }
            }
        }
    };

}
