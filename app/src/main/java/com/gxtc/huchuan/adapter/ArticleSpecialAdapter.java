package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.ArticleSpecialBean;

import java.util.List;

/**
 * 来自 苏修伟 on 2018/5/12.
 */
public class ArticleSpecialAdapter extends BaseRecyclerAdapter<ArticleSpecialBean> {
    private String isPay;

    public ArticleSpecialAdapter(Context context, List<ArticleSpecialBean> list, int itemLayoutId, String isPay) {
        super(context, list, itemLayoutId);
        this.isPay = isPay;
    }


    @Override
    public void bindData(ViewHolder holder, int position, ArticleSpecialBean articleSpecialBean) {
        ImageView mCover = (ImageView) holder.getView(R.id.iv_specia_cover);
        TextView mTitle = (TextView) holder.getView(R.id.tv_specia_title);
        TextView mType = (TextView) holder.getView(R.id.tv_type_type);
        if ("1".equals(isPay)) {
            mType.setText("立即阅读");
            mType.setTextColor(getContext().getResources().getColor(R.color.pay_finish));
        }else {
            //0=非专题文章或视频，1=免费，2=收费，3=试看
            switch (articleSpecialBean.isFreeSee()) {
                case 1:
                    mType.setText("免费阅读");
                    mType.setTextColor(getContext().getResources().getColor(R.color.pay_finish));
                    break;
                case 2:
                    mType.setText("订阅阅读");
                    mType.setTextColor(getContext().getResources().getColor(R.color.color_ff7531));
                    break;
                case 3:
                    mType.setText("试读");
                    mType.setTextColor(getContext().getResources().getColor(R.color.color_2b8cff));
                    break;
            }
        }

        mTitle.setText(articleSpecialBean.getTitle());
        ImageHelper.loadImage(getContext(), mCover, articleSpecialBean.getCover());
    }
}
