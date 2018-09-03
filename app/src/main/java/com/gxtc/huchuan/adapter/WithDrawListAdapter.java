package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.pay.AccountSet;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.CircleImageView;

import java.util.List;

/**
 * Created by Steven on 17/4/26.
 */

public class WithDrawListAdapter extends BaseRecyclerAdapter<AccountSet> {

    public WithDrawListAdapter(Context context, List<AccountSet> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, AccountSet bean) {


        TextView tvName = (TextView) holder.getView(R.id.epay_number);
        tvName.setText(bean.getUserAccount());

    }

}
