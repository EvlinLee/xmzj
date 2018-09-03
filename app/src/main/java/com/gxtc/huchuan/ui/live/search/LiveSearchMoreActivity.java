package com.gxtc.huchuan.ui.live.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleListAdapter;
import com.gxtc.huchuan.adapter.Deal1LevelAdapter;
import com.gxtc.huchuan.adapter.LiveRoomNewAdapter;
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
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.home.CircleWebActivityv2;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DateUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 搜索  展示 更多列表
 * Created by Gubr on 2017/4/1.
 */


public class LiveSearchMoreActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String TAG                = "LiveSearchMoreActivity";
    private static final int    CIRCLE_WEB_REQUEST = 1 << 3;

    @BindView(R.id.recyclerview) RecyclerView mRecyclerview;

    private String              mType;
    private String              mSearchKey;
    private View                mHeadView;
    private BaseRecyclerAdapter mBaseRecyclerAdapter;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private boolean flag;

    private CircleShareHandler mshareHandler;

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
        mSearchKey = intent.getStringExtra("searchKey");
        mshareHandler = new CircleShareHandler(this);

        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle("搜索");

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mHeadView = mRecyclerview.addHeadView(R.layout.item_livesearch_head);
        ImageView typeicon = (ImageView) mHeadView.findViewById(R.id.iv_search_type_icon);
        TextView  typename = (TextView) mHeadView.findViewById(R.id.tv_search_type_name);
        if (mType != null) {
            String name    = null;
            int    imageid = -1;
            switch (mType) {
                case "1":
                    name = "文章";
                    imageid = R.drawable.live_list_voice;
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<NewsBean>(this,
                            new ArrayList<NewsBean>(), R.layout.item_news_fragment) {
                        @Override
                        public void bindData(ViewHolder holder, int position, NewsBean newsBean) {
                            //作者
                            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
                            tvAuthor.setText(newsBean.getSource());

                            //阅读
                            TextView tvCount = (TextView) holder.getView(R.id.tv_read_count);
                            tvCount.setText("阅读："+newsBean.getReadCount());
                            ImageHelper.loadImage(holder.getItemView().getContext(),
                                    holder.getImageView(R.id.iv_news_cover), newsBean.getCover());
                            holder.setText(R.id.tv_news_title, newsBean.getTitle()).setText(R.id.tv_news_time,
                                    DateUtil.showTimeAgo(newsBean.getDate()));
                        }
                    };
                    break;
                case "2":
                    name = "课程";
                    imageid = R.drawable.live_list_voice;
                    mBaseRecyclerAdapter = new LiveRoomNewAdapter(this,  new ArrayList<ChatInfosBean>(), R.layout.item_search_topic);
                    break;
                case "3":
                    name = "课堂";
                    imageid = R.drawable.live_list_voice;
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
                    name = "交易";
                    imageid = R.drawable.live_list_voice;
                    mBaseRecyclerAdapter = new Deal1LevelAdapter(this,  new ArrayList<DealListBean>(), R.layout.deal_list_home_page);
                    break;
                case "5":
                    name = "会员";
                    imageid = R.drawable.live_list_voice;
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<MenberSearchBean>(this,
                            new ArrayList<MenberSearchBean>(), R.layout.item_search_menber) {

                        @Override
                        public void bindData(ViewHolder holder, int position,
                                MenberSearchBean menberSearchBean) {
                            ImageHelper.loadImage(holder.getItemView().getContext(),
                                    holder.getImageView(R.id.iv_menber_head),
                                    menberSearchBean.getUserHeadPic());
                            holder.setText(R.id.tv_search_userName, menberSearchBean.getUserName()).setText(R.id.tv_introduction,
                                    TextUtils.isEmpty(menberSearchBean.getNewsTitle()) ? "最近没有发布内容":menberSearchBean.getNewsTitle());
                        }
                    };
                    break;
                case "6":
                    name = "圈子";
                    imageid = R.drawable.live_list_voice;
                    mBaseRecyclerAdapter = new CircleListAdapter(this, new ArrayList<CircleBean>(), R.layout.item_circle_find_list);
                    break;
                case "7":
                    name = "朋友圈动态";
                    imageid = R.drawable.live_list_voice;
                    mBaseRecyclerAdapter = new BaseRecyclerAdapter<CircleHomeBean>(this,
                            new ArrayList<CircleHomeBean>(), R.layout.item_search_friend) {

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
                                                    LiveSearchMoreActivity.this,
                                                    bean.getUserCode());
                                        }
                                    });

                            final TextView content = holder.getViewV2(
                                    R.id.tv_circle_home_three_content);
                            content.setText(bean.getContent());
                        }
                    };
                    break;
            }
            typename.setText(name);

        }
        mRecyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerview.setAdapter(mBaseRecyclerAdapter);
    }

    @Override
    public void initData() {
        search(mSearchKey);

    }

    @Override
    public void initListener() {
        mRecyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                Log.d(TAG, "onLoadMoreRequested: ");
                search(mSearchKey);
            }
        });
        mBaseRecyclerAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {


                    @Override
                    public void onItemClick(View v, int position) {
                        Object o = mBaseRecyclerAdapter.getList().get(position);
                        switch (mType) {
                            case "1":
                                NewsBean newsBean = o instanceof NewsBean ? ((NewsBean) o) : null;
                                if (newsBean != null) {
                                    Intent intent = new Intent(LiveSearchMoreActivity.this, NewsWebActivity.class);
                                    intent.putExtra("data", newsBean);
                                    LiveSearchMoreActivity.this.startActivity(intent);

                                }
                                break;
                            case "2":
                                ChatInfosBean chatBean = o instanceof ChatInfosBean ? ((ChatInfosBean) o) : null;

                                if (chatBean != null) {
                                    mshareHandler.getLiveInfo(chatBean.getId(),null);
                                }
                                break;
                            case "3":
                                LiveRoomBean liveRoomBean = o instanceof LiveRoomBean ? ((LiveRoomBean) o) : null;
                                if (liveRoomBean != null) {
                                    LiveHostPageActivity.startActivity(LiveSearchMoreActivity.this,"1",
                                            liveRoomBean.getId());
                                }
                                break;
                            case "4":
                                DealListBean dealListBean = o instanceof DealListBean ? ((DealListBean) o) : null;
                                if (dealListBean != null) {
                                    Intent intent = new Intent(LiveSearchMoreActivity.this,
                                            GoodsDetailedActivity.class);
                                    intent.putExtra(Constant.INTENT_DATA, dealListBean);
                                    LiveSearchMoreActivity.this.startActivity(intent);
                                }
                                break;
                            case "5":
                                MenberSearchBean menberSearchBean = o instanceof MenberSearchBean ? ((MenberSearchBean) o) : null;
                                if (menberSearchBean != null) {
                                    PersonalInfoActivity.startActivity(LiveSearchMoreActivity.this,
                                            menberSearchBean.getUserCode());
                                }
                                break;
                            case "6":
                                CircleBean circleBean = o instanceof CircleBean ? ((CircleBean) o) : null;
                                if (circleBean != null) {

                                    String url   = circleBean.getJoinUrl();
                                    String name  = circleBean.getGroupName();
                                    double money = circleBean.getFee();
                                    int    id    = circleBean.getId();

                                    Intent intent = new Intent(LiveSearchMoreActivity.this,
                                            CircleJoinActivity.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("id", id);
                                    intent.putExtra("name", name);
                                    intent.putExtra("isAudit", circleBean.getIsAudit());
                                    intent.putExtra(Constant.INTENT_DATA, money);
                                    startActivityForResult(intent, 0);
                                }
                                break;
                            case "7":
                                CircleHomeBean circleHomeBean = o instanceof CircleHomeBean ? ((CircleHomeBean) o) : null;
                                if (circleHomeBean != null) {
                                    Intent intent = new Intent(LiveSearchMoreActivity.this,
                                            CircleWebActivityv2.class);
                                    intent.putExtra("data", circleHomeBean);

                                    startActivityForResult(intent, CIRCLE_WEB_REQUEST);
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
        HashMap<String, String> map = new HashMap<>();

        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
        }
        map.put("type", mType);
        map.put("start",
                mBaseRecyclerAdapter != null ? "" + mBaseRecyclerAdapter.getItemCount() : "0");
        map.put("searchKey", searchKey);
        map.put("pageSize", 15+"");
        search(map);

    }

    private void search(HashMap<String, String> map) {
        flag = true;
        mCompositeSubscription.add(
                AllApi.getInstance()
                      .searchList(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<List<SearchBean>>>(
                                new ApiCallBack<List<SearchBean>>() {
                                    @Override
                                    public void onSuccess(List<SearchBean> data) {
                                        if (mBaseRecyclerAdapter != null) {
                                            if (data != null && data.size() >= 1) {
                                                SearchBean searchBean = data.get(0);
                                                if (searchBean.getDatas().size() == 0) {
                                                    mRecyclerview.loadFinish();
                                                    flag = false;
                                                    return;
                                                }
                                                switch (searchBean.getType()) {
                                                    case "1":
                                                        List<NewsBean> newslist = searchBean.getDatas();
                                                        mRecyclerview.changeData(newslist,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                    case "2":
                                                        List<ChatInfosBean> chatInfolist = searchBean.getDatas();
                                                        mRecyclerview.changeData(chatInfolist,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                    case "3":
                                                        List<LiveRoomBean> liveroomlist = searchBean.getDatas();
                                                        mRecyclerview.changeData(liveroomlist,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                    case "4":
                                                        List<DealListBean> dealListBeanList = searchBean.getDatas();
                                                        mRecyclerview.changeData(dealListBeanList,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                    case "5":
                                                        List<MenberSearchBean> menberSearchBeanList = searchBean.getDatas();
                                                        mRecyclerview.changeData(
                                                                menberSearchBeanList,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                    case "6":
                                                        List<CircleBean> circleBeanList = searchBean.getDatas();
                                                        mRecyclerview.changeData(circleBeanList,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                    case "7":
                                                        List<CircleHomeBean> circleHomeBeanList = searchBean.getDatas();
                                                        mRecyclerview.changeData(circleHomeBeanList,
                                                                mBaseRecyclerAdapter);
                                                        break;
                                                }
                                            }
                                        }
                                        flag = false;
                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {
                                        flag = false;
                                    }
                                })));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    @Subscribe
    public void onEvent(EventLoadBean bean){
        getBaseLoadingView().show(bean.isLoading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        EventBusUtil.unregister(this);
        mshareHandler.destroy();
    }
}
