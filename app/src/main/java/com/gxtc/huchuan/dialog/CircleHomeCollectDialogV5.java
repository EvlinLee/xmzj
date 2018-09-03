package com.gxtc.huchuan.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steven on 17/4/21.
 * 收藏对话框
 */

public class CircleHomeCollectDialogV5 extends BasePopup<CircleHomeCollectDialogV5> implements View.OnClickListener {

    @BindView(R.id.tv_circle_copy)
    TextView tvShieldDynamic;
    @BindView(R.id.tv_circle_collect)
    TextView tvShieldUser;
    @BindView(R.id.set_best)
    TextView tvSetBest;
    @BindView(R.id.delete)
    TextView btnDelete;
    @BindView(R.id.toTop)
    TextView tvTop;
    @BindView(R.id.forbident_sent_dynamic)
    TextView tvForbident;
    int isMy;
    int isTop;

    private View.OnClickListener listener;

    public CircleHomeCollectDialogV5(Context context) {
        super(context);
    }
    public CircleHomeCollectDialogV5(Context context,int isMy,int isTop) {
        super(context);
        this.isMy=isMy;
        this.isTop=isTop;
    }

    @Override
    public void setUiBeforShow() {
        if(isMy == 1){
            tvSetBest.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            tvTop.setVisibility(View.VISIBLE);
            tvForbident.setVisibility(View.VISIBLE);
        }else {
            tvSetBest.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            tvTop.setVisibility(View.GONE);
            tvForbident.setVisibility(View.GONE);
        }
        setText();
    }

    @Override
    public View onCreatePopupView() {
        View view = View.inflate(getContext(), R.layout.dialog_circle_home_collect, null);
        ButterKnife.bind(this, view);
        tvShieldDynamic.setOnClickListener(this);
        tvShieldUser.setOnClickListener(this);
        tvSetBest.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        tvTop.setOnClickListener(this);
        tvForbident.setOnClickListener(this);
        return view;
    }

    private void setText() {
        if(isTop == 1){
            tvTop.setText("取消置顶");
        }else {
            tvTop.setText("置顶");
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public void isShowCollect(boolean flag) {
        if (!flag) {
            tvShieldUser.setVisibility(View.GONE);
        }
    }
}
