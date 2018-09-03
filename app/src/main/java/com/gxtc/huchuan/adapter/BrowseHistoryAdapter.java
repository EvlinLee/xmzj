package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.BrowseHistoryBean;
import com.gxtc.huchuan.bean.event.EventBrowseHistoryBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/16 0016.
 */

public class BrowseHistoryAdapter extends BaseRecyclerAdapter<BrowseHistoryBean> {
    public BrowseHistoryAdapter(Context context, List<BrowseHistoryBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, final int position, BrowseHistoryBean bean) {

        //是否选中
        final CheckBox cb = (CheckBox) holder.getView(R.id.cb_histroy);
        cb.setTag(bean);
        cb.setChecked(bean.isCheck());

        if (bean.isShow() == true) {
            cb.setVisibility(View.VISIBLE);
            if("1".equals(bean.getIsDel())){
                cb.setChecked(false);
            }
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e("tag", "onCheckedChanged: 00"+isChecked );
                    BrowseHistoryBean bean = (BrowseHistoryBean) buttonView.getTag();
                    if (isChecked && "0".equals(bean.getIsDel())){  //不仅被选中，还要是未被删除
                        bean.setCheck(true);
                        getBrowserInfoId(bean);
                        EventBusUtil.post(new EventBrowseHistoryBean(bean.getId(),bean.isCheck(),position));
                    }else {
                        if("0".equals(bean.getIsDel())){
                            bean.setCheck(false);
                            EventBusUtil.post(new EventBrowseHistoryBean(bean.getId(),bean.isCheck(),position));
                        }else {
                            buttonView.setChecked(false);
                            ToastUtil.showShort(MyApplication.getInstance(),"该数据已被删除，仅供浏览");
                        }
                    }
                }
            });

        } else {
            cb.setVisibility(View.GONE);
            cb.setChecked(false);
        }

        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);

        tvTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getCreatetime())));
        tvTitle.setText(bean.getTitle());
    }
    public String getBrowserInfoId(BrowseHistoryBean tag){
        String id = tag.getId();
        return id;
    }
}
