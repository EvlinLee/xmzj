package com.gxtc.huchuan.ui.circle.findCircle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleListAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.pop.PopChoose;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.pop.PopList;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 发现圈子
 */
public class CircleListActivity extends BaseTitleActivity implements CircleListContract.View, View.OnClickListener {

    public final static String CONSTANT_VARIABLE_ALL = null;
    public final static String CONSTANT_VARIABLE_FREE = "0";
    public final static String CONSTANT_VARIABLE_PAY = "1";
    public final static String CONSTANT_VARIABLE_HOT = "0";        //0、周热门
    public final static String CONSTANT_VARIABLE_NEW = "1";         //1、周最新
    public final static String CONSTANT_VARIABLE_TAYAL = "2";       //总排行


    @BindView(R.id.recyclerView)
    RecyclerView listView;
    @BindView(R.id.swipelayout)
    SwipeRefreshLayout swipeLayout;

    private String keyword;
    private List<CircleBean> types;
    private CircleBean mBean;

    private CircleListAdapter mAdapter;
    private CircleListContract.Presenter mPresenter;

    private String sortType = CONSTANT_VARIABLE_HOT;
    private String payType = CONSTANT_VARIABLE_ALL;

    private Integer typeId = null;

    private TextView tvHot;
    private TextView tvNew;
    private TextView tvTotal;
    private LinearLayout tvType;

