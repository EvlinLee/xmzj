package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ListDialogAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/15.
 */

public class ListDialog extends Dialog {

    private View     view;
    private ListView mListView;
    boolean isShowIv;
    private List<String> datas;
    private ListDialogAdapter mAdapter;
    Context context;

    private AdapterView.OnItemClickListener mItemClickListener;

    public ListDialog(@NonNull Context context, String [] datas) {
        super(context,R.style.dialog_Translucent);
        this.context=context;
        this.datas = new ArrayList<>();
        Collections.addAll(this.datas,datas);
        
        initView();
        initListener();
    }

    public ListDialog(@NonNull Context context, String [] datas,boolean isShowIv,int resId) {
        super(context,resId);
        this.context=context;
        this.isShowIv=isShowIv;
        this.datas = new ArrayList<>();
        Collections.addAll(this.datas,datas);

        initView();
        initListener();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bottom_list, null);
        LinearLayout mlayoutContent = (LinearLayout) view.findViewById(R.id.layout_content);
        mListView = (ListView) view.findViewById(R.id.listview);
        if(isShowIv){
            mlayoutContent.setBackgroundColor(context.getResources().getColor(R.color.white));
            mAdapter = new ListDialogAdapter(getContext(),datas,R.layout.item_simple_text,true);
        }else {
            mAdapter = new ListDialogAdapter(getContext(),datas,R.layout.item_simple_text);
        }
        mListView.setAdapter(mAdapter);
        setContentView(view);
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mItemClickListener != null)  mItemClickListener.onItemClick(parent,view,position,id);
                dismiss();
            }
        });
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
