package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.usermanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sjr on 2017/3/21.
 * 用户管理
 * 2017/3/22
 * 跟接口商定禁言跟黑名单页面用同一个接口
 */

public class UserManagerActivity extends BaseTitleActivity {

    @BindView(R.id.tv_user_manager_banned) TextView tvUserManagerBanned;
    @BindView(R.id.tv_user_manager_black)  TextView tvUserManagerBlack;

    private String chatroomId;
    private String chatType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
    }

    @Override
    public void initData() {
        chatroomId = getIntent().getStringExtra("id");
        chatType = getIntent().getStringExtra("chatType");
        initHeadView();
    }

    private void initHeadView() {
        getBaseHeadView().showTitle(getString(R.string.title_user_manager));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagerActivity.this.finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.news_compile_add,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showShort(UserManagerActivity.this, "我参与的");
                    }
                });

    }

    @OnClick({R.id.tv_user_manager_banned,
            R.id.tv_user_manager_black})
    public void onClick(View view) {
        switch (view.getId()) {
            //禁言用户
            case R.id.tv_user_manager_banned:
                goToActivity(2);
                break;

            //黑名单
            case R.id.tv_user_manager_black:
                goToActivity(1);
                break;
        }
    }

    private void goToActivity(int flag) {
        if (UserManager.getInstance().isLogin()) {
            Intent intent = new Intent(this, BannedOrBlackUserActivity.class);
            intent.putExtra("flag", flag);
            intent.putExtra(Constant.INTENT_DATA, chatroomId);
            intent.putExtra("chatType", chatType);
            startActivity(intent);
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

    }

    public static void startActivity(Activity activity, String id, String chatType, int resultCode){
        Intent intent =new Intent(activity, UserManagerActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("chatType", chatType);
        activity.startActivityForResult(intent,resultCode);
    }
}
