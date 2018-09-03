package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.event.EventChooseClassifyBean;
import com.gxtc.huchuan.bean.event.EventDelSeries;
import com.gxtc.huchuan.bean.event.EventIntroBean;
import com.gxtc.huchuan.bean.model.AppenGroudBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.dialog.NumberPickerDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.news.MineArticleActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
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
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Describe: 新建系列课 或 修改系列课
 * Created by ALing on 2017/3/15 .
 */

public class CreateSeriesCourseActivity extends BaseTitleActivity implements View.OnClickListener,
        CreateSeriesCourseContract.View, PictureConfig.OnSelectResultCallback,
        NumberPickerView.OnValueChangeListener {
    private static final String TAG                = CreateSeriesCourseActivity.class.getSimpleName();
    private static       int    REQUEST_CODE_IMAGE = 10000;

    @BindView(R.id.rl_avatar)                PercentRelativeLayout mRlAvatar;
    @BindView(R.id.tv_series_introduce)      TextView              mTvSeriesIntroduce;
    @BindView(R.id.tv_series_classification) TextView              mTvSeriesClassification;
    @BindView(R.id.edit_invite_count)        EditText              mEtInviteCount;
    @BindView(R.id.et_series_name)           EditText              mEtSeriesName;
    @BindView(R.id.et_money)                 EditText              mEtMoney;
    @BindView(R.id.edit_pent)                EditText              editPent;
    @BindView(R.id.iv_series_head)           ImageView             mIvSeriesHead;
    @BindView(R.id.btn_del)                  Button                mBtnDel;
    @BindView(R.id.btn_confirm)              Button                btnConfim;
    @BindView(R.id.tv_series_type1)          TextView              mBtnType;
    @BindView(R.id.tv_charge_type)           TextView              tvChargeType;            //是否收费
    @BindView(R.id.layout_money_area)        LinearLayout          layoutMoneyArea;         //是否收费
    @BindView(R.id.switch_tuiguang)          SwitchCompat          switchSpread;
    @BindView(R.id.listening_test)           SwitchCompat          switchListner;
    @BindView(R.id.layout_spread)            ViewGroup             layoutSpread;
    @BindView(R.id.tuiguang_text)            TextView              tvSpread;
    @BindView(R.id.listening_test_layout)    View                  viewTestLayout;
    @BindView(R.id.layout_invite_count)      View                  layoutInviteCount;
    @BindView(R.id.layout_invite)            View                  layoutInvite;


    private String                  intro;              //系列课简介
    private String                  cover;              //系列课头像
    private String                  isAuditions = "";   //0正常课程  1免费试听  2邀请制免费听课
    private HashMap<String, String> map;
    private String                  token;
    private String                  chatRoomId;
    private String                  typeId;
    private SeriesPageBean          seriesPageBean;
    private String                  mTypeId;
    private String                  id;                 //系列课id
    private AlertDialog             mAlertDialog;
    private PopPosition             mPopPosition;
    private String[]                mTitles;
    private List<ChatInfosBean>     mData;
    private String chatInfoTypeId = "";
    private List<String> listGroupName;
    private AppenGroudBean mAppenGroudBean;

    private boolean isFree   = true;       //true是免费   false是收费
    private boolean isSpread = false;       //true是推广

    //同步圈子的dialog
    IssueListDialog mIssueListDialog;
    ArrayList<Integer> mGroupIds = new ArrayList<>();//同步的圈子id

    private CreateSeriesCourseContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_series_course);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showBackButton(this);
        setPricePoint(mEtMoney);
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        //判断从哪里跳转过来的，如果从系列课主页跳过来，这把整个系列课的数据传过来，否则，是新建系列课

        seriesPageBean = (SeriesPageBean) getIntent().getSerializableExtra("bean");
        if (seriesPageBean != null) {
            //修改标题
            getBaseHeadView().showTitle(getString(R.string.title_modify_series));
            mBtnType.setVisibility(View.VISIBLE);
            id = seriesPageBean.getId();
            isAuditions = seriesPageBean.getIsAuditions();
            switch (seriesPageBean.getIsAuditions()){
                case "0":
                    onValueChange(null, 0, 0);
                    switchListner.setChecked(false);
                    break;
                case SeriesActivity.AUDITION_TYPE:
                    onValueChange(null, 0, 1);
                    switchListner.setChecked(true);
                    break;

                case SeriesActivity.AUDITION_INVITE_TYPE:
                    onValueChange(null, 0, 2);
                    break;
            }
            //显示删除按钮
            mBtnDel.setVisibility(View.VISIBLE);
            mTypeId = seriesPageBean.getType();                                 //系列课分类id
            ImageHelper.loadImage(CreateSeriesCourseActivity.this, mIvSeriesHead, seriesPageBean.getHeadpic());
            mEtSeriesName.setText(seriesPageBean.getSeriesname());
            mEtSeriesName.setSelection(seriesPageBean.getSeriesname().length());//将光标移至文字末尾
            mTvSeriesIntroduce.setText(seriesPageBean.getIntroduce());
            mTvSeriesClassification.setText(seriesPageBean.getTypeName());
            mEtMoney.setText(seriesPageBean.getFee());
            mEtMoney.setSelection(mEtMoney.getText().length());
            editPent.setText(seriesPageBean.getPent() + "");
            editPent.setSelection(editPent.getText().length());

            layoutSpread.setVisibility(View.GONE);
            tvSpread.setVisibility(View.GONE);

            if(!SeriesActivity.AUDITION_INVITE_TYPE.equals(seriesPageBean.getIsAuditions()) && (TextUtils.isEmpty(seriesPageBean.getFee()) || "0".equals(seriesPageBean.getFee()))){
                layoutMoneyArea.setVisibility(View.GONE);
                isFree = true;
                tvChargeType.setText("免费");
            }


        } else {
            //修改标题
            getBaseHeadView().showTitle(getString(R.string.title_create_series));
            switchListner.setChecked(false);
            mBtnType.setVisibility(View.VISIBLE);
            layoutInvite.setVisibility(View.GONE);
            layoutMoneyArea.setVisibility(View.GONE);
        }
        new CreateSeriesCoursePresenter(this);
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
        } else {
            GotoUtil.goToActivity(CreateSeriesCourseActivity.this, LoginAndRegisteActivity.class);
        }

        mPopPosition = new PopPosition(this, R.layout.pop_ts_position);
        getTypeData();
    }

    @Override
    public void initListener() {
        switchSpread.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSpread = isChecked;
            }
        });
        switchListner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAuditions = isChecked ? "1" : "0";
            }
        });
    }

    @OnClick({R.id.btn_confirm,
            R.id.rl_avatar,
            R.id.rl_series_introduce,
            R.id.rl_series_classification,
            R.id.btn_del,
            R.id.tv_series_type1,
            R.id.tv_charge_type})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //系列课头像
            case R.id.rl_avatar:
                chooseImg();
                break;

            //系列课简介
            case R.id.rl_series_introduce:
                gotoIntro();
                break;

            //系列课分类
            case R.id.rl_series_classification:
                if (seriesPageBean != null) {
                    map = new HashMap<>();
                    map.put("typeId", mTypeId);
                    GotoUtil.goToActivityWithData(this, ChooseSeriesClassifyActivity.class, map);
                } else {
                    GotoUtil.goToActivity(this, ChooseSeriesClassifyActivity.class);
                }

                break;

            //确定
            case R.id.btn_confirm:
                showDialog();
                break;

            //删除
            case R.id.btn_del:
                showDelDialog();
                break;

            case R.id.tv_series_type1:
                showTypePop();
                break;

            //收费类型
            case R.id.tv_charge_type:
                showChargeDialog();
                break;
        }
    }

    //选择收费类型
    private NumberPickerDialog chargeDialog;
    private String[] Charges = { "免费", "收费", "邀请制免费"};

    private void showChargeDialog() {
        if (chargeDialog == null) {
            chargeDialog = new NumberPickerDialog("收费类型", NumberPickerDialog.convertData(isFree ? 1 : 0, Charges));
            chargeDialog.setOnValueChangeListener(this);
        }
        chargeDialog.show(this);
    }

    //收费、免费切换换掉
    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnConfim.getLayoutParams();
        switchListner.setChecked(false);
        //免费
        if (newVal == 0) {
            viewTestLayout.setVisibility(View.GONE);
            layoutMoneyArea.setVisibility(View.GONE);
            layoutInvite.setVisibility(View.GONE);
            isAuditions = "0";
            isFree = true;
            params.topMargin = (int) getResources().getDimension(R.dimen.margin_larger);

        //收费
        } else if (newVal == 1) {
            viewTestLayout.setVisibility(View.VISIBLE);
            layoutMoneyArea.setVisibility(View.VISIBLE);
            layoutInvite.setVisibility(View.GONE);
            isAuditions = "0";
            isFree = false;
            params.topMargin = (int) getResources().getDimension(R.dimen.margin_middle);

        //邀请制免费
        } else{
            isFree = true;
            isAuditions = "2";
            layoutMoneyArea.setVisibility(View.GONE);
            layoutInvite.setVisibility(View.VISIBLE);
        }
        tvChargeType.setText(Charges[newVal]);
    }



    /**
     * 删除系列课
     */
    private AlertDialog delDialog;

    private void showDelDialog() {
        delDialog = DialogUtil.showInputDialog(this, false, "", "确认删除系列课？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        map = new HashMap<>();
                        map.put("token", UserManager.getInstance().getToken());
                        map.put("id", id);
                        mPresenter.delSeries(map);
                        delDialog.dismiss();
                    }
                });
    }

    /**
     * 选择系列课类型
     */
    private void showTypePop() {
        WindowUtil.closeInputMethod(this);
        if (mTitles != null && mTitles.length > 0) {
            mPopPosition.setData(mTitles);
        }

        mPopPosition.setTitle("选择分类");
        mPopPosition.showPopOnRootView(this);
        mPopPosition.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                if (mTitles != null && newVal < mTitles.length) {
                    chatInfoTypeId = mData.get(newVal).getId();
                    mBtnType.setText(mTitles[newVal]);
                }
            }
        });
    }

    private void getTypeData() {
        Subscription sub = AllApi.getInstance().getLiveType().observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(
                        new ApiCallBack<List<ChatInfosBean>>() {
                            @Override
                            public void onSuccess(List<ChatInfosBean> data) {
                                if (data != null && data.size() > 0) {
                                    mData = data;
                                    mTitles = new String[data.size()];
                                    for (int i = 0; i < data.size(); i++) {
                                        mTitles[i] = data.get(i).getTypeName();
                                    }
                                }
                                mPopPosition.setData(mTitles);
                                mPopPosition.setTitle("选择分类");
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                getBaseLoadingView().hideLoading();
                                ToastUtil.showShort(MyApplication.getInstance(), message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private void gotoIntro() {
        if (seriesPageBean != null) {
            seriesPageBean.getIntroduce();
            map = new HashMap<>();
            map.put("editIntro", seriesPageBean.getIntroduce());
            GotoUtil.goToActivityWithData(this, SeriesCourseIntroduceActivity.class, map);
        } else {
            GotoUtil.goToActivity(this, SeriesCourseIntroduceActivity.class);
        }
    }


    private void createSeries() {
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(mEtSeriesName.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.seriesname_cnnot_empty));
            return;
        }

        //判断是修改系列课或新建系列课
        if (seriesPageBean != null) {
            //修改
            map = new HashMap<>();
            map.put("token", token);
            map.put("chatRoom", chatRoomId);
            map.put("seriesname", mEtSeriesName.getText().toString());     //系列课名称

            if (isFree) {
                map.put("fee", "0");
            } else {
                map.put("fee", mEtMoney.getText().toString());
                if (!TextUtils.isEmpty(editPent.getText().toString())) {
                        float pent = Float.valueOf(editPent.getText().toString());
                        if (pent < 5) {
                            ToastUtil.showShort(this, "分销比例不得低于 5%");
                            return;

                        } else if (pent > 70) {
                            ToastUtil.showShort(this, "分销比例不得高于 70%");
                            return;

                        } else {
                            map.put("pent", editPent.getText().toString());
                        }
               }
            }

            if (cover != null) {
                map.put("headpic", cover);
            }

            if (intro != null) {
                map.put("introduce", intro);       //系列课简介
            }

            if (typeId != null) {
                map.put("type", typeId);           //系列课分类ID,从系列课分类传过来
            }

            //开启免费试听
            if(switchListner.isChecked()){
                map.put("isAuditions", "1");

                //开启邀请制免费听课
            } else if("2".equals(isAuditions)){
                map.put("isAuditions", "2");
                if(!TextUtils.isEmpty(mEtInviteCount.getText())){
                    map.put("invitationFreeNum", mEtInviteCount.getText().toString());
                }else{
                    ToastUtil.showShort(this, "请输入邀请人数");
                    return;
                }

            } else {
                map.put("isAuditions", "0");
            }

            if(!TextUtils.isEmpty(seriesPageBean.getChatInfoTypeId()))
            map.put("chatInfoTypeId", seriesPageBean.getChatInfoTypeId());
            map.put("id", seriesPageBean.getId());     //系列课ID，修改时传
            if (mGroupIds != null && mGroupIds.size() > 0) {
                String groupids = mGroupIds.toString().substring(1,
                        mGroupIds.toString().length() - 1);
                map.put("groupIds", groupids);
            }
            mPresenter.createLiveSeries(map);

        //创建
        } else {
            if (TextUtils.isEmpty(chatInfoTypeId)) {
                ToastUtil.showShort(this, "请选择课程类型");
                return;
            }

            if (TextUtils.isEmpty(isAuditions)) {
                ToastUtil.showShort(this, "请选择收费类型");
                return;
            }

            map = new HashMap<>();
            map.put("token", token);
            map.put("chatRoom", chatRoomId);
            map.put("seriesname", mEtSeriesName.getText().toString());     //系列课名称
            map.put("chatInfoTypeId", chatInfoTypeId);

            //是否收费
            if (isFree) {
                map.put("fee", "0");
            } else {
                if (TextUtils.isEmpty(mEtMoney.getText().toString())) {
                    ToastUtil.showShort(this, "请输入系列课价格");
                    return;
                }
                map.put("fee", mEtMoney.getText().toString());
                // 分销比可以不设置，设置则传，不设置则不传
                if(!TextUtils.isEmpty(editPent.getText().toString().trim())) {
                 float pent = Float.valueOf(editPent.getText().toString());
                  if (pent < 5) {
                      ToastUtil.showShort(this, "分销比例不得低于 5%");
                      return;
                   } else if (pent > 70) {
                      ToastUtil.showShort(this, "分销比例不得高于 70%");
                       return;
                   } else {
                       map.put("pent", editPent.getText().toString());
                   }
                  }
                }

            //是否推广
            map.put("isSpread", isSpread ? "1" : "0");

            //开启免费试听
            if(switchListner.isChecked()){
                map.put("isAuditions", "1");

            //开启邀请制免费听课
            } else if("2".equals(isAuditions)){
                map.put("isAuditions", "2");
                if(!TextUtils.isEmpty(mEtInviteCount.getText())){
                    map.put("invitationFreeNum", mEtInviteCount.getText().toString());
                }else{
                    ToastUtil.showShort(this, "请输入邀请人数");
                    return;
                }

            } else {
                map.put("isAuditions", "0");
            }

            if (cover != null) {
                map.put("headpic", cover);
            }
            if (intro != null) {
                map.put("introduce", intro);       //系列课简介
            }
            if (typeId != null) {
                map.put("type", typeId);           //系列课分类ID,从系列课分类传过来
            }
            if (mGroupIds != null && mGroupIds.size() > 0) {
                String groupids = mGroupIds.toString().substring(1,
                        mGroupIds.toString().length() - 1);
                map.put("groupIds", groupids);
            }
            mPresenter.createLiveSeries(map);
        }

    }

    //选择照片
    private void chooseImg() {
        String[] pers = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, 1011, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
                int height = (int) getResources().getDimension(R.dimen.px500dp);
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
                                .setMaxHeight(height)
                                .create();
                PictureConfig.getInstance().init(options).openPhoto(CreateSeriesCourseActivity.this, CreateSeriesCourseActivity.this);
            }

            @Override
            public void onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(CreateSeriesCourseActivity.this, false,
                        null, getString(R.string.pre_scan_notice_msg), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(
                                            CreateSeriesCourseActivity.this);
                                }
                                mAlertDialog.dismiss();
                            }
                        });


            }
        });
    }

    public void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mAlertDialog = null;
        mPresenter.destroy();
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    //系列课创建成功，跳到系列课主页  传系列课id
    @Override
    public void createLiveSeriesResult(SeriesPageBean bean) {
        if (seriesPageBean != null) {
            //修改
            ToastUtil.showShort(this, getString(R.string.modify_success));


            Intent intent = getIntent();
            intent.putExtra("seriesBean", bean);
            setResult(RESULT_OK, intent);
//            SeriesActivity.startActivity(this,bean.getId());
            finish();
        } else {
            //创建
            Log.d(TAG, "createLiveSeriesResult: " + bean.toString());
            ToastUtil.showShort(this, getString(R.string.toast_create_success));

            SeriesActivity.startActivity(this, bean.getId());
            finish();
        }
        EventBusUtil.post(new EventDelSeries(id));
        sysGroup(bean);
    }

    @Override
    public void showChatSeriesTypeList(List<ChooseClassifyBean> bean) {}

    @Override
    public void showAddSeriesClassify(List<ChooseClassifyBean> bean) {

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
        ImageHelper.loadImage(CreateSeriesCourseActivity.this, mIvSeriesHead, url);
    }

    @Override
    public void showDelSeries(Object object) {
        ToastUtil.showShort(this, getString(R.string.toast_del_series_success));
        EventBusUtil.post(new EventDelSeries(id));
        this.finish();
    }

    @Override
    public void setPresenter(CreateSeriesCourseContract.Presenter presenter) {
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
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {

    }

    @Subscribe
    public void onEvent(EventChooseClassifyBean bean) {
        Log.d(TAG, "onEvent: " + bean.getTypeId());
        typeId = bean.getTypeId();
        mTvSeriesClassification.setText(bean.getTypeName());
    }

    @Subscribe
    public void onEvent(EventIntroBean bean) {
        Log.d(TAG, "onEvent: " + bean.getIntro());
        intro = bean.getIntro();
        mTvSeriesIntroduce.setText(intro);
    }

    private void showDialog() {
        if(TextUtils.isEmpty(mEtSeriesName.getText().toString())){
            ToastUtil.showShort(this, "系列课名称不能为空");
            return;
        }

        if (TextUtils.isEmpty(chatInfoTypeId)) {
            ToastUtil.showShort(this, "请选择课程类型");
            return;
        }

        if (TextUtils.isEmpty(isAuditions)) {
            ToastUtil.showShort(this, "请选择收费类型");
            return;
        }

        if (!isFree) {
            if (TextUtils.isEmpty(mEtMoney.getText().toString())) {
                ToastUtil.showShort(this, "请输入系列课价格");
                return;
            }
            map = new HashMap<>();
            map.put("fee", mEtMoney.getText().toString());
            //分销比设置则传，不设置则不传
            if(!TextUtils.isEmpty(editPent.getText().toString().trim())) {
                float pent = Float.valueOf(editPent.getText().toString());
                if (pent < 5) {
                    ToastUtil.showShort(this, "分销比例不得低于 5%");
                    return;
                } else if (pent > 70) {
                    ToastUtil.showShort(this, "分销比例不得高于 70%");
                    return;
                } else {
                    map.put("pent", editPent.getText().toString());
                }
            }
        }

        if ("2".equals(isAuditions) && TextUtils.isEmpty(mEtInviteCount.getText())) {
            ToastUtil.showShort(this, "请输入邀请人数");
            return;
        }
        if(seriesPageBean != null)
           getlistAppendGroup(seriesPageBean,true);
        if (mIssueListDialog == null) {
            mIssueListDialog = new IssueListDialog(this, new String[]{});
            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(CreateSeriesCourseActivity.this, SyncIssueInCircleActivity.class);
                        intent.putExtra("default", id);
                        intent.putExtra("isClass", true);
                        if(seriesPageBean != null) {
                            intent.putExtra("type", 2);
                            intent.putExtra("linkId", seriesPageBean.getId());
                            intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mAppenGroudBean.getGroupIds());
                        }else{
                            intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mGroupIds);
                        }
                        startActivityForResult(intent, 666);
                    } else if (v.getId() == R.id.tv_sure) {
                        createSeries();
                    }
                }
            });
        }
        mIssueListDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            if(seriesPageBean != null)
              getlistAppendGroup(seriesPageBean,false);
            ArrayList<MineCircleBean> selectData = data.getParcelableArrayListExtra("select_data");
            mGroupIds.clear();
            if (selectData.size() > 0) {
                ArrayList<String> listGroupName = new ArrayList<>();
                for (MineCircleBean bean1 : selectData) {
                    listGroupName.add(bean1.getGroupName());
                    mGroupIds.add(bean1.getId());
                }
                mIssueListDialog.changeTongbuName(listGroupName);
            }

        }

    }

    public void getlistAppendGroup(final SeriesPageBean bean, final boolean flag) {
        HashMap<String, String> map = new HashMap<>();
        map.put("linkId", bean.getId());
        map.put("type", "6");//同步类型1、文章；2、课堂；3、文件
        MineApi.getInstance().listAppendGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<AppenGroudBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mAppenGroudBean = (AppenGroudBean) data;
                        if(mAppenGroudBean.getGroupIds().size() > 0)
                            getCircleName(flag);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(CreateSeriesCourseActivity.this, message);
                    }
                }));

    }
    private void getCircleName(final boolean flag){
        Subscription sub =
                AllApi.getInstance()
                        .listByUser(UserManager.getInstance().getToken(), "0")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                                if(flag) {
                                    listGroupName = new ArrayList<>();
                                    for (MineCircleBean bean : (List<MineCircleBean>) data) {
                                        if (mAppenGroudBean.getGroupIds().contains(bean.getId())) {
                                            listGroupName.add(bean.getGroupName());
                                        }
                                    }
                                    mIssueListDialog.changeTongbuName(listGroupName);
                                }

                            }

                            @Override
                            public void onError(String errorCode, String message) {}
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);

    }

    //同步文章到圈子 同步类型1、文章；2、课堂；3、文件
    private void sysGroup(SeriesPageBean bean ) {
        if (mGroupIds == null || mGroupIds.size() < 1) return;
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
        map.put("groupIds", groupids);
        map.put("linkId", bean.getId());
        map.put("type" , "6");

        Subscription sub = MineApi.getInstance().appendToGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
//                        ToastUtil.showShort(CreateSeriesCourseActivity.this, "同步成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(CreateSeriesCourseActivity.this, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {

    }

    @Override
    public void onSelectSuccess(LocalMedia media) {
        mPresenter.compressImg(media.getPath());
    }

}
