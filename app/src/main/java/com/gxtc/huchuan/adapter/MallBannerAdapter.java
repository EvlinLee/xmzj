package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.NewsAdsBean;


/**
 *
 *
 */
public class MallBannerAdapter implements Holder<MallBean> {
    private ImageView iv;
    private View view;

    @Override
    public View createView(Context context) {
        view = View.inflate(context, R.layout.banner_layout_news, null);
        iv = (ImageView) view.findViewById(R.id.iv_news_banner);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, MallBean data) {
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageHelper.loadImage(context,iv,data.getCover(), R.drawable.list_error_img);
    }
}
