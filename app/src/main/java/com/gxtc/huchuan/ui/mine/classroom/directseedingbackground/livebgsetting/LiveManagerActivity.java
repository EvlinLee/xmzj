package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveManagerAdapter;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Describe:直播间管理员
 * Created by ALing on 2017/3/22 .
 */
@Deprecated
public class LiveManagerActivity extends BaseTitleActivity implements View.OnClickListener,
        LiveBgSettingContract.View {

    private static final String TAG = LiveManagerActivity.class.getSimpleName();
    @BindView(R.id.rc_list)           RecyclerView   mRcList;
    @BindView(R.id.rl_invite_manager) RelativeLayout mRlInviteManager;
    @BindView(R.id.tv_label_share)    TextView       mTvLabelShare;
    @BindView(R.id.tv_label_des)      TextView       mTvLabelDes;

    private LiveBgSettingContract.Presenter mPresenter;
    private HashMap<String, String>         map;
    private int start = 0;
    private String                          mjoinType;  //0:普通用户， 1：管理员，  2：讲师。
    private String                          mShareUrl;   //分享链接
    private String                          mChatInfoId; //直播课程id
    private View                            headView;
    private ImageView                       mIvHead;
    private TextView                        mTvName;
    private TextView                        mTvSubName;
    private List<LiveManagerBean.DatasBean> list;
    private LiveManagerAdapter              adapter;
    private CompositeSubscription sub = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_manager);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_live_manager));
        getBaseHeadView().showBackButton(this);

        headView = View.inflate(this, R.layout.item_live_manager, null);
        mIvHead = ((ImageView) headView.findViewById(R.id.iv_head));
        mTvName = ((TextView) headView.findViewById(R.id.tv_name));
        mTvSubName = ((TextView) headView.findViewById(R.id.tv_sub_name));

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void initListener() {
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                start += 15;
                mPresenter.loadMoreManagers(map);
            }
        });
    }

    @Override
    public void initData() {
        new LiveBgSettingPrensenter(this);
        map = new HashMap<>();

        Object bean = getIntent().getSerializableExtra("bean");
        //普通用户
        if (bean instanceof ChatInfosBean) {
            mjoinType = "0";
            mShareUrl = ((ChatInfosBean) bean).getShareUrl();
            mChatInfoId = ((ChatInfosBean) bean).getId();
            map.put("chatInfoId", mChatInfoId);
            getBaseHeadView().showTitle(getString(R.string.titile_class_menber_list));
            mTvLabelShare.setText("发送邀请链接");
            mTvLabelDes.setText("课堂参与人");

        //管理员
        } else if (bean instanceof LiveRoomBean) {
            mjoinType = "1";
            mShareUrl = ((LiveRoomBean) bean).getShareUrl() + "&joinType=" + mjoinType;
            map.put("chatRoomId", UserManager.getInstance().getChatRoomId());

        //讲师
        } else if (bean instanceof ChatInfosBean) {
            mjoinType = "2";
            mShareUrl = ((ChatInfosBean) bean).getShareUrl();
            mChatInfoId = ((ChatInfosBean) bean).getId();
            map.put("chatInfoId", mChatInfoId);
        }

        map.put("token", UserManager.getInstance().getToken());
        map.put("joinType", mjoinType);        //0:普通用户， 1：管理员，  2：讲师。
        map.put("start", String.valueOf(start));
        mPresenter.getManagerList(map);

    }

    @OnClick({R.id.rl_invite_manager})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //邀请管理员
            case R.id.rl_invite_manager:
                InvitedGuestsActivity.startActivity(this, mShareUrl);
                break;
        }
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showLiveManageData(LiveRoomBean bean) {
    }

    @Override
    public void showPicList(List<BgPicBean> picData) {
    }

    @Override
    public void showCompressSuccess(File file) {
    }

    @Override
    public void showCompressFailure() {
    }

    @Override
    public void showUploadingSuccess(String url) {
    }

    @Override
    public void showChatRoomSetting(LiveBgSettingBean bean) {
    }

    @Override
    public void showLoadMore(List<BgPicBean> datas) {
    }

    @Override
    public void showNoMore() {
        if (mRcList != null && adapter != null) {
            mRcList.loadFinish();
        }
    }

    @Override
    public void showManagerList(LiveManagerBean bean) {
        ImageHelper.loadImage(this, mIvHead, bean.getSelfData().getHeadPic(),
                R.drawable.person_icon_head_120);
        mTvName.setText(bean.getSelfData().getName());
        mTvSubName.setText("创建者");
        mRcList.addHeadView(headView);
        mRcList.setAdapter(
                new LiveManagerAdapter(this, bean.getDatas(), R.layout.item_live_manager));
    }

    @Override
    public void showMoreManagerList(LiveManagerBean bean) {
        mRcList.changeData(bean.getDatas(), adapter);
    }

    @Override
    public void setPresenter(LiveBgSettingContract.Presenter presenter) {
        this.mPresenter = presenter;
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
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }

    public static void startActivity(Context context, Object o /*String joinType, String chatInfoId, String shareUrl*/) {
        Intent intent = new Intent(context, LiveManagerActivity.class);
        intent.putExtra(Constant.INTENT_DATA, (Serializable) o);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
