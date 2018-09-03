package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveBgSettingAdapter;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.widget.CircleImageView;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Describe:修改直播间背景
 * Created by ALing on 20/22 .
 */

public class ChangeLiveBgActivity extends BaseTitleActivity implements View.OnClickListener,
        LiveBgSettingContract.View, BaseRecyclerAdapter.OnReItemOnClickListener,
        PictureConfig.OnSelectResultCallback {

    @BindView(R.id.iv_bg)           ImageView       mIvBg;
    @BindView(R.id.iv_live_icon)    CircleImageView mIvLiveIcon;
    @BindView(R.id.tv_name)         TextView        mTvName;
    @BindView(R.id.tv_focus)        TextView        mTvFocus;
    @BindView(R.id.tv_upload_pic)   TextView        mTvUploadPic;
    @BindView(R.id.rl_upload)       RelativeLayout  mRlUpload;
    @BindView(R.id.lable_choose_bg) TextView        mLableChooseBg;
    @BindView(R.id.btn_confirm)     Button          mBtnConfirm;
    @BindView(R.id.rc_list)         RecyclerView    mRcList;

    private String                          token;
    private LiveBgSettingContract.Presenter mPresenter;
    private HashMap<String, String>         map;
    private String                          cover;
    private int    start = 0;
    private String type  = "1";
    private String               chatRoomId;
    private LiveBgSettingAdapter adapter;
    private String               picUrl;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_live_bg);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_change_live_bg));
        getBaseHeadView().showBackButton(this);
        initRecyCleView();
    }

    @Override
    public void initData() {
        new LiveBgSettingPrensenter(this);

        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
            getDefaultBgList();
            getTopBg();
        }

    }

    /**
     * 获取头部背景
     */
    private void getTopBg() {
        map = new HashMap<>();
        map.put("token", token);
        map.put("chatRoomId", chatRoomId);
        mPresenter.getLiveManageData(map);
    }

    /**
     * 获取默认背景列表
     */
    private void getDefaultBgList() {
        map = new HashMap<>();
        map.put("token", token);
        map.put("start", String.valueOf(start));
        map.put("type", type);
        // 获取背景图片
        mPresenter.getPicList(map);
    }

    @OnClick({R.id.btn_confirm, R.id.rl_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            case R.id.btn_confirm:
                confirmChangePic();
                break;
            case R.id.rl_upload:
                chooseImg();
                break;

        }
    }

    private void confirmChangePic() {
        //修改直播间默认背景
        map = new HashMap<>();
        map.put("token", token);
        map.put("id", chatRoomId);
        map.put("bakpic", cover);
        mPresenter.saveChatRoomSetting(map);
    }

    private void initRecyCleView() {
        mRcList.setLayoutManager(new GridLayoutManager(this, 2));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        mRcList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.BOTH_SET, 24,
                        getResources().getColor(R.color.color_ffffff)));
    }

    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和存储权限", pers, 1011, new PermissionsResultListener() {
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
                PictureConfig.getInstance().init(options).openPhoto(ChangeLiveBgActivity.this, ChangeLiveBgActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(ChangeLiveBgActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(v.getId() == R.id.tv_dialog_confirm){
                                    JumpPermissionManagement.GoToSetting(ChangeLiveBgActivity.this);
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
        ImageHelper.loadImage(this, mIvBg, bean.getBakpic(), R.drawable.live_room_icon_temp);

        ImageHelper.loadHeadIcon(this, mIvLiveIcon, R.drawable.person_icon_head_120,
                bean.getHeadpic());
        mTvName.setText(bean.getRoomname());
        mTvFocus.setText(bean.getFs() + "关注");
    }

    @Override
    public void showPicList(List<BgPicBean> picData) {
        adapter = new LiveBgSettingAdapter(this, picData, R.layout.item_live_bg);
        mRcList.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(this);
    }

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
//        ImageHelper.loadImage(ChangeLiveBgActivity.this, mIvBg, url);
        //修改直播间默认背景
        confirmChangePic();
    }

    @Override
    public void showChatRoomSetting(LiveBgSettingBean bean) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        ImageHelper.loadHeadIcon(this, mIvBg, cover);
        EventBusUtil.post(new LiveBgSettingBean(cover));
    }

    @Override
    public void showLoadMore(List<BgPicBean> datas) {

    }

    @Override
    public void showNoMore() {

    }

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
    public void onItemClick(View v, int position) {
        for (int i = 0; i < adapter.getList().size(); i++) {
            //取消所有选择
            if (adapter.getList().get(i).isSelect()) {
                adapter.getList().get(i).setSelect(false);
                mRcList.notifyItemChanged(i);
            }
        }
        picUrl = adapter.getList().get(position).getPicUrl();
        cover = picUrl;
        ImageHelper.loadImage(ChangeLiveBgActivity.this, mIvBg, cover);

        adapter.getList().get(position).setSelect(true);
        mRcList.notifyItemChanged(position);
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
        intent.putExtra("bgUri", cover);
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
