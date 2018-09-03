package com.gxtc.huchuan.http;

import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.UpyunBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.SystemUtil;
import com.upyun.library.common.Params;
import com.upyun.library.common.ResumeUploader;
import com.upyun.library.common.UpConfig;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import cn.edu.zafu.coreprogress.progress.ProgressRequestBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/5/12.
 * 上传文件工具类
 */
public class LoadHelper {

    public static final String TYPE_IMAGE = "image/png";
    public static final String TYPE_VIDEO_MP4 = "video/mp4";
    public static final String TYPE_OTHER = "other";

    public static final String UP_TYPE_IMAGE = "/image";
    public static final String UP_TYPE_VIDEO = "/video";
    public static final String UP_TYPE_OTHER = "/other";

    public static final String bucketName = "xmzjvip";
    public static final String operatorName = "xmzj";
    public static final String password = "caisen123";

    //这个不能加https
    private static final String notifyUrl = "http://app.xinmei6.com/publish/file/upYunNotify.do";


    /**
     * 使用又拍云上传单文件
     *
     * @param type {@link #UP_TYPE_IMAGE}
     */
    public static void uploadFile(String type, final UploadCallback callback, final UIProgressListener listener, File file) {
        String userCode = UserManager.getInstance().getUserCode();
        final UploadResult uploadResult = new UploadResult();
        final String name = "/{year}/{mon}/{day}/android_{random32}{.suffix}";
        Map<String, Object> map = new HashMap<>();
        map.put(Params.BUCKET, bucketName);
        map.put(Params.NOTIFY_URL, notifyUrl);
        if (TextUtils.isEmpty(userCode)) {
            map.put(Params.SAVE_KEY, name);
        } else {
            map.put(Params.SAVE_KEY, "/{year}/{mon}/{day}/" + userCode + "upload_android_{random32}{.suffix}");
        }

        UploadEngine.getInstance().formUpload(file, map, operatorName, UpYunUtils.md5(password),
                new UpCompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result) {
                        LogUtil.i("onComplete  result :  " + result);
                        try {
                            double code = (double) GsonUtil.getJsonValue(result, "code");
                            String msg = (String) GsonUtil.getJsonValue(result, "message");
                            if (isSuccess) {
                                //上传成功
                                if (code == 200) {
                                    String baseUrl = (String) GsonUtil.getJsonValue(result, "url");
                                    String url = "https://" + bucketName + ".b0.upaiyun.com" + baseUrl;
                                    LogUtil.i("url :  " + url);

                                    uploadResult.setUrl(url);
                                    if (callback != null) {
                                        callback.onUploadSuccess(uploadResult);
                                    }

                                    //上传出错
                                } else {
                                    if (callback != null) {
                                        callback.onUploadFailed(code + "", msg);
                                    }
                                }

                                //上传出错
                            } else {
                                if (callback != null) {
                                    callback.onUploadFailed(code + "", msg);
                                }
                            }

                            //上传出错
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (callback != null) {
                                callback.onUploadFailed("404", "上传图片失败");
                            }
                        }

                    }
                },
                new UpProgressListener() {
                    @Override
                    public void onRequestProgress(long bytesWrite, long contentLength) {
                        if (listener != null) {
                            listener.onUIProgress(bytesWrite, contentLength, bytesWrite == contentLength, 1);
                        }
                    }
                });
    }


    /**
     * 使用又拍云上传多文件
     *
     * @param type {@link #UP_TYPE_IMAGE}
     */
    public static void uploadFiles(String type, final UploadCallback callback, final UIProgressListener listener, final File... files) {
        String userCode = UserManager.getInstance().getUserCode();
        final UploadResult uploadResult = new UploadResult(files.length);
        for (int i = 0; i < files.length; i++) {
            final String name = "/{year}/{mon}/{day}/android_{random32}{.suffix}";
            final File file = files[i];
            Map<String, Object> map = new HashMap<>();
            map.put(Params.BUCKET, bucketName);
            map.put(Params.NOTIFY_URL, notifyUrl);
            if (TextUtils.isEmpty(userCode)) {
                map.put(Params.SAVE_KEY, name);
            } else {
                map.put(Params.SAVE_KEY, "/{year}/{mon}/{day}/" + userCode + "upload_android_{random32}{.suffix}");
            }

            final int index = i;
            UploadEngine.getInstance().formUpload(file, map, operatorName, UpYunUtils.md5(password),
                    new UpCompleteListener() {
                        @Override
                        public void onComplete(boolean isSuccess, String result) {
                            LogUtil.i("onComplete  result :  " + result);
                            try {
                                double code = (double) GsonUtil.getJsonValue(result, "code");
                                String msg = (String) GsonUtil.getJsonValue(result, "message");
                                if (isSuccess) {
                                    //上传成功
                                    if (code == 200) {
                                        String baseUrl = (String) GsonUtil.getJsonValue(result, "url");
                                        String url = "https://" + bucketName + ".b0.upaiyun.com" + baseUrl;
                                        LogUtil.i("url :  " + url);

                                        uploadResult.addResult(index, url);
                                        if (uploadResult.isFinish()) {
                                            if (callback != null) {
                                                callback.onUploadSuccess(uploadResult);
                                            }
                                        }

                                        //上传出错
                                    } else {
                                        if (callback != null) {
                                            callback.onUploadFailed(code + "", msg);
                                        }
                                    }

                                    //上传出错
                                } else {
                                    if (callback != null) {
                                        callback.onUploadFailed(code + "", msg);
                                    }
                                }

                                //上传出错
                            } catch (Exception e) {
                                if (callback != null) {
                                    callback.onUploadFailed("404", "上传图片失败");
                                }
                            }
                        }
                    },
                    new UpProgressListener() {
                        @Override
                        public void onRequestProgress(long bytesWrite, long contentLength) {
                            if (listener != null) {
                                listener.onUIProgress(bytesWrite, contentLength, bytesWrite == contentLength, uploadResult.getCurrIndex());
                            }
                        }
                    });
        }
    }


    /**
     * 使用又拍云上传视频
     * 特别的上传结果保存的 urls 里面 第二位url是视频的封面地址
     */
    public static void UpyunUploadVideo(File file, final UploadCallback callback, final UIProgressListener progressListener) {
        final int[] frameSize = new int[2];   //视频第一帧的尺寸
        final File videoFile = file;

        Observable.just(file)
                .subscribeOn(Schedulers.io())
                .map(new Func1<File, Bitmap>() {          //先将视频的第一帧提取出来
                    @Override
                    public Bitmap call(File file) {
                        Bitmap bitmap = ImageUtils.getVideoFirstFrame(file.getPath());
                        frameSize[0] = bitmap.getWidth();
                        frameSize[1] = bitmap.getHeight();
                        return bitmap;
                    }
                })
                .map(new Func1<Bitmap, File>() {          //将图片保存到本地
                    @Override
                    public File call(Bitmap bitmap) {
                        if (bitmap == null) {
                            if (callback != null) {
                                callback.onUploadFailed("", "视频图片提取失败");
                            }
                            return null;
                        }
                        File frameFile = new File(FileStorage.getImgCacheFile().getPath() + "/" + FileStorage.getImageTempName());//将要保存图片的路径
                        BufferedOutputStream bos = null;
                        try {
                            bos = new BufferedOutputStream(new FileOutputStream(frameFile));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                            bitmap.recycle();
                            bitmap = null;
                        } catch (IOException e) {
                            if (callback != null) {
                                callback.onUploadFailed("", "发生未知错误, 上传失败");
                            }

                            if (bos != null) {
                                try {
                                    bos.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            e.printStackTrace();

                        } finally {
                            if (bitmap != null && !bitmap.isRecycled()) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                        }
                        LogUtil.i("提取后的视频第一帧图片地址:  " + frameFile.getPath());
                        return frameFile;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File frameFile) {

                        //这里上传到又拍云服务器， 记得图片地址要加宽高 ?width*height
                        uploadVideo(new UploadCallback() {
                            @Override
                            public void onUploadSuccess(UploadResult result) {

                                if (result.getUrls().size() >= 2) {
                                    String picUrl = result.getUrls().get(1);
                                    picUrl = picUrl + "?" + frameSize[0] + "*" + frameSize[1];
                                    result.getUrls().set(1, picUrl);
                                }

                                if (callback != null) {
                                    callback.onUploadSuccess(result);
                                }
                            }

                            @Override
                            public void onUploadFailed(String errorCode, String msg) {
                                if (callback != null) {
                                    callback.onUploadFailed(errorCode, msg);
                                }
                            }
                        }, progressListener, videoFile, frameFile);
                    }
                });

    }


    /**
     * 上传视频跟封面专用
     */
    public static void uploadVideo(final UploadCallback callback, final UIProgressListener listener, final File... files) {
        String userCode = UserManager.getInstance().getUserCode();
        String videoPath = "/{year}/{mon}/{day}/" + userCode + "android_" + System.currentTimeMillis() + "{.suffix}";

        final UploadResult uploadResult = new UploadResult(files.length);

        for (int i = 0; i < files.length; i++) {
            final String name = videoPath;
            final File file = files[i];
            Map<String, Object> map = new HashMap<>();
            map.put(Params.BUCKET, bucketName);
            map.put(Params.NOTIFY_URL, notifyUrl);
            map.put(Params.SAVE_KEY, name);

            final int index = i;
            UploadEngine.getInstance().formUpload(file, map, operatorName, UpYunUtils.md5(password),
                    new UpCompleteListener() {
                        @Override
                        public void onComplete(boolean isSuccess, String result) {
                            LogUtil.i("onComplete  result :  " + result);
                            try {
                                double code = (double) GsonUtil.getJsonValue(result, "code");
                                String msg = (String) GsonUtil.getJsonValue(result, "message");
                                if (isSuccess) {
                                    //上传成功
                                    if (code == 200) {
                                        String baseUrl = (String) GsonUtil.getJsonValue(result, "url");
                                        String url = "https://" + bucketName + ".b0.upaiyun.com" + baseUrl;
                                        LogUtil.i("url :  " + url);

                                        uploadResult.addResult(index, url);
                                        if (uploadResult.isFinish()) {
                                            if (callback != null) {
                                                callback.onUploadSuccess(uploadResult);
                                            }
                                        }

                                        //上传出错
                                    } else {
                                        if (callback != null) {
                                            callback.onUploadFailed(code + "", msg);
                                        }
                                    }

                                    //上传出错
                                } else {
                                    if (callback != null) {
                                        callback.onUploadFailed(code + "", msg);
                                    }
                                }

                                //上传出错
                            } catch (Exception e) {
                                if (callback != null) {
                                    callback.onUploadFailed("404", "上传图片失败");
                                }
                            }
                        }
                    },
                    new UpProgressListener() {
                        @Override
                        public void onRequestProgress(long bytesWrite, long contentLength) {
                            if (listener != null) {
                                listener.onUIProgress(bytesWrite, contentLength, bytesWrite == contentLength, uploadResult.getCurrIndex());
                            }
                        }
                    });
        }
    }


    /**
     * 上传IM文件
     *
     * @param map      请求参数
     * @param mediType 文件类型
     * @param listener 进度监听器
     * @param files    待上传文件路径
     */
    public static Observable<ApiResponseBean<List<String>>> loadImFile(Map<String, String> map, String mediType, UIProgressListener listener, File... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                builder.addFormDataPart(key, map.get(key));
            }
        }
        for (File file : files) {
            builder.addFormDataPart("file", file.getName(),
                    RequestBody.create(MediaType.parse(mediType), file));
        }
        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(builder.build(), listener);
        return LiveApi.getInstance().uploadIMFile(progressRequestBody).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 混合多类型文件上传
     */
    public static Observable<ApiResponseBean<List<String>>> loadMultiTypeFile(Map<String, String> map, List<String> types, List<File> files, UIProgressListener listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                builder.addFormDataPart(key, map.get(key));
            }
        }

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(types.get(i)), file));
        }

        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(builder.build(), listener);
        return LiveApi.getInstance()
                .uploadIMFile(progressRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 多文件上传
     *
     * @param map      请求参数
     * @param mediType 文件类型
     * @param listener 进度监听器
     * @param files    待上传文件路径
     */
    @Deprecated
    public static Observable<ApiResponseBean<List<String>>> loadFiles(Map<String, String> map, String mediType, UIProgressListener listener, File... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                builder.addFormDataPart(key, map.get(key));
            }
        }
        for (File file : files) {
            builder.addFormDataPart("file", file.getName(),
                    RequestBody.create(MediaType.parse(mediType), file));
        }
        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(builder.build(), listener);
        return AllApi.getInstance().uploadFiles(progressRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传其它文件
     *
     * @param map      　其它参数
     * @param mediType 　　文件类型
     * @param file     　　文件
     * @param listener 进度监听
     */
    @Deprecated
    public static Observable<ApiResponseBean<UploadFileBean>> loadFile(Map<String, String> map, String mediType, File file, UIProgressListener listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                builder.addFormDataPart(key, map.get(key));
            }
        }

        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(mediType), file));

        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(builder.build(), listener);
        return MineApi.getInstance()
                .uploadFile(progressRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public interface UploadCallback {

        void onUploadSuccess(UploadResult result);

        void onUploadFailed(String errorCode, String msg);
    }


}

