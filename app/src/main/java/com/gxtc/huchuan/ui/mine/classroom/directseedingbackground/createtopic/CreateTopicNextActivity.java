package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.widget.MultiRadioGroupV2;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe:新建课程下一步
 * Created by ALing on 2017/3/16 .
 */

public class CreateTopicNextActivity extends BaseTitleActivity implements View.OnClickListener,
        MultiRadioGroupV2.OnCheckedChangeListener, CreateTopicContract.View {
    private static final String TAG = CreateTopicActivity.class.getSimpleName();

    @BindView(R.id.sw_intro)        Switch            mSwIntro;
    @BindView(R.id.et_topic_pws)    TextInputEditText mEtTopicPws;
    @BindView(R.id.et_topic_fee)    TextInputEditText mEtTopicFee;
    @BindView(R.id.btn_finish)      Button            mBtnFinish;
    @BindView(R.id.rb_public)       RadioButton       mRbPublic;
    @BindView(R.id.rb_private)      RadioButton       mRbPrivate;
    @BindView(R.id.rb_charge)       RadioButton       mRbCharge;
    @BindView(R.id.radioGroup)      MultiRadioGroupV2 mRadioGroup;
    @BindView(R.id.et_proportion)   TextInputEditText mEtProportion;
    @BindView(R.id.tuiguang_text)   TextView          tvTuiguangText;
    @BindView(R.id.switch_tuiguang) SwitchCompat      mSwitchCompat;
    @BindView(R.id.free_layout)     LinearLayout      freeLayoutExplain;
    @BindView(R.id.pwd_layout)      LinearLayout      pwdLayout;
    @BindView(R.id.no_free_layout)  LinearLayout      noFreeLayout;
    @BindView(R.id.persent_layout)  LinearLayout      persentLayout;

    private boolean isCheck;
    private String  subtitle;    //课程主题
    private String  starttime;   //开始时间
    private String  chatway;     //课堂形式
    //    private String chatTypeSonId;      //课堂子类型ID
    private String  token;
    private String chattype = "0";    //课堂类型
    private String id;          //课堂间ID
    private String chatSeries;  //系列课程id
    private String isSpread;  //0、不需要平台推广；1、需要平台推广

    private String facePic;             //课堂封面
    private String mainSpeaker;         //主讲人名字
    private String speakerIntroduce;    //主讲人介绍
    private String desc;                //课程简介
    private String videoPic;                //视频封面
    private String videoText;                //视频链接

    private CreateTopicContract.Presenter mPresenter;
    private int                           mGroupId;
    private ArrayList<Integer> mGroupIds      = new ArrayList<>();//同步的圈子id
    public  String             chatInfoTypeId = "";
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic_next);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_create_topic));
        getBaseHeadView().showBackButton(this);
        mRadioGroup.setOnCheckedChangeListener(this);

    }

    @Override
    public void initData() {
        super.initData();
        new CreateTopicPresenter(this);
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            id = UserManager.getInstance().getUser().getChatRoomId();
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
        subtitle = getIntent().getStringExtra("subtitle");
        starttime = getIntent().getStringExtra("starttime");
        chatway = getIntent().getStringExtra("chatway");
        chatSeries = getIntent().getStringExtra("chatSeries");
        chatInfoTypeId = getIntent().getStringExtra("chatInfoTypeId");

        facePic = getIntent().getStringExtra("facePic");
        mainSpeaker = getIntent().getStringExtra("mainSpeaker");
        speakerIntroduce = getIntent().getStringExtra("speakerIntroduce");
        videoPic = getIntent().getStringExtra("videoPic");
        videoText = getIntent().getStringExtra("videoText");
        desc = getIntent().getStringExtra("desc");

        String g = getIntent().getStringExtra("groupIds");
        mGroupId = Integer.valueOf(g);

        onCheckedChanged(mRadioGroup, R.id.rb_public);
    }

    @Override
    public void initListener() {
        super.initListener();
        mEtTopicPws.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnFinish.setEnabled(true);
                } else {
                    mBtnFinish.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtTopicFee.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index   = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});


        mEtTopicFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnFinish.setEnabled(true);
                } else {
                    mBtnFinish.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSpread = "1";
                } else {
                    isSpread = "0";
                }
            }
        });

        mSwitchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat switchCompat = (SwitchCompat) v;
                if (switchCompat.isChecked()) {
                    isSpread = "1";
                } else {
                    isSpread = "0";
                }
            }
        });
    }

    @OnClick({R.id.sw_intro, R.id.btn_finish, R.id.btn_savedraft})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //开启介绍页  默认是开启的
            case R.id.sw_intro:

                break;

            //申请上线
            case R.id.btn_finish:
                showDialog();
                break;

            //保存草稿
            case R.id.btn_savedraft:

                break;

        }
    }

    private void createFinish() {
        WindowUtil.closeInputMethod(this);
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("chatRoom", id);
        map.put("subtitle", subtitle);
        map.put("starttime", starttime);
        map.put("chatway", chatway);
        map.put("chattype", chattype);
        map.put("chatInfoTypeId", chatInfoTypeId);
        map.put("isPublish", STRING_PUBLIC_STATUS);

        map.put("facePic", facePic);                    //课程封面
        map.put("mainSpeaker", mainSpeaker);            //主讲人名字
        map.put("speakerIntroduce", speakerIntroduce);  //主讲人介绍
        map.put("desc", desc);                          //课程简介
        if (!TextUtils.isEmpty(videoPic) && !TextUtils.isEmpty(videoText)) {
            map.put("videoPic", videoPic);                          //视频封面
            map.put("videoText", videoText);                          //视频链接
        }
        if (isSpread != null) map.put("isSpread", isSpread);//0、不需要平台推广；1、需要平台推广
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        } else {
            map.put("groupIds", String.valueOf(mGroupId));
        }
        if (chatSeries != null) map.put("chatSeries", chatSeries);

        if ("1".equals(chattype)) {
            if (TextUtils.isEmpty(mEtTopicPws.getText().toString())) {
                ToastUtil.showShort(this, "请设置密码");
                return;
            }
            if (mEtTopicPws.getText().toString().length() < 6) {
                ToastUtil.showShort(this, "密码长度不能少于6位");
                return;
            }
            map.put("password", mEtTopicPws.getText().toString());
        } else if ("2".equals(chattype)) {
            if (TextUtils.isEmpty(mEtTopicFee.getText().toString().trim())) {
                ToastUtil.showShort(this, "请输入价格");
                return;
            }
            if ((StringUtil.toDouble(mEtTopicFee.getText().toString().trim()) < 1)) {
                ToastUtil.showShort(this, "输入金额至少1元");
                return;
            }

            if ((StringUtil.toDouble(mEtTopicFee.getText().toString().trim()) > 9999999)) {
                ToastUtil.showShort(this, "输入金额不得大于9999999");
                return;
            }
            map.put("fee", mEtTopicFee.getText().toString());


            if (!TextUtils.isEmpty(mEtProportion.getText().toString())) {
                String s = mEtProportion.getText().toString();
                int    i = Integer.valueOf(s).intValue();
                if (i >= 5 && i <= 70) {
                    map.put("pent", mEtProportion.getText().toString());
                } else {
                    ToastUtil.showShort(this, "请设置正确分成比例（5~70)");
                    return;
                }
            } else {
                map.put("pent", "0");
            }
        }
        Log.d("CreateTopicNextActivity", "createFinish: " + map.toString());
        mPresenter.createLiveTopic(map);
    }

    @Override
    public void onCheckedChanged(MultiRadioGroupV2 group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_public:
                chattype = "0";
                freeLayoutExplain.setVisibility(View.VISIBLE);
                pwdLayout.setVisibility(View.GONE);
                noFreeLayout.setVisibility(View.GONE);
                persentLayout.setVisibility(View.GONE);
                mEtTopicPws.setEnabled(false);
                mEtTopicFee.setEnabled(false);
                mBtnFinish.setEnabled(true);
                break;
            case R.id.rb_private:
                freeLayoutExplain.setVisibility(View.GONE);
                pwdLayout.setVisibility(View.VISIBLE);
                noFreeLayout.setVisibility(View.GONE);
                persentLayout.setVisibility(View.GONE);
                chattype = "1";
                mEtTopicFee.setText("");
                mEtTopicPws.setEnabled(true);
                mEtTopicPws.requestFocus();
                mEtTopicFee.setEnabled(false);
                mEtTopicFee.clearFocus();
                break;
            case R.id.rb_charge:
                freeLayoutExplain.setVisibility(View.GONE);
                pwdLayout.setVisibility(View.GONE);
                persentLayout.setVisibility(View.VISIBLE);
                noFreeLayout.setVisibility(View.VISIBLE);
                chattype = "2";
                mEtTopicPws.setText("");
                mEtTopicFee.setEnabled(true);
                mEtTopicFee.requestFocus();
                mEtTopicPws.setEnabled(false);
                mEtTopicPws.clearFocus();
                break;
        }
    }

    //同步圈子的dialog
    IssueListDialog mIssueListDialog;
    public static String STRING_PUBLIC_STATUS = "0"; //0：公开，1：私密

    private void showDialog() {
        if (mIssueListDialog == null) {
            mIssueListDialog = new IssueListDialog(this, new String[]{"公开", "仅圈内显示"});
            mIssueListDialog.setmOnItemClickListener(new IssueListDialog.OnItemClickListener() {
                @Override
                public void selectByPosition(int position) {
                    if (position == 0) {
                        STRING_PUBLIC_STATUS = "0";
                    } else if (position == 1) {
                        STRING_PUBLIC_STATUS = "1";
                    }
                }
            });

            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(CreateTopicNextActivity.this,
                                SyncIssueInCircleActivity.class);
                        intent.putExtra("isClass", true);
                        intent.putExtra("default", id);
                        intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mGroupIds);
                        CreateTopicNextActivity.this.startActivityForResult(intent, 666);

                    } else if (v.getId() == R.id.tv_sure) {
                        createFinish();
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

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void createLiveResult(final CreateLiveTopicBean bean) {
        //公开课程需要审核
        if ("0".equals(STRING_PUBLIC_STATUS)) {
            mAlertDialog = DialogUtil.showDeportDialog(this, true, "创建课程成功",
                    getResources().getString(R.string.title_notice_message),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBusUtil.post(new CreateLiveTopicBean());
                            mAlertDialog.dismiss();
                            finish();
                            LiveIntroActivity.startActivity(CreateTopicNextActivity.this,
                                    bean.getId());//这里 要传的是 课程ID  不是课堂间id
                        }
                    });
        } else {
            //私密课程不需要审核
            ToastUtil.showShort(this, "创建课程成功");
            EventBusUtil.post(new CreateLiveTopicBean());
            finish();
            LiveIntroActivity.startActivity(CreateTopicNextActivity.this,
                    bean.getId());//这里 要传的是 课程ID  不是课堂间id
        }
    }

    @Override
    public void setPresenter(CreateTopicContract.Presenter presenter) {
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

    }

    @Override
    public void showReLoad() {
        //        mPresenter.createLiveTopic(map);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
