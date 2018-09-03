package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PopListTypeAdapter;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.event.EventClickBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/26.
 */

public class PopDealType extends BasePopupWindow {

    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;

    private View footView;
    private View btnFinish;

    private PopListTypeAdapter mAdapter;

    public PopDealType(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);

        footView = View.inflate(getActivity(),R.layout.item_type_finish,null);
        btnFinish = footView.findViewById(R.id.tv_finish);
        mRecyclerView.addFootView(footView,RecyclerView.DEFAULT_FOOTVIEW);

        mAdapter = new PopListTypeAdapter(getActivity(),new ArrayList<DealTypeBean>(),new int[]{R.layout.item_list_deal_type_edit, R.layout.item_list_deal_type_btn,R.layout.item_list_deal_type});
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),1,false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusUtil.post(new EventClickBean("-1",v));
                WindowUtil.closeInputMethod(getActivity());
                closePop();
            }
        });
    }

    public void changeData(List<DealTypeBean> datas){
        mRecyclerView.notifyChangeData(datas,mAdapter);
    }

}
