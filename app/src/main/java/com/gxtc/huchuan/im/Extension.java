package com.gxtc.huchuan.im;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveInsertExtensionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.plugin.PluginAdapter;

/**
 * Created by Gubr on 2017/2/15.
 * 输入的控件
 */

public class Extension extends LinearLayout implements View.OnClickListener {

    private ViewGroup         mExtensionBar;
    private RelativeLayout    mRecord;
    private RelativeLayout    mWrite;
    private RelativeLayout    mPPT;
    private RelativeLayout    mImage;
    private ImageView         mImageInsert;
    private TextView          mTvInsert;
    private TextView          mVoicText;
    private View              mEditTextLayout;
    private PluginAdapter     mPluginAdapter;
    private EditText          mEditText;
    private VoicRecordView    mVoicRerodView;
    private ViewGroup         mEditTextArea;
    private EditText          mEditText1;
    private Button            mTextSend;
    private LinearLayout      mSimpleInputModelAcrea;
    private EditText          mSimpleEdit;
    private CheckBox          mSimpleModelIsAsk;
    private View              mSimpleSend;
    private LinearLayout      mExtensionbarArea;
    private RecyclerView      rvInsert;

    private LiveInsertExtensionAdapter insertAdapter;

    private ExtensionListener mExtensionListener;
    private onClickInsertListener mClickInsertListener;

    private boolean           mInputModel;
    private boolean           isKeyBoardActive;

    private String [] insertTitles = {"PPT", "名片", "圈子", "文章", "课程", "交易", "商品", "专题"};
    private int [] insertIds = {R.drawable.icon_ppt_120,
            R.drawable.icon_mingpian_120,
            R.drawable.icon_quanzi_120,
            R.drawable.icon_wenzhang_120,
            R.drawable.icon_kecheng_120,
            R.drawable.icon_jiaoyi_120,
            R.drawable.icon_shangpin_120,
            R.drawable.icon_wenzhang_120,};

    public Extension(Context context) {
        super(context);
    }

    public Extension(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        initListener();
    }

    public Extension(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mExtensionBar = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.im_ext_extension_bar, null);
        mExtensionbarArea = (LinearLayout) mExtensionBar.findViewById(R.id.extensionbar_area);
        mRecord = (RelativeLayout) mExtensionBar.findViewById(R.id.rl_record);
        mWrite = (RelativeLayout) mExtensionBar.findViewById(R.id.rl_write);
        mPPT = (RelativeLayout) mExtensionBar.findViewById(R.id.rl_ppt);
        mImage = (RelativeLayout) mExtensionBar.findViewById(R.id.rl_image);
        mImageInsert = (ImageView) mExtensionBar.findViewById(R.id.image_ppt);
        mTvInsert = (TextView) mExtensionBar.findViewById(R.id.text_ppt);
        mImage = (RelativeLayout) mExtensionBar.findViewById(R.id.rl_image);
        mVoicText = (TextView) mExtensionBar.findViewById(R.id.voice_text_hint);
        mVoicRerodView = (VoicRecordView) mExtensionBar.findViewById(R.id.voicearea);
        mEditTextArea = (ViewGroup) mExtensionBar.findViewById(R.id.editviewarea);
        mEditText = (EditText) mEditTextArea.findViewById(R.id.edittext);
        mTextSend = (Button) mEditTextArea.findViewById(R.id.textsend);
        mSimpleInputModelAcrea = (LinearLayout) mExtensionBar.findViewById(R.id.ll_simple_input_model_acrea);
        rvInsert = mExtensionBar.findViewById(R.id.rv_live_insert);

        //观众的输入框
        mSimpleEdit = (EditText) mSimpleInputModelAcrea.findViewById(R.id.et_simple_edit);
        mSimpleModelIsAsk = (CheckBox) mSimpleInputModelAcrea.findViewById(R.id.cb_simple_model_isask);
        mSimpleSend = mSimpleInputModelAcrea.findViewById(R.id.btn_simple_send);
        mSimpleSend.setEnabled(mSimpleEdit.getText().toString().length() > 0);
        addView(mExtensionBar);

