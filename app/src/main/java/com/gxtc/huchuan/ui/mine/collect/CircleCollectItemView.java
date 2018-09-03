package com.gxtc.huchuan.ui.mine.collect;

import android.app.Activity;
import android.net.Uri;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.ArrayList;

/**
 * Created by Gubr on 2017/6/5.
 *
 */

public abstract class CircleCollectItemView<T extends CollectionBean> implements ItemViewDelegate<T> {
    private static final String TAG = "CircleCollectItemView";
    private Activity mActivity;

    public CircleCollectItemView(Activity activity, RecyclerView recyclerView) {
        mActivity = activity;
        mRecyclerView = recyclerView;
    }

    private RecyclerView mRecyclerView;


    private OnShareAndLikeItemListener listener;

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, final int position) {
        final CircleHomeBean bean = (CircleHomeBean) collectionBean.getData();

        //内容
        final TextView tvContent = (TextView) holder.getView(R.id.tv_circle_home_three_content);
        final TextView mTime = (TextView) holder.getView(R.id.tv_news_collect_time);
        mTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getCreateTime())));
        View layoutContent = holder.getView(R.id.layout_content);
        if (!TextUtils.isEmpty(bean.getContent()))
            tvContent.setText(bean.getContent());
        else{
            tvContent.setVisibility(View.GONE);
            layoutContent.setVisibility(View.GONE);
        }

        final TextView tvzk = (TextView) holder.getView(R.id.tv_content_zk);

        LinearLayout llzk = (LinearLayout) holder.getView(R.id.ll_content_zk);


        if (bean.tagFlag) {
            tvContent.setFilters(new InputFilter[]{});
        } else {
            tvContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        }
        if (!TextUtils.isEmpty(bean.getContent())) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(bean.getContent());
        } else tvContent.setVisibility(View.GONE);
        if (TextUtils.isEmpty(bean.getContent()) || bean.getContent().length() <= 150) {
            layoutContent.setVisibility(View.GONE);
            tvzk.setVisibility(View.GONE);
        } else if (bean.getContent().length() > 150) {
            tvzk.setVisibility(View.VISIBLE);
            tvzk.setTag(bean.tagFlag ? "收起" : "全文");
            tvzk.setText(bean.tagFlag ? "收起" : "全文");
        }

        llzk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.tagFlag) {//收起
                    bean.tagFlag = false;
                    tvContent.setEllipsize(TextUtils.TruncateAt.END);
                    mRecyclerView.notifyItemChanged(position);

                } else {//全文
                    bean.tagFlag = true;
                    mRecyclerView.notifyItemChanged(position);
                }

            }
        });


        //用户名
        TextView tvNoName = (TextView) holder.getView(R.id.tv_circle_home_name);
        tvNoName.setText(bean.getUserName());

        //头像
        ImageView ivNoName = (ImageView) holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), ivNoName, bean.getUserPic(), R.drawable.person_icon_head);
        ivNoName.setTag(R.id.tag_first, bean);

        ivNoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleHomeBean bean = (CircleHomeBean) v.getTag(R.id.tag_first);
                if(listener != null)
                listener.goToCircleHome(bean);
            }
        });
        holder.getView(R.id.v_commentline).setVisibility(View.GONE);

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_circle_home_time);
        if (!(0 == bean.getCreateTime()))
            tvTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getCreateTime())));
        convertContent(holder, bean, position);
    }

    public void goImgActivity(CircleHomeBean bean, int position) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < bean.getPicList().size(); i++) {
            uris.add(Uri.parse(bean.getPicList().get(i).getPicUrl()));
        }
        CommonPhotoViewActivity.startActivity(mActivity,uris,position);
    }

    protected abstract void convertContent(ViewHolder holder, CircleHomeBean bean, int position);

    public void setOnShareAndLikeItemListener(OnShareAndLikeItemListener itemShare) {
        this.listener = itemShare;
    }

    //分享回调和点赞
    public interface OnShareAndLikeItemListener {
        void onShareOrDeleteOrComplaint(ViewHolder holder, int position, CircleHomeBean bean);
        void goToCircleHome(CircleHomeBean bean);
    }
}
