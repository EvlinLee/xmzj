/*
package com.gxtc.huchuan.ui.mine.personalhomepage.recommend;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomeContract;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePresenter;
import com.gxtc.huchuan.ui.mine.personalhomepage.homepage.PersonalHomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

*/
/**
 * Describe:个人主页>推荐
 * Created by ALing on 2017/4/12 .
 *//*


public class RecommendFragment extends BaseTitleFragment implements PersonalHomeContract.View{
    private static final String TAG = PersonalHomeFragment.class.getSimpleName();
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.rc_list)
    RecyclerView mRcList;

    private PersonalHomeContract.Presenter mPresenter;
    private MultiItemTypeAdapter<PersonalHomeDataBean> mListMultiItemTypeAdapter;
    private String userCode;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_personal_homepage, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        new PersonalHomePresenter(this);
        initRecyclerView();

        userCode = UserManager.getInstance().getUser().getUserCode();

        mPresenter.getUserRecommendList(userCode);
    }
    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新重新获取数据
                mPresenter.getUserRecommendList(userCode);
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mRcList.setLayoutManager(linearLayoutManager);
        mListMultiItemTypeAdapter = new
                MultiItemTypeAdapter<>(getActivity(), new ArrayList<PersonalHomeDataBean>());
        mListMultiItemTypeAdapter.addItemViewDelegate(new RecommendDealItem(getActivity()));
        mListMultiItemTypeAdapter.addItemViewDelegate(new RecommendNewsItem(getActivity()));
        mListMultiItemTypeAdapter.addItemViewDelegate(new RecommendTopicItem(getActivity()));
        mRcList.setAdapter(mListMultiItemTypeAdapter);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
    }

    List<PersonalHomeDataBean> mData;
    @Override
    public void showHomePageSelfList(List<PersonalHomeDataBean> list) {}

    private void parseList(List<PersonalHomeDataBean> list) {
        mData = new ArrayList<>();
        Observable.from(list)
                .filter(new Func1<PersonalHomeDataBean, Boolean>() {
                    @Override
                    public Boolean call(PersonalHomeDataBean bean) {
                        return bean.getData().size() > 0;
                    }
                })
                .subscribe(new Observer<PersonalHomeDataBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PersonalHomeDataBean bean) {
                        Log.d("LiveSearchActivity", "searchBean.getDatas()" +
                                ".size():" + bean.getData().size());
                        mData.add(bean);
                    }
                });
        showLoadFinish();

        if (mData.size() > 0) {
            Log.d(TAG, "showHomePageSelfList: "+mData.size());
        } else {
            showEmpty();
        }
        mListMultiItemTypeAdapter.changeDatas(mData);
    }

    @Override
    public void showHomePageUserList(List<PersonalHomeDataBean> list) {
    }

    @Override
    public void showSelfData(User user) {
    }

    @Override
    public void showMenberData(User user) {
    }

    @Override
    public void showUserFocus(Object object) {

    }

    @Override
    public void showRecommendList(List<PersonalHomeDataBean> list) {
        Log.d(TAG, "showHomePageSelfList: "+list.toString());
        mSwipeLayout.setRefreshing(false);
        parseList(list);
    }

    @Override
    public void setPresenter(PersonalHomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        mSwipeLayout.setRefreshing(false);
        ToastUtil.showShort(getActivity(),info);
    }

    @Override
    public void showNetError() {
        mSwipeLayout.setRefreshing(false);
        ToastUtil.showShort(getActivity(),getResources().getString(R.string.rc_network_error));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
*/
