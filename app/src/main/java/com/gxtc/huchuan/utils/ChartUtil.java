package com.gxtc.huchuan.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 17/2/24.
 * 图表工具类
 */

public class ChartUtil {

    /**
     * 创建一个用于lineChart的数据对象
     * @param xAxleText  x轴标签的文本数据
     * @param source     要显示的数据源，内层list表示一条折线的数据，外层list表示有多少条线
     * @param sourceTitle 每条线显示的标题
     * @return lineData
     */
    public static LineData createLineData(List<String> xAxleText, List<List<Float>> source,String sourceTitle [] ){
        ArrayList<Entry> datas = new ArrayList<>();
        datas.add(new Entry(0,0.2f));
        datas.add(new Entry(1,0.31f));
        datas.add(new Entry(2,0.46f));
        datas.add(new Entry(3,0.78f));

        LineDataSet dataSet = new LineDataSet(datas,"第一条数据");
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(dataSet);

        return new LineData(sets);
    }

}
