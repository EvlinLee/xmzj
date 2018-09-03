package com.gxtc.huchuan.ui.mall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.BubblePopup;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PopListAdapter;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.pop.MallPopList;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.List;

/**
 * Created by zzg on 2017/11/27.
 */

public class MallHandleUtil {

    public static void handlerByType(Context mContext, List<MallBean> datas,int position){
        switch (datas.get(position).getType()){
            //商品
            case "0":
                MallDetailedActivity.startActivity(mContext,  datas.get(position).getData());
                break;
            //链接
            case "1":
                CommonWebViewActivity.startActivity(mContext, datas.get(position).getData(), "");
                break;
            //商品分类列表
            case "2":
                Intent intent = new Intent(mContext, MallTagActivity.class);
                intent.putExtra("categoryId", datas.get(position).getData());
                intent.putExtra("title", datas.get(position).getName());
                mContext.startActivity(intent);
                break;
        }
    }

    public static void setSelected(Context mContext,View v, int  resId){
       /* TextView target  = (TextView) v;
        target.setTextColor(mContext.getResources().getColor(R.color.color_2b8cff));
        Drawable drawable = mContext.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getBounds().width(), drawable.getBounds().height());
        target.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);*/
    }

    public static void setNolmal(Context mContext,TextView mallShouyelText, TextView mallfenleilText, TextView mMallHuodongText, TextView mMallWodeText){
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_shouye_normal);
        drawable.setBounds(0, 0, drawable.getBounds().width(), drawable.getBounds().height());
        mallShouyelText.setTextColor(mContext.getResources().getColor(R.color.text_color_666));
        mallShouyelText.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);

        Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.icon_fenlei_normal);
        drawable1.setBounds(0, 0, drawable1.getBounds().width(), drawable1.getBounds().height());
        mallfenleilText.setTextColor(mContext.getResources().getColor(R.color.text_color_666));
        mallfenleilText.setCompoundDrawablesWithIntrinsicBounds(null,drawable1,null,null);

        Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.icon_huodong_normal);
        drawable2.setBounds(0, 0, drawable2.getBounds().width(), drawable2.getBounds().height());
        mMallHuodongText.setTextColor(mContext.getResources().getColor(R.color.text_color_666));
        mMallHuodongText.setCompoundDrawablesWithIntrinsicBounds(null,drawable2,null,null);

        Drawable drawable3 = mContext.getResources().getDrawable(R.drawable.icon_wode_normal);
        drawable3.setBounds(0, 0, drawable3.getBounds().width(), drawable3.getBounds().height());
        mMallWodeText.setTextColor(mContext.getResources().getColor(R.color.text_color_666));
        mMallWodeText.setCompoundDrawablesWithIntrinsicBounds(null,drawable3,null,null);
    }


    public static void showPop(final Activity mContext, View view, final List<MallBean> datas, int resId, int mode) {
        MallPopList    mBubblePopup = new MallPopList(mContext);
        mBubblePopup.setData(datas);
        mBubblePopup.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
            @Override
            public void onItemClick(RecyclerView parentView, View v, int position) {
                Intent intent = new Intent(mContext, MallTagActivity.class);
                intent.putExtra("categoryId", datas.get(position).getData());
                intent.putExtra("title", datas.get(position).getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        mBubblePopup.anchorView(view).showAnim(new PopEnterAnim().duration(200)).dismissAnim(new PopExitAnim().duration(200)).gravity(mode).cornerRadius(4F).
                bubbleColor(mContext.getResources().getColor(R.color.white)).show();
    }

    public static void showPop(final Activity mContext, View view, final List<MallBean> datas, int mode, BaseRecyclerAdapter.OnItemClickLisntener Lisntener) {
        MallPopList mBubblePopup1 = new MallPopList(mContext);
        mBubblePopup1.setData(datas);
        mBubblePopup1.setOnItemClickLisntener(Lisntener);
        mBubblePopup1.anchorView(view).showAnim(new PopEnterAnim().duration(200)).dismissAnim(new PopExitAnim().duration(200)).gravity(mode).cornerRadius(4F).
                bubbleColor(mContext.getResources().getColor(R.color.white)).show();
    }

}
