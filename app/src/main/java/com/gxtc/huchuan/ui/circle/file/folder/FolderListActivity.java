package com.gxtc.huchuan.ui.circle.file.folder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FolderListAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleFileShieldDialogV5;
import com.gxtc.huchuan.pop.PopFileAction;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.circle.file.CircleFileActivity;
import com.gxtc.huchuan.ui.circle.file.filelist.FileAuditActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/3/30.
 */

public class FolderListActivity extends BaseTitleActivity implements FolderListContract.View,
        View.OnClickListener, BaseRecyclerAdapter.OnReItemOnClickListener {
    private static final String TAG                     = "FolderListActivity";
    public static final  int    LOGINREQUEST            = 1;
    private final        int    GOTO_CIRCLE_REQUESTCODE = 1 << 3;
    @BindView(R.id.recyclerview) RecyclerView                 recyclerview;
    private                      String                       title;
    private                      FolderListContract.Presenter presenter;
    private FolderListAdapter        mFolderListAdapter;
    private String                   type;
    private int                      groupId;
    private String                   mUsercode;
    private int                      mMemberType;
    private CircleBean               mBean;
    private AlertDialog              dialog;
    private CircleFileShieldDialogV5 shieldDialog;
    private List<Integer>          mGroupIds  = new ArrayList<>();//同步的圈子id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folde_list);

    }

    @Override
    public void initView() {
        new FolderListPresenter(this);
        Intent intent = getIntent();

        groupId = intent.getIntExtra("data", -1);
        mUsercode = intent.getStringExtra("usercode");
        mMemberType = intent.getIntExtra("memberType", 0);
        mBean = (CircleBean) intent.getSerializableExtra("bean");
        getBaseHeadView().showBackButton(this);
        if (mMemberType != 0) {
            getBaseHeadView().showHeadRightImageButton(R.drawable.person_circle_manage_icon_add, this);
        }
        getBaseHeadView().showTitle("文件");


        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {

        recyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getData(true, groupId, mFolderListAdapter.getItemCount(),15);
            }
        });
    }

    @Override
    public void initData() {
        presenter.getData(false, groupId, 0,15);
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
    private void showSysDialog() {
        DialogUtil.showSysDialog(this,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.tv_issue_tongbu:
                                Intent intent = new Intent(FolderListActivity.this, SyncIssueInCircleActivity.class);
                                intent.putExtra("default", "-1");
                                FolderListActivity. this.startActivityForResult(intent, 666);
                                break;
                            case R.id.tv_cancel:
                                break;
                            case R.id.tv_sure:
                                break;
                        }

                    }
                });
    }

    @Override
    @OnClick({R.id.tv_input_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.HeadRightImageButton:
                if (mMemberType == 0) {

                    showCreateFolderDialog();
                }else{
                    showCreateFileDialog(view);
                }
                break;
            case R.id.tv_input_search:
                //这里跳到空的文件列表进行搜索
                CircleFileActivity.startActivity(this, mBean, -1, 1);
                break;
        }
    }
    private PopFileAction mPopComment;
    private void showEditDialog(View v,final FolderBean bean){
        if(mPopComment == null){
            mPopComment = new PopFileAction(this,false);
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int dalta = (getResources().getDimensionPixelSize(R.dimen.px700dp) - v.getWidth()) / 2;
        mPopComment.showAtLocation(v, Gravity.CENTER|Gravity.TOP, location[0] - dalta, location[1] - 30);
        mPopComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sys_btn:
                        showSysDialog();
                        break;
                    case R.id.move_btn:
                        break;
                    case R.id.rename_btn:
                        if (UserManager.getInstance().isLogin()) {
                            if (mMemberType != 0) {
                                showChangeFolderDialog(bean);
                            }
                        } else {
                            showLoginDialog();
                        }
                        break;
                    case R.id.down_btn:
                        break;
                    case R.id.delete_btn:
                        if (UserManager.getInstance().isLogin()) {
                            if (mMemberType != 0) {
                                showDeleteFolderDialog(FolderListActivity.this, bean);
                            }
                        } else {
                            showLoginDialog();
                        }
                        break;
                }
            }
        });
    }


    @Override
    public void showFolderData(final List<FolderBean> datas) {
        if (mFolderListAdapter == null) {
            mFolderListAdapter = new FolderListAdapter(this, datas, R.layout.item_circle_folder,
                    recyclerview);
            mFolderListAdapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
                @Override
                public void onItemLongClick(View v, int position) {
                    showEditDialog(v,datas.get(position));
                }
            });
            mFolderListAdapter.setFolderClickNameClickListener(
                    new FolderListAdapter.OnFolderClickListener() {
                        @Override
                        public void onOpenFolderClick(FolderBean bean) {
                            CircleFileActivity.startActivity(FolderListActivity.this, mBean,
                                    bean.getId(), 2);
                        }

                        @Override
                        public void onRenameClick(View view, FolderBean bean) {
                            if (UserManager.getInstance().isLogin()) {
                                if (mMemberType != 0) {
                                    showChangeFolderDialog(bean);
                                }
                            } else {
                                showLoginDialog();
                            }
                        }

                        @Override
                        public void onDeleteClick(View view, FolderBean bean) {
                            if (UserManager.getInstance().isLogin()) {
                                if (mMemberType != 0) {
                                    showDeleteFolderDialog(FolderListActivity.this, bean);
                                }
                            } else {
                                showLoginDialog();
                            }
                        }
                    });
            recyclerview.setAdapter(mFolderListAdapter);

        }else{
            recyclerview.notifyChangeData(datas,mFolderListAdapter);
        }
    }

    private void showLoginDialog() {

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


    private void showCreateFileDialog(View view){
        shieldDialog = new CircleFileShieldDialogV5(this);
        shieldDialog
                .showAnim(null)
                .dismissAnim(null)
                .anchorView(view)
                .dimEnabled(true)
                .dimEnabled(false)
                .gravity(Gravity.BOTTOM)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_create_folder:
                                showCreateFolderDialog();
                                break;

                            case R.id.tv_audit_file:
                                FileAuditActivity.startActivity(FolderListActivity.this,mBean,4);
                                break;
                        }
                    }
                });

        shieldDialog.show();
    }


    @Override
    public void createFolder(FolderBean bean) {
        if (mFolderListAdapter != null && mFolderListAdapter.getItemCount() > 0) {
            mFolderListAdapter.getList().add(0, bean);
            recyclerview.notifyChangeData();
        } else {
            ArrayList<FolderBean> folderBeen = new ArrayList<>();
            folderBeen.add(bean);
            mFolderListAdapter = new FolderListAdapter(this, folderBeen,
                    R.layout.item_circle_folder, recyclerview);
            mFolderListAdapter.setFolderClickNameClickListener(
                    new FolderListAdapter.OnFolderClickListener() {
                        @Override
                        public void onOpenFolderClick(FolderBean bean) {
                            Log.d(TAG, "onOpenFolderClick: "+mBean.getMemberType());
                            CircleFileActivity.startActivity(FolderListActivity.this, mBean,
                                    bean.getId(), 2);
//                            if (mBean.getMemberType()==0){
//                            }else{
//                                Log.d(TAG, "onOpenFolderClick: 打开FileAUditActivity");
//                                FileAuditActivity.startActivity(FolderListActivity.this,mBean,bean.getId());
//                            }

                        }

                        @Override
                        public void onRenameClick(View view, FolderBean bean) {
                            if (UserManager.getInstance().isLogin()) {
                                if (mMemberType != 0) {
                                    showChangeFolderDialog(bean);
                                }
                            } else {
                                showLoginDialog();
                            }
                        }

                        @Override
                        public void onDeleteClick(View view, FolderBean bean) {
                            if (UserManager.getInstance().isLogin()) {
                                if (mMemberType != 0) {
                                    showDeleteFolderDialog(FolderListActivity.this, bean);
                                }
                            } else {
                                showLoginDialog();
                            }
                        }
                    });
            recyclerview.setAdapter(mFolderListAdapter);
        }
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
        dialog = DialogUtil.showInputDialog(this, false, "", "确认删除文件夹以及里面的文件？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().isLogin()) {
                    presenter.deleteFolder(UserManager.getInstance().getToken(),groupId,bean.getId());
                } else {
                    Toast.makeText(FolderListActivity.this, "登录后才能操作", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }
        });
    }


    @Override
    public void deleteFolder(int grouid, int folderId) {
        List<FolderBean> list = mFolderListAdapter.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId()==folderId) {
                list.remove(i);
                recyclerview.notifyChangeData();
            }
        }
    }

    @Override
    public void saveFolder(int fileId, String name, int fileName) {

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
        if (mFolderListAdapter != null) {
            FolderBean folderBean = mFolderListAdapter.getList().get(position);
            CircleFileActivity.startActivity(this, mBean, folderBean.getId(), 2);
        }
    }


    public void onViewClicked() {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            recyclerview.reLoadFinish();
            presenter.getData(true, groupId, 0,15);

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
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter != null) presenter.destroy();
    }
}
