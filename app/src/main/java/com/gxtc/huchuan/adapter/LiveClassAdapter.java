package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveHeadPageBean;
import com.gxtc.huchuan.bean.LiveRoomBean;

import java.util.List;

/**
 * 直播首页类型适配器
 * Created by Gubr on 2017/2/10.
 */

public class LiveClassAdapter extends BaseRecyclerAdapter<LiveHeadPageBean.TypesChatRoom> implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "LiveClassAdapter";

    public LiveClassAdapter(Context context, List<LiveHeadPageBean.TypesChatRoom> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }


    private void setDatas(ViewHolder holder, LiveRoomBean bean) {

    }


    @Override
    public void bindData(ViewHolder holder, int position, LiveHeadPageBean.TypesChatRoom o) {
//        int size = o.getChatRooms().size();
//        holder.getView(R.id.item_live_class_acrea2).setVisibility(View.INVISIBLE);
//        holder.getView(R.id.item_live_class_acrea1).setVisibility(View.INVISIBLE);
//        holder.getView(R.id.item_live_class_acrea0).setVisibility(View.INVISIBLE);
//        switch (size) {
//            case 3:
//                LiveRoomBean liveRoomBean = o.getChatRooms().get(2);
//                holder.getView(R.id.item_live_class_acrea2).setVisibility(View.VISIBLE);
//                holder.setText(R.id.item_live_class_title2, liveRoomBean.getRoomname())
//                        .setText(R.id.item_live_class_count2, liveRoomBean.getFs());
////                ImageHelper.loadImage(holder.getItemView().getContext(),holder.getImageView(R.id.item_live_class_photo2),liveRoomBean.get);
//            case 2:
//                holder.getView(R.id.item_live_class_acrea1).setVisibility(View.VISIBLE);
//            case 1:
//                holder.getView(R.id.item_live_class_acrea0).setVisibility(View.VISIBLE);
//
//        }


        holder.setImage(R.id.item_live_class_photo0, R.drawable.live_class_icon_temp)
                .setImage(R.id.item_live_class_photo1, R.drawable.live_class_icon_temp)
                .setImage(R.id.item_live_class_photo2, R.drawable.live_class_icon_temp)
                .setText(R.id.item_live_class_label, "人文")
                .setText(R.id.item_live_class_title0, "文化大讲堂")
                .setText(R.id.item_live_class_title1, "文化大讲堂")
                .setText(R.id.item_live_class_title2, "文化大讲堂")
                .setText(R.id.item_live_class_count0, "7350关注")
                .setText(R.id.item_live_class_count1, "7350关注")
                .setText(R.id.item_live_class_count2, "7350关注");
        CheckBox checkBox0 = holder.getViewV2(R.id.item_live_class_check0);
        CheckBox checkBox1 = holder.getViewV2(R.id.item_live_class_check1);
        CheckBox checkBox2 = holder.getViewV2(R.id.item_live_class_check2);
        checkBox0.setChecked(true);
        checkBox0.setChecked(false);
        checkBox0.setChecked(true);

        checkBox0.setOnCheckedChangeListener(this);
        checkBox1.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
//        TextView textView0 = holder.getViewV2(R.id.item_live_class_btn0);
//        TextView textView1 = holder.getViewV2(R.id.item_live_class_btn1);
//        TextView textView2 = holder.getViewV2(R.id.item_live_class_btn2);
//        textView0.setOnClickListener(this);
//        textView1.setOnClickListener(this);
//        textView2.setOnClickListener(this);
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.item_live_class_btn0:
//                Log.d("LiveClassAdapter", "btn0");
//                break;
//            case R.id.item_live_class_btn1:
//                Log.d("LiveClassAdapter", "btn1");
//
//                break;
//            case R.id.item_live_class_btn2:
//                Log.d("LiveClassAdapter", "btn2");
//
//                break;
//        }
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_live_class_check0:
                Log.d(TAG, "onCheckedChanged: 这里将要去改变状态");
                break;
            case R.id.item_live_class_check1:
                Log.d(TAG, "onCheckedChanged: 这里将要去改变状态");

                break;
            case R.id.item_live_class_check2:
                Log.d(TAG, "onCheckedChanged: 这里将要去改变状态");

                break;
        }
    }


}
