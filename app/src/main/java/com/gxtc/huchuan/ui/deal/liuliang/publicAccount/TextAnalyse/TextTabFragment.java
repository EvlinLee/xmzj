package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse;

import android.support.v4.app.Fragment;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.TabBean;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.BaseTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 17/2/21.
 */

public class TextTabFragment extends BaseTabFragment {


    private String titles [] = {"单篇图文","全部图文"};
    private int selectedIcons [] = {R.drawable.live_public_icon_dptw_3,R.drawable.live_public_icon_qbtw_4,};
    private int unselectedIcons [] = {R.drawable.live_public_icon_dptw_3_normal,R.drawable.live_public_icon_qbtw_4_normal};

    @Override
    public ArrayList<CustomTabEntity> getTabBeans() {
        ArrayList<CustomTabEntity> lists = new ArrayList<>();
        lists.add(new TabBean(titles[0],selectedIcons[0],unselectedIcons[0]));
        lists.add(new TabBean(titles[1],selectedIcons[1],unselectedIcons[1]));
        return lists;
    }

    @Override
    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TextSingleFragment());
        fragments.add(new TextAllFragment());
        return fragments;
    }
}
