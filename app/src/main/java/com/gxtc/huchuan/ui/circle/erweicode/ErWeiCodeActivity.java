package com.gxtc.huchuan.ui.circle.erweicode;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleErweimaBean;
import com.gxtc.huchuan.bean.SentImageBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.MyDynamicActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.SystemUtil;
import com.gxtc.huchuan.utils.UMShareUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.message.ImageMessage;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/6/7.
 * 统一二维码页面
 */

public class ErWeiCodeActivity extends BaseTitleActivity implements View.OnClickListener {

    public static final int TYPE_CLASSROOM = 1;             //课堂
    public static final int TYPE_SERIES = 2;                //系列课主页
    public static final int TYPE_CLASSROOM_PAGE = 3;        //课堂主页
    public static final int TYPE_MALL_GOODS = 4;            //商城商品
    public static final int TYPE_DEAL_GOODS = 5;            //交易商品
    public static final int TYPE_YINGYONGBAO = 6;           //应用宝
    public static final int TYPE_CIRCLE = 7;                //圈子
    public static final int TYPE_PERSONINFO = 8;            //个人资料页面
    public static final int TYPE_CUSTOM_GROUP = 9;          //自己创建的群聊
    public static final int TYPE_CARD = 110;

    @BindView(R.id.iv_erwei_code) ImageView ivErweiCode;
    @BindView(R.id.tv_label)      TextView  tvLabel;

    CircleErweimaBean bean;

    public  final String SAVE_REAL_PATH = FileStorage.getImgCacheFile().getPath();//保存的确切位置

