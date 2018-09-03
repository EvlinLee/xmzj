package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.utils.ClipboardManagerUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe:添加公众号二维码
 * Created by ALing on 2017/3/22.
 */

public class AddPublicQRCodeActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String TAG = AddPublicQRCodeActivity.class.getSimpleName();
    @BindView(R.id.et_link)         EditText        mEtLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_public_qrcode);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_add_public_qrcode));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @OnClick({R.id.btn_get_link})
    public void onClick(View view){
        switch (view.getId()){
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //获取
            case R.id.btn_get_link:
                getLink();
                break;
        }
    }

    private void getLink() {
        if (TextUtils.isEmpty(mEtLink.getText().toString())){
            ToastUtil.showShort(this,getString(R.string.add_public_qrcode_connot_empty));
            return;
        }else {
            String paste = ClipboardManagerUtil.paste(this);
            Log.d(TAG, "getLink: "+paste);
        }
    }


}
