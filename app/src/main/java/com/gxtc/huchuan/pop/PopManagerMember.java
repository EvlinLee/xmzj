package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.utils.DialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PopManagerMember extends BasePopupWindow {


    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_kick)
    TextView tvKick;
    @BindView(R.id.tv_blacklist)
    TextView tvBlacklist;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private SignUpMemberBean mBean;

    int position = -1;


    public static final String KICK_ONCLICK = "kick_onclick";
    public static final String CANCEL_BACK = "cancel_back";
    public static final String ADD_BACK = "add_back";


    private View.OnClickListener mListener;


    public PopManagerMember(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
    }


    @Override
    public void initListener() {

    }


    public void setData(SignUpMemberBean data, int position) {
        mBean = data;
        this.position = position;
        if (mBean != null) {
            tvName.setText(mBean.getName());
            ImageHelper.loadImage(getActivity(), ivHead, mBean.getHeadPic(), R.drawable
                    .person_icon_head_120);
            if (mBean.bisBlack()) {
                tvBlacklist.setText("取消黑名单");
            } else {
                tvBlacklist.setText("加入黑名单");
            }
        }
    }

    public void setOnDialogListener(View.OnClickListener listener) {
        mListener = listener;
    }


    @OnClick({R.id.tv_kick, R.id.tv_blacklist, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_kick:
                showKickDialog();
                break;
            case R.id.tv_blacklist:
                Log.d("PopManagerMember", mBean.getIsBlack());
                if (mBean.bisBlack()) {
                    showCancelBackDialog();
                } else {
                    showAddBackDialog();
                }
                break;
            case R.id.tv_cancel:
                closePop();
                break;
        }
    }


    private void showKickDialog() {
        DialogUtil.showMemberManagerDialog(getActivity(),
                "踢出后，该用户将不能访问本课程。\n若用户已付款，请与用户自行协商退款问题。", "确定将" + mBean.getName() + "踢出？", new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这里去修改
                        v.setTag(KICK_ONCLICK);
                        mListener.onClick(v);
                    }
                });
    }

    private void showCancelBackDialog() {
        DialogUtil.showMemberManagerDialog(getActivity(),
                null, "确定将" + mBean.getName() + "取消黑名单？", new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这里去修改
                        v.setTag(CANCEL_BACK);
                        mListener.onClick(v);
                    }
                });
    }

    private void showAddBackDialog() {
        DialogUtil.showMemberManagerDialog(getActivity(),
                "黑名单后，将拒绝该用户访问你的课堂", "确定将" + mBean.getName() + "加入黑单？", new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这里去修改
                        v.setTag(ADD_BACK);
                        mListener.onClick(v);
                    }
                });
    }

    public SignUpMemberBean getBean() {
        return mBean;
    }

    public int getPosition() {
        return position;
    }
}
