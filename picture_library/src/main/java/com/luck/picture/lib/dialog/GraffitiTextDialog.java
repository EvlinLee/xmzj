package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.R;
import com.luck.picture.lib.utils.Utils;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/28.
 */

public class GraffitiTextDialog extends DialogFragment implements View.OnClickListener {


    private KeyboardDialog dialog ;
    private View dialogView;
    private TextView tvCancel;
    private TextView tvFinish;
    private EditText edit;

    private int color;
    private String text = "";
    private OnTextListener textListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogView = View.inflate(getContext(), R.layout.dialog_graffiti_text, null);
        tvCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        tvFinish = (TextView) dialogView.findViewById(R.id.btn_finish);
        edit = (EditText) dialogView.findViewById(R.id.edit_content);

        edit.setTextColor(color);
        edit.setText(text);
        edit.setSelection(text.length());

        tvCancel.setOnClickListener(this);
        tvFinish.setOnClickListener(this);


        if(dialog == null)
            dialog = new KeyboardDialog(getActivity(), R.style.dialog_Translucent);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.windowAnimations = R.style.mypopwindow_anim_style;

        return dialog;
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            dismiss();

        } else if (i == R.id.btn_finish) {
            if(!TextUtils.isEmpty(edit.getText().toString())){
                if(textListener != null ){
                    textListener.textFinish(edit.getText().toString());
                    dismiss();
                }

            }else{
                Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        if(!Utils.isFastDoubleClick() && manager.findFragmentByTag(tag) == null){
            manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
        }
        if(edit != null){
            edit.setText(text);
        }
    }

    public void setOnTextListener(OnTextListener onTextListener){
        textListener = onTextListener;
    }

    public interface OnTextListener{
        void textFinish(String text);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    class KeyboardDialog extends Dialog {

        public KeyboardDialog(@NonNull Context context, int resSty) {
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

}
