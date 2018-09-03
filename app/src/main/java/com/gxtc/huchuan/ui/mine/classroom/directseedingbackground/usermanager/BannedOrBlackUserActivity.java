package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.usermanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.BannedOrBlackUserAdapter;
import com.gxtc.huchuan.bean.BannedOrBlackUserBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.provide.SilentMessage;
import com.gxtc.huchuan.pop.PopDiscuss;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/3/21.
 * 禁言或取消黑名单用户列表
 * 2017/3/21
 * 接口商定，无下拉刷新 暂不需分页（可能二期要改）
 */

public class BannedOrBlackUserActivity extends BaseTitleActivity {

    @BindView(R.id.rv_banned_user)
    RecyclerView mRecyclerView;

    List<BannedOrBlackUserBean> mDatas;
    //系列课数
    private BannedOrBlackUserAdapter mAdapter;

    private int flag;
    private String id;
    private String chatType;
    private int mStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned_user);
    }

    @Override
    public void initData() {
        mDatas = new ArrayList<>();
        getBaseLoadingView().showLoading();
        mRecyclerView.setLayoutMode(R.layout.model_footview_loadmore);
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mStart = mStart + 15;
               getList(id, chatType, mStart + "", "15");
            }
        });
        initHeadView();
    }


    private void initHeadView() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", -1);
        id = intent.getStringExtra(Constant.INTENT_DATA);
        chatType = intent.getStringExtra("chatType");
        //黑名单
        if (1 == flag) {
            getBaseHeadView().showTitle(getString(R.string.title_black_user));
            getList(id,chatType,"0", "15");

        //禁言
        } else if (2 == flag) {
            getBaseHeadView().showTitle(getString(R.string.title_banned_user));
            getList(id,chatType,"0", "15");
        }
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannedOrBlackUserActivity.this.finish();
            }
        });
    }


    /*
     * type 1拉黑，2禁言
     * chatId 课程ID
     * chatType 1课程  2系列课
     * start
     * pageSize
     * token
     */
    private void getList(String chatId, String chatType, String start, String pageSize) {
        if (UserManager.getInstance().isLogin()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("type",flag + "");
            map.put("chatId", chatId);
            map.put("chatType", chatType);
            map.put("start", start);
            map.put("pageSize", pageSize);
            map.put("token", UserManager.getInstance().getToken());
            Subscription sub = AllApi.getInstance().bannedOrBalck(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<BannedOrBlackUserBean>>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(mRecyclerView == null)   return;
                            getBaseLoadingView().hideLoading();
                            mDatas = (List<BannedOrBlackUserBean>) data;
                            if (mDatas.size() == 0) {
                                getBaseEmptyView().showEmptyView();
                                return;
                            }
                            //黑名单
                            if(mStart == 0) {
                                if (1 == flag) {
                                    mAdapter = new BannedOrBlackUserAdapter(BannedOrBlackUserActivity.this, BannedOrBlackUserActivity.this, mDatas, R.layout.item_banned_user, 1);

                                    //禁言
                                } else if (2 == flag) {
                                    mAdapter = new BannedOrBlackUserAdapter(BannedOrBlackUserActivity.this, BannedOrBlackUserActivity.this, mDatas, R.layout.item_banned_user, 2);
                                    mRecyclerView.changeData(mDatas, mAdapter);
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(BannedOrBlackUserActivity.this);
                                mRecyclerView.setLayoutManager(manager);
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.addItemDecoration(new DividerItemDecoration(BannedOrBlackUserActivity.this, LinearLayoutManager.HORIZONTAL));
                                deleteBanned();

                            }else{
                                if(mDatas != null && mDatas.size() > 0)
                                   mRecyclerView.changeData(mDatas, mAdapter);
                                else{
                                    mRecyclerView.loadFinish();
                                }
                            }




                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            getBaseLoadingView().hideLoading();
                            ToastUtil.showShort(BannedOrBlackUserActivity.this, message);
                        }
                    }));

            RxTaskHelper.getInstance().addTask(BannedOrBlackUserActivity.this,sub);

        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    /**
     * 取消禁言
     */
    private void deleteBanned() {
        mAdapter.setOnDelItemListener(new BannedOrBlackUserAdapter.OnDelItemListener() {
            @Override
            public void onDel(final BaseRecyclerAdapter.ViewHolder holder, final int position, final BannedOrBlackUserBean bean) {
                String msg;
                if(flag == 1){
                    msg = "已取消黑名单";
                }else{
                    msg = "已解除禁言";
                }
                manageUser(id, chatType, flag+"", "0", bean.getUserCode(), position, msg);
//                Subscription sub = AllApi.getInstance().cancelBannedOrBalck(UserManager.getInstance().getToken(), UserManager.getInstance().getChatRoomId(), String.valueOf(flag),
//                        bean.getUserCode())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new ApiObserver<ApiResponseBean<List<BannedOrBlackUserBean>>>(new ApiCallBack() {
//                            @Override
//                            public void onSuccess(Object data) {
//                                if (mAdapter == null)   return;
//                                mAdapter.removeData(position);
//                                mAdapter.notifyDataSetChanged();
//                                //禁言
//                                if(flag == 2){
//                                    silendMessage(bean);
//                                }
//
//                                //黑名单
//                                if(flag == 1){
//
//                                }
//                            }
//
//                            @Override
//                            public void onError(String errorCode, String message) {
//                                ToastUtil.showShort(BannedOrBlackUserActivity.this, message);
//                            }
//                        }));
//
//                RxTaskHelper.getInstance().addTask(BannedOrBlackUserActivity.this,sub);
            }
        });
    }

    private void silendMessage(BannedOrBlackUserBean bean){
        ImMessageUtils.silentMessage("false", bean.getUserCode(), id, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                LogUtil.i("发送禁言消息成功");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtil.i("发送禁言消息失败");
            }
        });
    }

    /**
     *
     *   拉黑/解除拉黑操作  禁言/解除禁言
     *  chatId 课程/系列课  id
     *  chatType 1课程 2系列课
     *  userCode 目标用户新媒号
     *  token 当前操作人token
     *  type 1拉黑/解除拉黑操作  2禁言/解除禁言操作
     *  state 0.解除 1.拉黑/禁言
     */
    private void manageUser(String chatRoomId, String chatType, final String type, final String state, String userCode, final int position,final String msg) {
        HashMap<String,String> map =new HashMap<>();
        map.put("chatId", chatRoomId);
        map.put("chatType", chatType);
        map.put("userCode", userCode);
        map.put("token", UserManager.getInstance().getToken());
        map.put("type",type);
        map.put("state", state);

        LiveApi.getInstance().doJoinMemberBlacklistOrProhibitSpeaking(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new
                        ApiObserver<ApiResponseBean<Object>>
                        (new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
//                                callBack.onSuccess(new ArrayList<BannedOrBlackUserBean>());

                                //禁言
                                if(flag == 2){
                                    BannedOrBlackUserBean bean = mAdapter.getList().get(position);
                                    silendMessage(bean);
                                }

                                //黑名单
                                if(flag == 1){

                                }
                                if (mAdapter == null)   return;
                                mAdapter.removeData(position);
                                mAdapter.notifyDataSetChanged();
                                if(mAdapter.getList().size() == 0){
                                    getBaseEmptyView().showEmptyView();
                                }
                                ToastUtil.showShort(BannedOrBlackUserActivity.this, msg);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(BannedOrBlackUserActivity.this, message);
//                                callBack.onError(message);
                            }
                        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
