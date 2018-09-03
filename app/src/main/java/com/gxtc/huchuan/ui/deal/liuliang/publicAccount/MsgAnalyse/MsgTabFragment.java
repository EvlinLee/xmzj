package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.TabBean;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.BaseTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 17/2/21.
 */

public class MsgTabFragment extends BaseTabFragment {

    private String titles [] = {"消息分析","消息关键词"};
    private int selectedIcons [] = {R.drawable.live_public_icon_xxfx_5,R.drawable.live_public_icon_gjc_6,};
    private int unselectedIcons [] = {R.drawable.live_public_icon_xxfx_5_normal,R.drawable.live_public_icon_gjc_6_normal};

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
        fragments.add(new MsgAnalyseFragment());
        fragments.add(new MsgWordFragment());
        return fragments;
    }


}
