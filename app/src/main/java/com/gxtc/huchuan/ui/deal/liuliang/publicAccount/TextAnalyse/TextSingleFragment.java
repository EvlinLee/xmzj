package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse;

import android.graphics.DashPathEffect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.TextSingleAdapter;
import com.gxtc.huchuan.bean.TextSingleListBean;

import java.util.ArrayList;

/**
 * 图文分析－单篇图文
 * Created by Steven on 17/2/23.
 */

public class TextSingleFragment extends BaseTitleFragment implements TextContract.SingleView, AdapterView.OnItemClickListener {

    private View               headView;
    private SwipeRefreshLayout refreshLayout;
    private ListView           mListView;
    private LinearLayout       layoutTab;
    private LineChart          chart;


    private TextSingleAdapter  adapter;

    private TextContract.SinglePresenter mPresenter;

    private String [] tabs = {"第一篇","第二篇","第三篇","第四篇","第五篇","第六篇","第七篇","第八篇"};

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view       = inflater.inflate(R.layout.fragment_text_single,container,false);
        headView        = View.inflate(getContext(),R.layout.head_text_single,null);
        refreshLayout   = (SwipeRefreshLayout) view.findViewById(R.id.swiper_text_single);
        mListView       = (ListView) view.findViewById(R.id.rv_text_single);
        layoutTab       = (LinearLayout) headView.findViewById(R.id.layout_text_tab);
        chart           = (LineChart) headView.findViewById(R.id.chart_text_single);
        return view;
    }


    @Override
    public void initListener() {
    }


    @Override
    public void initData() {
        refreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        new TextSinglePresenter(this);

        for(int i= 0; i < tabs.length; i++){
            TextView text = (TextView) View.inflate(getContext(),R.layout.item_text_single_tab,null);
            text.setText(tabs[i]);
            if (i == 0) text.setSelected(true);

            text.setTag(i);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.changeTab(layoutTab, (Integer) v.getTag());
                }
            });
            layoutTab.addView(text);
        }

        mListView.addHeaderView(headView,null,false);
        ArrayList list =  new ArrayList();
        list.add(new TextSingleListBean());
        list.add(new TextSingleListBean());
        list.add(new TextSingleListBean());
        list.add(new TextSingleListBean());
        adapter = new TextSingleAdapter(getContext(),list,R.layout.item_list_text_single);
        mListView.setAdapter(adapter);

        initChart();
        initchartData();
    }

    private String xLabel [] = {"2-1","2-2","2-3","2-4","2-5","2-6","2-7"};

    private void initChart() {
        Description des = new Description();    des.setText("");
        chart.setDescription(des);          //设置图表的描述文字，会显示在图表的右下角。
        chart.setNoDataText("");            //设置当 chart 为空时显示的描述文字。
        chart.animateY(4000);
        chart.setDragEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setScaleXEnabled(true);
        chart.getLegend().setEnabled(false);    //设置不显示图表标签

        //x轴的设置
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);       //设置x轴标签位置
        xAxis.setLabelCount(7,false);        //设置X轴Label数量，    false 表示不强制绘制指定数量的label
        xAxis.setAxisMinimum(0);             //设置x轴最小数          需要配合setLabelCount一起使用
        xAxis.setAxisMaximum(6);             //设置x轴最大数          这里跟setLabelCount 计算的数值，将会影响到setValueFormatter里的value值的计算
        xAxis.setGranularityEnabled(true);
//        xAxis.setDrawGridLines(true);        //设置是否绘制网格
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel[(int) value];
            }
        });

        //右Y轴设置
        YAxis rAxis = chart.getAxisRight();
        rAxis.setEnabled(false);


        //左Y轴设置
        YAxis lAxis = chart.getAxisLeft();
        lAxis.setEnabled(true);
        lAxis.setAxisMinimum(0);
        lAxis.setLabelCount(10,false);  //设置Y轴Label数量，
        lAxis.setAxisMaximum(100);
        lAxis.setGridDashedLine(new DashPathEffect(new float[]{5.0f,5.0f},0));      //设置网格虚线
    }

    private void initchartData() {
        ArrayList<Entry> datas = new ArrayList<>();
        datas.add(new Entry(0,20));
        datas.add(new Entry(1,18));
        datas.add(new Entry(2,45));
        datas.add(new Entry(3,60));
        datas.add(new Entry(4,70));
        datas.add(new Entry(5,10));
        datas.add(new Entry(6,0));

        LineDataSet dataSet = new LineDataSet(datas,"第一条数据");
        dataSet.setColor(getContext().getResources().getColor(R.color.colorAccent));            //设置线条颜色
        dataSet.setHighlightLineWidth(1f);                                                      //设置点击线条高亮宽度
        dataSet.setCircleColor(getContext().getResources().getColor(R.color.colorAccent));      //设置数据节点圆点颜色
        dataSet.setHighLightColor(getContext().getResources().getColor(R.color.colorAccent));   //设置点击线条高亮颜色

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(dataSet);

        chart.setData(new LineData(sets));
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
    public void setPresenter(TextContract.SinglePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.i("onItemClick  : " + position);
    }
}
