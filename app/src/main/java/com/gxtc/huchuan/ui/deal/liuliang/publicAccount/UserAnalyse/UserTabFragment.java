package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.UserAnalyse;

import android.support.v4.app.Fragment;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.TabBean;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.BaseTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户分析tab fragment
 * Created by Steven on 17/2/21.
 */
public class UserTabFragment extends BaseTabFragment {

    private String titles [] = {"用户增长","用户属性"};
    private int selectedIcons [] = {R.drawable.live_public_icon_yhzz_1,R.drawable.live_public_icon_yhsx_2,};
    private int unselectedIcons [] = {R.drawable.live_public_icon_yhzz_1_normal,R.drawable.live_public_icon_yhsx_2_normal};

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
        fragments.add(new UserRiseFragment());
        fragments.add(new UserPropFragment());
        return fragments;
    }
}
