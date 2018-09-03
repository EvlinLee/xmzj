package com.gxtc.huchuan.ui.mine.collect;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.widget.popup.BubblePopup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectMallDetailBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.ConversationTextBean;
import com.gxtc.huchuan.bean.CustomCollectBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.collectresolve.CollectResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.rong.imkit.model.UIMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ui.mine.collect.CircleShareCollectItemView;

/**
 */

public class CollectActivity extends BaseTitleActivity implements CollectContract.View,
        View.OnClickListener {

    private static final int EDITOR_REQUEST = 1 << 5;

    @BindView(R.id.rl_news_collect) RecyclerView       mRecyclerView;
    @BindView(R.id.sw_news_collect) SwipeRefreshLayout swNewsCollect;
    @BindView(R.id.tv_news_collect) TextView           tvNewsCollect;

    private CollectContract.Presenter            mPresenter;
    private MultiItemTypeAdapter<CollectionBean> mAdapter;
    private List<NewsBean>                       mDatas;
    private List<BaseRecyclerAdapter.ViewHolder> holders;
    private List<String>                         newsIds;
    private List<CollectionBean>                 list;

    private boolean isSelect;
    int count = 0;
    private BubblePopup    mBubblePopup;
    private ImageView       cbEditor;
    private ImageButton    headBackButton;
    private CollectionBean selectBean;

    private CircleShareHandler mShareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_collect);
        EventBusUtil.register(this);
    }

    @Override
    public void initData() {
        mDatas = new ArrayList<>();
        holders = new ArrayList<>();
        newsIds = new ArrayList<>();

        swNewsCollect.setColorSchemeResources(Constant.REFRESH_COLOR);

        initRecyCleView();
        list = getData(CollectActivity.class.getName()+"_colllectlist" );
        if(list == null){
            list = new ArrayList<>();
        }else{
            mRecyclerView.notifyChangeData(list,mAdapter);
            mRecyclerView.smoothScrollToPosition(0);
        }
        new CollectPresenter(this);
        mPresenter.getData(false);
        mShareHandler = new CircleShareHandler(this);

        isSelect = getIntent().getBooleanExtra(Constant.SELECT, false);
    }

    @Override
    public void initView() {
        View head = LayoutInflater.from(this).inflate(R.layout.layout_collection_title, getBaseHeadView().getParentView(), false);
        cbEditor = ((ImageView) head.findViewById(R.id.cb_editor));
        headBackButton = ((ImageButton) head.findViewById(R.id.headBackButton));
        getBaseHeadView().getParentView().addView(head);
        headBackButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                CollectActivity.this.finish();
                break;

            case R.id.tv_news_collect://删除操作

                break;

            case R.id.cb_editor:
                Intent intent = new Intent(CollectActivity.this, CollectResolveActivity.class);
                startActivityForResult(intent, EDITOR_REQUEST);
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

        cbEditor.setOnClickListener(this);

    }


    /**
     * 取消收藏
     * <p>
     * 2017/3/30   这个方法有个问题，就是本来我是应该本地刷新的，但是莫名其妙只能删除第一个，项目赶为了避免bug删除后重新刷新数据了，后续要优化
     * 2017/4/6 上述问题已解决 删除时list的数据变了
     */
    private void deleteCollect(String token, String id, final int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("delIds", id);
        Subscription sub = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mAdapter == null) return;
                        mRecyclerView.removeData(mAdapter, position);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CollectActivity.this, errorCode,
                                message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    /**
     * 新闻列表
     */
    private void initRecyCleView() {
        if (mAdapter != null) return;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(CollectActivity.this, LinearLayoutManager.HORIZONTAL,
                        getResources().getDimensionPixelOffset(R.dimen.px20dp),
                        getResources().getColor(R.color.grey_F4F4F4)));

        mAdapter = new MultiItemTypeAdapter<>(this, new ArrayList<CollectionBean>());
        mAdapter.addItemViewDelegate(new NewsCollectItemView(this));
        mAdapter.addItemViewDelegate(new TopicCollectItemView(this));
        mAdapter.addItemViewDelegate(new DealCollectItemView(this));
        mAdapter.addItemViewDelegate(new CircleImgCollectItemView(this, mRecyclerView));
        mAdapter.addItemViewDelegate(new CircleVideoCollectItemView(this, mRecyclerView));
        mAdapter.addItemViewDelegate(new CircleNoImgCollectItemView(this, mRecyclerView));
        mAdapter.addItemViewDelegate(new CircleShareCollectItemView(this, mRecyclerView));
        mAdapter.addItemViewDelegate(new CustomCollectItemView(this));
        mAdapter.addItemViewDelegate(new ConversationTextCollectItemView(this));
        mAdapter.addItemViewDelegate(new ConversationImageCollectItemView(this));
        mAdapter.addItemViewDelegate(new ConversationCircleCollectItemView(this));
        mAdapter.addItemViewDelegate(new SeriesItemView());
        mAdapter.addItemViewDelegate(new MallCollectItemView(this));
        mAdapter.addItemViewDelegate(new VideoCollectItemView(this));
        mAdapter.addItemViewDelegate(new LiveCollectItemView(this));
        mAdapter.addItemViewDelegate(new SpecialtItemView(this));
        mRecyclerView.setAdapter(mAdapter);
    }


    private List<CollectionBean> getData(String key){
        String data = ACache.get(MyApplication.getInstance()).getAsString(key);
        Gson gson = new Gson();
        return gson.fromJson(data,new TypeToken<List<CollectionBean>>(){}.getType());

    }

    public String toJson(List<CollectionBean> list){
        Gson gson=new Gson();
        return gson.toJson(list);
    }
    @Override
    public void showData(final List<CollectionBean> datas) {

        if(datas != null){
            if(!list.containsAll(datas)){
                mRecyclerView.notifyChangeData(datas,mAdapter);
                ACache.get(MyApplication.getInstance()).put(CollectActivity.class.getName()+"_colllectlist" , toJson(datas));
            }
        }else{}
        mRecyclerView.smoothScrollToPosition(0);
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
                selectBean = mAdapter.getDatas().get(position);
                //选择好友分享收藏
                if (isSelect) {
                    shareCollect();
                    return;
                }

                switch (selectBean.getType()) {
                    //新闻文章
                    case "1":
                        if ("0".equals(selectBean.getIsDel())) {
                            NewsBean bean   = selectBean.getData();
                            Intent   intent = new Intent(CollectActivity.this,
                                    NewsWebActivity.class);
                            intent.putExtra("data", bean);
                            startActivity(intent);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该文章已被删除，仅供浏览");
                        }
                        break;

                    //话题
                    case "2":
                        if ("0".equals(selectBean.getIsDel())) {
                            ChatInfosBean bean1 = selectBean.getData();
                            mShareHandler.getLiveInfo(bean1.getId(), null);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该话题已被删除，仅供浏览");
                        }
                        break;

                    //交易信息
                    case "3":
                        if ("0".equals(selectBean.getIsDel())) {
                            DealListBean bean2   = selectBean.getData();
                            Intent       intent2 = new Intent(CollectActivity.this,
                                    GoodsDetailedActivity.class);
                            intent2.putExtra(Constant.INTENT_DATA, bean2);
                            startActivity(intent2);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该交易已被删除，仅供浏览");
                        }
                        break;

                    //圈子动态
                    case "4":
                        if ("0".equals(selectBean.getIsDel())) {
                            CircleHomeBean data = selectBean.getData();
                            DynamicDetialActivity.startActivity(CollectActivity.this,
                                    data.getId() + "");
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该动态已被删除，仅供浏览");
                        }
                        break;

                    //自宝义
                    case "5":
                        if ("0".equals(selectBean.getIsDel())) {
                            CustomCollectBean bean3   = selectBean.getData();
                            Intent            intent1 = new Intent(CollectActivity.this,
                                    CollectResolveActivity.class);
                            intent1.putExtra("data", bean3);
                            startActivityForResult(intent1, EDITOR_REQUEST);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该动态已被删除，仅供浏览");
                        }
                        break;

                    //会话文字
                    case "6":
                        if ("0".equals(selectBean.getIsDel())) {
                            ConversationTextBean bean6 = selectBean.getData();
                            bean6.setUserName(selectBean.getUserName());
                            bean6.setUserPic(selectBean.getUserPic());
                            bean6.setCreateTime(selectBean.getCreateTime());
                            Intent intent6 = new Intent(CollectActivity.this,
                                    ConversationCollectDeteilActivity.class);
                            intent6.putExtra("bean", bean6);
                            intent6.putExtra("type", "6");
                            startActivity(intent6);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该会话文字已被删除，仅供浏览");
                        }
                        break;

                    //会话图片
                    case "7":
                        if ("0".equals(selectBean.getIsDel())) {
                            Intent               intent7 = new Intent(CollectActivity.this,
                                    ConversationCollectDeteilActivity.class);
                            ConversationTextBean bean7   = selectBean.getData();
                            bean7.setUserName(selectBean.getUserName());
                            bean7.setUserPic(selectBean.getUserPic());
                            bean7.setCreateTime(selectBean.getCreateTime());
                            intent7.putExtra("bean", bean7);
                            intent7.putExtra("type", "7");
                            startActivity(intent7);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该会话图片已被删除，仅供浏览");
                        }
                        break;

                    //圈子收藏
                    case "8":
                        if ("0".equals(selectBean.getIsDel())) {
                            CircleBean bean8   = selectBean.getData();
                            Message    message = new Message();
                            message.setSenderUserId(UserManager.getInstance().getUserCode());
                            UIMessage uiMessage = UIMessage.obtain(message);
                            mShareHandler.shareHandle(CollectActivity.this, bean8.getId() + "", 4,
                                    uiMessage);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该圈子已被删除，仅供浏览");
                        }
                        break;

                    //系列课收藏
                    case "9":
                        if ("0".equals(selectBean.getIsDel())) {
                            SeriesPageBean series = selectBean.getData();
                            SeriesActivity.startActivity(CollectActivity.this, series.getId());
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该系列课已被删除，仅供浏览");
                        }
                        break;

                    //商城收藏
                    case "10":
                        if ("0".equals(selectBean.getIsDel())) {
                            CollectMallDetailBean mallBean = selectBean.getData();
                            MallDetailedActivity.startActivity(CollectActivity.this,
                                    mallBean.getStoreId() + "");
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该商品已被删除，仅供浏览");
                        }
                        break;

                    //小视频收藏
                    case "11":
                        if ("0".equals(selectBean.getIsDel())) {
                            Intent videoIntent = new Intent(CollectActivity.this, ConversationCollectDeteilActivity.class);
                            ConversationTextBean videoBean   = selectBean.getData();
                            videoBean.setUserName(selectBean.getUserName());
                            videoBean.setUserPic(selectBean.getUserPic());
                            videoBean.setCreateTime(selectBean.getCreateTime());
                            videoIntent.putExtra("bean", videoBean);
                            videoIntent.putExtra("type", "11");
                            startActivity(videoIntent);
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该视频已被删除，仅供浏览");
                        }
                        break;

                    //直播间收藏
                    case "12":
                        if ("0".equals(selectBean.getIsDel())) {
                            LiveRoomBean mLiveRoomBean   = selectBean.getData();
                            LiveHostPageActivity.startActivity(CollectActivity.this,"1", mLiveRoomBean.getId());
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "该直播间已被删除，仅供浏览");
                        }
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
                showManagerPop(view, position);
                return true;
            }
        });

        //setEditor(datas);
    }

    //分享收藏
    private void shareCollect() {
        if ("0".equals(selectBean.getIsDel())) {
            Conversation.ConversationType type   = (Conversation.ConversationType) getIntent().getSerializableExtra(
                    "type");
            Intent                        intent = new Intent();
            intent.putExtra(Constant.INTENT_DATA,
                    new EventShareMessage(Constant.SELECT_TYPE_COLLECT, selectBean, type));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            ToastUtil.showShort(MyApplication.getInstance(), "该条数据已被删除，仅供浏览");
        }
    }

    private void showManagerPop(View view, int position) {
        if (mBubblePopup != null && mBubblePopup.isShowing()) {
            mBubblePopup.dismiss();
        }
        final int      tempPosition   = position;
        CollectionBean collectionBean = mAdapter.getDatas().get(tempPosition);

        View inflate = View.inflate(this, R.layout.popup_bubble_text, null);
        TextView tvCopy = inflate.findViewById(R.id.tv_copy);
        View line = inflate.findViewById(R.id.line);

        if (collectionBean.getData() instanceof CircleHomeBean) {
            CircleHomeBean circleBean = collectionBean.getData();
            tvCopy.setTag(circleBean);
            tvCopy.setVisibility(TextUtils.isEmpty(circleBean.getContent()) ? View.GONE : View.VISIBLE);
            line.setVisibility(TextUtils.isEmpty(circleBean.getContent()) ? View.GONE : View.VISIBLE);
            tvCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBubblePopup != null && mBubblePopup.isShowing()) {
                        mBubblePopup.dismiss();
                    }
                    CircleHomeBean bean = (CircleHomeBean) v.getTag();
                    ClipboardManager cmb  = (ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
                    cmb.setText(bean.getContent());
                    ToastUtil.showShort(CollectActivity.this, "已复制");
                }
            });

        } else {
            line.setVisibility(View.GONE);
            tvCopy.setVisibility(View.GONE);
        }


        TextView tvDelet = inflate.findViewById(R.id.tv_bubble);
        tvDelet.setTag(collectionBean);
        tvDelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBubblePopup != null && mBubblePopup.isShowing()) {
                    mBubblePopup.dismiss();
                }
                if (!UserManager.getInstance().isLogin()) {
                    Toast.makeText(CollectActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                CollectionBean bean = (CollectionBean) v.getTag();
                deleteCollect(UserManager.getInstance().getToken(), bean.getId(), tempPosition);
            }
        });

        int gravity = view.getTop() > 200 ? Gravity.TOP : Gravity.BOTTOM;
        mBubblePopup = new BubblePopup(this, inflate);
        mBubblePopup.anchorView(view).showAnim(null).dismissAnim(null).gravity(gravity).show();
    }

    @Override
    public void tokenOverdue() {
        Intent intent = new Intent(CollectActivity.this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_COLLECT);
    }

    @Override
    public void setPresenter(CollectContract.Presenter presenter) {
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


    @Override
    public void showRefreshFinish(List<CollectionBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
//        cbEditor.setChecked(false);
    }

    @Override
    public void showLoadMore(List<CollectionBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    /**
     * 删除按钮
     */
    @Subscribe
    public void onEvent(EventCollectSelectBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (i == bean.getPosition() && bean.isSelected() == true) {
                holders.add(bean.holder);
                count++;
            } else if (i == bean.getPosition() && bean.isSelected() == false) {
                count--;
                if (count < 0) {
                    count = 0;
                }
            }
        }
        tvNewsCollect.setText("删除(" + count + ")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.requestCode.NEWS_COLLECT == requestCode && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
            return;
        }
        if (requestCode == EDITOR_REQUEST && resultCode == RESULT_OK) {
            initData();
        }

        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
        mShareHandler.destroy();
    }
}
