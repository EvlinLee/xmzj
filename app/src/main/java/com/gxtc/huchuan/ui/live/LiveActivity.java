package com.gxtc.huchuan.ui.live;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.gxtc.commlibrary.base.BaseActivity;
import com.gxtc.huchuan.R;


/**
 * 首页的liveFragment 被抽出来了
 */
public class LiveActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        LiveChanelFragment        lFragment = new LiveChanelFragment();
        Bundle bundle = new Bundle();
        lFragment.setArguments(bundle);
        FragmentTransaction ft        = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_fragment, lFragment);
        ft.commitAllowingStateLoss();
    }
}
