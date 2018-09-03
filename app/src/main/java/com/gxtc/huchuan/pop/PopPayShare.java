package com.gxtc.huchuan.pop;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.ui.live.intro.ShareImgActivity;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jsoup.Jsoup;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/5/2.
 */

public class PopPayShare extends BasePopupWindow {

    @BindView(R.id.btn_wx)         LinearLayout  mBtnWx;
    @BindView(R.id.btn_friend)     LinearLayout  mBtnFriend;
//    @BindView(R.id.btn_qq)         LinearLayout  mBtnQq;
//    @BindView(R.id.btn_kongjian)   LinearLayout  mBtnKongjian;
    @BindView(R.id.btn_invitation) LinearLayout  mBtnCopy;
    @BindView(R.id.tv_title)       TextView      mTvTitle;
    @BindView(R.id.tv_content)     TextView      mTvContent;
    private                        ChatInfosBean mData;

    private static final String title   = "分享赚￥%s";
    public static final  String content = "每当有1个人通过你分享的连接购买成功，你就可以获得订单金额%s (即%s)的佣金奖励哦";

    public PopPayShare(FragmentActivity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
    }

    @Override
    public void initListener() {

    }


    @OnClick({R.id.btn_wx, R.id.btn_friend, R.id.btn_invitation, R.id.tv_dimiss})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wx:
                SHARE_MEDIA weixin = SHARE_MEDIA.WEIXIN;
                share(weixin);
                break;

            case R.id.btn_friend:
                SHARE_MEDIA weixinCircle = SHARE_MEDIA.WEIXIN_CIRCLE;
                share(weixinCircle);
                break;

//            case R.id.btn_qq:
//                SHARE_MEDIA qq = SHARE_MEDIA.QQ;
//                share(qq);
//                break;
//
//            case R.id.btn_kongjian:
//                SHARE_MEDIA qzone = SHARE_MEDIA.QZONE;
//                share(qzone);
//                break;

            case R.id.btn_invitation://邀请卡
                Intent intent = new Intent(getActivity(), ShareImgActivity.class);
                intent.putExtra("chatInfoId", mData.getId());
                intent.putExtra("isPay", "true");
                getActivity().startActivity(intent);
                break;

            case R.id.tv_dimiss:
                closePop();
                break;
        }


    }


    public void setData(ChatInfosBean data) {
        mData = data;

        Double  fee   = Double.valueOf(mData.getFee());
        Integer pent  = Integer.valueOf(mData.getPent());
        double  price = fee * pent / 100;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String        format        = decimalFormat.format(price);
        mTvTitle.setText("分享赚￥" + price);
        mTvContent.setText(String.format(content, pent + "%", format));
    }


    private void share(final SHARE_MEDIA shareMedia) {
        String   desc  = TextUtils.isEmpty(mData.getDesc()) ? title : mData.getDesc();
        desc = Jsoup.parse(desc).body().text();
        new UMShareUtils((FragmentActivity) getActivity()).shareOne(shareMedia, mData.getFacePic(),
                mData.getSubtitle(), desc, mData.getShareUrl() + "&type=1");
    }


}
