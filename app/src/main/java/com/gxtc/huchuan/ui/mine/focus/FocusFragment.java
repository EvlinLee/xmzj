package com.gxtc.huchuan.ui.mine.focus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.MsgView;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FocusAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.bean.event.EventJPushBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ConfirmRelayDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.circle.SearchConversationActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.ui.message.AddressListActivity;
import com.gxtc.huchuan.ui.message.GroupListActivity;
import com.gxtc.huchuan.ui.message.MessageFragment;
import com.gxtc.huchuan.ui.message.NewFriendsActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/3/31.
 * 关注列表
 * 暂定一个flag 1是推荐跟我订阅的自媒体 2是我的好友 3是我的粉丝
 * typeid
 * 1：我关注的用户列表，
 * 2：关注我的用户列表，
 * 3：互相关注，
 * 4：推荐
 */

public class FocusFragment extends BaseTitleFragment implements FocusContract.View,
        View.OnClickListener {

    private static final String TAG = "FocusFragment";

    @BindView(R.id.rl_focus) RecyclerView       mRecyclerView;
    @BindView(R.id.sw_focus) SwipeRefreshLayout swipLayout;

    private MsgView mBubbleView;

    private int id;
    private int isSelectFriends = -1;
    private boolean isFollow = true;

    private FocusAdapter                  mAdapter;
    private FocusContract.Presenter       mPresenter;
    private String                        focusFlag;
    private String                        targetId;
    private FocusBean                     mFocusBean;
    private Conversation.ConversationType mConversationType;
    private String   groupChatId = "";
    public AlertDialog mAlertDialog;
    private EventShareMessage  currShareMessage;
    private ConfirmRelayDialog mRelayDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_focus, container, false);
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);

        swipLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        Bundle bundle = getArguments();
        id = bundle.getInt("type_id");
        targetId = bundle.getString("targetId");
        isSelectFriends = bundle.getInt("select_type_card",-1);
        isFollow = bundle.getBoolean("isFollow", true);
        mConversationType = (Conversation.ConversationType) bundle.getSerializable("type");

        initRecyCleView();

        new FocusPresenter(this, String.valueOf(id));

        if(UserManager.getInstance().isLogin()){
            mPresenter.getData(false,groupChatId);
        }else{
            showEmptyView(true);
        }

        if(id == 3){
            //未申请的好友数
            initUnReadMsg();
        }
    }


    private void initUnReadMsg() {
        int num = SpUtil.getInt(getContext(),MessageFragment.KEY_NUM(), 0);
        if(num == 0){
            mBubbleView.setVisibility(View.INVISIBLE);
        }else{
            mBubbleView.setVisibility(View.VISIBLE);
            UnreadMsgUtils.show(mBubbleView, num);
        }
    }

    @Override
    public void initListener() {
        swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true,groupChatId);
                mRecyclerView.reLoadFinish();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //新的朋友
            case R.id.layout_friends:
                SpUtil.putInt(getContext(), MessageFragment.KEY_NUM(), 0);
                EventBusUtil.post(new EventJPushBean(EventJPushBean.APPLY_FRIENDS,"",0));
                GotoUtil.goToActivity(getActivity(), NewFriendsActivity.class);
                break;

            //群聊
            case R.id.layout_group:
                GotoUtil.goToActivity(getActivity(), GroupListActivity.class);
                break;
        }
    }


    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(getActivity());
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(FocusContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        if(swipLayout != null){
            swipLayout.setRefreshing(false);
            getBaseLoadingView().hideLoading();
        }
    }

    @Override
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    /**
     * 加载更多时网络错误，直接打吐司
     */
    @Override
    public void showError(String info) {
        ToastUtil.showShort(this.getContext(), info);
    }

    /**
     * 初始网络错误，点击重新加载
     */
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
    public void showData(final List<FocusBean> datas) {
        mRecyclerView.notifyChangeData(datas,mAdapter);
    }


    @Override
    public void showRefreshFinish(List<FocusBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMore(List<FocusBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6666 && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            mPresenter.getData(true,groupChatId);

        } else if (requestCode == 777 && resultCode == Constant.ResponseCode.FOCUS_RESULT_CODE) {
            mPresenter.getData(true,groupChatId);

        } else if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == Activity.RESULT_OK){
            getActivity().setResult(Activity.RESULT_OK,data);
            getActivity().finish();
        }
    }

    @Subscribe
    public void onEvent(EventFocusBean bean) {
        //取消关注 刷新列表
        if (!bean.isFocus()) {
            for (int i = 0; i < mAdapter.getList().size(); i++) {
                FocusBean focusBean = mAdapter.getList().get(i);
                if (focusBean.getUserCode().equals(bean.userCode)) {
                    mRecyclerView.removeData(mAdapter, i);
                }
            }
        }else{
            //如果好友已在列表中不刷新列表
            for(FocusBean focusBean : mAdapter.getList()){
                if(focusBean.getUserCode().equals(bean.userCode)){
                    return;
                }
            }
            mPresenter.getData(true,groupChatId);
            mRecyclerView.reLoadFinish();
        }
    }

    @Subscribe
    public void onEvent(EventJPushBean bean){
        if(bean.type == EventJPushBean.APPLY_FRIENDS){
            if(bean.num == 0){
                mBubbleView.setVisibility(View.INVISIBLE);
            }else{
                mBubbleView.setVisibility(View.VISIBLE);
                UnreadMsgUtils.show(mBubbleView, bean.num);
            }
        }
    }


    @Subscribe
    public void onEvent(EventLoginBean bean){
        if(bean.status == EventLoginBean.EXIT){
            mBubbleView.setVisibility(View.INVISIBLE);
            mRecyclerView.notifyChangeData(new ArrayList<FocusBean>(),mAdapter);
            showEmptyView(true);
        }else{
            mPresenter.getData(false,groupChatId);
            showEmptyView(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mRelayDialog = null;
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
    }

    int cardPosituon = 0;
    private void initRecyCleView() {
        int layoutRes = id == 3 ? R.layout.item_invite_chat : R.layout.item_focus;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        if(id == 3){
            mRecyclerView.addHeadView(getHeadView(mRecyclerView));
            View head = View.inflate(getContext(),R.layout.head_friends_view,null);
            head.findViewById(R.id.layout_friends).setOnClickListener(this);
            head.findViewById(R.id.layout_group).setOnClickListener(this);
            mBubbleView = (MsgView) head.findViewById(R.id.msgView);

            if(isSelectFriends == -1){
                mRecyclerView.addHeadView(head);
            }
        }

        if(id == 4){
                View  view = View.inflate(getActivity(),R.layout.contact_header_layout,null);
                mRecyclerView.addHeadView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GotoUtil.goToActivity(getActivity(), AddressListActivity.class);
                    }
                });
        }

        mAdapter = new FocusAdapter(this.getContext(), new ArrayList<FocusBean>(), layoutRes, id , isFollow);
        mRecyclerView.setAdapter(mAdapter);

        //好友跳im 粉丝和关注的人跳转个人中心
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (isSelectFriends){
                    //分享文章
                    case Constant.SELECT_TYPE_SHARE:
                        final FocusBean bean = mAdapter.getList().get(position);
                        if(TextUtils.isEmpty(bean.getUserCode())) return;
                        showConfirmDialog(bean,Constant.SELECT_TYPE_SHARE);
                        return;

                    //发送名片
                    case Constant.SELECT_TYPE_CARD:
                        cardPosituon = position;
                        FocusBean bean1 = mAdapter.getList().get(position);
                        if(TextUtils.isEmpty(targetId)){
                            //这种情况主要是从用户个人信息那边推荐名片做特殊处理
                            setChooseType(bean1.getUserCode(),Conversation.ConversationType.PRIVATE,Constant.SELECT_TYPE_CARD);
                        }else {
                            setChooseType(targetId,mConversationType,Constant.SELECT_TYPE_CARD);
                        }
                        return;

                    case Constant.SELECT_TYPE_RELAY:
                        FocusBean bean2 = mAdapter.getList().get(position);
                        if(TextUtils.isEmpty(bean2.getUserCode())) return;
                        showConfirmDialog(bean2,Constant.SELECT_TYPE_RELAY);
                        return;

                    //申请担保交易
                    case Constant.SELECT_TYPE_GUARAN_DEAL:
                        cardPosituon = position;
                        FocusBean beanDeal = mAdapter.getList().get(position);
                        setChooseType(beanDeal.getUserCode(), Conversation.ConversationType.PRIVATE, Constant.SELECT_TYPE_GUARAN_DEAL);
//                        sentTargetUserInfo();
                        return;
                }

                //我的粉丝
                if(id == 2 || id == 1 || id == 4){
                    PersonalInfoActivity.startActivity(getContext(),mAdapter.getList().get(position).getUserCode());
                }

                //我的好友
                if(id == 3){
                    connect(mAdapter.getList().get(position));
                }
            }
        });
    }

    private  void setChooseType(final String targetId, final Conversation.ConversationType  type, final int shareFlag){
        if(type == Conversation.ConversationType.PRIVATE ){
            getUserInfo(targetId,shareFlag);
        }else {
            getGroupUnfo(targetId,shareFlag);
        }
    }

    private void getUserInfo(final String userCode,final int shareFlag){
        Subscription sub = MineApi.getInstance().getUserMemberByUserCode(userCode,"")
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new ApiObserver<ApiResponseBean<User>>(new ApiCallBack() {
                                      @Override
                                      public void onSuccess(Object data) {
                                          User user = (User) data;
                                          FocusBean bean = new FocusBean();
                                          bean.setUserCode(user.getUserCode());
                                          bean.setUserName(user.getName());
                                          bean.setUserHeadPic(user.getHeadPic());
                                          showConfirmDialog(bean,shareFlag);
                                      }

                                      @Override
                                      public void onError(String errorCode, String message) {
                                          ToastUtil.showShort(MyApplication.getInstance(),message);
                                      }
                                  }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private  void getGroupUnfo(final String s,final int shareFlag){
        Subscription sub = CircleApi.getInstance()
                                    .getGroupInfo(UserManager.getInstance().getToken(),s)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                                        @Override
                                        public void onSuccess(Object data) {
                                            CircleBean bean = (CircleBean) data;
                                            if(bean != null){
                                                FocusBean bean1 = new FocusBean();
                                                bean1.setUserCode(s);
                                                bean1.setUserName(bean.getGroupName());
                                                bean1.setUserHeadPic(bean.getCover());
                                                showConfirmDialog(bean1,shareFlag);
                                            }
                                        }

                                        @Override
                                        public void onError(String errorCode, String message) {
                                            ToastUtil.showShort(getActivity(),message);
                                        }
                                    }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void showConfirmDialog(final FocusBean bean, final int isSelectFriends){
        UserInfo userInfo = new UserInfo(bean.getUserCode(),bean.getUserName(),Uri.parse(bean.getUserHeadPic()));
        mRelayDialog = new ConfirmRelayDialog();
        mRelayDialog.setSelectFriends(isSelectFriends);
        mRelayDialog.setUserInfo(userInfo);
        mRelayDialog.setWidth((int) (WindowUtil.getScreenW(getContext()) * 0.8));
        if(Constant.SELECT_TYPE_GUARAN_DEAL == isSelectFriends ){
            mRelayDialog.setSelectFriends(Constant.SELECT_TYPE_GUARAN_DEAL);//担保交易的把留言输入框隐藏
        }
        mRelayDialog.show(getChildFragmentManager(),"");
        mRelayDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liuyan = mRelayDialog.getEditContent().getText().toString();
                switch (isSelectFriends) {
                    //分享文章
                    case Constant.SELECT_TYPE_SHARE:
                        mRelayDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra(Constant.INTENT_DATA,
                                new EventSelectFriendBean(bean.getUserCode(), Conversation.ConversationType.PRIVATE,liuyan));
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                        return;

                    //发送名片
                    case Constant.SELECT_TYPE_CARD:
                        mRelayDialog.dismiss();
                        FocusBean bean1 = mAdapter.getList().get(cardPosituon);
                        EventSelectFriendForPostCardBean resultData;
                        if (mConversationType == null) {
                            //在个人信息页发送名片
                            resultData = new EventSelectFriendForPostCardBean(bean1.getUserCode(),
                                    "", "", "", Conversation.ConversationType.PRIVATE,liuyan);
                        } else {
                            resultData = new EventSelectFriendForPostCardBean(targetId, bean1.getUserCode(), bean1.getUserName(),
                                    bean1.getUserHeadPic(), mConversationType,liuyan);
                        }
                        Intent intent1 = new Intent();
                        intent1.putExtra(Constant.INTENT_DATA, resultData);
                        getActivity().setResult(Activity.RESULT_OK, intent1);
                        getActivity().finish();
                        return;

                    case Constant.SELECT_TYPE_RELAY:
                        mRelayDialog.dismiss();
                        FocusBean bean2 = bean;
                        EventShareMessage message = new EventShareMessage(Constant.SELECT_TYPE_RELAY,bean2.getUserCode(),Conversation.ConversationType.PRIVATE,liuyan);
                        currShareMessage = message;
                        Intent intentRelay = new Intent();
                        intentRelay.putExtra(Constant.INTENT_DATA,currShareMessage);
                        getActivity().setResult(Activity.RESULT_OK,intentRelay);
                        getActivity().finish();
                        return;

                    //发送名片
                    case Constant.SELECT_TYPE_GUARAN_DEAL:
                        mRelayDialog.dismiss();
                        FocusBean beanDeal = mAdapter.getList().get(cardPosituon);
                        EventSelectFriendForPostCardBean dealResult;
                        dealResult = new EventSelectFriendForPostCardBean(beanDeal.getUserCode(), "", "", "", Conversation.ConversationType.PRIVATE,liuyan);
                        Intent dealIntent = new Intent();
                        dealIntent.putExtra(Constant.INTENT_DATA, dealResult);
                        getActivity().setResult(Activity.RESULT_OK, dealIntent);
                        getActivity().finish();
                        return;
                }
            }
        });
    }


    private View getHeadView(ViewGroup viewGroup) {
        View inflate  = LayoutInflater.from(FocusFragment.this.getContext()).inflate(R.layout.find_list_search_view, viewGroup, false);
        View viewById = inflate.findViewById(R.id.et_input_search);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果分享聊天的东西给好友 应该跳转到搜索自己的好友列表里面
                if (isSelectFriends != -1) {
                    if(targetId != null || mConversationType != null){
                        SearchConversationActivity.startActivity(getActivity(), isSelectFriends,1, ConversationActivity.REQUEST_SHARE_CONTENT,targetId,mConversationType);
                    } else {
                        SearchConversationActivity.startActivity(getActivity(), isSelectFriends,1, ConversationActivity.REQUEST_SHARE_CONTENT);
                    }
                } else {
                    if(id != 3){
                        NewSearchActivity.jumpToSearch(getActivity(),  "5");
                    }else{
                        if(UserManager.getInstance().isLogin()){
                            SearchConversationActivity.startActivity(getActivity(), isSelectFriends,1,0);
                        }else {
                            GotoUtil.goToActivity(getActivity(),LoginAndRegisteActivity.class);
                        }
                    }
                }
            }
        });
        return inflate;
    }


    private void connect(FocusBean focusBean) {
        if (!com.gxtc.huchuan.data.UserManager.getInstance().isLogin()) {
            return;
        }
        if (focusBean != null) {
            Uri uri = Uri.parse(
                    "rong://" + getActivity().getApplicationInfo().packageName).buildUpon().appendPath(
                    "conversation").appendPath(
                    Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter(
                    "targetId", focusBean.getUserCode()).appendQueryParameter("title",
                    focusBean.getUserName()).appendQueryParameter("flag", "1").build();
            startActivity(new Intent("android.intent.action.VIEW", uri));
        }
    }

    private void showEmptyView(boolean isShow){
        if(isShow){
            getBaseEmptyView().showEmptyView(R.drawable.rc_conversation_list_empty, "您未登陆,点击这里登陆",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GotoUtil.goToActivity(getActivity(),LoginAndRegisteActivity.class);
                        }
                    });
        }else{
            getBaseEmptyView().hideEmptyView();
        }
    }


}
