package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.widget.JustifyTextView;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.lang.ref.WeakReference;


/**
 * Created by Steven on 16/10/13.
 */

public class DialogUtil {


    public static AlertDialog createDialog(Activity context, String title, String content,
                                             String cancelText, String okText,
                                             DialogClickListener callBack) {
        return DialogUtil.createDialog(context, title, content, cancelText, okText, callBack, false);
    }

    public static AlertDialog createDialog2(Activity context, String content,
                                              String cancelText, String okText,
                                            DialogClickListener callBack) {
        return DialogUtil.createDialog(context, "", content, cancelText, okText, callBack, false);
    }

    public static AlertDialog createDialog(Activity context, String title, String content,
                                             String cancelText, String okText,
                                             final DialogClickListener callBack, boolean
                                                     OutSideCancel) {
        //不要用MDAlertDialog，源码里定义了一个静态的context,造成内存泄漏，可是源码又改不了(因为引用第三方夹包的)，只能重写一个dialog
        WeakReference<Activity> weakReference = new WeakReference<>(context);
        if(weakReference .get() == null) return null;

        Activity wRactivity = weakReference .get();
        final AlertDialog dialog = new AlertDialog.Builder(wRactivity).create();
        //自定义布局
        View view = wRactivity.getLayoutInflater().inflate(R.layout.dialog_common, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = wRactivity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(OutSideCancel);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        TextView tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        if(TextUtils.isEmpty(title)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);
        tvContent.setText(content);
        leftButton.setText(cancelText);
        rightButton.setText(okText);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.clickLeftButton(view);
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.clickRightButton(view);
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }


    public static AlertDialog createDialog(Activity activity, View layoutView) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        dialog.setView(layoutView, 0, 0, 0, 0);
        dialog.show();
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        return dialog;
    }

