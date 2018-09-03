package com.gxtc.huchuan.ui.live;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ChannelPagerAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.live.liveroomlist.LiveRoomListActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by zzg on 2017/10/15
 */

public class LiveChanelFragment extends BaseTitleFragment implements View.OnClickListener {

    @BindView(R.id.iv_news_search) ImageView             ivSearch;
    @BindView(R.id.pss_tab)        MPagerSlidingTabStrip pssTab;
    @BindView(R.id.vp_fragment)    ViewPager             viewPager;
    @BindView(R.id.iv_back)        View                  back;

    private Fragment fragment;
    private Bundle   bundle;

    private ArrayList<Fragment> container = new ArrayList<>();
    private ChannelPagerAdapter mAdapter;

    private View header;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_live_chanel, container, false);
        header = getLayoutInflater().inflate(R.layout.search_head_view,getBaseHeadView().getParentView(),false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF,R.id.headBackButton);
        params.addRule(RelativeLayout.LEFT_OF,R.id.headRightLinearLayout);
        getBaseHeadView().getParentView().addView(header);
        getBaseHeadView().hideHeadLine();
        header.findViewById(R.id.search_layout).setBackground(getResources().getDrawable(R.drawable.shape_search_btn));
        header.findViewById(R.id.et_input_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewSearchActivity.jumpToSearch(getActivity(),NewSearchActivity.TYPE_LIVE);
            }
        });
        getBaseHeadView().showBackButton( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        getBaseHeadView().showHeadRightButton("已关注" ,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnWDGZ();
            }
        });
        getBaseHeadView().getHeadRightButton().setTextColor(getResources().getColor(R.color.text_color_333));
        return view;
    }

    @Override
    public void initListener() {
        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
                params.topMargin = (int) ((getResources().getDimension(R.dimen.actionBar_height) - header.getHeight()) / 2);
                header.setLayoutParams(params);
                header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void initData() {
        getLiveType();
    }


    @OnClick({R.id.iv_news_search, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.iv_news_search:
//                NewSearchActivity.jumpToSearch(getActivity(), "2");
//                break;
//            case R.id.iv_back:
//                getActivity().finish();
//                break;
            case R.id.headRightButton:

                break;
        }
    }

    /**
     * 获取Column栏目 数据
     */
    private void initColumnData(List<ChatInfosBean> datas) {
        ChatInfosBean recommend = new ChatInfosBean();
        recommend.setTypeName("推荐");
        recommend.setId("-1");
        datas.add(0, recommend);

        List<String> titles = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            fragment = new LiveFragment();
            bundle = new Bundle();
            bundle.putInt("position", i);
            bundle.putString("typeCode", datas.get(i).getId());
            fragment.setArguments(bundle);
            container.add(fragment);
            titles.add(datas.get(i).getTypeName());
        }

        mAdapter = new ChannelPagerAdapter(getChildFragmentManager(), this.container, titles);

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.getParent().requestDisallowInterceptTouchEvent(true);

        pssTab.setViewPager(viewPager);
    }


    private void getLiveType() {
        getBaseLoadingView().showLoading();
        Subscription sub = AllApi.getInstance().getLiveType().observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(
                        new ApiCallBack<List<ChatInfosBean>>() {
                            @Override
                            public void onSuccess(List<ChatInfosBean> data) {
                                getBaseLoadingView().hideLoading();
                                initColumnData(data);

                                Observable.just(data).subscribeOn(Schedulers.io()).subscribe(
                                        new Action1<List<ChatInfosBean>>() {
                                            @Override
                                            public void call(List<ChatInfosBean> datas) {
                                                ArrayList<ChatInfosBean> temp = new ArrayList<ChatInfosBean>();
                                                temp.addAll(datas);
                                                ACache.get(MyApplication.getInstance()).put("tag_home_head_bean", temp);
                                            }
                                        });
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                readCache();
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void readCache() {
        getBaseLoadingView().hideLoading();
        Observable.just("tag_home_head_bean")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<ChatInfosBean>>() {
                    @Override
                    public ArrayList<ChatInfosBean> call(String s) {
                        return (ArrayList<ChatInfosBean>) ACache.get(MyApplication.getInstance()).getAsObject("tag_home_head_bean");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<ChatInfosBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove("tag_home_head_bean");
                        getLiveType();
                    }

                    @Override
                    public void onNext(ArrayList<ChatInfosBean> datas) {
                        if(datas != null && datas.size() > 0){
                            initColumnData(datas);
                        }else{
                            if(getBaseHeadView() != null){
                                getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getLiveType();
                                        getBaseEmptyView().hideEmptyView();
                                    }
                                });
                            }
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    private void btnWDGZ() {
        if (UserManager.getInstance().isLogin()) {
            LiveRoomListActivity.startActivity(getContext(), "已关注讲师");
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
    }

}
