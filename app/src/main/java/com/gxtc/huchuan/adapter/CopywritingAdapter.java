package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CopywritingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 17/3/2.
 */

public class CopywritingAdapter extends BaseRecyclerAdapter<CopywritingBean> implements View.OnClickListener {

    private View.OnClickListener clickListener;


    public CopywritingAdapter(Context context, List<CopywritingBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CopywritingBean copywritingBean) {
        TextView tvCancel = holder.getViewV2(R.id.btn_cancel);
        TextView tvAdd = holder.getViewV2(R.id.btn_add);
        TextView tvUpdata = holder.getViewV2(R.id.btn_updata);
        TextView tvXiugai = holder.getViewV2(R.id.btn_xiugai);
        PieChart chart = holder.getViewV2(R.id.chart_pd);

        tvCancel.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        tvUpdata.setOnClickListener(this);
        tvXiugai.setOnClickListener(this);

        initChart(chart);
        initChartData(chart);
    }

    private void initChartData(PieChart chart) {
        int [] colors = new int[]{
                getContext().getResources().getColor(R.color.refresh_color1),
                getContext().getResources().getColor(R.color.refresh_color2),
                getContext().getResources().getColor(R.color.refresh_color3),
                getContext().getResources().getColor(R.color.refresh_color4)};

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(26.7f, ""));
        entries.add(new PieEntry(24.0f, ""));
        entries.add(new PieEntry(30.8f, ""));
        entries.add((new PieEntry(18.5f, "")));

        PieDataSet set = new PieDataSet(entries, "");
        set.setDrawValues(true);                //设置是否绘制PieEntry的值
        set.setColors(colors);                  //设置图表的颜色，每个PieEntry对应一个色值
        set.setValueTextColor(getContext().getResources().getColor(R.color.white));   //设置是否绘制PieEntry的值的颜色
        set.setValueTextSize(15f);              //设置是否绘制PieEntry的值的字体
        set.setFormSize(0);

        PieData data = new PieData(set);
        chart.setData(data);
    }

    private void initChart(PieChart chart) {
        chart.setHoleRadius(0);                 //设置中心圆孔半径占整个饼状图半径的百分比（100f 是最大=整个图表的半径），默认的50％的百分比（即50f）。
        Description des = new Description();   des.setText("");
        chart.setDescription(des);
        chart.setUsePercentValues(true);        //使用百分比
        chart.setDrawHoleEnabled(false);        //设置是否绘制中心圆
        chart.animateX(500);
        chart.setTransparentCircleRadius(0);    //设置中心透明圈半径占整个饼状图半径的百分比，默认是 55％ 的半径 -> 大于默认是 50％ 的中心圆孔半径
        chart.setDrawEntryLabels(true);        //设置是否显示pieEntry中的text
        chart.setEntryLabelColor(getContext().getResources().getColor(R.color.white));      //设置是否显示pieEntry中的text的颜色
        chart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        chart.getLegend().setEnabled(false);
    }

    @Override
    public void onClick(View v) {

        if(clickListener != null){
            clickListener.onClick(v);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
