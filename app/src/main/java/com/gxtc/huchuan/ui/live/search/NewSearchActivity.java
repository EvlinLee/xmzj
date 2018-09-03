package com.gxtc.huchuan.ui.live.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ClickUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.Deal1LevelAdapter;
import com.gxtc.huchuan.adapter.LiveRoomNewAdapter;
import com.gxtc.huchuan.adapter.SearchCircleAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.MenberSearchBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DateUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NewSearchActivity extends BaseTitleActivity implements View.OnClickListener, TextWatcher,
        TextView.OnEditorActionListener {

    public static final String TYPE_NEWS = "1";  //文章
    public static final String TYPE_LIVE = "2";  //课堂
    public static final String TYPE_DEAL = "4";  //交易
    public static final String TYPE_USER = "5";  //用户
    public static final String TYPE_CIRCLE = "6";  //圈子
    public static final String TYPE_DYNAMIC = "7";  //动态
    @BindView(R.id.ll_flow)      LinearLayout mLLFlow;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerview;

    private EditText            mEtInputSearch;
    private String              mType;
    private String              mSearchKey;
    private View                mHeadView;
    private BaseRecyclerAdapter mBaseRecyclerAdapter;

    private int searchStart = 0;
    private boolean flag;
    private CircleShareHandler mshareHandler;
    private TextView           typename;

    public static void jumpToSearch(Context mContext, String type) {
        Intent intent = new Intent(mContext, NewSearchActivity.class);
        intent.putExtra("type", type);//1 :文章
        mContext.startActivity(intent);
    }

    public static void startActivity(Context context, String keyWord) {
        Intent intent = new Intent(context, NewSearchActivity.class);
        intent.putExtra("keyWord", keyWord);
        intent.putExtra("type", "1");//1 文章 默认
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livesearch_mroe);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        mSearchKey = intent.getStringExtra("keyWord");
        mshareHandler = new CircleShareHandler(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.news_icon_search, this);
        View headView = LayoutInflater.from(this).inflate(R.layout.model_search_view,
                getBaseHeadView().getParentView(), false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) headView.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, R.id.headBackButton);
        params.addRule(RelativeLayout.LEFT_OF, R.id.headRightLinearLayout);
        getBaseHeadView().getParentView().addView(headView);
        getBaseHeadView().showBackButton(this);
        mEtInputSearch = (EditText) headView.findViewById(R.id.et_input_search);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
        mHeadView = mRecyclerview.addHeadView(R.layout.item_livesearch_head);
        typename = (TextView) mHeadView.findViewById(R.id.tv_search_type_name);
        mHeadView.setVisibility(View.GONE);
    }


    private void setAdaterByType() {
        if (mType != null) {
            switch (mType) {
                case "1":
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<NewsBean>(this,
                            new ArrayList<NewsBean>(), R.layout.item_news_fragment) {
                        @Override
                        public void bindData(ViewHolder holder, int position, NewsBean newsBean) {
                            //作者
                            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
                            tvAuthor.setText(newsBean.getSource());

                            //阅读
                            TextView tvCount = (TextView) holder.getView(R.id.tv_read_count);
                            tvCount.setText("阅读：" + newsBean.getReadCount());
                            ImageHelper.loadImage(holder.getItemView().getContext(),
                                    holder.getImageView(R.id.iv_news_cover), newsBean.getCover());
                            holder.setText(R.id.tv_news_title, newsBean.getTitle()).setText(
                                    R.id.tv_news_time, DateUtil.showTimeAgo(newsBean.getDate()));
                        }
                    };
                    break;

                case "2":
                    mBaseRecyclerAdapter = new LiveRoomNewAdapter(this, new ArrayList<ChatInfosBean>(), R.layout.item_live_new_room);
                    break;

                case "3":
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<LiveRoomBean>(this,
                            new ArrayList<LiveRoomBean>(), R.layout.item_search_liveroom) {
                        @Override
                        public void bindData(ViewHolder holder, int position, LiveRoomBean bean) {
                            ImageHelper.loadImage(holder.getItemView().getContext(),
                                    holder.getImageView(R.id.iv_search_icon), bean.getHeadpic(),
                                    R.drawable.person_icon_head_120);
                            holder.setText(R.id.tv_search_title, bean.getRoomname()).setText(
                                    R.id.tv_search_join_count, bean.getFs() + "人次");
                        }
                    };
                    break;

                case "4":
                    mBaseRecyclerAdapter = new Deal1LevelAdapter(this, new ArrayList<DealListBean>(), R.layout.deal_list_home_page);
                    break;

                case "5":
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<MenberSearchBean>(this, new ArrayList<MenberSearchBean>(), R.layout.item_search_menber) {

                        @Override
                        public void bindData(ViewHolder holder, int position, MenberSearchBean menberSearchBean) {
                            ImageHelper.loadImage(holder.getItemView().getContext(),
                                    holder.getImageView(R.id.iv_menber_head),
                                    menberSearchBean.getUserHeadPic());
                            holder.setText(R.id.tv_search_userName,
                                    menberSearchBean.getUserName()).setText(R.id.tv_introduction,
                                    TextUtils.isEmpty(menberSearchBean.getNewsTitle()) ? "最近没有发布内容" : menberSearchBean.getNewsTitle());
                        }
                    };
                    break;

                case "6":
                    mBaseRecyclerAdapter = new SearchCircleAdapter(this, new ArrayList<CircleBean>(), R.layout.item_search_circle);
                    break;

                case "7":
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<CircleHomeBean>(this, new ArrayList<CircleHomeBean>(), R.layout.item_search_friend) {

                        @Override
                        public void bindData(ViewHolder holder, int position,
                                             final CircleHomeBean bean) {
                            holder.setText(R.id.tv_circle_home_name, bean.getUserName());
                            holder.getView(R.id.tv_circle_home_three_content).setVisibility(
                                    TextUtils.isEmpty(
                                            bean.getContent()) ? View.GONE : View.VISIBLE);
                            ImageHelper.loadImage(holder.getItemView().getContext(),
                                    holder.getImageView(R.id.iv_circle_home_img),
                                    bean.getUserPic());
                            holder.getView(R.id.iv_circle_home_img).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            PersonalInfoActivity.startActivity(
                                                    NewSearchActivity.this, bean.getUserCode());
                                        }
                                    });

                            final TextView content = holder.getViewV2(
                                    R.id.tv_circle_home_three_content);
                            content.setText(bean.getContent());
                        }
                    };
                    break;
            }
        }
        mRecyclerview.setAdapter(mBaseRecyclerAdapter);
    }

    @Override
    public void initData() {
        autoSerchByType(mType);
    }

    @Override
    public void initListener() {
        mEtInputSearch.addTextChangedListener(this);
        mEtInputSearch.setOnEditorActionListener(this);
        mEtInputSearch.setOnClickListener(this);
        setAdaterByType();
        mRecyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                searchStart = mBaseRecyclerAdapter.getItemCount();
                search(mSearchKey);
            }
        });

        mBaseRecyclerAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if(ClickUtil.isFastClick()) return;
                        Object o = mBaseRecyclerAdapter.getList().get(position);
                        switch (mType) {
                            case "1":
                                NewsBean newsBean = o instanceof NewsBean ? ((NewsBean) o) : null;
                                if (newsBean != null) {
                                    Intent intent = new Intent(NewSearchActivity.this, NewsWebActivity.class);
                                    intent.putExtra("data", newsBean);
                                    NewSearchActivity.this.startActivity(intent);

                                }
                                break;

                            case "2":
                                ChatInfosBean chatBean = o instanceof ChatInfosBean ? ((ChatInfosBean) o) : null;

                                if (chatBean != null) {
                                    mshareHandler.getLiveInfo(chatBean.getId(), null);
                                }
                                break;

                            case "3":
                                LiveRoomBean liveRoomBean = o instanceof LiveRoomBean ? ((LiveRoomBean) o) : null;
                                if (liveRoomBean != null) {
                                    LiveHostPageActivity.startActivity(NewSearchActivity.this,"1",
                                            liveRoomBean.getId());
                                }
                                break;

                            case "4":
                                DealListBean dealListBean = o instanceof DealListBean ? ((DealListBean) o) : null;
                                if (dealListBean != null) {
                                    Intent intent = new Intent(NewSearchActivity.this,
                                            GoodsDetailedActivity.class);
                                    intent.putExtra(Constant.INTENT_DATA, dealListBean);
                                    NewSearchActivity.this.startActivity(intent);
                                }
                                break;

                            case "5":
                                MenberSearchBean menberSearchBean = o instanceof MenberSearchBean ? ((MenberSearchBean) o) : null;
                                if (menberSearchBean != null) {
                                    PersonalInfoActivity.startActivity(NewSearchActivity.this,
                                            menberSearchBean.getUserCode());
                                }
                                break;

                            case "6":
                                CircleBean circleBean = o instanceof CircleBean ? ((CircleBean) o) : null;
                                if (circleBean != null) {

                                    if(circleBean.getIsJoin() == 1){
                                        Intent circleIntent = new Intent(NewSearchActivity.this, CircleMainActivity.class);
                                        circleIntent.putExtra("groupId", circleBean.getId());
                                        startActivity(circleIntent);
                                    }else {
                                        String url   = circleBean.getJoinUrl();
                                        String name  = circleBean.getGroupName();
                                        double money = circleBean.getFee();
                                        int    id    = circleBean.getId();

                                        Intent intent = new Intent(NewSearchActivity.this,
                                                CircleJoinActivity.class);
                                        intent.putExtra("url", url);
                                        intent.putExtra("id", id);
                                        intent.putExtra("name", name);
                                        intent.putExtra("isAudit", circleBean.getIsAudit());
                                        intent.putExtra(Constant.INTENT_DATA, money);
                                        startActivityForResult(intent, 0);
                                    }
                                }
                                break;

                            case "7":
                                if (o instanceof CircleHomeBean) {
                                    CircleHomeBean circleHomeBean = (CircleHomeBean) o;
                                    DynamicDetialActivity.startActivity(NewSearchActivity.this, circleHomeBean.getId() + "");
                                }
                                break;
                        }
                    }
                });
    }

    private void search(String searchKey) {
        if (flag) {
            return;
        }

        if(TextUtils.isEmpty(searchKey)){
            return;
        }

        getBaseLoadingView().showLoading();
        HashMap<String, String> map = new HashMap<>();

        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
        }

        map.put("type", mType);
        map.put("start", String.valueOf(searchStart));
        map.put("searchKey", searchKey);
        map.put("pageSize", 15 + "");
        search(map);
    }

    private void search(final HashMap<String, String> map) {
        flag = true;
        Subscription sub =
                AllApi.getInstance()
                      .searchList(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<SearchBean>>>(
                            new ApiCallBack<List<SearchBean>>() {
                                @Override
                                public void onSuccess(List<SearchBean> data) {
                                    flag = false;
                                    getBaseLoadingView().hideLoading();
                                    if(TextUtils.isEmpty(mEtInputSearch.getText())){
                                        isNoSeach();
                                        return;
                                    }
                                    mLLFlow.setVisibility(View.GONE);

                                    if (data != null && data.size() >= 1) {
                                        SearchBean searchBean = data.get(0);
                                        if (searchBean.getDatas().size() == 0) {
                                            mRecyclerview.loadFinish();
                                            if ("0".equals(map.get("start"))) {
                                                getBaseEmptyView().showEmptyContent();
                                            }
                                            return;
                                        }else{
                                            getBaseEmptyView().hideEmptyView();
                                        }

                                        switch (mType) {
                                            case "1":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("文章");
                                                List<NewsBean> newslist = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(newslist, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(newslist, mBaseRecyclerAdapter);
                                                }
                                                break;

                                            case "2":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("课程");
                                                List<ChatInfosBean> chatInfolist = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(chatInfolist, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(chatInfolist, mBaseRecyclerAdapter);
                                                }
                                                break;

                                            case "3":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("课堂");
                                                List<LiveRoomBean> liveroomlist = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(liveroomlist, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(liveroomlist, mBaseRecyclerAdapter);
                                                }
                                                break;

                                            case "4":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("交易");
                                                List<DealListBean> dealListBeanList = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(dealListBeanList, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(dealListBeanList, mBaseRecyclerAdapter);
                                                }
                                                break;

                                            case "5":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("会员");
                                                List<MenberSearchBean> menberSearchBeanList = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(menberSearchBeanList, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(menberSearchBeanList, mBaseRecyclerAdapter);
                                                }
                                                break;

                                            case "6":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("圈子");
                                                List<CircleBean> circleBeanList = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(circleBeanList, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(circleBeanList, mBaseRecyclerAdapter);
                                                }
                                                break;

                                            case "7":
                                                mHeadView.setVisibility(View.VISIBLE);
                                                typename.setText("朋友圈动态");
                                                List<CircleHomeBean> circleHomeBeanList = searchBean.getDatas();
                                                if (searchStart != 0) {
                                                    mRecyclerview.changeData(circleHomeBeanList, mBaseRecyclerAdapter);
                                                } else {
                                                    mRecyclerview.notifyChangeData(circleHomeBeanList, mBaseRecyclerAdapter);
                                                }
                                                break;
                                        }
                                    }
                                    flag = false;
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    flag = false;
                                }
                            }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.HeadRightImageButton:
                WindowUtil.closeInputMethod(this);
                search(mSearchKey);
                break;

        }
    }


    @Subscribe
    public void onEvent(EventLoadBean bean) {
        getBaseLoadingView().show(bean.isLoading);
    }


    //    1：文章，2：直播间课程，3：直播间，4：担保交易，5：用户，6：圈子，7：朋友圈动态
    @OnClick({R.id.search_key_1, R.id.search_key_2, R.id.search_key_3, R.id.search_key_4, R.id.search_key_5, R.id.search_key_6})
    public void onViewClicked(View view) {
        mType = "";
        switch (view.getId()) {
            //朋友圈
            case R.id.search_key_1:
                mType = TYPE_DYNAMIC;
                mEtInputSearch.setHint("搜索动态");
                break;

            //文章
            case R.id.search_key_2:
                mType = TYPE_NEWS;
                mEtInputSearch.setHint("搜索文章");
                break;

            //课堂
            case R.id.search_key_3:
                mType = TYPE_LIVE;
                mEtInputSearch.setHint("搜索课堂");
                break;

            //自媒体
            case R.id.search_key_4:
                mType = TYPE_USER;
                mEtInputSearch.setHint("搜索用户");
                break;

            //圈子
            case R.id.search_key_5:
                mType = TYPE_CIRCLE;
                mEtInputSearch.setHint("搜索圈子");
                break;

            //交易
            case R.id.search_key_6:
                mType = TYPE_DEAL;
                mEtInputSearch.setHint("搜索交易");
                break;
        }
        mRecyclerview.reLoadFinish();
        initListener();
        if (mBaseRecyclerAdapter != null) {
            mBaseRecyclerAdapter.getList().clear();
            mBaseRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void autoSerchByType(String mType) {
        switch (mType) {
            //朋友圈
            case TYPE_DYNAMIC:
                mEtInputSearch.setHint("搜索动态");
                break;

            //文章
            case TYPE_NEWS:
                mEtInputSearch.setHint("搜索文章");
                break;

            //课堂
            case TYPE_LIVE:
                mEtInputSearch.setHint("搜索课堂");
                break;

            //用户
            case TYPE_USER:
                mEtInputSearch.setHint("搜索用户");
                break;

            //圈子
            case TYPE_CIRCLE:
                mEtInputSearch.setHint("搜索圈子");
                break;

            //交易
            case TYPE_DEAL:
                mEtInputSearch.setHint("搜索交易");
                break;
        }
        if (mBaseRecyclerAdapter != null) {
            mBaseRecyclerAdapter.getList().clear();
            mBaseRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        RxTaskHelper.getInstance().cancelTask(this);
        mEtInputSearch.removeTextChangedListener(this);
        mEtInputSearch.setOnEditorActionListener(null);
        EventBusUtil.unregister(this);
        mshareHandler.destroy();
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        searchStart = 0;
        mRecyclerview.reLoadFinish();
        if (!TextUtils.isEmpty(s.toString())) {
            mSearchKey = s.toString();
            search(mSearchKey);
        } else {
            isNoSeach();
        }
    }

    public void isNoSeach(){
        mSearchKey = "";
        getBaseEmptyView().hideEmptyView();
        mLLFlow.setVisibility(View.VISIBLE);
        mHeadView.setVisibility(View.GONE);
        mBaseRecyclerAdapter.getList().clear();
        mBaseRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (mSearchKey != null && mSearchKey.length() > 0) {
                WindowUtil.closeInputMethod(this);
                getBaseLoadingView().showLoading();
                search(mSearchKey);
            }
        }
        return false;
    }
}
