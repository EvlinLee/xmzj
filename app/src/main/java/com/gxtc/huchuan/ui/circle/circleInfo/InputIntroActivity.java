package com.gxtc.huchuan.ui.circle.circleInfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 输入简介
 */
public class InputIntroActivity extends BaseTitleActivity implements View.OnClickListener,
        CircleInfoContract.View {

    private static final String TAG = InputIntroActivity.class.getSimpleName();
    @BindView(R.id.edit_content) EditText editContent;

    private CircleBean                   bean;
    private int                          mGroupID;
    private String                       mGroupName;
    private CircleInfoContract.Presenter mPresenter;
    private HashMap<String, Object>      map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_intro);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_circle_intro));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("保存", this);
    }

    @Override
    public void initData() {
        super.initData();
        new CircleInfoPresenter(this);
        map = new HashMap<>();
        bean = (CircleBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        if (bean != null) {
            mGroupID = bean.getId();
            mGroupName = bean.getName();
            Log.d(TAG, "initData: " + mGroupID + ",groupName:" + mGroupName);
        }

    }

    @Override
    public void onClick(View v) {
        WindowUtil.closeInputMethod(this);
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                save();
                break;
        }
    }

    private void save() {
        String content = editContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "内容不能为空");
            return;
        } else {
            map.put("token", UserManager.getInstance().getToken());
            map.put("id", mGroupID);
            map.put("groupName", mGroupName);
            map.put("content", editContent.getText().toString());
            mPresenter.editCircleInfo(map);
        }

        /*Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_DATA,content);
        setResult(101,intent);
        finish();*/
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showMemberList(List<CircleMemberBean> datas) {}

    @Override
    public void showRefreshFinish(List<CircleMemberBean> datas) {}

    @Override
    public void showLoadMore(List<CircleMemberBean> datas) {}

    @Override
    public void showNoMore() {}

    @Override
    public void showCompressSuccess(File file) {}

    @Override
    public void showCompressFailure() {}

    @Override
    public void showUploadingSuccess(String url) {}

    @Override
    public void showCircleInfo(CircleBean bean) {}

    @Override
    public void showEditCircle(Object o) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        EventBusUtil.post(new EventCircleIntro(editContent.getText().toString()));
        this.finish();
    }

    //---------------- 在圈子成员管理用到的


    @Override
    public void removeMember(CircleMemberBean circleMemberBean) {

    }

    @Override
    public void transCircle(CircleMemberBean circleMemberBean) {

    }

    @Override
    public void showChangeMemberTpye(CircleMemberBean circleMemberBean) {

    }

    //---------------- 在圈子成员管理用到的


    @Override
    public void setPresenter(CircleInfoContract.Presenter presenter) {
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
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) mPresenter.destroy();
    }
}
