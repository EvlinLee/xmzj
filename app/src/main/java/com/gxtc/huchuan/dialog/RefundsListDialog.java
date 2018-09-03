package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ListDialogAdapterV1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.gxtc.huchuan.adapter.IssueDialogAdapter;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/15.
 */

public class RefundsListDialog extends Dialog implements View.OnClickListener {

    private View                view;
    private ListView            mListView;
    private List<String>        datas;
    private ListDialogAdapterV1 mAdapter;
    Context context;

    private View.OnClickListener mItemClickListener;
    public  OnItemClickListener  mOnItemClickListener;

    public interface OnItemClickListener {
        public void selectByPosition(int position,String text);
    }

    public RefundsListDialog(@NonNull Context context, String[] datas) {
        super(context, R.style.dialog_Translucent);
        this.context = context;
        this.datas = new ArrayList<>();
        Collections.addAll(this.datas, datas);

        initView();
        initListener();
    }

    private TextView btnSure;

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_refunds, null);
        mListView = (ListView) view.findViewById(R.id.choose_list);
        btnSure = (TextView) view.findViewById(R.id.tv_sure);
        setCanceledOnTouchOutside(true);
        mAdapter = new ListDialogAdapterV1(getContext(), datas, R.layout.issue_item_layout);
        mListView.setAdapter(mAdapter);
        setContentView(view);
        mAdapter.setmChoosedTextListenner(new ListDialogAdapterV1.ChoosedTextListenner() {
            @Override
            public void OnChooseTextItem(int position, String text) {
                if(mOnItemClickListener != null)
                mOnItemClickListener.selectByPosition(position,text);
            }
        });
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private void initListener() {
        btnSure.setOnClickListener(this);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        if(mItemClickListener != null)
        mItemClickListener.onClick(v);
        dismiss();
    }

}
