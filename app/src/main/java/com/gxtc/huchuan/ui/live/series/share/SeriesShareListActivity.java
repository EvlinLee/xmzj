package com.gxtc.huchuan.ui.live.series.share;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SeriesShareListAdapter;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;

import org.jsoup.Jsoup;

import java.util.Locale;

import butterknife.BindView;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 分享榜
 */
public class SeriesShareListActivity extends BaseTitleActivity implements View.OnClickListener,
        SeriesShareListContract.View {

    private final String countStr = "推荐了<font color=\"#0ab70e\">%s</font>个朋友过来听课";

    @BindView(R.id.recyclerview)  RecyclerView mRecyclerview;
    @BindView(R.id.ll_topic_root) LinearLayout llTopicRoot;

    private SeriesShareListAdapter            mAdapter;
    private SeriesShareListContract.Presenter mPresenter;
    private AlertDialog                       mAlertDialog;
    private SeriesPageBean                    mSeriesBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_share);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_share_notice));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initListener() {
        mRecyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    @Override
    public void initData() {
        new SeriesShareListPresenter(this);
        mSeriesBean = (SeriesPageBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        mPresenter.getData(mSeriesBean.getId());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.tv_item_topic_share_list_goshare:
                shareDialog();
                break;
        }
    }

    private void shareDialog() {
        if(mSeriesBean == null) return ;
        //这里跳出去分享
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        String   title = mSeriesBean.getSeriesname();
                        String desc = Jsoup.parse(mSeriesBean.getIntroduce()).body().text();
                        desc  = TextUtils.isEmpty(desc) ? title : desc;
                        String uri = mSeriesBean.getHeadpic();

                        UMShareUtils shareUtils = new UMShareUtils(SeriesShareListActivity.this);
                        shareUtils.shareClassInviteFree(uri, title, desc, mSeriesBean.getShareUrl());
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(SeriesShareListActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(SeriesShareListActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }


    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(SeriesShareListActivity.this,
                false, null, getString(R.string.token_overdue),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            ReLoginUtil.ReloginTodo(SeriesShareListActivity.this);
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK){
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareMessage(targetId,type,bean.liuyan);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan){
        String title = mSeriesBean.getSeriesname();
        String img = mSeriesBean.getHeadpic();
        String id = mSeriesBean.getId();
        ImMessageUtils.shareMessage(targetId, type, id, title, img, CircleShareHandler.SHARE_SERIES, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(SeriesShareListActivity.this,"分享成功");
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage
                            (liuyan,targetId,type);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(SeriesShareListActivity.this,"分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }


    @Override
    public void setPresenter(SeriesShareListContract.Presenter presenter) {
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
        ToastUtil.showShort(this, "暂时没有人邀请，赶快做第一个推荐人吧！");
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
    public void showData(TopicShareListBean datas) {
        if (datas.getDatas().size() > 0) {
            mRecyclerview.setVisibility(View.VISIBLE);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
            mRecyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
            mAdapter = new SeriesShareListAdapter(this, datas.getDatas(), R.layout.item_series_share_list);
            setHeadView(datas);
            mRecyclerview.setAdapter(mAdapter);
        } else {
            View      view    = LayoutInflater.from(this).inflate(R.layout.head_series_share_list, llTopicRoot, false);
            ImageView head    = (ImageView) view.findViewById(R.id.iv_item_topic_share_list_head);
            TextView  content = (TextView) view.findViewById(R.id.tv_item_topic_share_list_content);
            TextView  goshare = (TextView) view.findViewById(R.id.tv_item_topic_share_list_goshare);
            TextView  name    = (TextView) view.findViewById(R.id.tv_item_topic_share_list_name);

            setInviteInfo(view);

            ImageHelper.loadImage(this, head, UserManager.getInstance().getHeadPic(), R.drawable.circle_head_icon_120);
            name.setText(UserManager.getInstance().getUserName());
            content.setText(Html.fromHtml(String.format(countStr, mSeriesBean.getUserInvitationAllShareNum())));

            goshare.setOnClickListener(this);
            llTopicRoot.addView(view);
        }
    }


    private void setHeadView(TopicShareListBean bean) {
        View      view = LayoutInflater.from(this).inflate(R.layout.head_series_share_list, mRecyclerview, false);
        ImageView head = (ImageView) view.findViewById(R.id.iv_item_topic_share_list_head);
        TextView content = (TextView) view.findViewById(R.id.tv_item_topic_share_list_content);
        TextView goshare = (TextView) view.findViewById(R.id.tv_item_topic_share_list_goshare);
        TextView name    = (TextView) view.findViewById(R.id.tv_item_topic_share_list_name);

        setInviteInfo(view);

        ImageHelper.loadImage(this, head, UserManager.getInstance().getHeadPic(), R.drawable.circle_head_icon_120);
        name.setText(UserManager.getInstance().getUserName());
        content.setText(Html.fromHtml(String.format(countStr, mSeriesBean.getUserInvitationAllShareNum())));
        goshare.setOnClickListener(this);
        mRecyclerview.addHeadView(view);
    }


    private void setInviteInfo(View headView){
        View layoutInvite = headView.findViewById(R.id.layout_invite);
        TextView tvInvite = headView.findViewById(R.id.tv_invite_info);
        TextView btn = headView.findViewById(R.id.tv_invite_btn);
        if(mSeriesBean.getUserInvitationSharNum() >= mSeriesBean.getInvitationFreeNum()){
            tvInvite.setText(String.format(Locale.CHINA, "你已成功邀请%d位好友", mSeriesBean.getUserInvitationSharNum()));
            btn.setText("去听课");
        }else{
            tvInvite.setText(String.format(Locale.CHINA, "你已邀请%d位好友, 还差%d位好友解锁课程", mSeriesBean.getUserInvitationSharNum(), mSeriesBean.getInvitationFreeNum() - mSeriesBean.getUserInvitationSharNum()));
            btn.setText("去邀请");
        }

        layoutInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSeriesBean.getUserInvitationSharNum() >= mSeriesBean.getInvitationFreeNum()){
                    finish();
                }else{
                    shareDialog();
                }
            }
        });

        if(!mSeriesBean.bIsSelf() && SeriesActivity.AUDITION_INVITE_TYPE.equals(mSeriesBean.getIsAuditions())){
            layoutInvite.setVisibility(View.VISIBLE);
        }else{
            layoutInvite.setVisibility(View.GONE);
        }
    }


    @Override
    public void showLoadMore(TopicShareListBean datas) {
        mRecyclerview.changeData(datas.getDatas(), mAdapter);
    }

    @Override
    public void showNoMore() {
        ToastUtil.showShort(this, "没有更多数据");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }


    public static void startActivity(Context context, SeriesPageBean bean){
        Intent intent = new Intent(context, SeriesShareListActivity.class);
        intent.putExtra(Constant.INTENT_DATA, bean);
        context.startActivity(intent);
    }

}
