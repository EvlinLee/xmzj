package com.gxtc.huchuan.ui.live.setting.normal;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveMemberAdapter;
import com.gxtc.huchuan.adapter.LiveMemberManagerAdapter;
import com.gxtc.huchuan.adapter.LiveOrdinaryMembersAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.LiveBgSettingRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ShareDialog;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.circleInfo.ApplyRefundsActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.live.conversation.LiveConversationActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.intro.ShareImgActivity;
import com.gxtc.huchuan.ui.live.member.MemberManagerSource;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.InvitedGuestsActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.LiveMemberManagerActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.usermanager.UserManagerActivity;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.ClassOrderActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.NoEventRecyclerView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Gubr on 2017/3/17.
 * 课堂（单间）设置
 */

public class NormalSettingActivity extends BaseTitleActivity {

    @BindView(R.id.iv_normal_setting_head)      ImageView           mIvHead;
    @BindView(R.id.tv_normal_setting_name)      TextView            mTvLiveRoomName;
    @BindView(R.id.tv_refund)                   TextView            tvRefund;
    @BindView(R.id.iv_normal_setting_attention) ImageView           mIvAttention;
    @BindView(R.id.rc_live_member)              NoEventRecyclerView mRcLiveMember;
    @BindView(R.id.rc_live_managemember)       NoEventRecyclerView mRcLivemanageMember;
    @BindView(R.id.cb_setting_autoplay_other)   CheckBox            mCbSettingAutoplayOther;
    @BindView(R.id.cb_setting_autoplay_mine)    CheckBox            mCbSettingAutoplay;
    @BindView(R.id.ll_normal_name)              LinearLayout        llNormalName;
    @BindView(R.id.iv_model_banned)             ImageView           ivModelBanned;//禁言
    @BindView(R.id.ll_normal_showpsd)           LinearLayout        llNormalShowpsd;
    @BindView(R.id.ll_psd_mine)                 LinearLayout        llPsdMine;
    @BindView(R.id.ll_over_mine)                LinearLayout        llOverMine;
    @BindView(R.id.ll_psd_refund)               LinearLayout        llRefund;
    @BindView(R.id.layout_live_order)           View                layoutOrder;
    @BindView(R.id.item_line_member)            View                 lineMember;
    @BindView(R.id.tv_commission)             TextView                commission; //佣金

    private GridLayoutManager mGridLayoutManager;
    private GridLayoutManager mGridLayoutManager2;
    private LiveMemberAdapter mLiveMemberAdapter;
    private LiveMemberAdapter mLiveMemberAdapter2;

    ChatInfosBean mBean;
    private AlertDialog mDialog;

    private String                  isBanned;//0已经禁言 1没有禁言
    private LiveBgSettingRepository mData;
    MemberManagerSource memberManagerSource ;
    private AlertDialog mAlertDialog;
    private SeriesPageBean seriesBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_settting);
    }


    @Override
    public void initView() {
        initHeadView();
        mBean = (ChatInfosBean) getIntent().getSerializableExtra("bean");
        seriesBean = (SeriesPageBean) getIntent().getSerializableExtra("seriesBean");
        getMember();
        getOrdinaryMember();
        //是否是自己的课程
        if (mBean.isSelff()) {
            findViewById(R.id.model_mine).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.model_other).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_normal_name).setVisibility(View.GONE);
