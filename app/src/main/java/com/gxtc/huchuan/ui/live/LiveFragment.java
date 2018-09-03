package com.gxtc.huchuan.ui.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveBannerAdapter;
import com.gxtc.huchuan.adapter.LiveBoutiqueAdapter;
import com.gxtc.huchuan.adapter.LivePopularityAdapter;
import com.gxtc.huchuan.adapter.UnifyClassAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.LiveHeadPageBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.event.PlayEvent;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.hotlist.HotListActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.liveroomlist.LiveRoomListActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.DirectSeedingBackgroundActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicActivity;
import com.gxtc.huchuan.ui.mine.classroom.history.MyHistoryActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AdClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.StatisticsHandler;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.PlayBarView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.ui.MainActivity.STATISTICS;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_TOP_BEST_CLASS;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_TOP_FREE_CLASS;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_TOP_HOT_CLASS;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_TOP_SIGNED_CLASS;


/**
 * Created by zzg on 2018/1/24. 改版后的课程首页
 * 之前的代码太过冗余，无关的代码太多，布局里嵌套的太多，影响性能
 */

public class LiveFragment extends BaseLazyFragment implements LiveContract.View,
        View.OnClickListener {
    public static final  int    LOGINREQUEST = 1;

    @BindView(R.id.live_room)     RecyclerView       mLiveRoom;
    @BindView(R.id.refreshlayout) SwipeRefreshLayout mRefreshlayout;
    @BindView(R.id.playbarview)   PlayBarView        mPlaybarview;

    private                       ConvenientBanner                               mConvenientBanner;
    private                       LiveContract.Presenter                         mPresenter;
    private                       UnifyClassAdapter                              mainAdapter;
    private                       LiveBoutiqueAdapter                            boutiqueAdapter;
    private                       LivePopularityAdapter                          livePopularityAdapter;
    private                       RecyclerView                                   boutiqueLive;    //精品课程
    private                       RecyclerView                                   hotLecturer;   //热门课程
    private                       LinearLayout                                   hotClassRoom;
    private                       LinearLayout                                   youlikeClassroom;

    private int            position;
    private int            mStaus;
    private int            pageSize;
    private String         typeCode;        //课程的分类码
    private int            mStart = 0;

    private final int GOTO_CIRCLE_REQUESTCODE = 1 << 3;
    private List<NewsAdsBean> mAdvertise;
    private ChatInfosBean chatInfosBean;
    private ChatInfosBean currentPlayChatInfosBean;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_live_v3, container, false);
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        super.onGetBundle(bundle);
        position = bundle.getInt("position", 0);
        typeCode = bundle.getString("typeCode");
    }

    @Override
    public void initListener() {
        initLiveRoomAdapter();

        mRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter != null) {
                    mLiveRoom.reLoadFinish();
                    pageSize = 0;
                    mPresenter.getLiveRoom(UserManager.getInstance().getToken(), pageSize, true, typeCode);
                } else {
                    mRefreshlayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void initData() {
        mRefreshlayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        EventBusUtil.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AudioPlayManager.getInstance().isAudioPlaying()) {
//            mRotateView.setRotate(true);
        } else {
//            mRotateView.setRotate(false);
        }
    }


    @Override
    public void showCbBanner(List<NewsAdsBean> advertise) {
        mAdvertise = advertise;
        mConvenientBanner.startTurning(5000);
        mConvenientBanner.setPages(new CBViewHolderCreator<LiveBannerAdapter>() {
            @Override
            public LiveBannerAdapter createHolder() {
                return new LiveBannerAdapter();
            }
        }, mAdvertise);
        mConvenientBanner.setPageIndicator(
                new int[]{R.drawable.news_icon_dot_small, R.drawable.news_icon_dot_big});
        mConvenientBanner.setPageIndicatorAlign(
                ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        mConvenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AdClickUtil.performClick(getActivity(), mAdvertise.get(position));
            }
        });
    }


    @Subscribe
    public void PlayEvenListener(PlayEvent bean) {
        if (bean.getStaus() != mStaus) {
            if (bean.getStaus() == PlayEvent.TOGGLE_STATUS)
                mStaus = mStaus == PlayEvent.START_STATUS ? PlayEvent.PAUSE_STATUS : PlayEvent.START_STATUS;
            switch (mStaus) {
                case PlayEvent.START_STATUS:
//                    mRotateView.setRotate(false);
                    break;
                case PlayEvent.PAUSE_STATUS:
//                    mRotateView.setRotate(true);
                    break;
            }
        }
    }


    @Subscribe
    public void onEvent(EventScorllTopBean bean) {}

    @Override
    public void showHeadIcon(final List<LiveHeadTitleBean> datas) {}


    @Override
    public void showLiveForeshow(final List<ChatInfosBean> datas) {}


    private void ChatItemClick(int type, String id) {

        if(type == 1){
            LiveIntroActivity.startActivity(getActivity(), String.valueOf(id));
        }else{
            SeriesActivity.startActivity(getActivity(), String.valueOf(id), true);
        }
    }

    private AlertDialog dialog;

    private void joinSeriesDialog(final String seriesId) {
        dialog = DialogUtil.showInputDialog(getActivity(), false, "", "要先购买系列课才能进入?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SeriesActivity.startActivity(getActivity(), seriesId, true);
                        dialog.dismiss();
                    }
                });
    }


    private void joinGroupDialog(final long groupId) {
        final NormalDialog dialog = new NormalDialog(getContext());
        dialog.content("要先加载圈子才能进入?").show();
        dialog.btnText("取消", "加入");
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                Intent intent = new Intent(getActivity(), CircleJoinActivity.class);
                intent.putExtra("byLiveId", (int) groupId);
                startActivityForResult(intent, GOTO_CIRCLE_REQUESTCODE);
                dialog.dismiss();
            }
        });
    }


    private void initLiveRoomAdapter() {
        if (position == 0) {
            chatInfosBean = ConversationManager.getInstance().getChatInfosBean();
            if (chatInfosBean != null) {
                currentPlayChatInfosBean = chatInfosBean;
                mPlaybarview.setData(chatInfosBean);
                mPlaybarview.setVisibility(View.VISIBLE);
                if (AudioPlayManager.getInstance().isAudioPlaying()) {
                    mPlaybarview.setStuas(PlayBarView.START_STATUS);
                }
            }
            mPresenter.getData(mStart,4);
            mPresenter.gethotData(0,15);
            View header = View.inflate(getContext(),R.layout.activity_live_header_layout,null);
            mConvenientBanner = header.findViewById(R.id.cb_live_banner);
            int  bannerHeight = (int) WindowUtil.getScaleHeight(16, 7.5f, getContext());
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mConvenientBanner.getLayoutParams();
            params.height = bannerHeight;
            header.findViewById(R.id.tv_update).setOnClickListener(this);
            header.findViewById(R.id.btn_free).setOnClickListener(this);
            header.findViewById(R.id.btn_hot).setOnClickListener(this);
            header.findViewById(R.id.btn_boutique).setOnClickListener(this);
            header.findViewById(R.id.self_btn_ybck).setOnClickListener(this);
            hotClassRoom = header.findViewById(R.id.ll_hot_classroom);
            youlikeClassroom = header.findViewById(R.id.ll_youlike_classroom);
            boutiqueLive =  header.findViewById(R.id.boutiquelive_room);
            hotLecturer =  header.findViewById(R.id.hot_lecturer);

            initRecyclerView();
            mLiveRoom.addHeadView(header);
        }else {
            mPlaybarview.setVisibility(View.GONE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mainAdapter = new UnifyClassAdapter(getContext(), new ArrayList<UnifyClassBean>(), R.layout.item_live_new_room);
        mLiveRoom.setLayoutManager(linearLayoutManager);
        mLiveRoom.setLoadMoreView(R.layout.model_footview_loadmore);
        mLiveRoom.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                pageSize = pageSize + 15;
                mPresenter.getLiveLoadMore(pageSize, typeCode);
            }
        });
        mLiveRoom.setAdapter(mainAdapter);
        mainAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        UnifyClassBean bean = mainAdapter.getList().get(position);
                        ChatItemClick(bean.getType(), bean.getData().getId() + "");

                    }
                });
    }

    private void initRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        boutiqueLive.setLayoutManager(gridLayoutManager);
        boutiqueAdapter = new LiveBoutiqueAdapter(getContext(), new ArrayList<ClassLike>(), R.layout.item_live_boutique_room);
        boutiqueLive.setAdapter(boutiqueAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hotLecturer.setLayoutManager(linearLayoutManager);
        livePopularityAdapter = new LivePopularityAdapter(getContext(), new ArrayList<ClassHotBean>(), R.layout.item_live_popularity_room);
        hotLecturer.setAdapter(livePopularityAdapter);

        livePopularityAdapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
            @Override
            public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {
                ClassHotBean bean = livePopularityAdapter.getList().get(position);
                LiveHostPageActivity.startActivity(getContext(), "1", bean.getId());
            }
        });
        boutiqueAdapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
            @Override
            public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {
                ClassLike bean = boutiqueAdapter.getList().get(position);
                ChatItemClick(Integer.parseInt(bean.getType()), bean.getId());
            }
        });
    }

    @Override
    public void showLiveRoom( List<UnifyClassBean> datas) {
        if (mLiveRoom == null) return;
        mLiveRoom.notifyChangeData(datas, mainAdapter);
    }

    //接收播放事件
    @Subscribe
    public void showCurrentPlayTopic(PlayEvent data) {
        currentPlayChatInfosBean = data.getChatInfosBean();
        switch (data.getStaus()) {
            case PlayEvent.TOGGLE_STATUS:
                mPlaybarview.setStuas(PlayBarView.TOGGLE_STATUS);
                break;

            case PlayEvent.START_STATUS:
                if (position == 0) {
                    mPlaybarview.setVisibility(View.VISIBLE);
                    mPlaybarview.setStuas(PlayBarView.START_STATUS);
                }else {
                    mPlaybarview.setVisibility(View.GONE);
                }
                break;

            case PlayEvent.CLICK_STATUS:
            case PlayEvent.PAUSE_STATUS:
                mPlaybarview.setStuas(PlayBarView.PAUSE_STATUS);
                break;
        }
        mPlaybarview.setData(currentPlayChatInfosBean);
    }


    @Override
    public void showClassDatas(final List<LiveHeadPageBean.TypesChatRoom> datas) {}


    @Override
    public void showloadMoreLiveRoom(List<UnifyClassBean> datas) {
        if (mLiveRoom != null)
            mLiveRoom.changeData(datas, mainAdapter);
    }


    @Override
    public void showNoLoadMoreLiveRoom() {
        if (mLiveRoom != null) {
            mLiveRoom.loadFinish();
        }
    }

    @Override
    public void showData(List<ClassLike> data) {
        if(boutiqueLive == null) return;

                if(data == null || data.size() == 0){
                    if(mStart == 0){
                        youlikeClassroom.setVisibility(View.GONE);
                    }else{
                        ToastUtil.showShort(getContext(), "已经到尽头啦！");
                    }
                    return;
                }else{
                    youlikeClassroom.setVisibility(View.VISIBLE);
                }
                boutiqueAdapter.getList().clear();
                boutiqueLive.notifyChangeData(data, boutiqueAdapter);


    }

    @Override
    public void showHotData(List<ClassHotBean> data) {
        if(data == null || data.size() == 0){
            hotClassRoom.setVisibility(View.GONE);
        }else {
            hotClassRoom.setVisibility(View.VISIBLE);
            hotLecturer.notifyChangeData(data, livePopularityAdapter);
        }
    }

    @Override
    public void tokenOverdue() {}

    @Override
    public void showNetError() {
      /*  getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });*/
    }

    @Override
    public void showError(String info) {
       /* ToastUtil.showShort(this.getContext(), info);
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getLiveRoom(UserManager.getInstance().getToken(), 0,false,typeCode);
            }
        });
        getBaseLoadingView().hideLoading();
        getBaseEmptyView().hideEmptyView();*/
    }

    @Override
    public void showEmpty() {
        getBaseLoadingView().hideLoading();
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showLoad() {
        if (!mRefreshlayout.isRefreshing()) {
            getBaseLoadingView().showLoading();
        }
    }

    @Override
    public void showLoadFinish() {
        if (mRefreshlayout != null) {
            mRefreshlayout.setRefreshing(false);
            getBaseEmptyView().hideEmptyView();
            getBaseLoadingView().hideLoading();
        }

    }

    @Override
    public void showReLoad() {}

    @Override
    public void setPresenter(LiveContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.self_btn_ybck://已报课程
                StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_TOP_SIGNED_CLASS,false);
                btnYBCK();
                break;

            case R.id.btn_free:
                StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_TOP_FREE_CLASS,false);
                gotoHotListActivity("免费课程", "1");
                break;

            case R.id.btn_boutique:
                StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_TOP_BEST_CLASS,false);
                gotoHotListActivity("精品课程", "2");
                break;

            case R.id.btn_hot:
                StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_TOP_HOT_CLASS,false);
                gotoHotListActivity("人气课程", "0");
                break;
            case R.id.tv_update:
                mStart += 4;
                mPresenter.getData(mStart,4);
                break;

        }
    }

    private void gotoHotListActivity(String title, String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("type", type);
        GotoUtil.goToActivityWithData(getActivity(), HotListActivity.class, map);
    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(getActivity(), toClass);
        } else {
            GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
        }
    }

    private void btnYBCK() {
        if (UserManager.getInstance().isLogin(getActivity())) {
            Intent intent = new Intent(LiveFragment.this.getContext(), MyHistoryActivity.class);
            intent.putExtra("title", "已报课程");
            intent.putExtra(MainActivity.STATISTICS_STATUS, MainActivity.STATISTICS);
            startActivity(intent);
        }
    }

    private void btnWDGZ() {
        if (UserManager.getInstance().isLogin()) {
            LiveRoomListActivity.startActivity(getContext(), "我关注的");
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
    }

    private void btnXJKC() {
        if (UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(this, CreateTopicActivity.class);
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

        }
    }

    private void btnGLKC() {
        if (UserManager.getInstance().isLogin()) {
            goToActivity(DirectSeedingBackgroundActivity.class);
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
    }

    int start = 4;

    private void refreshLiveForeshow(String token) {
        Subscription sub = LiveApi.getInstance().getChatInfoAdvanceList(token,
                String.valueOf(start), "4").subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        List<ChatInfosBean> datas = (List<ChatInfosBean>) data;
                        showLiveForeshow(datas);
                        start += 4;
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(LiveFragment.this.getActivity(), errorCode, message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void onDestroy() {
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        StatisticsHandler.Companion.getInstant().destroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGINREQUEST && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            if (mPresenter != null) {
                mPresenter.getLiveRoom(UserManager.getInstance().getToken(), 0, true, typeCode);
            }

        }
        if (resultCode == Constant.ResponseCode.NORMAL_FLAG) {
            mPresenter.getLiveRoom(UserManager.getInstance().getToken(), 0, true, typeCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void lazyLoad() {
        if (mPresenter == null) {
            new LivePresenter(this,STATISTICS);
        }
        readCache();
    }

    private void readCache() {
        String tempToken = "";
        if (UserManager.getInstance().isLogin()) {
            tempToken = UserManager.getInstance().getToken();
        }
        final String finalTempToken = tempToken;
        final String finalTempToken1 = tempToken;
        Observable.just(LiveFragment.class.getSimpleName() + typeCode +"list")
                  .subscribeOn(Schedulers.io())
                  .map(new Func1<String, List<UnifyClassBean>>() {
                    @Override
                    public List<UnifyClassBean> call(String key) {
                        return (List<UnifyClassBean>) ACache.get(MyApplication.getInstance()).getAsObject(key);
                    }
                })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<List<UnifyClassBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove(LiveFragment.class.getSimpleName() + typeCode +"list");
                        mPresenter.getLiveRoom(finalTempToken, 0, false, typeCode);
                        if (position == 0) {
                            if (UserManager.getInstance().isLogin()) {
                                mPresenter.getAdvertise(UserManager.getInstance().getToken());
                            } else {
                                mPresenter.getAdvertise();
                            }
                        }
                    }

                    @Override
                    public void onNext(List<UnifyClassBean> beans) {
                        if(beans != null && beans.size() > 0){
                            showLiveRoom(beans);
                        }
                        mPresenter.getLiveRoom(finalTempToken1, 0, false, typeCode);
                        if (position == 0) {
                            if (UserManager.getInstance().isLogin()) {
                                mPresenter.getAdvertise(UserManager.getInstance().getToken());
                            } else {
                                mPresenter.getAdvertise();
                            }
                        }
                    }
                });
    }
}
