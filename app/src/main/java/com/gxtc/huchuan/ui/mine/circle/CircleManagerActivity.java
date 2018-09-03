package com.gxtc.huchuan.ui.mine.circle;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleManagerAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleManagerBean;
import com.gxtc.huchuan.bean.CircleShareInviteBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventCircleMemberShipBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleMemberActivity;
import com.gxtc.huchuan.ui.circle.classroom.classroomaudit.ClassRoomAuditActivity;
import com.gxtc.huchuan.ui.circle.classroom.newcalssroomaudit.NewClassRoomAuditActivity;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.file.filelist.FileAuditActivity;
import com.gxtc.huchuan.ui.circle.file.folder.FolderListActivity;
import com.gxtc.huchuan.ui.circle.topic.CircleDynamicManagerActivity;
import com.gxtc.huchuan.ui.live.hostpage.RefundsAndCheckMemberActivity;
import com.gxtc.huchuan.ui.mine.circle.article.ArticleManagerActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CircleRecyclerView;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;
import com.yalantis.ucrop.util.FileUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * Created by sjr on 2017/5/2.
 * 圈子管理界面
 * <p>
 */

public class CircleManagerActivity extends BaseTitleActivity {

    @BindView(R.id.rv_circle_manager) CircleRecyclerView mRecyclerView;

    private int          circleId;      //圈子id
    private int          mIsMy;         //是否是圈主
    private String       targetId;      //聊天ID

    Intent intent;

    ProgressDialog mProgressDialog;


