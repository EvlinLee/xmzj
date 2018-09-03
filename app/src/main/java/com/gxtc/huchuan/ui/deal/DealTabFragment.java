package com.gxtc.huchuan.ui.deal;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DealTabAdapter;
import com.gxtc.huchuan.bean.event.EventMenuBean;
import com.gxtc.huchuan.ui.deal.deal.DealFragment;
import com.gxtc.huchuan.ui.deal.liuliang.FlowFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 交易Tabfragment
 * Created by Steven on 17/2/13.
 */
public class DealTabFragment extends BaseTitleFragment implements View.OnClickListener {

    private final int TAB_DEAL = 0x01;
    private final int TAB_FLOW = 0x02;

    @BindView(R.id.vp_deal_tab)         ViewPager       viewPager;

    private TextView        tvDeal;
    private TextView        tvFlow;
    private ImageView       btnMenu;

    private DealTabAdapter adapter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_deal_tab,container,false);
        View headTab = LayoutInflater.from(getContext()).inflate(R.layout.model_deal_tab, (ViewGroup) getBaseHeadView().getParentView(), false);

        tvDeal = (TextView) headTab.findViewById(R.id.tv_deal_tab_deal);
        tvFlow = (TextView) headTab.findViewById(R.id.tv_deal_tab_flow);
        btnMenu = (ImageView) headTab.findViewById(R.id.btn_menu_head);

        setActionBarTopPadding(headTab);
        ((RelativeLayout) getBaseHeadView().getParentView()).addView(headTab);
        getBaseHeadView().hideHeadLine();
        return view;
    }


    @Override
    public void initListener() {
        tvDeal.setOnClickListener(this);
        tvFlow.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }



            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    changeTab(TAB_DEAL);
                }else{
                    changeTab(TAB_FLOW);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {
        DealFragment dFragment = new DealFragment();
        FlowFragment fFragment = new FlowFragment();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(dFragment);
        fragments.add(fFragment);

        adapter = new DealTabAdapter(getFragmentManager(),fragments);
        viewPager.setAdapter(adapter);

        changeTab(TAB_DEAL);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_deal_tab_deal:
                changeTab(TAB_DEAL);
                viewPager.setCurrentItem(0);
                break;

            case R.id.tv_deal_tab_flow:
                changeTab(TAB_FLOW);
                viewPager.setCurrentItem(1);
                break;

            case R.id.btn_menu_head:
                EventBusUtil.post(new EventMenuBean());
                break;
        }
    }


    private void changeTab(int tab){
        tvDeal.setSelected(tab == TAB_DEAL);
        tvFlow.setSelected(tab == TAB_FLOW);
    }


}
