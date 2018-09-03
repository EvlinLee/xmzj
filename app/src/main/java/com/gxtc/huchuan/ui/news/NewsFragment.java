package com.gxtc.huchuan.ui.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ChannelPagerAdapter;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.dao.NewChannelItem;
import com.gxtc.huchuan.bean.event.EventGuideBean;
import com.gxtc.huchuan.data.ChannelManager;
import com.gxtc.huchuan.helper.GuideHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.gxtc.commlibrary.utils.ErrorCodeUtil.TOKEN_OVERDUE_10001;

/**
 * Created by sjr on 2017/2/6.
 * 资讯界面
 * 2017/4/6
 * 老板最新要求，不需要频道筛选功能
 */

public class NewsFragment extends BaseTitleFragment /*implements View.OnClickListener*/ {

    public final static String FIRST_GUIDE = "guide";

    //@BindView(R.id.iv_more)
    //ImageView ivMore;
    @BindView(R.id.iv_news_search) ImageView             ivSearch;
    @BindView(R.id.pss_tab)        MPagerSlidingTabStrip pssTab;
    @BindView(R.id.vp_fragment)    ViewPager             viewPager;
    @BindView(R.id.rl_head)        RelativeLayout        head;
//    @BindView(R.id.rl_type)         RecyclerView          articleType;


    //请求CODE
    //public final static int CHANNELREQUEST = 1;
    // 调整返回的RESULTCODE
    //public final static int CHANNELRESULT = 10;
    //用户选择的新闻分类列表
    private List<NewChannelItem> userChannelList      = new ArrayList<>();
    private List<NewChannelItem> localChannelList      = new ArrayList<>();
    private List<NewChannelItem> defaultOtherChannels = new ArrayList<>();
    private String [] chanalDatas;
    private int [] chanalDatasId;
    //新闻条目
    private ArrayList<Fragment>  container            = new ArrayList<>();
    private ChannelPagerAdapter mAdapter;
    private List<String> datas = new ArrayList<>();
    private Fragment    fragment;
    private Bundle      bundle;
    private AlertDialog mAlertDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        return view;
    }

    @Override
    public void initData() {
        setActionBarTopPadding(head);
        initColumnData();
    }


    @OnClick({R.id.iv_news_search/*, R.id.iv_more*/})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_news_search:
                NewSearchActivity.jumpToSearch(getContext(),"1");
                break;

            //新手指引
            case R.id.img_start:
                EventBusUtil.post(new EventGuideBean(2));
                break;

//            case R.id.iv_more:
//                Intent intent_channel = new Intent(NewsFragment.this.getActivity(), ChannelActivity.class);
//                startActivityForResult(intent_channel, CHANNELREQUEST);
//                break;
        }
    }

    /**
     * 获取Column栏目 数据
     */
    private void initColumnData() {

        List<NewChannelItem> userChannel = ChannelManager.getInstance().getUserChannel();
        if (userChannel != null && !userChannel.isEmpty()) {
            userChannelList = userChannel;
            initVp();
            showGuide();
            geDefauleChannels();

        } else {
            //第一次启动app拿本地写死的数据，在从网络拿存到本地数据库里，下次启动只需从数据库拿就行了，网络拿数据只是为了更新数据库里的数据
            chanalDatas = getActivity().getResources().getStringArray(R.array.chanal);
            chanalDatasId = getActivity().getResources().getIntArray(R.array.chanal_id);
            for(int i = 0;i < chanalDatas.length;i++){
                NewChannelItem localBean = new NewChannelItem();
                localBean.setId(chanalDatasId[i]);
                localBean.setName(chanalDatas[i]);
                userChannelList.add(localBean);
            }
            initVp();
            showGuide();
            geDefauleChannels();
        }
    }

    private void initVp() {
        container.clear();
        datas.clear();

        Subscription sub = Observable.from(userChannelList).subscribe(new Action1<NewChannelItem>() {
            @Override
            public void call(NewChannelItem newChannelItem) {
                fragment = new NewsItemFragment();
                bundle = new Bundle();
                bundle.putInt("id", newChannelItem.getId());
                bundle.putInt("news_type", 1);
                bundle.putString("name", newChannelItem.getName());
                bundle.putString("userCode", "");
                fragment.setArguments(bundle);
                datas.add(newChannelItem.getName());
                container.add(fragment);
            }
        });

        RxTaskHelper.getInstance().addTask(this,sub);

        mAdapter = new ChannelPagerAdapter(getChildFragmentManager(), this.container, datas);

        viewPager.setAdapter(mAdapter);
        viewPager.getParent().requestDisallowInterceptTouchEvent(true);
        viewPager.setOffscreenPageLimit(1);
        pssTab.setViewPager(viewPager);
        pssTab.invalidate();
    }


    /**
     * 获取默认用户数据
     */
    private void geDefauleChannels() {
        Subscription sub = AllApi.getInstance().getNewsChannels("").subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<ChannelBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ChannelBean bean = (ChannelBean) data;
                        localChannelList.clear();
                        //默认用户频道
                        List<ChannelBean.DefaultBean> defaultX = bean.getDefaultX();
                        if (defaultX.size() != 0 && defaultX != null) {
                            for (int i = 0; i < defaultX.size(); i++) {
                                localChannelList.add(new NewChannelItem(defaultX.get(i).getNewstypeId(), defaultX.get(i).getNewstypeName(), i + 1, i + 1));
                            }
                            userChannelList = localChannelList;
                            ChannelManager.getInstance().deleteAllChannel();
                            ChannelManager.getInstance().saveUserChannel(userChannelList);
                          /*  initVp();
                            showGuide();*/

                        }
                        //默认的其他频道
                        List<ChannelBean.NormalBean> normal = bean.getNormal();
                        if (normal.size() != 0 && normal != null) {
                            for (int i = 0; i < normal.size(); i++) {
                                defaultOtherChannels.add(new NewChannelItem(normal.get(i).getNewstypeId(), normal.get(i).getNewstypeName(), i + 1, i + 1));
                            }
                            ChannelManager.getInstance().deleteAllChannel();
                            ChannelManager.getInstance().saveOtherChannel(defaultOtherChannels);
                        }

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(TOKEN_OVERDUE_10001 == Integer.parseInt(errorCode)){
                            mAlertDialog =  DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                        ReLoginUtil.ReloginTodo(getActivity());
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                        }else {
                            if(userChannelList == null || userChannelList.size() == 0){
                                getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getBaseEmptyView().hideEmptyView();
                                        geDefauleChannels();
                                    }
                                });
                            }
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case CHANNELREQUEST:
//                if (resultCode == CHANNELRESULT) {
//                    initColumnData();
//                }
//                break;
//
//            default:
//                break;
//        }
//    }


    //显示新手指引页
    private void showGuide(){
        boolean firstLaunch = SpUtil.getBoolean(getActivity(), FIRST_GUIDE, true);
        if(firstLaunch){
            SpUtil.putBoolean(getActivity(),FIRST_GUIDE,false);
            new GuideHelper(getActivity()).show(GuideHelper.TYPE_ONE,pssTab);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxTaskHelper.getInstance().cancelTask(this);
    }


}
