package com.gxtc.huchuan.ui.live.intro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.SentImageBean;
import com.gxtc.huchuan.bean.ShareImgBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.im.ui.SendImageManager;
import com.gxtc.huchuan.ui.circle.homePage.CircleInviteActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.InvitedGuestsActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/4/17.
 * 邀请卡分享
 */

public class ShareImgActivity extends BaseTitleActivity {

    @BindView(R.id.btn_share_type)  Button    btnShareType;
    @BindView(R.id.btn_Share_share) Button    btnShareShare;
    @BindView(R.id.iv_share_img)    ImageView ivShareImg;

    int type = 1;
    private Subscription sub;
    private String       chatInfoId;
    private String       seriesId;  //系列课的id
    private ShareImgBean bean;
    private AlertDialog  mAlertDialog;
    private Boolean      mBoolean;
    private static final String SAVE_PIC_PATH  = Environment.getExternalStorageState().equalsIgnoreCase(
            Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH;//保存的确切位置
    public    String SHARE_REAL_PATH ;
    private MyHandler handler = new MyHandler(this);

    public static class MyHandler extends Handler{
        private ShareImgActivity                mActivity;
        private WeakReference<ShareImgActivity> mWeakReference ;

        private MyHandler(ShareImgActivity mActivity) {
            mWeakReference = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mWeakReference.get() == null) return;
            mActivity = mWeakReference.get();
            if (msg.obj != null) {
                SentImageBean mSentImageBean = (SentImageBean) msg.obj;
                Bitmap bitmap = mSentImageBean.getBitmap();
                Intent intent = mSentImageBean.getIntent();
                try {
                    if (bitmap != null) {
                        Uri uri = Uri.parse(mActivity.bean.getInvitationCardUrl());
                        ImageUtils.saveImageToSD(mActivity, SAVE_REAL_PATH + uri.getPath(), bitmap, 100);
                        mActivity.SHARE_REAL_PATH = SAVE_REAL_PATH + uri.getPath();
                        if (intent != null) {

                            EventSelectFriendBean bean = intent.getParcelableExtra(Constant.INTENT_DATA);
                            mActivity.sendRongIm(bean);
                        }else {
                            ToastUtil.showLong(mActivity, "图片已保存至" + SAVE_REAL_PATH + uri.getPath());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_img);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareImgActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_share_card));
        getBaseLoadingView().showLoading(true);

        int imgWidth = (int) (WindowUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.margin_larger) * 2);
        int imgHeight = imgWidth * 1280 / 720;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivShareImg.getLayoutParams();
        params.height = imgHeight;

    }


    @Override
    public void initData() {
        chatInfoId = getIntent().getStringExtra("chatInfoId");
        seriesId = getIntent().getStringExtra("seriesId");
        String isPay = getIntent().getStringExtra("isPay");
        if (isPay != null) {
            mBoolean = Boolean.valueOf(isPay);
        }
        if (UserManager.getInstance().isLogin()){
            getImage(String.valueOf(type));
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
    }

    private void getImage(final String type) {
        String id = "";
        String chatType = "";
        if(TextUtils.isEmpty(chatInfoId)){
            id = seriesId;
            chatType = "1";
        }else{
            id = chatInfoId;
            chatType = "0";
        }

        sub = LiveApi.getInstance()
                     .getChatInfoInvitation(UserManager.getInstance().getUserCode(), id, type, chatType)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<ShareImgBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if(ivShareImg == null)  return;
                                bean = (ShareImgBean) data;
                                ImageHelper.loadImage(ShareImgActivity.this, ivShareImg, bean.getInvitationCardUrl());
                                getBaseLoadingView().hideLoading();
                                if (ShareImgActivity.this.type == 9) {
                                    ShareImgActivity.this.type = 1;
                                } else {
                                    ShareImgActivity.this.type ++;
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(ShareImgActivity.this, errorCode, message);
                                if(ivShareImg == null)  return;
                                getBaseLoadingView().hideLoading();
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    @OnClick({R.id.btn_share_type, R.id.btn_Share_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_share_type:
                getBaseLoadingView().showLoading(true);
                getImage(String.valueOf(type));
//                if (type == 4) {
//                    getImage(String.valueOf(type));
//                } else {
//                    ++type;
//                    getImage(String.valueOf(type));
//                }
                break;
            case R.id.btn_Share_share:
                String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                        new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                if (!"".equals(bean.getInvitationCardUrl())) {
                                    UMShareUtils utils = new UMShareUtils(ShareImgActivity.this);
                                    utils.shareClassImg(bean.getInvitationCardUrl());
                                    utils.setOnItemClickListener(
                                            new UMShareUtils.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(int flag) {
                                                    //保存图片
                                                    if (0 == flag) {
                                                        createByComprs(bean.getInvitationCardUrl(),null);
                                                    } else if (1 == flag) {
//                                                        FocusActivity.startSelect(ShareImgActivity.this, Constant.SELECT_TYPE_SHARE,null,null, ConversationActivity.STATUE_INVATE_CARD);
                                                        ConversationListActivity.startActivity(ShareImgActivity.this, ConversationActivity.STATUE_INVATE_CARD,Constant.SELECT_TYPE_SHARE);
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onPermissionDenied() {
                                mAlertDialog = DialogUtil.showDeportDialog(ShareImgActivity.this,
                                        false, null, getString(R.string.pre_storage_notice_msg),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == R.id.tv_dialog_confirm) {
                                                    JumpPermissionManagement.GoToSetting(
                                                            ShareImgActivity.this);
                                                }
                                                mAlertDialog.dismiss();
                                            }
                                        });

                            }
                        });
                break;
        }
    }

    public void createByComprs(final String url,final Intent data) {
        getBaseLoadingView().showLoading(true);
        Glide.with(this)
             .asBitmap()
             .load(url)
             .into(new SimpleTarget<Bitmap>() {
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource,
                                             @Nullable Transition<? super Bitmap> transition) {
                     Message msg = Message.obtain();
                     SentImageBean mSentImageBean = new SentImageBean();
                     mSentImageBean.setBitmap(resource);
                     mSentImageBean.setIntent(data);
                     msg.obj = mSentImageBean;
                     handler.sendMessage(msg);
                 }
             });
    }

    /**
     * 系列课id
     */
    public static void startActivity(Context context, String seriesId) {
        Intent intent = new Intent(context, ShareImgActivity.class);
        intent.putExtra("seriesId", seriesId);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConversationActivity.STATUE_INVATE_CARD && resultCode == RESULT_OK){
            createByComprs(ShareImgActivity.this.bean.getInvitationCardUrl(),data);
        }
    }

    private void sendRongIm(final EventSelectFriendBean bean1) {
        getBaseLoadingView().hideLoading();
        ImageMessage imageMessage = ImageMessage.obtain(Uri.fromFile(FileUtil.createFile(SHARE_REAL_PATH)),Uri.fromFile(FileUtil.createFile(SHARE_REAL_PATH)),true);  //
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
                    RongIMTextUtil.INSTANCE.relayMessage
                            (bean1.liuyan,bean1.targetId,bean1.mType);
                }
            }

            @Override
            public void onProgress(io.rong.imlib.model.Message message, int i) {}
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlertDialog = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
