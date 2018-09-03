package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
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
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.TextAllAdapter;
import com.gxtc.huchuan.bean.TabBean;
import com.gxtc.huchuan.bean.TextAllListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 图文分析－全部图文
 * Created by Steven on 17/2/23.
 */

public class TextAllFragment extends BaseTitleFragment implements TextContract.AllView{

    private View headView;
    private LineChart chart;
    private ListView listView;
    private LinearLayout    layoutSource;
    private CommonTabLayout tabLayout;
    private SwipeRefreshLayout refreshLayout;


    private String tabs[] = {"7天", "14天", "30天"};
    private String xLabel [] = {"2-1","2-2","2-3","2-4","2-5","2-6","2-7","2-8","2-9","2-10","2-11","2-12","2-13","2-14"};

    private TextAllAdapter mAdapter;

    private TextContract.AllPresenter  mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_text_all,container,false);
        listView      = (ListView) view.findViewById(R.id.lv_user_rise);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_public);
        headView      = View.inflate(getContext(), R.layout.head_text_all, null);
        tabLayout     = (CommonTabLayout) headView.findViewById(R.id.common_tab_layout);
        chart         = (LineChart) headView.findViewById(R.id.user_rise_chart);
        layoutSource  = (LinearLayout) headView.findViewById(R.id.layout_text_all);

        refreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        return view;
    }


    @Override
    public void initData() {
        new TextAllPresenter(this);

        initTab();
        initChart();
        initCharData();

        listView.addHeaderView(headView,null,false);
        List<TextAllListBean>  list = new ArrayList<>();
        list.add(new TextAllListBean());
        list.add(new TextAllListBean());
        list.add(new TextAllListBean());
        list.add(new TextAllListBean());
        list.add(new TextAllListBean());

        for(int i = 0 ; i < 5 ; i++){
            View v = View.inflate(getContext(),R.layout.item_text_all_source,null);
            layoutSource.addView(v);
        }

        mAdapter = new TextAllAdapter(getContext(),list,R.layout.item_text_all);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

            }

            @Override
            public void onTabReselect(int position) {
            }

        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    private void initTab(){
        ArrayList<CustomTabEntity> list = new ArrayList<>();
        list.add(new TabBean(tabs[0], 0, 0));
        list.add(new TabBean(tabs[1], 0, 0));
        list.add(new TabBean(tabs[2], 0, 0));
        tabLayout.setTabData(list);
    }

    private void initChart() {
        Description des = new Description();    des.setText("");
        chart.setDescription(des);          //设置图表的描述文字，会显示在图表的右下角。
        chart.setNoDataText("");            //设置当 chart 为空时显示的描述文字。
        chart.animateY(4000);
        chart.setDragEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setScaleXEnabled(true);
        chart.zoom(2f,0,0,0);

//        chart.setDrawGridBackground(false);           //如果启用，chart 绘图区后面的背景矩形将绘制。
//        chart.setGridBackgroundColor(0);              //设置网格背景应与绘制的颜色。
//        chart.setDrawBorders(false);                  // 启用/禁用绘制图表边框（chart周围的线）。
//        chart.setMaxVisibleValueCount(int count) :    //设置最大可见绘制的 chart count 的数量。 只在 setDrawValues() 设置为 true 时有效。

        //x轴的设置
//        xAxis.setSpaceMin(0);              //设置X轴距离左下角远点的便宜量
//        xAxis.setDrawGridLines(true);        //设置是否绘制网格
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);       //设置x轴标签位置
        xAxis.setLabelCount(14,false);        //设置X轴Label数量，    false 表示不强制绘制指定数量的label
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(13);
        xAxis.setGranularityEnabled(true);
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
        lAxis.setAxisMinimum(0);
        lAxis.setAxisMaximum(1.0f);
        lAxis.setLabelCount(6,true);        //设置Y轴Label数量，
        lAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + "%";
            }
        });
//        lAxis.setGridDashedLine(new DashPathEffect(new float[]{5.0f,5.0f},0));      //设置网格虚线


    }


    private void initCharData() {
        ArrayList<Entry> datas = new ArrayList<>();
        datas.add(new Entry(0,0.2f));
        datas.add(new Entry(1,0.31f));
        datas.add(new Entry(2,0.46f));
        datas.add(new Entry(3,0.78f));

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
    public void setPresenter(TextContract.AllPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
