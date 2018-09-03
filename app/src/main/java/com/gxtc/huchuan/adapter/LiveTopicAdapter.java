package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by Gubr on 2017/3/2.
 */
public class LiveTopicAdapter extends BaseRecyclerAdapter<ChatInfosBean> {


    private SeriesPageBean seriesPageBean;

    private View.OnClickListener listener;

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public LiveTopicAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    public LiveTopicAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId,SeriesPageBean seriesPageBean) {
        super(context, list, itemLayoutId);
        this.seriesPageBean = seriesPageBean;
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final ChatInfosBean o) {
        ImageView statusIame = (ImageView) holder.getView(R.id.iv_item_topic_isliving);
        TextView price    = holder.getViewV2(R.id.tv_item_topic_price);
        TextView title = holder.getViewV2(R.id.tv_item_topic_title);

        holder.setText(R.id.tv_item_topic_title, o.getSubtitle())
              .setText(R.id.tv_item_topic_content, o.getChatRoomName())
              .setText(R.id.tv_item_topic_people, o.getJoinCount() + "人次");
        TextView checkStatus = holder.getViewV2(R.id.tv_check_status);
        if (o.isSelff()) {
            price.setVisibility(View.GONE);
            holder.getView(R.id.tv_item_topic_manager).setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_item_topic_manager).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l != null)
                            l.onClick(v, o,position);
                        }
                    });
        } else {
            price.setVisibility(View.VISIBLE);
            if(seriesPageBean != null){
                //第一条数据，讲师设置了试听，用户没有买，必须是付费的，不是圈子成员的
                if(position == 0
                        && SeriesActivity.AUDITION_TYPE.equals(seriesPageBean.getIsAuditions())
                        && !seriesPageBean.bIsBuy()
                        && 0 == seriesPageBean.getJoinType()
                        && Double.valueOf(seriesPageBean.getFee()) > 0d
                        && "0".equals(seriesPageBean.getIsGroupUser())){

                    holder.getView(R.id.tv_item_topic_manager).setVisibility(View.VISIBLE);
                    ((TextView)holder.getView(R.id.tv_item_topic_manager)).setText("试听");
                    holder.getView(R.id.tv_item_topic_manager).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setTag(position);
                            if(listener != null)
                            listener.onClick(view);
                        }
                    });

                //2表示 开启了免费邀请制度，并且报名成功  但是邀请人数未达标
                } else if(position == 0
                        && SeriesActivity.AUDITION_INVITE_TYPE.equals(seriesPageBean.getIsAuditions())
                        && !seriesPageBean.isFinishInvite()
                        && 0 == seriesPageBean.getJoinType()) {

                    holder.getView(R.id.tv_item_topic_manager).setVisibility(View.VISIBLE);
                    ((TextView)holder.getView(R.id.tv_item_topic_manager)).setText("试听");
                    holder.getView(R.id.tv_item_topic_manager).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setTag(position);
                            if(listener != null)
                                listener.onClick(view);
                        }
                    });

                } else {
                    holder.getView(R.id.tv_item_topic_manager).setVisibility(View.GONE);
                }

            }else {
                holder.getView(R.id.tv_item_topic_manager).setVisibility(View.GONE);
            }
        }


        title.setText(o.getSubtitle());

        if (!"0".equals(o.getChatSeries())) {
            price.setVisibility(View.GONE);

        } else if ("1".equals(o.getIsForGrop())) {
            price.setVisibility(View.GONE);

        } else {
            if (o.isFree()) {
                price.setText("免费");
                price.setTextColor(holder.getItemView().getResources().getColor(R.color.text_color_999));
            } else {
                price.setText(o.getFee() + "元");
                price.setTextColor(holder.getItemView().getResources().getColor(R.color.color_fb4717));

            }
        }

        //０未审核１：已审核，２：审核不通过   新媒之家官方审核
        switch (o.getAudit()){
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
        setStatus(o, statusIame);
        setTime((TextView) holder.getView(R.id.tv_time), o);

        ImageHelper.loadRound(MyApplication.getInstance(), (ImageView) holder.getViewV2(R.id.iv_item_topic_image), o.getFacePic(),4);
    }

    private void setStatus(final ChatInfosBean o,ImageView statusIame) {

        switch (o.getStatus()) {
            //预告
            case "1":
                statusIame.setVisibility(View.VISIBLE);
                statusIame.setImageResource(R.drawable.class_no_start);
                break;

            //直播中
            case "2":
                statusIame.setVisibility(View.VISIBLE);
                statusIame.setImageResource(R.drawable.playing);
                break;

            //结束
            case "3":
                statusIame.setVisibility(View.VISIBLE);
                statusIame.setImageResource(R.drawable.live_list_yikaishi);
                break;
        }

        //0  正常  1结束  2下架
        if("2".equals(o.getShowinfo())){
            statusIame.setVisibility(View.VISIBLE);
            statusIame.setImageResource(R.drawable.down);
        }

    }

    public void setTime(TextView view, ChatInfosBean o) {
        if (TextUtils.isEmpty(o.getStarttime())) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            Long aLong = 0L;
            try{
                 aLong = Long.valueOf(o.getStarttime());
            }catch (NumberFormatException e){
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
            }else {
                view.setVisibility(View.GONE);
            }
        }
    }
    private OnManagerClickListener l;

    public interface OnManagerClickListener {
        public void onClick(View view, ChatInfosBean bean,int position);
        public void onCollectClick(View view,ChatInfosBean bean,int position);
    }

    public void setOnManagerClickListener(OnManagerClickListener l) {
        this.l = l;
    }
}
