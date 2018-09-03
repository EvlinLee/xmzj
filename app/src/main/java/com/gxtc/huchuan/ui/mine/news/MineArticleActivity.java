package com.gxtc.huchuan.ui.mine.news;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MineArticleAdapter;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.model.AppenGroudBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;
import com.gxtc.huchuan.ui.mine.news.applyauthor.ApplyAuthorActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/3/30.
 * 个人发布的文章
 */

public class MineArticleActivity extends BaseTitleActivity implements MineArticleContract.View,
        View.OnClickListener {

    @BindView(R.id.rl_news_collect) RecyclerView       mRecyclerView;
    @BindView(R.id.sw_news_collect) SwipeRefreshLayout swNewsCollect;

    private MineArticleContract.Presenter mPresenter;
    private MineArticleAdapter            mAdapter;
    private List<NewsBean>                mDatas;

    private List<Integer> mGroupIds = new ArrayList<>();    //同步的圈子id
    private VertifanceFlowDialog mVertifanceFlowDialog;

    private  AppenGroudBean mAppenGroudBean;      //获取已经同步的
    private  IssueListDialog mIssueListDialog; //同步圈子的dialog
    private  AlertDialog mAlertDialog;
    private ArrayList<String> listGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_article);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("我的文章").showBackButton(this).showHeadRightImageButton(R.drawable.nav_icon_add, this);
        mDatas = new ArrayList<>();
        swNewsCollect.setColorSchemeResources(Constant.REFRESH_COLOR);
        initRecyCleView();
    }

    @Override
    public void initData() {
        new MineArticlePresenter(this);
        mPresenter.getData(false);
        if ("0".equals(UserManager.getInstance().getIsAuthor())) {
            showDialog();
        } else if ("2".equals(UserManager.getInstance().getIsAuthor())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setMessage("您现在还不是作者，您提交的申请在审核中！");
            dialog.setCancelable(false);
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MineArticleActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }

    }

    private void showDialog() {
        mDialog = DialogUtil.createDialog(this, "提示", "您还不是作者不能发表文章，请申请成为作者！", "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        MineArticleActivity.this.finish();
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        GotoUtil.goToActivity(MineArticleActivity.this, ApplyAuthorActivity.class);
                        MineArticleActivity.this.finish();
                        mDialog.dismiss();
                    }

                });
        mDialog.show();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                MineArticleActivity.this.finish();
                break;

            case R.id.HeadRightImageButton:
                GotoUtil.goToActivityForResult(this, ArticleResolveActivity.class, 0);
                break;

            //点击列表
            case R.id.content:
                NewsBean bean = (NewsBean) view.getTag();
                if( 1 == bean.getIsDeploy()){
                    /*Intent intent = new Intent(this,ArticleResolveActivity.class);
                    intent.putExtra("bean",bean);
                    startActivityForResult(intent,0);*/

                    Intent intent = new Intent(MineArticleActivity.this, NewsWebActivity.class);
                    intent.putExtra("data", bean);
                    intent.putExtra("edit", 1);
                    startActivityForResult(intent, ArticleResolveActivity.REQUEST_CODE_EDIT);
                    return;
                }
                switch (bean.getAudit()) {
                    case "0":
                        showVertifanceDialog(bean);
                        break;

                    case "1":
                        Intent intent = new Intent(MineArticleActivity.this, NewsWebActivity.class);
                        intent.putExtra("data", bean);
                        intent.putExtra("edit", 1);
                        startActivityForResult(intent, ArticleResolveActivity.REQUEST_CODE_EDIT);
                        break;

                    case "2":
                        mNewsBean = bean;
                        showVertifanceDialog(bean);
                        break;

                    case "3":
                        mNewsBean = bean;
                        showVertifanceDialog(bean);
                        break;
                }
                break;

            //删除操作
            case R.id.tv_delete:
                NewsBean dBean = (NewsBean) view.getTag();
                showDeleteDialog(dBean);
                break;

            //同步
            case R.id.tv_tongbu:
                NewsBean tBean = (NewsBean) view.getTag();
                currentBean = tBean;
                showDialog(tBean);
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


    /**
     * 删除收藏文章
     */
    private void showDeleteDialog(final NewsBean bean) {
        mAlertDialog =  DialogUtil.showDeportDialog(this, false, null,"确定要删除文章吗", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    deleteCollect(UserManager.getInstance().getToken(), bean.getId());
                }
                mAlertDialog.dismiss();
            }
        });
    }


    /**
     * 删除文章成功
     */
    private void deleteCollect(String token, final String newsIds) {
        if (!TextUtils.isEmpty(newsIds)){
            Subscription sub =
                    AllApi.getInstance()
                          .deleteNews(token, newsIds)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                @Override
                                public void onSuccess(Object data) {
                                    if(mAdapter != null){
                                        for (int i = 0; i < mAdapter.getList().size(); i++) {
                                            if (newsIds.equals(mAdapter.getList().get(i).getId())) {
                                                mRecyclerView.removeData(mAdapter, i);
                                                break;
                                            }
                                        }
                                        if(mAdapter.getList().size() <= 0){
                                            getBaseEmptyView().showEmptyView();
                                        }
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    LoginErrorCodeUtil.showHaveTokenError(MineArticleActivity.this, errorCode, message);
                                }
                            }));
            RxTaskHelper.getInstance().addTask(this,sub);

        } else{
            ToastUtil.showShort(MineArticleActivity.this, "请选择文章");
        }

    }

    /**
     * 新闻列表
     */
    private void initRecyCleView() {
        int padding = getResources().getDimensionPixelSize(R.dimen.margin_small);
        TextView tvHint = new TextView(this);
        tvHint.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textSize_s));
        tvHint.setText(getString(R.string.news_issue_hint));
        tvHint.setTextColor(getResources().getColor(R.color.text_color_999));
        tvHint.setPadding(padding, padding, padding, padding);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.addHeadView(tvHint);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MineArticleActivity.this,
                LinearLayoutManager.HORIZONTAL));
    }

    private NewsBean mNewsBean;
    private NewsBean currentBean;
    private NormalDialog mNormalDialog;

    @Override
    public void showData(final List<NewsBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        getBaseEmptyView().hideEmptyView();
        mAdapter = new MineArticleAdapter(this, mDatas, R.layout.item_mine_article);
        mAdapter.setOnClickListener(this);
        mAdapter.setOnReItemOnLongClickListener(
                new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        if (position < datas.size()) {
                            if("1".equals(mAdapter.getList().get(position).getAudit())){
                                showDialog(datas.get(position));
                            }else{
                                mNormalDialog =
                                DialogUtil.showNormalDialog(MineArticleActivity.this,
                                        getString(R.string.models_updata_hint),
                                        getString(R.string.mine_article_sny_failed),
                                        getString(R.string.label_confirm), new OnBtnClickL() {
                                            @Override
                                            public void onBtnClick() {
                                                mNormalDialog.dismiss();
                                            }
                                        });
                            }
                        }
                    }
                });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showVertifanceDialog(final NewsBean bean) {
        mVertifanceFlowDialog = new VertifanceFlowDialog(this);
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("2".equals(bean.getAudit())) {
                    /*Intent intent = new Intent(MineArticleActivity.this, ArticleResolveActivity.class);
                    intent.putExtra("bean", bean);
                    intent.putExtra("edit", 1);
                    startActivityForResult(intent, 0);*/

                    Intent intent = new Intent(MineArticleActivity.this, NewsWebActivity.class);
                    intent.putExtra("data", bean);
                    intent.putExtra("edit", 1);
                    startActivityForResult(intent, ArticleResolveActivity.REQUEST_CODE_EDIT);
                }
                mVertifanceFlowDialog.dismiss();
            }
        });
        if ("0".equals(bean.getAudit())) {
            mVertifanceFlowDialog.setFlowStatus("审核中...");
            mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
        } else {
            checkCicle(bean);
        }
    }

    public void checkCicle(final NewsBean bean) {
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(), bean.getId(),
                Constant.STATUE_LINKTYPE_ARTICLE, new ApiCallBack<CheckBean>() {

                    @Override
                    public void onSuccess(CheckBean data) {
                        if (data == null) return;
                        switch (bean.getAudit()) {
                            case "2":
                                mVertifanceFlowDialog.setFlowStatus(getString(R.string.issue_deal_faild) + data.getContent());
                                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
                                mVertifanceFlowDialog.setUpButtonText(getString(R.string.mine_article_reedit));
                                break;

                            case "3":
                                mVertifanceFlowDialog.setFlowStatus("审核失败:" + data.getContent());//审核失败
                                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
                                break;
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MineArticleActivity.this, message);
                    }
                }).addTask(this);
    }

    public void getlistAppendGroup(final NewsBean bean,final boolean flag) {
        HashMap<String, String> map = new HashMap<>();
        map.put("linkId", bean.getId());
        map.put("type", "1");//同步类型1、文章；2、课堂；3、文件
        MineApi.getInstance().listAppendGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<AppenGroudBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mAppenGroudBean = (AppenGroudBean) data;
                        if(mAppenGroudBean.getGroupIds().size() > 0)
                             getCircleName(flag);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MineArticleActivity.this, message);
                    }
                }));

    }

    private void getCircleName(final boolean flag){
        Subscription sub =
                AllApi.getInstance()
                        .listByUser(UserManager.getInstance().getToken(), "0")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                                if(flag) {
                                    listGroupName = new ArrayList<>();
                                    for (MineCircleBean bean : (List<MineCircleBean>) data) {
                                        if (mAppenGroudBean.getGroupIds().contains(bean.getId())) {
                                            listGroupName.add(bean.getGroupName());
                                        }
                                    }
                                    mIssueListDialog.changeTongbuName(listGroupName);
                                }

                            }

                            @Override
                            public void onError(String errorCode, String message) {}
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);

    }

    private void showDialog(final NewsBean bean) {
        getlistAppendGroup(bean,true);
        mIssueListDialog = new IssueListDialog(this, new String[]{});
        mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(MineArticleActivity.this, SyncIssueInCircleActivity.class);
                        intent.putExtra("default", -1);
                        intent.putExtra("type",3);
                        intent.putExtra("linkId",bean.getId());
                        if (mAppenGroudBean != null) {
                            intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mAppenGroudBean.getGroupIds());
                        }
                        MineArticleActivity.this.startActivityForResult(intent, 666);
                    } else if (v.getId() == R.id.tv_sure) {
                        sysGroup(bean);
                        mIssueListDialog.dismiss();
                    }
                }
            });
        mIssueListDialog.show();
    }

    //同步文章到圈子 同步类型1、文章；2、课堂；3、文件
    private void sysGroup(NewsBean bean) {
        if (mGroupIds == null || mGroupIds.size() < 1) return;

        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
        map.put("groupIds", groupids);
        map.put("linkId", bean.getId());
        map.put("type", "1");
        Subscription sub = MineApi.getInstance().appendToGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(MineArticleActivity.this, "同步成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MineArticleActivity.this, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private AlertDialog mDialog;

    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(MineArticleActivity.this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(MineArticleActivity.this);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(MineArticleContract.Presenter presenter) {
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
        getBaseEmptyView().showEmptyContent();
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
        ViewStub stub     = (ViewStub) findViewById(R.id.vs);
        View     view     = stub.inflate();
        TextView textView = (TextView) view.findViewById(R.id.emptyTextView);
        textView.setText("您的网络好像有问题");
    }


    @Override
    public void showRefreshFinish(List<NewsBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMore(List<NewsBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(currentBean != null)
            getlistAppendGroup(currentBean,false);
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.requestCode.MINE_ARTICLE == requestCode && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }

        if (resultCode == RESULT_OK) {
            mPresenter.getData(false);
        }

        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            ArrayList<MineCircleBean> selectData = data.getParcelableArrayListExtra("select_data");
            mGroupIds.clear();
            mIssueListDialog.clearText();
            if (selectData.size() > 0) {
                listGroupName = new ArrayList<>();
                for (MineCircleBean bean1 : selectData) {
                    listGroupName.add(bean1.getGroupName());
                    mGroupIds.add(bean1.getId());
                }
                mIssueListDialog.changeTongbuName(listGroupName);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
        if (mNewsBean != null && (mNewsBean.getAudit().equals("2") || mNewsBean.getAudit().equals(
                "3"))) {
            CheaekUtil.getInstance().cancelTask(this);
        }
    }
}
