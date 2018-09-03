package com.gxtc.huchuan.ui.live.hostpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;

/**
 * 来自 苏修伟 on 2018/4/12.
 */

public class OrderActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        getBaseHeadView().showTitle("课程订单");
        getBaseHeadView().showBackButton( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LiveOrderFramgent liveOrderFramgent = new LiveOrderFramgent();
        Bundle bundle = new Bundle();
        bundle.putString("RoomId", getIntent().getStringExtra("RoomId"));
        liveOrderFramgent.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_fragment, liveOrderFramgent).commitAllowingStateLoss();
    }

    public static void startActivity(Context context, String RoomId){
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra("RoomId",RoomId);
        context.startActivity(intent);
    }

}