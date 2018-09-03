package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gxtc.commlibrary.base.KeyboardDialog;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ListBottomDialogAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/3.
 */

public class ListBottomDialog extends DialogFragment {

    @BindView(R.id.listview) ListView mListView;

    private List<String> datas;
    private int [] imgs;
    private boolean isShowDivider = true;

    private ListBottomDialogAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bottom_list, null);
        ButterKnife.bind(this, rootView);

        KeyboardDialog dialog = new KeyboardDialog(getActivity(), R.style.dialog_Translucent);
        dialog.setContentView(rootView);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.windowAnimations = R.style.mypopwindow_anim_style;

        if(!isShowDivider){
            mListView.setDividerHeight(0);
        }

        initData();
        return dialog;
    }

    private void initData() {
        if(datas == null) datas = new ArrayList<>();
        mAdapter = new ListBottomDialogAdapter(getContext(),datas,R.layout.item_simple_text,imgs);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mItemClickListener != null) mItemClickListener.onItemClick(parent,view,position,id);
            }
        });
    }


    public List<String> getDatas() {
        return datas;
    }

    public void changeData(List<String> datas){
        mAdapter.changeData(datas);
    }

    public void notifyChangeData(List<String> datas){
        if(mAdapter != null){
            mAdapter.notifyChangeData(datas);
        }
    }

    public ListBottomDialog setDatas(List<String> datas) {
        this.datas = datas;
        return this;
    }

    public ListBottomDialog setDatas(String [] items) {
        this.datas = new ArrayList<>();
        Collections.addAll(this.datas, items);
        return this;
    }

    public ListBottomDialog setDrawableImgs(int[] imgs) {
        this.imgs = imgs;
        return this;
    }

    public ListBottomDialog setShowDivider(boolean showDivider) {
        isShowDivider = showDivider;
        return this;
    }

    public ListBottomDialog setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
        return this;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if(manager.findFragmentByTag(ListBottomDialog.class.getName()) == null){
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }

}
