package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbBasePagerAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventMainClickBean;

import java.util.List;

public class MainPagerAdapter extends AbBasePagerAdapter<List<String>> {

    private List<List<Integer>> imgs;

    public MainPagerAdapter(Context context, List<View> views, List<List<String>> datas, List<List<Integer>> imgs) {
        super(context, views, datas);
        this.imgs = imgs;
    }

    @Override
    public void bindData(View view, List<String> strings,int position) {
        String btnName = "btn_more_";
        String imgName = "img_more_";
        String tvName = "tv_more_";

        for (int i = 0; i < strings.size(); i++) {
            int btnId = getContext().getResources().getIdentifier(btnName + (i + 1), "id", getContext().getPackageName());
            int imgId = getContext().getResources().getIdentifier(imgName + (i + 1), "id", getContext().getPackageName());
            int tvId = getContext().getResources().getIdentifier(tvName + (i + 1), "id", getContext().getPackageName());

            View v = view.findViewById(btnId);
            ImageView img = (ImageView) view.findViewById(imgId);
            TextView tv = (TextView) view.findViewById(tvId);

            String tab = strings.get(i);
            tv.setText(tab);

            img.setImageResource(imgs.get(position).get(i));

            EventMainClickBean bean = new EventMainClickBean(position,i);
            v.setTag(bean);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventMainClickBean temp = (EventMainClickBean) v.getTag();
                    EventBusUtil.post(temp);
                }
            });
        }

    }
}
