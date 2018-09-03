package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;

/**
 * Created by zzg on 2017/12/18.
 */

public class NewLiveTopicAdapter extends BaseMoreTypeRecyclerAdapter<ChatInfosBean> {
    List<ChatInfosBean> mDdatas;

    public NewLiveTopicAdapter(Context context, List<ChatInfosBean> list, int... itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mDdatas = list;
    }

    @Override
    public int getItemViewType(int position) {

        switch (mDdatas.get(position).getType()) {
            //话题
            case 1:
                return 0;
            //系列课
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void bindData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, ChatInfosBean o) {
        switch (o.getType()) {
            //话题
            case 1:
                setTopData(holder, position, o);
                break;
            //系列课
            case 2:
                setSeriseData(holder, position, o);
                break;
        }


    }

    private void setSeriseData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, final int position, final ChatInfosBean bean) {
        //系列课图片
        ImageView imageView = (ImageView) holder.getView(R.id.iv_item_topic_image);
        ImageHelper.loadRound(MyApplication.getInstance(), imageView, bean.getCover(), 4);

        holder.getView(R.id.tv_item_live_list_status).setVisibility(View.VISIBLE);
        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_item_topic_title);
        tvTitle.setText(bean.getTitle());

        //购买人数
        TextView tvPerson = (TextView) holder.getView(R.id.tv_item_topic_people);
        tvPerson.setText(bean.getJoinCount() + "人次");

        //购买人数
        TextView tvToatal = (TextView) holder.getView(R.id.tv_total);
        tvToatal.setText("共有" + bean.getChatInfoCount() + "节");


        //价格
        TextView tvPrice = (TextView) holder.getView(R.id.tv_item_topic_price);
        if (0 == bean.getIsFree()) {//免费
            tvPrice.setTextColor(mContext.getResources().getColor(R.color.series_free));
            tvPrice.setText("免费");
        } else {
            tvPrice.setTextColor(mContext.getResources().getColor(R.color.series_no_free));
            tvPrice.setText("￥" + StringUtil.formatMoney(2, bean.getFee()) + "元");
        }

        TextView tvMore = (TextView) holder.getView(R.id.tv_item_topic_manager);

        if (bean.isSelff()) {
            tvMore.setVisibility(View.VISIBLE);
        } else {
            tvMore.setVisibility(View.GONE);
        }
        tvMore.setTag(position);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l != null) {
                    l.onClick(v, bean, position);
                }
            }
        });

    }

    private void setTopData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, final int position, final ChatInfosBean o) {
        ImageView statusIame = (ImageView) holder.getView(R.id.iv_item_topic_isliving);
        TextView price = (TextView) holder.getView(R.id.tv_item_topic_price);
        TextView title = (TextView) holder.getView(R.id.tv_item_topic_title);
        TextView content = (TextView) holder.getView(R.id.tv_item_topic_content);
        TextView peopleCount = (TextView) holder.getView(R.id.tv_item_topic_people);
        title.setText(o.getTitle());
        content.setText("￥" + StringUtil.formatMoney(2, o.getFee()));
        content.setTextColor(content.getResources().getColor(R.color.color_fb4717));
        peopleCount.setText(o.getJoinCount() + "人次");
        TextView checkStatus = (TextView) holder.getView(R.id.tv_check_status);
        if (o.isSelff()) {
            price.setVisibility(View.GONE);
            holder.getView(R.id.tv_item_topic_manager).setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_item_topic_manager).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            l.onClick(v, o, position);
                        }
                    });
        } else {
            price.setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_item_topic_manager).setVisibility(View.GONE);
        }
        if (0 == o.getIsFree()) {
            price.setText("免费");
            price.setTextColor(holder.getItemView().getResources().getColor(R.color.text_color_999));
        } else {
            price.setText(StringUtil.formatMoney(2,o.getFee()) + "元");
            price.setTextColor(holder.getItemView().getResources().getColor(R.color.color_fb4717));

        }

        //０未审核１：已审核，２：审核不通过   新媒之家官方审核
        switch (o.getAudit()) {
            case "0":
                statusIame.setVisibility(View.GONE);
                checkStatus.setText("未审核");
                break;

            case "1":
                statusIame.setVisibility(View.VISIBLE);
                checkStatus.setText("审核成功");
                break;

            case "2":
                statusIame.setVisibility(View.GONE);
                checkStatus.setText("审核不通过");
                break;
        }

        checkStatus.setVisibility(o.isSelff() ? View.VISIBLE : View.GONE);
        setStatus(o, statusIame, (TextView) holder.getView(R.id.tv_time));
        setTime((TextView) holder.getView(R.id.tv_time), o);

        ImageHelper.loadRound(MyApplication.getInstance(), (ImageView) holder.getView(R.id.iv_item_topic_image), o.getCover(), 4);
    }

    private void setStatus(final ChatInfosBean o, ImageView statusIame, TextView time) {

        switch (o.getStatus()) {
            //预告
            case "1":
                time.setVisibility(View.VISIBLE);
                statusIame.setVisibility(View.VISIBLE);
                statusIame.setImageResource(R.drawable.class_no_start);
                break;

            //直播中
            case "2":
                time.setVisibility(View.GONE);
                statusIame.setVisibility(View.VISIBLE);
                statusIame.setImageResource(R.drawable.playing);
                break;

            //结束
            case "3":
                time.setVisibility(View.GONE);
                statusIame.setVisibility(View.VISIBLE);
                statusIame.setImageResource(R.drawable.live_list_yikaishi);
                break;
        }

        //0  正常  1结束  2下架
        if ("2".equals(o.getShowinfo())) {
            statusIame.setVisibility(View.VISIBLE);
            statusIame.setImageResource(R.drawable.down);
        }

    }

    public void setTime(TextView view, ChatInfosBean o) {
        if (TextUtils.isEmpty(o.getTime())) {
            view.setVisibility(View.GONE);
        } else {
            Long aLong = 0L;
            try {
                aLong = Long.valueOf(o.getTime());
            } catch (NumberFormatException e) {
                aLong = 0L;
            }

            long l = aLong - System.currentTimeMillis();
            if (l > 0) {
                String[] strings = DateUtil.countDownNotAddZero(l);
                if (!strings[0].equals("0")) {
                    view.setText(strings[0] + "天后");
                } else if (!strings[1].equals("0")) {
                    view.setText(strings[1] + "小时后");
                }
            }
        }
    }

    private OnManagerClickListener l;

    public interface OnManagerClickListener {
        public void onClick(View view, ChatInfosBean bean, int position);

        public void onCollectClick(View view, ChatInfosBean bean, int position);
    }

    public void setOnManagerClickListener(OnManagerClickListener l) {
        this.l = l;
    }
}
