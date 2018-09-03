package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewsAdsBean;


/**
 * Created by 伍玉南 on 2016/10/25.
 * 资讯轮播图适配器
 */
public class DealBannerAdapter implements Holder<NewsAdsBean> {
    private ImageView iv;
    private View view;

    @Override
    public View createView(Context context) {
        view = View.inflate(context, R.layout.banner_layout_news, null);
        iv = (ImageView) view.findViewById(R.id.iv_news_banner);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, NewsAdsBean data) {
//        ImageHelper.getInstance().display(data.getCover(), iv);
        ImageHelper.loadImage(context,iv,data.getCover(), R.drawable.list_error_img);
    }
}
