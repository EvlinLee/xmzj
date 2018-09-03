package com.gxtc.huchuan.ui.circle.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.mine.dymic.DymicMineActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.dynamic.DynamicFragment;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;

/**
 * Created by sjr on 2017/6/2.
 * 我的动态
 */

public class MineDynamicActivity extends BaseTitleActivity {

    @BindView(R.id.fl_mine_dynamic_fragment) FrameLayout flFragment;

    private DynamicFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_dynamic);

    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_mine_dynamic));
        getBaseHeadView().showHeadRightButton("互动", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoUtil.goToActivity(MineDynamicActivity.this, DymicMineActivity.class);
            }
        });
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        mFragment = new DynamicFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag",true);
        mFragment.setArguments(bundle);
        switchFragment(mFragment, DynamicFragment.class.getSimpleName(), R.id.fl_mine_dynamic_fragment);
    }

    @Override
    public void onBackPressed() {
        if (!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
    }
}
