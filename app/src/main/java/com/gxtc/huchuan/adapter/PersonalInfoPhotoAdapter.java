package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/6/1 .
 */

public class PersonalInfoPhotoAdapter extends AbsBaseAdapter<String> {
    private List<String> photoList;
    public PersonalInfoPhotoAdapter(Context context, List<String> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.photoList = list;
    }

    @Override
    public int getCount() {
        return photoList.size() <= 4 ? photoList.size() : 4;
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, String s, int position) {
        Log.i("tag", "mRcAlbum: "+s);
        ImageView ivPhoto = (ImageView) holder.getView(R.id.iv_photo);
        ImageHelper.loadImage(getContext(), ivPhoto,s,R.drawable.live_list_place_holder_120);
    }
}
