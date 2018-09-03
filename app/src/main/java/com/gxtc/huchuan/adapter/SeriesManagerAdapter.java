package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.pop.PopBubble;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/30 .
 */

public class SeriesManagerAdapter extends BaseRecyclerAdapter<ChooseClassifyBean> {

    public SeriesManagerAdapter(Context context, List<ChooseClassifyBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, final int position, ChooseClassifyBean bean) {
        TextView tvSeriesType = (TextView) holder.getView(R.id.tv_series_type);
        tvSeriesType.setText(bean.getTypeName());

        final ImageView ivEdit = (ImageView) holder.getView(R.id.iv_edit);
        ivEdit.setTag(bean);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChooseClassifyBean tag = (ChooseClassifyBean) ivEdit.getTag();
                tag.setSelect(true);
                PopBubble bubblePopup = new PopBubble(getContext());
                bubblePopup
                        .anchorView(ivEdit)
                        .location(10, -5)
                        .gravity(Gravity.TOP)
                        .showAnim(new BounceBottomEnter())
                        .dismissAnim(new SlideBottomExit())
                        .dimEnabled(true)
                        .bubbleColor(Color.parseColor("#ffffff"))
                        .cornerRadius(4)
                        .show();
                ToastUtil.showShort(getContext(),position+"///"+tag.getId());

            }
        });
    }
    public String getInfo(ChooseClassifyBean tag){
        String typeName = tag.getTypeName();
        return typeName;
    }
}
