package com.gxtc.huchuan.ui.deal;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gxtc.commlibrary.base.BaseActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.deal.deal.DealFragment;

/**
 * 交易单独的界面
 */
public class DealActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        DealFragment dealFragment = new DealFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("showBar", 0);
        dealFragment.setArguments(bundle);
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_fragment, dealFragment).commitAllowingStateLoss();
    }
}
