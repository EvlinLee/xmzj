package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gxtc.huchuan.R;
//import com.gxtc.huchuan.adapter.IssueDialogAdapter;
import com.gxtc.huchuan.adapter.ListDialogAdapterV1;
import com.gxtc.huchuan.ui.circle.article.ArticleContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/15.
 */

public class IssueListDialog extends Dialog implements View.OnClickListener {

    private View                view;
    private ListView            mListView;
    private List<String>        datas;
    private ListDialogAdapterV1 mAdapter;
    Context context;

    private View.OnClickListener mItemClickListener;
    public  OnItemClickListener  mOnItemClickListener;

    public interface OnItemClickListener {
        public void selectByPosition(int position);
    }

    public IssueListDialog(@NonNull Context context, String[] datas) {
        super(context, R.style.dialog_Translucent);
        this.context = context;
        this.datas = new ArrayList<>();
        Collections.addAll(this.datas, datas);

        initView();
        initListener();
    }

    private TextView btnTongBu;
    private TextView btnHasTongBu;
    private TextView btnCancel;
    private TextView btnSure;

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_issue, null);
        mListView = (ListView) view.findViewById(R.id.choose_list);
        btnTongBu = (TextView) view.findViewById(R.id.tv_issue_tongbu);
        btnHasTongBu = (TextView) view.findViewById(R.id.tv_has_tongbu);
        btnCancel = (TextView) view.findViewById(R.id.tv_cancel);
        btnSure = (TextView) view.findViewById(R.id.tv_sure);
        setCanceledOnTouchOutside(false);
        mAdapter = new ListDialogAdapterV1(getContext(), datas, R.layout.issue_item_layout);
        mListView.setAdapter(mAdapter);
        setContentView(view);
        mAdapter.setmChoosedListenner(new ListDialogAdapterV1.ChoosedListenner() {
            @Override
            public void OnChooseItem(int position) {
                mOnItemClickListener.selectByPosition(position);
            }
        });
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private void initListener() {
        btnTongBu.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onClick(v);
        if (v.getId() != R.id.tv_issue_tongbu){
            clearText();
            dismiss();
        }
    }

    //显示已经同步的圈子名字
    public void changeTongbuName(List<String> names){
        StringBuffer sb = new StringBuffer();
        for(String s : names){
            sb.append(s).append(" ,");
        }
        if(sb.toString().endsWith(",")){
            sb.delete(sb.toString().length() - 1,sb.toString().length());
        }
        String temp = "已经同步到：" + sb.toString();
        btnHasTongBu.setText(temp);
    }
    public void clearText(){
        btnHasTongBu.setText("");
    }
}
