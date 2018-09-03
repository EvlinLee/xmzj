package com.gxtc.huchuan.customemoji.utils;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.gxtc.huchuan.customemoji.adapter.EmotionGridViewAdapter;

import io.rong.imkit.emoticon.AndroidEmoji;


public class GlobalOnItemClickManagerUtils {

    private static volatile GlobalOnItemClickManagerUtils instance;
    private EditText mEditText;//输入框

    public static GlobalOnItemClickManagerUtils getInstance() {
        if (instance == null) {
            synchronized (GlobalOnItemClickManagerUtils.class) {
                if(instance == null) {
                    instance = new GlobalOnItemClickManagerUtils();
                }
            }
        }
        return instance;
    }

    public void attachToEditText(EditText editText) {
        mEditText = editText;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(final int emotion_map_type) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAdapter = parent.getAdapter();

                if (itemAdapter instanceof EmotionGridViewAdapter) {
                    // 点击的是表情
                    EmotionGridViewAdapter emotionGvAdapter = (EmotionGridViewAdapter) itemAdapter;

                    if (position == emotionGvAdapter.getCount() - 1) {
                        // 如果点击了最后一个回退按钮,则调用删除键事件
                        mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    } else {
                        // 如果点击了表情,则添加到输入框中
                        int emotionName = emotionGvAdapter.getItem(position);
                        String emojiConverCode = EmotionUtils.getEmojiString(emotionName);

                        // 获取当前光标位置,在指定位置上添加表情图片文本
                        int curPosition = mEditText.getSelectionStart();
                        StringBuilder sb = new StringBuilder(mEditText.getText().toString());
                        sb.insert(curPosition,emojiConverCode);

                        mEditText.setText(AndroidEmoji.ensure(sb.toString()));
                        // 特殊文字处理,将表情等转换一下
                        //mEditText.setText(SpanStringUtils.getEmotionContent(emotion_map_type, MyApplication.getInstance(), mEditText, sb.toString()));
                        // 将光标设置到新增完表情的右侧
                        //mEditText.setSelection(curPosition + emotionName.length());
                        mEditText.setSelection(curPosition + emojiConverCode.length());
                    }

                }
            }
        };
    }


}
