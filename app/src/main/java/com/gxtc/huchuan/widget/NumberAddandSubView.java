package com.gxtc.huchuan.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.melink.bqmmplugin.rc.baseframe.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumberAddandSubView extends LinearLayout implements TextView.OnEditorActionListener {

    @BindView(R.id.add_btn) TextView mAddBtn;
    @BindView(R.id.sub_btn) TextView mSubBtn;
    @BindView(R.id.number)  EditText mNumber;

    private TextView mAddButton, mSubButton;
    private EditText mEditText;

    private int num = 1;          //editText中的数值
    private int max = 1;
    private Context             mContext;
    private OnNumChangeListener onNumChangeListener;

    public NumberAddandSubView(Context context) {
        this(context, null);
    }

    public NumberAddandSubView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddandSubView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContext = getContext();
        View view = inflate(mContext, R.layout.view_number_add_sub, null);
        ButterKnife.bind(this, view);
        this.setOrientation(HORIZONTAL);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.addView(view);

        //设置输入类型为数字
        mNumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mNumber.setText(String.valueOf(num));
        mNumber.addTextChangedListener(new OnTextChangeListener());
        mNumber.setOnEditorActionListener(this);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /**
     * 设置editText中的值
     *
     * @param num
     */
    public void setNum(int num) {
        this.num = num;
        mNumber.setText(String.valueOf(num));
    }

    /**
     * 获取editText中的值
     *
     * @return
     */
    public int getNum() {
        if (mNumber.getText().toString() != null) {
            return Integer.parseInt(mNumber.getText().toString());
        } else {
            return 0;
        }
    }

    /**
     * 设置EditText文本变化监听
     *
     * @param onNumChangeListener
     */
    public void setOnNumChangeListener(OnNumChangeListener onNumChangeListener) {
        this.onNumChangeListener = onNumChangeListener;
    }

    @OnClick({R.id.add_btn, R.id.sub_btn})
    public void onClick(View view) {
        String numString = mNumber.getText().toString();
        if (numString == null || numString.equals("")) {
            num = 0;
            mNumber.setText("1");
        }
        switch (view.getId()) {
            case R.id.add_btn:
                if (++num < 1)  //先加，再判断
                {
                    num--;
                    ToastUtil.showShort(mContext, "请输入一个大于0的数字");
                } else {
                    if (num <= max) {
                        mNumber.setText(String.valueOf(num));
                        if (onNumChangeListener != null) {
                            onNumChangeListener.onNumChange(NumberAddandSubView.this, num);
                        }
                    }else{
                        num--;
                    }

                }
                break;
            case R.id.sub_btn:
                if (--num < 1)  //先减，再判断
                {
                    num++;
                    //MyToast.show(mContext, "请输入一个大于0的数字");
                } else {

                    if(num > 0){
                        mNumber.setText(String.valueOf(num));
                        if (onNumChangeListener != null) {
                            onNumChangeListener.onNumChange(NumberAddandSubView.this, num);
                        }
                    }else{
                        num ++;
                    }

                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            String numString = mNumber.getText().toString();
            if (numString == null || numString.equals("")) {
                num = 1;
                mNumber.setText("1");
                if (onNumChangeListener != null) {
                    onNumChangeListener.onNumChange(NumberAddandSubView.this, num);
                }
            } else {
                int numInt = Integer.parseInt(numString);
                if (numInt < 0) {
                    Toast.makeText(mContext, "请输入一个大于0的数字", Toast.LENGTH_SHORT).show();
                } else {
                    //设置EditText光标位置 为文本末端
                    mNumber.setSelection(mNumber.getText().toString().length());
                    num = numInt;
                    if (onNumChangeListener != null) {
                        onNumChangeListener.onNumChange(NumberAddandSubView.this, num);
                    }
                }
            }
            mNumber.setCursorVisible(false);
            return true;
        }
        return false;
    }


    /**
     * EditText输入变化事件监听器
     *
     * @author ZJJ
     */
    class OnTextChangeListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            String numString = s.toString();
            if (numString == null || numString.equals("")) {
                num = 0;
                if (onNumChangeListener != null) {
                    onNumChangeListener.onNumChange(NumberAddandSubView.this, num);
                }
            } else {
                int numInt = Integer.parseInt(numString);
                if (numInt < 0) {
                    Toast.makeText(mContext, "请输入一个大于0的数字", Toast.LENGTH_SHORT).show();
                } else {
                    //设置EditText光标位置 为文本末端
                    mNumber.setSelection(mNumber.getText().toString().length());
                    num = numInt;
                    if (onNumChangeListener != null) {
                        onNumChangeListener.onNumChange(NumberAddandSubView.this, num);
                    }
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (StringUtils.toInt(s) <= 0 && !StringUtils.isEmpty(s)) {
                mNumber.setText("1");
            }
            if (StringUtils.toInt(s) > max) {
                ToastUtil.showShort(mContext, "最多能购买" + max + "件");
                mNumber.setText(String.valueOf(max));
            }
        }

    }

    public interface OnNumChangeListener {
        /**
         * 输入框中的数值改变事件
         *
         * @param view 整个AddAndSubView
         * @param num  输入框的数值
         */
        public void onNumChange(View view, int num);
    }
}
