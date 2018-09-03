package com.gxtc.huchuan.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListBottomDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.luck.picture.lib.ui.PictureEditActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.rong.common.FileUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ImageMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/7.
 */

public class CommonImagePresenter implements CommonImageContract.Presenter{

    private CommonImageContract.View         mView;
    private WeakReference<BaseTitleActivity> mActivityReference;
    private String editPicturePath;
    private String shareSource;     //要分享的原图url

    public CommonImagePresenter(CommonImageContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }

    //分享图片给好友
    @Override
    public void shareImageToConversation(BaseTitleActivity activity, int requestCode, Uri uri) {
        mActivityReference = new WeakReference<>(activity);
        shareSource = uri.toString();
        if(UserManager.getInstance().isLogin(activity)){
            ConversationListActivity.startActivity(activity, ConversationActivity.REQUEST_SHARE_SRC_IMAGE, Constant.SELECT_TYPE_SHARE);
        }
    }

    //收藏图片
    @Override
    public void collectImage(Uri uri) {
        if(uri != null){
            String url = uri.toString();
            if(url.contains("http")){
                collect(url);
            }else{
                uploadImage(uri.getPath());
            }
        }
    }

    //保存图片到本地
    @Override
    public void saveImage(final Uri uri) {
        if(uri != null ){
            String path = uri.getScheme().startsWith("http") ? uri.toString() : uri.getPath();
            Subscription sub =
            Observable.just(path)
                      .subscribeOn(Schedulers.io())
                      .map(new Func1<String, File>() {
                          @Override
                          public File call(String s) {
                              return downloadFile(s);
                          }
                      })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<File>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            if(mView != null){
                                mView.showSaveResult(null);
                            }
                        }

                        @Override
                        public void onNext(File local) {
                            if(mView != null){
                                if(local != null){
                                    mView.showSaveResult(Uri.fromFile(local));
                                }else{
                                    mView.showSaveResult(null);
                                }
                            }
                        }
                    });

            RxTaskHelper.getInstance().addTask(this, sub);
        }
    }

    //编辑图片
    @Override
    public void editImage(BaseTitleActivity activity, int requestCode, Uri uri) {
        mActivityReference = new WeakReference<>(activity);
        Subscription sub =
        Observable.just(uri.toString())
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, File>() {
                    @Override
                    public File call(String s) {
                        return downloadFile(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        if(mView != null && mActivityReference.get() != null && file != null){
                            PictureEditActivity.startActivit(mActivityReference.get(), file.toString());
                        }
                    }
                });

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PictureEditActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            editPicturePath = data.getStringExtra("data");
            showEditPictureDialog();
        }

        //分享编辑的图片
        if(requestCode == ConversationActivity.REQUEST_SHARE_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareImage(targetId, type, Uri.fromFile(new File(editPicturePath)),bean.liuyan);
        }

        //分享图片
        if(requestCode == ConversationActivity.REQUEST_SHARE_SRC_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            shareConversation(bean);
        }
    }

    private File downloadFile(String url){
        String filePath = FileStorage.getImgCacheFile().getPath() + "/" + FileStorage.getImageTempName();
        try {
            File file;
            //融云的图片是在本地的
            if(url.contains("file")){
                url = url.replace("file://", "");
                file = new File(url);
            }else{
                file = Glide.with(MyApplication.getInstance())
                                 .asFile()
                                 .load(url)
                                 .submit()
                                 .get();
            }
            if(file != null){
                if(FileUtil.copyFile(file.getAbsolutePath(), filePath)){
                    scanPhoto(filePath);
                    return new File(filePath);

                    //拷贝文件失败
                }else{
                    return null;
                }

            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 让Gallery上能马上看到该图片
     */
    private static void scanPhoto(String imgFileName) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        MyApplication.getInstance().sendBroadcast(mediaScanIntent);
    }


    private ListBottomDialog mBottomDialog;
    private void showEditPictureDialog() {
        final String[] items       = new String[]{"发送好友", "收藏", "保存图片"};
        List<String> dialogItems = new ArrayList<>();
        Collections.addAll(dialogItems, items);

        mBottomDialog = new ListBottomDialog();
        mBottomDialog.setDatas(items)
                     .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                             mBottomDialog.dismiss();
                             switch (items[position]){
                                 case "发送好友":
                                     if(UserManager.getInstance().isLogin(mActivityReference.get())){
                                         ConversationListActivity.startActivity(mActivityReference.get(), ConversationActivity.REQUEST_SHARE_IMAGE, Constant.SELECT_TYPE_SHARE);
                                     }
                                     break;

                                 case "收藏":
                                     if(UserManager.getInstance().isLogin(mActivityReference.get()) && !TextUtils.isEmpty(editPicturePath)){
                                         collectImage(Uri.fromFile(new File(editPicturePath)));
                                     }
                                     break;

                                 case "保存图片":
                                     ToastUtil.showLong(mActivityReference.get(), "图片已保存至:  " + editPicturePath);
                                     break;
                             }
                         }
                     });
        mBottomDialog.show(mActivityReference.get().getSupportFragmentManager(), ListBottomDialog.class.getName());
    }


    private void shareImage(final String targetId, final Conversation.ConversationType type,
                            final Uri uri, final String liuyan) {
        if(RongIM.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED){
            ImageMessage                imgMsg = ImageMessage.obtain(uri, uri, true);
            io.rong.imlib.model.Message msg    = ImMessageUtils.obtain(targetId, type, imgMsg);
            RongIM.getInstance().sendImageMessage(msg, "[图片]", "[图片]", new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(io.rong.imlib.model.Message message) {

                }

                @Override
                public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                    if(mView != null){
                        mView.showLoadFinish();
                        ToastUtil.showShort(mActivityReference.get(), "发送失败 : " + errorCode);
                    }
                }

                @Override
                public void onSuccess(io.rong.imlib.model.Message message) {
                    if(mView != null){
                        mView.showLoadFinish();
                        //删除临时图片文件
                        Observable.just(uri.getPath())
                                  .subscribeOn(Schedulers.io())
                                  .subscribe(new Action1<String>() {
                                      @Override
                                      public void call(String s) {
                                          if(FileUtil.deleteFile(editPicturePath)){
                                              //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！
                                              Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                              Uri    uri    = Uri.fromFile(new File(editPicturePath));
                                              intent.setData(uri);
                                              mActivityReference.get().sendBroadcast(intent);
                                          }
                                      }
                                  });
                        if(!TextUtils.isEmpty(liuyan)){
                            RongIMTextUtil.INSTANCE.relayMessage(liuyan,targetId,type);
                        }
                        ToastUtil.showShort(mActivityReference.get(), "发送成功");
                    }
                }

                @Override
                public void onProgress(io.rong.imlib.model.Message message, int i) {

                }
            });
        }
    }

    private void shareConversation(final EventSelectFriendBean bean){
        mView.showLoad();
        Subscription sub =
        Observable.just(shareSource)
                  .subscribeOn(Schedulers.io())
                  .map(new Func1<String, File>() {
                      @Override
                      public File call(String s) {
                          return downloadFile(s);
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<File>() {
                        @Override
                        public void call(File file) {
                            if(file != null){
                                shareImage(bean.targetId, bean.mType, Uri.fromFile(file),
                                        bean.liuyan);
                            }
                        }
                    });
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    //收藏编辑的图片的时候需要先上传到服务器上面
    private void uploadImage(String path){
        mView.showLoad();

        final File source = new File(path);
        Subscription sub =
        Luban.get(MyApplication.getInstance())
             .load(new File(path))                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
             .asObservable()
             .subscribeOn(Schedulers.io())
             .map(new Func1<File, File>() {
                 @Override
                 public File call(File compressFile) {
                     return FileUtils.getFileSize(source) > Constant.COMPRESS_VALUE ? compressFile : source;
                 }
             })
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(new Subscriber<File>() {
                 @Override
                 public void onCompleted() {}

                 @Override
                 public void onError(Throwable e) {
                     if(mView != null){
                         mView.showLoadFinish();
                         mView.showError("上传图片失败");
                     }
                 }

                 @Override
                 public void onNext(File uploadFile) {
                     LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
                         @Override
                         public void onUploadSuccess(UploadResult result) {
                             if(mView != null){
                                 mView.showLoadFinish();
                                 collect(result.getUrl());
                             }
                         }

                         @Override
                         public void onUploadFailed(String errorCode, String msg) {
                             if(mView != null){
                                 mView.showLoadFinish();
                                 mView.showError(msg);
                             }
                         }
                     }, null, uploadFile);
                 }
             });
    }


    private void collect(String url){
        mView.showLoad();

        String                       bizType = "";
        String                       content = "";
        String                       token   = UserManager.getInstance().getToken();
        final HashMap<String,String> map     = new HashMap<>();

        String name = UserManager.getInstance().getUserName();
        String userPic = UserManager.getInstance().getHeadPic();
        if(!TextUtils.isEmpty(name)){
            map.put("userName", name);
        }
        if(!TextUtils.isEmpty(userPic)){
            map.put("userPic", userPic);
        }
        if(!TextUtils.isEmpty(token)){
            map.put("token", token);
        }
        bizType = "7";
        content = url;
        map.put("content",content);
        map.put("bizType",bizType);

        Subscription sub =
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        try {
                            return Glide.with(MyApplication.getInstance())
                                        .asBitmap()
                                        .load(s)
                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .filter(new Func1<Bitmap, Boolean>() {
                    @Override
                    public Boolean call(Bitmap bitmap) {
                        return bitmap != null;
                    }
                })
                .flatMap(new Func1<Bitmap, Observable<ApiResponseBean<Object>>>() {
                    @Override
                    public Observable<ApiResponseBean<Object>> call(Bitmap bitmap) {
                        map.put("title",bitmap.getWidth() + "*" + bitmap.getHeight());
                        return AllApi.getInstance().saveCollection(map);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(mView != null){
                            mView.showLoadFinish();
                            mView.showCollectResult("");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mView != null){
                            mView.showLoadFinish();
                            mView.showCollectResult(message);
                        }
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

}
