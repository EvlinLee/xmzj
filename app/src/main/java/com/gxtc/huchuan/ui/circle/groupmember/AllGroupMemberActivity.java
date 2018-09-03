package com.gxtc.huchuan.ui.circle.groupmember;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.AllMembersListAdapter;
import com.gxtc.huchuan.adapter.GroupMemberListAdapter;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.SearchChatBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleMemberActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.MyGridView;
import com.gxtc.huchuan.widget.RecyclerSpace;
import com.gxtc.huchuan.widget.SearchView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/6/7 .
 */

public class AllGroupMemberActivity extends BaseTitleActivity implements View.OnClickListener {
    @BindView(R.id.rl_news)       RecyclerView       mChatMenberList;
    @BindView(R.id.swipe_members) SwipeRefreshLayout swipeNews;

    private ImageView              ivBack;
    private TextView               tvChatCount;
    private int                    groupId;
    private String                 targetId;
    private Subscription           sub;
    public  List<CircleMemberBean> list;
    private AllMembersListAdapter  adapter;
    boolean isLoadMore = false;
    private boolean      isAiter;
    private Subscription subSear;
    private String                 searKey  = "";
    private List<CircleMemberBean> teapData = new ArrayList<>();
    int                     startSearch = 0;
    HashMap<String, String> map         = new HashMap();
    private int        count;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_group_member);
    }

    @Override
    public void initView() {
        super.initView();
        View head = LayoutInflater.from(this).inflate(R.layout.layout_title_group_chat_info,
                (ViewGroup) getBaseHeadView().getParentView(), false);
        ((RelativeLayout) getBaseHeadView().getParentView()).addView(head);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvChatCount = (TextView) findViewById(R.id.tv_chat_count);
        list = new ArrayList<>();
        ivBack.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        swipeNews.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
        mChatMenberList.setLayoutManager(new GridLayoutManager(this, 5));
        mChatMenberList.addItemDecoration(
                new RecyclerSpace(10, getResources().getColor(R.color.white)));
        mChatMenberList.setLoadMoreView(R.layout.model_footview_loadmore);
        showMemberList();
        if (getIntent().getStringExtra("targetId") != null) {
            groupId = getIntent().getIntExtra("groupId", 0);
            targetId = getIntent().getStringExtra("targetId");
            isAiter = getIntent().getBooleanExtra("isAiter", false);
            count = getIntent().getIntExtra("count", 0);
            getMenberList();
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        swipeNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChatMenberList.reLoadFinish();
                isLoadMore = false;
                if (TextUtils.isEmpty(searKey)) {
                    star = 0;
                    getMenberList();
                } else {
                    startSearch = 0;
                    searchMeamber(searKey, false);
                }
            }
        });
        mChatMenberList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                if (TextUtils.isEmpty(searKey)) {
                    star = star + 40;
                    getMenberList();
                } else {
                    startSearch = startSearch + 15;
                    searchMeamber(searKey, true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    int star     = 0;
    int pageSize = 40;
    long loadTime;
    private void getMenberList() {
        if(star == 0){
            loadTime = System.currentTimeMillis();
        }
        sub = CircleApi.getInstance().getListMember1(targetId, star, pageSize, loadTime).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<CircleMemberBean>>>(
                        new ApiCallBack<List<CircleMemberBean>>() {
                            @Override
                            public void onSuccess(List<CircleMemberBean> data) {
                                getBaseLoadingView().hideLoading();
                                if (data == null || data.size() == 0) {
                                    getBaseLoadingView().hideLoading();
                                    mChatMenberList.loadFinish();
                                    return;
                                }
                                if (!isLoadMore) {
                                    list.clear();
                                    teapData.clear();
                                    swipeNews.setRefreshing(false);
                                    mChatMenberList.notifyChangeData(data, adapter);
                                } else {
                                    mChatMenberList.changeData(data, adapter);
                                }
                                list.addAll(data);
                                teapData = list;//暂时保存搜索之前的数据
                                setTittle(list);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                mChatMenberList.loadFinish();
                                LoginErrorCodeUtil.showHaveTokenError(AllGroupMemberActivity.this,
                                        errorCode, message);
                            }
                        }));
    }

    public void setTittle(List<CircleMemberBean> datas) {
        if (!isAiter) {
            tvChatCount.setText("聊天信息(" + count + ")");
        } else {
            tvChatCount.setText("选择提醒的人");
        }
    }

    private void showMemberList() {
        mSearchView = new SearchView(this);
        mSearchView.setOnQueryTextListener(
                new android.support.v7.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        WindowUtil.closeInputMethod(AllGroupMemberActivity.this);
                        searchData();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searKey = newText.toString();
                        if (TextUtils.isEmpty(searKey)) {
                            searchData(); //当搜索为空时，自动还原搜索前的数据
                        }
                        return false;
                    }
                });
        mChatMenberList.addHeadView(mSearchView);
        adapter = new AllMembersListAdapter(AllGroupMemberActivity.this,
                new ArrayList<CircleMemberBean>(), R.layout.item_group_chat_member);
        mChatMenberList.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position >= adapter.getList().size()) return;
                CircleMemberBean circleMemberBean = adapter.getList().get(position);
                //@他人回调
                if (isAiter) {
                    String   name = circleMemberBean.getUserName();
                    String   code = circleMemberBean.getUserCode();
                    String   pic  = circleMemberBean.getUserPic();
                    UserInfo info = new UserInfo(code, name, Uri.parse(pic));
                    RongMentionManager.getInstance().mentionMember(info);
                    finish();
                } else {
                    //跳转个人资料
                    PersonalInfoActivity.startActivity(AllGroupMemberActivity.this,
                            circleMemberBean.getUserCode());
                }
            }
        });
    }

    private void searchMeamber(final String searchKey, final boolean isLoadMore) {
        mChatMenberList.loadFinish();
        String token = UserManager.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
            map.put("token", token);
        }
        map.put("start", startSearch + "");
        map.put("searchKey", searchKey);
        map.put("type", "1");
        map.put("chatId", targetId);
        map.put("pageSize", "1000");
        subSear = CircleApi.getInstance().searchMentionedMember(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<SearchChatBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data != null && ((List<SearchChatBean>) data).size() > 0) {
                            List<SearchChatBean>   source      = (List<SearchChatBean>) data;
                            List<CircleMemberBean> convertBean = new ArrayList<>();
                            for (SearchChatBean temp : source) {
                                CircleMemberBean bean = new CircleMemberBean();
                                bean.setUserName(temp.getName());
                                bean.setUserCode(temp.getCode());
                                bean.setUserPic(temp.getPic());
                                convertBean.add(bean);
                            }
                            if (!isLoadMore) {
                                showSearchData(convertBean);
                            } else {
                                showSearchLoadMoreData(convertBean);
                            }
                        } else {
                            mChatMenberList.loadFinish();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));
    }

    public void showSearchData(List<CircleMemberBean> datas) {
        swipeNews.setRefreshing(false);
        mChatMenberList.notifyChangeData(datas, adapter);
        setTittle(datas);
    }

    public void showSearchLoadMoreData(List<CircleMemberBean> datas) {
        mChatMenberList.changeData(datas, adapter);
        setTittle(adapter.getList());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        if (subSear != null && !subSear.isUnsubscribed()) {
            subSear.unsubscribe();
        }
    }

    public void searchData() {
        if (!TextUtils.isEmpty(searKey)) {
            startSearch = 0;
            searchMeamber(searKey, false);
        } else {
            searKey = "";
            mChatMenberList.reLoadFinish();
            mChatMenberList.notifyChangeData(teapData, adapter);
            setTittle(teapData);
        }
    }
}
