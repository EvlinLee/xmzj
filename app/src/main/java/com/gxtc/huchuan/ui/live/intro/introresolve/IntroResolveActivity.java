package com.gxtc.huchuan.ui.live.intro.introresolve;

import android.Manifest;
import android.app.Activity;
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
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.CustomCollectBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ColorSelectDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.pop.PopArticleType;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by sjr on 2017/2/28.
 * 文章分解界面
 * 2017/3/1
 * 暂时先搁着，评论放本地操作了先做评论
 */

public class IntroResolveActivity extends BaseTitleActivity implements IntroResolveContract.View,
        View.OnClickListener, PictureConfig.OnSelectResultCallback {

    private static int REQUEST_CODE_IMAGE = 9999;

    @BindView(R.id.layout_import)
    View layoutImport;
    @BindView(R.id.iv_article_cover)
    ImageView ivArticleCover;
    @BindView(R.id.et_article_link)
    EditText etArticleLink;
    @BindView(R.id.et_article_title)
    EditText etArticleTitle;
    @BindView(R.id.et_article_abstract)
    EditText etArticleAbstract;
    @BindView(R.id.btn_article_import_link)
    Button btnArticleImportLink;
    @BindView(R.id.root_view)
    ScrollView rootView;
    @BindView(R.id.edit)
    RichEditor edit;

    //编辑菜单相关
    @BindView(R.id.layout)
    LinearLayout layoutMenu;
    @BindView(R.id.layout_text)
    LinearLayout layoutText;
    @BindView(R.id.hl)
    LinearLayout layoutHl;
    @BindView(R.id.btn_font)
    ImageButton btnFont;

    private EditMenuView menuView;


    private String content;         //文章内容
    private String url;             //文章原链接


    private String[] items;
    private List<ChannelBean.NormalBean> beans;
    private PopArticleType pop;

    private boolean imgFlag;            //选择图片标志

    private IntroResolveContract.Presenter mPresenter;
    private CustomCollectBean mCustomCollectBean;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_resolve);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("课堂概要");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("完成", this);

        edit.setPlaceholder("请输入内容");
        edit.setFontSize(10);
        edit.setPadding(10, 10, 10, 10);

        etArticleTitle.requestFocus();

        menuView = new EditMenuView(layoutMenu);
    }

    @Override
    public void initData() {
        new IntroResolvePresenter(this);

        String intro = getIntent().getStringExtra("intro");
        if (intro != null)
            edit.setHtml(intro);

    }

    @OnClick({R.id.iv_article_cover, R.id.btn_article_import_link, R.id.btn_font, R.id.btn_jiacu, R.id.btn_xieti, R.id.btn_center, R.id.btn_left, R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.btn_undo, R.id.btn_redo, R.id.btn_color, R.id.btn_img, R.id.btn_video,})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.headBackButton:
                showCloseDialog();
                WindowUtil.closeInputMethod(this);
                break;

            //保存键
            case R.id.headRightButton:
                save();
                WindowUtil.closeInputMethod(this);
                break;

            //导入链接按钮
            case R.id.btn_article_import_link:
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
                            ToastUtil.showShort(IntroResolveActivity.this, "请先下载一个文件管理器");
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(IntroResolveActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(IntroResolveActivity.this);
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
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        Uri fileUri = Uri.fromFile(FileStorage.getVideoCacheFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(intent, 999);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(IntroResolveActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(IntroResolveActivity.this);
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
                    PictureConfig.getInstance().init(options).openPhoto(IntroResolveActivity.this, IntroResolveActivity.this);
                } else if (type == 2) {
                    PictureConfig.getInstance().init(options).startOpenCamera(IntroResolveActivity.this, IntroResolveActivity.this);
                }
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(IntroResolveActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(IntroResolveActivity.this);
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
                LogUtil.i(text);
                if (!TextUtils.isEmpty(text)) {
                    if (layoutImport.getVisibility() == View.VISIBLE) {
                        layoutImport.setVisibility(View.GONE);
                    }
                    getBaseHeadView().getHeadRightButton().setText("保存");
                    getBaseHeadView().getHeadRightButton().setVisibility(View.VISIBLE);
                } else {
                    getBaseHeadView().getHeadRightButton().setVisibility(View.GONE);
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

    //返回上一个界面
    private void showCloseDialog() {
        if (TextUtils.isEmpty(edit.getHtml())) {
            finish();
        } else {
            DialogUtil.showInputDialog(this, false, null, getString(R.string.dialog_out),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
        }
    }


    //选择照片
    private void chooseImg() {
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
                                .setIsCrop(!imgFlag)        //imgflaf = false 表示选择的是封面
                                .setAspectRatio(imgFlag ? 0 : 4, imgFlag ? 0 : 3)
                                .create();
                PictureConfig.getInstance().init(options).openPhoto(IntroResolveActivity.this, IntroResolveActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(IntroResolveActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(IntroResolveActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });
    }

    //保存文章
    private void save() {
        submit();
    }

    private void submit() {
        String token = UserManager.getInstance().getToken();
        if (!UserManager.getInstance().isLogin(this)) {
            return;
        }
        content = edit.getHtml();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "内容不能为空");
            return;
        }

        Intent intent = getIntent();
        intent.putExtra("intro", edit.getUnityHtml());
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void showSaveSuccess() {
        ToastUtil.showShort(this, getString(R.string.editor_save_success));
        setResult(Activity.RESULT_OK);
        finish();
    }

    //上传图片成功
    @Override
    public void showUploadingSuccess(String id, String url) {
        //表示富文本插入图片s
        if (id.startsWith("a")) {
            edit.uploadFinish(id, url);
        } else {
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
    public void setPresenter(IntroResolveContract.Presenter presenter) {
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
        if (requestCode == IntroResolveContract.REQUEST && resultCode == IntroResolveContract.RESULTE) {
            content = data.getStringExtra(Constant.INTENT_DATA);
        }

        if (requestCode == 999 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String file = UriUtils.getPath(this, uri);
                String a = "a" + System.currentTimeMillis();
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
        mAlertDialog = null;
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
