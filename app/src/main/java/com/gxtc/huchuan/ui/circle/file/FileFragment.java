package com.gxtc.huchuan.ui.circle.file;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleFileAdapter;
import com.gxtc.huchuan.bean.CircleFileBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 圈子－文件
 * Created by Steven on 17/4/25.
 */

public class FileFragment extends BaseTitleFragment {

    @BindView(R.id.recyclerView) RecyclerView       listView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;

    private CircleFileAdapter mAdapter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_purchase_list, container, false);
        return view;
    }


    @Override
    public void initData() {
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        listView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        List<CircleFileBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new CircleFileBean());
        }

        mAdapter = new CircleFileAdapter(getContext(), list, R.layout.item_circle_file,listView);
        listView.setAdapter(mAdapter);
    }

}
