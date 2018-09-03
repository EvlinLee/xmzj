/*
package com.gxtc.huchuan.ui.mine.personalhomepage.homepage;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalTopicMoreActivity;

import java.util.List;

*/
/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 *//*


public class TopicItemView implements ItemViewDelegate<PersonalHomeDataBean> {
    private Activity mContext = null;
    private String userCode;
    public TopicItemView(Activity context,String userCode) {
        mContext = context;
        this.userCode = userCode;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_homepage_layout;
    }

    @Override
    public boolean isForViewType(PersonalHomeDataBean item, int position) {
        return "chatInfo".equals(item.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final PersonalHomeDataBean personalHomeDataBean, int position) {
        List<HomePageChatInfo> bean = personalHomeDataBean.getData();
        MyrecyclerViewAdapter myrecyclerViewAdapter=null;
        if (holder.getDataTag() == null) {
            RecyclerView recyclerView = holder.getView(R.id
                    .rc_list);
            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext()
                    , bean, R.layout.item_homepage_topic);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView()
                    .getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter
                    .OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    String id = ((MyrecyclerViewAdapter)holder.getDataTag()).getList().get(position).getId();
                    LiveIntroActivity.startActivity(v.getContext(), id);
                }
            });
            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_label_des, "课堂");
            holder.getView(R.id.tv_more).setTag(userCode);
            holder.getView(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userCode = (String) v.getTag();
                    GotoUtil.goToActivity(mContext, PersonalTopicMoreActivity.class,0,userCode);
                }
            });

        }else{
            myrecyclerViewAdapter=(MyrecyclerViewAdapter) holder.getDataTag();
            myrecyclerViewAdapter.changeDatas(bean);
        }
    }
    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<HomePageChatInfo> {


        @Override
        public int getItemCount() {
            return super.getItemCount() < 3 ? super.getItemCount() : 3;
        }

        public MyrecyclerViewAdapter(Context context, List<HomePageChatInfo> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(ViewHolder holder, int position, HomePageChatInfo chatInfoBean) {
            ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id
                    .iv_face), chatInfoBean.getFacePic(), R.mipmap.ic_launcher);
            holder.setText(R.id.tv_title,chatInfoBean.getSubtitle())
                    .setText(R.id.tv_joinCount,chatInfoBean.getJoinCount());
            String isfree = chatInfoBean.getIsfree();
            if ("0".equals(isfree)){
                //免费
            }else {
                //收费
                String fee = chatInfoBean.getFee();
                holder.setText(R.id.tv_start_time,chatInfoBean.getStarttime());
            }
        }
    }
}
*/
