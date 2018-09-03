package com.gxtc.huchuan.ui.mine.scanCode;

import android.Manifest;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;

import org.greenrobot.eventbus.Subscribe;

/**
 * 扫一扫
 */
@Deprecated
public class ScanCodeActivity extends BaseTitleActivity implements View.OnClickListener {

    //@BindView(R.id.zbarview) QRCodeView zbarView;

    private QrcodeHandler   codeHandler;
    private Vibrator        vibrator;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("扫一扫");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("相册",this);

    }

    @Override
    public void initListener() {
        /*callBack = new CodeCallBack();
        zbarView.setDelegate(callBack);*/
    }
    //private QRCodeView.Delegate callBack;

    @Override
    public void initData() {
        performRequestPermissions(getString(R.string.pre_scan_msg),
                new String[]{Manifest.permission.VIBRATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                202, new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        //ZXingLibrary.initDisplayOpinion(ScanCodeActivity.this);
                        //zbarView.startSpotAndShowRect();
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(ScanCodeActivity.this, false, null, "应用需要读取相机和存储权限,是否需要开启",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(ScanCodeActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
        codeHandler = new QrcodeHandler(this);
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                selectorImg();
                break;
        }
    }

    private void selectorImg() {
        performRequestPermissions(getString(R.string.pre_scan_msg),
                new String[]{Manifest.permission.VIBRATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                202, new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options =
                                new FunctionOptions.Builder()
                                        .setType(FunctionConfig.TYPE_IMAGE)
                                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                                        .setMaxSelectNum(1)
                                        .setImageSpanCount(3)
                                        .setEnableQualityCompress(false) //是否启质量压缩
                                        .setEnablePixelCompress(false) //是否启用像素压缩
                                        .setEnablePreview(false) // 是否打开预览选项
                                        .setShowCamera(false)
                                        .setPreviewVideo(false)
                                        .create();
                        //PictureConfig.getInstance().init(options).openPhoto(ScanCodeActivity.this, resultCallback);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(ScanCodeActivity.this, false, null, "应用需要读取相机和存储权限,是否需要开启",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(ScanCodeActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    @Subscribe
    public void onEvent(EventLoadBean bean){
        getBaseLoadingView().show(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //zbarView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //zbarView.stopSpotAndHiddenRect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mAlertDialog = null;
        codeHandler.destroy();
        //zbarView.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    /*private class CodeCallBack implements QRCodeView.Delegate{

        @Override
        public void onScanQRCodeSuccess(String result) {
            if(codeHandler != null){
                codeHandler.resolvingCode(result,"");
            }
            //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
            //第二个参数为重复次数，-1为不重复，0为一直震动
            if(vibrator != null){
                vibrator.vibrate(new long[]{100, 10, 100, 600}, -1);
            }
        }

        @Override
        public void onScanQRCodeOpenCameraError() {
            ToastUtil.showShort(ScanCodeActivity.this, "扫码失败");
        }
    }*/

    /*private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {}

        @Override
        public void onSelectSuccess(LocalMedia media) {
            Subscription sub =
                    Observable.just(media)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<LocalMedia, String>() {
                        @Override
                        public String call(LocalMedia localMedia) {
                            return QRCodeDecoder.syncDecodeQRCode(localMedia.getPath());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String result) {
                            if(codeHandler != null){
                                codeHandler.resolvingCode(result,"");
                            }
                            if(vibrator != null){
                                vibrator.vibrate(new long[]{100, 10, 100, 600}, -1);
                            }
                        }
                    });
            RxTaskHelper.getInstance().addTask(ScanCodeActivity.this,sub);
        }
    };*/

}
