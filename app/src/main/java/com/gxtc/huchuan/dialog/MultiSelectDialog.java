package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MultiSelectAdapter;
import com.gxtc.huchuan.bean.AllTypeBaen;
import com.gxtc.huchuan.bean.event.EventClickBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选or单选
 * 来自 伍玉南 的装逼小尾巴 on 17/6/1.
 */

public class MultiSelectDialog extends Dialog implements View.OnClickListener {

    private boolean multiterm;      //是否是多项选择

    private View view;
    private ListView mListView;
    private TextView tvFinish;
    private TextView tvCancel;
    private MultiSelectAdapter mAdapter;

    private List<AllTypeBaen.UdefBean.Entity> mList;

    public MultiSelectDialog(@NonNull Context context){
        super(context,R.style.dialog_Translucent);
        initView();
        initListener();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_multiselect, null);
        mListView = (ListView) view.findViewById(R.id.listview);
        tvFinish = (TextView) view.findViewById(R.id.tv_finish);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        mListView = (ListView) view.findViewById(R.id.listview);
        mAdapter = new MultiSelectAdapter(getContext(),new ArrayList<AllTypeBaen.UdefBean.Entity>(),R.layout.item_multi_select);
        mListView.setAdapter(mAdapter);
        setContentView(view);
    }

    private void initListener() {
        tvFinish.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }


    public void notifyChangeData(List<AllTypeBaen.UdefBean.Entity> datas){
        mList = datas;
        mAdapter.notifyChangeData(datas);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_finish:
                EventBusUtil.post(new EventClickBean("",mList));
                break;

            case R.id.tv_cancel:
                mAdapter.notifyChangeData(new ArrayList<AllTypeBaen.UdefBean.Entity>());
                break;
        }
        dismiss();
    }
}