//            //分享课堂下显示佣金
//            if(Double.parseDouble(mBean.getFee()) > 0) {
//                commission.setVisibility(View.VISIBLE);
//                Double comm = Double.parseDouble(mBean.getPent()) * Double.parseDouble(mBean.getFee()) * 0.01;
//                commission.setText("没邀请一位好友听课可获得"+ StringUtil.formatMoney(2,comm) + "元");
//            }
        }

        isBanned = mBean.getIsBanned();
        //已经禁言
        if ("1".equals(isBanned)) {
            ivModelBanned.setImageResource(R.drawable.live_set_icon_shut_up_select);

        //未禁言
        } else {
            ivModelBanned.setImageResource(R.drawable.live_set_icon_shut_up);
        }

        //是否显示密码设置按钮
        if ("".equals(mBean.getPassword())) {
            llPsdMine.setVisibility(View.INVISIBLE);
            llPsdMine.setEnabled(false);
        }

        //如果课程结束不显示结束按钮
        if ("3".equals(mBean.getStatus())) {
            llPsdMine.setVisibility(View.INVISIBLE);
            llPsdMine.setEnabled(false);
            llOverMine.setVisibility(View.INVISIBLE);
            llOverMine.setEnabled(false);
            findViewById(R.id.ll_over_mine).setVisibility(View.INVISIBLE);
        }

        llRefund.setVisibility(View.GONE);
        //是否可以申请退款
        /*if ("0".equals(mBean.getIsRefund())) {
        } else {
            llRefund.setVisibility(View.VISIBLE);
            showRefindStauts();
        }*/

        if(("3".equals(mBean.getRoleType()) || "4".equals(mBean.getRoleType())) &&
                (Double.parseDouble( mBean.getFee()) > 0 || mBean.getPassword().equals(""))){

            layoutOrder.setVisibility(View.VISIBLE);
        }else{

            layoutOrder.setVisibility(View.GONE);
        }

        ImageHelper.loadImage(this, mIvHead, mBean.getChatRoomHeadPic(),
                R.drawable.person_icon_head_120);
