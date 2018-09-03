package com.gxtc.huchuan.ui.circle.dynamic;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.IssueDynamicBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2018/1/3.
 */
public class UpImageService extends Service {
    public static final String ACTION_UPLOAD_IMG = "ACTION_UPLOAD_IMG";
    public static final String EXTRA_IMG_PATH    = "EXTRA_IMG_PATH";
    public static final String EXTRA_TYPE   = "EXTRA_TYPE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_IMG.equals(action)) {
                ArrayList<IssueDynamicBean> paths = (ArrayList<IssueDynamicBean>) intent.getSerializableExtra(EXTRA_IMG_PATH);
                handleUploadImg(paths);
//                startForeground(1, getNotification("Uploading...", 0,-1));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleUploadImg(final ArrayList<IssueDynamicBean> paths) {
        List<File> list = new ArrayList<File>();
        for (IssueDynamicBean bean : paths) {
            list.add(bean.getFile());
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());

        LoadHelper.uploadFiles(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                ArrayList<String> data = new ArrayList<>();
                data.addAll(result.getUrls());
                EventBusUtil.post(data);
                stopSelf();     //上传完毕结束服务
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ToastUtil.showShort(getBaseContext(), msg);
            }
        }, new UIProgressListener() {
            @Override
            public void onUIProgress(long currentBytes, long contentLength, boolean done) {

            }
        }, list.toArray(new File[]{}));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, long currentProgress,long max) {
        Intent                     intent  = new Intent(this, MainActivity.class);
        PendingIntent              pi      = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        int progress = (int) ((currentProgress) * 100 / max);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100,  progress, false);
        }
        return builder.build();
    }

}
