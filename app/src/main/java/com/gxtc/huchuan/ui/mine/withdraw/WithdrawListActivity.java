package com.gxtc.huchuan.ui.mine.withdraw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.WithDrawListAdapter;
import com.gxtc.huchuan.bean.AccountSetInfoBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.TransCircleBeanEvent;
import com.gxtc.huchuan.bean.pay.AccountSet;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CustomDayDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.PayApi;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleInfoContract;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleInfoPresenter;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.ui.mine.withdraw.WithdrawActivity.SET_ACCOUNT;

/**
 * zzg
 */
public class WithdrawListActivity extends BaseTitleActivity implements View.OnClickListener{

    @BindView(R.id.recyclerView) RecyclerView       listView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;

    private WithDrawListAdapter          mAdapter;
    List<AccountSet>  mdataList;
     AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrwa_list);

    }

    @Override
    public void initView() {
        EventBusUtil.register(this);
            getBaseHeadView().showTitle("提现");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView( ).showHeadRightButton("新增", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoUtil.goToActivityForResult(WithdrawListActivity.this, WithdrawInputActivity.class, SET_ACCOUNT);
            }
        });
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        listView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST,WindowUtil.dip2px(this,1),getResources().getColor(R.color.module_divide_line)));
        mAdapter = new WithDrawListAdapter(this, new ArrayList<AccountSet>(),
                R.layout.item_withdrwa_list);
        listView.setAdapter(mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent();
                intent.putExtra("name", mAdapter.getList().get(position).getUserName());
                intent.putExtra("account",mAdapter.getList().get(position).getUserAccount());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mAdapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
            @Override
            public void onItemLongClick(View v, final int position) {
              mAlertDialog=DialogUtil.showDeportDialog(WithdrawListActivity.this, false, null, "确定要删除",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(v.getId() == R.id.tv_dialog_confirm){
                                    deleteAccount(mAdapter.getList().get(position).getId(),position);
                                }
                                    mAlertDialog.dismiss();
                            }
                        });
            }
        });
    }

    private void deleteAccount(String id,final int position) {
        getBaseLoadingView().showLoading();
        delsub= PayApi.getInstance().deleteAccountSet(UserManager.getInstance().getToken(),id).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        getBaseLoadingView().hideLoading();
                        listView.removeData(mAdapter,position);
                        Intent intent = new Intent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        if (mAdapter.getItemCount() == 0) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(WithdrawListActivity.this,message);
                    }
                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }
     Subscription sub;
     Subscription delsub;
    @Override
    public void initData() {
        getBaseLoadingView().showLoading();
        sub= PayApi.getInstance().getAccountSet(UserManager.getInstance().getToken()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<AccountSet>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(data == null) return;
                        getBaseLoadingView().hideLoading();
                        mdataList= (List<AccountSet>) data;
                        listView.notifyChangeData(mdataList,mAdapter);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(WithdrawListActivity.this,message);
                    }
                }));
    }

    @Override
    public void initListener() {
        super.initListener();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_ACCOUNT && resultCode == RESULT_OK) {
            initData();
        }
    }
    //圈子成员类型
    @Subscribe(sticky = true)
    public void onEvent(Boolean bean) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        if(sub != null && !sub.isUnsubscribed()){
            sub.unsubscribe();
        }
        if(delsub != null && !delsub.isUnsubscribed()){
            delsub.unsubscribe();
        }
    }
}
