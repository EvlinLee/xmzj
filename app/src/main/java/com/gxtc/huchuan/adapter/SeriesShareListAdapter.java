package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.TopicShareListBean;

import java.util.List;

/**
 * Created by Gubr on 2017/3/22.
 */

public class SeriesShareListAdapter extends BaseRecyclerAdapter<TopicShareListBean.DatasBean> {
    private final String countStr = "推荐了<font color=\"#0ab70e\">%s</font>个朋友过来听课";


    public SeriesShareListAdapter(Context context, List<TopicShareListBean.DatasBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, TopicShareListBean.DatasBean datasBean) {
        ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id.iv_item_topic_share_list_head), datasBean.getHeadPic(), R.drawable.person_icon_head_120);

        holder.setText(R.id.tv_item_topic_share_list_name, datasBean.getName());
        TextView shareContent = holder.getViewV2(R.id.tv_item_topic_share_list_content);
        shareContent.setText(Html.fromHtml(String.format(countStr, datasBean.getCount())));

        //称号不需要
//        Integer integer = Integer.valueOf(datasBean.getCount());
//        int i = integer.intValue();
//        TextView call=holder.getViewV2(R.id.tv_item_topic_share_list_call);
//        call.setVisibility(i<=0?View.GONE:View.VISIBLE);
//        String[] stringArray = getContext().getResources().getStringArray(R.array.share_call);
//        String[] colors = getContext().getResources().getStringArray(R.array.share_call_color);
//        int index= i/10;
//        if (index<5){
//            holder.setText(R.id.tv_item_topic_share_list_call,stringArray[index]);
//            holder.getView(R.id.tv_item_topic_share_list_call).setBackgroundColor(Color.parseColor(colors[index]));
//
//        }
    }

}
