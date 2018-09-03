package com.gxtc.commlibrary.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/24.
 * 解决当点击空白区域软键盘无法关闭的问题
 */

public class KeyboardDialog extends Dialog {

    public KeyboardDialog(@NonNull Context context,int resSty) {
        super(context,resSty);
    }

    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if(view instanceof EditText){
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        super.dismiss();
    }
}
