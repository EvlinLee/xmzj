package com.gxtc.huchuan.ui.live.invitation;

import android.os.Bundle;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;

import butterknife.OnClick;

/**
 * Created by Gubr on 2017/3/17.
 */

public class ActivityInvitationHonored extends BaseTitleActivity   {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_honored);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("邀请嘉宾");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_submit)
    public void onClick(View view) {

    }
}
