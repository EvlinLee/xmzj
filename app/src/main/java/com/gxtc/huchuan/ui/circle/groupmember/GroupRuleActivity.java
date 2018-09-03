package com.gxtc.huchuan.ui.circle.groupmember;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.GroupRuleDialog;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.HashMap;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/6/9 .
 */

public class GroupRuleActivity extends BaseTitleActivity implements View.OnClickListener,
        GroupRuleContract.View {
    private static final String TAG = GroupRuleActivity.class.getSimpleName();
    @BindView(R.id.et_input_rule)   EditText       mEtInputRule;
    @BindView(R.id.cb_post_redpack) CheckBox       mCbPostRedpack;
    @BindView(R.id.cb_post_rule) CheckBox       mCbPostRule;
    @BindView(R.id.rl_post_redpack) RelativeLayout mRlPostRedPack;
    @BindView(R.id.rl_post_rule)    RelativeLayout    mRlRule;

    private GroupRuleContract.Presenter mPresenter;
    private HashMap<String, Object>     map;
    private int                         groupId;    //圈子id
    private String                      id;         //群规id
    private String                      isMy;
    private int                         isRedBag;
    private int                         isSendRule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_rule);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_group_rule));
        getBaseHeadView().showBackButton(this);
        if (getIntent().getIntExtra("groupId", 0) != 0) {
            groupId = getIntent().getIntExtra("groupId", 0);
            isMy = getIntent().getStringExtra("isMy");
        }
        //  群主可编辑群规
        if ("1".equals(isMy) || "2".equals(isMy) ) {
            getBaseHeadView().showHeadRightButton(getString(R.string.label_save), this);
        } else {
            getBaseHeadView().hideHeadRightButton();
            mRlPostRedPack.setVisibility(View.GONE);
            mEtInputRule.setEnabled(false);
        }
    }

    @Override
    public void initData() {
        new GroupRulePresenter(this);
        mPresenter.getGroupRule(Integer.valueOf(groupId));
    }

    @Override
    public void initListener() {
        super.initListener();
        mCbPostRedpack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRedBag = 1;
                } else {
                    isRedBag = 0;
                }
            }
        });
        mCbPostRule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSendRule = 1;
                } else {
                    isSendRule = 0;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headRightButton:
                save();
                break;
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    //保存群规
    private void save() {
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(mEtInputRule.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_group_rule));
        } else {
            if (TextUtils.isEmpty(id)) {
                //新建
                map = new HashMap<>();
                map.put("token", UserManager.getInstance().getToken());
                map.put("groupId", groupId);
                map.put("roletext", mEtInputRule.getText().toString());
                map.put("isRedBag", isRedBag);
                map.put("isSendRule", String.valueOf(isSendRule));
                mPresenter.saveGroupRule(map);

            } else {
                //修改
                map = new HashMap<>();
                map.put("token", UserManager.getInstance().getToken());
                map.put("groupId", groupId);
                map.put("roletext", mEtInputRule.getText().toString());
                map.put("isSendRule", String.valueOf(isSendRule));
                map.put("id", id);
                map.put("isRedBag", isRedBag);
                mPresenter.saveGroupRule(map);
            }
        }
    }

    private GroupRuleDialog showOpenDialog(Context context, GroupRuleBean data) {
        GroupRuleDialog mOpenDialog = new GroupRuleDialog(context, data);
        mOpenDialog.show();
        return mOpenDialog;
    }

    @Override
    public void showGroupRule(GroupRuleBean data) {
        if (data != null) {
            id = data.getId();
            mEtInputRule.setText(data.getRoletext());
            mEtInputRule.setSelection(mEtInputRule.length());

            if (!TextUtils.isEmpty(data.getRoletext())) {
                showOpenDialog(this, data);
            }

            //没创建群聊
            if("1".equals(data.getCreateGroupChat())){
                mRlRule.setVisibility(View.GONE);
                mRlPostRedPack.setVisibility(View.GONE);
            }

            if("0".equals(data.getCreateGroupChat())){
                mRlRule.setVisibility(View.VISIBLE);
                mRlPostRedPack.setVisibility(View.VISIBLE);
                mCbPostRule.setChecked("1".equals(data.getIsSendRule()) ? true:false );
                mCbPostRedpack.setChecked("1".equals(data.getIsRedbag()));
            }
        }
    }

    @Override
    public void showSaveGroupRule(GroupRuleBean data) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        EventBusUtil.post(new GroupRuleBean(mEtInputRule.getText().toString()));
        finish();
    }

    @Override
    public void setPresenter(GroupRuleContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {

    }

    @Override
    public void showEmpty() {

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
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
