package com.gxtc.huchuan.ui.mine.incomedetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SegmentTabLayout;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.live.LiveChanelFragment;
import com.gxtc.huchuan.ui.mine.incomedetail.profit.circle.CircleProfitFragment;
import com.gxtc.huchuan.ui.mine.incomedetail.profit.circle.CircleProfitListFragment;
import com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom.ClassProfitFragment;
import com.gxtc.huchuan.ui.mine.incomedetail.profit.classroom.ClassRoomProfitFragment;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.utils.ViewFindUtils;

import java.util.ArrayList;

/**
 * Describe: 改版 收益明细之课堂、圈子
 * Created by zzg on 201712/30 .
 */

public class IncomeDetailActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_detail);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomeDetailActivity.this.finish();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        int type = getIntent().getIntExtra("type",-1);
        int dateType = getIntent().getIntExtra("dateType",-1);
        FragmentTransaction  ft = getSupportFragmentManager().beginTransaction();
        if(type == 0){
            getBaseHeadView().showTitle("课程收益");
            ClassRoomProfitFragment classRoomProfitFragment = new ClassRoomProfitFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("dateType",dateType);
            classRoomProfitFragment.setArguments(bundle);
            ft.add(R.id.fl_change, classRoomProfitFragment);
            ft.commitAllowingStateLoss();
        }
        if(type == 1){
            getBaseHeadView().showTitle("圈子收益");
            CircleProfitListFragment circleProfitFragment = new CircleProfitListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("dateType",dateType);
            circleProfitFragment.setArguments(bundle);
            ft.add(R.id.fl_change, circleProfitFragment);
            ft.commitAllowingStateLoss();
        }
    }

    public static void jumpToIncomeDetailActivity(Activity activity,int type,int dateType){
        Intent intent = new Intent(activity,IncomeDetailActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("dateType",dateType);
        activity.startActivity(intent);
    }
}