    public static AlertDialog createAuseDialog(Activity context, String title, String content,
                                           String cancelText, String okText,
                                           final DialogClickListener callBack, boolean
                                                   OutSideCancel) {
        //不要用MDAlertDialog，源码里定义了一个静态的context,造成内存泄漏，可是源码又改不了(因为引用第三方夹包的)，只能重写一个dialog
        WeakReference<Activity> weakReference = new WeakReference<>(context);
        if(weakReference .get() == null) return null;

        Activity wRactivity = weakReference .get();
        final AlertDialog dialog = new AlertDialog.Builder(wRactivity).create();
        //自定义布局
        View view = wRactivity.getLayoutInflater().inflate(R.layout.dialog_auth_layout, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = wRactivity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(OutSideCancel);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        JustifyTextView tvContent = (JustifyTextView) view.findViewById(R.id.tv_dialog_content);
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        if(TextUtils.isEmpty(title)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);
        tvContent.setText(content);
        leftButton.setText(cancelText);
        rightButton.setText(okText);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.clickLeftButton(view);
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.clickRightButton(view);
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    /**
     * 通用风格dialog
     */
    public static AlertDialog showCommonDialog(final Context context, boolean isShowTitle,
                                              String titleText, String contentText, View
                                                      .OnClickListener listener) {
        if(context == null) return null;
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        //自定义布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = WindowUtil.getScreenWidth(context);
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);          //标题
        TextView tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);      //内容
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);      //取消
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);    //确定
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        if (contentText == null) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(contentText);
        }
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }


    /**
     * 头顶带有条线的对话框，这里的取消按钮默认只是对话框消失
     *
     * @param activity
     * @param isShowTitle 是否显示标题
     * @param titleText   标题文字
     * @param contentText
     * @param listener    确认对话框监听
     */
    public static AlertDialog showInputDialog(final Activity activity, boolean isShowTitle,
                                              String titleText, String contentText, View
                                                      .OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_common, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = (int) (width * 0.6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);          //标题
        TextView tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);      //内容
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);      //取消
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);    //确定
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        if (contentText == null) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(contentText);
        }
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }

    public static AlertDialog showDeportDialog(final Activity activity, boolean isShowTitle,
                                              String titleText, String contentText, View
                                                      .OnClickListener listener) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        if(weakReference .get() == null) return null;

        Activity wRactivity = weakReference .get();
        final AlertDialog dialog = new AlertDialog.Builder(wRactivity).create();
        //自定义布局
        View view = wRactivity.getLayoutInflater().inflate(R.layout.dialog_common, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = wRactivity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);

        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        TextView tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        if (contentText == null) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(contentText);
        }
        leftButton.setOnClickListener(listener);
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }

    public static AlertDialog showNoticeDialog(final Activity activity, boolean isShowTitle,
                                              String titleText, String contentText, View
                                                      .OnClickListener listener) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        if(weakReference .get() == null) return null;

        Activity wRactivity = weakReference .get();
        final AlertDialog dialog = new AlertDialog.Builder(wRactivity).create();
        //自定义布局
        View view = wRactivity.getLayoutInflater().inflate(R.layout.dialog_notice_layout, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = wRactivity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        JustifyTextView tvContent = (JustifyTextView) view.findViewById(R.id.tv_dialog_content);
        //确定
        Button confirmButton = (Button) view.findViewById(R.id.tv_dialog_confirm);
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        if (contentText == null) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(contentText);
        }
        confirmButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }


    public static AlertDialog showNoteDialog(final Activity activity, boolean isShowTitle,
                                              String titleText, String contentText, View
                                                      .OnClickListener listener) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        if(weakReference .get() == null) return null;

        Activity wRactivity = weakReference .get();
        final AlertDialog dialog = new AlertDialog.Builder(wRactivity).create();
        //自定义布局
        View view = wRactivity.getLayoutInflater().inflate(R.layout.dialog_note, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = wRactivity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        JustifyTextView tvContent = (JustifyTextView) view.findViewById(R.id.tv_dialog_content);
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        if (contentText == null) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(contentText);
        }
        leftButton.setOnClickListener(listener);
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }

    public static AlertDialog showRefundDialog(final Activity activity, boolean isShowTitle,
                                               String titleText, String oldText, View
                                                       .OnClickListener listener, View view) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_confirm);
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }


    public static AlertDialog showInputDialog(final Activity activity, boolean isShowTitle,
            String titleText, String contentText,String cancelbtntext,String submitText, View
            .OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_common, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = (int) (width * 0.6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        TextView tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        if (cancelbtntext != null) {
            leftButton.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            leftButton.setText(cancelbtntext);
        }

        View line1 = view.findViewById(R.id.line_horizontal);
        View line2 = view.findViewById(R.id.line_vertical);

        if(TextUtils.isEmpty(cancelbtntext)){
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }

        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        if (submitText!=null){
            rightButton.setText(submitText);
        }
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        if (contentText == null) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(contentText);
        }
        leftButton.setOnClickListener(listener);
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }


    public static AlertDialog showCircleDialog(final Activity activity,
                                               View.OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_circle, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        rightButton.setOnClickListener(listener);
        dialog.show();
        return dialog;
    }

    private static EditText etContent;

    public static AlertDialog showInputDialog2(final Activity activity, boolean isShowTitle,
                                               String titleText, String oldText, View
                                                       .OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_classify_input, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        //内容
        etContent = (EditText) view.findViewById(R.id.et_input);
        etContent.setText(oldText);
        etContent.setSelection(etContent.getText().length());
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_confirm);
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        //        tvContent.setText(contentText);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }
    public static AlertDialog showGriupDialog(final Activity activity, View view,View
                                                       .OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_confirm);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }

    public static String getEtInput() {
        //        String inputSring = et.getText().toString();
        String inputSring = etContent.getText().toString();
        return inputSring;
    }

    public static AlertDialog showTopicInputDialog(Activity activity, String titleText, String
            hint, String conten, final View.OnClickListener listener) {
        return showTopicInputDialog(activity, titleText, hint, conten, 0, 0, listener);
    }


    public static AlertDialog showTopicInputDialog(Activity activity, String titleText, String
            hint, String content, int inputType, int editLines
            , final View.OnClickListener listener) {

        View view = View.inflate(activity, R.layout.dialog_edit_topic, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogStyle2);
        final AlertDialog mAlertDialog = builder.setView(view).create();

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(titleText);

        View cancel = view.findViewById(R.id.btn_cancel);

        final EditText mEdit = (EditText) view.findViewById(R.id.edit);
        mEdit.setHint(hint);
        if (editLines > 0) {
            if(editLines == 1){
                mEdit.setSingleLine();
            }else{
                mEdit.setLines(editLines);
            }
        }
        if (inputType > 0){
            mEdit.setInputType(inputType);
        }

        if (!TextUtils.isEmpty(content)) {
            mEdit.setText(content);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        View okview = view.findViewById(R.id.btn_issue);
        okview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(mEdit.getText().toString());
                listener.onClick(v);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
        return mAlertDialog;
    }

    public static AlertDialog VerificationDialog(Activity activity
            , final View.OnClickListener listener) {

        View view = View.inflate(activity, R.layout.dialog_verification, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogStyle2);
        final AlertDialog mAlertDialog = builder.setView(view).create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener clicListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                mAlertDialog.dismiss();
            }
        };
        view.findViewById(R.id.btn_vertican).setOnClickListener(clicListener);
        view.findViewById(R.id.btn_writer).setOnClickListener(clicListener);
        view.findViewById(R.id.btn_profeess).setOnClickListener(clicListener);
        view.findViewById(R.id.btn_pay_circle).setOnClickListener(clicListener);
        view.findViewById(R.id.btn_cancel).setOnClickListener(clicListener);
        view.findViewById(R.id.btn_issue).setOnClickListener(clicListener);
        mAlertDialog.show();
        return mAlertDialog;
    }

    public static AlertDialog showSysDialog(Activity activity
            , final View.OnClickListener listener) {

        View view = View.inflate(activity, R.layout.dialog_file_sys_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, 0);
        final AlertDialog mAlertDialog = builder.setView(view).create();
        View.OnClickListener clicListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.tv_cancel || v.getId() == R.id.tv_sure){
                    mAlertDialog.dismiss();
                }
                listener.onClick(v);
            }
        };
        view.findViewById(R.id.tv_issue_tongbu).setOnClickListener(clicListener);
        view.findViewById(R.id.tv_cancel).setOnClickListener(clicListener);
        view.findViewById(R.id.tv_sure).setOnClickListener(clicListener);
        mAlertDialog.show();
        return mAlertDialog;
    }

    public static AlertDialog showBindPhoneDialog(Activity activity, View contentView, final View.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, 0);
        final AlertDialog mAlertDialog = builder.setView(contentView).create();
        View.OnClickListener clicListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.tv_sure){
                    listener.onClick(v);
                }
            }
        };
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(clicListener);
        contentView.findViewById(R.id.tv_sure).setOnClickListener(clicListener);
        mAlertDialog.show();
        return mAlertDialog;
    }

    public static AlertDialog showEditPwdDialog(Activity activity,View view, final View.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogStyle2);
        final AlertDialog mAlertDialog = builder.setView(view).create();
        View.OnClickListener clicListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.tv_sure){
                    listener.onClick(v);
                }else{
                    mAlertDialog.dismiss();
                }
            }
        };
        view.findViewById(R.id.tv_cancel).setOnClickListener(clicListener);
        view.findViewById(R.id.tv_sure).setOnClickListener(clicListener);
        mAlertDialog.show();
        return mAlertDialog;
    }

    public static AlertDialog showAuthorDialog(final Activity activity,View view, final View.OnClickListener listener) {

        final AlertDialog dialog = new AlertDialog.Builder(activity,R.style.MyDialogStyle2).create();
        //自定义布局
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_confirm);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                WindowUtil.closeInputMethod(activity);
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }



    /**
     * 用户管理  踢出   黑名单操作  dialog
     *
     * @param activity
     * @param aler
     * @param ask
     * @param listener
     * @return
     */
    public static AlertDialog showMemberManagerDialog(Activity activity, String aler, String
            ask, final View.OnClickListener listener) {
        View view = View.inflate(activity, R.layout.dialog_member_manager, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogStyle2);
        final AlertDialog mAlertDialog = builder.setView(view).create();
        TextView alertContent = (TextView) view.findViewById(R.id.tv_alert_content);
        if (aler == null) {
            alertContent.setVisibility(View.GONE);
        } else {

            alertContent.setText(aler);
        }
        View cancel = view.findViewById(R.id.tv_cancel);
        TextView alertAsk = (TextView) view.findViewById(R.id.tv_alert_ask);
        alertAsk.setText(ask);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        View okview = view.findViewById(R.id.tv_confirm);
        okview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onClick(v);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
        return mAlertDialog;
    }

    public static AlertDialog showSeriesClassifyDialog(final Activity activity, boolean isShowTitle,
                                                       String titleText, String contentText, View
                                                               .OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        //自定义布局
        View view = activity.getLayoutInflater().inflate(R.layout
                .dialog_edit_seriesclassify_name, null);
        dialog.setView(view, 0, 0, 0, 0);
        //得到当前显示设备的宽度，单位是像素
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //得到这个dialog界面的参数对象
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //设置dialog的界面宽度
        params.width = width - (width / 6);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        //标题
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        //内容
        etContent = (EditText) view.findViewById(R.id.et_dialog_content);
        etContent.setText(contentText);
        etContent.setSelection(contentText.length());//将光标移至文字末尾

        //取消
        TextView leftButton = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        //确定
        TextView rightButton = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        if (isShowTitle)
            tvTitle.setText(titleText);
        else
            tvTitle.setVisibility(View.GONE);
        etContent.setText(contentText);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rightButton.setOnClickListener(listener);
        dialog.show();

        return dialog;
    }


    /**
     * 有两个按钮的弹窗
     * @param listeners 按钮监听 从左到右 按顺序来
     */
    public static NormalDialog showNormalDialogTwo(Context context, String title, String content, String leftText, String rightText, OnBtnClickL... listeners){
        NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(!TextUtils.isEmpty(title))
              .title(title)
              .content(content)
              .contentGravity(Gravity.CENTER)
              .btnText(leftText, rightText)
              .show();

        dialog.setOnBtnClickL(listeners);
        return dialog;
    }

    /**
     * 单按钮提示的弹窗  一般用于提示
     * @param listeners 按钮监听 从左到右 按顺序来
     */
    public static NormalDialog showNormalDialog(Context context, String title, String content, String leftText, OnBtnClickL... listeners){
        NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(!TextUtils.isEmpty(title))
              .title(title)
              .content(content)
              .contentGravity(Gravity.CENTER)
              .btnText(leftText)
              .show();

        dialog.setOnBtnClickL(listeners);
        return dialog;
    }

    /**
     * 仿苹果底部菜单栏弹窗
     */
    public static MyActionSheetDialog showActionDialog(Context context, String [] titles){
        MyActionSheetDialog dialog = new MyActionSheetDialog(context, titles, null);
        dialog.isTitleShow(false)
              .titleTextSize_SP(14.5f)
              .widthScale(1f)
              .cancelMarginBottom(0)
              .cornerRadius(0f)
              .dividerHeight(1)
              .itemTextColor(MyApplication.getInstance().getResources().getColor(R.color.black))
              .cancelText("取消")
              .show();

        return dialog;
    }

    public interface DialogClickListener{
        void clickLeftButton(View view);

        void clickRightButton(View view);
    }

}
