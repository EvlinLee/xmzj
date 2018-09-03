package com.gxtc.huchuan.im.extension;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by zzg on 2017/11/15 .
 * 文件自定义插件
 */

public class CustomFilePlugin implements IPluginModule  {
    Fragment fragment;
    public int mRequestCode;
    public BaseTitleActivity.PermissionsResultListener mListener;
    private AlertDialog mAlertDialog;

    //设置插件 Plugin 图标
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.plugin_file_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return "文件";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        this.fragment = fragment;
        openFileManager(fragment);
    }

    /**
     * 打开文件管理器
     */
    private void openFileManager(final Fragment fragment) {
        final String id = fragment.getActivity().getIntent().getData().getQueryParameter("targetId");
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(fragment.getString(R.string.txt_card_permission), pers, 10010,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            intent.putExtra(Constant.INTENT_DATA, id);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            fragment.getActivity().startActivityForResult(intent, Constant.requestCode.UPLOAD_FILE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showShort(MyApplication.getInstance(), "请先下载一个文件管理器");
                        }

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(fragment.getActivity(), false, null, fragment.getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(fragment.getActivity());
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    protected void performRequestPermissions(String desc, String[] permissions, int requestCode, BaseTitleActivity.PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {// 已经申请权限
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
    }

    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return true 需要申请权限,false 已申请权限
     */
    private boolean checkEachSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MyApplication.getInstance().getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void requestEachPermissions(String desc, String[] permissions, int requestCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(desc, permissions, requestCode);
        } else {
            ActivityCompat.requestPermissions(fragment.getActivity(), permissions, requestCode);
        }
    }

    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 弹出声明的 Dialog
     *
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private void showRationaleDialog(String desc, final String[] permissions, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle("提醒")
               .setMessage(desc)
               .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       ActivityCompat.requestPermissions(fragment.getActivity(), permissions, requestCode);
                   }
               })
               .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               })
               .setCancelable(false)
               .show();
    }


}
