package com.gxtc.huchuan.qrcode;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Toast;

import com.google.zxing.Result;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.qrcode.camera.CameraManager;
import com.gxtc.huchuan.qrcode.decode.CaptureActivityHandler;
import com.gxtc.huchuan.qrcode.decode.DecodeManager;
import com.gxtc.huchuan.qrcode.decode.InactivityTimer;
import com.gxtc.huchuan.qrcode.utils.QRCodeDecoder;
import com.gxtc.huchuan.qrcode.view.QrCodeFinderView;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 二维码扫描界面。
 */
public class QrCodeActivity extends BaseTitleActivity implements Callback, OnClickListener {

    public static final String INTENT_OUT_STRING_SCAN_RESULT = "scan_result";

    private static final String INTENT_IN_INT_SUPPORT_TYPE = "support_type";
    private static final int REQUEST_PERMISSIONS = 1;
    private CaptureActivityHandler mCaptureActivityHandler;
    private boolean mHasSurface;
    private InactivityTimer mInactivityTimer;
    private QrCodeFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;
    private View mLlFlashLight;
    private ViewStub mSurfaceViewStub;
    private QrcodeHandler codeHandler;
    private Vibrator vibrator;
    private DecodeManager mDecodeManager = new DecodeManager();

    Timer timer;

    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
    }


    @Override
    public void initView() {
        getBaseHeadView().showTitle("扫一扫");
        getBaseHeadView().showHeadRightButton("相册", this);

        mQrCodeFinderView = (QrCodeFinderView) findViewById(R.id.qr_code_view_finder);
        mLlFlashLight = findViewById(R.id.qr_code_ll_flash_light);
        mSurfaceViewStub = (ViewStub) findViewById(R.id.qr_code_view_stub);
        mHasSurface = false;
    }


    @Override
    public void initData() {
        codeHandler = new QrcodeHandler(this);
        timer = new Timer();
        CameraManager.init();
        /*
         * Android 5.1.1
         * 用户拒绝权限之后会抛出异常
         */
        CameraManager.get().OnCancelListneter(new CameraManager.OnCancelListneter() {
            @Override
            public void onCancel() {
                showDialog();
            }
        });
        mInactivityTimer = new InactivityTimer(this);

//        String[] permissions = new String[]{"android.permission.CAMERA"};
//        performRequestPermissions(getString(R.string.pre_scan_notice_msg), permissions, 110, new PermissionsResultListener() {
//            @Override
//            public void onPermissionGranted() {
//                initCamera();
//            }
//
//            @Override
//            public void onPermissionDenied() {
//                mAlertDialog = DialogUtil.showDeportDialog(QrCodeActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (v.getId() == R.id.tv_dialog_confirm) {
//                                    JumpPermissionManagement.GoToSetting(QrCodeActivity.this);
//                                }
//                                mAlertDialog.dismiss();
//                                finish();
//                            }
//                        });
//            }
//        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS);
        }

        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headRightButton:
                gotoImgae();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(new Intent(this, QrCodeActivity.class));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                QrCodeActivity.this.finish();
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }
        if (mCaptureActivityHandler != null) {
            try {
                mCaptureActivityHandler.quitSynchronously();
                mCaptureActivityHandler = null;
                mHasSurface = false;
                if (null != mSurfaceView) {
                    mSurfaceView.getHolder().removeCallback(this);
                }
                CameraManager.get().closeDriver();
            } catch (Exception e) {
                // 关闭摄像头失败的情况下,最好退出该Activity,否则下次初始化的时候会显示摄像头已占用.
                finish();
            }
        }
        if (codeHandler != null) {
            codeHandler.destroy();
            codeHandler = null;
        }
        RxTaskHelper.getInstance().cancelTask(this);
        super.onDestroy();
    }


    private void initCamera() {
        if (null == mSurfaceView) {
            mSurfaceViewStub.setLayoutResource(R.layout.layout_surface_view);
            mSurfaceView = (SurfaceView) mSurfaceViewStub.inflate();
        }
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
    }


    private void showPermissionDeniedDialog() {
        findViewById(R.id.qr_code_view_background).setVisibility(View.VISIBLE);
        mQrCodeFinderView.setVisibility(View.GONE);
        mDecodeManager.showPermissionDeniedDialog(this);
    }


    /**
     * Handler scan result
     *
     * @param result
     */
    public void handleDecode(Result result) {
        mInactivityTimer.onActivity();
        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this,
                    new DecodeManager.OnRefreshCameraListener() {
                        @Override
                        public void refresh() {
                            restartPreview();
                        }
                    });
        } else {
            String resultString = result.getText();
            handleResult(resultString);
        }
    }


    private void initCamera(final SurfaceHolder surfaceHolder) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!CameraManager.get().openDriver(surfaceHolder)) {
                            showPermissionDeniedDialog();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        } catch (RuntimeException re) {
            re.printStackTrace();
            finish();
            showPermissionDeniedDialog();
            return;
        }
        mQrCodeFinderView.setVisibility(View.VISIBLE);
        mLlFlashLight.setVisibility(View.VISIBLE);
        findViewById(R.id.qr_code_view_background).setVisibility(View.GONE);
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this);
        }
    }


    private void restartPreview() {
        if (null != mCaptureActivityHandler) {
            try {
                mCaptureActivityHandler.restartPreviewAndDecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return mCaptureActivityHandler;
    }

    private void gotoImgae() {
        FunctionOptions options =
                new FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_IMAGE)
                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                        .setMaxSelectNum(1)
                        .setImageSpanCount(3)
                        .setEnableQualityCompress(false) //是否启质量压缩
                        .setEnablePixelCompress(false)   //是否启用像素压缩
                        .setEnablePreview(false)         //是否打开预览选项
                        .setShowCamera(false)
                        .setPreviewVideo(false)
                        .create();
        PictureConfig.getInstance().init(options).openPhoto(this, new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> resultList) {

            }

            @Override
            public void onSelectSuccess(final LocalMedia media) {
                resolvQRCode(media.getPath());
            }
        });
    }

    //识别图片二维码
    private void resolvQRCode(String path) {
        getBaseLoadingView().showLoading();
        Subscription sub =
                Observable.just(path)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String url) {
                                return QRCodeDecoder.syncDecodeQRCode(url);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String url) {
                                if (codeHandler != null) {
                                    getBaseLoadingView().hideLoading();
                                    if (TextUtils.isEmpty(url)) {
                                        ToastUtil.showShort(QrCodeActivity.this, "无法识别二维码，换个试试～");
                                    } else {
                                        boolean b = codeHandler.resolvingCode(url, "");
                                        if (b) {
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    QrCodeActivity.this.finish();
                                                }
                                            }, 1000);

                                        }

                                    }
                                }
                            }
                        });

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ToastUtil.showShort(this, "执行了");
        if (grantResults.length != 0) {
            int cameraPermission = grantResults[0];
            if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showShort(this, "有权限");
                initCamera();
            } else {
                ToastUtil.showShort(this, "没权限");
                showDialog();
            }
        }
    }

    private void showDialog() {
        mAlertDialog = DialogUtil.showDeportDialog(QrCodeActivity.this, false, null, getString(R.string.pre_scan_camera_msg),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            JumpPermissionManagement.GoToSetting(QrCodeActivity.this);
                        }
                        mAlertDialog.dismiss();
                        finish();
                    }
                });
    }

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            ToastUtil.showShort(this, "无法识别二维码，换个试试～");
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this,
                    new DecodeManager.OnRefreshCameraListener() {
                        @Override
                        public void refresh() {
                            restartPreview();
                        }
                    });
        } else {
            if (codeHandler != null) {
                boolean b = codeHandler.resolvingCode(resultString, "");
                if (b) {
                    if (b) {
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                QrCodeActivity.this.finish();
                            }
                        }, 1000);

                    }
                }

            }
            //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
            //第二个参数为重复次数，-1为不重复，0为一直震动
            if (vibrator != null) {
                vibrator.vibrate(new long[]{100, 10, 100, 600}, -1);
            }
        }
    }
}