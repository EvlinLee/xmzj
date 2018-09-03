package com.gxtc.huchuan.ui.circle.file.folder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FileMoveSelectorListAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.circle.file.CircleFileActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/3/30.
 */

public class FileMoveSlectorActivity extends BaseTitleActivity implements FolderListContract.View,
        View.OnClickListener, BaseRecyclerAdapter.OnReItemOnClickListener {
    private static final String TAG                     = "FolderListActivity";
    public static final  int    LOGINREQUEST            = 1;
    private final        int    GOTO_CIRCLE_REQUESTCODE = 1 << 3;
    @BindView(R.id.recyclerview)      RecyclerView                 recyclerview;
    @BindView(R.id.tv_move_file_name) TextView                     mTvMoveFileName;
    private                           String                       title;
    private                           FolderListContract.Presenter presenter;
    private                           FileMoveSelectorListAdapter  mFolderListAdapter;
    private                           String                       type;
    private                           int                          groupId;
    private                           String                       mUsercode;
    private                           int                          mMemberType;
    private                           CircleBean                   mBean;
    private                           AlertDialog                  dialog;
    private                           CircleFileBean               mCircleFileBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemoveselector_list);
    }

    @Override
    public void initView() {
        new FolderListPresenter(this);
        Intent intent = getIntent();

        mBean = (CircleBean) intent.getSerializableExtra("bean");
        groupId = mBean.getId();
        mUsercode = mBean.getUserCode();
        mMemberType = mBean.getMemberType();
        mCircleFileBean = (CircleFileBean) intent.getSerializableExtra("file");
        getBaseHeadView().showHeadRightButton("取消", this);
        getBaseHeadView().showTitle("移动文件");
        setTvMoveFileName(mCircleFileBean.getFileName());

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    public static void startActivity(Activity context, CircleBean circleBean,
            CircleFileBean circleFileBean, int requestCode) {
        Intent intent = new Intent(context, FileMoveSlectorActivity.class);
        intent.putExtra("bean", circleBean);
        intent.putExtra("file", circleFileBean);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void initListener() {

        recyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getData(true, groupId, mFolderListAdapter.getItemCount(),
                        Integer.MAX_VALUE);
            }
        });
    }

    @Override
    public void initData() {
        presenter.getData(false, groupId, 0, Integer.MAX_VALUE);
    }

    private void showChangeFolderDialog(final FolderBean bean) {
        DialogUtil.showTopicInputDialog(this, "填写文件夹名", "请填写新的文件夹名", bean.getFolderName(),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            presenter.createFolder(UserManager.getInstance().getToken(),
                                    bean.getGroupId(), str, "" + bean.getId());
                        }
                    }
                });
    }

    private void showCreateFolderDialog() {
        DialogUtil.showTopicInputDialog(this, "填写文件夹名", "请填写新的文件夹名", null,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = (String) v.getTag();
                        if (TextUtils.isEmpty(str)) {

                        } else {
                            presenter.createFolder(UserManager.getInstance().getToken(), groupId,
                                    str, null);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.HeadRightImageButton:
                showCreateFolderDialog();
                break;

            case R.id.headRightButton:
                finish();
                break;

            case R.id.tv_input_search:
                //这里跳到空的文件列表进行搜索
                CircleFileActivity.startActivity(this, mBean, -1, 1);
                break;
        }
    }

    @Override
    public void showFolderData(List<FolderBean> datas) {
        if (mFolderListAdapter == null) {
            mFolderListAdapter = new FileMoveSelectorListAdapter(this, datas,
                    R.layout.item_circle_selector_folder, recyclerview);
            mFolderListAdapter.setFolderClickListener(new FileMoveSelectorListAdapter.OnFolderClickListener() {
                @Override
                public void onFolderClick(FolderBean bean) {

                }
            });
            recyclerview.setAdapter(mFolderListAdapter);

        }
    }

    @Override
    public void showLoMore(List<FolderBean> datas) {
        if (mFolderListAdapter != null) {
            recyclerview.changeData(datas, mFolderListAdapter);
        }
    }


    @Override
    public void setPresenter(FolderListContract.Presenter presenter) {
        this.presenter = presenter;
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
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void loadFinish() {
        if (recyclerview != null) {
            recyclerview.loadFinish();
        }
    }

    @Override
    public void showFileData(List<CircleFileBean> datas) {


    }

    @Override
    public void createFolder(int grouid, String folderName, String fileid) {
        List<FolderBean> list = mFolderListAdapter.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == Integer.valueOf(fileid)) {
                list.get(i).setFolderName(folderName);
                recyclerview.notifyItemChanged(i);
                return;
            }
        }

    }


    @Override
    public void createFolder(FolderBean bean) {
        if (mFolderListAdapter != null && mFolderListAdapter.getItemCount() > 0) {
            mFolderListAdapter.getList().add(0, bean);
            recyclerview.notifyChangeData();
        } else {
            ArrayList<FolderBean> folderBeen = new ArrayList<>();
            folderBeen.add(bean);
            recyclerview.setAdapter(mFolderListAdapter);
        }
    }

    private void setTvMoveFileName(String name) {
        String tempStr = "<a style=\"color: #999999;\">将</a><a style=\"color: #333333;\">" + name + "</a><a style=\"color: #999999;\"> 移动到：</a>";
        mTvMoveFileName.setText(Html.fromHtml(tempStr));
    }


    public void showDeleteFolderDialog(final Context context, final FolderBean bean) {
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add("删除文件夹以及里面的文件");
        final String[] contents = new String[itemList.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(context,
                itemList.toArray(contents), null);
        dialog.cancelMarginTop(1).
                isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginBottom(
                0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                getResources().getColor(R.color.black)).cancelText(
                getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (position) {
                        case 0:
                            //删除文件夹以及里面的文件
                            removeFolder(bean);
                            break;

                    }
                    String animType = contents[position];
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeFolder(final FolderBean bean) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = DialogUtil.showInputDialog(this, false, "", "确认删除文件夹以及里面的文件？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            presenter.deleteFolder(UserManager.getInstance().getToken(), groupId,
                                    bean.getId());
                        } else {
                            Toast.makeText(FileMoveSlectorActivity.this, "登录后才能操作",
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                });
    }


    @Override
    public void deleteFolder(int grouid, int folderId) {
        List<FolderBean> list = mFolderListAdapter.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == folderId) {
                list.remove(i);
                recyclerview.notifyChangeData();
            }
        }
    }

    @Override
    public void saveFolder(int fileId, String fileName, int folderId) {
        mCircleFileBean.setFolderId((long) folderId);
        Intent intent = getIntent();
        intent.putExtra("file", mCircleFileBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        Log.d(TAG, "onItemClick: " + position);
        if (mFolderListAdapter != null) {
            FolderBean folderBean = mFolderListAdapter.getList().get(position);
            CircleFileActivity.startActivity(this, mBean, folderBean.getId(), 2);
//            LiveIntroActivity.startActivity(this, id);
        }
    }


    public void onViewClicked() {}

    @OnClick({R.id.tv_newfolder, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_newfolder:
                showCreateFolderDialog();
                break;
            case R.id.btn_submit:
                submit();

                break;
        }
    }

    private void submit() {
        if (UserManager.getInstance().isLogin()) {
            if (mFolderListAdapter.getSelectedFolderBean() == null) {
                Toast.makeText(this, "请先选择文件夹", Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.saveFile(UserManager.getInstance().getToken(),
                    mCircleFileBean.getId().intValue(), null,
                    mFolderListAdapter.getSelectedFolderBean().getId());
        } else {
            Toast.makeText(this, "要登录后才能操作", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter != null) presenter.destroy();
    }
}
