package com.gxtc.huchuan.ui.message;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.AllMembersInfoListAdapter;
import com.gxtc.huchuan.adapter.AllMembersListAdapter;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.MessageBean;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.http.service.MessageApi;
import com.gxtc.huchuan.ui.circle.groupmember.AllGroupMemberActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.RecyclerSpace;
import com.gxtc.huchuan.widget.SearchView;

import org.greenrobot.greendao.test.DbTest;

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
 * Created by zzg on 2017/9/15 .
 */

public class AllGroupInfoMemberActivity extends BaseTitleActivity implements View.OnClickListener{
    @BindView(R.id.rl_news)
    RecyclerView mChatMenberList;
    @BindView(R.id.swipe_members)
    SwipeRefreshLayout swipeNews;
    private                          ImageView              ivBack;
    private                          TextView               tvChatCount;
    private                          int                    groupId;
    private                          String                    targetId;
    private                          Subscription           sub;
    public                           List<MessageBean> list;
    private AllMembersInfoListAdapter adapter;
    boolean isLoadMore = false;
    private boolean isAiter;
    private Subscription subSear;
    private String searKey ="";
    private List<MessageBean> teapData = new ArrayList<>();
    private int startSearch = 0;
    private HashMap<String,String> map =new HashMap();
    private int count;
    private SearchView mSearchView;
    private int star = 0;
    private int pageSize = 40;
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
        swipeNews.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        mChatMenberList.setLayoutManager(new GridLayoutManager(this,5));
        mChatMenberList.addItemDecoration(
                new RecyclerSpace(10, getResources().getColor(R.color.white)));
        mChatMenberList.setLoadMoreView(R.layout.model_footview_loadmore);
        showMemberList();
        if (getIntent().getStringExtra("targetId") != null) {
            groupId = getIntent().getIntExtra("groupId", 0);
            targetId = getIntent().getStringExtra("targetId");
            count = getIntent().getIntExtra("count",0);
            isAiter = getIntent().getBooleanExtra("isAiter",false);
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
                isLoadMore=false;
                if(TextUtils.isEmpty(searKey)){
                    star = 0;
                    getMenberList();
                }else {
                    startSearch = 0;
                    searchMeamber(searKey,false);
                }
            }
        });
        mChatMenberList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                if(TextUtils.isEmpty(searKey)){
                    star = star + 40;
                    getMenberList();
                }else {
                    startSearch = startSearch + 15;
                    searchMeamber(searKey,true);
                }
            }
        });
    }

    @Override
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private long loadTime ;
    private void getMenberList() {
        if(star == 0){
            loadTime = System.currentTimeMillis();
        }
        sub = MessageApi.getInstance()
                .getGroupMembers(targetId, star, pageSize, loadTime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<MessageBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        getBaseLoadingView().hideLoading();
                        List<MessageBean> datas = ((List<MessageBean>)data);
                        if(data == null || datas.size() == 0 ){
                            getBaseLoadingView().hideLoading();
                            mChatMenberList.loadFinish();
                            return;
                        }
                        if(!isLoadMore){
                            list.clear();
                            teapData.clear();
                            swipeNews.setRefreshing(false);
                            mChatMenberList.notifyChangeData(datas,adapter);
                        }else {
                            mChatMenberList.changeData(datas,adapter);
                        }
                        list.addAll(datas) ;
                        teapData = list;//暂时保存搜索之前的数据
                        setTittle(list);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                      ToastUtil.showShort(AllGroupInfoMemberActivity.this,message);
                    }
                }));
    }

   public void setTittle(List<MessageBean> datas){
        if(!isAiter){
            tvChatCount.setText("聊天信息("+count+")");
        }else {
            tvChatCount.setText("选择提醒的人");
        }
    }

    private void showMemberList() {
        mSearchView = new SearchView(this);
        mChatMenberList.addHeadView(mSearchView);
        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WindowUtil.closeInputMethod(AllGroupInfoMemberActivity.this);
                searchData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searKey = newText.toString();
                if(TextUtils.isEmpty(searKey)){
                    searchData(); //当搜索为空时，自动还原搜索前的数据
                }
                return false;
            }
        });
        adapter = new AllMembersInfoListAdapter(AllGroupInfoMemberActivity.this, new ArrayList<MessageBean>(),
                R.layout.item_group_chat_member);
        mChatMenberList.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MessageBean circleMemberBean =  adapter.getList().get(position);
                //@他人回调
                if(isAiter){
                    String name = circleMemberBean.getUserName();
                    String code = circleMemberBean.getUserCode();
                    String pic = circleMemberBean.getUserPic();
                    UserInfo info = new UserInfo(code,name, Uri.parse(pic));
                    RongMentionManager.getInstance().mentionMember(info);
                    finish();
                }else {
                    //跳转个人资料
                    PersonalInfoActivity.startActivity(AllGroupInfoMemberActivity.this,circleMemberBean.getUserCode());
                }
            }
        });
    }

    //搜索成员
    private void searchMeamber(String searchKey,final boolean isLoadMore) {
        map.put("start",startSearch+"");
        map.put("searchKey",searchKey);
        map.put("groupChatId",targetId+"");
        map.put("pageSize","30");
        subSear = MessageApi.getInstance().searchMember(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<MessageBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(data != null && ((List<MessageBean> ) data).size() > 0){
                            if(!isLoadMore){
                                showSearchData((List<MessageBean> ) data);
                            }else {
                                showSearchLoadMoreData((List<MessageBean> ) data);
                            }
                        }else {
                            mChatMenberList.loadFinish();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
    }

    public  void showSearchData(List<MessageBean> datas){
        swipeNews.setRefreshing(false);
        mChatMenberList.notifyChangeData(datas, adapter);
        setTittle(datas);
    }

    public  void showSearchLoadMoreData(List<MessageBean> datas){
        mChatMenberList.changeData(datas, adapter);
        setTittle(adapter.getList());
    }

    public void searchData(){
        if(!TextUtils.isEmpty(searKey)){
            startSearch = 0;
            searchMeamber(searKey,false);
        }else{
            searKey = "";
            mChatMenberList.reLoadFinish();
            mChatMenberList.notifyChangeData(teapData,adapter);
            setTittle(teapData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()){
            sub.unsubscribe();
        }
        if (subSear != null && !subSear.isUnsubscribed()){
            subSear.unsubscribe();
        }
    }
}
