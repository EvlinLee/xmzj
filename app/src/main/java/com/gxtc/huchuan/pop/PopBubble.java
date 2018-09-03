package com.gxtc.huchuan.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BaseBubblePopup;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventPopBubleBean;

/**
 * Describe:系列课分类item弹出框
 * Created by ALing on 2017/3/30 .
 */

public class PopBubble extends BaseBubblePopup<PopBubble>{

    private TextView mTvEdit;
    private TextView mTvDel;

    public PopBubble(Context context) {
        super(context);
    }

    @Override
    public View onCreateBubbleView() {
        View inflate = View.inflate(mContext, R.layout.popup_bubble_classify_edit, null);
        mTvEdit = (TextView) inflate.findViewById(R.id.tv_edit);
        mTvDel = (TextView) inflate.findViewById(R.id.tv_del);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        mTvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                EventBusUtil.post(new EventPopBubleBean("0"));
            }
        });
        mTvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                EventBusUtil.post(new EventPopBubleBean("1"));
            }
        });
    }

}
