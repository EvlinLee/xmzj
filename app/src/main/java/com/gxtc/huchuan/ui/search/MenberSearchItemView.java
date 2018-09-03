package com.gxtc.huchuan.ui.search;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MenberSearchBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.ui.live.search.LiveSearchMoreActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:会员搜索
 * Created by ALing on 2017/5/3 .
 */

public class MenberSearchItemView implements ItemViewDelegate<SearchBean> {
    private static final String TAG = "MenberSearchItemView";
    private final Activity mActivity;

    public MenberSearchItemView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "5".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
        List<MenberSearchBean> bean                  = o.getDatas();
        MyrecyclerViewAdapter  myrecyclerViewAdapter = null;

            android.support.v7.widget.RecyclerView recyclerView = holder.getView(R.id.rc_recyclerview);
            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext(), bean, R.layout.item_search_menber);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView().getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);

            myrecyclerViewAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        MenberSearchBean menberSearchBean = ((MyrecyclerViewAdapter) holder.getDataTag()).getList().get(position);
                        PersonalInfoActivity.startActivity(mActivity,
                                menberSearchBean.getUserCode());

                    }
                });

            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_search_type_name, "会员");
            holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "5");
                    map.put("searchKey", o.getSearchKey());
                    GotoUtil.goToActivityWithData(mActivity, LiveSearchMoreActivity.class, map);

                }
            });

    }


    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<MenberSearchBean> {

        public MyrecyclerViewAdapter(Context context, List<MenberSearchBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(BaseRecyclerAdapter.ViewHolder holder, int position, MenberSearchBean menberSearchBean) {
            ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id.iv_menber_head), menberSearchBean.getUserHeadPic());
            holder.setText(R.id.tv_search_userName, menberSearchBean.getUserName()).setText(R.id.tv_introduction,
                    TextUtils.isEmpty(menberSearchBean.getNewsTitle()) ? "最近没有发布内容":menberSearchBean.getNewsTitle());
        }
    }
}

