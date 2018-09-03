package com.gxtc.huchuan.ui.mall;

import android.app.ActionBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.BaseMallAdapter;
import com.gxtc.huchuan.adapter.MallBannerAdapter;
import com.gxtc.huchuan.adapter.MallGridAdapter;
import com.gxtc.huchuan.adapter.MallLineImageAdapter;
import com.gxtc.huchuan.bean.CoustomMerBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.MallDetailBean;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.MallParamDialog;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.StatisticsHandler;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.widget.MyGridView;
import com.gxtc.huchuan.widget.RecyclerSpace;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2018/4/28. 改版后的商城首页
 * 之前的代码太过冗余，无关的代码太多，布局里嵌套的太多，加载更多卡顿，影响性能
 */

public class NewMallFragment extends BaseTitleFragment implements MallContract.View {
    @BindView(R.id.refreshlayout)      SwipeRefreshLayout mRefreshlayout;
    @BindView(R.id.live_room)           com.gxtc.commlibrary.recyclerview.RecyclerView mLiveRoom;
    @BindView(R.id.bottom_layout)      View                                          bottomlayout ;
    @BindView(R.id.mall_shouyel)       TextView                                       mallShouyelText;
    @BindView(R.id.mall_fenlei)        TextView                                       mallfenleilText;
    @BindView(R.id.mall_huodong)       TextView                                       mMallHuodongText;
    @BindView(R.id.mall_wode)          TextView                                       mMallWodeText;
    @BindView(R.id.fab_issue)          View                                           mFloatingActionButton;

    private MyGridView                                     myGridView;
    private RecyclerView                                   mLiveForeshow;
    private ConvenientBanner                               mConvenientBanner;

    private boolean isLoadMore = false;
    private MallParamDialog        mallDialog;
    private MallContract.Presenter mPresenter;
    private MallLineImageAdapter   mLiveForeshowAdapter;
    private BaseMallAdapter        mLiveRoomAdapter;
    private List<MallBean>         mAdvertise;
    private GridLayoutManager      gridLayoutManager;
    private MallGridAdapter adapter;
    private AlertDialog     mAlertDialog;