    private int id;
    private int type;
    private String qrCodeUrl;
    private android.support.v7.app.AlertDialog mAlertDialog;
    public  String SHARE_REAL_PATH ;
    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends  Handler{

        private WeakReference<ErWeiCodeActivity> mWeakReference;
        private ErWeiCodeActivity mErWeiCodeActivity;

        private MyHandler(ErWeiCodeActivity codeActivity){
            mWeakReference = new WeakReference<>(codeActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mWeakReference.get() != null){
                mErWeiCodeActivity = mWeakReference.get();
                if (msg.obj != null) {
                    SentImageBean mSentImageBean = (SentImageBean) msg.obj;
                    Bitmap bitmap = mSentImageBean.getBitmap();
                    Intent intent = mSentImageBean.getIntent();
                    Uri uri = null;
                    try {
                        if (bitmap != null) {
                            if(!TextUtils.isEmpty(mErWeiCodeActivity.qrCodeUrl)){
                                uri = Uri.parse(mErWeiCodeActivity.qrCodeUrl);
                            }else {
                                uri = Uri.parse(mErWeiCodeActivity.bean.getQrUrl());
                            }
                            ImageUtils.saveImageToSD(mErWeiCodeActivity, mErWeiCodeActivity.SAVE_REAL_PATH + uri.getPath(), bitmap, 100);
                            mErWeiCodeActivity.SHARE_REAL_PATH = mErWeiCodeActivity.SAVE_REAL_PATH + uri.getPath();
                            mErWeiCodeActivity.getBaseLoadingView().hideLoading();
                            if (intent != null) {
                                EventSelectFriendBean bean = intent.getParcelableExtra(Constant.INTENT_DATA);
                                mErWeiCodeActivity.sendRongIm(bean);
                            }else {
                                // 发二维码到圈子动态
                                if(1 == mSentImageBean.getIsShare()){
                                    Intent intent1 = new Intent(mErWeiCodeActivity, IssueDynamicActivity.class);
                                    intent1.putExtra("select_type", "5");
                                    intent1.putExtra("type", "1");
                                    intent1.putExtra("picUrl", mErWeiCodeActivity.SHARE_REAL_PATH);
                                    mErWeiCodeActivity.startActivity(intent1);
                                    mErWeiCodeActivity.finish();
                                }else {
                                    ToastUtil.showLong(mErWeiCodeActivity, "图片已保存至" + mErWeiCodeActivity.SAVE_REAL_PATH + uri.getPath());
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erwei_code);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        id = getIntent().getIntExtra("id", -1);
        type = getIntent().getIntExtra("type", -1);
        qrCodeUrl = getIntent().getStringExtra(Constant.INTENT_DATA);

        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(getString(R.string.title_circle_erweicode));
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
    }

    @Override
    public void initData() {
        if(TextUtils.isEmpty(qrCodeUrl)){

            switch (type){
                case TYPE_CLASSROOM:
                case TYPE_SERIES:
                case TYPE_MALL_GOODS:
                case TYPE_DEAL_GOODS:
                case TYPE_CIRCLE:
                case TYPE_PERSONINFO:
                case TYPE_CUSTOM_GROUP:
                    getOrderQrcode();
                    break;

                default:
                    erweima();
            }

        }else{
            if(type == TYPE_CARD){     //从群聊那边跳转过来直接传二维码链接
                tvLabel.setVisibility(View.VISIBLE);
            }else {
                tvLabel.setVisibility(View.GONE);
            }
            ImageHelper.loadImage(this,ivErweiCode,qrCodeUrl,R.drawable.circle_place_holder_246);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            case R.id.HeadRightImageButton:
                showShare();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConversationActivity.STATUE_INVATE_CARD && resultCode == RESULT_OK){
            if(!TextUtils.isEmpty(qrCodeUrl)){
                createByComprs(qrCodeUrl,data,0);
            }else {
                createByComprs(bean.getQrUrl(),data,0);
            }
        }
    }

    public void createByComprs(final String url,final Intent data,final int isShare) {
        getBaseLoadingView().showLoading(true);
        Glide.with(this)
             .asBitmap()
             .load(url)
             .into(new SimpleTarget<Bitmap>() {
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                     Message       msg            = Message.obtain();
                     SentImageBean mSentImageBean = new SentImageBean();
                     mSentImageBean.setBitmap(resource);
                     mSentImageBean.setIntent(data);
                     mSentImageBean.setIsShare(isShare);
                     msg.obj = mSentImageBean;
                     handler.sendMessage(msg);

                 }
             });
    }


    private void sendRongIm(final EventSelectFriendBean bean1) {
        ImageMessage imageMessage = ImageMessage.obtain(Uri.fromFile(FileUtil.createFile(SHARE_REAL_PATH)),Uri.fromFile(FileUtil.createFile(SHARE_REAL_PATH)),true);
        RongIM.getInstance().sendImageMessage(bean1.mType, bean1.targetId, imageMessage, null, null, new RongIMClient.SendImageMessageCallback() {


            @Override
            public void onAttached(io.rong.imlib.model.Message message) {}

            @Override
            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(), "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
            }

            @Override
            public void onSuccess(io.rong.imlib.model.Message message) {
                ToastUtil.showShort(MyApplication.getInstance(),"分享成功");
                if(!TextUtils.isEmpty(bean1.liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage(bean1.liuyan,bean1.targetId,bean1.mType);
                }
            }

            @Override
            public void onProgress(io.rong.imlib.model.Message message, int i) {}
        });
    }

    @Subscribe
    public void onEvent(EventLoginBean bean){
        if(bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.THIRDLOGIN){
            erweima();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
        mAlertDialog = null;
    }

    /**
     * @param type      表示 从哪里跳转过来的 其他的给 -1
     *                  自己创建的群聊  传1
     *                  课堂 传2
     *                  系列课主页 传3
     *                  个人课堂主页 传4
     *
     * @param id  这个是圈子的二维码需要的 默认－1
     * @param qrCodeUrl 二维码图片url，有就传
     */
    public static void startActivity(Context context, int type, int id, String qrCodeUrl){
        Intent intent = new Intent(context,ErWeiCodeActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("id",id);
        intent.putExtra(Constant.INTENT_DATA,qrCodeUrl);
        context.startActivity(intent);
    }


    private void showShare() {
        if (!TextUtils.isEmpty(qrCodeUrl) || (bean != null && !"".equals(bean.getQrUrl()))) {
            String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200, new PermissionsResultListener() {

                @Override
                public void onPermissionGranted() {
                    UMShareUtils utils = new UMShareUtils(ErWeiCodeActivity.this);
                    if(bean != null){
                        utils.shareErweiCodeImg(bean.getQrUrl());
                    }

                    utils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                        @Override
                        public void onItemClick(int flag) {

                            //保存图片
                            switch (flag){
                                case 0:
                                    if(bean != null){
                                        SystemUtil.copyToClipboard(ErWeiCodeActivity.this, bean.getSourceUrl());
                                    }

                                    ToastUtil.showShort(ErWeiCodeActivity.this, "复制成功");
                                    break;

                                case 1:
                                    if(!TextUtils.isEmpty(qrCodeUrl)){
                                        createByComprs(qrCodeUrl,null,0);
                                    }else {
                                        createByComprs(bean.getQrUrl(),null,0);
                                    }
                                    break;

                                case 2:
                                    ConversationListActivity.startActivity(ErWeiCodeActivity.this, ConversationActivity.STATUE_INVATE_CARD,Constant.SELECT_TYPE_SHARE);
                                    break;

                                case 3:
                                    if(!TextUtils.isEmpty(qrCodeUrl)){
                                        createByComprs(qrCodeUrl,null,1);
                                    }else {
                                        createByComprs(bean.getQrUrl(),null,1);
                                    }
                                    break;
                            }
                        }
                    });

                }

                @Override
                public void onPermissionDenied() {
                    mAlertDialog = DialogUtil.showDeportDialog(ErWeiCodeActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(v.getId() == R.id.tv_dialog_confirm){
                                        JumpPermissionManagement.GoToSetting(ErWeiCodeActivity.this);
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                }
            });
        }
    }

    private void getOrderQrcode(){
        if (UserManager.getInstance().isLogin(this)) {
            getBaseLoadingView().showLoading();
            Map<String,String> map = new HashMap<>();
            map.put("token", UserManager.getInstance().getToken());
            map.put("objId",String.valueOf(id));
            map.put("type",String.valueOf(type));
            Subscription sub
                    = AllApi.getInstance().getOrderQrcode(map)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new ApiObserver<ApiResponseBean<CircleErweimaBean>>(new ApiCallBack<CircleErweimaBean>() {
                                @Override
                                public void onSuccess(CircleErweimaBean data) {
                                    if(getBaseLoadingView() == null || data == null) return;
                                    bean = data;
                                    getBaseLoadingView().hideLoading();
                                    ImageHelper.loadImage(ErWeiCodeActivity.this, ivErweiCode, data.getQrUrl());

                                    qrCodeUrl = data.getQrUrl();
                                    ivErweiCode.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            showShare();
                                            return true;
                                        }
                                    });
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    if(getBaseLoadingView() == null) return;
                                    getBaseLoadingView().hideLoading();
                                    ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), message);
                                }
                            }));


            RxTaskHelper.getInstance().addTask(this,sub);
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

        }
    }

