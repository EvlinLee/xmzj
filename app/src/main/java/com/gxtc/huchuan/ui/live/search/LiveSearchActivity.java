package com.gxtc.huchuan.ui.live.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gubr on 2017/3/31.
 */
@Deprecated
public class LiveSearchActivity extends BaseTitleActivity implements View.OnClickListener {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private View searchView;
    private SearchView mSearchView;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private MultiItemTypeAdapter<SearchBean> mListMultiItemTypeAdapter;
    private List<SearchBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livesearch);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        searchView = View.inflate(this, R.layout.model_search_actionbar, null);
        ((RelativeLayout) getBaseHeadView().getParentView()).addView(searchView);
        mSearchView = (SearchView) searchView.findViewById(R.id.sv_search);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mListMultiItemTypeAdapter = new
                MultiItemTypeAdapter<>(this, new ArrayList<SearchBean>());
        mListMultiItemTypeAdapter.addItemViewDelegate(new NewsSearchItemView(this));
        mListMultiItemTypeAdapter.addItemViewDelegate(new LiveRoomSearchItemView(this));
        mListMultiItemTypeAdapter.addItemViewDelegate(new TopicSearchItemView(this));
        mRecyclerview.setAdapter(mListMultiItemTypeAdapter);
    }

    @Override
    public void initListener() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("LiveSearchActivity", query);
                search(query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("LiveSearchActivity", newText);
                return false;
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                break;
        }
    }

    private void search(String searchKey) {
        getBaseLoadingView().showLoading();
        getBaseEmptyView().hideEmptyView();
        HashMap<String, String> map = new HashMap<>();

        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
        }
        map.put("type", "2,3");
        map.put("start", "0");
        map.put("searchKey", searchKey);
        search(map);

    }


    private void search(HashMap<String, String> map) {
        mCompositeSubscription.add(AllApi.getInstance().searchList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<SearchBean>>>(new ApiCallBack<List<SearchBean>>() {
                    @Override
                    public void onSuccess(List<SearchBean> data) {
                        if (mListMultiItemTypeAdapter != null) {
                            mData = new ArrayList<SearchBean>();

                            Observable.from(data)
                                    .filter(new Func1<SearchBean, Boolean>() {
                                        @Override
                                        public Boolean call(SearchBean searchBean) {
                                            return searchBean.getDatas().size() > 0;
                                        }
                                    })
                                    .subscribe(new Observer<SearchBean>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(SearchBean searchBean) {
                                            mData.add(searchBean);
                                        }
                                    });
                            getBaseLoadingView().hideLoading();

                            if (mData.size() > 0) {

                            } else {
                                getBaseEmptyView().showEmptyView();
                            }
                            mListMultiItemTypeAdapter.changeDatas(mData);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(getBaseLoadingView() == null)   return;
                        getBaseLoadingView().hideLoading();
                    }
                })));
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }
}
