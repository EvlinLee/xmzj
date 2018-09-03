/*
package com.gxtc.huchuan.ui.mine.personalhomepage.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalNewsMoreActivity;
import com.gxtc.huchuan.ui.news.NewsWebViewActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

*/
/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 *//*


public class NewsItemView implements ItemViewDelegate<PersonalHomeDataBean> {
    private Activity mContext = null;
    private String userCode;
    public NewsItemView(Activity context,String userCode) {
        mContext = context;
        this.userCode = userCode;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_homepage_layout;
    }

    @Override
    public boolean isForViewType(PersonalHomeDataBean item, int position) {
        return "news".equals(item.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final PersonalHomeDataBean personalHomeDataBean, int position) {
        List<NewsBean> bean = personalHomeDataBean.getData();
        MyrecyclerViewAdapter myrecyclerViewAdapter=null;
        if (holder.getDataTag() == null) {
            RecyclerView recyclerView = holder.getView(R.id
                    .rc_list);
            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext()
                    , bean, R.layout.item_homepage_news);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView()
                    .getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter
                    .OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    NewsBean newsBean = ((MyrecyclerViewAdapter) holder.getDataTag()).getList().get(position);
                    Intent intent = new Intent(mContext, NewsWebViewActivity.class);
                    intent.putExtra("data", newsBean);
                    mContext.startActivity(intent);
                }
            });
            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_label_des, "文章");
            holder.getView(R.id.tv_more).setTag(userCode);
            holder.getView(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String useCode = (String) v.getTag();
                    GotoUtil.goToActivity(mContext,PersonalNewsMoreActivity.class,0,useCode);
                }
            });

        }else{
            myrecyclerViewAdapter=(MyrecyclerViewAdapter) holder.getDataTag();
            myrecyclerViewAdapter.changeDatas(bean);
        }
    }
    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<NewsBean> {


        @Override
        public int getItemCount() {
            return super.getItemCount() < 3 ? super.getItemCount() : 3;
        }

        public MyrecyclerViewAdapter(Context context, List<NewsBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(ViewHolder holder, int position, NewsBean newsBean) {
            ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id
                    .iv_news_cover), newsBean.getCover(), R.mipmap.ic_launcher);
            ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id
                    .iv_news_sourceicon), newsBean.getCover(), R.mipmap.ic_launcher);
            holder.setText(R.id.tv_news_title,newsBean.getTitle())
                    .setText(R.id.tv_news_author,newsBean.getSource())
                    .setText(R.id.tv_news_time, DateUtil.showTimeAgo(newsBean.getDate()));
        }
    }
}
*/
