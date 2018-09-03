package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.event.EventLive;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.pop.PopQrCode;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.widget.RoundImageView;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity.REQUEST_CODE_AVATAR;

/**
 * Describe:课堂管理
 * Created by ALing on 2017/3/21 .
 */

public class LiveBgSettingActivity extends BaseTitleActivity implements View.OnClickListener,
        LiveBgSettingContract.View, PictureConfig.OnSelectResultCallback {
    private static final String TAG = "LiveBgSettingActivity";
    @BindView(R.id.iv_live_icon)   RoundImageView mIvLiveIcon;
    @BindView(R.id.tv_live_name)   TextView       mTvLiveName;
    @BindView(R.id.tv_live_intro)  TextView       mTvLiveIntro;
    @BindView(R.id.iv_qrcode)      ImageView      mIvQrcode;
    @BindView(R.id.rl_live_qrcode) RelativeLayout rlLiveQrcode;

    private HashMap<String, String>         map;
    private String                          token;
    private LiveBgSettingContract.Presenter mPresenter;
    private String                          chatRoomId;          //课堂ID
    private AlertDialog                     dialog;
    private PopQrCode                       popQrCode;
    private String                          cover;
    private String                          roomName;
    private String                          bgUri;
    private String                          introduce;
    private LiveRoomBean                    mLiveRoomBean;
    private String                          mQrCode;
    private static final int intro_requestcode = 1 << 2;
    private static final int bg_requestcode=1<<3;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_bg_setting);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_live_bg_setting));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        super.initData();
        new LiveBgSettingPrensenter(this);
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            chatRoomId = UserManager.getInstance().getUser().getChatRoomId();

            map = new HashMap<>();
            map.put("token", token);
            map.put("chatRoomId", chatRoomId);
            // 获取课堂后台信息
            mPresenter.getLiveManageData(map);
        }

    }

    @OnClick(
            {R.id.rl_live_icon, R.id.rl_live_name, R.id.rl_live_intro, R.id.rl_background, R.id.rl_live_qrcode})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //课堂图标
            case R.id.rl_live_icon:
                chooseImg();
                break;
            //课堂名称
            case R.id.rl_live_name:
                changeLiveName();
                break;
            //课堂简介
            case R.id.rl_live_intro:
                String introduce = mTvLiveIntro.getText().toString();
                map = new HashMap<>();
                map.put("introduce", introduce);
                GotoUtil.goToActivityWithDataAndForResult(this, LiveBgIntroduceActivity.class, map,
                        intro_requestcode);
                break;
            //背景
            case R.id.rl_background:
                GotoUtil.goToActivityForResult(this, ChangeLiveBgActivity.class,bg_requestcode);
                break;
            //课堂二维码名片
            case R.id.rl_live_qrcode:
                popQrCode = new PopQrCode(this, R.layout.pop_qrcode_layout);
                popQrCode.showQrCode(mQrCode);
                popQrCode.showPopOnRootView(this);
                break;

        }
    }

    /**
     * 修改课堂名称
     */
    private void changeLiveName() {
        dialog = DialogUtil.showInputDialog2(this, true, "课堂名称", mTvLiveName.getText().toString(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        roomName = DialogUtil.getEtInput();
                        map = new HashMap<String, String>();
                        map.put("token", token);
                        map.put("id", chatRoomId);
                        map.put("roomname", roomName);
                        mPresenter.saveChatRoomSetting(map);

                        dialog.dismiss();
                    }
                });
    }


    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, REQUEST_CODE_AVATAR,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        FunctionOptions options =
                                new FunctionOptions.Builder()
                                        .setType(FunctionConfig.TYPE_IMAGE)
                                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                                        .setImageSpanCount(3)
                                        .setEnableQualityCompress(false) //是否启质量压缩
                                        .setEnablePixelCompress(false) //是否启用像素压缩
                                        .setEnablePreview(true) // 是否打开预览选项
                                        .setShowCamera(true)
                                        .setPreviewVideo(true)
                                        .setIsCrop(true)
                                        .setAspectRatio(4, 3)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(LiveBgSettingActivity.this, LiveBgSettingActivity.this);

                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(LiveBgSettingActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(LiveBgSettingActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showLiveManageData(LiveRoomBean bean) {
        if (bean != null) {
            mLiveRoomBean = bean;
            ImageHelper.loadHeadIcon(this, mIvLiveIcon, R.drawable.person_icon_head_logo_62,
                    bean.getHeadpic());
            roomName = bean.getRoomname();
            bgUri = bean.getBakpic();
            introduce = bean.getIntroduce();
            cover=bean.getHeadpic();
            mTvLiveName.setText(bean.getRoomname());
            mTvLiveIntro.setText(bean.getIntroduce());
            mQrCode = bean.getQrcode();
            ImageHelper.loadHeadIcon(this, mIvQrcode, bean.getQrcode());
        }
    }


    @Override
    public void showPicList(List<BgPicBean> picData) {}

    @Override
    public void showCompressSuccess(File file) {
        mPresenter.uploadingFile(file);
    }

    @Override
    public void showCompressFailure() {
        ToastUtil.showShort(this, "压缩图片失败");
    }

    //上传图片成功
    @Override
    public void showUploadingSuccess(String url) {
        cover = url;
        ImageHelper.loadImage(LiveBgSettingActivity.this, mIvLiveIcon, url);
//        修改课堂图标
        map = new HashMap<>();
        map.put("token", token);
        map.put("id", chatRoomId);
        map.put("headpic", cover);
        mPresenter.saveChatRoomSetting(map);
        EventBusUtil.post(new EventLive(cover));
    }

    @Override
    public void showChatRoomSetting(LiveBgSettingBean bean) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        roomName = bean.getRoomname();
        mTvLiveName.setText(roomName);
    }

    @Override
    public void showLoadMore(List<BgPicBean> datas) {}

    @Override
    public void showNoMore() {}

    @Override
    public void showManagerList(LiveManagerBean bean) {

    }

    @Override
    public void showMoreManagerList(LiveManagerBean bean) {
    }

    @Override
    public void setPresenter(LiveBgSettingContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {
        mPresenter.getLiveManageData(map);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getLiveManageData(map);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == intro_requestcode && resultCode == RESULT_OK) {
            introduce = data.getStringExtra("introduce");
            mTvLiveIntro.setText(introduce);
        }
        if (requestCode == bg_requestcode && resultCode == RESULT_OK) {
            bgUri = data.getStringExtra("bgUri");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mAlertDialog = null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("cover", cover);
        intent.putExtra("roomName", roomName);
        intent.putExtra("bgUri", bgUri);
        intent.putExtra("introduce", introduce);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        mPresenter.compressImg(media.getPath());
    }
}
