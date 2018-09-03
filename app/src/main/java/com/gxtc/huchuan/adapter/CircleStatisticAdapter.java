package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/22.
 */
@Deprecated
public class CircleStatisticAdapter extends BaseRecyclerAdapter<CircleBean> {

    public CircleStatisticAdapter(Context context, List<CircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleBean bean) {
        ImageView imgHead   = (ImageView) holder.getView(R.id.img_head);
        TextView  tvName    = (TextView) holder.getView(R.id.tv_name);
        TextView  tvInfo    = (TextView) holder.getView(R.id.tv_info);
        TextView  tvPosts   = (TextView) holder.getView(R.id.tv_posts);
        TextView  tvComment = (TextView) holder.getView(R.id.tv_comment);

        ImageHelper.loadCircle(getContext(), imgHead, bean.getUserPic());
        imgHead.setTag(R.id.tag_first,bean.getUserCode());
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = (String) v.getTag(R.id.tag_first);
                PersonalInfoActivity.startActivity(getContext(),code);
//                PersonalHomePageActivity.startActivity(getContext(),code);
            }
        });

        tvName.setText(bean.getUserName());
        tvInfo.setText(bean.getContent());
        tvPosts.setText(bean.getInfoNum() + "");
        tvComment.setText(bean.getCommNum() + "");
    }
}
