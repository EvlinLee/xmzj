package com.gxtc.huchuan.ui.mine.editinfo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.bean.event.EventIntroBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.EditInfoDialog;
import com.gxtc.huchuan.dialog.ProvinceCityAreaDialog;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.widget.RoundImageView;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

//import com.imnjh.imagepicker.CapturePhotoHelper;

/**
 * Created by ALing on 2017/2/22.
 */

public class EditInfoActivity extends BaseTitleActivity implements EditInfoContract.View,
        ProvinceCityAreaDialog.OnCitySelectListener, PictureConfig.OnSelectResultCallback {

    private static final String TAG = EditInfoActivity.class.getSimpleName();
    @BindView(R.id.iv_my_avatar)    RoundImageView ivMyAvatar;
    @BindView(R.id.rl_avatar)       RelativeLayout rlAvatar;
    @BindView(R.id.tv_nikename)     TextView       tvNikename;
    @BindView(R.id.rl_nikename)     RelativeLayout rlNikename;
    @BindView(R.id.tv_sex)          TextView       tvSex;
    @BindView(R.id.rl_sex)          RelativeLayout rlSex;
    @BindView(R.id.tv_area)         TextView       tvArea;
    @BindView(R.id.rl_area)         RelativeLayout rlArea;
    @BindView(R.id.tv_introduction) TextView       tvIntroduction;
    @BindView(R.id.rl_Verification) RelativeLayout       reVerification;
    @BindView(R.id.Verification_status) TextView       tvStutus;

    public static final int REQUEST_CODE_AVATAR = 10000;

    private boolean                 isLogin;
    private String                  sex = "";
    private HashMap<String, String> mDataMap;

    private EditInfoContract.Presenter mPresenter;
    private String                     token;

    private ProvinceCityAreaDialog dialog;
    private String                 province;
    private String                 city;
    private String                 area;


    private AlertDialog       mediaDialog;
    private String            intro;
    private EditInfoDialog mEditInfoDialog;
    private AlertDialog mAlertDialog;
    private ImageView iv;
    private VertifanceFlowDialog mVertifanceFlowDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
    }


    @Override
    public void initData() {
        super.initData();
        new EditInfoPrenster(this);
        EventBusUtil.register(this);
        mDataMap = new HashMap<>();
        isLogin = UserManager.getInstance().isLogin();
        if (isLogin) {
            token = UserManager.getInstance().getToken();
            mPresenter.getUserInfo(token);
        }
        //先判断是否头像为空
        if(TextUtils.isEmpty(UserManager.getInstance().getHeadPic()) || TextUtils.isEmpty(UserManager.getInstance().getUserName())){
            showInfoDialog();
        }else {
            switch (UserManager.getInstance().getIsRealAudit()){
                case  "0":
                    showDialog();
                    tvStutus.setText("未认证");
                    break;
                case  "1":
                    tvStutus.setText("已认证");
                    break;
                case  "2":
                    tvStutus.setText("审核中");
                    break;
                case  "3":
                    tvStutus.setText("审核不通过");
                    break;
            }
        }
    }

    private void showDialog() {
        DialogUtil.VerificationDialog(this,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                   if(v.getId() == R.id.btn_issue){
                            GotoUtil.goToActivityForResult(EditInfoActivity.this,VertifanceActivity.class,100);
                        }
                    }
                });
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showTitle(getString(R.string.title_edit_info));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void showInfoDialog(){
        if(mEditInfoDialog == null) mEditInfoDialog = new EditInfoDialog(this);
        mEditInfoDialog.show();
        mEditInfoDialog.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.head_pic:
                        isFireRegister = true;
                        iv = (ImageView) v;
                        chooseImg();
                        break;
                    case R.id.man:
                        sex = "1";
                        break;
                    case R.id.woman:
                        sex = "2";
                        break;
                    case R.id.btn_next:
                        if( TextUtils.isEmpty(picHead)){
                            ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"请选择头像");
                            return;
                        }
                        if(TextUtils.isEmpty(sex)){
                            ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"请选择性别");
                            return;
                        }
                        nikeNameSave();
                        break;
                }
            }
        });
        mEditInfoDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                else
                    return false; //默认返回 false
            }
        });
    }


    @OnClick({R.id.rl_avatar, R.id.rl_nikename, R.id.rl_introduction, R.id.rl_sex, R.id.rl_area,R.id.rl_Verification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_avatar:
//                changeAvatarDialog();
                chooseImg();
                break;
            case R.id.rl_nikename:
                GotoUtil.goToActivity(this, EditNikeNameActivity.class);
                break;
            case R.id.rl_introduction:
                gotoEditIntro();
                break;
            case R.id.rl_sex:
                changeSexDialog();
                break;
            case R.id.rl_area:
                showChooseProvinceCityArea();
                break;
            case R.id.rl_Verification://实名认证
                if ("0".equals(UserManager.getInstance().getIsRealAudit())){
                    GotoUtil.goToActivityForResult(this,VertifanceActivity.class,100);
                }else {
                      showVertifanceDialog();
                }
                break;
        }

    }

    private void showVertifanceDialog() {
      if(mVertifanceFlowDialog == null){
          mVertifanceFlowDialog = new VertifanceFlowDialog(this);
      }
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("3".equals(UserManager.getInstance().getIsRealAudit())){
                    GotoUtil.goToActivityForResult(EditInfoActivity.this,VertifanceActivity.class,100);
                }
                 mVertifanceFlowDialog.dismiss();
            }
        });
        switch (UserManager.getInstance().getIsRealAudit()){
            case  "1":
                mVertifanceFlowDialog.setFlowStatus("审核成功");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_3);
                break;
            case  "2":
                mVertifanceFlowDialog.setFlowStatus("系统正在审核中，请耐心等待");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case  "3":
                checkCicle();
                break;
        }
    }

    public void checkCicle(){
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(),UserManager.getInstance().getUserCode(),  Constant.STATUE_LINKTYPE_REAL_NAME, new ApiCallBack<CheckBean>() {

            @Override
            public void onSuccess(CheckBean data) {
                if(data == null) return;
                mVertifanceFlowDialog.setFlowStatus("审核不通过 "+data.getContent());
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(EditInfoActivity.this,message);
            }
        }).addTask(this);
    }

    private void nikeNameSave() {
        if (TextUtils.isEmpty(mEditInfoDialog.getNiCheng().trim())){
            ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),getString(R.string.tusi_nikename_canot_empty));
            return;
        }else if (mEditInfoDialog.getNiCheng().length() > 8){
            ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"昵称字数不能大于8");
            return;
        }else {
            HashMap<String,String> map = new HashMap<String, String>();
            map.put("name",mEditInfoDialog.getNiCheng());
            map.put("sex",sex);
            map.put("token", UserManager.getInstance().getToken());
            mPresenter.getEditInfo(map);
        }
    }

    private void gotoEditIntro() {
        Intent intent = new Intent(this, EditIntroductionActivity.class);
        intent.putExtra("editIntro", tvIntroduction.getText().toString());
        this.startActivity(intent);
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
                                        .setAspectRatio(1, 1)
                                        .create();
                        PictureConfig.getInstance().init(options).openPhoto(EditInfoActivity.this, EditInfoActivity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(EditInfoActivity.this, false, null, getString(R.string.pre_scan_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(EditInfoActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });


                    }
                });
    }

    private void showChooseProvinceCityArea() {
        if (dialog == null) {
            dialog = new ProvinceCityAreaDialog(this);
            dialog.setOnCitySelectListener(this);
        }
        dialog.show();
    }

    @Override
    public void onCitySelect(String province, String city, String area) {
        this.province = province;
        this.city = city;
        this.area = TextUtils.isEmpty(area) ? "" : area;
//        String encodeProvince = Base64Util.encode(province);
//        String encodeCity = Base64Util.encode(city);
//        String encodeArea = Base64Util.encode(area);
        tvArea.setText(province + "" + city + area);
        mDataMap.put("token", token);
        mDataMap.put("province", province);
        mDataMap.put("city", city);
        mDataMap.put("area", area);
        mPresenter.getEditInfo(mDataMap);
    }

    /**
     * 修改性别对话框
     */
    public void changeSexDialog() {
        String[]                items  = getResources().getStringArray(R.array.change_sex);
        final ActionSheetDialog dialog = new ActionSheetDialog(this, items, null);
        dialog.isTitleShow(false).show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sex = "1";
                        break;
                    case 1:
                        sex = "2";
                        break;
                }
                setSex(sex);
                dialog.dismiss();
            }
        });
    }

    void setSex(String sexStatus){
        HashMap<String,String> sexData = new HashMap();
        sexData.put("token", token);
        sexData.put("sex", sexStatus);
        mPresenter.getEditInfo(sexData);
    }

    //获取用户信息成功
    @Override
    public void getUserSuccess(User user) {
        ImageHelper.loadHeadIcon(this, ivMyAvatar, R.drawable.person_icon_head, user.getHeadPic());
        tvNikename.setText(user.getName());
//        tvIntroduction.setText(user.getInterest());  简介 接口少参数
        String introduction = TextUtils.isEmpty(user.getIntroduction()) ? "未设置": user.getIntroduction();
        String city = TextUtils.isEmpty(user.getProvince()) && TextUtils.isEmpty(user.getCity()) && TextUtils.isEmpty(user.getArea())
                ? "未设置": user.getProvince() + " " + user.getCity() + "" + user.getArea();
        String sex = user.getSex();
        if ("2".equals(sex)) {
            tvSex.setText("女");
        } else if("1".equals(sex)) {
            tvSex.setText("男");
        }else {
            tvSex.setText("未知");
        }
        tvArea.setText(city);
        tvIntroduction.setText(introduction);
        User bean = UserManager.getInstance().getUser();
        bean.setName(user.getName());
        bean.setSex(user.getSex());
        UserManager.getInstance().updataUser(bean);
        EventBusUtil.post(new EventEditInfoBean( EventEditInfoBean.CHANGENAME));
        EventBusUtil.post(new EventEditInfoBean( EventEditInfoBean.UPDATSEXSTATUS));
    }

    @Override
    public void EditInfoSuccess(Object object) {
        if(mEditInfoDialog != null){
            mEditInfoDialog.dismiss();
        }
        ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), getString(R.string.modify_success));
        mPresenter.getUserInfo(token);
    }
    boolean isFireRegister;
    String picHead = "";
    @Override
    public void showUploadResult(User bean) {
        picHead = bean.getHeadPic();
        if(isFireRegister){
            ImageHelper.loadHeadIcon(this, iv, R.drawable.person_icon_head, bean.getHeadPic());
        }else {
            ImageHelper.loadHeadIcon(this, ivMyAvatar, R.drawable.person_icon_head, bean.getHeadPic());
        }
        isFireRegister = false;
        User user = UserManager.getInstance().getUser();
        user.setHeadPic(bean.getHeadPic());
        UserManager.getInstance().updataUser(user);
        EventBusUtil.post(new EventEditInfoBean(EventEditInfoBean.UPLOADAVATAR));
    }


    @Override
    public void compression(String path) {
        //将图片进行压缩
        final File file = new File(path);
        Luban.get(this).load(file)                     //传人要压缩的图片
             .putGear(Luban.THIRD_GEAR)
             .load(file)
             .launch()
             .asObservable()
             .subscribeOn(Schedulers.newThread())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(new Action1<File>() {
                @Override
                public void call(File compressFile) {
                    if(FileUtil.getSize(file) > maxLen_500k ){
                        Log.d("tag","大于半兆");
                        setFile(compressFile);
                    }else {
                        Log.d("tag","小于半兆");
                        setFile(file);
                    }
                }
        });
    }

    private void setFile(File file){
        if(file == null || !file.exists()) return;
        token = UserManager.getInstance().getToken();
        if(TextUtils.isEmpty(token)){
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            return;
        }
        RequestBody requestBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse("image*//**//*"), file)).build();
        mPresenter.uploadAvatar(requestBody);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
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
    public void showEmpty() {}

    @Override
    public void showReLoad() {
        mPresenter.getUserInfo(token);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    @Override
    public void setPresenter(EditInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Subscribe
    public void onEvent(EventEditInfoBean bean) {
        User user = UserManager.getInstance().getUser();
        tvNikename.setText(user.getName());

    }

    @Subscribe
    public void onEvent(EventIntroBean bean) {
        intro = bean.getIntro();
        tvIntroduction.setText(intro);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        Luban.get(this).setCompressListener(null);
        if("3".equals(UserManager.getInstance().getIsRealAudit())){
            CheaekUtil.getInstance().cancelTask(this);
        }
        EventBusUtil.unregister(this);
        mAlertDialog = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            String status = data.getStringExtra("status");
            if("2".equals(status))
               tvStutus.setText("审核中");
        }
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        //压缩图片并上传
        compression(media.getPath());
    }
}