    //统一整合 这个接口不用了
    @Deprecated
    private void erweima() {
        if (UserManager.getInstance().isLogin(this)) {
            getBaseLoadingView().showLoading();

            Subscription subErweima = CircleApi.getInstance().getGroupQr(UserManager.getInstance().getToken(), id)
                                               .subscribeOn(Schedulers.io())
                                               .observeOn(AndroidSchedulers.mainThread())
                                               .subscribe(new ApiObserver<ApiResponseBean<CircleErweimaBean>>(new ApiCallBack() {
                                                   @Override
                                                   public void onSuccess(Object data) {
                                                       if(getBaseLoadingView() == null) return;
                                                       getBaseLoadingView().hideLoading();

                                                       bean = (CircleErweimaBean) data;
                                                       if (!"".equals(bean.getQrUrl())) {

                                                           ImageHelper.loadImage(ErWeiCodeActivity.this, ivErweiCode, bean.getQrUrl());
                                                           ivErweiCode.setOnLongClickListener(new View.OnLongClickListener() {
                                                               @Override
                                                               public boolean onLongClick(View v) {
                                                                   showShare();
                                                                   return true;
                                                               }
                                                           });
                                                       }
                                                   }

                                                   @Override
                                                   public void onError(String errorCode, String message) {
                                                       if(getBaseLoadingView() == null) return;
                                                       getBaseLoadingView().hideLoading();
                                                       ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), message);
                                                   }
                                               }));
            RxTaskHelper.getInstance().addTask(this,subErweima);
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

        }
    }

    private void createByComprs(final String url) {
        getBaseLoadingView().showLoading(true);
        Subscription sub = Observable.just(url)
                                     .subscribeOn(Schedulers.io())
                                     .map(new Func1<String, Bitmap>() {
                                         @Override
                                         public Bitmap call(String s) {
                                             URL fileUrl = null;
                                             try {
                                                 fileUrl = new URL(url);
                                             } catch (MalformedURLException e) {
                                                 e.printStackTrace();
                                             }
                                             try {
                                                 HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
                                                 conn.setConnectTimeout(8000);
                                                 conn.setReadTimeout(60000);
                                                 conn.setDoInput(true);
                                                 conn.connect();
                                                 InputStream is = conn.getInputStream();
                                                 BitmapFactory.Options newOpts = new BitmapFactory.Options();
                                                 // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
                                                 newOpts.inJustDecodeBounds = true;// 设置不读入内存
                                                 byte[] bytes = new byte[1024];
                                                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                                 int count = 0;
                                                 while ((count = is.read(bytes)) != -1) {
                                                     bos.write(bytes, 0, count);
                                                 }
                                                 byte[] byteArray = bos.toByteArray();
                                                 newOpts.inSampleSize = 1;// 设置缩放比例 1表示不缩放
                                                 newOpts.inJustDecodeBounds = false;
                                                 Bitmap result = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, newOpts);
                                                 return result;

                                             } catch (IOException e) {
                                                 getBaseLoadingView().hideLoading();
                                                 e.printStackTrace();
                                             }
                                             return null;
                                         }
                                     })
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(new Action1<Bitmap>() {
                                         @Override
                                         public void call(Bitmap bitmap) {
                                             if (bitmap != null) {
                                                 try {
                                                     //这里是我改的 这几句代码用我的
                                                     String qrUrl = bean == null ? qrCodeUrl : bean.getQrUrl();
                                                     Uri uri = Uri.parse(qrUrl);
                                                     ImageUtils.saveImageToSD(ErWeiCodeActivity.this, SAVE_REAL_PATH + uri.getPath(), bitmap, 100);
                                                     getBaseLoadingView().hideLoading();
                                                     ToastUtil.showLong(ErWeiCodeActivity.this, "图片已保存至" + SAVE_REAL_PATH + uri.getPath());
                                                 } catch (IOException e) {
                                                     e.printStackTrace();
                                                 }

                                             }
                                         }
                                     });
        RxTaskHelper.getInstance().addTask(this,sub);
    }

}
