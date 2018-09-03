package com.gxtc.huchuan.ui.news;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ChannelPagerAdapter;
import com.gxtc.huchuan.adapter.VideoNewsAdapter;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectBean;
import com.gxtc.huchuan.bean.event.EventThumbsupBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * 来自 苏修伟 on 2018/5/5.
 */
public class VideoNewsFragment extends BaseLazyFragment implements VideoNewsContract.View{


    @BindView(R.id.pss_tab)             MPagerSlidingTabStrip    pssTab;
    @BindView(R.id.vp_videopage)        ViewPager               videoPage;

    private VideoNewsContract.Presenter mPresenter;
    private ChannelPagerAdapter mAdapter;
    private String[] datas ;
    private List<String> list = new ArrayList<>();
    private ArrayList<Fragment>  container            = new ArrayList<>();

    @Override
    protected void lazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container)  {
        return inflater.inflate(R.layout.fragment_main_news_video, container, false);
    }

    @Override
    public void initListener() {


    }

    @Override
    public void initData() {
        new VideoNewsPresenter(this);
        mPresenter.getData(false);
        datas = getActivity().getResources().getStringArray(R.array.chanal);
        for(String Classtitle : datas){
            VideoNewsItemFragment fragment = new VideoNewsItemFragment();
            container.add(fragment);
            list.add(Classtitle);
        }
        mAdapter = new ChannelPagerAdapter(getChildFragmentManager(), container, list);
        videoPage.setAdapter(mAdapter);
        pssTab.setViewPager(videoPage);
    }



    @Override
    public void tokenOverdue() {

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
        ToastUtil.showShort(getContext(), info);
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



    }

    @Override
    public void showRefreshFinish(List<NewsBean> datas) {

    }

    @Override
    public void showLoadMore(List<NewsBean> datas) {

    }

    @Override
    public void showNoMore() {

    }

    @Override
    public void showShieldSuccess(String articleId, String targetUserCode) {

    }

    @Override
    public void showShieldError(String articleId, String targetUserCode, String msg) {
        ToastUtil.showShort(getContext(), msg);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
