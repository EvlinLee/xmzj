package com.gxtc.huchuan.ui.mine.collect;

import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectionBean;

/**
 * Created by Gubr on 2017/6/5.
 */

class CircleNoImgCollectItemView extends CircleCollectItemView<CollectionBean> {
    public CircleNoImgCollectItemView(CollectActivity collectActivity,RecyclerView recyclerView) {
        super(collectActivity,recyclerView);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_circle_noimg;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        if ("4".equals(item.getType())){
            CircleHomeBean itemdata =  item.getData();
            if (0 == itemdata.getType()) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void convertContent(ViewHolder holder, CircleHomeBean bean, int position) {

    }
}
