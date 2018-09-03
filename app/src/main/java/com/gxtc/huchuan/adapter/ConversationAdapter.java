package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;

import java.util.List;

import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/2/27.
 */

public class ConversationAdapter extends MultiItemTypeAdapter<Message> {
    public ConversationAdapter(Context context, List<Message> datas) {
        super(context, datas);
//        AudioPlayManager.getInstance().reinit();
//        for (int i = 0; i < datas.size(); i++) {
//            if (datas.get(i).getObjectName().equals("RC:VcMsg")) {
//                AudioPlayManager.getInstance().addPlay(datas.get(i));
//            }
//        }
    }


    @Override
    public void addData(Message item) {
//        if (item.getObjectName().equals("RC:VcMsg")) {
//            AudioPlayManager.getInstance().addPlay(item);
//        }
        super.addData(item);
    }
}
