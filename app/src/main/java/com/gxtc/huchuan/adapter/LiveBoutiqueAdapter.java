package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.Global;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;

/**
 * 来自 苏修伟 on 2018/4/16.
 */

public class LiveBoutiqueAdapter extends BaseRecyclerAdapter<ClassLike> {

    public LiveBoutiqueAdapter(Context context, List<ClassLike> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ClassLike classLike) {
        ((TextView)holder.getView(R.id.tv_live_name)).setText(classLike.getSubtitle());
        String count = classLike.getJoinNum() + "";
        int joinCount = Integer.parseInt(classLike.getJoinNum());
        if(joinCount >= 10000) {
            count = (joinCount % 10000 < 100 ? joinCount / 10000 : StringUtil.formatMoney(2,(double) joinCount / 10000)) + "万";
        }

        ((TextView)holder.getView(R.id.tv_live_count)).setText(count + "人报名");
        if( classLike.getFee() > 0){
          ((TextView)holder.getView(R.id.tv_live_price)).setText("￥" + StringUtil.formatMoney( 2, classLike.getFee()));
          ((TextView)holder.getView(R.id.tv_live_price)).setTextColor(context.getResources().getColor(R.color.color_fa4717));
        }
        else{
            ((TextView)holder.getView(R.id.tv_live_price)).setText("免费");
            ((TextView)holder.getView(R.id.tv_live_price)).setTextColor(context.getResources().getColor(R.color.pay_finish));
        }
        ImageView imageView = (ImageView) holder.getView(R.id.iv_live_image);
        ImageHelper.loadRound(context, imageView, classLike.getHeadPic(), 5);

    }

    @Override
    public int getItemCount() {
        return super.getItemCount() > 4 ? 4 : super.getItemCount();
    }

    @Override
    public void ConfigView(View view, int resId) {
        int padding = (int) (getContext().getResources().getDimension(R.dimen.px20dp) * 2);
        int divider = (int) getContext().getResources().getDimension(R.dimen.px20dp);

        if(resId == R.layout.item_live_boutique_room){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            params.width = (Global.Companion.getScreenW() - padding - divider) / 2;
        }
    }
}