    private PopList mPopList;
    private PopChoose mPopChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_list);

    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_circle_find));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("筛选", this);
        swipeLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        listView.addHeadView(getHeadView(listView));
        mAdapter = new CircleListAdapter(this, new ArrayList<CircleBean>(), R.layout.item_circle_find_list);
        listView.setAdapter(mAdapter);
        mPopList = new PopList(this, R.layout.dialog_bottom_list);

    }

    @Override
    public void initListener() {
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mBean = mAdapter.getList().get(position);
                if (mBean.getIsJoin() == 0) {
                    String url = mBean.getJoinUrl();
                    String name = mBean.getGroupName();
                    double money = mBean.getFee();
                    int id = mBean.getId();
                    String isAudit = mBean.getIsAudit();

                    Intent intent = new Intent(CircleListActivity.this, CircleJoinActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("isAudit", isAudit);
                    intent.putExtra(Constant.INTENT_DATA, money);
                    startActivityForResult(intent, 0);
                } else {
                    GotoUtil.goToActivity(CircleListActivity.this, CircleMainActivity.class, 0, mBean);
                }

            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                mPresenter.refreshData();
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreData();
            }
        });

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBean = (CircleBean) v.getTag();
                String url = mBean.getJoinUrl();
                String name = mBean.getGroupName();
                double money = mBean.getFee();
                int id = mBean.getId();
                String isAudit = mBean.getIsAudit();

                Intent intent = new Intent(CircleListActivity.this, CircleJoinActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("isAudit", isAudit);
                intent.putExtra(Constant.INTENT_DATA, money);
                startActivity(intent);
            }
        });

        mPopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typeId = types.get(position).getId();
                listView.reLoadFinish();
                if (position == 0) {
                    mPresenter.getData(keyword, payType, sortType, null);
                } else {
                    mPresenter.getData(keyword, payType, sortType, typeId);
                }
                for (CircleBean bean : types) {
                    if (bean.isSelect()) {
                        bean.setSelect(false);
                        break;
                    }
                }
                types.get(position).setSelect(true);
                mPopList.getMAdapter().setPostion(position);
                mPopList.notifyDataSetChanged();
            }
        });
    }


    private View getHeadView(ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.find_list_search_view, viewGroup, false);
        View viewById = inflate.findViewById(R.id.et_input_search);
        View bar = inflate.findViewById(R.id.bar);
        bar.setVisibility(View.VISIBLE);
        tvHot = (TextView) inflate.findViewById(R.id.tv_hot);
        tvNew = (TextView) inflate.findViewById(R.id.tv_new);
        tvType = (LinearLayout) inflate.findViewById(R.id.tv_type);
        tvTotal = (TextView) inflate.findViewById(R.id.tatal);

        tvHot.setOnClickListener(this);
        tvNew.setOnClickListener(this);
        tvType.setOnClickListener(this);
        tvTotal.setOnClickListener(this);

        viewById.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            //筛选
            case R.id.headRightButton:
                if (mPopChoose == null) {
                    mPopChoose = new PopChoose(this);
                    mPopChoose.anchorView(getBaseHeadView().getHeadRightButton())
                            .location(0, -30)
                            .showAnim(new PopEnterAnim().duration(200))
                            .dismissAnim(new PopExitAnim().duration(200))
                            .gravity(Gravity.BOTTOM)
                            .cornerRadius(4)
                            .bubbleColor(Color.parseColor("#ffffff"))
                            .setBgAlpha(0.1f)
                            .setMlistenner(this);
                }
                mPopChoose.show();
                break;

            //热门
            case R.id.tv_hot:
                reSetTextColor();
                setTextColor(v);
                sortType = CONSTANT_VARIABLE_HOT;
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                break;

            //最新
            case R.id.tv_new:
                reSetTextColor();
                setTextColor(v);
                sortType = CONSTANT_VARIABLE_NEW;
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                break;

            //总量
            case R.id.tatal:
                reSetTextColor();
                setTextColor(v);
                sortType = CONSTANT_VARIABLE_TAYAL;
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                break;

            //搜索
            case R.id.et_input_search:
                NewSearchActivity.jumpToSearch(CircleListActivity.this, "6");
                break;

            //付费
            case R.id.btn_pay_1:
                payType = CONSTANT_VARIABLE_PAY;
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                break;

            //全部
            case R.id.btn_all:
                payType = CONSTANT_VARIABLE_ALL;
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                break;
            //免费
            case R.id.btn_free_1:
                payType = CONSTANT_VARIABLE_FREE;
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                break;

            //分类
            case R.id.tv_type:
                if (types == null) return;
                mPopList.showPop(tvType, 0, 0);
                break;
        }
    }


    private void setTextColor(View v) {
        TextView txt = (TextView) v;
        txt.setTextColor(Color.parseColor("#2b8cff"));
    }

    private void reSetTextColor() {
        tvHot.setTextColor(Color.parseColor("#666666"));
        tvNew.setTextColor(Color.parseColor("#666666"));
        tvTotal.setTextColor(Color.parseColor("#666666"));
//        tvType.setTextColor(Color.parseColor("#666666"));
        //tvChoose.setTextColor(Color.parseColor("#666666"));
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        new CircleListPresenter(this);
        mPresenter.getData(keyword, payType, sortType, typeId);
    }


    @Override
    public void showData(List<CircleBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
        if (types == null) {
            mPresenter.getListType();
        }
    }

    @Override
    public void showListType(List<CircleBean> datas) {
        types = new ArrayList<>();
        CircleBean bean = new CircleBean();
        bean.setTypeName("全部");
        types.add(bean);
        types.addAll(datas);
        if (mPopList != null) {
            mPopList.setData(types);
        }
    }

    @Override
    public void showRefreshData(List<CircleBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoadMoreData(List<CircleBean> datas) {
        listView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoLoadMore() {
        listView.loadFinish();
    }

    @Override
    public void setPresenter(CircleListContract.Presenter presenter) {
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
        ToastUtil.showShort(MyApplication.getInstance(), "暂时没有数据");
    }

    @Override
    public void showReLoad() {
    }

    @Override
    public void showError(String info) {
        if (mAdapter.getItemCount() == 0) {
            getBaseEmptyView().showEmptyView(R.drawable.load_error, info, null);
        } else {
            ToastUtil.showShort(this, info);
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.reLoadFinish();
                mPresenter.getData(keyword, payType, sortType, typeId);
                getBaseEmptyView().hideEmptyView();
            }
        });
    }


    //退出圈子消息
    @Subscribe(sticky = true)
    public void onEvent(EventCircleIntro bean) {
        if (bean.status == EventCircleIntro.EXIT || bean.status == EventCircleIntro.DELETE) {
            listView.reLoadFinish();
            mPresenter.getData(keyword, payType, sortType, typeId);
        } else if (bean.status == EventCircleIntro.ENTER) {
            if (mBean != null) {
                int posistion = mAdapter.getList().indexOf(mBean);
                mBean.setIsJoin(1);
                listView.notifyItemChanged(posistion);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
    }

}
