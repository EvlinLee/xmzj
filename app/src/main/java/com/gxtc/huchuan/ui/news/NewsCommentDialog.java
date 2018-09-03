package com.gxtc.huchuan.ui.news;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;

/**
 * Created by sjr on 2017/3/6.
 * 评论对话框
 * 统一风格
 * @see com.gxtc.huchuan.dialog.DynamicCommentDialog
 */
@Deprecated
public class NewsCommentDialog extends Dialog implements
        android.view.View.OnClickListener {


    private Context mContext;
    private EditText mEdittext;
    private TextView mTvCancel;
    private TextView mTvSend;
    private OnSendListener onSendListener;


    public NewsCommentDialog(Context context) {
        super(context, R.style.dialog_Translucent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mContext = context;

        View v = View.inflate(mContext, R.layout.dialog_news_comment, null);

        initView(v);
        setContentView(v);
        setLayout();
        setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                mEdittext.setFocusableInTouchMode(true);
                mEdittext.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) mEdittext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mEdittext, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void initView(View v) {
        mEdittext = (EditText) v.findViewById(R.id.et_comment);
        mTvCancel = (TextView) v.findViewById(R.id.tv_cancel);
        mTvSend = (TextView) v.findViewById(R.id.tv_send);
        mTvCancel.setOnClickListener(this);
        mTvSend.setOnClickListener(this);
    }

    private void setLayout() {
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;

            case R.id.tv_send:
                String content = mEdittext.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(mContext, "请输入评论内容");
                    return;
                }

                if (onSendListener != null) {
                    onSendListener.sendComment(content);
                    mTvSend.setTextColor(getContext().getResources().getColor(R.color.my_oroder_sub));
                    mTvSend.setOnClickListener(null);
                }
                break;

            default:
                break;
        }
    }

    public void clearContent(){
        if(mEdittext != null){
            mEdittext.setText("");
            mTvSend.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            mTvSend.setOnClickListener(this);
        }
    }

    public void reset(){
        mTvSend.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        mTvSend.setOnClickListener(this);
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public interface OnSendListener {
        void sendComment(String content);
    }

    public EditText getEdittext() {
        return mEdittext;
    }
}
