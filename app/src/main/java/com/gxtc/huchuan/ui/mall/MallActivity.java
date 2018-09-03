package com.gxtc.huchuan.ui.mall;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gxtc.commlibrary.base.BaseActivity;
import com.gxtc.huchuan.R;

public class MallActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        NewMallFragment newMallFragment = new NewMallFragment();

        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_fragment, newMallFragment).commitAllowingStateLoss();
    }

}
