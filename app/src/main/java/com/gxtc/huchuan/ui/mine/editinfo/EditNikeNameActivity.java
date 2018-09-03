package com.gxtc.huchuan.ui.mine.editinfo;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.data.UserManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ALing on 2017/2/23.
 * 编辑昵称
 */

public class EditNikeNameActivity extends BaseTitleActivity implements EditInfoContract.View {
    @BindView(R.id.et_nike_name)
    TextInputEditText mEtNikeName;
    @BindView(R.id.iv_delete)
    ImageView mIvDelete;

    private EditInfoContract.Presenter mPresenter;
    private String mNikeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nikename);
        initData();
    }

    @Override
    public void initData() {
        super.initData();
        getBaseHeadView().showTitle(getString(R.string.title_edit_nikename));
        getBaseHeadView().showCancelBackButton(getString(R.string.label_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightButton(getString(R.string.label_save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nikeNameSave();
            }
        });
        new EditInfoPrenster(this);
        mEtNikeName.addTextChangedListener(new EditTextWatcher());
    }

    @OnClick({R.id.iv_delete})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_delete:
                mEtNikeName.setText("");
                break;
        }
    }

    private void nikeNameSave() {
        if (TextUtils.isEmpty(mEtNikeName.getText().toString().trim())){
            ToastUtil.showShort(this,getString(R.string.tusi_nikename_canot_empty));
        } else if (mEtNikeName.getText().toString().length() > 10){
            ToastUtil.showShort(this,"昵称字数不能大于8");
        }else {
            mNikeName = mEtNikeName.getText().toString();
            HashMap<String,String> map = new HashMap<String, String>();
            map.put("name",mNikeName);
            map.put("token", UserManager.getInstance().getToken());
            mPresenter.getEditInfo(map);
        }
    }

    @Override
    public void getUserSuccess(User object) {

    }

    @Override
    public void EditInfoSuccess(Object object) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        User user = UserManager.getInstance().getUser();
        user.setName(mNikeName);
        UserManager.getInstance().updataUser(user);
        EventBusUtil.post(new EventEditInfoBean(EventEditInfoBean.CHANGENAME));
        this.finish();
    }

    @Override
    public void showUploadResult(User responseBody) {
    }

    @Override
    public void compression(String path) {

    }

    @Override
    public void tokenOverdue() {

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
    public void setPresenter(EditInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    /**
     * 编辑框监听
     */
    class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mEtNikeName.getText().toString())) {
                mIvDelete.setVisibility(View.VISIBLE);
            } else {
                mIvDelete.setVisibility(View.GONE);
            }
        }
    }
}
