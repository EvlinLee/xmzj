package com.gxtc.huchuan.ui.circle.file.filelist;


import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleFileAdapter;
import com.gxtc.huchuan.adapter.CircleFileAuditedAdapter;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.CircleFileBeanDao;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


import static com.gxtc.huchuan.ui.live.conversation.PPTSelectActivity.REQUEST_CODE_IMG;

/**
 * Created by Gubr on 2017/6/10.
 *
 */

public class FileAuditedListFragment extends BaseTitleFragment implements
        FileListContract.AuditedView {
    private static final String TAG = "FileAuditedListFragment";
    @BindView(R.id.recyclerview) RecyclerView             mRecyclerview;
    private                      BroadcastReceiver        mReceiver;
    private                      DownloadManager          mDownloadManager;
    private                      CircleFileAuditedAdapter mAdapter;
    FileListContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_fileauditlist, container, false);
    }

    @Override
    public void initData() {
        mPresenter = (FileListContract.Presenter) getActivity();
        mPresenter.getAuditedDatas(0, 15);
    }

    @Override
    public void setPresenter(FileListContract.Presenter presenter) {

    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {

    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {

    }

    @Override
    public void showNetError() {

    }


    @Override
    public void setAuditedDatas(Integer start, List<CircleFileBean> datas) {
        Log.d(TAG, datas.toString());
        if (start == 0 && datas.size() == 0) {
            Log.d(TAG, "setAuditedDatas: 这里运行了吗。");
            showEmpty();
            return;
        }
        if (start == 0) {
            Log.d(TAG, "setAuditedDatas: 审核更新走这里");
            if (mAdapter==null) {


                mAdapter = new CircleFileAuditedAdapter(getContext(), datas, R.layout.item_circle_file_audited,
                        mRecyclerview);


                mAdapter.setOnFileClickListener(new CircleFileAuditedAdapter.OnFileClickListener() {
                    @Override
                    public void onOpenFileClick(final CircleFileBean bean) {
                        String[] pers = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        performRequestPermissions(getString(R.string.txt_permission), pers,
                                REQUEST_CODE_IMG, new BaseTitleActivity.PermissionsResultListener() {
                                    @Override
                                    public void onPermissionGranted() {
                                        checkFileIsexist(bean);
                                    }

                                    @Override
                                    public void onPermissionDenied() {
                                        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, getString(R.string.pre_scan_notice_msg),
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if(v.getId() == R.id.tv_dialog_confirm){
                                                            JumpPermissionManagement.GoToSetting(getActivity());
                                                        }
                                                        mAlertDialog.dismiss();
                                                    }
                                                });
                                    }
                                });
                    }

                    @Override
                    public void onMoveClick(View view, CircleFileBean bean) {
                        Log.d(TAG, "onMoveClick: 移动" + bean);
//                showMoveFolder(bean);
                    }

                    @Override
                    public void onDeleteClick(View view, CircleFileBean bean) {
//                showRemovedDialog(bean);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false);
                mRecyclerview.setLayoutManager(linearLayoutManager);
                mRecyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
                mRecyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        mPresenter.getAuditedDatas(mAdapter.getItemCount(), 15);
                    }
                });
                mRecyclerview.setAdapter(mAdapter);
            }else{
                mRecyclerview.notifyChangeData(datas,mAdapter);
            }
        } else {
            if (datas.size() > 0) {
                mRecyclerview.changeData(datas, mAdapter);
            } else {
                mRecyclerview.loadFinish();
            }
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

    AlertDialog dialog;

    public void checkFileIsexist(final CircleFileBean bean) {
        CircleFileBean circleFileBean = checkFileIsExistbyDao(bean);
        if (circleFileBean.getLoadId() <= 0) {
            dialog = DialogUtil.showInputDialog(getActivity(), true, "是否下载?", "此文件还没有下载\n是否要下载文件",
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
                mDownloadManager = (DownloadManager) getContext().getSystemService(downloadService);
            }
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(circleFileBean.getLoadId());
            Cursor cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                long aLong = cursor.getLong(
                        cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                long totalBytes = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                String string1 = cursor.getString(
                        cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                Log.d("CircleFileActivity", "string1" + string1);
                String file = string1.replace("file://", "");

                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String string = cursor.getString(
                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (aLong == totalBytes && string1 != null && (status == 8 || status == 200)) {
                    Uri parse = null;
                    if (Build.VERSION.SDK_INT >= 24) {
                        parse = FileProvider.getUriForFile(getContext(),
                                this.getContext().getApplicationContext().getPackageName() + ".FileProvider",
                                new File(file));
                    } else {
                        parse = Uri.fromFile(new File(file));
                    }

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
                    try {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setDataAndNormalize(parse);
                        intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent1.setDataAndType(parse, type);
                        startActivity(intent1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "打开失败", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "下载中", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlertDialog = null;
    }

    public void getDownLoadManage(CircleFileBean bean) {
        if (mDownloadManager == null) {
            String downloadService = Context.DOWNLOAD_SERVICE;
            mDownloadManager = (DownloadManager) getContext().getSystemService(downloadService);
        }


        Uri                     uri     = Uri.parse(bean.getFileUrl());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                bean.getFileName());
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
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d("CircleFileActivity", reference + "");
                DownloadManager.Query query  = new DownloadManager.Query();
                DownloadManager.Query query1 = query.setFilterById(reference);
                Cursor                query2 = mDownloadManager.query(query1);
                if (query2.moveToFirst()) {
                    for (int i = 0; i < query2.getColumnCount(); i++) {
                        Log.d("CircleFileActivity", "query2.getType(i):" + query2.getType(i));


                        Log.d("CircleFileActivity" + i, query2.getColumnName(i));
                    }


                }
            }
        };
        getActivity().registerReceiver(mReceiver, filter);
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

        }

        pausedDownloads.close();
    }


}
