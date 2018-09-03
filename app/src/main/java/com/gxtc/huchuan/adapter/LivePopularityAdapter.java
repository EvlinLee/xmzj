package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;

/**
 * 来自 苏修伟 on 2018/4/16.
 */

public class LivePopularityAdapter extends BaseRecyclerAdapter<ClassHotBean> {

    public LivePopularityAdapter(Context context, List<ClassHotBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ClassHotBean classHotBean) {
        ((TextView)holder.getView(R.id.iv_live_popularity_name)).setText(classHotBean.getName());
        String count = classHotBean.getCount() + "";
        int joinCount = classHotBean.getCount();
        if(joinCount >= 10000) {
            count = (joinCount % 10000 < 100 ? joinCount / 10000 : StringUtil.formatMoney(2,(double) joinCount / 10000)) + "万";

        }
        ((TextView)holder.getView(R.id.iv_live_popularity_count)).setText(count + "人关注");

        ImageView imageView = (ImageView) holder.getView(R.id.iv_live_popularity_image);
        ImageHelper.loadCircle(context, imageView, classHotBean.getHeadPic());
    }


}
