package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventRefundBean;
import com.gxtc.huchuan.customemoji.fragment.EmotionMainFragment;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 评论面板
 * Created by Steven on 17/5/2.
 */

public class PopComment extends BasePopupWindow {

    @BindView(R.id.btn_cancel)     TextView btnCancel;
    @BindView(R.id.btn_pop_send)   TextView btnSend;
    @BindView(R.id.tv_title)       TextView tvTitle;
    @BindView(R.id.edit_content)   EditText editContent;

    private FragmentManager fragmentManager ;

    private EmotionMainFragment emotionMainFragment;

    private View.OnClickListener mClickListener;

    public PopComment(FragmentActivity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.btn_cancel, R.id.btn_pop_send})
    public void onClick(View v) {
        WindowUtil.closeInputMethod(getActivity());
        switch (v.getId()) {
            case R.id.btn_cancel:
                clear();
                closePop();
                break;

            case R.id.btn_pop_send:
                EventBusUtil.post(new EventRefundBean(editContent.getText().toString()));
                closePop();
                break;
        }

        if (mClickListener != null) {
            mClickListener.onClick(v);
        }
    }


    public String getContent() {
        return editContent.getText().toString();
    }

    /**
     * 清除内容
     */
    public void clear() {
        editContent.setText("");
    }

    @Override
    public void showPopOnRootView(Activity activity) {
        super.showPopOnRootView(activity);
      //  initEmotionMainFragment();
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void changeThemeText() {
        btnSend.setText("确定");
        editContent.setHint("请输入内容");
        tvTitle.setVisibility(View.INVISIBLE);
    }
}
