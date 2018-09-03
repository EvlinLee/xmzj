package com.gxtc.huchuan.ui.live.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 */

public class NewsSearchItemView implements ItemViewDelegate<SearchBean> {


    private final Activity mActivity;

    public NewsSearchItemView(Activity activity) {
        mActivity = activity;
    }

    private static final String TAG = "TopicSearchItemView";

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "1".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
             List<NewsBean>        bean                  = o.getDatas();
             MyrecyclerViewAdapter myrecyclerViewAdapter = null;
            android.support.v7.widget.RecyclerView recyclerView = holder.getView(R.id.rc_recyclerview);
            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext(), bean, R.layout.item_news_fragment);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView().getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(
                    new BaseRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            NewsBean newsBean = ((MyrecyclerViewAdapter) holder.getDataTag()).getList().get(position);
                            Intent   intent   = new Intent(mActivity, NewsWebActivity.class);
                            intent.putExtra("data", newsBean);
                            mActivity.startActivity(intent);
                        }
                    });
            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_search_type_name, "文章");
            holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "1");
                    map.put("searchKey", o.getSearchKey());
                    GotoUtil.goToActivityWithData(mActivity, LiveSearchMoreActivity.class, map);
                }
            });


    }


    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<NewsBean> {

        public MyrecyclerViewAdapter(Context context, List<NewsBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(ViewHolder holder, int position, NewsBean newsBean) {
            //作者
            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
            tvAuthor.setText(newsBean.getSource());

            //阅读
            TextView tvCount = (TextView) holder.getView(R.id.tv_read_count);
            tvCount.setText("阅读："+newsBean.getReadCount());
            ImageHelper.loadImage(holder.getItemView().getContext(),
                    holder.getImageView(R.id.iv_news_cover), newsBean.getCover());
            holder.setText(R.id.tv_news_title, newsBean.getTitle()).setText(R.id.tv_news_time,
                    DateUtil.showTimeAgo(newsBean.getDate()));
        }
    }
}
