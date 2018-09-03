package com.gxtc.huchuan.dialog;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.gxtc.huchuan.bean.model.ProvinceModel;
import com.gxtc.huchuan.utils.XmlParserHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by ALing on 2017/3/4.
 */

public class ProvinceCityAreaDialog {
    private List<String> proList = new ArrayList<>();           //省
    private List<List<String>> cityList = new ArrayList<>();    //市
    private List<List<List<String>>> areaList = new ArrayList<>();     //区

    private OptionsPickerView pvOptions;
    private OnCitySelectListener listener;

    public ProvinceCityAreaDialog(Context context ){
        AssetManager asset = context.getAssets();
        InputStream input = null;
        List<ProvinceModel> provinceList = null;
        try {
            input = asset.open("province_data.xml");

            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (provinceList != null && provinceList.size() > 0) {
            for (ProvinceModel list : provinceList) {
                proList.add(list.getPickerViewText());
                cityList.add(list.getCityNameList());
                areaList.add(list.getDisNameList());
            }
        }

        pvOptions = new  OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                //返回的分别是三个级别的选中位置
               /* String tx = proList.get(options1).getPickerViewText()
                        + cityList.get(options1).get(option2)
                        + areaList.get(options1).get(option2).get(options3).getPickerViewText();
                tvOptions.setText(tx);*/

                String province = proList.get(options1);
                String city = cityList.get(options1).get(option2).replace("市","");
                String area = areaList.get(options1).get(option2).get(options3);
                Log.e("tag", "onOptionsSelect: "+province+city+area );
                listener.onCitySelect(province,city,area);
            }
        })
                /*.setSubmitText("确定")
                .setCancelText("取消")
                .setTitleText("城市选择")
                .setTitleSize(20)
                .setSubCalSize(18)//确定取消按钮大小
                .setTitleColor(Color.BLACK)
                .setSubmitColor(Color.BLUE)
                .setCancelColor(Color.BLUE)
                .setBackgroundColor(Color.WHITE)
                .setLinkage(false)//default true
                .setCyclic(false, false, false)//循环与否
                .setOutSideCancelable(false)//点击外部dismiss, default true
                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode*/
               /* .setLabels("省", "市", "区")//设置选择的三级单位*/
               /* .setLineSpacingMultiplier(2.0f) //设置两横线之间的间隔倍数（范围：1.2 - 2.0倍 文字高度）*/
               /* .setDividerColor(Color.RED)//设置分割线的颜色*/
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)//设置滚轮文字大小
                .setSelectOptions(0,1,2)  //设置默认选中项
//                .isDialog(true)//设置为对话框模式
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(proList, cityList, areaList);//三级选择器

    }

    public void show(){
        pvOptions.show();
    }

    public interface OnCitySelectListener{
        void onCitySelect(String province,String city,String area);
    }

    public void setOnCitySelectListener(OnCitySelectListener listener){
        this.listener = listener;
    }
}
