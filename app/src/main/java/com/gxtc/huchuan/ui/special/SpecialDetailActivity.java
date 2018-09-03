package com.gxtc.huchuan.ui.special;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseActivity;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.RomUtils;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ChannelPagerAdapter;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.PayBean;
import com.gxtc.huchuan.customemoji.utils.ScreenUtils;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.dialog.UpdataDialog;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.deal.MyIssueDetailedActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsCollectActivity;
import com.gxtc.huchuan.ui.news.NewsCollectPresenter;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.ExpandableTextView;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;


/**
 * Created by laoshiren on 2018/5/12.
 * 专题详情
 * 上个列表页面已经获取到详情数据了，但是为了以后可能的拓展，在这个页面请求
 */
public class SpecialDetailActivity extends BaseTitleActivity implements SpecialDetailContract.View {


    @BindView(R.id.special_detail_tab)
    MPagerSlidingTabStrip tab;
    @BindView(R.id.vp_specail_detail)
    ViewPager viewPager;
    @BindView(R.id.expandable_text)
    ExpandableTextView expandableText;
    @BindView(R.id.rl_special_detail_root)
    RelativeLayout relativeLayout;
    //    @BindView(R.id.iv_back)
//    ImageView ivBack;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    @BindView(R.id.tv_special_detail_title)
    TextView tvTitle;
    @BindView(R.id.tv_special_detail_label)
    TextView tvLabel;
    @BindView(R.id.btn_read)
    Button button;
    @BindView(R.id.ll_special_detail_bottom)
    LinearLayout llBottom;

    private ChannelPagerAdapter mAdapter;
    SpecialBean mBean;
    int toolBarPositionY = 0;
    private String id;//专题id
    private int isFee;//是否收费0=免费，1=收费
    private String isSubscribe;//是否订阅 0=未订阅，1=已订阅
    private boolean playing = false;//是否正在支付
    private SpecialDetailContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;

