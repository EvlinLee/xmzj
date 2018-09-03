package com.gxtc.huchuan.ui.circle.classroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveRoomAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 圈子－课堂
 * Created by Steven on 17/4/25.
 */

public class ClassroomActivity extends BaseTitleActivity implements ClassroomContract.View,
        View.OnClickListener,TextWatcher {
    private static final String TAG = "ClassroomActivity";
    @BindView(R.id.recyclerView) RecyclerView       listView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;
    private ImageView                 searchImage;
    private EditText                  searchEditText;
    private LiveRoomAdapter mAdapter;
    private int GOTO_CIRCLE_REQUESTCODE = 1 << 3;
    private int LOGINREQUEST            = 1 << 4;
    List<ChatInfosBean> tempDatas = new ArrayList<>();
    ClassroomContract.Presenter mPresenter;
    private int    mCircleid;
    private String mUserCode;
    public  String searchKey = "";
    public  boolean  searchLoadMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("课堂");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.person_circle_manage_icon_add,this);
    }

    @Override
    public void initListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchLoadMore = false;
                swipeLayout.setRefreshing(false);
                if(!TextUtils.isEmpty(searchKey)){
                    mPresenter.getSeachData(false,searchKey,mCircleid+"");
                }
            }
        });
    }

    @Override
    public void initData() {
        mCircleid = getIntent().getIntExtra("circleid", 0);
        mUserCode = getIntent().getStringExtra("userCode");
        View view = getLayoutInflater().inflate(R.layout.search_bar_layout,null);
        searchImage = (ImageView) view.findViewById(R.id.seach_iamge);
        searchEditText = (EditText) view.findViewById(R.id.seach_title);
        searchEditText.addTextChangedListener(this);
        searchEditText.setHint("搜索课堂");
        listView.addHeadView(view);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowUtil.closeInputMethod(ClassroomActivity.this);
                mPresenter.getSeachData(false,searchKey,mCircleid+"");
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    mPresenter.getSeachData(false,searchKey,mCircleid+"");
                }
                return false;
            }
        });
        new ClassroomPresenter(this);
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        listView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(!TextUtils.isEmpty(searchKey)){
                    searchLoadMore = true;
                    mPresenter.getSeachData(true,searchKey,mCircleid+"");
                }else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("userCode", mUserCode);
                    if (mCircleid != 0) map.put("groupId", ""+mCircleid);
                    mPresenter.loadmoreDatas(map);
                }
            }
        });
        getDatas();
    }


    private void getDatas() {
        getBaseLoadingView().showLoading();
        HashMap<String, String> map = new HashMap<>();
        map.put("userCode", mUserCode);

        if (mCircleid != 0) map.put("groupId", ""+mCircleid);
        mPresenter.getDatas(map);
    }

    @Override
    public void showLiveRoom(final List<ChatInfosBean> datas) {
        getBaseLoadingView().hideLoading();
        if (mAdapter == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mAdapter = new LiveRoomAdapter(this, datas, R.layout.item_live_room);
            listView.setLayoutManager(linearLayoutManager);
            listView.setAdapter(mAdapter);
            mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {

                    ChatInfosBean bean = mAdapter.getList().get(position);
                    ChatItemClick(bean);
                }
            });
        } else {
            mAdapter.changeDatas(datas);
        }
        tempDatas.addAll(datas);

    }

    @Override
    public void showSearChLiveRoom(List<SearchBean> datas) {
        final List<ChatInfosBean> searchDatas = new ArrayList<>();
        if(datas == null)  return;
        Observable.from(datas).flatMap(new Func1<SearchBean, Observable<Object>>() {
            @Override
            public Observable<Object> call(SearchBean searchBean) {
                return Observable.just(searchBean.getDatas()).flatMap(new Func1<List<Object>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(List<Object> objects) {
                        return Observable.from(objects);
                    }
                });
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Observer<Object>() {

                      @Override
                      public void onCompleted() {
                          if(searchDatas != null && searchDatas.size() > 0){
                              if(!searchLoadMore){
                                  listView.notifyChangeData(searchDatas,mAdapter);
                              }else {
                                  listView.changeData(searchDatas,mAdapter);
                              }
                          }else {
                              listView.loadFinishNotView();
                          }
                      }

                      @Override
                      public void onError(Throwable e) {
                          ToastUtil.showShort(MyApplication.getInstance(),e.getMessage());
                      }

                      @Override
                      public void onNext(Object o) {
                          ChatInfosBean bena = (ChatInfosBean) o;
                          searchDatas.add(bena);
                      }
                  });

    }

    @Override
    public void showSearData(List<ClassAndSeriseBean> datas) {}

    @Override
    public void showlaodMoreLiveRoom(List<ChatInfosBean> datas) {
        if (mAdapter != null) listView.changeData(datas, mAdapter);
    }

    @Override
    public void showUnauditGroup(List<ClassAndSeriseBean> datas) {}


    public static void startActivity(Context context, String userCode, int circleid) {
        Intent intent = new Intent(context, ClassroomActivity.class);
        intent.putExtra("circleid", circleid);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }


    private void ChatItemClick(ChatInfosBean bean) {
        if (bean.isSingUp()) {
            LiveIntroActivity.startActivity(this, bean.getId());
        } else if (!"0".equals(bean.getChatSeries())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.isSingUp())) {
                    LiveIntroActivity.startActivity(this, bean.getId());
                } else {
                    //这里提示先购买系列课
                    //joinSeriesDialog(bean.getChatSeries());
                    SeriesActivity.startActivity(this,bean.getChatSeries(), true);
                }
            } else {
                GotoUtil.goToActivityForResult(ClassroomActivity.this,
                        LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(this, bean.getId());
                } else {
                    //这里提示加入圈子
                    joinGroupDialog(bean.getGroupId());
                }
            } else {
                GotoUtil.goToActivityForResult(ClassroomActivity.this,
                        LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else {
            //是否加入圈子,   0：未加入；1：已加入
            if(bean.getJoinGroup() .equals("1") ){
                LiveIntroActivity.startActivity(this, bean.getId());
            }else {
                //是否收费（0：免费，1：收费）
                if(bean.getIsfree() .equals("0") ){
                    LiveIntroActivity.startActivity(this, bean.getId());
                }else {
                    ToastUtil.showShort(this,"该课堂为收费课堂，你可以加入本圈子或是付费才可以进入课堂");
                }
            }
        }
    }

//    private void joinSeriesDialog(final String seriesId) {
//        final NormalDialog dialog = new NormalDialog(getContext());
//        dialog.content("要先购买系列课才能进入?")//
////              .showAnim(mBasIn)//
////              .dismissAnim(mBasOut)//
//              .show();
//        dialog.btnText("取消", "加入");
//        dialog.setOnBtnClickL(new OnBtnClickL() {
//            @Override
//            public void onBtnClick() {
////                        T.showShort(mContext, "left");
//                dialog.dismiss();
//            }
//        }, new OnBtnClickL() {
//
//            @Override
//            public void onBtnClick() {
//                SeriesActivity.startActivity(this, seriesId,true);
//                dialog.dismiss();
//            }
//        });
//    }


    private void joinGroupDialog(final long groupId) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("要先加载圈子才能进入?")//
//              .showAnim(mBasIn)//
//              .dismissAnim(mBasOut)//
              .show();
        dialog.btnText("取消", "加入");
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
//                        T.showShort(mContext, "left");
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                Intent intent = new Intent(ClassroomActivity.this, CircleJoinActivity.class);
                intent.putExtra("byLiveId", (int) groupId);
                startActivityForResult(intent, GOTO_CIRCLE_REQUESTCODE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void setPresenter(ClassroomContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
        if (listView != null) {
            listView.loadFinish();
        }
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
getBaseEmptyView().showEmptyView(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatas();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.HeadRightImageButton:
                GotoUtil.goToActivity(this, CreateTopicActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchEditText.removeTextChangedListener(this);
        if(mPresenter != null) mPresenter.destroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchKey = s.toString();
        if(TextUtils.isEmpty(searchKey)){
            listView.notifyChangeData(tempDatas,mAdapter);
        }

    }
}
