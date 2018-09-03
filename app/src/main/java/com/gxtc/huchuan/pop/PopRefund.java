package com.gxtc.huchuan.pop;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PopRefundAdapter;
import com.gxtc.huchuan.bean.event.EventRefundBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/10.
 */

public class PopRefund extends BasePopupWindow implements View.OnClickListener {

    @BindView(R.id.rv_complaint)         RecyclerView mRecyclerView;
    @BindView(R.id.tv_title)             TextView     tvTitle;
    @BindView(R.id.tv_complaint_content) TextView     tvOrder;
    @BindView(R.id.tv_complaint_ok)      TextView     tvOk;

    private String title;
    private List<String> contents;

    private FragmentActivity mActivity;

    private PopRefundAdapter mAdapter;

    public PopRefund(FragmentActivity activity, int resId) {
        super(activity, resId);
        mActivity = activity;
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mAdapter = new PopRefundAdapter(getActivity(),new ArrayList<String>(),R.layout.item_complaint);
        mRecyclerView.setAdapter(mAdapter);

        tvTitle.setText("退款原因");
        tvOrder.setText("其他原因");
    }

    @Override
    public void initListener() {
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                EventBusUtil.post(new EventRefundBean(mAdapter.getList().get(position)));
                closePop();
            }
        });
    }

    @OnClick({R.id.tv_complaint_ok,R.id.tv_complaint_content})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_complaint_ok:
                closePop();
                break;

            case R.id.tv_complaint_content:
                showEditDialog();
                break;

            case R.id.btn_pop_send:
                closePop();
                break;
        }
    }

    private PopComment mPopComment;
    private void showEditDialog(){
        if(mPopComment == null){
            mPopComment = new PopComment(mActivity,R.layout.pop_comment);
            mPopComment.changeThemeText();
            mPopComment.setOnClickListener(this);
        }
        mPopComment.clear();
        mPopComment.showPopOnRootView(getActivity());
    }

    public void setData(String title, List<String> contents){
        this.title = title;
        this.contents = contents;

        mAdapter.notifyChangeData(contents);
    }

    @Override
    protected int[] loadAnimRes() {
        return new int[]{R.anim.popshow_anim,R.anim.pophidden_anim};
    }
}