//        mTvLiveRoomName.setText(mBean.getChatRoomName());
        mTvLiveRoomName.setText("免费邀请成员");
        if (mBean.isFolow()) {
            mIvAttention.setImageResource(R.drawable.live_anchor_attention_selected);
        }
        Boolean isAutoPlay = SpUtil.getBoolean(NormalSettingActivity.this,
                Constant.AUTO_PLAY_NEXT_VOICEMASSAGE, true);
        mCbSettingAutoplay.setChecked(isAutoPlay);
        mCbSettingAutoplayOther.setChecked(isAutoPlay);
    }

    private void showRefindStauts() {
        switch (mBean.getIsRefund()) {
            case "1":
                tvRefund.setText("申请退款");
                llRefund.setClickable(true);
                break;
            case "2":
                tvRefund.setText("申请中...");
                llRefund.setClickable(false);
                showVertifanceDialog(mBean);
                break;
            case "3":
                tvRefund.setText("申请通过");
                llRefund.setClickable(false);
                showVertifanceDialog(mBean);
                break;
            case "4":
                tvRefund.setText("申请不通过");
                llRefund.setClickable(true);
                showVertifanceDialog(mBean);
                break;
        }
    }

    VertifanceFlowDialog mVertifanceFlowDialog;

    private void showVertifanceDialog(final ChatInfosBean mChatInfosBean) {
        if (mVertifanceFlowDialog == null) {
            mVertifanceFlowDialog = new VertifanceFlowDialog(this);
        }
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("4".equals(mChatInfosBean.getIsRefund())) {
                    applyRefund();//重新申请
                }
                mVertifanceFlowDialog.dismiss();
            }
        });
        switch (mChatInfosBean.getIsRefund()) {
            case "2":
                mVertifanceFlowDialog.setFlowStatus("申请中...");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case "4":
                mVertifanceFlowDialog.setFlowStatus("申请不通过：" + mBean.getRefundRemark());
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
                break;
        }
    }

    private void getMember() {
        mData = new LiveBgSettingRepository();
        memberManagerSource = new MemberManagerSource();
        final HashMap<String, String> map = new HashMap<>();
        map.put("chatInfoId", mBean.getId());
        map.put("start", "0");
        map.put("pageSize", "5");
        mData.getChatJoinList1(new ApiCallBack<ChatJoinBean>() {
            @Override
            public void onSuccess(final ChatJoinBean data) {
                if (mGridLayoutManager2 == null) {

                    mLiveMemberAdapter2 = new LiveMemberAdapter(NormalSettingActivity.this,
                            data.getCurrList(), R.layout.item_live_member);
                    mGridLayoutManager2 = new GridLayoutManager(NormalSettingActivity.this, 5);
                    mRcLivemanageMember.setLayoutManager(mGridLayoutManager2);

                    mRcLivemanageMember.setAdapter(mLiveMemberAdapter2);
                    if(data != null)
                        mLiveMemberAdapter2.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
                            @Override
                            public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {

                                if(!data.getCurrList().get(position).getUserCode().equals(UserManager.getInstance().getUserCode())){
                                    PersonalInfoActivity.startActivity(NormalSettingActivity.this, data.getCurrList().get(position).getUserCode());
                                }
                            }
                        });
                    if( mLiveMemberAdapter2.getItemCount() == 0)
                        mRcLivemanageMember.setVisibility(View.GONE);

                }else{
                    mLiveMemberAdapter2.getList().clear();
                    mLiveMemberAdapter2.notifyChangeData(data.getCurrList());
                }
            }

            @Override
            public void onError(String errorCode, String message) {}
        }, map);


    }

    private void getOrdinaryMember() {
        mData = new LiveBgSettingRepository();
        memberManagerSource = new MemberManagerSource();
        final HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("start", "0");
        map.put("pageSize", "5");
        map.put("userType","3");
        if(seriesBean != null){
            map.put("type","2");
            map.put("chatId",seriesBean.getId());
        }else{
            map.put("type","1");
            map.put("chatId",mBean.getId());
        }
//        map.put("searchKey","");
        long loadTime = System.currentTimeMillis();
        map.put("loadTime",loadTime + "");
        mData.getChatJoinList(new ApiCallBack<ArrayList<ChatJoinBean.MemberBean>>() {
            @Override
            public void onSuccess(final ArrayList<ChatJoinBean.MemberBean> data) {
                if (mGridLayoutManager == null) {
                    mGridLayoutManager = new GridLayoutManager(NormalSettingActivity.this, 5);
                    mRcLiveMember.setHasFixedSize(true);

                    mLiveMemberAdapter = new LiveMemberAdapter(NormalSettingActivity.this,
                            data, R.layout.item_live_member);
                    mRcLiveMember.setLayoutManager(mGridLayoutManager);

                    mRcLiveMember.setAdapter(mLiveMemberAdapter);
                    if(data == null || data.size() == 0){
                        lineMember.setVisibility(View.GONE);
                    }
                    if(data != null) {
                        mLiveMemberAdapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
                            @Override
                            public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {

                                if (!data.get(position).getUserCode().equals(UserManager.getInstance().getUserCode())){
                                    PersonalInfoActivity.startActivity(NormalSettingActivity.this, data.get(position).getUserCode());

                                }
                            }
                        });
                    }
                } else{
                    mLiveMemberAdapter.getList().clear();
                    mLiveMemberAdapter.notifyChangeData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {}
        }, map);


    }




    private void initHeadView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_normal_setting));
    }

    @Override
    public void initListener() {
        mCbSettingAutoplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(NormalSettingActivity.this, Constant.AUTO_PLAY_NEXT_VOICEMASSAGE,
                        isChecked);
            }
        });
        mCbSettingAutoplayOther.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SpUtil.putBoolean(NormalSettingActivity.this,
                                Constant.AUTO_PLAY_NEXT_VOICEMASSAGE, isChecked);
                    }
                });
    }

    @Override
    public void initData() {


    }

    @OnClick({R.id.ll_over_invite,
            R.id.ll_psd_manage,
            R.id.tv_more,
            R.id.iv_more_member,
            R.id.iv_normal_setting_attention,
            R.id.ll_live_intro_other,
            R.id.ll_live_inform_other,
            R.id.ll_psd_mine,
            R.id.ll_banned_mine,
            R.id.ll_over_mine,
            R.id.ll_intro_mine,
            R.id.ll_weixin,
            R.id.ll_friend,
            R.id.ll_weibo,
            R.id.ll_normal_name,
            R.id.ll_psd_refund,
            R.id.layout_live_order,
            R.id.ll_copylink,
            R.id.ll_circlefriends,
            R.id.ll_qrcode,
            R.id.tv_managemore})
    public void onClick(View view) {
        switch (view.getId()) {
            //参课成员 更多
            case R.id.tv_more:
            case R.id.iv_more_member:
//                if(mLiveMemberAdapter.getItemCount() > 0) {
                    Intent intent1 = new Intent(this, LiveMemberManagerActivity.class);
                    intent1.putExtra("bean", mBean);
                    intent1.putExtra("ismanager", 2);
                    intent1.putExtra("type", "1");
                    intent1.putExtra("seriesBean", seriesBean);
//                    startActivity(intent1);
                    startActivityForResult(intent1, 66);
//                }
                break;

                //管理成员 更多
            case R.id.tv_managemore:
                intent1 = new Intent(this, LiveMemberManagerActivity.class);
                intent1.putExtra("bean", mBean);
                intent1.putExtra("ismanager", 1);
                intent1.putExtra("type", "1");
                intent1.putExtra("seriesBean", seriesBean);
//                startActivity(intent1);
                startActivityForResult(intent1, 66);
                break;

            //加关注
            case R.id.iv_normal_setting_attention:
                focusUser();
                break;

            //直播间介绍
            case R.id.ll_live_intro_other:
                if (mBean != null) LiveIntroActivity.startActivity(this, mBean.getId());
                break;

            //举报课程
            case R.id.ll_live_inform_other:
                if (UserManager.getInstance().isLogin()) {
                    ReportActivity.jumptoReportActivity(NormalSettingActivity.this, mBean.getId(),
                            "2");
                } else {
                    gotoLogin(getString(R.string.login));
                }
                break;

            case R.id.ll_psd_mine:
                setPsd("请输入新密码");
                break;

            //用户管理
            case R.id.ll_psd_manage:
                String id = mBean.getId();
                String chatType = "1";
                if(seriesBean != null) {
                    id = seriesBean.getId();
                    chatType = "2";
                }
                UserManagerActivity.startActivity(NormalSettingActivity.this, id, chatType, 0);
//
//                GotoUtil.goToActivity(NormalSettingActivity.this, UserManagerActivity.class, 0,
//                        id);
                break;

            case R.id.ll_banned_mine:
                if ("0".equals(isBanned)) {
                    getSettingMsg("isBanned", "1", "确定禁言？", true);

                } else if ("1".equals(isBanned)) {
                    getSettingMsg("isBanned", "0", "确定关闭禁言？", true);
                }

                break;

            //结束课程
            case R.id.ll_over_mine:
                stopChatInfo();
                break;

            case R.id.ll_intro_mine:
                if (mBean != null) LiveIntroActivity.startActivity(this, mBean.getId());
                break;

            case R.id.ll_weixin:
                shareChatInfo(SHARE_MEDIA.WEIXIN);
                break;

            case R.id.ll_friend:
                shareChatInfo(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;

            case R.id.ll_weibo:
                shareChatInfo(SHARE_MEDIA.SINA);
                break;

            //发送好友
            case R.id.ll_circlefriends:
                ConversationListActivity.startActivity(NormalSettingActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE);
                break;
            //二维码
            case R.id.ll_qrcode:
                ErWeiCodeActivity.startActivity(NormalSettingActivity.this, ErWeiCodeActivity.TYPE_CLASSROOM, Integer.valueOf(mBean.getId()), "");
                break;

            //复制链接
            case R.id.ll_copylink:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(mBean.getShareUrl());
                ToastUtil.showShort(this, "已复制");
                    break;

            case R.id.ll_over_invite:
                Intent intent = new Intent(NormalSettingActivity.this, ShareImgActivity.class);
                intent.putExtra("chatInfoId", mBean.getId());
                startActivity(intent);
                break;

            case R.id.ll_normal_name://跳转个人中心
//                if (mBean != null && UserManager.getInstance().isLogin())
//                    LiveHostPageActivity.startActivity(this, mBean.getChatRoom());
                //免费邀请成员
                intent1 = new Intent(this, LiveMemberManagerActivity.class);
                intent1.putExtra("bean",mBean);
                intent1.putExtra("seriesBean",seriesBean);
                intent1.putExtra("showfriend",false);
                startActivity(intent1);
                break;

            case R.id.ll_psd_refund://申请退款
                applyRefund();
                break;

            //课程订单
            case R.id.layout_live_order:
                gotoLiveOrder();
                break;
        }
    }

    private void gotoLiveOrder(){
        String id = mBean.getId();
        int type = 1 ;
        if(seriesBean != null) {
            type = 2;
            id = seriesBean.getId();
        }

        ClassOrderActivity.startActivity(this, id ,type);
    }

    public void applyRefund() {
        Intent intent1 = new Intent(NormalSettingActivity.this, ApplyRefundsActivity.class);
        intent1.putExtra("chatInfo", mBean);
        startActivity(intent1);
    }

    private void shareChatInfo(final SHARE_MEDIA shareMedia) {
        if (mBean != null) {
            String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                    new PermissionsResultListener() {
                        @Override
                        public void onPermissionGranted() {
                            if (!"".equals(mBean.getFacePic())) {
                                String uri = TextUtils.isEmpty(mBean.getFacePic()) ? Constant.DEFUAL_SHARE_IMAGE_URI : mBean.getFacePic();
                                new UMShareUtils(NormalSettingActivity.this).shareOne(shareMedia, uri, mBean.getChatRoomName(), mBean.getSubtitle(), mBean.getShareUrl());

                            } else
                                new UMShareUtils(NormalSettingActivity.this).shareOne(shareMedia, R.drawable.list_error_img, mBean.getChatRoomName(), mBean.getSubtitle(), mBean.getShareUrl());
                        }

                        @Override
                        public void onPermissionDenied() {
                            mAlertDialog = DialogUtil.showDeportDialog(NormalSettingActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(v.getId() == R.id.tv_dialog_confirm){
                                                JumpPermissionManagement.GoToSetting(NormalSettingActivity.this);
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                        }
                    });
        }
    }


    private AlertDialog dialog;


    /**
     * 获取信息以及相关操作，
     * 参见文档 47.	保存直播间课程介绍页信息接口
     *
     * @param field   接口字段名
     * @param msg     接口参数
     * @param content 对话框文本
     * @param isBanne 是否是禁言按钮 禁言要切换状态
     * @return
     */
    private void getSettingMsg(final String field, final String msg, String content,
                               final boolean isBanne) {
        if (mBean != null) {
            if (UserManager.getInstance().isLogin()) {
                dialog = DialogUtil.showInputDialog(this, false, "", content,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("token", UserManager.getInstance().getToken());
                                map.put("id", mBean.getId());
                                map.put(field, msg);

                                Subscription sub = LiveApi.getInstance().saveChatInfoIntroduction(
                                        map).subscribeOn(Schedulers.io()).observeOn(
                                        AndroidSchedulers.mainThread()).subscribe(
                                        new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                if (ivModelBanned == null) return;
                                                if (isBanne) {
                                                    if ("0".equals(isBanned)) {
                                                        ivModelBanned.setImageResource(R.drawable.live_set_icon_shut_up_select);
                                                        isBanned = "1";
                                                        sendMessage("true");
                                                    } else if ("1".equals(isBanned)) {
                                                        ivModelBanned.setImageResource(R.drawable.live_set_icon_shut_up);
                                                        isBanned = "0";
                                                        sendMessage("false");
                                                    }
                                                }
                                                dialog.dismiss();
                                                ToastUtil.showShort(NormalSettingActivity.this, "设置成功");
                                            }

                                            @Override
                                            public void onError(String errorCode, String message) {
                                                if (dialog == null) return;
                                                LoginErrorCodeUtil.showHaveTokenError(
                                                        NormalSettingActivity.this, errorCode,
                                                        message);
                                                dialog.dismiss();
                                            }
                                        }));

                                RxTaskHelper.getInstance().addTask(this, sub);
                            }
                        });
            } else {
                gotoLogin("请先登录");
            }
        } else {
            ToastUtil.showShort(this, "服务器异常，请重试");
        }
    }

    /**
     * 修改密码，商定只要传过来的密码字段不为空这个开关就显示否则隐藏
     */
    private void setPsd(String titleText) {
        if (mBean != null) {
            if (UserManager.getInstance().isLogin()) {
                dialog = DialogUtil.showInputDialog2(this, true, titleText, "",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String etInput = DialogUtil.getEtInput();
                                if ("".equals(etInput)) {
                                    ToastUtil.showShort(NormalSettingActivity.this, "密码不能为空");
                                    return;
                                }
                                HashMap<String, String> map = new HashMap<>();
                                map.put("token", UserManager.getInstance().getToken());
                                map.put("id", mBean.getId());
                                map.put("password", etInput);

                                Subscription sub = LiveApi.getInstance().saveChatInfoIntroduction(
                                        map).subscribeOn(Schedulers.io()).observeOn(
                                        AndroidSchedulers.mainThread()).subscribe(
                                        new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                if (dialog == null) return;
                                                ToastUtil.showShort(NormalSettingActivity.this, "设置成功");
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(String errorCode, String message) {
                                                if (dialog == null) return;
                                                LoginErrorCodeUtil.showHaveTokenError(
                                                        NormalSettingActivity.this, errorCode,
                                                        message);
                                                dialog.dismiss();
                                            }
                                        }));

                                RxTaskHelper.getInstance().addTask(this, sub);
                            }
                        });
            } else {
                gotoLogin("请先登录");
            }
        } else {
            ToastUtil.showShort(this, "服务器异常，请重试");
        }


    }

    //禁言消息不要加 isClass标记
    private void sendMessage(String msg) {
        ImMessageUtils.silentMessage(msg, "0", mBean.getId(),
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
    }


    /**
     * 结束课程
     */
    private void stopChatInfo() {
        if (mBean != null) {
            if (UserManager.getInstance().isLogin()) {
                dialog = DialogUtil.showInputDialog(this, false, "", "是否结束课程",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Subscription sub = LiveApi.getInstance().stopChatInfo(
                                        UserManager.getInstance().getToken(),
                                        mBean.getId()).subscribeOn(Schedulers.io()).observeOn(
                                        AndroidSchedulers.mainThread()).subscribe(
                                        new ApiObserver<ApiResponseBean<List<Object>>>(
                                                new ApiCallBack<List<Object>>() {
                                                    @Override
                                                    public void onSuccess(List<Object> data) {
                                                        if (llOverMine == null) return;
                                                        ToastUtil.showShort(
                                                                NormalSettingActivity.this, "设置成功");
                                                        llOverMine.setVisibility(View.INVISIBLE);
                                                        llPsdMine.setVisibility(View.INVISIBLE);
                                                        llPsdMine.setEnabled(false);
                                                        llOverMine.setEnabled(false);
                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onError(String errorCode,
                                                                        String message) {
                                                        if (dialog == null) return;
                                                        LoginErrorCodeUtil.showHaveTokenError(
                                                                NormalSettingActivity.this,
                                                                errorCode, message);
                                                        dialog.dismiss();
                                                    }
                                                }));

                                RxTaskHelper.getInstance().addTask(this, sub);
                            }
                        });
            } else {
                gotoLogin("请先登录");
            }
        } else {
            ToastUtil.showShort(this, "服务器异常，请重试");
        }
    }

    /**
     * 关注用户
     */
    private void focusUser() {
        //没关注的可以点击关注
        if (!mBean.isFolow()) {
            if (UserManager.getInstance().isLogin()) {
                Subscription sub = AllApi.getInstance().setUserFollow(
                        UserManager.getInstance().getToken(), "2", mBean.getChatRoom()).subscribeOn(
                        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if (mIvAttention == null) return;
                                mIvAttention.setImageResource(R.drawable.live_anchor_attention_selected);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(NormalSettingActivity.this,
                                        errorCode, message);
                            }
                        }));

                RxTaskHelper.getInstance().addTask(this, sub);
            } else gotoLogin("进行相关操作需要登录");

        }
    }



    private void gotoLogin(String content) {
        mDialog = DialogUtil.createDialog(this, "登录账号", content, "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        Intent intent = new Intent(NormalSettingActivity.this,
                                LoginAndRegisteActivity.class);
                        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    public static void startActivity(Context context, ChatInfosBean bean, SeriesPageBean seriesBean) {
        Intent intent = new Intent(context, NormalSettingActivity.class);
        intent.putExtra("bean", bean);
        intent.putExtra("seriesBean", seriesBean);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            String targetId = bean.targetId;
            Conversation.ConversationType type = bean.mType;
            shareMessage(targetId,type,bean.liuyan);
        }else if(requestCode == 66 && resultCode == LiveMemberManagerActivity.REFRESH){
            getMember();
            getOrdinaryMember();
        }
    }
    //分享到好友
    private void shareMessage(final String targetId, final Conversation.ConversationType type, final String liuyan){
        String title = mBean.getSubtitle();
        String img = mBean.getFacePic();
        String id = mBean.getId();
        ImMessageUtils.shareMessage(targetId, type, id, title, img, "2", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(NormalSettingActivity.this,"分享成功");
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage
                            (liuyan,targetId,type);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(NormalSettingActivity.this,"分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.destroy();
        mAlertDialog = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }

}