    private UMShareUtils shareUtils;
    private String isCollect;//是否收藏 0没有 1收藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        EventBusUtil.register(this);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        getBaseHeadView().showTitle("专题详情");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpecialDetailActivity.this.finish();
            }
        });


        id = getIntent().getStringExtra("id");

        new SpecialDetailPresenter(this, id);
        hideContentView();
        mPresenter.getData();


    }

    private void showShareDialog() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,

                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(SpecialDetailActivity.this);
                        ShareDialog.Action[] actions = new ShareDialog.Action[0];
                        if ("0".equals(isCollect)) {//没收藏
                            actions = new ShareDialog.Action[]{
                                    new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null)};
                        } else if ("1".equals(isCollect)) {//已收藏
                            actions = new ShareDialog.Action[]{
                                    new ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_CANCLE_COLLECT, R.drawable.share_icon_collect, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                                    new ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null)};
                        }

                        //todo 分享地址没有
                        shareUtils.shareCustom(mBean.getPic(), mBean.getName(), mBean.getAbstracts(), "https://www.hao123.com/", actions, new ShareDialog.OnShareLisntener() {
                            @Override
                            public void onShare(@Nullable String key, @Nullable SHARE_MEDIA media) {
                                switch (key) {
                                    //二维码
                                    case ShareDialog.ACTION_QRCODE:
                                        ToastUtil.showShort(SpecialDetailActivity.this, "跳转二维码页面");
//                                        ErWeiCodeActivity.startActivity(
//                                                SpecialDetailActivity.this,
//                                                ErWeiCodeActivity.TYPE_CLASSROOM,
//                                                Integer.valueOf(mBean.getId()), "");
                                        break;

                                    //收藏
                                    case ShareDialog.ACTION_COLLECT:
                                    case ShareDialog.ACTION_CANCLE_COLLECT:
                                        mPresenter.collectSpecia();
                                        break;

                                    //分享到动态
                                    case ShareDialog.ACTION_CIRCLE:
                                        IssueDynamicActivity.share(
                                                SpecialDetailActivity.this,
                                                String.valueOf(mBean.getId()),
                                                "9",
                                                mBean.getName(),
                                                mBean.getPic());
                                        break;

                                    //分享到好友
                                    case ShareDialog.ACTION_FRIENDS:
                                        ConversationListActivity.startActivity(SpecialDetailActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE);
                                        break;
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {

                        mAlertDialog = DialogUtil.showDeportDialog(SpecialDetailActivity.this,
                                false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    SpecialDetailActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }


    public static void gotoSpecialDetailActivity(Activity activity, String id) {
        Intent intent = new Intent(activity, SpecialDetailActivity.class);
        intent.putExtra("id", id);
        activity.startActivity(intent);
    }

    @Override
    public void showData(SpecialBean bean) {
        showContentView();
        mBean = bean;

        initFragment();

        expandableText.setText(mBean.getIntroduce());
        tvTitle.setText(mBean.getName());
        StringBuffer stringBuffer = new StringBuffer("");
        List<SpecialBean.Label> list = mBean.getLabel();
        if (list != null & list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                SpecialBean.Label label = list.get(i);
                label.setName("#" + list.get(i).getName());
                stringBuffer.append(label.getName() + " ");
            }
        }
        tvLabel.setText(stringBuffer.toString());

        isSubscribe = mBean.isSubscribe();
        isFee = mBean.isFee();
        isCollect = mBean.isCollect();

        if ("0".equals(isSubscribe)) {//未订阅
            llBottom.setVisibility(View.VISIBLE);

            if (0 == isFee) {//免费
                button.setText("免费订阅专题");

            } else if (1 == isFee) {
                button.setText("￥" + mBean.getPrice() + "订阅专题");
            }
        } else if ("1".equals(isSubscribe)) {//已订阅
            llBottom.setVisibility(View.GONE);

        }

        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().isLogin()) {
                    showShareDialog();
                } else {
                    GotoUtil.goToActivity(SpecialDetailActivity.this, LoginAndRegisteActivity.class);

                }
            }
        });

        tab.invalidate();

    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("specialId", String.valueOf(mBean.getId()));
        bundle.putString("isPay", mBean.isPay());
        Fragment articleFragment = new ArticleSpecialListFragment();
        Fragment videoFragment2 = new SpecialVideoFragment();
        articleFragment.setArguments(bundle);
        videoFragment2.setArguments(bundle);
        List<String> datas = new ArrayList<>();
        datas.add("专题文章");
        datas.add("专题视频");
        ArrayList<Fragment> container = new ArrayList<>();
        container.add(articleFragment);
        container.add(videoFragment2);

        mAdapter = new ChannelPagerAdapter(getSupportFragmentManager(), container, datas);
        viewPager.setAdapter(mAdapter);
        tab.setViewPager(viewPager);
    }

    /**
     * 订阅成功
     */
    @Override
    public void showSubscription() {

        mPresenter.getData();
    }

    /**
     * 收藏成功
     */
    @Override
    public void collectSpeciaSucc() {
        if ("0".equals(isCollect)) {
            isCollect = "1";
            ToastUtil.showShort(this, "收藏成功");
        } else if ("1".equals(isCollect)) {
            isCollect = "0";
            ToastUtil.showShort(this, "取消收藏成功");
        }

    }

    @OnClick({R.id.btn_read})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_read:
                if (UserManager.getInstance().isLogin()) {
                    if (0 == isFee) {//免费订阅
                        mPresenter.getSubscription();
                    } else if (1 == isFee) {//付费订阅
                        goPay();
                    }
                } else {
                    Intent intent = new Intent(SpecialDetailActivity.this, LoginAndRegisteActivity.class);
                    startActivityForResult(intent, Constant.requestCode.SPECIAL_DETAIL_LOGIN);
                }

                break;
        }
    }


    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(SpecialDetailActivity.this);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    //跳转支付
    private void goPay() {
        OrdersRequestBean requestBean = new OrdersRequestBean();

        String money = mBean.getPrice().toString();

        BigDecimal moneyB = new BigDecimal(money);
        double total = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        String token = UserManager.getInstance().getToken();
        String transType = "ZS";
        String extra = "{\"specialId\":\"" + id + "\"}";

        requestBean.setTotalPrice(total + "");
        requestBean.setToken(token);
        requestBean.setTransType(transType);
        requestBean.setExtra(extra);
        requestBean.setGoodsName("专题收费-" + mBean.getName());
        playing = true;
        GotoUtil.goToActivity(this, PayActivity.class, 0, requestBean);
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {

    }

    /**
     * 支付回调
     *
     * @param bean
     */
    @Subscribe
    public void onEvent(PayBean bean) {
        if (!playing) return;
        if (!bean.isPaySucc) {//支付失败
            ToastUtil.showShort(this, "支付失败");
        } else {
            mPresenter.getSubscription();
        }
        playing = false;
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
    public void setPresenter(SpecialDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.requestCode.SPECIAL_DETAIL_LOGIN == requestCode && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }

        if(ConversationActivity.REQUEST_SHARE_CONTENT == requestCode && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            shareMessage(bean);
        }

        if (requestCode == 0 && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            ToastUtil.showShort(this, "分享成功");
        }
    }

    private void shareMessage(final EventSelectFriendBean bean){
        ImMessageUtils.shareMessage(bean.targetId, bean.mType, id, mBean.getName(), mBean.getPic(), CircleShareHandler.SHARE_SPECIAL,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) { }

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(SpecialDetailActivity.this, "分享成功");
                        if (!TextUtils.isEmpty(bean.liuyan)) {
                            RongIMTextUtil.INSTANCE.relayMessage(bean.liuyan, bean.targetId, bean.mType);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(SpecialDetailActivity.this, "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }
}
