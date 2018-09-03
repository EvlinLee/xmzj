package com.gxtc.huchuan.ui.circle.home;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleHomeShieldDialogV5;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.DynamicUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.SystemTools;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.PraiseTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import cn.jzvd.JZVideoPlayer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.Constant.ResponseCode.LOGINRESPONSE_CODE;

/**
 * Describe:圈子动态精华列表
 * Created by ALing on 2017/6/8 .
 */

public class EssenceDymicListActivity extends BaseTitleActivity implements
        EssenceDymicListContract.View {

    private static final int CIRCLE_WEB_REQUEST = 1 << 3;

    private static final String TAG = EssenceDymicListActivity.class.getSimpleName();
    @BindView(R.id.rc_list)      RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.editTextBodyLl)  LinearLayout                edittextbody;
    @BindView(R.id.panel_root)      KPSwitchFSPanelLinearLayout mPanelRoot;
    @BindView(R.id.et_simple_edit)  EditText                    mEditText;
    @BindView(R.id.btn_simple_send) Button                      mBtnSend;

    private EssenceDymicListContract.Presenter mPresenter;
    private CircleDynamicAdapter               mAdapter;
    private CommentConfig                      commentConfig;
    private int                                id;
    public CircleHomeBean                         mbean;
    private List<CircleHomeBean> mDatas = new ArrayList<>();
    private CircleHomeShieldDialogV5 shieldDialog;
    private AlertDialog            mSureDialog;
    private int                      shieldPosition;
    private UMShareUtils             shareUtils;
    private AlertDialog              mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essence_dymic_list);

    }

    @Override
    public void initView() {
//        super.initView();

        getBaseHeadView().showTitle(getString(R.string.title_essence_list));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    @Override
    public void initData() {
        if (getIntent().getIntExtra("groupId", 0) != 0) {
            id = getIntent().getIntExtra("groupId", 0);
        } /*else {
            bean = (CircleBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
            id = bean.getId();
        }*/

        new EssenceDymicListPresenter(id, this);
        mPresenter.getEssenceList(false);

    }

    @Override
    public void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getEssenceList(true);
                mRcList.reLoadFinish();
            }
        });
        mSwipeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edittextbody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });

        KeyboardUtil.attach(this, mPanelRoot, new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                Log.d(TAG, String.format("Keyboard is %s", isShowing ? "showing" : "hiding"));
                if (isShowing) {
                    mPanelRoot.setVisibility(View.VISIBLE);
                } else {
                    mPanelRoot.setVisibility(View.GONE);
                }
            }
        });
        KPSwitchConflictUtil.attach(mPanelRoot, null, mEditText,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            mEditText.clearFocus();
                        } else {
                            mEditText.requestFocus();
                        }
                    }
                });
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserManager.getInstance().isLogin()) {
                    Toast.makeText(EssenceDymicListActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPresenter != null) {
                    //发布评论
                    String content = mEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(EssenceDymicListActivity.this, "评论内容不能为空...",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (commentConfig.commentType) {
                        case PUBLIC:
                            mPresenter.addComment(content, commentConfig,id);
                            break;
                        case REPLY:
                            mPresenter.addCommentReply(content, commentConfig);
                            break;
                    }

                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void setPresenter(EssenceDymicListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showEssenceList(List<CircleHomeBean> datas) {
        if (mAdapter == null) {
            mAdapter = new CircleDynamicAdapter(false,mRcList, this, this, datas,
                    new int[]{R.layout.item_circle_home_noimgv5, R.layout.item_circle_home_videov5, R.layout.item_circle_home_threeimgv2});
            mRcList.setAdapter(mAdapter);
        } else {
            mRcList.notifyChangeData(datas, mAdapter);
        }
        setAdapterListener();
    }

    private void setAdapterListener() {
        mAdapter.setOnCommentAndPraiseClickListener(
                new CircleDynamicAdapter.OnCommentAndPraiseClickListener() {
                    @Override
                    public void onNickNameClick(int groupPosition, int position,
                            CommentListTextView.CommentInfo mInfo) {
                        //如果是自己的 弹出Dialog 显示  复制 删除
                        if (UserManager.getInstance().isLogin()) {

                            if (mInfo.getNickUserCode().equals(
                                    UserManager.getInstance().getUserCode())) {
                                showCopyOrRemoveDialg(groupPosition, position, mInfo);
                            }
                        }

                        //如果是别的  显示 评论输入框
                        Log.d("CircleMainActivity", "onNickNameClickmInfo:" + mInfo);
                    }



                    @Override
                    public void onToNickNameClick(int groupPosition, int position,
                            CommentListTextView.CommentInfo mInfo) {
                        Log.d("CircleMainActivity", "onToNickNameClickmInfo:" + mInfo);

                    }

                    @Override
                    public void onCommentItemClick(int groupPosition, int position,
                            CircleCommentBean mInfo) {


                    }

                    @Override
                    public void onCommentOtherClick() {
                        Log.d("CircleMainActivity", "mInfo:onCommentOtherClick:");
                        //如果按到的是显示全部  去服务器请求更多

                    }

                    @Override
                    public void onClick(int groupPosition, int position,
                            PraiseTextView.PraiseInfo mPraiseInfo) {
                        Log.d("CircleMainActivity", "mPraiseInfo:" + mPraiseInfo);
                        //用户名被点击  显示更多。
                    }

                    @Override
                    public void onPraiseOtherClick() {
                        Log.d("CircleMainActivity", "mPraiseInfo:onPraiseOtherClick");

                    }

                    @Override
                    public void onCommentItemLongClick(int groupPosition, int commentPosition,
                            CircleCommentBean commentItem , final CommentConfig comConfig, View v) {

                    }

                    @Override
                    public void onPopCommentClick(CommentConfig commentConfig) {
                        mPresenter.showEditTextBody(commentConfig);
                    }

                    @Override
                    public void onCommentItemClick(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            if (!commentConfig.targetUserCode.equals(
                                    UserManager.getInstance().getUserCode())) {
                                mPresenter.showEditTextBody(commentConfig);

                            } else {
                                showRemoreCommentItemDialog(commentConfig);

                            }
                        }
                    }

                    @Override
                    public void onCancelZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.support(UserManager.getInstance().getToken(), commentConfig);

                        } else {
                            Toast.makeText(EssenceDymicListActivity.this, "请先登录",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDianZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.support(UserManager.getInstance().getToken(), commentConfig);

                        } else {
                            Toast.makeText(EssenceDymicListActivity.this, "请先登录",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onMoreCommentClick(CommentConfig commentConfig) {
                        DynamicDetialActivity.startActivity(EssenceDymicListActivity.this,commentConfig.groupInfoId + "");
                    }

                    @Override
                    public void onRemoveCommentClick(final CommentConfig commentConfig) {
                        DynamicUtil.showDoalogConfiromRemove(EssenceDymicListActivity.this, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mPresenter != null) {
                                    mPresenter.removeComment(commentConfig);
                                }
                            }
                        });

                    }
                });

        mAdapter.setOnShareAndLikeItemListener(new CircleDynamicAdapter.OnShareAndLikeItemListener() {


            @Override
            public void goToCircleHome(CircleHomeBean bean) {
                PersonalInfoActivity.startActivity(EssenceDymicListActivity.this, bean.getUserCode());
//                PersonalHomePageActivity.startActivity(CircleMainActivity.this, bean.getUserCode());

            }

            @Override
            public void notifyRecyclerView(String userCode) {
                for (CircleHomeBean bean : mAdapter.getDatas()) {
                    if (!TextUtils.isEmpty(bean.getUserCode())) {
                        if (bean.getUserCode().equals(userCode)) {
                            bean.setIsAttention(1);
                        }
                    }
                }
//                mRecyclerView.notifyChangeData(mAdapter.getDatas(), mAdapter);
                mRcList.notifyChangeData();
            }

            @Override
            public void onShield(int position, CircleHomeBean bean, View view) {
                shieldPosition = position;
                mbean = bean;
                showShieldDialog(view);
            }

            @Override
            public void shareItem(int position, CircleHomeBean bean) {
                mbean = bean;
                shareIssueNamic(bean);
            }
        });
    }

    private void showShieldDialog(View view) {
        shieldDialog = new CircleHomeShieldDialogV5(this);
        shieldDialog.showAnim(new PopEnterAnim().duration(200))
                .dismissAnim(new PopExitAnim().duration(200))
                .cornerRadius(4).bubbleColor(Color.parseColor("#ffffff")).anchorView(view). setBgAlpha(0.1f).location(10,
                -50).gravity(Gravity.BOTTOM).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_shield_dynamic://屏蔽此条动态

                                if (UserManager.getInstance().isLogin())
                                    shieldDynamic(shieldPosition, mbean, "1");
                                else GotoUtil.goToActivity(EssenceDymicListActivity.this,
                                        LoginAndRegisteActivity.class);

                                break;
                            case R.id.tv_shield_user://屏蔽此人动态
                                if (UserManager.getInstance().isLogin()) {

                                    mSureDialog = DialogUtil.createDialog2(EssenceDymicListActivity.this,
                                            "确定屏蔽此人所有动态？", "取消", "确定", new DialogUtil.DialogClickListener() {
                                                @Override
                                                public void clickLeftButton(View view) {
                                                    mSureDialog.dismiss();
                                                }

                                                @Override
                                                public void clickRightButton(View view) {
                                                    mSureDialog.dismiss();
                                                    shieldDynamic(shieldPosition, mbean, "0");
                                                }
                                            });
                                    mSureDialog.show();

                                } else GotoUtil.goToActivity(EssenceDymicListActivity.this,
                                        LoginAndRegisteActivity.class);

                                break;
                        }
                    }
                });

        shieldDialog.show();
    }

    private void shieldDynamic(int position, CircleHomeBean bean, String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", type);
        if ("1".equals(type)) {
            map.put("targetId", String.valueOf(bean.getId()));
        } else if ("0".equals(type)){
            map.put("targetId", String.valueOf(bean.getUserCode()));
        }

        final String tempType = type;
        final int tempPosition = position;
        final CircleHomeBean tempBean = bean;

        Subscription subShield = AllApi.getInstance().shield(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if ("1".equals(tempType)) {
                            mRcList.removeData(mAdapter, tempPosition);

                        } else if ("0".equals(tempType)) {
                            String userCode = tempBean.getUserCode();

                            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                                CircleHomeBean bean = mAdapter.getDatas().get(i);
                                if (userCode.equals(bean.getUserCode())) {
                                    mAdapter.getDatas().remove(bean);
                                    i--;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mRcList.notifyChangeData();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(EssenceDymicListActivity.this, errorCode,
                                message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subShield);
    }

    private void shareIssueNamic(final CircleHomeBean bean) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(EssenceDymicListActivity.this);

                        if (bean.getPicList().size() > 0) {
                            shareUtils.shareCircleIssueDynamic(bean.getPicList().get(0).getPicUrl(),
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        } else {
                            shareUtils.shareCircleIssueDynamic(R.mipmap.person_icon_head_share,
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        }

                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                if (flag == 0) {//复制链接
                                    ClipboardManager cmb = (ClipboardManager) EssenceDymicListActivity.this.getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                                    cmb.setText(bean.getUrl().trim());
                                    ToastUtil.showShort(EssenceDymicListActivity.this, "复制成功");
                                } else if (flag == 1) {//收藏
                                    if (UserManager.getInstance().isLogin())
                                        collect(UserManager.getInstance().getToken(),
                                                String.valueOf(bean.getId()));
                                    else GotoUtil.goToActivity(EssenceDymicListActivity.this,
                                            LoginAndRegisteActivity.class);
                                } else if (flag == 2) {//投诉
                                    if (UserManager.getInstance().isLogin()) {
                                        ReportActivity.jumptoReportActivity(EssenceDymicListActivity.this,String.valueOf(bean.getId()),"5");
                                    } else {
                                       GotoUtil.goToActivity(EssenceDymicListActivity.this,LoginAndRegisteActivity.class);
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(EssenceDymicListActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(EssenceDymicListActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    private void collect(String token, String id) {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "4");
        map.put("bizId", id);

        Subscription subCollect =
                AllApi.getInstance()
                      .saveCollection(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                          @Override
                          public void onSuccess(Object data) {
                              switch (mbean.getIsCollect()){
                                  case 0:
                                      mbean.setIsCollect(1);
                                      ToastUtil.showShort(MyApplication.getInstance(), "收藏成功");
                                      break;
                                  case 1:
                                      mbean.setIsCollect(0);
                                      ToastUtil.showShort(MyApplication.getInstance(), "取消收藏成功");
                                      break;
                              }
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              ToastUtil.showShort(MyApplication.getInstance(), message);
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,subCollect);
    }




    private void showRemoreCommentDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,
                mMenuRemoveCommentItems.toArray(contents), null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    mPresenter.removeComment(commentConfig);
                }
                actionSheetDialog.dismiss();
            }
        });
    }


    private void showRemoreCommentItemDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,
                mMenuRemoveCommentItems.toArray(contents), null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    mPresenter.removeCommentItem(commentConfig);
                }
                actionSheetDialog.dismiss();
            }
        });
    }


    private ArrayList<String> mMenuRemoveCommentItems = new ArrayList<>();

    {
        mMenuRemoveCommentItems.add("删除");

    }

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    {
        mMenuItems.add(new DialogMenuItem("收藏", 0));
        mMenuItems.add(new DialogMenuItem("下载", 0));
    }

    private void showCopyOrRemoveDialg(int groupPosition, int position,
            CommentListTextView.CommentInfo mInfo) {
        final NormalListDialog dialog = new NormalListDialog(this, mMenuItems);
        dialog.title("请选择")//
              .isTitleShow(false)//
              .itemPressColor(Color.parseColor("#85D3EF"))//
              .itemTextColor(Color.parseColor("#303030"))//
              .itemTextSize(15)//
              .cornerRadius(2)//
              .widthScale(0.75f)//
              .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
//                T.showShort(this, mMenuItems.get(position).mOperName);
                dialog.dismiss();
            }
        });
    }


    @Override
    public void update2DeleteCircle(int circleId) {
        List<CircleHomeBean> circleItems = mAdapter.getDatas();
        for (int i = 0; i < circleItems.size(); i++) {
            if (circleId == circleItems.get(i).getId()) {
                circleItems.remove(i);
                mRcList.notifyChangeData();
                ToastUtil.showShort(MyApplication.getInstance(),"删除成功");
                return;
            }
        }
    }

    @Override
    public void update2AddFavorite(int circlePosition, ThumbsupVosBean data) {
        if (mAdapter != null) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
            item.getThumbsupVos().add(data);
            item.setIsDZ(1);

            mRcList.notifyItemChanged(circlePosition);
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
    }

    @Override
    public void update2DeleteFavort(int circlePosition, String favortId) {
        Log.d(TAG, "update2DeleteFavort: " + circlePosition);
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        String userCode = UserManager.getInstance().getUserCode();
        CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<ThumbsupVosBean> items = item.getThumbsupVos();
        for (int i = 0; i < items.size(); i++) {
            if (userCode.equals(items.get(i).getUserCode())) {
                Log.d(TAG, userCode + " 相同  " + items.get(i).getUserCode());
                items.remove(i);
                item.setIsDZ(0);
//                mAdapter.notifyDataSetChanged();
                mRcList.notifyItemChanged(circlePosition);
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            } else {
                Log.d(TAG, userCode + " 不同  " + items.get(i).getUserCode());
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CircleCommentBean addItem) {
        Log.d(TAG, "update2AddComment: 添加  评论" + addItem.toString());
        if (addItem != null) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
            item.getCommentVos().add(addItem);
//            mAdapter.notifyDataSetChanged();
            mRcList.notifyItemChanged(circlePosition);
        }
        //清空评论文本
        mEditText.setText("");
    }

    @Override
    public void update2AddComment(int circlePosition, List<CircleCommentBean> data) {
        Log.d(TAG, "update2AddComment: 添加  评论  List List");

        if (data != null) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
            item.getCommentVos().addAll(data);
//            mAdapter.notifyDataSetChanged();
            mRcList.notifyItemChanged(circlePosition);
        }
    }

    @Override
    public void update2DeleteCircleItem(int groupInfoId, int commentPosition) {

    }

    @Override
    public void update2DeleteComment(int circlePosition, int commentId) {
        CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<CircleCommentBean> items = item.getCommentVos();
        for (int i = 0; i < items.size(); i++) {
            if (commentId == (items.get(i).getId())) {
                Log.d(TAG, "update2DeleteComment: " + commentId + " " + items.get(i).getId());
                item.setLiuYan(item.getLiuYan() - 1);
                items.remove(i);
//                mAdapter.notifyDataSetChanged();
                mRcList.notifyItemChanged(circlePosition);
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            }
        }
    }

    @Override
    public void showRefreshFinish(List<CircleHomeBean> datas) {
        mRcList.notifyChangeData(datas, mAdapter);
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoadMore(List<CircleHomeBean> datas) {
        if (datas.size()>0){
            if (mRcList!=null&&mAdapter!=null) {

                mRcList.changeData(datas, mAdapter);
            }
        }else{
            mRcList.loadFinish();
        }

    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
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
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        edittextbody.setVisibility(visibility);

//        measureCircleItemHighAndCommentItemOffset(commentConfig);

        if (View.VISIBLE == visibility) {
            if (!TextUtils.isEmpty(commentConfig.targetUserName)) {
                mEditText.setHint("回复：" + commentConfig.targetUserName);
            } else {
                mEditText.setHint("回复");
            }
            mEditText.requestFocus();
            //弹出键盘
            SystemTools.showSoftInput(mEditText.getContext(), mEditText);

        } else if (View.GONE == visibility) {
            //隐藏键盘
            mEditText.setHint("");
            SystemTools.hideSoftInput(mEditText.getContext(), mEditText);
        }
    }


    @Override
    public void showEmpty() {
        mAdapter = new CircleDynamicAdapter(false,mRcList, this, this,
                new ArrayList<CircleHomeBean>(),
                new int[]{R.layout.item_circle_home_noimgv2, R.layout.item_circle_home_videov5, R.layout.item_circle_home_threeimgv2});

        mRcList.setAdapter(mAdapter);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(false);
            }
        });
        setAdapterListener();

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
//        if (mAdapter.getItemCount() == 0) {
//            mScrollView.setVisibility(View.VISIBLE);
//        } else {
        ToastUtil.showShort(this, info);
