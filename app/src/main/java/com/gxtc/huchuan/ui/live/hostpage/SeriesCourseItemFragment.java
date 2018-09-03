package com.gxtc.huchuan.ui.live.hostpage;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SeriesCourseAdapter;
import com.gxtc.huchuan.bean.ChatSeriesBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.event.EventDelSeries;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sjr on 2017/3/10.
 * 系列课条目
 * 暂无下拉刷新上拉加载更多
 * ps:后期应该会加
 */

public class SeriesCourseItemFragment extends BaseTitleFragment {
    
    @BindView(R.id.rl_serier_item) RecyclerView mRecyclerView;

    //系列课数据适配器
    private SeriesCourseAdapter mAdapter;
    LiveRoomBean bean;
    private String               id;
    private List<ChatSeriesBean> seriesBeens;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_series_course_item, container, false);
        return view;
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        Bundle bundle = getArguments();
        seriesBeens = new ArrayList<>();
        bean = (LiveRoomBean) bundle.getSerializable("series_bean");
        id = (String) bundle.getSerializable("series_id");
        getSeriesCourse();
    }

    private void getSeriesCourse() {
        //全部
        if ("-1".equals(id)) {
            seriesBeens.clear();
            seriesBeens = bean.getChatSeries();
            setSeriesCourse(seriesBeens);

        //其他分类
        } else {
            seriesBeens.clear();
            for (int i = 0; i < bean.getChatSeries().size(); i++) {
                if (id.equals(bean.getChatSeries().get(i).getType()))
                    seriesBeens.add(bean.getChatSeries().get(i));
            }
            setSeriesCourse(seriesBeens);
        }

    }

    private void setSeriesCourse(List<ChatSeriesBean> datas) {
        mAdapter = new SeriesCourseAdapter(this.getContext(), datas, R.layout.item_serier_course, bean);
        final GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 2);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ChatSeriesBean chatSeriesBean = mAdapter.getList().get(position);
                Log.d("SeriesCourseFragment", chatSeriesBean.getId());

                SeriesActivity.startActivity(getActivity(), chatSeriesBean.getId());
            }
        });
    }


    @Subscribe
    public void onEvent(EventDelSeries eventbean) {
        String id = eventbean.getId();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
