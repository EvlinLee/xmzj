package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.KeyboardDialog;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventRefundBean;
import com.gxtc.huchuan.customemoji.fragment.EmotionMainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/24.
 */

public class CommentDialog extends DialogFragment {

    @BindView(R.id.btn_cancel)     TextView btnCancel;
    @BindView(R.id.btn_pop_send)   TextView btnSend;
    @BindView(R.id.tv_title)       TextView tvTitle;
    @BindView(R.id.edit_content)   EditText editContent;
    @BindView(R.id.layout_emotion) View     layoutEmotion;

    private View          rootView;
    //private MyEmotionView mEmotionView;

    private EmotionMainFragment emotionMainFragment;

    private View.OnClickListener mClickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_face_comment, null);
        ButterKnife.bind(this, rootView);

        KeyboardDialog dialog = new KeyboardDialog(getActivity(), R.style.dialog_Translucent);
        dialog.setContentView(rootView);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        //mEmotionView = new MyEmotionView(getContext(), layoutEmotion,editContent);

        return dialog;
    }


    @OnClick({R.id.btn_cancel, R.id.btn_pop_send})
    public void onClick(View v) {
        WindowUtil.closeInputMethod(getActivity());
        switch (v.getId()) {
            case R.id.btn_cancel:
                clear();
                dismiss();
                break;

            case R.id.btn_pop_send:
                EventBusUtil.post(new EventRefundBean(editContent.getText().toString()));
                dismiss();
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

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void changeThemeText() {
        btnSend.setText("确定");
        editContent.setHint("请输入内容");
        tvTitle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if(manager.findFragmentByTag("dialog") == null){
            manager.beginTransaction().add(this,tag).commitAllowingStateLoss();//不要使用父类的commit方法，容易在Activity因异常销毁而报错
        }
    }
}
