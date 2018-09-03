package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;

import java.util.List;

import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/2/20.
 */

public class LiveConversationAdaspter extends MultiItemTypeAdapter<Message> {




    public LiveConversationAdaspter(Context context, List<Message> datas) {
        super(context, datas);
    }


}
