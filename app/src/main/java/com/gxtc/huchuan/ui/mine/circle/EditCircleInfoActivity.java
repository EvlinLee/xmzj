package com.gxtc.huchuan.ui.mine.circle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.helper.PermissionsHelper;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleInfoBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ColorSelectDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 编辑圈子详情页
 */
public class EditCircleInfoActivity extends BaseTitleActivity implements View.OnClickListener,
        EditCircleInfoContract.View, PictureConfig.OnSelectResultCallback {


    @BindView(R.id.layout_bottom)
    View layoutHl;
    @BindView(R.id.editor)
    RichEditor mEditor;
    @BindView(R.id.edit_title)
    EditText editTitle;
    @BindView(R.id.img_icon)
    ImageView imgIcon;

    private EditMenuView mMenuView;

    private String cover;
    private int mGroupId;       //圈子id
    private int mIsMy;
    private int id;             //介绍id
    private boolean isCover;
    private boolean createFlag;     //创建圈子flag
    private HashMap<String, Object> map;
    public String text = "";
    private EditCircleInfoContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_circle_info);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        mGroupId = getIntent().getIntExtra("groupId", -1);
        mIsMy = getIntent().getIntExtra("isMy", -1);
        createFlag = getIntent().getBooleanExtra("flag", false);

        mMenuView = new EditMenuView(layoutHl);

        mEditor.setPlaceholder("请输入内容");
        mEditor.setFontSize(10);
        mEditor.setPadding(10, 10, 10, 10);

        getBaseHeadView().showTitle(getString(R.string.title_circle_detailed));
        getBaseHeadView().showBackButton(this);
//        1 管理员  2 圈主
        if (mIsMy == 1 || mIsMy == 2) {
            getBaseHeadView().showHeadRightButton("保存", this);
        } else {
            mMenuView.getLayoutHl().setVisibility(View.GONE);
            editTitle.setEnabled(false);
            mEditor.setEditEnable(false);
            imgIcon.setEnabled(false);
        }

    }

    @Override
    public void initData() {
        super.initData();
        new EditCircleInfoPresenter(this);
        mPresenter.getGroupDesc(mGroupId);
    }

    @OnClick({R.id.img_icon, R.id.btn_font, R.id.btn_jiacu, R.id.btn_xieti, R.id.btn_center, R.id.btn_left, R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.btn_undo, R.id.btn_redo, R.id.btn_color, R.id.btn_img, R.id.btn_video,})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                if (createFlag) {
                    DialogUtil.showInputDialog(this, false, null,
                            "不保存填写圈子详情介绍页，将导致您创建的圈子不能通过审核，确认退出？", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GotoUtil.goToActivity(EditCircleInfoActivity.this,MainActivity.class);
                                    finish();
                                }
                            });
                } else {
                    showCloseDialog();
                }
                WindowUtil.closeInputMethod(this);
                break;

            case R.id.headRightButton:
                modifyDetail();
                WindowUtil.closeInputMethod(this);
                break;

            //上传封面
            case R.id.img_icon:
                isCover = true;
                takePhoto();
                WindowUtil.closeInputMethod(this);
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
                break;//撤销

            //字体颜色
            case R.id.btn_color:
                showColorDialog();
                break;

            //插入图片
            case R.id.btn_img:
                isCover = false;
                takePhoto();
                break;

            //插入视频
            case R.id.btn_video:
                showVideoDialog();
                break;

        }
    }

    //返回上一个界面
    private void showCloseDialog() {
        if (TextUtils.isEmpty(mEditor.getHtml())) {
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
                            ToastUtil.showShort(EditCircleInfoActivity.this, "请先下载一个文件管理器");
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(EditCircleInfoActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(EditCircleInfoActivity.this);
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
                        mAlertDialog = DialogUtil.showDeportDialog(EditCircleInfoActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(EditCircleInfoActivity.this);
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
                    mEditor.setTextColor(color);
                }
            });
        }
        mColorDialog.show();
    }

    private void modifyDetail() {
        String token = UserManager.getInstance().getToken();
        map = new HashMap<>();

        if (!(TextUtils.isEmpty(cover))) {
            map.put("cover", cover);
        }
        if (TextUtils.isEmpty(editTitle.getText().toString().trim()) || editTitle.getText().length() < 4) {
            ToastUtil.showShort(this, "标题必须是4-25字");
            return;
        }

        if (TextUtils.isEmpty(mEditor.getHtml())) {
            ToastUtil.showShort(this, "内容不能为空");
            return;
        }

        map.put("groupId", mGroupId);
        map.put("token", token);
        map.put("id", id);
        map.put("title", editTitle.getText().toString());
        map.put("content", mEditor.getUnityHtml());
        mPresenter.saveGroupDesc(map);
    }


    /**
     * 选取图片
     */
    private void takePhoto() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        performRequestPermissions(getString(R.string.txt_permission), pers, 1011,
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
                                        .setIsCrop(isCover)
                                        .setAspectRatio(isCover ? 1 : 0, isCover ? 1 : 0)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(EditCircleInfoActivity.this, EditCircleInfoActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(EditCircleInfoActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(EditCircleInfoActivity.this);
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
                    PictureConfig.getInstance().init(options).openPhoto(EditCircleInfoActivity.this, EditCircleInfoActivity.this);
                } else if (type == 2) {
                    PictureConfig.getInstance().init(options).startOpenCamera(EditCircleInfoActivity.this, EditCircleInfoActivity.this);
                }
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(EditCircleInfoActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(EditCircleInfoActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        });
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showGroupDesc(CircleInfoBean bean) {
        id = bean.getId();
        ImageHelper.loadImage(EditCircleInfoActivity.this, imgIcon, bean.getCover(),
                R.drawable.circle_compile_cover_icon_camera);
        editTitle.setText(bean.getTitle());
        editTitle.setSelection(editTitle.getText().length());
        mEditor.setHtml(bean.getContent());
    }

    @Override
    public void showSaveGroupDesc(Object o) {
        if (mGroupId == -1) {
            text = "圈子创建成功";
        } else {
            text = getString(R.string.modify_success);
        }
        Subscription showSub = Observable.timer(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mAlertDialog = DialogUtil.showDeportDialog(EditCircleInfoActivity.this, true, text, getString(R.string.title_notice_message), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    GotoUtil.goToActivity(EditCircleInfoActivity.this, MainActivity.class);
                                    EditCircleInfoActivity.this.finish();
                                }
                                mAlertDialog.dismiss();
                            }
                        });
                    }
                });
        RxTaskHelper.getInstance().addTask(this, showSub);
