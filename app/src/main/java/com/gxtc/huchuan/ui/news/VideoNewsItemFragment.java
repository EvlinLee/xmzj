package com.gxtc.huchuan.ui.news;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.VideoNewsItemAdapter;
import com.gxtc.huchuan.bean.VideoNewsBean;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 来自 苏修伟 on 2018/5/8.
 * 首页  视频集列表界面
 */
public class VideoNewsItemFragment extends BaseLazyFragment {


    @BindView(R.id.swipe_video_news)      SwipeRefreshLayout       refreshLayout;
    @BindView(R.id.rl_video_news)          RecyclerView             videoListView;

    private String type = "0";

    private VideoNewsItemAdapter adapter;

    @Override
    protected void lazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_news_video, container, false);
        return view;
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                videoListView.reLoadFinish();
            }
        });

        videoListView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        });
    }

    @Override
    public void initData() {
        refreshLayout.setColorSchemeResources(Constant.REFRESH_COLOR);
        videoListView.setLoadMoreView(R.layout.model_footview_loadmore);
        videoListView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VideoNewsItemAdapter(getContext(), getData(), R.layout.item_news_video);
        adapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
            @Override
            public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {

            }
        });
        videoListView.setAdapter(adapter);

    }

    private List<VideoNewsBean> getData(){
        List<VideoNewsBean> list = new ArrayList<>();
        for(int i = 0 ;i < 10; i++){
            VideoNewsBean videoNewsBean = new VideoNewsBean();
            videoNewsBean.setAuthor("新媒之家-33");
            videoNewsBean.setTitle("标题标题啊标题标题啊标题标题啊标题标题啊标题标题啊标题标题啊");
            videoNewsBean.setCreateTime(1525760781550l);
            videoNewsBean.setId(i);
            videoNewsBean.setHeadPic("http://img.zcool.cn/community/01023a556895750000012716477a1a.jpg@1280w_1l_2o_100sh.webp");
            if(i == 0){
                videoNewsBean.setSetTop(0);
            }
            list.add(videoNewsBean);
        }
        return list;
    }
}
