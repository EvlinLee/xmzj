package com.gxtc.huchuan.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.NewsCollectAdapter;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/3/15.
 * 新闻收藏列表
 */

public class NewsCollectActivity extends BaseTitleActivity implements NewsCollectContract.View {

    @BindView(R.id.rl_news_collect)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_news_collect)
    SwipeRefreshLayout swNewsCollect;
    @BindView(R.id.headBackButton)
    ImageButton headBackButton;
    @BindView(R.id.cb_editor)
    CheckBox cbEditor;
    @BindView(R.id.tv_news_collect)
    TextView tvNewsCollect;
    private NewsCollectContract.Presenter mPresenter;
    private NewsCollectAdapter mAdapter;
    private List<NewsBean> mDatas;
    private List<BaseRecyclerAdapter.ViewHolder> holders;
    private List<String> newsIds;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_collect);
        EventBusUtil.register(this);
    }

    @Override
    public void initData() {
        mDatas = new ArrayList<>();
        holders = new ArrayList<>();
        newsIds = new ArrayList<>();

        swNewsCollect.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2, R.color.refresh_color3,
                R.color.refresh_color4);

        initRecyCleView();
        new NewsCollectPresenter(this);
        mPresenter.getData(false);
    }


    @OnClick({R.id.headBackButton, R.id.tv_news_collect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                NewsCollectActivity.this.finish();
                break;
            case R.id.tv_news_collect://删除操作
                showDeleteDialog();
                break;
        }
    }

    @Override
    public void initListener() {

        swNewsCollect.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                mRecyclerView.reLoadFinish();
            }
        });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });

    }


    Subscription subThumbsup;

    /**
     * 删除收藏文章
     */
    private void showDeleteDialog() {
        mDialog = DialogUtil.createDialog(this, "提示", "确定要删除" + count + "条收藏吗？", "取消",
                "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        for (int i = 0; i < mAdapter.getList().size(); i++) {
                            NewsBean bean = mAdapter.getList().get(i);
                            if (bean.isCheck()) {//选中的时候
                                newsIds.add(bean.getId());
                            }
                        }
                        deleteCollect(UserManager.getInstance().getToken(), (ArrayList<String>) newsIds);

                        mDialog.dismiss();
                    }

                });
        mDialog.show();
    }


    /**
     * 取消收藏
     * <p>
     * 2017/3/30   这个方法有个问题，就是本来我是应该本地刷新的，但是莫名其妙只能删除第一个，项目赶为了避免bug删除后重新刷新数据了，后续要优化
     * 2017/4/6 上述问题已解决 删除时list的数据变了
     *
     * @param token
     * @param newsIds
     */
    private void deleteCollect(String token, final ArrayList<String> newsIds) {
        if (newsIds.size() > 0)
            subThumbsup = AllApi.getInstance().getCollect(token, newsIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            for (int i = 0; i < mAdapter.getList().size(); i++) {
                                NewsBean bean = mAdapter.getList().get(i);
                                if (bean.isCheck()) {//选中的时候
                                    mAdapter.getList().remove(bean);
                                    i--;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.notifyChangeData();
//                            mPresenter.getData(true);
//                            for (int i = 0; i < newsIds.size(); i++) {
//                                LogUtil.printD("aa:" + newsIds.get(i));
//
//                            }
                            tvNewsCollect.setVisibility(View.GONE);
                            for (int i = 0; i < mDatas.size(); i++) {
                                mDatas.get(i).setShow(false);
                            }
                            newsIds.clear();
                            cbEditor.setChecked(false);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(NewsCollectActivity.this, errorCode, message);
                        }
                    }));
        else ToastUtil.showShort(NewsCollectActivity.this, "请选择文章");

    }

    /**
     * 新闻列表
     */
    private void initRecyCleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(NewsCollectActivity.this, LinearLayoutManager.HORIZONTAL));
    }


    @Override
    public void showData(final List<NewsBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter = new NewsCollectAdapter(this, datas,
                R.layout.item_news_collect_activity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(NewsCollectActivity.this, NewsWebActivity.class);
                intent.putExtra("data", datas.get(position));
                startActivity(intent);
            }
        });
        setEditor(datas);
    }

    private void setEditor(final List<NewsBean> datas) {
        cbEditor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//可以编辑状态
                    cbEditor.setText("取消");
                    for (int i = 0; i < datas.size(); i++) {
                        datas.get(i).setShow(true);
                    }
                    mRecyclerView.notifyChangeData();
                    tvNewsCollect.setVisibility(View.VISIBLE);
                    tvNewsCollect.setText("删除(0)");
                } else {
                    cbEditor.setText("编辑");
                    tvNewsCollect.setVisibility(View.GONE);
                    for (int i = 0; i < datas.size(); i++) {
                        datas.get(i).setShow(false);
                        datas.get(i).setCheck(false);
                    }
                    count = 0;

                    mRecyclerView.notifyChangeData();
                }
            }
        });
    }


    private AlertDialog mDialog;

    @Override
    public void tokenOverdue() {

        Intent intent = new Intent(NewsCollectActivity.this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_COLLECT);
    }

    @Override
    public void setPresenter(NewsCollectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swNewsCollect.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }


    /**
     * 加载更多时网络错误，直接打吐司
     *
     * @param info
     */
    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    /**
     * 初始网络错误，点击重新加载
     */
    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });
    }


    @Override
    public void showRefreshFinish(List<NewsBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
//        cbEditor.setChecked(false);
    }

    @Override
    public void showLoadMore(List<NewsBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    /**
     * 删除按钮
     */
    @Subscribe
    public void onEvent(EventCollectSelectBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (i == bean.getPosition() && bean.isSelected() == true) {
                holders.add(bean.holder);
                count++;
            } else if (i == bean.getPosition() && bean.isSelected() == false) {
                count--;
                if (count < 0) {
                    count = 0;
                }
            }
        }
        tvNewsCollect.setText("删除(" + count + ")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.requestCode.NEWS_COLLECT == requestCode && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (subThumbsup != null && subThumbsup.isUnsubscribed()) {
            subThumbsup.unsubscribe();
        }
        EventBusUtil.unregister(this);
    }
}
