package com.gxtc.huchuan.ui.live.intro;

import android.Manifest;
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
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.TopicShareListAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jsoup.Jsoup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 分享榜
 */
public class TopicShareListActivity extends BaseTitleActivity implements View.OnClickListener,
        TopicShareListContract.View {

    private final String countStr = "推荐了<font color=\"#0ab70e\">%s</font>个朋友过来听课";

    @BindView(R.id.recyclerview)  RecyclerView mRecyclerview;
    @BindView(R.id.ll_topic_root) LinearLayout llTopicRoot;

    private TopicShareListAdapter            mAdapter;
    private TopicShareListContract.Presenter mPresenter;
    private String                           chatRoomId;//直播间id
    private String                           chatInfoId;//直播间课程id
    private AlertDialog                      mAlertDialog;
    protected ChatInfosBean                  bean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_share_list);
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
        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chatRoomId");
        chatInfoId = intent.getStringExtra("chatInfoId");
        bean = (ChatInfosBean) intent.getSerializableExtra("chatInfosBean");
        new TopicShareListPresenter(this, chatRoomId, chatInfoId);
        mPresenter.getData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.tv_item_topic_share_list_goshare:
                //这里跳到生成的分享页
                /*Intent intent = new Intent(this, ShareImgActivity.class);
                intent.putExtra("chatInfoId", chatInfoId);
                startActivity(intent);*/
                shareDialog();
                break;
        }
    }

    private void shareDialog() {
        if(bean == null) return ;
        //这里跳出去分享
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        String   title = bean.getSubtitle();
                        String desc = Jsoup.parse(bean.getDesc()).body().text();
                        desc  = TextUtils.isEmpty(desc) ? title : desc;
                        String uri = bean.getFacePic();

                        ShareDialog.Action [] actions = {new ShareDialog.Action(ShareDialog.ACTION_INVITE, R.drawable.share_icon_invitation, null),
                                new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                                new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null),
                                new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                                new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                                new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null)};

                        UMShareUtils shareUtils = new UMShareUtils(TopicShareListActivity.this);
                        shareUtils.shareCustom(uri, title, desc, bean.getShareUrl(), actions, new ShareDialog.OnShareLisntener() {
                            @Override
                            public void onShare(String key, SHARE_MEDIA media) {
                                switch (key){
                                    //这里跳去邀请卡
                                    case ShareDialog.ACTION_INVITE:
                                        Intent intent = new Intent(TopicShareListActivity.this, ShareImgActivity.class);
                                        intent.putExtra("chatInfoId", bean.getId());
                                        startActivity(intent);
                                        break;

                                    //分享到动态
                                    case ShareDialog.ACTION_CIRCLE:
                                        IssueDynamicActivity.share(TopicShareListActivity.this,bean.getId(),"2",bean.getSubtitle(),bean.getHeadPic());
                                        break;

                                    //分享到好友
                                    case ShareDialog.ACTION_FRIENDS:
                                        ConversationListActivity.startActivity(TopicShareListActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT,
                                                Constant.SELECT_TYPE_SHARE);
                                        break;

                                    //收藏
                                    case ShareDialog.ACTION_COLLECT:
                                        mPresenter.collect(bean.getId());
                                        break;

                                    //二维码
                                    case ShareDialog.ACTION_QRCODE:
                                        ErWeiCodeActivity.startActivity(TopicShareListActivity.this, ErWeiCodeActivity.TYPE_CLASSROOM, Integer.valueOf(bean.getId()), "");
                                        break;
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(TopicShareListActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(TopicShareListActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    private void setHeadView(TopicShareListBean bean) {
        View      view = LayoutInflater.from(this).inflate(R.layout.item_topic_share_list_me,
                mRecyclerview, false);
        ImageView head = (ImageView) view.findViewById(R.id.iv_item_topic_share_list_head);
//        TextView call = (TextView) view.findViewById(R.id.tv_item_topic_share_list_call);
        TextView content = (TextView) view.findViewById(R.id.tv_item_topic_share_list_content);
        TextView goshare = (TextView) view.findViewById(R.id.tv_item_topic_share_list_goshare);
        TextView name    = (TextView) view.findViewById(R.id.tv_item_topic_share_list_name);

        ImageHelper.loadImage(this, head, bean.getSelfData().getHeadPic(),
                R.drawable.circle_head_icon_120);
        name.setText(bean.getSelfData().getName());
        content.setText(Html.fromHtml(String.format(countStr, bean.getSelfData().getCount())));


        //称号暂时不需要
//        Integer integer = Integer.valueOf(bean.getSelf().getCount());
//        int i = integer.intValue();
//        call.setVisibility(i <= 0 ? View.GONE : View.VISIBLE);
//        String[] stringArray = getResources().getStringArray(R.array.share_call);
//        String[] colors = getResources().getStringArray(R.array.share_call_color);
//        int index = i / 10;
//        if (index < 5) {
//            call.setText(stringArray[index]);
//            call.setBackgroundColor(Color.parseColor(colors[index]));
//        }

        goshare.setOnClickListener(this);

        mRecyclerview.addHeadView(view);

    }


    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(TopicShareListActivity.this,
                false, null, getString(R.string.token_overdue),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            ReLoginUtil.ReloginTodo(TopicShareListActivity.this);
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
        String title = bean.getSubtitle();
        String img = bean.getFacePic();
        String id = bean.getId();
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "2", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(TopicShareListActivity.this,"分享成功");
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage
                            (liuyan,targetId,type);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(TopicShareListActivity.this,"分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }


    @Override
    public void setPresenter(TopicShareListContract.Presenter presenter) {
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
            mAdapter = new TopicShareListAdapter(this, datas.getDatas(), R.layout.item_topic_share_list_other);
            setHeadView(datas);
            mRecyclerview.setAdapter(mAdapter);
        } else {
            View      view    = LayoutInflater.from(this).inflate(R.layout.item_topic_share_list_me,
                    llTopicRoot, false);
            ImageView head    = (ImageView) view.findViewById(R.id.iv_item_topic_share_list_head);
            TextView  content = (TextView) view.findViewById(R.id.tv_item_topic_share_list_content);
            TextView  goshare = (TextView) view.findViewById(R.id.tv_item_topic_share_list_goshare);
            TextView  name    = (TextView) view.findViewById(R.id.tv_item_topic_share_list_name);

            ImageHelper.loadImage(this, head, datas.getSelfData().getHeadPic(),
                    R.drawable.circle_head_icon_120);
            name.setText(datas.getSelfData().getName());
            content.setText(Html.fromHtml(String.format(countStr, datas.getSelfData().getCount())));

            goshare.setOnClickListener(this);
            llTopicRoot.addView(view);
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
    public void showCollectResult() {
        if("0".equals(bean.getIsCollect())){
            bean.setIsCollect("1");
            ToastUtil.showShort(this,"收藏成功");
        }else{
            bean.setIsCollect("0");
            ToastUtil.showShort(this,"取消收藏");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
