package com.gxtc.huchuan.ui.live.hostpage.newhostpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveTopicAdapter;
import com.gxtc.huchuan.adapter.NewLiveTopicAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatSeriesBean;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.SeriesSelBean;
import com.gxtc.huchuan.bean.model.AppenGroudBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.live.hostpage.IGetChatinfos;
import com.gxtc.huchuan.ui.live.hostpage.newhostpage.ClassAndSerisePresenter;
import com.gxtc.huchuan.ui.live.hostpage.newhostpage.LiveAndSeriseContract;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.ClassOrderActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import org.w3c.dom.DOMStringList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2017/12/1.
 * 课程
 */

public class LiveTopicAndSeriseFragment extends BaseTitleFragment implements
        NewLiveTopicAdapter.OnManagerClickListener, LiveAndSeriseContract.View {

    private static final String TAG = "LiveTopicFragment";

    public static final int REQUEST_TOPIC_INTRO = 1;

    @BindView(R.id.rl_topic) RecyclerView       mRecyclerView;
    @BindView(R.id.sw_topic) SwipeRefreshLayout swTopic;

    private NewLiveTopicAdapter    mLiveTopicAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    private boolean isDatasChanged = false;
    private boolean isSeries       = false;
    private double mSeriesFree;

    private String              mChatSeries;
    private String              strStutas;
    private String              shareUserCode;
    private String              freeSign;
    private AlertDialog         dialog;
    private List<SeriesSelBean> mSeriesSelBeanList;
    private ArrayList<Integer> mGroupIds = new ArrayList<>();//同步的圈子id
    private AppenGroudBean mAppenGroudBean;
    private IssueListDialog mIssueListDialog;
    private AlertDialog mAlertDialog;
    private AlertDialog mDialog;
    private LiveAndSeriseContract.Presenter presenter;
    private int start;
    private boolean isLoadMore;
    private int curPosion;
    private String liveId;
    private ChatInfosBean currentBean;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        return view;
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        isSeries = bundle.getBoolean("series");
        mChatSeries = bundle.getString("chatSeries");
        liveId = bundle.getString("liveId");
        shareUserCode = bundle.getString("shareUserCode");
        freeSign = bundle.getString("freeSign");
        mSeriesFree = bundle.getDouble("seriesFree");
        if (TextUtils.isEmpty(liveId)) return;
        getChatSeriesList();
    }

    @Override
    public void initListener() {
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                start = start + 15;
                presenter.getData( liveId,UserManager.getInstance().getToken(),start+"");

            }
        });
        swTopic.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadMore = false;
                start = 0;
                mRecyclerView.reLoadFinish();
                presenter.getData(liveId,UserManager.getInstance().getToken(),start+"");
            }
        });

    }


    @Override
    public void initData() {
        if (TextUtils.isEmpty(liveId)) return;
        getBaseLoadingView().showLoading();
        new ClassAndSerisePresenter(this);
        presenter.getData( liveId,UserManager.getInstance().getToken(),start+"");

            mLiveTopicAdapter = new NewLiveTopicAdapter(getContext(), new ArrayList<ChatInfosBean>(), R.layout.item_topicv_and_serise,R.layout.item_series_course);
            mLiveTopicAdapter.setOnManagerClickListener(this);
            mRecyclerView.setAdapter(mLiveTopicAdapter);
            mLiveTopicAdapter.setOnReItemOnClickListener(new BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
               @Override
               public void onItemClick(View v, int position) {
                   switch (mLiveTopicAdapter.getDatas().get(position).getType()) {
                       //话题
                       case 1:
                            Intent intent = new Intent(getActivity(), LiveIntroActivity.class);
                            intent.putExtra("id",  mLiveTopicAdapter.getDatas().get(position).getId());
                            intent.putExtra("shareUserCode",shareUserCode);
                            intent.putExtra("freeSign",freeSign);
                            startActivityForResult(intent, REQUEST_TOPIC_INTRO);
                          break;
                       //系列课
                       case 2:
                           SeriesActivity.startActivityForResult(getActivity(), mLiveTopicAdapter.getDatas().get(position).getId(), 666);
                           break;
                   }
               }
           });
    }

    private void getChatSeriesList() {
        Subscription sub = LiveApi.getInstance().getChatSeriesSelList(
                liveId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<SeriesSelBean>>>(
                        new ApiCallBack<List<SeriesSelBean>>() {
                            @Override
                            public void onSuccess(List<SeriesSelBean> data) {
                                mSeriesSelBeanList = data;
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(getContext(), message);
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    public void showCollectionDialog(final Context context, final ChatInfosBean chatInfosBean,
                                     final int location) {
        final ArrayList<String> itemList = new ArrayList<String>();
        switch (chatInfosBean.getType()) {
            //话题
            case 1:
                itemList.add("删除");
                itemList.add("移动到系列课");
                if ("2".equals(chatInfosBean.getShowInfo())) {
                    //结束标识。 0：正常，1：结束 2,下架
                    itemList.add("上架");
                    strStutas = "上架";
                } else {
                    itemList.add("下架");
                    strStutas = "下架";
                }

                itemList.add("结束课程");
                if (1 == chatInfosBean.getIsFree()) {
                    itemList.add("退款");
                    itemList.add("查看订单");
                }
                itemList.add("同步课程");
                break;
            //系列课
            case 2:
                itemList.add("删除");
                if (1 == chatInfosBean.getIsFree()) {//不是免费
                    itemList.add("退款");
//                    if(("3".equals(chatInfosBean.getRoleType()) || "4".equals(chatInfosBean.getRoleType())))
                       itemList.add("查看订单");
                }
                itemList.add("同步系列课");
                break;
        }


        final String[] contents = new String[itemList.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(context,
                itemList.toArray(contents), null);
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginBottom(
                0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (itemList.get(position)) {
                        case "删除":
                            removeTopicDialog(chatInfosBean, location);
                            break;
                        case "移动到系列课":
                            curPosion = location;
                            transSeriesDialog(chatInfosBean);
                            break;
                        case "上架":
                        case "下架":
                            showDialog(chatInfosBean, location);
                            break;
                        case "结束课程":
                            endTopicDialog(chatInfosBean);
                            break;
                        case "退款":
                            mDialog = DialogUtil.showInputDialog(getActivity(), false, "", "确定退款？",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (UserManager.getInstance().isLogin()) {
                                                reFunds(chatInfosBean);
                                            } else {
                                                Toast.makeText(getContext(), "登录后才能操作",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            mDialog.dismiss();
                                        }
                                    });
                            break;
                        case "同步课程":
                            showSysDialog(chatInfosBean);
                            break;
                        case "同步系列课":
                            showSysDialog(chatInfosBean);
                            break;
                        case "查看订单":
                            gotoLiveOrder(chatInfosBean.getType(),chatInfosBean);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void gotoLiveOrder(int type,ChatInfosBean chatInfosBean){
        ClassOrderActivity.startActivity(getActivity(), chatInfosBean.getId() ,type);
    }
    private void reFunds(ChatInfosBean chatInfosBean) {
        HashMap<String, String> map = new HashMap<>();
        if(1 == chatInfosBean.getType()){
            map.put("type", "0");//0、课程；1：系列课
        }
        if(2 == chatInfosBean.getType()){
            map.put("type", "1");
        }
        map.put("linkId", chatInfosBean.getId());
        map.put("token", UserManager.getInstance().getToken());
        Subscription sub = MineApi.getInstance().refund(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(getContext(), "退款成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private void showDialog(final ChatInfosBean chatInfosBean, final int position) {
        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null,
                "你只有" + strStutas + "机会一次，确定要" + strStutas, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            repealClass(chatInfosBean, position);
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }


    //下架或上架
    private void repealClass(final ChatInfosBean chatInfosBean, final int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("chatInfoId", chatInfosBean.getId());
        map.put("token", UserManager.getInstance().getToken());
        Subscription sub = MineApi.getInstance().repeal(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (chatInfosBean.getShowInfo().equals(
                                "0") || chatInfosBean.getShowInfo().equals("1")) {
                            //结束标识。 0：正常，1：结束 2,下架
                            chatInfosBean.setShowInfo("2");
                            ToastUtil.showShort(getContext(), "下架成功");
                        } else {
                            chatInfosBean.setShowInfo("0");
                            ToastUtil.showShort(getContext(), "上架成功");
                        }
                        mRecyclerView.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private void showSysDialog(final ChatInfosBean bean) {

            getlistAppendGroup(bean,bean.getType(),true);
            mIssueListDialog = new IssueListDialog(getContext(), new String[]{});
            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(getContext(), SyncIssueInCircleActivity.class);
                        intent.putExtra("default", -1);
                        intent.putExtra("isClass", true);
                        intent.putExtra("type", bean.getType());
                        intent.putExtra("linkId", bean.getId());
//                        if(1 == bean.getType()){
//                            intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mAppenGroudBean.getGroupIds());
//                        }else if(2 == bean.getType()){
                            intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mAppenGroudBean.getGroupIds());
//                        }
                        getActivity().startActivityForResult(intent, 666);
                    } else if (v.getId() == R.id.tv_sure) {
//                        if(1 == bean.getType()){
                            SysGroup(bean);
//                        }else if(2 == bean.getType()){
//                            sysSerise(bean);
//                        }


                        mIssueListDialog.dismiss();
                    }
                }
            });
            mIssueListDialog.show();
    }

    private void sysSerise(ChatInfosBean mChatSeriesBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", mChatSeriesBean.getId());
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        }
        map.put("token", UserManager.getInstance().getToken());
        Subscription sub =  MineApi.getInstance().appendSeriesToGroup(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(getContext(), "同步成功");
                        mGroupIds.clear();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    public void getlistAppendGroup(final ChatInfosBean bean,int type,final boolean flag) {
        if(bean == null)   return;
        HashMap<String, String> map = new HashMap<>();
         map.put("linkId", bean.getId());
         if(type == 1){
             map.put("type", "2");//同步类型1、文章；2、课堂；3、文件
         }
        else if(type == 2){
            map.put("type", "6");//同步类型1、文章；2、课堂；3、文件；6.课程系列
        }

        Subscription sub = MineApi.getInstance().listAppendGroup(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<AppenGroudBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mAppenGroudBean = (AppenGroudBean) data;
                        if(mAppenGroudBean.getGroupIds().size() > 0)
                            getCircleName(flag);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void getCircleName(final boolean flag){
        Subscription sub =
                AllApi.getInstance()
                        .listByUser(UserManager.getInstance().getToken(), "0")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                                List<String>listGroupName=new ArrayList<>();
                                if(flag) {
                                    for (MineCircleBean bean : (List<MineCircleBean>) data) {
                                        if (mAppenGroudBean.getGroupIds().contains(bean.getId())) {
                                            listGroupName.add(bean.getGroupName());
                                        }
                                    }
                                    mIssueListDialog.changeTongbuName(listGroupName);
                                }

                            }

                            @Override
                            public void onError(String errorCode, String message) {}
                        }));

        RxTaskHelper.getInstance().addTask(this,sub);

    }

    //同步文章到圈子 同步类型1、文章；2、课堂；3、文件；6.系列课
    private void SysGroup(final ChatInfosBean bean) {
        if(mGroupIds == null || mGroupIds.size() < 1)   return;

        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
        map.put("groupIds", groupids);
        map.put("linkId", bean.getId() + "");
        if(bean.getType() == 1){
            map.put("type", "2");//同步类型1、文章；2、课堂；3、文件
        }
        else if(bean.getType() == 2){
            map.put("type", "6");//同步类型1、文章；2、课堂；3、文件；4.课堂系列
        }

        Subscription sub = MineApi.getInstance().appendToGroup(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(bean.getAudit() != null) {
                            switch (bean.getAudit()) {
                                case "1":
                                    ToastUtil.showShort(getContext(), "同步成功");
                                    break;
                                default:
                                    ToastUtil.showLong(getContext(), "同步成功,但必须审核成功才会出现在圈子的课堂里");
                                    break;
                            }
                        }
                        else{
                            ToastUtil.showShort(getContext(), "同步成功");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void removeTopicDialog(final ChatInfosBean chatInfosBean, final int position) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = DialogUtil.showInputDialog(getActivity(), false, "", "确认删除课程？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            if(1 == chatInfosBean.getType()){
                                removeTopic(chatInfosBean, position);
                            }else if(2 == chatInfosBean.getType()) {
                                deleteSeries(chatInfosBean, position);
                            }
                        } else {
                            Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void deleteSeries(ChatInfosBean mChatSeriesBean, final int loacation) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", mChatSeriesBean.getId());
        map.put("token", UserManager.getInstance().getToken());
        Subscription sub = MineApi.getInstance().deleteSeries(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mRecyclerView.removeData(mLiveTopicAdapter, loacation);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void removeTopic(final ChatInfosBean chatInfosBean, final int position) {
        Subscription sub = LiveApi.getInstance().delChatInfo(UserManager.getInstance().getToken(),
                chatInfosBean.getId()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mLiveTopicAdapter == null) return;
                        mRecyclerView.removeData(mLiveTopicAdapter, position);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void transSeriesDialog(final ChatInfosBean chatInfosBean) {
        if (mSeriesSelBeanList == null) {
            Toast.makeText(getContext(), "加载数据出错", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chatInfosBean.getIsGroup() == 1) {
            Toast.makeText(getContext(), "已同步圈子的课堂不能移动到系列课中", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSeriesSelBeanList.size() == 0) {
            Toast.makeText(getContext(), "请先建系列课", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> itemList = new ArrayList<String>();
        for (SeriesSelBean seriesSelBean : mSeriesSelBeanList) {
            itemList.add(seriesSelBean.getSeriesname());
        }
        final String[] contents = new String[itemList.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(getContext(),
                itemList.toArray(contents), null);
        dialog.isTitleShow(true).title("选择要移动到的系列课").padding(0, 100, 0, 0).titleTextSize_SP(
                14.5f).widthScale(1f).cancelMarginBottom(0).cornerRadius(0f).dividerHeight(
                1).itemTextColor(getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    commit(chatInfosBean, mSeriesSelBeanList.get(position),curPosion);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void commit(final ChatInfosBean chatInfosBean, final SeriesSelBean seriesSelBean,
                        final int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("id", chatInfosBean.getId());
        map.put("chatSeries", seriesSelBean.getId());

        Subscription sub = LiveApi.getInstance().saveChatInfoIntroduction(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(getContext(), "移动成功", Toast.LENGTH_SHORT).show();
                        chatInfosBean.setChatSeries(seriesSelBean.getId());
                        mRecyclerView.removeData(mLiveTopicAdapter,position);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        Toast.makeText(getContext(), "移动失败", Toast.LENGTH_SHORT).show();
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void endTopicDialog(final ChatInfosBean chatInfosBean) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = DialogUtil.showInputDialog(getActivity(), false, "", "确认结束课程？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            endTopic(chatInfosBean);
                        } else {
                            Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                });
    }

    private void endTopic(final ChatInfosBean chatInfosBean) {
        Subscription sub = LiveApi.getInstance().stopChatInfo(UserManager.getInstance().getToken(),
                chatInfosBean.getId()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<Object>>>(new ApiCallBack<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> data) {
                        chatInfosBean.setEndtime(System.currentTimeMillis() + "");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    public void saveCollection(final ChatInfosBean chatInfosBean, final int position) {
        HashMap<String, String> map = new HashMap<>();
        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
            map.put("bizType", "2");
            map.put("bizId", chatInfosBean.getId());
            saveCollection(map, new ApiCallBack() {
                @Override
                public void onSuccess(Object data) {
                    chatInfosBean.setIsCollect("1");
                    mRecyclerView.notifyItemChanged(position);
                }

                @Override
                public void onError(String errorCode, String message) {
                    ToastUtil.showShort(getContext(), message);
                }
            });

        }
    }

    public void cancelCollection(final ChatInfosBean chatInfosBean, final int position) {
        Log.d(TAG, chatInfosBean.toString());
        HashMap<String, String> map = new HashMap<>();
        if (UserManager.getInstance().isLogin()) {
            map.put("token", UserManager.getInstance().getToken());
            map.put("bizId", chatInfosBean.getId());
            map.put("bizType", "2");
            saveCollection(map, new ApiCallBack() {
                @Override
                public void onSuccess(Object data) {
                    chatInfosBean.setIsCollect("0");
                    mRecyclerView.notifyItemChanged(position);

                }

                @Override
                public void onError(String errorCode, String message) {
                    Log.d(TAG, message);
                }
            });

        }
    }

    public void getChatinfosL(String token, String chatRoom, String chatSeries, int start,
                              ApiCallBack<List<ChatInfosBean>> callBack) {
        Subscription sub = LiveApi.getInstance().getChatInfoList(token, chatRoom, chatSeries,
                start).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(callBack));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    public void saveCollection(Map<String, String> map, ApiCallBack callBack) {
        Subscription sub = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(callBack));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(currentBean != null)
            getlistAppendGroup(currentBean,currentBean.getType(),false);
        if (requestCode == REQUEST_TOPIC_INTRO && resultCode == Activity.RESULT_OK) {
            ChatInfosBean bean = (ChatInfosBean) data.getSerializableExtra("bean");
            if (mLiveTopicAdapter != null && mLiveTopicAdapter.getDatas() != null) {
                for (int i = 0; i < mLiveTopicAdapter.getDatas().size(); i++) {
                    if (mLiveTopicAdapter.getDatas().get(i).getId().equals(bean.getId())) {
                        mLiveTopicAdapter.getDatas().remove(i);
                        mLiveTopicAdapter.getDatas().add(i, bean);
                        break;
                    }
                }
                mRecyclerView.notifyChangeData();
            }
        }
        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            ArrayList<MineCircleBean> selectData = data.getParcelableArrayListExtra("select_data");
            mGroupIds.clear();
            mIssueListDialog.clearText();
            if (selectData.size() > 0) {
                ArrayList<String> listGroupName = new ArrayList<>();
                for (MineCircleBean bean1 : selectData) {
                    listGroupName.add(bean1.getGroupName());
                    mGroupIds.add(bean1.getId());
                }
                mIssueListDialog.changeTongbuName(listGroupName);
            }

        }
        if(requestCode == 666 && resultCode == 667){
            List<ChatInfosBean> list = mLiveTopicAdapter.getDatas();
            String id = data.getStringExtra("SeriesId");
            int newCount = data.getIntExtra("count", 0);
            for(int i = 0; i< list.size(); i++ ){
                ChatInfosBean bean = list.get(i);
                if(bean.getId().equals(id)){
                    int oldCount = Integer.parseInt(TextUtils.isEmpty(bean.getChatInfoCount()) ? "0" : bean.getChatInfoCount());
                    bean.setChatInfoCount((oldCount - newCount) + "");
//                    mLiveTopicAdapter.notifyDataSetChanged();
                    mRecyclerView.notifyItemChanged(i);
                   break;
                }
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View view, ChatInfosBean bean, int position) {
        currentBean=bean;
        showCollectionDialog(getContext(), bean, position);
    }

    @Override
    public void onCollectClick(View view, ChatInfosBean bean, int position) {
        //收藏
        if ("0".equals(bean.getIsCollect())) {
            saveCollection(bean, position);
        } else {
            cancelCollection(bean, position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        if(presenter != null)
        presenter.destroy();
    }

    @Override
    public void showDatat(List<ChatInfosBean> data) {
        getBaseLoadingView().hideLoading();
        if(!isLoadMore){
            swTopic.setRefreshing(false);
            mRecyclerView.notifyChangeData(data,mLiveTopicAdapter);
        }else {
            mRecyclerView.changeData(data,mLiveTopicAdapter);
        }

    }

    @Override
    public void showLoad() {}

    @Override
    public void showLoadFinish() {}

    @Override
    public void showEmpty() {
        if(start == 0){
            getBaseEmptyView().showEmptyContent();
        }else {
            mRecyclerView.loadFinish();
        }
    }

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {}

    @Override
    public void showNetError() {}

    @Override
    public void setPresenter(LiveAndSeriseContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
