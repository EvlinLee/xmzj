package com.gxtc.huchuan.ui.news;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.flyco.dialog.utils.StatusBarUtils;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.NewsAdapter;
import com.gxtc.huchuan.adapter.VideoNewsAdapter;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventThumbsupBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.pop.PopShield;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * Created by sjr on 2017/3/30.
 * 视频新闻列表页
 */

public class VideoNewsActivity extends BaseTitleActivity implements VideoNewsContract.View {

    @BindView(R.id.rl_video_news)    RecyclerView       mRecyclerView;
    @BindView(R.id.swipe_video_news) SwipeRefreshLayout swipeVideoNews;

    private VideoNewsContract.Presenter mPresenter;
    private VideoNewsAdapter            mAdapter;
    private AlertDialog                 mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_news_video);
        EventBusUtil.register(this);
    }

    /**   http://blog.csdn.net/acerhphp/article/details/62889468
     *   JZVideoPlayer 内部的AudioManager会对Activity持有一个强引用，而AudioManager的生命周期比较长，导致这个Activity始终无法被回收
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)){
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });
    }


    @Override
    public void initData() {
        swipeVideoNews.setColorSchemeResources(Constant.REFRESH_COLOR);
        getBaseHeadView().showTitle(getString(R.string.title_video));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoNewsActivity.this.finish();
            }
        });
        initRecyCleView();

        new VideoNewsPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void initListener() {
        swipeVideoNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
            }
        });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    private void initRecyCleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    GotoUtil.goToActivity(VideoNewsActivity.this, LoginAndRegisteActivity.class);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(VideoNewsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swipeVideoNews.setRefreshing(false);
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


    private List<NewsBean> mDatas = new ArrayList<>();

    @Override
    public void showData(final List<NewsBean> datas) {
        mAdapter = new VideoNewsAdapter(this, datas, R.layout.item_news_video_fragment);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(VideoNewsActivity.this, NewsWebActivity.class);
                intent.putExtra("data", datas.get(position));
                startActivity(intent);
            }
        });

        mAdapter.setOnShieldListener(new VideoNewsAdapter.OnShieldListener() {
            @Override
            public void onShieldVideo(String id) {
                if(UserManager.getInstance().isLogin(VideoNewsActivity.this)){
                    mPresenter.shieldType(id, "", 1, "1");
                }
            }

            @Override
            public void onShieldUser(String userCode) {
                if(UserManager.getInstance().isLogin(VideoNewsActivity.this)) {
                    mPresenter.shieldType("", userCode, 2, "1");
                }
            }
        });
        mDatas.clear();
        mDatas.addAll(datas);
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
    public void showShieldSuccess(String articleId, String targetUserCode) {
        if(mAdapter == null) return;

        List<NewsBean> bean = mAdapter.getList();
        //屏蔽用户
        if(TextUtils.isEmpty(targetUserCode)){
            for (int i = 0; i < bean.size(); i++) {
                if(!TextUtils.isEmpty(articleId) && articleId.equals(bean.get(i).getId())){
                    mRecyclerView.removeData(mAdapter, i);
                    return;
                }
            }

            //屏蔽文章
        }else{
            Iterator<NewsBean> it = mAdapter.getList().iterator();
            while (it.hasNext()){
                NewsBean temp = it.next();
                if(!TextUtils.isEmpty(targetUserCode) && targetUserCode.equals(temp.getUserCode())){
                    it.remove();
                }
            }
            mRecyclerView.notifyChangeData();
        }
    }

    @Override
    public void showShieldError(String articleId, String targetUserCode, String msg) {
        ToastUtil.showShort(getContext(), msg);
    }

    @Subscribe
    public void onEvent(EventThumbsupBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId().equals(bean.newsId)) {
                mDatas.get(i).setIsThumbsup(bean.isThumbsup);
            }
        }
        mRecyclerView.notifyChangeData(mDatas, mAdapter);
    }

    @Subscribe
    public void onEvent(EventCollectBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId().equals(bean.newsId)) {
                mDatas.get(i).setIsCollect(bean.isCollect);
            }
        }
        mRecyclerView.notifyChangeData(mDatas, mAdapter);
    }

    @Override
    public void onBackPressed() {
        if(!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
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
        EventBusUtil.unregister(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
    }
}