    private View header;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.mall_fragment_new_layout, container, false);
        getBaseHeadView().showTitle("营销工具");
        getBaseHeadView().showHeadRightButton("分类", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter != null && adapter.getDatas() != null)
                    MallHandleUtil.showPop(getActivity(),v,adapter.getDatas(),R.drawable.pop_top_right_icon_bg,Gravity.BOTTOM);
            }
        });
        header = getLayoutInflater().inflate(R.layout.search_head_view,getBaseHeadView().getParentView(),false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF,R.id.headBackButton);
        params.addRule(RelativeLayout.LEFT_OF,R.id.headRightLinearLayout);
        getBaseHeadView().getParentView().addView(header);
        header.findViewById(R.id.search_layout).setBackground(getResources().getDrawable(R.drawable.shape_search_btn));
        header.findViewById(R.id.et_input_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MallSearchActivity.Companion.jumpToMallSearchActivity(getActivity(), "");
            }
        });
        return view;
    }

    @Override
    public void initListener() {
        mLiveRoom.setFocusableInTouchMode(false);//让RecyclerView失去焦点，否则当添加的头部的高度大于手机屏幕时，刚进入界面，RecyclerView会自动往上移动一段距离的
        mLiveRoom.requestFocus();
        getBaseHeadView().showBackButton( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadMore = false;
                mRefreshlayout.setRefreshing(false);
                mLiveRoom.reLoadFinish();
                mPresenter.getGridData(false);
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MallCustomersActivity.Companion.goToCustomerServicesActivity(getActivity(),
                        MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_MALL(),
                        MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_LIST());

            }
        });

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
        getBaseLoadingView().showLoading();
        EventBusUtil.register(this);
        mRefreshlayout.setColorSchemeResources(Constant.REFRESH_COLOR);
        View hearderView = getLayoutInflater().inflate(R.layout.mall_header_layout,null);
        mConvenientBanner = hearderView.findViewById(R.id.cb_live_banner);
        myGridView = hearderView.findViewById(R.id.logo_list);
        mLiveForeshow = hearderView.findViewById(R.id.live_foreshow);
        myGridView.setSmoothScrollbarEnabled(false);
        mLiveForeshow.setNestedScrollingEnabled(false);
        mLiveRoom.addHeadView(hearderView);
        initLiveRoomAdapter();
        int  bannerHeight = (int) WindowUtil.getScaleHeight(16, 8, getContext());
        LinearLayout.LayoutParams params       = (LinearLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        params.height = bannerHeight;
        new MallPresenter(this);
        readAdCache();
        readTagCache();
        readLinesCache();
        readGridCache();
    }

    private void readGridCache() {
        Observable.create(new Observable.OnSubscribe<ArrayList<MallBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MallBean>> subscriber) {
                ArrayList<MallBean> temp = (ArrayList<MallBean>) ACache.get(MyApplication.getInstance()).getAsObject(NewMallFragment.class.getSimpleName() + "goods");
                subscriber.onNext(temp);
            }
        })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<ArrayList<MallBean>>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          ACache.get(MyApplication.getInstance()).remove(NewMallFragment.class.getSimpleName() + "goods");
                          mPresenter.getGridData(false);
                      }

                      @Override
                      public void onNext(ArrayList<MallBean> mallBeen) {
                          if(mallBeen != null && mallBeen.size() > 0){
                              showGridData(mallBeen);
                          }
                          mPresenter.getGridData(false);
                      }
                  });
    }

    private void readLinesCache() {
        Observable.create(new Observable.OnSubscribe<ArrayList<MallBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MallBean>> subscriber) {
                ArrayList<MallBean> temp = (ArrayList<MallBean>) ACache.get(MyApplication.getInstance()).getAsObject(NewMallFragment.class.getSimpleName() + "lines");
                subscriber.onNext(temp);
            }
        })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<ArrayList<MallBean>>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          ACache.get(MyApplication.getInstance()).remove(NewMallFragment.class.getSimpleName() + "lines");
                          mPresenter.getLinesData(UserManager.getInstance().getToken(), 0);
                      }

                      @Override
                      public void onNext(ArrayList<MallBean> mallBeen) {
                          if(mallBeen != null && mallBeen.size() > 0){
                              showLinesData(mallBeen);
                          }
                          mPresenter.getLinesData(UserManager.getInstance().getToken(), 0);
                      }
                  });
    }

    private void readTagCache() {
        Observable.create(new Observable.OnSubscribe<ArrayList<MallBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MallBean>> subscriber) {
                ArrayList<MallBean> temp = (ArrayList<MallBean>) ACache.get(MyApplication.getInstance()).getAsObject(NewMallFragment.class.getSimpleName() + "tag");
                subscriber.onNext(temp);
            }
        })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<ArrayList<MallBean>>() {
                      @Override
                      public void onCompleted() {}

                      @Override
                      public void onError(Throwable e) {
                          ACache.get(MyApplication.getInstance()).remove(NewMallFragment.class.getSimpleName() + "tag");
                          mPresenter.getTags(UserManager.getInstance().getToken());
                      }

                      @Override
                      public void onNext(ArrayList<MallBean> mallBeen) {
                          if(mallBeen != null && mallBeen.size() > 0){
                              showHeadIcon(mallBeen);
                          }
                          mPresenter.getTags(UserManager.getInstance().getToken());
                      }
                  });
    }

    private void readAdCache() {
        Observable.create(new Observable.OnSubscribe<ArrayList<MallBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MallBean>> subscriber) {
                ArrayList<MallBean> temp = (ArrayList<MallBean>) ACache.get(MyApplication.getInstance()).getAsObject(NewMallFragment.class.getSimpleName() + "ad");
                subscriber.onNext(temp);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MallBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove(NewMallFragment.class.getSimpleName() + "ad");
                        mPresenter.getAdvertise();
                    }

                    @Override
                    public void onNext(ArrayList<MallBean> mallBeen) {
                        if(mallBeen != null && mallBeen.size() > 0){
                           showCbBanner(mallBeen);
                        }
                        mPresenter.getAdvertise();
                    }
                });

    }

    @Override
    public void showCbBanner(final List<MallBean> advertise) {
        getBaseLoadingView().hideLoading();
        mAdvertise = advertise;
        mConvenientBanner.startTurning(4000);
        mConvenientBanner.setPages(new CBViewHolderCreator<MallBannerAdapter>() {
            @Override
            public MallBannerAdapter createHolder() {
                return new MallBannerAdapter();
            }
        }, mAdvertise);
        mConvenientBanner.setPageIndicator(
                new int[]{R.drawable.news_icon_dot_small, R.drawable.news_icon_dot_big});
        mConvenientBanner.setPageIndicatorAlign(
                ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        mConvenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MallHandleUtil.handlerByType(getContext(), advertise,position);
            }
        });

    }

    @Subscribe
    public void onEvent(EventScorllTopBean bean) {
        if (bean.position == 1 && mLiveForeshowAdapter != null) {
            mLiveRoom.scrollTo(0, 0);
        }
    }

    @Override
    public void showHeadIcon(final List<MallBean> datas) {
        adapter = new MallGridAdapter(getContext(), datas,
                R.layout.mall_line_image_item);
        myGridView.setAdapter(adapter);
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MallHandleUtil.handlerByType(getContext(),datas,position);
            }
        });
    }


    @Override
    public void showLinesData(final List<MallBean> datas) {
        if (getBaseLoadingView() == null) return;
        getBaseLoadingView().hideLoading();
        if (mLiveForeshowAdapter == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false);
            mLiveForeshowAdapter = new MallLineImageAdapter(getContext(), datas,
                    R.layout.mall_line_image_item);
            mLiveForeshow.setLayoutManager(linearLayoutManager);
            mLiveForeshow.setHasFixedSize(true);
            mLiveForeshow.setNestedScrollingEnabled(false);
            mLiveForeshow.setAdapter(mLiveForeshowAdapter);
            mLiveForeshowAdapter.setOnReItemOnClickListener(
                    new BaseRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            MallHandleUtil.handlerByType(getContext(),mLiveForeshowAdapter.getList(),position);
                        }
                    });
        } else {
            mLiveForeshowAdapter.changeDatas(datas);
        }

    }


    private void initLiveRoomAdapter() {
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mLiveRoom.setLayoutManager(gridLayoutManager);
        mLiveRoomAdapter = new BaseMallAdapter(getContext(), new ArrayList<MallBean>(),
                R.layout.activity_my_mall_center);
        mLiveRoom.setLoadMoreView(R.layout.model_footview_loadmore);
        mLiveRoom.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                mPresenter.getGridData(true);
            }
        });
        mLiveRoom.setAdapter(mLiveRoomAdapter);
        mLiveRoomAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        MallHandleUtil.handlerByType(getContext(),mLiveRoomAdapter.getList(),position);
                    }
                });
        //加入购物车
        mLiveRoomAdapter.setAddshopCartListenner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserManager.getInstance().isLogin()){
                    if (!ClickUtil.isFastClick()) {
                        int position = StringUtil.toInt(v.getTag());
                        showParamDialog(mLiveRoomAdapter.getList().get(position));
                    }
                }else {
                    GotoUtil.goToActivity(getActivity(),LoginAndRegisteActivity.class);
                }
            }
        });
    }

    private void showParamDialog(final MallBean mallBean) {
        if (mallDialog == null) {
            mallDialog = new MallParamDialog();
            mallDialog.setDialogListener(new MallParamDialog.MallParamListener() {
                @Override
                public void onPayClick(MallParamDialog dialog, MallDetailBean bean, View view) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("token", UserManager.getInstance().getToken());
                    map.put("storePriceId", dialog.getParam().getId() + "");
                    map.put("amount", mallDialog.getSelectCount() + "");
                    mPresenter.addShopCar(map);
                }
            });
        }
        MallParamDialog.Builder builder = new MallParamDialog.Builder();
        builder.setStoreId(StringUtil.toInt(mallBean.getData()));
        builder.setCover(mallBean.getPicUrl());
        builder.setGoodsName(mallBean.getName());
        builder.setPrice(mallBean.getPrice());
        builder.setPriceList(mallBean.getPriceList());
        mallDialog.show(getChildFragmentManager(), MallParamDialog.class.getSimpleName(),
                builder.builde(), true);
    }

    @Override
    public void showGridData(final List<MallBean> datas) {
        getBaseLoadingView().hideLoading();
        if (datas != null && datas.size() > 0) {
            if (!isLoadMore) {
                mLiveRoom.notifyChangeData(datas, mLiveRoomAdapter);
            } else {
                mLiveRoom.changeData(datas, mLiveRoomAdapter);
            }
        } else {
            mLiveRoom.loadFinishNotView();
        }
        //合并单元格
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int _position) {
                    if(_position == 0) return 2; //因为RecycleView 添加了头部，所以这里头部是位置0，必须返回2，合并两个单元格才能正常显示
                    int position = _position - 1;//这里拿数据要从索引0开始拿，否则数据会错乱的，所以必须减一
                    if ( position  < mLiveRoomAdapter.getList().size()) {
                        if ("1".equals(mLiveRoomAdapter.getList().get(position).getRatio())) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                return 2;//这里一定要返回2，不然加载更多会不居中！！！
            }
        });
    }

    @Override
    public void showAddShopCartResult(Object datas) {
        ToastUtil.showShort(getContext(), "加入购物车成功");
        if (mallDialog != null) {
            mallDialog.dismiss();
        }
    }

    @Override
    public void showCustermersList(ArrayList<CoustomMerBean> datas) {
        if (datas != null && datas.size() > 0)
        RongImHelper.startCustomerServices(getActivity(), datas.get(0).getName(), datas.get(0).getUserCode());
    }

    @Override
    public void showActivitysData(List<MallBean> datas) {
        if(datas == null || datas.size() == 0 ) return;
        MallHandleUtil.handlerByType(getContext(),datas,0);
    }


    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(getActivity());
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void showNetError() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getContext(), info);
    }

    @Override
    public void showEmpty() {}

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mRefreshlayout.setRefreshing(false);
        getBaseEmptyView().hideEmptyView();
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showReLoad() {}

    @Override
    public void setPresenter(MallContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @OnClick({R.id.mall_shouyel,R.id.mall_fenlei,R.id.mall_huodong,R.id.mall_wode})
    public void onClick(View view) {
        MallHandleUtil.setNolmal(getContext(),mallShouyelText,mallfenleilText,mMallHuodongText,mMallWodeText);
        switch (view.getId()) {
            case R.id.mall_shouyel:
                MallHandleUtil.setSelected(getContext(),view,R.drawable.icon_shouye_selected);
                break;
            case R.id.mall_fenlei:
                if(adapter != null && adapter.getDatas() != null) {
                    MallHandleUtil.setSelected(getContext(), view, R.drawable.icon_fenlei_selected);
                    MallHandleUtil.showPop(getActivity(), view, adapter.getDatas(), R.drawable.shop_pop_bg, Gravity.TOP);
                }
                break;
            case R.id.mall_huodong:
                MallHandleUtil.setSelected(getContext(),view,R.drawable.icon_huodong_selected);
                mPresenter.getActivitysData("", 0);
                break;
            case R.id.mall_wode:
                MallHandleUtil.setSelected(getContext(),view,R.drawable.icon_wode_selected);
                goToActivity(MyMallCenterActivity.class);
                break;
        }
    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(getActivity(), toClass);
        } else {
            GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
        }
    }

    @Override
    public void onDestroy() {
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        StatisticsHandler.Companion.getInstant().destroy();
        mPresenter.destroy();
        super.onDestroy();
    }

}
