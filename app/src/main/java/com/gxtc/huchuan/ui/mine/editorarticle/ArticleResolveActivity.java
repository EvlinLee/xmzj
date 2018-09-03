package com.gxtc.huchuan.ui.mine.editorarticle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.helper.PermissionsHelper;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ColorSelectDialog;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.pop.PopArticleType;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.UriUtils;
import com.gxtc.huchuan.widget.EditMenuView;
import com.gxtc.huchuan.widget.richEditor.RichEditor;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/28.
 * 文章分解界面
 * 2017/3/1
 */

public class ArticleResolveActivity extends BaseTitleActivity implements
        ArticleResolveContract.View, View.OnClickListener, PictureConfig.OnSelectResultCallback {

    public static int    REQUEST_CODE_EDIT = 101;
    public static String KEY_ID            = "groupId";


    @BindView(R.id.layout_import)           View       layoutImport;
    @BindView(R.id.iv_article_cover)        ImageView  ivArticleCover;
    @BindView(R.id.et_article_link)         EditText   etArticleLink;
    @BindView(R.id.et_article_title)        EditText   etArticleTitle;
    @BindView(R.id.et_article_abstract)     EditText   etArticleAbstract;
    @BindView(R.id.btn_article_import_link) Button     btnArticleImportLink;
    @BindView(R.id.root_view)               ScrollView rootView;
    @BindView(R.id.edit)                    RichEditor edit;

    //编辑菜单相关
    @BindView(R.id.layout)      LinearLayout layoutMenu;
    @BindView(R.id.layout_text) LinearLayout layoutText;
    @BindView(R.id.hl)          LinearLayout layoutHl;
    @BindView(R.id.btn_font)    ImageButton  btnFont;

//    @BindView(R.id.tv_issue_circle)
//    TextView tvIssueCircle;

    private EditMenuView menuView;

    private int id = -1;            //圈子id  以前写的 不知道有什么用
    private String typeId;          //文章类型
    private String title;           //文章标题
    private String content;         //文章内容
    private String url;             //文章原链接
    private String cover;           //文章封面
    private String digest;          //文章摘要

    private String[]                     items;
    private List<ChannelBean.NormalBean> beans;
    private PopArticleType               pop;

    private boolean imgFlag;            //选择图片标志  插入图片是true 选择封面是false

    //同步圈子的dialog
    IssueListDialog mIssueListDialog;
    public static String STRING_PUBLIC_STATUS   = "0"; //0：公开，1：私密
    public static String STRING_ISDEPLOY_STATUS = "0"; //1草稿，0正式。默认0

    private ArticleResolveContract.Presenter mPresenter;
    private ArrayList<Integer> mGroupIds = new ArrayList<>();//同步的圈子id

    private NewsBean newBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_resolve);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("发表文章");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("导入", this);

        edit.setPlaceholder("请输入内容");
        edit.setFontSize(10);
        edit.setPadding(10, 10, 10, 10);

        etArticleTitle.requestFocus();

        menuView = new EditMenuView(layoutMenu);
    }

    @Override
    public void initData() {
        new ArticleResolvePresenter(this);
        mPresenter.getArticleType();

        newBean = (NewsBean) getIntent().getSerializableExtra("bean");
        fillData();
    }

    private void fillData() {
        if (newBean == null) return;
        etArticleTitle.setText(newBean.getTitle());
        etArticleAbstract.setText(newBean.getDigest());

        cover = newBean.getCover();
        ImageHelper.loadImage(this, ivArticleCover, cover);

        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        Subscription sub = AllApi.getInstance().getNewsInfo(token, newBean.getId()).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<NewsBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        getBaseLoadingView().hideLoading();
                        NewsBean bean = (NewsBean) data;
                        if (bean != null && edit != null) {
                            content = bean.getContent();
                            edit.setHtml(bean.getContent());
                            getBaseHeadView().getHeadRightButton().setText("保存");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        getBaseLoadingView().hideLoading();
                        ToastUtil.showShort(ArticleResolveActivity.this, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @OnClick({R.id.iv_article_cover, R.id.btn_article_import_link, R.id.btn_font, R.id.btn_jiacu, R.id.btn_xieti, R.id.btn_center, R.id.btn_left, R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.btn_undo, R.id.btn_redo, R.id.btn_color, R.id.btn_img, R.id.btn_video, R.id.tv_issue_tongbu})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.headBackButton:
                showDepotDialog();
                WindowUtil.closeInputMethod(this);
                break;

            //保存键
            case R.id.headRightButton:
                save();
                WindowUtil.closeInputMethod(this);
                break;

            //导入链接按钮
            case R.id.btn_article_import_link:
                importContent();
                WindowUtil.closeInputMethod(this);
                break;

            //封面
            case R.id.iv_article_cover:
                imgFlag = false;
                chooseImg();
                WindowUtil.closeInputMethod(this);
                break;

            //字体
            case R.id.btn_font:
                switchFontPanel();
                break;

            //加粗
            case R.id.btn_jiacu:
                edit.setBold();
                break;

            //斜体
            case R.id.btn_xieti:
                edit.setItalic();
                break;

            //左对齐
            case R.id.btn_left:
                edit.setAlignLeft();
                break;

            //居中
            case R.id.btn_center:
                edit.setAlignCenter();
                break;

            //h1
            case R.id.h1:
                edit.setHeading(1);
                break;

            //h2
            case R.id.h2:
                edit.setHeading(2);
                break;

            //h3
            case R.id.h3:
                edit.setHeading(3);
                break;

            //h4
            case R.id.h4:
                edit.setHeading(4);
                break;

            //撤销
            case R.id.btn_undo:
                edit.undo();
                break;

            // 恢复
            case R.id.btn_redo:
                edit.redo();
                break;//撤销

            //字体颜色
            case R.id.btn_color:
                showColorDialog();
                break;

            //插入图片
            case R.id.btn_img:
                imgFlag = true;
                chooseImg();
                break;

            //插入视频
            case R.id.btn_video:
                showVideoDialog();
                break;
        }
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
                        selectVideo(2);
                        //从文件选择
                    } else {
//                        gotoSelectFile();
                        selectVideo(1);
                    }

                }
            });
        }
        mListDialog.show();
    }

    //去文件夹选择文件
    private void gotoSelectFile() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 10010,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(intent, 888);
                        } catch (android.content.ActivityNotFoundException ex) {
                            ToastUtil.showShort(ArticleResolveActivity.this, "请先下载一个文件管理器");
                        }

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(ArticleResolveActivity.this,
                                false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    ArticleResolveActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    //调用系统相机拍摄视频
    private void gotoShoot() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        performRequestPermissions(getString(R.string.txt_permission), pers, 1011,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent  = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        Uri    fileUri = Uri.fromFile(FileStorage.getVideoCacheFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(intent, 999);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(ArticleResolveActivity.this,
                                false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    ArticleResolveActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    //选择或拍摄视频
    private void selectVideo(final int type) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionsHelper.getInstance(this).performRequestPermissions(
                getString(R.string.txt_card_permission), pers, 10010,
                new PermissionsHelper.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options = new FunctionOptions.Builder().setType(
                                FunctionConfig.TYPE_VIDEO).setRecordVideoDefinition(
                                1).setSelectMode(FunctionConfig.MODE_SINGLE).setShowCamera(
                                true).setImageSpanCount(3).setPreviewVideo(true).create();
                        if (type == 1) {
                            PictureConfig.getInstance().init(options).openPhoto(
                                    ArticleResolveActivity.this, ArticleResolveActivity.this);
                        } else if (type == 2) {
                            PictureConfig.getInstance().init(options).startOpenCamera(
                                    ArticleResolveActivity.this, ArticleResolveActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(ArticleResolveActivity.this,
                                false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    ArticleResolveActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    //选择字体颜色
    private ColorSelectDialog mColorDialog;

    private void showColorDialog() {
        if (mColorDialog == null) {
            mColorDialog = new ColorSelectDialog(this);
            mColorDialog.setColorListener(new ColorSelectDialog.OnColorListener() {
                @Override
                public void onColor(int color) {
                    edit.setTextColor(color);
                }
            });
        }
        mColorDialog.show();
    }

    private void switchFontPanel() {
        menuView.switchFontPanel();
        if (layoutText.getVisibility() == View.GONE) {
            Observable.timer(50, TimeUnit.MILLISECONDS).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    rootView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    @Override
    public void initListener() {
        edit.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                content = text;
                if (TextUtils.isEmpty(text)) {
                    getBaseHeadView().getHeadRightButton().setText("导入");

                } else {
                    if (layoutImport.getVisibility() == View.VISIBLE) {
                        layoutImport.setVisibility(View.GONE);
                    }
                    getBaseHeadView().getHeadRightButton().setText("保存");
                }
            }
        });
        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutHl.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        etArticleTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutHl.setVisibility(View.GONE);
                }
                return false;
            }
        });
        etArticleAbstract.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutHl.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }


    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和存储权限", pers, 1011, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                FunctionOptions options = new FunctionOptions.Builder().setType(
                        FunctionConfig.TYPE_IMAGE).setSelectMode(
                        FunctionConfig.MODE_SINGLE).setImageSpanCount(3).setEnableQualityCompress(
                        false) //是否启质量压缩
                                                                       .setEnablePixelCompress(
                                                                               false)   //是否启用像素压缩
                                                                       .setEnablePreview(
                                                                               true)          //是否打开预览选项
                                                                       .setShowCamera(
                                                                               true).setPreviewVideo(
                                true).setIsCrop(!imgFlag).setAspectRatio(imgFlag ? 0 : 4,
                                imgFlag ? 0 : 3).create();
                PictureConfig.getInstance().init(options).openPhoto(ArticleResolveActivity.this,
                        ArticleResolveActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.showShort(ArticleResolveActivity.this, "应用没有读取相机和存储权限");
            }
        });
    }

    //保存文章
    private void save() {
        if (getBaseHeadView().getHeadRightButton().getText().equals("导入")) {
            layoutImport.setVisibility(View.VISIBLE);

        } else {
            showPopType();
        }

    }

    public void savaDeport() {
        String token = UserManager.getInstance().getToken();
        title = etArticleTitle.getText().toString();
        digest = etArticleAbstract.getText().toString();
        content = edit.getHtml();
        int groupId = getIntent().getIntExtra(KEY_ID, 0);
        if (groupId != 0) {
            mGroupIds.add(groupId);
        }

        if (!UserManager.getInstance().isLogin(this)) {
            return;
        }
        if (TextUtils.isEmpty(title)) {
            title = "";
        }

        if (TextUtils.isEmpty(content)) {
            content = "";
        }

        if (TextUtils.isEmpty(typeId)) {
            typeId = "36";
        }

        if (TextUtils.isEmpty(cover)) {
            cover = "";
        }

        if (cover.endsWith("webp")) {
            ToastUtil.showShort(this, "暂不支持webp图片");
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        }
        map.put("token", token);
        map.put("content", content);
        map.put("digest", digest);
        map.put("title", title);
        map.put("typeId", typeId);          //默认淘客
        map.put("cover", cover);
        map.put("isPublish", STRING_PUBLIC_STATUS);//0：公开，1：私密
        map.put("isDeploy", STRING_ISDEPLOY_STATUS);           //1草稿，0正式。默认0
        if (!TextUtils.isEmpty(url)) {
            map.put("sourceUrl", url);
        }
        if (newBean != null) {
            map.put("id", newBean.getId());
        }
        mPresenter.saveArticle(map);
    }

    private void submit() {
        String token = UserManager.getInstance().getToken();
        title = etArticleTitle.getText().toString();
        digest = etArticleAbstract.getText().toString();
        content = edit.getUnityHtml();
        int groupId = getIntent().getIntExtra(KEY_ID, 0);
        if (groupId != 0) {
            mGroupIds.add(groupId);
        }

        if (!UserManager.getInstance().isLogin(this)) {
            return;
        }

        if (TextUtils.isEmpty(title)) {
            ToastUtil.showShort(this, "标题不能为空");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "内容不能为空");
            return;
        }

        if (TextUtils.isEmpty(typeId)) {
            ToastUtil.showShort(this, "请选择文章类型");
            return;
        }

        if (TextUtils.isEmpty(cover)) {
            ToastUtil.showShort(this, "封面不能为空");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        }
        map.put("token", token);
        map.put("content", content);
        map.put("digest", digest);
        map.put("title", title);
        map.put("typeId", typeId);
        map.put("cover", cover);
        map.put("isPublish", STRING_PUBLIC_STATUS);//0：公开，1：私密
        map.put("isDeploy", STRING_ISDEPLOY_STATUS);//1草稿，0正式。默认0
        if (!TextUtils.isEmpty(url)) {
            map.put("sourceUrl", url);
        }
        if (newBean != null) {
            map.put("id", newBean.getId());
        }
        mPresenter.saveArticle(map);
    }

    //显示选择弹窗
    private void showPopType() {
        if (pop != null) {
            pop.showPopOnRootView(this);
        }
    }

    //导入内容
    private void importContent() {
        String url = etArticleLink.getText().toString();
        if (url.isEmpty()) {
            ToastUtil.showShort(this, getString(R.string.toast_empty_url));
        } else {
            mPresenter.getHtmlContent(url);
        }
    }

    @Override
    public void showHtmlContent(final String url, final String title, final String des,
                                final String imgUrl, final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadFinish();
                getBaseHeadView().getHeadRightButton().setText("保存");
                ArticleResolveActivity.this.title = title;
                ArticleResolveActivity.this.content = content;
                ArticleResolveActivity.this.digest = des;
                ArticleResolveActivity.this.cover = imgUrl;
                ArticleResolveActivity.this.url = url;

                etArticleTitle.setText(title);
                etArticleAbstract.setText(des);
                edit.setHtml(content);
                ImageHelper.loadImage(ArticleResolveActivity.this, ivArticleCover, imgUrl);
            }
        });
    }

    @Override
    public void showArticleType(String[] item, List<ChannelBean.NormalBean> beans) {
        items = item;
        this.beans = beans;

        if (pop == null) {
            pop = new PopArticleType(this, R.layout.pop_article);
            pop.setData(items);
            pop.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    typeId = ArticleResolveActivity.this.beans.get(newVal).getNewstypeId() + "";
                    //发表文章
//                    submit();
                    showDialog();
                    pop.onDismiss();
                }
            });
        }
    }

    private void showDialog() {
        if (mIssueListDialog == null) {
            mIssueListDialog = new IssueListDialog(this, new String[]{"公开", "仅圈内显示"});
            mIssueListDialog.setmOnItemClickListener(new IssueListDialog.OnItemClickListener() {
                @Override
                public void selectByPosition(int position) {
                    if (position == 0) {
                        STRING_PUBLIC_STATUS = "0";
                    } else if (position == 1) {
                        STRING_PUBLIC_STATUS = "1";
                    }
                }
            });

            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(ArticleResolveActivity.this,
                                SyncIssueInCircleActivity.class);
                        intent.putExtra("default", id);
                        intent.putExtra(Constant.INTENT_DATA, mGroupIds);
                        ArticleResolveActivity.this.startActivityForResult(intent, 666);
                    } else if (v.getId() == R.id.tv_sure) {
                        STRING_ISDEPLOY_STATUS = "0";
                        submit();
                    }
                }
            });
        }
        mIssueListDialog.show();
    }

    AlertDialog mAlertDialog;

    void showDepotDialog() {
        mAlertDialog = DialogUtil.showDeportDialog(ArticleResolveActivity.this, false, null,
                getString(R.string.dialog_title), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            STRING_ISDEPLOY_STATUS = "1";
                            savaDeport();
                        } else {
                            mAlertDialog.dismiss();
                            finish();
                        }
                    }
                });
    }

    @Override
    public void showHtmlError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadFinish();
                ToastUtil.showShort(ArticleResolveActivity.this, "网址连接异常");
            }
        });
    }

    @Override
    public void showSaveSuccess() {
        if ("1".equals(STRING_ISDEPLOY_STATUS)) {
            ToastUtil.showShort(MyApplication.getInstance(), "保存草稿成功");
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            mAlertDialog = DialogUtil.showDeportDialog(this, true,
                    getString(R.string.editor_save_success),
                    getResources().getString(R.string.title_notice_message),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult(Activity.RESULT_OK);
                            mAlertDialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    //上传图片成功
    @Override
    public void showUploadingSuccess(String id, String url) {
        //表示富文本插入图片s
        if (id.startsWith("a")) {
            edit.uploadFinish(id, url);

        } else {
            cover = url;
            ImageHelper.loadImage(this, ivArticleCover, url);
        }
    }

    @Override
    public void showUploadingFailure(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showUploadVideoSuccess(String id, String url) {
        edit.uploadVideoFinish(id, url);
    }

    @Override
    public void showUploadVideoFailure(String info) {

    }

    @Override
    public void tokenOverdue() {
        UserManager.getInstance().isLogin(this);
    }

    @Override
    public void setPresenter(ArticleResolveContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ArticleResolveContract.REQUEST && resultCode == ArticleResolveContract.RESULTE) {
            content = data.getStringExtra(Constant.INTENT_DATA);
        }

        if (requestCode == 999 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String file = UriUtils.getPath(this, uri);
                String a    = "a" + System.currentTimeMillis();
                edit.insertVideo(a, "file:///android_asset/load_animation.gif");
                mPresenter.uploadingVideo(a, file);
            }
        }

        if (requestCode == 888 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String file = UriUtils.getPath(this, uri);
                if (!TextUtils.isEmpty(file) && file.endsWith(".mp4")) {
                    String a = "a" + System.currentTimeMillis();
                    edit.insertVideo(a, "file:///android_asset/load_animation.gif");
                    mPresenter.uploadingVideo(a, file);
                } else {
                    ToastUtil.showShort(this, "请选择mp4格式的视频");
                }
            }
        }
        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            ArrayList<MineCircleBean> selectData = data.getParcelableArrayListExtra("select_data");
            mGroupIds.clear();
            if (selectData.size() > 0) {
                ArrayList<String> listGroupName = new ArrayList<>();
                for (MineCircleBean bean1 : selectData) {
                    listGroupName.add(bean1.getGroupName());
                    mGroupIds.add(bean1.getId());
                }
                mIssueListDialog.changeTongbuName(listGroupName);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (edit != null) {
            edit.setWebChromeClient(null);
            edit.setWebViewClient(null);

            ViewParent parent = edit.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(edit);
            }
            edit.removeAllViews();
            edit.destroy();
            edit = null;
        }

        if (mIssueListDialog != null) {
            mIssueListDialog.dismiss();
        }

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDepotDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void startActivity(Context context, NewsBean bean, int groupId) {
        Intent intent = new Intent(context, ArticleResolveActivity.class);
        if (bean != null) {
            intent.putExtra("bean", bean);
        }
        context.startActivity(intent);
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        if (media.getType() == FunctionConfig.TYPE_IMAGE) {
            String id;
            if (imgFlag) {
                id = "a" + System.currentTimeMillis();
                edit.insertImage(id, "file:///android_asset/load_animation.gif", "");
            } else {
                id = "b" + System.currentTimeMillis();
            }
            mPresenter.uploadingFile(id, media.getPath());
        }
        if (media.getType() == FunctionConfig.TYPE_VIDEO) {
            if (media.getPath() != null) {
                String file = media.getPath();
                if (!TextUtils.isEmpty(file) && file.endsWith(".mp4")) {
                    String a = "a" + System.currentTimeMillis();
                    edit.insertVideo(a, "file:///android_asset/load_animation.gif");
                    mPresenter.uploadingVideo(a, file);
                } else {
                    ToastUtil.showShort(this, "请选择mp4格式的视频");
                }
            }
        }
    }
}
