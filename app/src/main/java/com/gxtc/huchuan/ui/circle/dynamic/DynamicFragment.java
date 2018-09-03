package com.gxtc.huchuan.ui.circle.dynamic;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.base.EmptyView;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;

import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;

/**
 * 圈子-动态
 * Created by Steven on 17/4/25.
 * 不用这个 用另外一个包的
 */
@Deprecated
public class DynamicFragment extends BaseTitleFragment implements DynamicContract.View,
        View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView listView;
    @BindView(R.id.swipelayout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.base_empty_area)
    View emptyView;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    private EmptyView mEmptyView;

    private int id;

    private CircleDynamicAdapter mAdapter;
    private DynamicContract.Presenter mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_circle_dynamic, null, false);
        return view;
    }

    @Override
    public void initData() {
        mEmptyView = new EmptyView(emptyView);

        swipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

//        mAdapter = new CircleDynamicAdapter(getContext(), new ArrayList<CircleHomeBean>(), new int[]{
//                R.layout.item_circle_home_noimg,
//                /*R.layout.item_circle_home_video,
//                R.layout.item_circle_home_oneimg,
//                R.layout.item_circle_home_twoimg,*/
//                R.layout.item_circle_home_threeimg});
//        mAdapter.setOnClickListener(this);
//        mAdapter.setOnReItemOnClickListener(new BaseRecyclerTypeAdapter.OnReItemOnClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Intent intent = new Intent(DynamicFragment.this.getContext(), CircleWebActivity.class);
//                CircleHomeBean data = mAdapter.getDatas().get(position);
//                intent.putExtra("data", data);
//                startActivityForResult(intent, Constant.requestCode.CIRCLE_DZ);
//            }
//        });

        listView.setAdapter(mAdapter);

        new DynamicPresenter(this);
        mPresenter.getData(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点赞
            case R.id.rl_circle_home_like:
                CircleHomeBean bean1 = (CircleHomeBean) v.getTag();
                mPresenter.dianZan(bean1.getId());
                break;

            //分享
            case R.id.rl_circle_home_share:
                ToastUtil.showShort(getContext(), "分享");
                break;

            //点击头像
            case R.id.iv_circle_home_img:
                CircleHomeBean bean = (CircleHomeBean) v.getTag(R.id.tag_first);
                PersonalInfoActivity.startActivity(getContext(), bean.getUserCode());
//                PersonalHomePageActivity.startActivity(getContext(), bean.getUserCode());
                break;
        }
    }

    @Override
    public void initListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollView.setVisibility(View.GONE);
                listView.reLoadFinish();
                mPresenter.refreshData();
            }
        });
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        id = bundle.getInt(Constant.INTENT_DATA);
    }

    @Override
    public void showData(List<CircleHomeBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showDZSuccess(int id) {
        for (int i = 0; i < mAdapter.getDatas().size(); i++) {
            CircleHomeBean bean = mAdapter.getDatas().get(i);
            if (bean.getId() == id) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
                View v = layoutManager.findViewByPosition(i);
                View v1 = v.findViewById(R.id.rl_circle_home_like);
                TextView tv = (TextView) v1.findViewById(R.id.tv_circle_home_like);

                //0是没有点赞
                if (bean.getIsDZ() == 0) {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle_home_icon_zan_select);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

                    tv.setCompoundDrawables(drawable, null, null, null);
                    tv.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                    mAdapter.getDatas().get(i).setDianZan(bean.getDianZan() + 1);
                    mAdapter.getDatas().get(i).setIsDZ(1);

                    //1是已经点赞
                } else {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle_home_icon_zan);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

                    tv.setCompoundDrawables(drawable, null, null, null);
                    tv.setTextColor(getContext().getResources().getColor(R.color.text_color_999));
                    mAdapter.getDatas().get(i).setDianZan(bean.getDianZan() - 1);
                    mAdapter.getDatas().get(i).setIsDZ(0);
                }
                tv.setText(String.valueOf(mAdapter.getDatas().get(i).getDianZan()));
            }
        }
    }

    @Override
    public void showRefreshData(List<CircleHomeBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoadMoreData(List<CircleHomeBean> datas) {
        listView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoLoadMore() {
        listView.loadFinish();
    }

    @Override
    public void setPresenter(DynamicContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        mScrollView.setVisibility(View.VISIBLE);
        mEmptyView.showEmptyContent();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        if (mAdapter.getItemCount() == 0) {
            mScrollView.setVisibility(View.VISIBLE);
            mEmptyView.showEmptyView(R.drawable.load_error, info,null);
        } else {
            ToastUtil.showShort(getContext(), info);
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(id);
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

}
