package com.gxtc.huchuan.ui.circle.file.filelist;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FileAuditPageAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.CircleFileBeanDao;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by Gubr on 2017/6/10.
 *
 */

public class FileAuditActivity extends BaseTitleActivity implements View.OnClickListener,
        FileListContract.Presenter {
    private static final String TAG = "FileAuditActivity";
    private static final int MOVE_REQUESTCODE = 1 << 3;

    @BindView(R.id.tl_fileaudit_page_indicator) TabLayout mTlFileauditPageIndicator;
    @BindView(R.id.vp_fileaudit_viewpager)      ViewPager mVpFileauditViewpager;
    private static String[]       labTitles  = {"已审核", "未审核"};
    private        List<Fragment> mFragments = new ArrayList<>();
    private FileListContract.Source mSource;
    private CircleBean              mBean;
    private FileAuditPageAdapter mFileAuditPageAdapter;

    private FileAuditedListFragment auditedFragment;
    private FileAuditListFragment auditFragment;
    private int mFolderId;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileaudit);
        mSource = new FileAuditSource();
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("文件列表");
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        mBean = ((CircleBean) getIntent().getSerializableExtra("bean"));
        mFolderId = getIntent().getIntExtra("folderId",-1);
        initFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.HeadRightImageButton:
//                openFileManager();
                break;

            case R.id.headBackButton:
                finish();
                break;
        }
    }

    private void initFragment() {
        mFragments.clear();
        auditedFragment = new FileAuditedListFragment();
        auditFragment = new FileAuditListFragment();
        mFragments.add(auditedFragment);
        mFragments.add(auditFragment);
        mFileAuditPageAdapter=new FileAuditPageAdapter(getSupportFragmentManager(),mFragments,labTitles);
        mVpFileauditViewpager.setAdapter(mFileAuditPageAdapter);
        mTlFileauditPageIndicator.setupWithViewPager(mVpFileauditViewpager);
    }


    @Override
    public void start() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.destroy();
        mSource.destroy();
        mAlertDialog = null;
    }

    @Override
    public void destroy() {}

    @Override
    public void getAuditedDatas(final Integer start, Integer pageSize) {
        if (UserManager.getInstance().isLogin()) {
            String token = UserManager.getInstance().getToken();
            mSource.getFileDatas(token,mBean.getId(), start, pageSize, 1,
                    new ApiCallBack<List<CircleFileBean>>() {
                        @Override
                        public void onSuccess(List<CircleFileBean> data) {
                            if (auditedFragment instanceof FileListContract.AuditedView) {
                                ((FileListContract.AuditedView) auditedFragment).setAuditedDatas(
                                        start, data);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {

                        }
                    });
        }
    }

    @Override
    public void getAuditDatas(final Integer start, Integer pageSize) {
        if (UserManager.getInstance().isLogin()) {
            String token = UserManager.getInstance().getToken();
            mSource.getFileDatas(token,mBean.getId(), start, pageSize, 0,
                    new ApiCallBack<List<CircleFileBean>>() {
                        @Override
                        public void onSuccess(List<CircleFileBean> data) {
                            if (auditFragment instanceof FileListContract.AuditView) {
                                ((FileListContract.AuditView) auditFragment).setAuditDatas(start, data);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {

                        }
                    });
        }
    }

    @Override
    public FileListContract.Presenter getPresenter() {
        return this;
    }

    @Override
    public void auditFile(final CircleFileBean bean, final int i) {
        if (UserManager.getInstance().isLogin()) {

            mSource.auditFile(UserManager.getInstance().getToken(), bean.getId()+"", 1,mBean.getId() , new ApiCallBack<Void>() {
                @Override
                public void onSuccess(Void data) {
                    if (auditFragment instanceof FileListContract.AuditView){
                        ((FileListContract.AuditView) auditFragment).setAuditFileSuccessful(bean,i);
                    }
                    getAuditedDatas(0, 15);
                }

                @Override
                public void onError(String errorCode, String message) {

                }
            });
        }
    }

    public CircleFileBean checkFileIsExistbyDao(CircleFileBean bean) {
        List<CircleFileBean> list = GreenDaoHelper.getInstance().getSeeion().getCircleFileBeanDao().queryBuilder().where(
                CircleFileBeanDao.Properties.Id.eq(bean.getId())).build().list();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return bean;
        }
    }


    /**
     * 打开文件管理器
     */
    private void openFileManager() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 10010,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(intent, Constant.requestCode.UPLOAD_FILE);
                        } catch (android.content.ActivityNotFoundException ex) {
                            ToastUtil.showShort(FileAuditActivity.this, "请先下载一个文件管理器");
                        }

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(FileAuditActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(FileAuditActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MOVE_REQUESTCODE) {
            CircleFileBean file = (CircleFileBean) data.getSerializableExtra("file");
//            if (file != null && mAdapter != null) {
//                List<CircleFileBean> list = mAdapter.getList();
//                for (int i = 0; i < list.size(); i++) {
//                    if (list.get(i).getId().equals(file.getId())) {
//                        showMOveSuccessfulDialg();
//                        if (!list.get(i).getFolderId().equals(file.getFolderId())) {
//                            list.remove(i);
//                            setResult(RESULT_OK);
//                            listView.notifyChangeData();
//                            return;
//                        } else {
//                            return;
//                        }
//                    }
//                }
//            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.requestCode.UPLOAD_FILE) {
            Uri    uri  = data.getData();
            String path = FileUtils.getPath(this, uri);

            if (!"".equals(path)) {
                File file = new File(path);
//                getBaseLoadingView().showLoading(true);
                //这里判断类型
                //图片
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(
                        ".jpeg") || path.endsWith(".gif")) {
                    uploadFile(4, file);
//                    compression(path);
                } else if (path.endsWith(".doc")) {//文档

                    uploadFile(0, file);
                } else if (path.endsWith(".xls")) {//excel

                    uploadFile(1, file);
                } else if (path.endsWith(".ppt")) {//ppt

                    uploadFile(2, file);
                } else if (path.endsWith(".pdf")) {//pdf

                    uploadFile(3, file);
                } else if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(
                        ".rmvb") || path.endsWith(".wmv") || path.endsWith("rm") || path.endsWith(
                        ".flv") || path.endsWith(".mpg") || path.endsWith("mkv")) {//视频文件

                    uploadFile(5, file);
                } else if (path.endsWith(".exe")) {//exe执行文件

                    uploadFile(6, file);
                } else if (path.endsWith(".zip") || path.endsWith(".rar")) {//压缩文件

                    uploadFile(7, file);
                } else {

                    uploadFile(8, file);

                }


            } else {

                ToastUtil.showShort(FileAuditActivity.this, "文件路径解析失败！");
            }


        }
    }
    /**
     * 压缩图片
     */
    public void compression(String path) {
        //将图片进行压缩
        File file = new File(path);
        Luban.get(MyApplication.getInstance()).load(file)                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
             .setCompressListener(new OnCompressListener() {

                 // 压缩开始前调用，可以在方法内启动 loading UI
                 @Override
                 public void onStart() {
                 }

                 // 压缩成功后调用，返回压缩后的图片文件
                 @Override
                 public void onSuccess(final File file) {
                     uploadFile(4, file);

                 }

                 //  当压缩过去出现问题时调用
                 @Override
                 public void onError(Throwable e) {
//                        getBaseLoadingView().hideLoading();
                     ToastUtil.showShort(FileAuditActivity.this, e.toString());
                 }
             }).launch();
    }

    ProgressDialog mProgressDialog;

    private void uploadFile(final int type, final File file) {
        if (UserManager.getInstance().isLogin()) {
            mProgressDialog = new ProgressDialog(FileAuditActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("文件正在努力上传中，请稍等...");
            mProgressDialog.setProgress(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            LoadHelper.uploadFile(LoadHelper.UP_TYPE_VIDEO, new LoadHelper.UploadCallback() {
                @Override
                public void onUploadSuccess(UploadResult result) {
                    saveCircleFile(UserManager.getInstance().getToken(), mBean.getId(), file, result.getUrl(), type, mFolderId);
                }

                @Override
                public void onUploadFailed(String errorCode, String msg) {
                    ToastUtil.showShort(FileAuditActivity.this, "上传出错");
                    mProgressDialog.dismiss();
                }
            },new UIProgressListener() {
                @Override
                public void onUIProgress(long currentBytes, long contentLength, boolean done) {
                    mProgressDialog.setProgress((int) ((100 * currentBytes) / contentLength));
                    if (currentBytes == contentLength) {
                        mProgressDialog.dismiss();
                    }
                }
            }, file);

        }
    }

    private void saveCircleFile(String token, int id, File file, String url, int type,
            int folderId) {
        mSource.saveCircleFile(token, id, file, url, type, folderId, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                ToastUtil.showShort(FileAuditActivity.this,"保存成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                Toast.makeText(FileAuditActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void startActivity(Context context, CircleBean bean, int id) {
        Intent intent = new Intent(context, FileAuditActivity.class);
        intent.putExtra("bean", bean);
        intent.putExtra("folderId",id);
        context.startActivity(intent);
    }

}