        //插入菜单初始化
        List<String> titles = new ArrayList<>();
        Collections.addAll(titles, insertTitles);
        insertAdapter = new LiveInsertExtensionAdapter(getContext(), titles, R.layout.item_live_insert_extension, insertIds);
        rvInsert.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvInsert.setAdapter(insertAdapter);
    }

    private void initListener() {
        mSimpleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSimpleEdit.getText().toString().length() > 0) {
                    mSimpleSend.setEnabled(true);
                } else {
                    mSimpleSend.setEnabled(false);
                }
            }
        });

        mSimpleSend.setOnClickListener(this);

        insertAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(mClickInsertListener != null){
                    String title = insertAdapter.getList().get(position);
                    mClickInsertListener.onClick(v, position, title);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_record:
                onRecordClick();
                break;

            case R.id.rl_write:
                onWriteClick();
                break;

            case R.id.rl_ppt:
                onPPTClick();
                break;

            case R.id.rl_image:
                onImageClick();
                break;

            case R.id.textsend:
                onTextSendClick();
                break;

            case R.id.btn_simple_send:
                onSimpleSendClick();
                break;
        }
    }

    private void onSimpleSendClick() {
        if (mExtensionListener != null) {
            String  s       = mSimpleEdit.getText().toString();
            boolean checked = mSimpleModelIsAsk.isChecked();
            mSimpleModelIsAsk.setChecked(false);
            mSimpleEdit.setText("");
            mExtensionListener.onSimpleSendClick(s, checked);
        }
    }

    private void onTextSendClick() {
        if (getEditText().getText().toString().length() <= 0) {
            Toast.makeText(getContext(), "文字内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mExtensionListener != null) {
            mExtensionListener.onTextSendClick();
        }
    }

    private void onImageClick() {
        hideInertLayout();
        if (mExtensionListener != null) {
            mExtensionListener.onImageClick();
        }
    }

    private void onPPTClick() {
        hidRecord();
        hidtWirte();
        hidInputKeyBoard();
        showInertLayout();
        //这里要跳到别的activity来显示素材
        if (mExtensionListener != null) {
            mExtensionListener.onPPT();
        }

    }

    private void onWriteClick() {
        showWrite();
        hidRecord();
        hideInertLayout();
        showInputKeyBoard();

    }

    private void onRecordClick() {
        hideInertLayout();
        mExtensionListener.getRecordPer();
    }


    public void showRecordLayout(){
        if (mExtensionListener != null) {
            hidInputKeyBoard();
            showRecord();
            hidtWirte();
        }
    }


    private void showRecordPlugin() {
        View pager = mPluginAdapter.getPager();
        if (pager == mVoicRerodView && pager.getVisibility() == GONE) {
            mPluginAdapter.setVisibility(VISIBLE);
        } else {
            mPluginAdapter.removePager(pager);
            mPluginAdapter.addPager(mVoicRerodView);
            setPluginBoard();
        }
    }

    private void initData() {
        mRecord.setOnClickListener(this);
        mWrite.setOnClickListener(this);
        mPPT.setOnClickListener(this);
        mImage.setOnClickListener(this);
        mTextSend.setOnClickListener(this);
        mVoicRerodView.setVoicListener(new VoicRerodeListener());
    }

    private void hindPluginBoard() {
        if (this.mPluginAdapter != null) {
            this.mPluginAdapter.setVisibility(GONE);
            View pager = this.mPluginAdapter.getPager();
            this.mPluginAdapter.removePager(pager);
        }
    }

    private void setPluginBoard() {
        if (mPluginAdapter.isInitialized()) {
            View pager = mPluginAdapter.getPager();
            if (pager != null) {
                pager.setVisibility(pager.getVisibility() == GONE ? VISIBLE : GONE);
            } else {
                mPluginAdapter.setVisibility(GONE);
            }
        } else {
            mPluginAdapter.bindView(this);
            mPluginAdapter.setVisibility(VISIBLE);
        }
    }


    public boolean isVoiceShow() {
        return mVoicRerodView.isShown();
    }

    public boolean isInsertLayoutShow() {
        return rvInsert.isShown();
    }

    private static final String TAG = "Extension";

    private void showRecord() {
        mVoicRerodView.setVisibility(VISIBLE);
        changeRecord(true);
    }

    private void hidRecord() {
        mVoicRerodView.setVisibility(GONE);
        changeRecord(false);
    }

    private void showInertLayout(){
        if(rvInsert.getVisibility() != VISIBLE){
            rvInsert.setVisibility(VISIBLE);
        }

        mImageInsert.setImageResource(R.drawable.person_live_icon_insert_select);
        mTvInsert.setTextColor(getResources().getColor(R.color.tool_bar_bg));
    }

    public void hideInertLayout(){
        if(rvInsert.getVisibility() == VISIBLE){
            rvInsert.setVisibility(GONE);
        }
        mImageInsert.setImageResource(R.drawable.person_live_icon_insert);
        mTvInsert.setTextColor(getResources().getColor(R.color.black1));
    }

    private void changeRecord(boolean flag) {
        for (int i = 0; i < mRecord.getChildCount(); i++) {
            View child = mRecord.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(
                        flag ? R.drawable.icon_voice_select : R.drawable.icon_voice);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(
                        getResources().getColor(flag ? R.color.tool_bar_bg : R.color.black1));
            }
        }
    }

    public void showWrite() {
        mEditTextArea.setVisibility(VISIBLE);
        changeWrite(true);
    }

    public void hidtWirte() {
        mEditTextArea.setVisibility(GONE);
        changeWrite(false);
    }

    private void changeWrite(boolean flag) {
        for (int i = 0; i < mWrite.getChildCount(); i++) {
            View child = mWrite.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(
                        flag ? R.drawable.icon_text_select : R.drawable.icon_text);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(
                        getResources().getColor(flag ? R.color.tool_bar_bg : R.color.black1));
            }
        }
    }

    private void hidInputKeyBoard() {
        if (!isKeyBoardActive) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
        mEditText.clearFocus();
        isKeyBoardActive = false;
    }

    private void showInputKeyBoard() {
        this.mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.mEditText, 0);
        this.isKeyBoardActive = true;
    }


    public void closeInput() {
        if (!mVoicText.isShown() && mVoicRerodView.isShown()) {
            hidRecord();
        }
    }


    private class VoicRerodeListener implements VoicRecordView.OnVoicListener {

        @Override
        public void onOneModelStart() {
            mVoicText.setBackgroundResource(R.drawable.select_btn_blue);
            mVoicText.setVisibility(VISIBLE);
            mVoicText.setText(R.string.topic_detail_record_hint);
            if (mExtensionListener != null) {
                mExtensionListener.onStartRecord(getContext());
            }
        }

        @Override
        public void onOneModelStop() {
            mVoicText.setText(R.string.topic_detail_record_next);
            if (mExtensionListener != null) {
                mExtensionListener.onStopRecord();
            }
        }

        @Override
        public void onOneModelSend() {
            mVoicText.setVisibility(GONE);

            if (mExtensionListener != null) {
                mExtensionListener.onSend();
            }
        }

        @Override
        public void onLongModelTouchDown() {
            mVoicText.setVisibility(VISIBLE);
            mVoicText.setBackgroundResource(R.drawable.select_btn_blue);
            mVoicText.setText(R.string.topic_detail_record_long_in);
            if (mExtensionListener != null) {
                mExtensionListener.onStartRecord(getContext());
            }
        }

        @Override
        public void onLongModelTouchIsOutSize(boolean flag) {
            if (flag) {
                mVoicText.setBackgroundColor(Color.RED);
                mVoicText.setText(R.string.topic_detail_record_long_out);
            } else {
                mVoicText.setBackgroundResource(R.drawable.select_btn_blue);
                mVoicText.setText(R.string.topic_detail_record_long_in);
            }

        }

        @Override
        public void onLongModelTouchUp(boolean flag) {
            mVoicText.setVisibility(GONE);

            if (mExtensionListener != null) {
                if (flag) {
                    mExtensionListener.onCancnelRecord();
                } else {
                    mExtensionListener.onStopRecordAndSend();
                }
            }

        }

        @Override
        public void onVoicCancel() {
            mVoicText.setVisibility(GONE);
            if (mExtensionListener != null) {
                mExtensionListener.onCancnelRecord();
            }
        }
    }


    public void addEdittextListener(TextWatcher watcher) {
        getEditText().addTextChangedListener(watcher);
    }


    /**
     * 主播输入模式
     */
    public void hostInputModel() {
        inputModel(true);
    }


    /**
     * 观众输入模式
     */
    public void audienceInputModel() {
        inputModel(false);
    }


    private void inputModel(boolean flag) {//true  为主持人模式
        mInputModel = flag;
        mExtensionbarArea.setEnabled(flag);
        Log.d(TAG, "flag:" + flag);
        mExtensionbarArea.setVisibility(flag ? VISIBLE : GONE);
        mSimpleInputModelAcrea.setVisibility(flag ? View.GONE : View.VISIBLE);
    }


    /**
     * @return true 为主持人模式  false 为观众模式
     */
    public boolean getInputModel() {
        return mInputModel;
    }


    public EditText getEditText() {
        return mInputModel ? mEditText : mSimpleEdit;
    }

    public void setExtensionListener(ExtensionListener extensionListener) {
        mExtensionListener = extensionListener;
    }

    public interface ExtensionListener {
        /**
         * 发送文本按钮
         */
        void onTextSendClick();


        /**
         * 获取录音权限
         *
         * @return
         */
        boolean getRecordPer();

        /**
         * 图片按钮
         */
        void onImageClick();

        /**
         * 开始录音
         */
        void onStartRecord(Context context);

        /**
         * 停止录音
         */
        void onStopRecord();

        /**
         * 停止并发送录音
         */
        void onStopRecordAndSend();

        /**
         * 取消录音
         */
        void onCancnelRecord();

        /**
         * 发送录音
         */
        void onSend();

        /**
         * 打开PPT库
         */
        void onPPT();

        /**
         * 简单模式下的发送按钮
         *
         * @param s
         * @param checked
         */
        void onSimpleSendClick(String s, boolean checked);
    }


    public interface onClickInsertListener{

        void onClick(View v, int position, String title);

    }

    public void setClickInsertListener(onClickInsertListener clickInsertListener) {
        mClickInsertListener = clickInsertListener;
    }
}
