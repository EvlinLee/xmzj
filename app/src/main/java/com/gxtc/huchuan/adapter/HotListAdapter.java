package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
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
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;

/**
 * Created by Gubr on 2017/2/27.
 * @see LiveRoomNewAdapter 统一样式 用这个的
 */
@Deprecated
public class HotListAdapter extends BaseRecyclerAdapter<ChatInfosBean> {
    public HotListAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }
    View.OnClickListener collectListener;
    public void setCollectListener(View.OnClickListener collectListener){
        this.collectListener = collectListener;
    }
    @Override
    public void bindData(ViewHolder holder, final int position, ChatInfosBean o) {
        if("0".equals(o.getChatSeries())){
            setClassData(holder,position,o);//普通课程
        }else {
            setChatSeriesData(holder,position,o);//系列课
        }
    }

    private void setChatSeriesData(ViewHolder holder, final int position, ChatInfosBean o) {
        SeriesPageBean mSeriesPageBean = o.getChatSeriesData();
        ImageHelper.loadImage(MyApplication.getInstance(), (ImageView) holder.getViewV2(R.id.iv_item_live_list_image), mSeriesPageBean.getHeadpic(), R.drawable.live_room_icon_temp);
        holder.setText(R.id.tv_item_live_list_title,mSeriesPageBean.getSeriesname()).setText(
                R.id.tv_item_live_list_content, mSeriesPageBean.getChatRoomName()).setText(
                R.id.tv_item_live_list_people, mSeriesPageBean.getBuyCount() + "人次");
        TextView status = holder.getViewV2(R.id.tv_item_live_list_status);
        TextView fee = holder.getViewV2(R.id.tv_item_live_list_price);
        fee.setText(mSeriesPageBean.getFee());
        int padding = (int) getContext().getResources().getDimension(R.dimen.margin_tiny);
        status.setText("系列课");
        status.setTextColor(getContext().getResources().getColor(R.color.circle_main_Btn_fabiao));
        status.setPadding(padding,1,padding,1);
        status.setBackgroundResource(R.drawable.shape_circle_main_btn_bg);
        holder.getImageView(R.id.iv_item_live_list_head).setVisibility(View.GONE);
    }

    private void setClassData(ViewHolder holder,final int position, ChatInfosBean o) {
        TextView title = holder.getViewV2(R.id.tv_item_live_list_title);
        holder.setText(R.id.tv_item_live_list_title, o.getSubtitle()).setText(
                R.id.tv_item_live_list_content, o.getChatRoomName()).setText(
                R.id.tv_item_live_list_people, o.getJoinCount() + "人次");
        TextView price    = holder.getViewV2(R.id.tv_item_live_list_price);

        if (!"0".equals(o.getChatSeries())) {
            price.setVisibility(View.INVISIBLE);
            setDrawableLeft(title, o.getSubtitle(), R.drawable.live_list_icon_series_topic);

        } else if ("1".equals(o.getIsForGrop())) {
            setDrawableLeft(title, o.getSubtitle(), R.drawable.live_list_icon_circle_topic);
            price.setVisibility(View.INVISIBLE);

        } else {
            setDrawableLeft(title, o.getSubtitle(), R.drawable.live_list_icon_topic);
            if (o.isFree()) {
                price.setVisibility(View.VISIBLE);
                price.setText("免费");
                price.setTextColor(holder.getItemView().getResources().getColor(R.color.text_color_999));

            } else {
                String temp = StringUtil.formatMoney(2,o.getFee()) + "元";
                price.setVisibility(View.VISIBLE);
                price.setText(temp);
                price.setTextColor(holder.getItemView().getResources().getColor(R.color.color_fb4717));
            }
        }
        holder.getImageView(R.id.iv_item_live_list_head).setVisibility(View.GONE);
        TextView status = holder.getViewV2(R.id.tv_item_live_list_status);
        status.setBackgroundResource(R.color.transparency);
        status.setPadding(0,0,0,0);
        //判断是否是系列课

            switch (o.getStatus()) {
//            1：预告，2：直播中，3：结束
                case "1":
                    status.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                    setTime(status, o);
                    break;
                case "2":
                    status.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                    status.setText("进行中");
                    break;
                case "3":
                    status.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                    status.setText("已开始");
                    break;
            }
        ImageHelper.loadImage(MyApplication.getInstance(),
                (ImageView) holder.getViewV2(R.id.iv_item_live_list_image), o.getFacePic(),
                R.drawable.live_room_icon_temp);
    }

    public CharSequence transferBiaoQing(String commonStr, int bqId) {
        return Html.fromHtml("<img src=\"" + bqId + "\">" + commonStr, mImageGetter, null);


    }

    private Html.ImageGetter mImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            int id = Integer.parseInt(source);

            Drawable drawable = getContext().getResources().getDrawable(id);
            drawable.setState(new int[]{id});
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            return drawable;
        }
    };


    private void setDrawableLeft(TextView textView, String coomtemt, int drawableId) {
        textView.setText(coomtemt);

        // TODO: 2017/6/5 下面注释的代码不能删除  到时候如果需要在标题添加标签  再改回来
//        Drawable drawable = getContext().getResources().getDrawable(drawableId);
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        ImageSpan imageSpan = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
//
//        SpannableStringBuilder s = new SpannableStringBuilder("q");
//        s.append(coomtemt);
//        s.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.setText(s);

    }

    public void setTime(TextView view, ChatInfosBean o) {
        if (!TextUtils.isEmpty(o.getEndtime())) {
        } else {
            Long aLong = 0L;
            try {
                aLong = Long.valueOf(o.getStarttime());
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
                }else if (!strings[2].equals("0")) {
                    view.setText(strings[2] + "分后");
                }else if (!strings[3].equals("0")) {
                    view.setText(strings[3] + "秒后");
                }
            }
        }
    }

}
