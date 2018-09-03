package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.UserAnalyse;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.huchuan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户分析－用户属性
 * Created by Steven on 17/2/23.
 */

public class UserPropFragment extends BaseTitleFragment implements UserContract.PropView {

    private SwipeRefreshLayout  swiperLayout;
    private LinearLayout        layoutCity;
    private LinearLayout        layoutTerminal;
    private PieChart            chart;

    private int colors [] ;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_user_prop,container,false);
        swiperLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiper_public_account);
        chart = (PieChart) view.findViewById(R.id.user_prop_chart);
        layoutCity = (LinearLayout) view.findViewById(R.id.layout_user_prop_city);
        layoutTerminal = (LinearLayout) view.findViewById(R.id.layout_user_prop_spread);
        return view;
    }

    @Override
    public void initListener() {
        swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    @Override
    public void initData() {
        swiperLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        initChart();
        initChartData();

        for(int i=0 ; i<10 ; i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = 1;
            View line = new View(getContext());
            line.setBackgroundResource(R.color.divide_line);
            line.setLayoutParams(params);
            layoutCity.addView(line);

            View view = View.inflate(getContext(),R.layout.item_user_prop,null);
            layoutCity.addView(view);
        }

        for(int i=0 ; i<3 ; i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = 1;
            View line = new View(getContext());
            line.setBackgroundResource(R.color.divide_line);
            line.setLayoutParams(params);
            layoutTerminal.addView(line);

            View view = View.inflate(getContext(),R.layout.item_user_prop,null);
            layoutTerminal.addView(view);
        }
    }


    private void initChartData() {
        colors = new int[]{
                getContext().getResources().getColor(R.color.refresh_color1),
                getContext().getResources().getColor(R.color.refresh_color2),
                getContext().getResources().getColor(R.color.refresh_color3),
                getContext().getResources().getColor(R.color.refresh_color4)};

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(26.7f, "Yellow"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));
        entries.add((new PieEntry(18.5f, "Green")));

        PieDataSet set = new PieDataSet(entries, "");
        set.setDrawValues(true);                //设置是否绘制PieEntry的值
        set.setColors(colors);                  //设置图表的颜色，每个PieEntry对应一个色值
        set.setValueTextColor(getContext().getResources().getColor(R.color.white));   //设置是否绘制PieEntry的值的颜色
        set.setValueTextSize(15f);              //设置是否绘制PieEntry的值的字体

        PieData data = new PieData(set);
        chart.setData(data);


    }

    private void initChart() {
//        chart.setHoleRadius(0);                 //设置中心圆孔半径占整个饼状图半径的百分比（100f 是最大=整个图表的半径），默认的50％的百分比（即50f）。
        Description des = new Description();   des.setText("");
        chart.setDescription(des);
        chart.setUsePercentValues(true);        //使用百分比
        chart.setDrawHoleEnabled(false);        //设置是否绘制中心圆
        chart.animateX(4000);
        chart.setTransparentCircleRadius(0);    //设置中心透明圈半径占整个饼状图半径的百分比，默认是 55％ 的半径 -> 大于默认是 50％ 的中心圆孔半径
        chart.setDrawEntryLabels(true);        //设置是否显示pieEntry中的text
        chart.setEntryLabelColor(getContext().getResources().getColor(R.color.white));      //设置是否显示pieEntry中的text的颜色
        chart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void setPresenter(UserContract.PropPresenter presenter) {

    }
}