    CircleBean mBean;               //圈子资料
    private AlertDialog mAlertDialog;
    public String liuyan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_manager);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_circle_manager));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleManagerActivity.this.finish();
            }
        });
        EventBusUtil.register(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        circleId = intent.getIntExtra("circleId", -1);
        targetId = intent.getStringExtra("targetId");
        mIsMy = intent.getIntExtra("isMy", mIsMy);
        initCircleData();
    }

    /**
     * 获取圈子资料
     */
    private void initCircleData() {
        if (UserManager.getInstance().isLogin()) {
            Subscription sub =
                    CircleApi.getInstance()
                             .circleData(UserManager.getInstance().getToken(), circleId)
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                                @Override
                                public void onSuccess(Object data) {
                                    if(mRecyclerView == null)   return;
                                    mBean = (CircleBean) data;
                                    mBean.getIsAudit();
                                    setMenu(mBean.getIsAudit());
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    ToastUtil.showShort(CircleManagerActivity.this, message);
                                }
                            }));
            RxTaskHelper.getInstance().addTask(this,sub);
        } else
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

    }

    private void setMenu(String isAudit) {
        List<CircleManagerBean> datas = new ArrayList<>();
        //不需要审核
        if ("0".equals(isAudit)) {
            final String[] title = {"动态管理", "文章管理", "课堂管理", "文件管理", "发布群广播", "编辑详情页", "成员管理", "圈子设置", "免费邀请", "二维码", "数据统计","审核"};
            int[] resid = {R.drawable.circle_manage_icon_topic,
                    R.drawable.circle_manage_icon_article,
                    R.drawable.circle_manage_icon_class,
                    R.drawable.circle_manage_icon_upload,
                    R.drawable.circle_manage_icon_notice,
                    R.drawable.circle_manage_icon_detail,
                    R.drawable.circle_manage_icon_cygl,
                    R.drawable.circle_manage_icon_qzsz,
                    R.drawable.circle_manage_icon_mfyq,
                    R.drawable.circle_manage_icon_rwm,
                    R.drawable.circle_manage_icon_sjtj,
                    R.drawable.circle_manage_icon_detail};
            for (int i = 0; i < title.length; i++) {
                CircleManagerBean bean = new CircleManagerBean();
                bean.setName(title[i]);
                bean.setResid(resid[i]);
                datas.add(bean);
            }
        } else if ("1".equals(isAudit)) {
            final String[] title = {"动态管理", "文章管理", "课堂管理", "文件管理", "发布群广播", "编辑详情页", "成员管理", "圈子设置", "免费邀请", "二维码", "数据统计","审核"};//申请列表移到RefundsActivity 这个见面   R.drawable.circle_manage_icon_shsq
            int[]          resid = {R.drawable.circle_manage_icon_topic,
                    R.drawable.circle_manage_icon_article,
                    R.drawable.circle_manage_icon_class,
                    R.drawable.circle_manage_icon_upload,
                    R.drawable.circle_manage_icon_notice,
                    R.drawable.circle_manage_icon_detail,
                    R.drawable.circle_manage_icon_cygl,
                    R.drawable.circle_manage_icon_qzsz,
                    R.drawable.circle_manage_icon_mfyq,
                    R.drawable.circle_manage_icon_rwm,
                    R.drawable.circle_manage_icon_sjtj,
                    R.drawable.circle_manage_icon_detail};
            for (int i = 0; i < title.length; i++) {
                CircleManagerBean bean = new CircleManagerBean();
                bean.setName(title[i]);
                bean.setResid(resid[i]);
                datas.add(bean);
            }
        }
        initRecyclerView(datas);
    }

    private void initRecyclerView(List<CircleManagerBean> datas) {

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        final CircleManagerAdapter adapter = new CircleManagerAdapter(this, datas, R.layout.item_circle_manager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    //动态
                    case 0:
                        if (UserManager.getInstance().isLogin()) {
                            Intent intent = new Intent(CircleManagerActivity.this,
                                    CircleDynamicManagerActivity.class);
                            intent.putExtra("circle_id", circleId);
                            startActivity(intent);

                        } else GotoUtil.goToActivity(CircleManagerActivity.this,
                                LoginAndRegisteActivity.class);
                        break;

                    //文章管理
                    case 1:
                        if (UserManager.getInstance().isLogin()) {
                            intent = new Intent(CircleManagerActivity.this,
                                    ArticleManagerActivity.class);
                            intent.putExtra("circle_id", circleId);
                            startActivity(intent);
                        } else GotoUtil.goToActivity(CircleManagerActivity.this,
                                LoginAndRegisteActivity.class);
                        break;

                    //创建课堂
                    case 2:
//                        ClassRoomAuditActivity.startActivity(CircleManagerActivity.this, circleId, UserManager.getInstance().getUserCode());
                        NewClassRoomAuditActivity.startActivity(CircleManagerActivity.this, circleId, UserManager.getInstance().getUserCode());
                        break;

                    //文件管理
                    case 3:
                        onClickFile();
                        break;

                    //发布群广播
                    case 4:
                        if (UserManager.getInstance().isLogin()) {
                            intent = new Intent(CircleManagerActivity.this, IssueNoticeActivity.class);
                            intent.putExtra("groud_id", circleId);
                            intent.putExtra("targetId", targetId);
                            startActivity(intent);
                        } else
                            GotoUtil.goToActivity(CircleManagerActivity.this, LoginAndRegisteActivity.class);
                        break;

                    //编辑详情页
                    case 5:
                        gotoDetailed();
                        break;

                    //成员管理
                    case 6:
                        gotoMemberList(EventCircleIntro.ALLMEMBER);
                        break;

                    //圈子设置
                    case 7:
                        if (UserManager.getInstance().isLogin()) {
                            CircleManagerActivity.this.intent = new Intent(CircleManagerActivity.this, CircleSettingActivity.class);
                            CircleManagerActivity.this.intent.putExtra("circle_data", mBean);
                            startActivityForResult(CircleManagerActivity.this.intent, 666);
                        } else {
                            GotoUtil.goToActivity(CircleManagerActivity.this, LoginAndRegisteActivity.class);
                        }
                        break;

                    //免费邀请
                    case 8:
                        if(mBean != null){
                            shareInvite();
                        }
                        break;

                    //二维码
                    case 9:
                        if (mBean != null) {
                            if (UserManager.getInstance().isLogin()) {
                                CircleManagerActivity.this.intent = new Intent(CircleManagerActivity.this, ErWeiCodeActivity.class);
                                CircleManagerActivity.this.intent.putExtra("id", mBean.getId());
                                startActivity(CircleManagerActivity.this.intent);
                            } else {
                                GotoUtil.goToActivity(CircleManagerActivity.this, LoginAndRegisteActivity.class);
                            }
                        }
                        break;

                    //数据统计
                    case 10:
                        gotoSjtj();
                        break;

                    //申请列表
                    case 11:
                      //  Intent intent1 = new Intent(CircleManagerActivity.this, ApplyForMemberListActivity.class); 原来的申请列表界面
                        Intent intent = new Intent(CircleManagerActivity.this, RefundsAndCheckMemberActivity.class); //memberType;        //成员类型  0 普通成员  1 管理员  2 圈主
                        intent.putExtra("bean", mBean);
                        startActivity(intent);
                        break;
                }

            }
        });
    }

    private void onClickFile() {
        FileAuditActivity.startActivity(this,mBean,4);
    }

    private void gotoMemberList(int flag) {
        EventBusUtil.postStickyEvent(new EventCircleIntro(flag));
        GotoUtil.goToActivity(this, CircleMemberActivity.class, 0, mBean);
    }


    private void gotoSjtj() {
        Intent intent = new Intent(this, CircleStatisticActivity.class);
        intent.putExtra(Constant.INTENT_DATA, mBean.getId());
        intent.putExtra("name", mBean.getName());
        startActivity(intent);
    }

    /**
     * 免费邀请
     */
    private void shareInvite() {
        if (UserManager.getInstance().isLogin()) {
            getBaseLoadingView().showLoading();
            Subscription sub =
                    CircleApi.getInstance()
                             .getGroupFreeInviteUrl(UserManager.getInstance().getToken(), mBean.getId())
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(new ApiObserver<ApiResponseBean<CircleShareInviteBean>>(new ApiCallBack() {
                                @Override
                                public void onSuccess(Object data) {
                                    if(mRecyclerView == null)   return;
                                    getBaseLoadingView().hideLoading();
                                    CircleShareInviteBean bean = (CircleShareInviteBean) data;
                                    if (!"".equals(bean.getFreeUrl())){
                                        UMShareUtils shareUtils = new UMShareUtils(CircleManagerActivity.this);
                                        shareUtils.shareFreeCircle(mBean.getCover(), mBean.getName(), mBean.getName() + " 限时免费加入，快来加入与我一起探讨吧", bean.getFreeUrl());
                                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int flag) {
                                                ConversationListActivity.CAN_SHARE_INVITE = 6;
                                                ConversationListActivity.startActivity(CircleManagerActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE,ConversationListActivity.CAN_SHARE_INVITE);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    if(mRecyclerView == null)   return;
                                    getBaseLoadingView().hideLoading();
                                    ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), message);
                                }
                            }));
            RxTaskHelper.getInstance().addTask(this,sub);
        } else
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    private void gotoDetailed() {
        Intent intent = new Intent(this, EditCircleInfoActivity.class);
        intent.putExtra("groupId", circleId);
        intent.putExtra("isMy", mBean.getMemberType());  // 0 普通成员  1 管理员  2 圈主;
        startActivity(intent);
    }

    /**
     * 打开文件管理器
     */
    private void openFileManager() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 10010,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(intent, Constant.requestCode.UPLOAD_FILE);
                        } catch (android.content.ActivityNotFoundException ex) {
                            ToastUtil.showShort(CircleManagerActivity.this, "请先下载一个文件管理器");
                        }

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CircleManagerActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CircleManagerActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });


                    }
                });
    }


    /**
     * 压缩图片
     */
    public void compression(String path) {
        //将图片进行压缩
        File file = new File(path);
        Luban.get(MyApplication.getInstance()).load(file)                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
             .setCompressListener(new OnCompressListener() {

                 // 压缩开始前调用，可以在方法内启动 loading UI
                 @Override
                 public void onStart() {
                 }

                 // 压缩成功后调用，返回压缩后的图片文件
                 @Override
                 public void onSuccess(final File file) {
                     uploadFile(4, file);

                 }

                 //  当压缩过去出现问题时调用
                 @Override
                 public void onError(Throwable e) {
//                        getBaseLoadingView().hideLoading();
                     ToastUtil.showShort(CircleManagerActivity.this, e.toString());
                 }
             }).launch();
    }


    private void uploadFile(final int type, final File file) {
        if (UserManager.getInstance().isLogin()) {
            mProgressDialog = new ProgressDialog(CircleManagerActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("文件正在努力上传中，请稍等...");
            mProgressDialog.setProgress(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            LoadHelper.uploadFile(LoadHelper.UP_TYPE_VIDEO, new LoadHelper.UploadCallback() {
                @Override
                public void onUploadSuccess(UploadResult result) {
                    saveCircleFile(file, result.getUrl(), type);
                }

                @Override
                public void onUploadFailed(String errorCode, String msg) {
                    ToastUtil.showShort(CircleManagerActivity.this, "上传出错");
                    mProgressDialog.dismiss();
                }
            },new UIProgressListener() {
                @Override
                public void onUIProgress(long currentBytes, long contentLength, boolean done) {
                    mProgressDialog.setProgress((int) ((100 * currentBytes) / contentLength));
                    if (currentBytes == contentLength) {
                        mProgressDialog.dismiss();
                    }
                }
            }, file);



        }
    }


    /**
     * 将上传后的文件保存到圈子中
     */
    private void saveCircleFile(File file, String fileUrl, int type) {
        Subscription sub =
                AllApi.getInstance()
                      .saveCircleFile(circleId, UserManager.getInstance().getToken(), fileUrl, file.getName(), type, null)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                ToastUtil.showShort(CircleManagerActivity.this, "上传成功");
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(CircleManagerActivity.this, errorCode,
                                        message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == 678) {
            mBean = (CircleBean) data.getSerializableExtra("circle_data");

            setMenu(mBean.getIsAudit());
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.requestCode.UPLOAD_FILE) {
            Uri    uri  = data.getData();
            String path = FileUtils.getPath(this, uri);

            if (!"".equals(path)) {
                File file = new File(path);
                //这里判断类型
                //图片
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(
                        ".jpeg") || path.endsWith(".gif")) {

                    compression(path);
                } else if (path.endsWith(".doc")) {//文档

                    uploadFile(0, file);
                } else if (path.endsWith(".xls")) {//excel

                    uploadFile(1, file);
                } else if (path.endsWith(".ppt")) {//ppt

                    uploadFile(2, file);
                } else if (path.endsWith(".pdf")) {//pdf

                    uploadFile(3, file);
                } else if (path.endsWith(".mp4") || path.endsWith(".avi") || path.endsWith(
                        ".rmvb") || path.endsWith(".wmv") || path.endsWith("rm") || path.endsWith(
                        ".flv") || path.endsWith(".mpg") || path.endsWith("mkv")) {//视频文件

                    uploadFile(5, file);
                } else if (path.endsWith(".exe")) {//exe执行文件

                    uploadFile(6, file);
                } else if (path.endsWith(".zip") || path.endsWith(".rar")) {//压缩文件

                    uploadFile(7, file);
                } else {

                    uploadFile(8, file);

                }

            } else {
                ToastUtil.showShort(CircleManagerActivity.this, "文件路径解析失败！");
            }
        }

        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT){
            if(data != null){
                EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
                if(bean != null)
                    shareInvite(bean);
            }
        }
    }


    @Subscribe
    public void onEvent(EventCircleMemberShipBean bean) {
        mBean.setIsFee(bean.getIsFree());
        mBean.setFee(Double.valueOf(bean.getFee()));
        mBean.setPent(Integer.valueOf(bean.getFee()));
    }

    //邀请好友免费加入
    private void shareInvite(EventSelectFriendBean bean){
        liuyan = bean.liuyan;
        String                        targetId = bean.targetId;
        Conversation.ConversationType type     = bean.mType;
        String name = UserManager.getInstance().getUserName();
        String infoType = "6";
        String title = name + "邀请你加入" + mBean.getName() ;
        getFreeSign(targetId,type,title,mBean.getCover(),infoType);
    }

    private void getFreeSign(String targetId, Conversation.ConversationType type, String title, String cover, String infoType) {
        final String                        mId       = targetId;
        final Conversation.ConversationType mType     = type;
        final String                        mTitle    = title;
        final String                        mImg      = cover;
        final String                        mInfoType = infoType;
        String                              token     = UserManager.getInstance().getToken();

        getBaseLoadingView().showLoading();
        Subscription sub =
                CircleApi.getInstance()
                         .getGroupFreeInviteUrl(token,Integer.valueOf(circleId))
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribeOn(Schedulers.io())
                         .subscribe(new ApiObserver<ApiResponseBean<CircleShareInviteBean>>(new ApiCallBack() {
                             @Override
                             public void onSuccess(Object data) {
                                 if(getBaseLoadingView() == null)   return;
                                 getBaseLoadingView().hideLoading();
                                 CircleShareInviteBean bean = (CircleShareInviteBean) data;
                                 if(bean != null){
                                     String url = bean.getFreeUrl();
                                     if(!TextUtils.isEmpty(url)){
                                         String freeSign = url.substring(url.lastIndexOf("=") + 1,url.length());
                                         String tempId = circleId + "&" + freeSign;
                                         sendMessage(mId, mType, tempId, mTitle, mImg, mInfoType);
                                     }
                                 }
                             }

                             @Override
                             public void onError(String errorCode, String message) {
                                 if(getBaseLoadingView() == null)   return;
                                 getBaseLoadingView().hideLoading();
                                 ToastUtil.showShort(CircleManagerActivity.this,message);
                             }
                         }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void sendMessage(final String mId, final Conversation.ConversationType type, String id, String title, String img, String infoType) {
        ImMessageUtils.shareMessage(mId, type, id, title, img, infoType,
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {}

                    @Override
                    public void onSuccess(Message message) {
                        ToastUtil.showShort(CircleManagerActivity.this, "分享成功");
                        if(!TextUtils.isEmpty(liuyan)){
                            RongIMTextUtil.INSTANCE.relayMessage
                                    (liuyan,mId,type);
                        }
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(CircleManagerActivity.this, "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode));
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        mAlertDialog = null;

    }
}
