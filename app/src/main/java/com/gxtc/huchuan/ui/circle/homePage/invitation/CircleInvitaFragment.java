package com.gxtc.huchuan.ui.circle.homePage.invitation;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseFragment;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleInviteAdapter;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleInviteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/27.
 * * 圈子邀请-邀请好友
 * 苏修伟
 */

public class CircleInvitaFragment extends BaseFragment {

    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private String id;
    private String sharImgUrl;
    private String shareTitle;
    private String shareUrl;
    private int memberType ;
    private double money;   //邀请佣金

    private CircleInviteAdapter mAdapter;

    private String [] titles = new String [] {"微信","复制链接","邀请好友","二维码"};
    private int [] idRes = new int[] {
            R.drawable.circle_invitation_icon_weixin,
            R.drawable.circle_invitation_icon_copy,
            R.drawable.circle_invitation_icon_chat,
            R.drawable.circle_invitation_icon_ewm
    };

    private int [] idRes2 = new int[] {
            R.drawable.circle_invitation_icon_weixin,
            R.drawable.circle_invitation_icon_copy,
            R.drawable.circle_invitation_icon_chat,
            R.drawable.circle_invitation_icon_ewm
    };

    private int pressIndex = 0;
    private AlertDialog mAlertDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {

        return inflater.inflate(R.layout.fragment_circle_invite,container,false);
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        id = bundle.getString("id");
        sharImgUrl = bundle.getString("share_img_url");
        shareTitle = bundle.getString("share_title");
        shareUrl = bundle.getString("share_url");
        money = bundle.getDouble("money",0);
        memberType = bundle.getInt("memberType",0);

        List<String> titles = new ArrayList<>();
        Collections.addAll(titles,this.titles);

        if(!TextUtils.isEmpty(shareUrl)){
            if(shareUrl.endsWith("1")){
                titles.remove(titles.size() - 1);
            }
        }

        String s = null ;
        if(memberType == 1 || memberType == 2){
            s = "邀请好友，聚集更多人气，你可以通过以下方式邀请好友。";
            mAdapter = new CircleInviteAdapter(getContext(),titles,R.layout.item_invite_grid,idRes);
        }else {
            if(money == 0){
                s = "邀请好友，聚集更多人气，你可以通过以下方式邀请好友。";
                mAdapter = new CircleInviteAdapter(getContext(),titles,R.layout.item_invite_grid,idRes);
            }else{
                //分享到qq或者空间的话算不了佣金
//                titles.remove(2);
//                titles.remove(2);
//                pressIndex = 2;
                mAdapter = new CircleInviteAdapter(getContext(),titles,R.layout.item_invite_grid,idRes2);
                s = "每当有1个人通过你分享的链接购买成功，你就可以获得%.2f元的佣金奖励。";
                s = String.format(Locale.CHINA,s,money);
            }
        }
        tvDes.setText(s);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void initListener() {
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(position == 0){
                    share(SHARE_MEDIA.WEIXIN);
                    return;
                }
                if(position + pressIndex == 1){
                    ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(shareUrl);
                    ToastUtil.showShort(getContext(), "复制成功");
                    return;
                }
                if(position + pressIndex == 2){
                    gotoInvite();
                    return;
                }

                if(position + pressIndex == 3){
                    ErWeiCodeActivity.startActivity(getContext(), -1, StringUtil.toInt(id), "");
                }
            }
        });
    }

    private void share(final SHARE_MEDIA shareMedia) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    public void onPermissionGranted() {
                        if (!"".equals(sharImgUrl))
                            new UMShareUtils(getActivity()).shareOne(shareMedia, sharImgUrl, shareTitle, "我在这个圈子里发现了很多志同道合的小伙伴，快来一起加入吧~", shareUrl);
                        else
                            new UMShareUtils(getActivity()).shareOne(shareMedia, R.drawable.list_error_img, shareTitle, "我在这个圈子里发现了很多志同道合的小伙伴，快来一起加入吧~", shareUrl);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(getActivity());
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    //分享到圈子
    private void shareToCircle(){
        if(TextUtils.isEmpty(shareUrl)) return;
        String infoType = "";
        //分享普通成员
        if(shareUrl.endsWith("0")){
            infoType = "4";
            //邀请管理员
        }else{
            infoType = "5";
        }
        String title = shareTitle;
        IssueDynamicActivity.share(getActivity(),id,infoType,title,sharImgUrl);
    }

    private void gotoInvite(){
        //发送邀请普通成员可以分享到群里面
        if(shareUrl.endsWith("0")){
            ConversationListActivity.startActivity(getActivity(), ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE);

            //发送管理员邀请只能分享给好友
        } else {
            ConversationListActivity.CAN_SHARE_INVITE = 5;
            ConversationListActivity.startActivity(getActivity(), ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE,ConversationListActivity.CAN_SHARE_INVITE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;
    }


}