//        EventBusUtil.post(new EventCreateCirlceBean());
//        this.finish();
    }

    @Override
    public void showUploadingSuccess(String id, String url) {
        if (id.startsWith("b")) {
            cover = url;
            ImageHelper.loadRound(this, imgIcon, url, 2);

        } else {
            mEditor.uploadFinish(id, url);
        }
    }


    @Override
    public void showUploadingFailure(String info) {
        ToastUtil.showShort(this, "上传失败");
    }

    @Override
    public void showUploadVideoSuccess(String id, String url) {
        mEditor.uploadVideoFinish(id, url);
    }

    @Override
    public void showUploadVideoFailure(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void setPresenter(EditCircleInfoContract.Presenter presenter) {
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
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果是创建圈子调过来 不填写详情页 则提示弹窗
            if (createFlag) {
                DialogUtil.showInputDialog(this, false, null, "不保存填写圈子详情介绍页，将导致您创建的圈子不能通过审核，确认退出？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GotoUtil.goToActivity(EditCircleInfoActivity.this,MainActivity.class);
                                finish();
                            }
                        });

                //修改详情页
            } else {
                showCloseDialog();
            }
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
            if (isCover) {
                id = "b" + System.currentTimeMillis();
            } else {
                id = "a" + System.currentTimeMillis();
                mEditor.insertImage(id, "file:///android_asset/load_animation.gif", "");
            }
            mPresenter.uploadingFile(id, media.getPath());
        }
        if (media.getType() == FunctionConfig.TYPE_VIDEO) {
            if (media.getPath() != null) {
                String file = media.getPath();
                if (!TextUtils.isEmpty(file) && file.endsWith(".mp4")) {
                    String a = "a" + System.currentTimeMillis();
                    mEditor.insertVideo1(this, a, "file:///android_asset/load_animation.gif");
                    mPresenter.uploadingVideo(a, file);
                } else {
                    ToastUtil.showShort(this, "请选择mp4格式的视频");
                }

            }
        }
    }
}
