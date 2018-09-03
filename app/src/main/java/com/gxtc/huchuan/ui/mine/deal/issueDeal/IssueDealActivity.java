package com.gxtc.huchuan.ui.mine.deal.issueDeal;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.helper.PermissionsHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AllTypeBaen;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ColorSelectDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.dialog.MultiSelectDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.pop.PopIssueDeal;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RegexUtils;
import com.gxtc.huchuan.utils.UriUtils;
import com.gxtc.huchuan.utils.ViewFindUtils;
import com.gxtc.huchuan.widget.EditMenuView;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.gxtc.huchuan.widget.richEditor.RichEditor;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.zaaach.citypicker.CityPickerActivity;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 发布交易
 */

public class IssueDealActivity extends BaseTitleActivity implements View.OnClickListener, IssueDealContract.View,
        PictureConfig.OnSelectResultCallback, LoadHelper.UploadCallback {

    private static int REQUEST_CODE_IMAGE = 9999;
    private static int REQUEST_CODE_CITY = 8888;

    @BindView(R.id.btn_type)
    TextView btnType;
    @BindView(R.id.btn_isBuyer)
    TextView btnBuyer;
    @BindView(R.id.layout_issue)
    LinearLayout layoutUdef;
    @BindView(R.id.root_view)
    View rootView;

    @BindView(R.id.layout_cover)
    RelativeLayout layoutCover;
    @BindView(R.id.layout_upload)
    RelativeLayout layoutUpload;
    @BindView(R.id.img_cover)
    ImageView imgCover;

    @BindView(R.id.layout_money)
    View layoutMoney;
    @BindView(R.id.layout_type)
    View layoutType;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.line2)
    View line2;

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_price)
    EditText editPrice;
    @BindView(R.id.edit_title)
    EditText editTitle;

    @BindView(R.id.edit)
    RichEditor mEditor;
    @BindView(R.id.layout_bottom)
    View layoutMenu;
    @BindView(R.id.hl)
    View layoutHl;

    @BindView(R.id.layout_parame)
    View layoutParam;
    @BindView(R.id.edit_num)
    EditText editNum;

    @BindView(R.id.btn_video)
    ImageButton bTnVideo;
    @BindView(R.id.layout_uploadvideo)
    RelativeLayout rLUploadVideo;
    @BindView(R.id.ev_deal_video)
    JZVideoPlayerStandard evDealVideo;
    @BindView(R.id.iv_cancelvideo)
    ImageView ivCancelVideo;

    private EditMenuView mMenuView;

    private AlertDialog textDialog;
    private TextView tvCancel;
    private TextView tvOk;
    private TextView tvUdef;
    private EditText editContent;

    private int flagId = -1;

    private int typeIndex = -1;
    private int subTypeIndex = -1;
    private int pattern = 0;          //是否是交易模式  0  交易模式  1是论坛模式
    private boolean isModify = false;       //是否是修改
    private boolean isCover = false;        //是否是上传封面
    private String coverUrl = "";           //封面图片
    private String tradeType = "0";         //0是出售  1是求购
    private String anonymous = "0";        //是否匿名 0:不匿名，1：匿名
    private String content;
    private boolean isPublish = false;

    private int uploadType = -1;//上传的类型，上传图片还是视频
    private String videoPath = "";
    private String videoUrl = "";
    private String videoPic = "";
    private boolean isUploadFinish = true;// 视频是否上传成功
    private boolean isUploadVideo = false;// 是否上传视频 （默认不上传）

    private GoodsDetailedBean goodsBean;
    private List<AllTypeBaen> bean;

    private PopIssueDeal pop;
    private PopPosition popType;

    private IssueDealContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_deal);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_issue_deal));
        getBaseHeadView().showHeadRightButton("提交", this);
        getBaseHeadView().showBackButton(this);
        bTnVideo.setVisibility(View.GONE);

        popType = new PopPosition(this, R.layout.pop_ts_position);
        popType.setTitle("交易类型");

        if (textDialog == null) {
            View view = View.inflate(this, R.layout.dialog_edit_topic, null);
            tvCancel = (TextView) view.findViewById(R.id.btn_cancel);
            tvOk = (TextView) view.findViewById(R.id.btn_issue);
            editContent = (EditText) view.findViewById(R.id.edit);
            tvCancel.setOnClickListener(this);
            tvOk.setOnClickListener(this);
            textDialog = DialogUtil.createDialog(this, view);
            textDialog.dismiss();
        }

        mMenuView = new EditMenuView(layoutMenu);
        mMenuView.getLayoutHl().setVisibility(View.GONE);

        mEditor.setPlaceholder("请输入内容");
        mEditor.setFontSize(10);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setBackgroundColor(getResources().getColor(R.color.module_divide_line));
        mEditor.setEditorHeight(250);

    }

    @Override
    public void initListener() {
        popType.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                tradeType = newVal + "";
                if (newVal == 0) {
                    btnBuyer.setText("出售");
                    layoutUdef.setVisibility(View.VISIBLE);
                } else {
                    btnBuyer.setText("求购");
                    layoutUdef.setVisibility(View.GONE);
                }
            }
        });

        AndroidBug5497Workaround.assistActivity(this, new AndroidBug5497Workaround.KeyboardListener() {
            @Override
            public void keyboardStateChange(int state, int contentHeight) {
                if (state == AndroidBug5497Workaround.KEYBOARD_STATE_SHOW) {
                    layoutHl.setVisibility(View.VISIBLE);
                } else {
                    layoutHl.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void initData() {
        new IssueDealPresenter(this);
        mPresenter.getType();
        popType.setData(new String[]{"出售", "求购"});

        flagId = getIntent().getIntExtra("id", -1);
        goodsBean = (GoodsDetailedBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        if (goodsBean != null) {
            getBaseHeadView().showTitle("修改交易");
        }
    }

    @OnClick({R.id.btn_type,
            R.id.layout_cover,
            R.id.img_cover,
            R.id.btn_isBuyer,
            R.id.btn_font,
            R.id.btn_jiacu,
            R.id.btn_xieti,
            R.id.btn_center,
            R.id.btn_left,
            R.id.h1,
            R.id.h2,
            R.id.h3,
            R.id.h4,
            R.id.btn_undo,
            R.id.btn_redo,
            R.id.btn_color,
            R.id.btn_img,
            R.id.btn_video,
            R.id.layout_video,
            R.id.iv_cancelvideo
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                showCloseDialog();
                closeInputMethod();
                break;

            //发布
            case R.id.headRightButton:
                if (!isPublish)
                    issueDeal();
                closeInputMethod();
                break;

            //交易类型
            case R.id.btn_isBuyer:
                popType.showPopOnRootView(this);
                closeInputMethod();
                break;

            //选择分类
            case R.id.btn_type:
                if (pop != null) {
                    pop.showPopOnRootView(this);
                }
                closeInputMethod();
                break;

            //取消
            case R.id.btn_cancel:
                textDialog.dismiss();
                closeInputMethod();
                break;

            //提交发布
            case R.id.btn_issue:
                if (!isPublish)
                    issueDeal();
                closeInputMethod();
                break;

            //上传封面
            case R.id.layout_cover:
                isCover = true;
                takePhoto();
                closeInputMethod();
                break;

            //更换封面
            case R.id.img_cover:
                isCover = true;
                takePhoto();
                closeInputMethod();
                break;

            //字体
            case R.id.btn_font:
                mMenuView.switchFontPanel();
                break;

            //加粗
            case R.id.btn_jiacu:
                mEditor.setBold();
                break;

            //斜体
            case R.id.btn_xieti:
                mEditor.setItalic();
                break;

            //左对齐
            case R.id.btn_left:
                mEditor.setAlignLeft();
                break;

            //居中
            case R.id.btn_center:
                mEditor.setAlignCenter();
                break;

            //h1
            case R.id.h1:
                mEditor.setHeading(1);
                break;

            //h2
            case R.id.h2:
                mEditor.setHeading(2);
                break;

            //h3
            case R.id.h3:
                mEditor.setHeading(3);
                break;

            //h4
            case R.id.h4:
                mEditor.setHeading(4);
                break;

            //撤销
            case R.id.btn_undo:
                mEditor.undo();
                break;

            // 恢复
            case R.id.btn_redo:
                mEditor.redo();
                break;

            //字体颜色
            case R.id.btn_color:
                showColorDialog();
                break;

            //插入图片
            case R.id.btn_img:
                isCover = false;
                takePhoto();
                break;

            //插入视频（弃用）
            case R.id.btn_video:
                showVideoDialog();
                break;

            //选择udef
            case R.id.btn_select_udef:
                showUdef(v);
                closeInputMethod();
                break;

            //插入视频
            case R.id.layout_video:
                showVideoDialog();
                break;
            //取消插入视频
            case R.id.iv_cancelvideo:
                isUploadFinish = true;
                isUploadVideo = false;
                evDealVideo.setVisibility(View.GONE);
                rLUploadVideo.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showUdef(View v) {
        tvUdef = (TextView) v;
        AllTypeBaen.UdefBean bean = (AllTypeBaen.UdefBean) tvUdef.getTag();
        int type = bean.getUdfType();

        //udefType = 0:文本，1：城市，2：时间，3，下拉，4，多选，5，单选
        switch (type) {
            //城市选择
            case 1:
                startActivityForResult(new Intent(this, CityPickerActivity.class), REQUEST_CODE_CITY);
                break;

            //时间选择
            case 2:
                showTimePop();
                break;

            //多选
            case 4:
                showSelectDialog(v);
                break;

            //单选
            case 5:
                showSelectDialog(v);
                break;

        }
    }

    private void closeInputMethod() {
        WindowUtil.closeInputMethod(this);
        mMenuView.hideMenu();
    }

    private MultiSelectDialog mSelectDialog;

    private void showSelectDialog(View v) {
        if (mSelectDialog == null) {
            mSelectDialog = new MultiSelectDialog(this);
        }

        AllTypeBaen.UdefBean bean = (AllTypeBaen.UdefBean) tvUdef.getTag();
        mSelectDialog.notifyChangeData(bean.getEntity());
        mSelectDialog.show();
    }

    private TimePickerView timePickerView;
    private SimpleDateFormat sdf;

    //显示选择时间弹窗
    private void showTimePop() {
        if (timePickerView == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            TimePickerView.Builder builder = new TimePickerView.Builder(this,
                    new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {
                            String time = sdf.format(date);
                            tvUdef.setText(time);
                            AllTypeBaen.UdefBean bean = (AllTypeBaen.UdefBean) tvUdef.getTag();
                            bean.setContent(time);
                        }
                    }).setType(TimePickerView.Type.YEAR_MONTH_DAY).setDate(
                    new Date()).setOutSideCancelable(true);

            timePickerView = new TimePickerView(builder);
        }
        timePickerView.show();
    }


    //返回上一个界面
    private void showCloseDialog() {
        if (TextUtils.isEmpty(content)) {
            finish();

        } else {
            AlertDialog a = DialogUtil.showInputDialog(this, false, null, getString(R.string.dialog_out),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
        }
    }

    //去文件夹选择文件
    private void gotoSelectFile(final int type) {
        uploadType = 888;
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
                    PictureConfig.getInstance().init(options).openPhoto(IssueDealActivity.this, IssueDealActivity.this);
                } else if (type == 2) {
                    PictureConfig.getInstance().init(options).startOpenCamera(IssueDealActivity.this, IssueDealActivity.this);
                }
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(IssueDealActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(IssueDealActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });
        //系统
//        performRequestPermissions(getString(R.string.txt_card_permission), pers, 10010,
//                new PermissionsResultListener() {
//                    @Override
//                    public void onPermissionGranted() {
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("*/*");
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        try {
//                            startActivityForResult(intent, 888);
//                        } catch (android.content.ActivityNotFoundException ex) {
//                            ToastUtil.showShort(IssueDealActivity.this, "请先下载一个文件管理器");
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionDenied() {
//                        mAlertDialog = DialogUtil.showDeportDialog(IssueDealActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
//                                new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if(v.getId() == R.id.tv_dialog_confirm){
//                                            JumpPermissionManagement.GoToSetting(IssueDealActivity.this);
//                                        }
//                                        mAlertDialog.dismiss();
//                                    }
//                                });
//                    }
//                });
    }

    //调用系统相机拍摄视频
    private void gotoShoot() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        performRequestPermissions(getString(R.string.txt_permission), pers, 1011,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        Uri fileUri = Uri.fromFile(FileStorage.getVideoCacheFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(intent, 999);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(IssueDealActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(IssueDealActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });

    }

    //选择视频
    private ListDialog mListDialog;

    private void showVideoDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this, new String[]{"拍摄", "从本地选择"});
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //使用系统相机拍摄
                    if (position == 0) {
//                        gotoShoot();
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

    //选择字体颜色
    private ColorSelectDialog mColorDialog;

    private void showColorDialog() {
        if (mColorDialog == null) {
            mColorDialog = new ColorSelectDialog(this);
            mColorDialog.setColorListener(new ColorSelectDialog.OnColorListener() {
                @Override
                public void onColor(int color) {
                    mEditor.setTextColor(color);
                }
            });
        }
        mColorDialog.show();
    }

    private void postDelay() {
        Subscription sub =
                Observable.timer(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                changeUdef(typeIndex, 0);
                            }
                        });
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void showType(List<AllTypeBaen> datas) {
        this.bean = datas;
        pop = new PopIssueDeal(this, R.layout.pop_issue_deal);
        pop.setDatas(datas);
        pop.setOnPopClickListener(new PopIssueDeal.OnPopClickListener() {
            @Override
            public void onPopClick(int typeIndex, int subTypeIndex) {
                changeUdef(typeIndex, subTypeIndex);
            }
        });
        pattern = datas.get(0).getPattern();
        for (int i = 0; i < datas.size(); i++) {
            AllTypeBaen allBean = datas.get(i);
            for (AllTypeBaen.UdefBean udefBean : allBean.getUdef()) {
                if (udefBean.getUdfType() == 4 || udefBean.getUdfType() == 5) {
                    udefBean.setEntity(udefBean.getValues());
                }
            }

            if (flagId != -1 && flagId == allBean.getCode()) {
                typeIndex = i;
                subTypeIndex = 0;
            }
        }
        //如果是从确定好的子分类页面选择发布的话，自动选择那一项子分类
        if (flagId != -1) {
            pop.setTypeValue(typeIndex);
            btnType.setText(datas.get(typeIndex).getTitle());
            postDelay();
        }

        fillData();
        rootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUploadingSuccess(String id, String url) {
        //上传封面
        if (id.startsWith("b")) {
            coverUrl = url;
            imgCover.setVisibility(View.VISIBLE);
            layoutUpload.setVisibility(View.INVISIBLE);
            ImageHelper.loadImage(this, imgCover, url, R.drawable.list_error_img);

            //上传图片描述
        } else {
            mEditor.uploadFinish(id, url);
        }
    }

    @Override
    public void showUploadingFailure(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showUploadVideoSuccess(String id, final String url) {
        //mEditor.uploadVideoFinish(id, url);
    }

    @Override
    public void showUploadVideoFailure(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showIssueSuccess() {

        mAlertDialog = DialogUtil.showDeportDialog(this, true, "帖子发布成功", "审核后(一个工作日内审核)会推荐到交易板块，期间不影响你转发给朋友进行交易", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }

        });
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isPublish = false;
                setResult(Constant.ResponseCode.NORMAL_FLAG);
                finish();
            }
        });
    }

    @Override
    public void showIssueFailure(String info) {
        isPublish = false;
        ToastUtil.showShort(this, "发布失败，" + info);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void setPresenter(IssueDealContract.Presenter presenter) {
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
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();

        if (mEditor != null) {
            mEditor.setWebChromeClient(null);
            mEditor.setWebViewClient(null);

            ViewParent parent = mEditor.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mEditor);
            }
            mEditor.removeAllViews();
            mEditor.destroy();
            mEditor = null;

        }
        EventBusUtil.unregister(this);
        mAlertDialog = null;

        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String file = UriUtils.getPath(this, uri);
                String a = "a" + System.currentTimeMillis();
                mEditor.insertVideo(a, "file:///android_asset/load_animation.gif");
                mPresenter.uploadingVideo(a, file);
            }
        }

        if (requestCode == 888 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String file = UriUtils.getPath(this, uri);
                if (!TextUtils.isEmpty(file) && file.endsWith(".mp4")) {
                    String a = "a" + System.currentTimeMillis();
                    mEditor.insertVideo(a, "file:///android_asset/load_animation.gif");
                    mPresenter.uploadingVideo(a, file);
                } else {
                    ToastUtil.showShort(this, "请选择mp4格式的视频");
                }
            }
        }

        if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
            if (data != null) {
                String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                tvUdef.setText(city);
                AllTypeBaen.UdefBean bean = (AllTypeBaen.UdefBean) tvUdef.getTag();
                bean.setContent(city);
            }
        }
    }

    //根据大分类改变自定义字段内容
    private void changeUdef(int typeIndex, int subTypeIndex) {
        this.typeIndex = typeIndex;
        this.subTypeIndex = subTypeIndex;

        AllTypeBaen typeBean = bean.get(typeIndex);
        pattern = typeBean.getPattern();
        //交易模式显示金额跟交易类型
        if (typeBean.getPattern() == 0) {
            if (layoutMoney.getVisibility() == View.GONE) {
                layoutMoney.setVisibility(View.VISIBLE);
                layoutType.setVisibility(View.VISIBLE);
                line1.setVisibility(View.VISIBLE);
                line2.setVisibility(View.VISIBLE);
                editTitle.setHint("请输入商品标题");
            }


            //论坛模式要隐藏金额输入跟交易类型
        } else {
            if (layoutMoney.getVisibility() == View.VISIBLE) {
                layoutMoney.setVisibility(View.GONE);
                layoutType.setVisibility(View.GONE);
                editTitle.setHint("请输入帖子标题");
                line1.setVisibility(View.GONE);
                line2.setVisibility(View.GONE);
            }
        }

        if (layoutUdef.getChildCount() != 0) {
            layoutUdef.removeAllViews();
        }

        AllTypeBaen bean = this.bean.get(typeIndex);
        if (tradeType.equals("0") && bean.getUdef() != null && bean.getUdef().size() > 0) {
            layoutParam.setVisibility(View.VISIBLE);
            for (int i = 0; i < bean.getUdef().size(); i++) {
                AllTypeBaen.UdefBean udef = bean.getUdef().get(i);

                String oldValue = "";
                //如果是从修改页面传过来的自定义分类信息，填充上去
                if (goodsBean != null) {
                    for (GoodsDetailedBean.Udef temp : goodsBean.getUdefs()) {
                        if (temp.getTradeField().equals(udef.getCode())) {
                            oldValue = temp.getTradeInfoValue();
                            udef.setContent(oldValue);
                            //单选 将那些单选或者多选的状态标记上去
                            if (udef.getUdfType() == 5) {
                                for (AllTypeBaen.UdefBean.Entity entity : udef.getEntity()) {
                                    if (temp.getTradeInfoValue().equals(entity.getTitle())) {
                                        entity.setSelect(true);
                                    }
                                }
                            }

                            if (udef.getUdfType() == 4) {
                                String value = temp.getTradeInfoValue();
                                if (value.contains(",")) {
                                    String strings[] = value.split(",");
                                    for (String s : strings) {
                                        for (AllTypeBaen.UdefBean.Entity entity : udef.getEntity()) {
                                            if (s.equals(entity.getTitle())) {
                                                entity.setSelect(true);
                                            }
                                        }
                                    }
                                } else {
                                    for (AllTypeBaen.UdefBean.Entity entity : udef.getEntity()) {
                                        if (temp.getTradeInfoValue().equals(entity.getTitle())) {
                                            entity.setSelect(true);
                                        }
                                    }
                                }
                            }

                            break;
                        }
                    }
                }

                View view = null;
                //udefType = 0:文本，1：城市，2：时间，3，下拉，4，多选，5，单选
                if (udef.getUdfType() == 0) {
                    view = View.inflate(this, R.layout.item_issue_deal, null);
                    TextView tv = (TextView) view.findViewById(R.id.tv_name);
                    EditText edit = (EditText) view.findViewById(R.id.edit_issue);
                    tv.setText(udef.getTitle());
                    edit.setTag(udef);
                    String content = udef.getContent();
                    if (!TextUtils.isEmpty(content)) {
                        edit.setText(content);
                    }

                } else {
                    view = View.inflate(this, R.layout.item_issue_deal_btn, null);
                    TextView tv = (TextView) view.findViewById(R.id.tv_name);
                    TextView btnTv = (TextView) view.findViewById(R.id.btn_select_udef);
                    tv.setText(udef.getTitle());
                    btnTv.setTag(udef);
                    btnTv.setOnClickListener(this);
                    String content = udef.getContent();
                    if (!TextUtils.isEmpty(content)) {
                        btnTv.setText(content);
                    }
                }

                layoutUdef.addView(view);
            }

        } else {
            layoutParam.setVisibility(View.GONE);
        }

        String type = bean.getTitle();
        if (bean.getSon() != null && bean.getSon().size() > 0) {
            type += "," + bean.getSon().get(subTypeIndex).getTitle();
        }
        btnType.setText(type);

    }

    /**
     * 选取图片
     */
    private void takePhoto() {
        uploadType = 777;
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, 1011, new PermissionsResultListener() {
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
                                .setIsCrop(isCover)
                                .setAspectRatio(isCover ? 1 : 0, isCover ? 1 : 0)
                                .create();
                PictureConfig.getInstance().init(options).openPhoto(IssueDealActivity.this, IssueDealActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(IssueDealActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(IssueDealActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });

    }

    //发布交易
    private void issueDeal() {
        String token = UserManager.getInstance().getToken();
        String money = editPrice.getText().toString();
        String title = editTitle.getText().toString();
        String phone = editPhone.getText().toString();
        String number = editNum.getText().toString();

        if (TextUtils.isEmpty(title.trim())) {
            ToastUtil.showShort(this, "请输入标题");

            return;
        }

        if (title.length() < 8) {
            ToastUtil.showShort(this, "标题不得少于8个字");
            return;
        }


        if (typeIndex == -1) {
            ToastUtil.showShort(this, "请选择分类");
            return;
        }

        if (pattern == 0 && TextUtils.isEmpty(money)) {
            ToastUtil.showShort(this, "金额不能为空");
            return;
        }
        if (pattern == 0 && !isNumber(money)) {
            ToastUtil.showShort(this, "请输入正确金额");
            return;
        }
        if (pattern == 0 && Double.valueOf(money) < 0.01) {
            ToastUtil.showShort(this, "金额不能小于0.01元");
            return;
        }
        if (pattern == 0 && Double.valueOf(money) > 1000000) {
            ToastUtil.showShort(this, "单次交易金额不能超过1000000");
            return;
        }
        if (TextUtils.isEmpty(editNum.getText().toString().trim())) {
            ToastUtil.showShort(this, "单次交易数量不能为空");
            return;
        }
        if (!isInteger(editNum.getText().toString())) {
            ToastUtil.showShort(this, "请输入正确的数量");
            return;
        }
        if (Float.valueOf(editNum.getText().toString()) > 99999) {
            ToastUtil.showShort(this, "单次交易数量最多不能超过99999");
            return;
        }

        if (TextUtils.isEmpty(phone.trim())) {
            ToastUtil.showShort(this, "请输入联系方式");
            return;
        }
        if (!RegexUtils.isMobileExact(phone.trim())) {
            ToastUtil.showShort(this, getString(R.string.incorrect_phone_format));
            return;
        }

        if (TextUtils.isEmpty(coverUrl)) {
            ToastUtil.showShort(this, "请上传封面图片");
            return;
        }

        if (coverUrl.endsWith("webp")) {
            ToastUtil.showShort(this, "暂不支持webp图片");
            return;
        }

        content = mEditor.getHtml();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "请输入详细描述");
            return;
        }
        String text = Jsoup.parse(content).body().text();
        if (text.length() < 50) {
            ToastUtil.showShort(this, "正文内容不得少于50个字");
            return;
        }
        if (!isUploadFinish) {
            ToastUtil.showShort(this, "请等待视频上传成功");
            return;
        }
        isPublish = true;
        String typeCode = this.bean.get(typeIndex).getCode() + "";
        String subTypeCode = this.bean.get(typeIndex).getSon().size() == 0 ? "" : this.bean.get(typeIndex).getSon().get(subTypeIndex).getCode() + "";

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("tradeTypeSonId", subTypeCode);
        map.put("tradeTypeId", typeCode);
        map.put("tradeType", tradeType);
        map.put("title", title);
        map.put("content", mEditor.getUnityHtml());
        map.put("picUrl", coverUrl);
        map.put("contacts", phone);
        map.put("anonymous", anonymous);
        if (isUploadVideo) {
            map.put("videoPic", videoPic);//视频封面
            map.put("videoText", videoUrl);//视频链接
        }
        if (goodsBean != null) {
            map.put("id", goodsBean.getId() + "");
        }
        if (!TextUtils.isEmpty(number)) {
            map.put("num", number);
        }
        if (!TextUtils.isEmpty(money)) {
            map.put("price", money);
        }

        for (int i = 0; i < layoutUdef.getChildCount(); i++) {
            EditText edit = (EditText) layoutUdef.getChildAt(i).findViewById(R.id.edit_issue);
            if (edit == null) {
                TextView tv = (TextView) layoutUdef.getChildAt(i).findViewById(R.id.btn_select_udef);
                if (tv != null) {
                    AllTypeBaen.UdefBean udefBean = (AllTypeBaen.UdefBean) tv.getTag();
                    map.put(udefBean.getCode(), tv.getText().toString());
                }
            } else {
                AllTypeBaen.UdefBean udefBean = (AllTypeBaen.UdefBean) edit.getTag();
                map.put(udefBean.getCode(), edit.getText().toString());
            }
        }
        Log.d("IssueDealActivity", "issueDeal: " + map.toString());
        mPresenter.issueDeal(map);
    }

    public boolean isNumber(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,9})?$"); // 判断小数点后9位的数字的正则表达式
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isInteger(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))$");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    //填充数据
    private void fillData() {
        if (goodsBean == null) return;

        // 视频
        if (!TextUtils.isEmpty(goodsBean.getVideoText()) && !TextUtils.isEmpty(goodsBean.getVideoPic())) {
            isUploadVideo = true;
            isUploadFinish = true;
            videoPic = goodsBean.getVideoPic();
            videoUrl = goodsBean.getVideoText();
            rLUploadVideo.setVisibility(View.GONE);
            evDealVideo.setVisibility(View.VISIBLE);
            evDealVideo.setUp(goodsBean.getVideoText(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, "", "");
            ImageHelper.loadImage(this, evDealVideo.thumbImageView, goodsBean.getVideoPic());
        }

        //交易模式显示金额跟交易类型
        if (goodsBean.getPattern() == 0) {
            layoutMoney.setVisibility(View.VISIBLE);
            layoutType.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            editPrice.setText(goodsBean.getPrice());

            //论坛模式要隐藏金额输入跟交易类型
        } else {
            layoutMoney.setVisibility(View.GONE);
            layoutType.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }

        editTitle.setText(goodsBean.getTitle());
        editPhone.setText(goodsBean.getContacts());
        editNum.setText(goodsBean.getNum() + "");

        //这里要延迟加载图片不不然会跟布局动画属性冲突的
        coverUrl = TextUtils.isEmpty(goodsBean.getPicUrl()) ? "" : goodsBean.getPicUrl();
        Subscription sub =
                Observable.timer(1000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                imgCover.setVisibility(View.VISIBLE);
                                layoutUpload.setVisibility(View.INVISIBLE);
                                ImageHelper.loadImage(IssueDealActivity.this, imgCover, goodsBean.getPicUrl(), R.drawable.list_error_img);
                            }
                        });

        RxTaskHelper.getInstance().addTask(this, sub);

        if (goodsBean.getTradeType() == 0) {
            btnBuyer.setText("出售");
        } else {
            btnBuyer.setText("求购");
        }
        tradeType = goodsBean.getTradeType() + "";
        popType.setValue(goodsBean.getTradeType());

        //交易分类
        String type = goodsBean.getTradeTypeName();
        String subType = goodsBean.getTradeTypeSonName();
        for (int i = 0; i < bean.size(); i++) {
            if (bean.get(i).getTitle().equals(type)) {
                typeIndex = i;
                pop.setTypeValue(typeIndex);

                for (int j = 0; j < bean.get(i).getSon().size(); j++) {
                    String temp = bean.get(i).getSon().get(j).getTitle();
                    if (temp.equals(subType)) {
                        subTypeIndex = j;
                        pop.setSubTypeValue(j);
                        break;
                    }
                }
                break;
            }
        }

        String typeTemp;
        if (TextUtils.isEmpty(subType)) {
            typeTemp = type;
        } else {
            typeTemp = type + "," + subType;
        }
        btnType.setText(typeTemp);

        layoutUdef.removeAllViews();
        //自定义属性
        if (goodsBean.getUdefs() != null && goodsBean.getUdefs().size() > 0) {
            List<GoodsDetailedBean.Udef> udefs = goodsBean.getUdefs();

            String typeId = udefs.get(0).getTradeTypeId();

            for (int i = 0; i < bean.size(); i++) {
                AllTypeBaen allBean = bean.get(i);
                if (typeId.equals(allBean.getCode() + "")) {
                    changeUdef(i, 0);
                }
            }
        }

        //加载商品描述
        mEditor.setHtml(goodsBean.getContent());

    }

    @Subscribe
    public void onEvent(EventClickBean bean) {
        StringBuffer sb = new StringBuffer();
        List<AllTypeBaen.UdefBean.Entity> datas = (List<AllTypeBaen.UdefBean.Entity>) bean.bean;
        for (AllTypeBaen.UdefBean.Entity entity : datas) {
            if (entity.isSelect()) {
                sb.append(entity.getTitle()).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
            tvUdef.setText(sb.toString());
        } else {
            tvUdef.setText("请选择");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCloseDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = ViewFindUtils.getViewAtViewGroup(mEditor, (int) ev.getX(), (int) ev.getY());
            if (v != null) {
                layoutHl.setVisibility(View.VISIBLE);
            } else {
                closeInputMethod();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        switch (uploadType) {
            case 777://帖子封面
                String id;
                if (isCover) {
                    id = "b" + System.currentTimeMillis();
                } else {
                    id = "a" + System.currentTimeMillis();
                    mEditor.insertImage(id, "file:///android_asset/load_animation.gif", "");
                }
                mPresenter.uploadingFile(id, media.getPath());
                break;
            case 888://视频
                isUploadVideo = true;
                isUploadFinish = false;
                String file = media.getPath();
                videoPath = file;
                File videoFile = new File(file);
                if (!TextUtils.isEmpty(file) && file.endsWith(".mp4")) {
                    String a = "a" + System.currentTimeMillis();
//                    mEditor.insertVideo(a, "file:///android_asset/load_animation.gif");
                    LoadHelper.UpyunUploadVideo(videoFile, this, new UIProgressListener() {
                        @Override
                        public void onUIFailed(String errorCode, String msg) {
                            super.onUIFailed(errorCode, msg);
                        }
                    });
//                    mPresenter.uploadingVideo(a, file);
                } else {
                    ToastUtil.showShort(this, "请选择mp4格式的视频");
                }
                break;
        }
    }

    @Override
    public void onUploadSuccess(UploadResult result) {
        videoUrl = result.getUrls().get(0);
        videoPic = result.getUrls().get(1);
        rLUploadVideo.setVisibility(View.GONE);
        evDealVideo.setVisibility(View.VISIBLE);
        Bitmap bitmap = ImageUtils.getVideoFirstFrame(videoPath);
        evDealVideo.setUp(videoPath, JZVideoPlayer.SCREEN_WINDOW_NORMAL, "", "");
        evDealVideo.thumbImageView.setImageBitmap(bitmap);
        isUploadFinish = true;
    }

    @Override
    public void onUploadFailed(String errorCode, String msg) {
        isUploadFinish = false;
    }
}
