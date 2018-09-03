package com.gxtc.huchuan.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.ui.circle.home.CircleWebActivityv2;
import com.gxtc.huchuan.ui.live.search.LiveSearchMoreActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/4.
 */

public class FriendSearchItemView implements ItemViewDelegate<SearchBean> {
    private static final String TAG = "FriendSearchItemView";
    private final Activity mActivity;
    private static final int CIRCLE_WEB_REQUEST = 1 << 3;
    public FriendSearchItemView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "7".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
        List<CircleHomeBean>  bean                  = o.getDatas();
        MyrecyclerViewAdapter myrecyclerViewAdapter = null;

        if (holder.getDataTag() == null) {
            android.support.v7.widget.RecyclerView recyclerView = holder.getView(
                    R.id.rc_recyclerview);
            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext(),
                    bean, R.layout.item_search_friend);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(holder.getConvertView().getContext(),
                            LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(
                    new BaseRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            CircleHomeBean circleBean = ((MyrecyclerViewAdapter) holder.getDataTag()).getList().get(
                                    position);
                            Intent intent = new Intent(mActivity,
                                    CircleWebActivityv2.class);
                            intent.putExtra("data", circleBean);

                            mActivity. startActivityForResult(intent, CIRCLE_WEB_REQUEST);
                        }
                    });
            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_search_type_name, "朋友圈动态");
            holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "7");
                    map.put("searchKey", o.getSearchKey());
                    GotoUtil.goToActivityWithData(mActivity, LiveSearchMoreActivity.class, map);

                }
            });
            myrecyclerViewAdapter.setOnShareAndLikeItemListener(
                    new CircleDynamicAdapter.OnShareAndLikeItemListener() {
                        @Override
                        public void goToCircleHome(CircleHomeBean bean) {
                            PersonalInfoActivity.startActivity(mActivity, bean.getUserCode());
                        }

                        @Override
                        public void notifyRecyclerView(String userCode) {

                        }

                        @Override
                        public void onShield(int position, CircleHomeBean bean, View view) {

                        }

                        @Override
                        public void shareItem(int position, CircleHomeBean bean) {

                        }
                    });
        } else {
            myrecyclerViewAdapter = (MyrecyclerViewAdapter) holder.getDataTag();
            myrecyclerViewAdapter.changeDatas(bean);
        }

    }


    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<CircleHomeBean> {

        private CircleDynamicAdapter.OnShareAndLikeItemListener listener;

        public MyrecyclerViewAdapter(Context context, List<CircleHomeBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);

        }

        @Override
        public void bindData(ViewHolder holder, final int position, final CircleHomeBean bean) {
            holder.setText(R.id.tv_circle_home_name, bean.getUserName());
            holder.getView(R.id.tv_circle_home_three_content).setVisibility(
                    TextUtils.isEmpty(bean.getContent()) ? View.GONE : View.VISIBLE);
            ImageHelper.loadImage(holder.getItemView().getContext(),
                    holder.getImageView(R.id.iv_circle_home_img), bean.getUserPic());
            holder.getView(R.id.iv_circle_home_img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.goToCircleHome(bean);
                }
            });

            final TextView content = holder.getViewV2(R.id.tv_circle_home_three_content);
            content.setText(bean.getContent());
        }

        public interface OnShareAndLikeItemListener {

//        void onLike(View view, int isLike, int position, CircleHomeBean bean);

            void goToCircleHome(CircleHomeBean bean);
        }

        public void setOnShareAndLikeItemListener(
                CircleDynamicAdapter.OnShareAndLikeItemListener itemShare) {
            this.listener = itemShare;
        }
    }
}