//        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CIRCLE_WEB_REQUEST && resultCode == RESULT_OK) {
            CircleHomeBean bean = (CircleHomeBean) data.getSerializableExtra("newData");
            Log.d(TAG, "onActivityResult: 到这里应该有数据了嗓");
            if (bean != null) {

                for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                    if (mAdapter.getDatas().get(i).getId() == bean.getId()) {
                        mAdapter.getDatas().remove(i);
                        mAdapter.getDatas().add(i, bean);
                        mRcList.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
        if (requestCode == 0 && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            mPresenter.getEssenceList(true);
            mRcList.reLoadFinish();
            mRcList.scrollToPosition(0);
        }
        if (requestCode == Constant.requestCode.REFRESH_CIRCLE_HOME
                && resultCode == LOGINRESPONSE_CODE) {
            initData();
        }
        if (requestCode == Constant.requestCode.CIRCLE_DZ
                && resultCode == Constant.ResponseCode.CIRCLE_RESULT_DZ) {
            int circleId = data.getIntExtra("circle_id", -1);
            int isDz = data.getIntExtra("is_dz", -1);
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i).getId() == circleId) {
                    mDatas.get(i).setIsDZ(isDz);
                    if (0 == isDz) {

                        mDatas.get(i).setDianZan(mDatas.get(i).getDianZan() - 1);
                    } else if (1 == isDz) {
                        mDatas.get(i).setDianZan(mDatas.get(i).getDianZan() + 1);
                    }
                    mRcList.notifyItemChanged(i);
                }
            }
        }
        if (requestCode == Constant.requestCode.CIRCLE_DT_REFRESH
                && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            mPresenter.getEssenceList(true);
            mRcList.reLoadFinish();
            mRcList.scrollToPosition(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mPresenter.destroy();
    }
}
