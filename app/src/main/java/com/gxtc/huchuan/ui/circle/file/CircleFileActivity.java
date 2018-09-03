package com.gxtc.huchuan.ui.circle.file;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleFileAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.CircleFileBeanDao;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.model.AppenGroudBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopFileAction;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleEditActivity;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.circle.file.filelist.FileAuditActivity;
import com.gxtc.huchuan.ui.circle.file.folder.FileMoveSlectorActivity;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 圈子－文件
 * Created by Steven on 17/4/25.
 */

public class CircleFileActivity extends BaseTitleActivity implements CircleFileContract.View,
        View.OnClickListener {
    private static final String TAG = "CircleFileActivity";
    @BindView(R.id.recyclerView) RecyclerView       listView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;

    CircleFileContract.Presenter mPresenter;
    @BindView(R.id.et_input_search) EditText mEtInputSearch;

    private CircleFileAdapter mAdapter;
    private int               id;
    private BroadcastReceiver mReceiver;
    private DownloadManager   mDownloadManager;
    private int REQUEST_CODE_IMG = 1;
    private String      mUsercode;
    private CircleBean  mBean;
    private int         mFolderId;
    private int         mType;
    private String      mKeyWord;
    private AlertDialog dialog;
    private static final int MOVE_REQUESTCODE = 1 << 3;
    private Subscription sub;
    private List<Integer> mGroupIds = new ArrayList<>();//同步的圈子id
    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circlefile_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("文件");
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (mType) {
                    case 0://全文件
                        mPresenter.getData(false);
                        break;
                    case 1://全文件搜索
                        if (mKeyWord == null) mKeyWord = "";
                        mPresenter.queryFile(mKeyWord, id, null, 0, 15);
                        break;
                    case 2://文件夹的文件
                        mPresenter.getFolderFile(mFolderId, 0);
                        break;
                }
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                switch (mType) {
                    case 0://全文件
                        mPresenter.getData(false);
                        break;
                    case 1://全文件搜索
                        mPresenter.queryFile(mKeyWord, id, null, mAdapter.getItemCount(), 15);
                        break;
                    case 2://文件夹的文件
                        mPresenter.getFolderFile(mFolderId, mAdapter.getItemCount());
                        break;
                }
            }
        });

        mEtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mKeyWord = mEtInputSearch.getText().toString();
                } else {
                    mKeyWord = "";
                }
                search(mKeyWord);
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initData() {
//        type  全文件0   全文件搜索1  文件夹的文件2
        mBean = (CircleBean) getIntent().getSerializableExtra("bean");
        mFolderId = getIntent().getIntExtra("folderId", -1);
        mType = getIntent().getIntExtra("type", -1);
        id = mBean.getId();
        mUsercode = mBean.getUserCode();
        if (mFolderId != -1)
            getBaseHeadView().showHeadRightImageButton(R.drawable.person_circle_manage_icon_add,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFileManager();
                        }
                    });
        new CircleFilePresenter(this, id);
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        receiverDownLoad();
        initAdapter();

        switch (mType) {
            //全文件
            case 0:
                mPresenter.getData(false);
                break;

            //全文件搜索
            case 1:
                break;

            //文件夹的文件
            case 2:
                mPresenter.getFolderFile(mFolderId, 0);
                break;
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
                            ToastUtil.showShort(CircleFileActivity.this, "请先下载一个文件管理器");
                        }

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CircleFileActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CircleFileActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    private void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new CircleFileAdapter(this, new ArrayList<CircleFileBean>(),
                    R.layout.item_circle_file, listView);

            mAdapter.setOnFileClickListener(new CircleFileAdapter.OnFileClickListener() {
                @Override
                public void onOpenFileClick(final CircleFileBean bean) {
                    downLoadFile(bean);
                }

                @Override
                public void onMoveClick(View view, CircleFileBean bean) {
                    showMoveFolder(bean);
                }

                @Override
                public void onDeleteClick(View view, CircleFileBean bean) {
                    showRemovedDialog(bean);
                }

                @Override
                public void onRenameClick(View view, CircleFileBean bean) {
                    showChangeFolderDialog(bean);
                }
            });

            mAdapter.setOnReItemOnLongClickListener(
                    new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
                        @Override
                        public void onItemLongClick(View v, int position) {
                            if(mBean != null && mBean.getMemberType() != 0){
                                showEditDialog(v, mAdapter.getList().get(position));
                            }
                        }
                    });
        }

        listView.setAdapter(mAdapter);
    }

    private PopFileAction mPopComment;

    private void showEditDialog(View v, final CircleFileBean bean) {
        if (mPopComment == null) {
            mPopComment = new PopFileAction(this, true);
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int dalta = (getResources().getDimensionPixelSize(R.dimen.px700dp) - v.getWidth()) / 2;
        mPopComment.showAtLocation(v, Gravity.CENTER | Gravity.TOP, location[0] - dalta,
                location[1] - 30);
        mPopComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sys_btn:
                        showSysDialog(bean);
                        break;
                    case R.id.move_btn:
                        showMoveFolder(bean);
                        break;
                    case R.id.rename_btn:
                        showChangeFolderDialog(bean);
                        break;
                    case R.id.down_btn:
                        downLoadFile(bean);
                        break;
                    case R.id.delete_btn:
                        showRemovedDialog(bean);
                        break;
                }
            }
        });
    }

    IssueListDialog mIssueListDialog;

    private void showSysDialog(final CircleFileBean bean) {
        getlistAppendGroup(bean);
        if (mIssueListDialog == null) {
            mIssueListDialog = new IssueListDialog(this, new String[]{});
            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(CircleFileActivity.this,
                                SyncIssueInCircleActivity.class);
                        intent.putExtra("default", -1);
                        intent.putIntegerArrayListExtra(Constant.INTENT_DATA,
                                mAppenGroudBean.getGroupIds());
                        CircleFileActivity.this.startActivityForResult(intent, 666);
                    } else if (v.getId() == R.id.tv_sure) {
                        SysGroup(bean);
                        mIssueListDialog.dismiss();
                    }
                }
            });
        }
        mIssueListDialog.show();

    }

    AppenGroudBean mAppenGroudBean;

    public void getlistAppendGroup(final CircleFileBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("linkId", bean.getId() + "");
        map.put("type", "3");//同步类型1、文章；2、课堂；3、文件
        MineApi.getInstance().listAppendGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<AppenGroudBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mAppenGroudBean = (AppenGroudBean) data;
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(CircleFileActivity.this, message);
                    }
                }));

    }

    //同步文章到圈子 同步类型1、文章；2、课堂；3、文件
    private void SysGroup(CircleFileBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        }
        map.put("linkId", bean.getId() + "");
        map.put("type", "3");
        MineApi.getInstance().appendToGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(CircleFileActivity.this, "同步成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(CircleFileActivity.this, message);
                    }
                }));
    }

    public void downLoadFile(final CircleFileBean bean) {
        String[] pers = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, REQUEST_CODE_IMG,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        checkFileIsexist(bean);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CircleFileActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CircleFileActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });

    }

    private void showChangeFolderDialog(final CircleFileBean bean) {
        DialogUtil.showTopicInputDialog(this, "填写文件夹名", "请填写新的文件夹名", bean.getFileName(),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            renameFile(bean, str);
                        }
                    }
                });
    }

    private void renameFile(final CircleFileBean bean, String fileName) {
        int fileId   = Integer.parseInt(bean.getId() + "");
        int folderId = Integer.parseInt(bean.getFolderId() + "");
        sub = CircleApi.getInstance().update(UserManager.getInstance().getToken(), fileId, fileName,
                folderId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mPresenter.getFolderFile(mFolderId, 0);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showLong(CircleFileActivity.this, message);
                    }
                }));
    }

    private void showMoveFolder(CircleFileBean bean) {
        FileMoveSlectorActivity.startActivity(this, mBean, bean, MOVE_REQUESTCODE);
    }


    private void search(String keyWord) {
        if (mType == 2) {
            mPresenter.queryFile(keyWord, id, mFolderId, 0, 15);
        } else {
            mPresenter.queryFile(keyWord, id, null, 0, 15);
        }

    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void setPresenter(CircleFileContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
        if(swipeLayout.isRefreshing()){
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showData(List<CircleFileBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
    }

    private void showRemovedDialog(final CircleFileBean circleFileBean) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,
                mMenuRemoveCommentItems.toArray(contents), null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    if (UserManager.getInstance().isLogin()) {
                        if (UserManager.getInstance().getUserCode().equals(mUsercode)) {
                            mPresenter.deleteCircleFile(UserManager.getInstance().getToken(),
                                    CircleFileActivity.this.id, "" + circleFileBean.getId());
                        } else {
                            Toast.makeText(CircleFileActivity.this, "不让删除",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                actionSheetDialog.dismiss();
            }
        });
    }


    private ArrayList<String> mMenuRemoveCommentItems = new ArrayList<>();
    {
        mMenuRemoveCommentItems.add("删除");

    }


    private void openFile(CircleFileBean circleFileBean1) {

    }

    @Override
    public void showRefreshFinish(List<CircleFileBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMore(List<CircleFileBean> datas) {
        listView.changeData(datas, mAdapter);
    }

    @Override
    public void updateByDeleteCircleFile(int groupid, String fileId) {
        if (groupid == id) {
            List<CircleFileBean> list = mAdapter.getList();
            for (int i = 0; i < list.size(); i++) {
                if (fileId.equals("" + list.get(i).getId())) {
                    list.remove(i);
                    break;
                }
            }
            listView.notifyChangeData();
        }
    }

    @Override
    public void showFolderFile(int start, final List<CircleFileBean> datas) {
        if (start == 0) {
            listView.notifyChangeData(datas, mAdapter);
        } else {
            listView.changeData(datas, mAdapter);
        }
    }

    @Override
    public void showQueryFile(int start, List<CircleFileBean> datas) {
        if (start == 0) {
            listView.notifyChangeData(datas, mAdapter);
        } else {
            listView.changeData(datas, mAdapter);
        }
    }

    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showSaveCirccleFileResult(String message) {
        if (mBean.getMemberType() == 0) {
            Toast.makeText(this, "上传成功  等待审核", Toast.LENGTH_SHORT).show();
        }
        switch (mType) {
            //全文件
            case 0:
                mPresenter.getData(false);
                break;

            //全文件搜索
            case 1:
                if (mKeyWord == null) mKeyWord = "";
                mPresenter.queryFile(mKeyWord, id, null, 0, 15);
                break;

            //文件夹的文件
            case 2:
                mPresenter.getFolderFile(mFolderId, 0);
                break;
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

    public void checkFileIsexist(final CircleFileBean bean) {
        CircleFileBean circleFileBean = checkFileIsExistbyDao(bean);
        if(circleFileBean == null)  return;
        if (circleFileBean.getLoadId() <= 0) {
            dialog = DialogUtil.showInputDialog(this, true, "是否下载?", "此文件还没有下载\n是否要下载文件",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDownLoadManage(bean);
                            dialog.dismiss();
                        }
                    });

        } else {
            if (mDownloadManager == null) {
                String downloadService = Context.DOWNLOAD_SERVICE;
                mDownloadManager = (DownloadManager) getSystemService(downloadService);
            }
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(circleFileBean.getLoadId());
            Cursor cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                long aLong = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                long totalBytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String string1 = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                //下载完成  打开文件
                if (aLong == totalBytes && string1 != null && (status == 8 || status == 200)) {
                    String file = Uri.decode(string1.replace("file://", ""));
                    Uri parse ;

                    if (Build.VERSION.SDK_INT >= 24) {
                        parse = FileProvider.getUriForFile(this,
                                this.getApplicationContext().getPackageName() + ".FileProvider",
                                new File(file));
                    } else {
                        parse = Uri.fromFile(new File(file));
                    }

                    //判断是否有后缀，没后缀一律不给打开
                    if(file.contains(".")){
                        String   substring   = file.substring(file.lastIndexOf("."), file.length());
                        String   type        = "";
                        String[] stringArray = getResources().getStringArray(R.array.type_array_name);
                        String[] stringType  = getResources().getStringArray(R.array.type_array_type);
                        for (int i = 0; i < stringArray.length; i++) {
                            if (stringArray[i].equals(substring)) {
                                type = stringType[i];
                                break;
                            }
                        }
                        Log.d("CircleFileActivity", "type" + type);
                        if (type.equals("")) {
                            type = "*/*";
                        }
                        ToastUtil.showLong(CircleFileActivity.this, "文件已下载至：" + file);
                        try {
                            Intent intent1 = new Intent(Intent.ACTION_VIEW);
                            intent1.setDataAndNormalize(parse);
                            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent1.setDataAndType(parse, type);
                            startActivity(intent1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showNoSupportDialog();
                        }

                    }else{
                        showNoSupportDialog();
                    }

                } else {
                    Toast.makeText(this, "下载中", Toast.LENGTH_SHORT).show();
                    String statusMsg = "";
                    switch (status) {
                        case DownloadManager.STATUS_PAUSED:
                            statusMsg = "STATUS_PAUSED";
                            break;
                        case DownloadManager.STATUS_PENDING:
                            statusMsg = "STATUS_PENDING";
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            statusMsg = "STATUS_RUNNING";
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            statusMsg = "STATUS_SUCCESSFUL";
                            break;
                        case DownloadManager.STATUS_FAILED:
                            statusMsg = "STATUS_FAILED";
                            break;

                        default:
                            statusMsg = "未知状态";
                            break;
                    }
                }
                cursor.close();
            } else {
                getDownLoadManage(bean);
            }
        }
    }


    public void getDownLoadManage(CircleFileBean bean) {
        if(bean == null)    return;
        if (mDownloadManager == null) {
            String downloadService = Context.DOWNLOAD_SERVICE;
            mDownloadManager = (DownloadManager) getSystemService(downloadService);
        }

        Uri                     uri     = Uri.parse(bean.getFileUrl());
        String filePath = FileStorage.appName + "/" + FileStorage.filePathName;
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(filePath, bean.getFileName());
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        long refrenceId = mDownloadManager.enqueue(request);
        bean.setLoadId(refrenceId);
        GreenDaoHelper.getInstance().getSeeion().getCircleFileBeanDao().insertOrReplace(bean);
    }


    public void receiverDownLoad() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ToastUtil.showShort(CircleFileActivity.this,"文件下载完成");
                if(mDownloadManager != null){
                    long loadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    for(CircleFileBean bean : mAdapter.getList()){
                        if(loadId == bean.getLoadId()){
                            checkFileIsexist(bean);
                        }
                    }
                }
            }
        };
        registerReceiver(mReceiver, filter);
    }


    public void loadStop(DownloadManager downloadManager) {
        DownloadManager.Query pausedDownloadQuery = new DownloadManager.Query();
        pausedDownloadQuery.setFilterByStatus(DownloadManager.STATUS_PAUSED);

        Cursor pausedDownloads = downloadManager.query(pausedDownloadQuery);

        // Find the column indexes for the data we require.
        int reasonIdx   = pausedDownloads.getColumnIndex(DownloadManager.COLUMN_REASON);
        int titleIdx    = pausedDownloads.getColumnIndex(DownloadManager.COLUMN_TITLE);
        int fileSizeIdx = pausedDownloads.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        int bytesDLIdx = pausedDownloads.getColumnIndex(
                DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);

        // Iterate over the result Cursor.
        while (pausedDownloads.moveToNext()) {
            // Extract the data we require from the Cursor.
            String title    = pausedDownloads.getString(titleIdx);
            int    fileSize = pausedDownloads.getInt(fileSizeIdx);
            int    bytesDL  = pausedDownloads.getInt(bytesDLIdx);

            // Translate the pause reason to friendly text.
            int    reason       = pausedDownloads.getInt(reasonIdx);
            String reasonString = "Unknown";
            switch (reason) {
                case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                    reasonString = "Waiting for WiFi";
                    break;
                case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                    reasonString = "Waiting for connectivity";
                    break;
                case DownloadManager.PAUSED_WAITING_TO_RETRY:
                    reasonString = "Waiting to retry";
                    break;
                default:
                    break;
            }

            // Construct a status summary
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n");
            sb.append(reasonString).append("\n");
            sb.append("Downloaded ").append(bytesDL).append(" / ").append(fileSize);

            // Display the status
            Log.d("DOWNLOAD", sb.toString());
        }

        // Close the result Cursor.
        pausedDownloads.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MOVE_REQUESTCODE) {
            CircleFileBean file = (CircleFileBean) data.getSerializableExtra("file");
            if (file != null && mAdapter != null) {
                List<CircleFileBean> list = mAdapter.getList();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(file.getId())) {
                        showMOveSuccessfulDialg();
                        if (!list.get(i).getFolderId().equals(file.getFolderId())) {
                            list.remove(i);
                            setResult(RESULT_OK);
                            listView.notifyChangeData();
                            return;
                        } else {
                            return;
                        }
                    }
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
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.requestCode.UPLOAD_FILE) {
            Uri    uri  = data.getData();
            String path = FileUtils.getPath(this, uri);

            if (path != null && !"".equals(path)) {
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

                ToastUtil.showShort(CircleFileActivity.this, "文件路径解析失败！");
            }

        }
    }


    private void showNoSupportDialog(){
        dialog = DialogUtil.showInputDialog(this, true, "提示", getString(R.string.circle_file_no_support), "", "确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    private void showMOveSuccessfulDialg() {

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
                     ToastUtil.showShort(CircleFileActivity.this, e.toString());
                 }
             }).launch();
    }

    ProgressDialog mProgressDialog;

    private void uploadFile(final int type, final File file) {
        if (UserManager.getInstance().isLogin()) {
            mProgressDialog = new ProgressDialog(CircleFileActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("文件正在努力上传中，请稍等...");
            mProgressDialog.setProgress(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            LoadHelper.uploadFile(LoadHelper.UP_TYPE_VIDEO, new LoadHelper.UploadCallback() {
                @Override
                public void onUploadSuccess(UploadResult result) {
                    mPresenter.saveCircleFile(UserManager.getInstance().getToken(), mBean.getId(), file, result.getUrl(), type, mFolderId);
                }

                @Override
                public void onUploadFailed(String errorCode, String msg) {
                    ToastUtil.showShort(CircleFileActivity.this, "上传出错");
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


    /**
     * @param type 全文件0   全文件搜索1  文件夹的文件2
     */
    public static void startActivity(Activity context, CircleBean bean, int folderId, int type) {
        Intent intent = new Intent(context, CircleFileActivity.class);
        intent.putExtra("bean", bean);
        intent.putExtra("folderId", folderId);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, 2);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        mPresenter.destroy();
        mAlertDialog = null;
        super.onDestroy();
    }
}
