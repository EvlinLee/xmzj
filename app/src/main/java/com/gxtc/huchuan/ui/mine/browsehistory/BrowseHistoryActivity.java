package com.gxtc.huchuan.ui.mine.browsehistory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.gxtc.huchuan.adapter.BrowseHistoryAdapter;
import com.gxtc.huchuan.bean.BrowseHistoryBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectMallDetailBean;
import com.gxtc.huchuan.bean.ConversationTextBean;
import com.gxtc.huchuan.bean.CustomCollectBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.event.EventBrowseHistoryBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.collect.CollectActivity;
import com.gxtc.huchuan.ui.mine.collect.ConversationCollectDeteilActivity;
import com.gxtc.huchuan.ui.mine.collectresolve.CollectResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.model.UIMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Describe:浏览历史
 * Created by ALing on 2017/5/16 .
 */

public class BrowseHistoryActivity extends BaseTitleActivity implements BrowseHistoryContract.View,
        View.OnClickListener {

    @BindView(R.id.tv_del)         TextView           mTvDel;
    @BindView(R.id.tv_choose_all)  TextView           mTvChooseAll;
    @BindView(R.id.rc_list)        RecyclerView       mRcList;
    @BindView(R.id.ll_edit_bottom) LinearLayout       mLlEditBottom;
    @BindView(R.id.swipe_layout)   SwipeRefreshLayout mSwipeLayout;

    private Button               headRightButton;
    private BrowseHistoryAdapter adapter;

    private BrowseHistoryContract.Presenter mPresenter;
    private boolean                         isEditable;     //是否可编辑
    private List<String>                    delIdList;
    int count = 0;
    private CircleShareHandler mShareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_browser_record));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton(getString(R.string.label_edit), this);

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initData() {
        super.initData();
        new BrowseHistoryPresenter(this);
        mPresenter.getData(false);
        mShareHandler = new CircleShareHandler(this);
        delIdList = new ArrayList<>();
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore();       //加载更多

            }
        });
        adapter = new BrowseHistoryAdapter(this, new ArrayList<BrowseHistoryBean>(), R.layout.item_browse_history);
        mRcList.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String       browseType = adapter.getList().get(position).getBrowseType();
                String       isDel      = adapter.getList().get(position).getIsDel();
                final String id         = adapter.getList().get(position).getBrowseId();

                if (!"0".equals(isDel)) {
                    ToastUtil.showShort(MyApplication.getInstance(), "该内容已被删除，仅供浏览");
                    return;
                }

                //1：新闻文章，2：课程，3：交易信息，4：圈子
                switch (browseType) {
                    //新闻文章
                    case "1":
                        mShareHandler.getNewsData(id);
                        break;

                    //话题
                    case "2":
                        mShareHandler.getLiveInfo(id, null);
                        break;

                    //交易信息
                    case "3":
                        GoodsDetailedActivity.startActivity(BrowseHistoryActivity.this, id);
                        break;

                    //圈子动态
                    case "4":
                        mShareHandler.getCircleInfo(id, new ApiCallBack<CircleBean>() {
                            @Override
                            public void onSuccess(CircleBean data) {
                                CircleBean bean = (CircleBean) data;
                                if(bean != null && adapter != null){
                                    //未加入圈子
                                    if(bean.getIsJoin() == 0){
                                        Intent joinIntent = new Intent(BrowseHistoryActivity.this,CircleJoinActivity.class);
                                        joinIntent.putExtra("byLiveId",Integer.valueOf(id));
                                        joinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(joinIntent);

                                    }else{
                                        Intent intent = new Intent(BrowseHistoryActivity.this, CircleMainActivity.class);
                                        intent.putExtra("groupId",bean.getId());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if(adapter != null){
                                    ToastUtil.showShort(MyApplication.getInstance(), message);
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    @OnClick({R.id.tv_choose_all, R.id.tv_del})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            //编辑
            case R.id.headRightButton:
                headRightButton = (Button) this.findViewById(R.id.headRightButton);
                if (isEditable == false) {
                    isEditable = true;
                    mSwipeLayout.setEnabled(false);
                    headRightButton.setText(R.string.label_cancel);
                    for (int i = 0; i < adapter.getList().size(); i++) {
                        adapter.getList().get(i).setShow(true);
                        mRcList.notifyItemChanged(i);
                    }
                    mLlEditBottom.setVisibility(View.VISIBLE);
                } else {
                    isEditable = false;
                    mSwipeLayout.setEnabled(true);
                    headRightButton.setText(R.string.label_edit);
                    for (int i = 0; i < adapter.getList().size(); i++) {
                        if (adapter.getList().get(i).isShow()) {
                            adapter.getList().get(i).setShow(false);
                            mRcList.notifyItemChanged(i);
                        }
                    }
                    mLlEditBottom.setVisibility(View.GONE);
                }
                break;
            //全选
            case R.id.tv_choose_all:
                for (int i = 0; i < adapter.getList().size(); i++) {
                    adapter.getList().get(i).setCheck(true);
                    mRcList.notifyItemChanged(i);


                }
                break;
            //删除
            case R.id.tv_del:
                showDeleteDialog();
                break;
        }
    }

    //删除
    private AlertDialog mDialog;

    private void showDeleteDialog() {
        if (count == 0) {
            ToastUtil.showShort(BrowseHistoryActivity.this, "请选择要删除的条目");
            return;
        }
        mDialog = DialogUtil.createDialog(this, "提示", "确定要删除" + count + "条记录吗？", "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        Log.i("tag", "clickRightButton: " + delIdList.toString());
                        mPresenter.deleteBrowseRecord(UserManager.getInstance().getToken(),
                                (ArrayList<String>) delIdList);

                        mDialog.dismiss();
                    }

                });
        mDialog.show();
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<BrowseHistoryBean> datas) {
        getBaseEmptyView().hideEmptyView();
        mRcList.notifyChangeData(datas, adapter);
    }

    @Override
    public void showRefreshFinish(List<BrowseHistoryBean> datas) {
        mRcList.notifyChangeData(datas, adapter);
    }

    @Override
    public void showDelResult(List<Object> datas) {
        mSwipeLayout.setEnabled(true);
        ToastUtil.showShort(this, getString(R.string.toast_del_success));
        mLlEditBottom.setVisibility(View.GONE);
        isEditable = false;
        headRightButton.setText(R.string.label_edit);
        count = 0;
        mTvDel.setText("删除(" + count + ")");
        mPresenter.getData(false);
    }

    @Override
    public void showLoadMore(List<BrowseHistoryBean> datas) {
        mRcList.changeData(datas, adapter);
    }

    //没有加载更多数据
    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mSwipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {
        mPresenter.getData(false);
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    @Override
    public void setPresenter(BrowseHistoryContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
        mShareHandler.destroy();
    }

    /**
     * 删除按钮
     */
    @Subscribe
    public void onEvent(EventBrowseHistoryBean bean) {

        String chatInfoId = bean.getDelIds();
        if (bean.isSelected()) {
            count++;
            delIdList.add(chatInfoId);
        } else {
            count--;
            if (count < 0) {
                count = 0;
            }
            delIdList.remove(chatInfoId);
        }

        mTvDel.setText("删除(" + count + ")");
    }

}
