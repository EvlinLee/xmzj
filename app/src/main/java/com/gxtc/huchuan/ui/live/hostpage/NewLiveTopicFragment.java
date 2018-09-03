package com.gxtc.huchuan.ui.live.hostpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveTopicAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.SeriesSelBean;
import com.gxtc.huchuan.bean.dao.FirstCurriculum;
import com.gxtc.huchuan.bean.dao.FirstCurriculumDao;
import com.gxtc.huchuan.bean.event.EventSeriesInviteBean;
import com.gxtc.huchuan.bean.model.AppenGroudBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/1.
 * 课程
 */

public class NewLiveTopicFragment extends BaseTitleFragment implements
        LiveTopicAdapter.OnManagerClickListener {

    private static final String TAG = "LiveTopicFragment";

    public static final int REQUEST_TOPIC_INTRO = 1;

    @BindView(R.id.rl_topic) RecyclerView       mRecyclerView;
    @BindView(R.id.sw_topic) SwipeRefreshLayout swTopic;

    public LiveTopicAdapter    mLiveTopicAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    private boolean isSeries       = false;

    private String              mChatSeries;
    private String              strStutas;
    private String              shareUserCode;
    private String              freeSign;
    private AlertDialog         dialog;
    private List<SeriesSelBean> mSeriesSelBeanList;
    private List<Integer> mGroupIds = new ArrayList<>();//同步的圈子id
    private AppenGroudBean mAppenGroudBean;
    private IssueListDialog mIssueListDialog;
    private AlertDialog mAlertDialog;
    private SeriesPageBean seriesBean;
    private int start = 0;
    private boolean isLoadMore;
    private String chatRoom;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        return view;
    }


    @Override
    protected void onGetBundle(Bundle bundle) {
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        swTopic.setColorScheme(Constant.REFRESH_COLOR);

        seriesBean = (SeriesPageBean)bundle.getSerializable("seriesBean");
        isSeries = bundle.getBoolean("series");
        chatRoom = bundle.getString("chatRoom");
        mChatSeries = bundle.getString("chatSeries");
        shareUserCode = bundle.getString("shareUserCode");
        freeSign = bundle.getString("freeSign");
        if (TextUtils.isEmpty(chatRoom)) return;
        getChatSeriesList();
    }


    private void getChatSeriesList() {
        Subscription sub = LiveApi.getInstance().getChatSeriesSelList(
                chatRoom).subscribeOn(Schedulers.io()).observeOn(
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

    @Override
    public void initListener() {
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                start = start + 15;
                getData();
            }
        });
        swTopic.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadMore = false;
                start = 0;
                mRecyclerView.reLoadFinish();
                getData();
            }
        });

    }


    @Override
    public void initData() {
        mLiveTopicAdapter = new LiveTopicAdapter(getContext(), new ArrayList<ChatInfosBean>(), R.layout.item_topicv2,seriesBean);
        mLiveTopicAdapter.setOnManagerClickListener(this);
        mRecyclerView.setAdapter(mLiveTopicAdapter);
        mLiveTopicAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, final int position) {
                        if(seriesBean != null){
                            //第一条数据，讲师设置了试听，用户没有买，必须是付费的，不是圈子成员的
                            if(position == 0
                                    && SeriesActivity.AUDITION_TYPE.equals(seriesBean.getIsAuditions())
                                    && Double.valueOf(seriesBean.getFee()) > 0d
                                    && !seriesBean.bIsBuy()
                                    && !"1".equals(seriesBean.getIsSelf())
                                    && "0".equals(seriesBean.getIsGroupUser())
                                    && 0 == seriesBean.getJoinType()){

                                startAuditions(position);

                            //2表示 开启了免费邀请制度，并且报名成功  但是邀请人数未达标
                            } else if(position == 0
                                    && !"1".equals(seriesBean.getIsSelf())
                                    && SeriesActivity.AUDITION_INVITE_TYPE.equals(seriesBean.getIsAuditions())
                                    && !seriesBean.isFinishInvite()
                                    && 0 == seriesBean.getJoinType()) {

                                if(seriesBean.bIsBuy()){
                                    startAuditions(position);
                                }else{
                                    ToastUtil.showShort(getContext(), "报名课程后才可以试听");
                                }

                            //报名成功  但是邀请人数未达标
                            } else if(SeriesActivity.AUDITION_INVITE_TYPE.equals(seriesBean.getIsAuditions())
                                    && !"1".equals(seriesBean.getIsSelf())
                                    && !seriesBean.isFinishInvite()
                                    && 0 == seriesBean.getJoinType()){

                                if(seriesBean.bIsBuy()){
                                    EventBusUtil.post(new EventSeriesInviteBean());
                                }else{
                                    ToastUtil.showShort(getContext(), "报名课程后才可以邀请好友");
                                }

                            }else if(SeriesActivity.AUDITION_INVITE_TYPE.equals(seriesBean.getIsAuditions())
                                    && !"1".equals(seriesBean.getIsSelf())
                                    && seriesBean.isFinishInvite()
                                    && 0 == seriesBean.getJoinType()){
                                FirstCurriculum firstCurriculum = GreenDaoHelper.getInstance()
                                                                  .getSeeion()
                                                                  .getFirstCurriculumDao()
                                                                  .queryBuilder()
                                                                  .where(FirstCurriculumDao.Properties.Id.eq(seriesBean.getId()))
                                                                   .unique();
                                if(firstCurriculum == null){
                                    FirstCurriculum firstCurriculum1 = new FirstCurriculum();
                                    firstCurriculum1.setSeriesOrtopId(seriesBean.getId());
                                    GreenDaoHelper.getInstance().getSeeion().getFirstCurriculumDao().save(firstCurriculum1);
                                    NormalDialog normalDialog = new NormalDialog(getContext());
                                    normalDialog.setTitle("弹窗");
                                    TextView textView = new TextView(getContext());
                                    textView.setText("您已成功解锁课程");
                                    normalDialog.content("您已成功解锁课程")
                                            .btnNum(1)
                                            .btnText("开始听课");
                                    normalDialog.setOnBtnClickL(new OnBtnClickL() {
                                        @Override
                                        public void onBtnClick() {
                                            EnterClassroom(position);
                                        }
                                    });
                                    normalDialog.show();
                                }

                            }else {
                                EnterClassroom(position);
                            }
                        }
                    }
                });

        //试听回调
        mLiveTopicAdapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                startAuditions(position);
            }
        });
        getData();
    }
    private void EnterClassroom(int position){
        Intent intent = new Intent(getActivity(), LiveIntroActivity.class);
        intent.putExtra("id", mLiveTopicAdapter.getList().get(position).getId());
        intent.putExtra("shareUserCode",shareUserCode);
        intent.putExtra("freeSign",freeSign);
        intent.putExtra("seriesBean",seriesBean);
        startActivityForResult(intent, REQUEST_TOPIC_INTRO);
    }



    private void startAuditions(int position){
        Intent intent = new Intent(getActivity(), LiveIntroActivity.class);
        intent.putExtra("id", mLiveTopicAdapter.getList().get(position).getId());
        intent.putExtra("shareUserCode",shareUserCode);
        intent.putExtra("freeSign",freeSign);
        intent.putExtra("seriesBean",seriesBean);
        intent.putExtra("isAuditions",true);
        startActivityForResult(intent, REQUEST_TOPIC_INTRO);
    }

    private  void getData(){
        String token = null;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
        }
        getChatinfosL(token, mChatSeries,
                start, new ApiCallBack<List<ChatInfosBean>>() {
                    @Override
                    public void onSuccess(List<ChatInfosBean> data) {
                        swTopic.setRefreshing(false);
                        if(data != null && data.size() > 0){
                            if(!isLoadMore){
                                mRecyclerView.notifyChangeData(data, mLiveTopicAdapter);
                            }else {
                                mRecyclerView.changeData(data, mLiveTopicAdapter);
                            }
                        }else {
                                if(isLoadMore)
                                mRecyclerView.loadFinish();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        Toast.makeText(NewLiveTopicFragment.this.getContext(), message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showCollectionDialog(final Context context, final ChatInfosBean chatInfosBean,
                                     final int location) {
        final ArrayList<String> itemList = new ArrayList<String>();
        itemList.add("删除课程");
        itemList.add("移动到系列课");
        if (chatInfosBean.getShowinfo().equals("2")) {
            //结束标识。 0：正常，1：结束 2,下架
            itemList.add("上架");
            strStutas = "上架";
        } else {
            itemList.add("下架");
            strStutas = "下架";
        }
        if(!"3".equals(chatInfosBean.getStatus()))
        itemList.add("结束课程");
        if(!isSeries)
        itemList.add("同步课程");

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
                        case "删除课程":
                            removeTopicDialog(chatInfosBean, location);
                            break;
                        case "移动到系列课":
                            transSeriesDialog(chatInfosBean);
                            break;
                        case "上架":
                        case "下架":
                            showDialog(chatInfosBean, location);
                            break;
                        case "结束课程":
                            endTopicDialog(chatInfosBean);
                            break;
                        case "同步课程":
                            showSysDialog(chatInfosBean);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                        if (chatInfosBean.getShowinfo().equals(
                                "0") || chatInfosBean.getShowinfo().equals("1")) {
                            //结束标识。 0：正常，1：结束 2,下架
                            chatInfosBean.setShowinfo("2");
                            ToastUtil.showShort(getContext(), "下架成功");
                        } else {
                            chatInfosBean.setShowinfo("0");
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
            getlistAppendGroup(bean);
            mIssueListDialog = new IssueListDialog(getContext(), new String[]{});
            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(getContext(), SyncIssueInCircleActivity.class);
                        intent.putExtra("default", -1);
                        intent.putExtra("isClass", true);
                        intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mAppenGroudBean.getGroupIds());
                        getActivity().startActivityForResult(intent, 666);
                    } else if (v.getId() == R.id.tv_sure) {
                        SynsGroup(bean);
                        mIssueListDialog.dismiss();
                    }
                }
            });
            mIssueListDialog.show();
    }


    public void getlistAppendGroup(final ChatInfosBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("linkId", bean.getId());
        map.put("type", "2");//同步类型1、文章；2、课堂；3、文件

        Subscription sub = MineApi.getInstance().listAppendGroup(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<AppenGroudBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mAppenGroudBean = (AppenGroudBean) data;
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    //同步文章到圈子 同步类型1、文章；2、课堂；3、文件
    private void SynsGroup(final ChatInfosBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        }
        map.put("linkId", bean.getId() + "");
        map.put("type", "2");

        Subscription sub = MineApi.getInstance().appendToGroup(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        switch (bean.getAudit()) {
                            case "1":
                                ToastUtil.showShort(getContext(), "同步成功");
                                break;
                            default:
                                ToastUtil.showLong(getContext(), "同步成功,但必须审核成功才会出现在圈子的课堂里");
                                break;
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
                            removeTopic(chatInfosBean, position);
                        } else {
                            Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private int delCount = 0;
    private void removeTopic(final ChatInfosBean chatInfosBean, final int position) {
        Subscription sub = LiveApi.getInstance().delChatInfo(UserManager.getInstance().getToken(),
                chatInfosBean.getId()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mLiveTopicAdapter == null) return;
                        mRecyclerView.removeData(mLiveTopicAdapter, position);
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        delCount ++;
                        Intent intent = getActivity().getIntent();
                        intent.putExtra("SeriesId", mChatSeries);
                        intent.putExtra("count", delCount);
                        getActivity().setResult(667, intent);

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
                    commit(chatInfosBean, mSeriesSelBeanList.get(position));
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void commit(final ChatInfosBean chatInfosBean, final SeriesSelBean seriesSelBean) {
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

    public void getChatinfosL(String token, String chatSeries, int start,
                              ApiCallBack<List<ChatInfosBean>> callBack) {
        Subscription sub = LiveApi.getInstance().listChatSeriesChildren(token, chatSeries,
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
        if (requestCode == REQUEST_TOPIC_INTRO && resultCode == Activity.RESULT_OK) {
            ChatInfosBean bean = (ChatInfosBean) data.getSerializableExtra("bean");
            if (mLiveTopicAdapter != null && mLiveTopicAdapter.getList() != null) {
                for (int i = 0; i < mLiveTopicAdapter.getList().size(); i++) {
                    if (mLiveTopicAdapter.getList().get(i).getId().equals(bean.getId())) {
                        mLiveTopicAdapter.getList().remove(i);
                        mLiveTopicAdapter.getList().add(i, bean);
                        break;
                    }
                }

                mRecyclerView.notifyChangeData();
            }
        }
        if (requestCode == 666 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
            ArrayList<MineCircleBean> selectData = data.getParcelableArrayListExtra("select_data");
            mGroupIds.clear();
            if (selectData.size() > 0) {
                ArrayList<String> listGroupName = new ArrayList<>();
                for (MineCircleBean bean1 : selectData) {
                    listGroupName.add(bean1.getGroupName());
                    mGroupIds.add(bean1.getId());
                }
                mIssueListDialog.changeTongbuName(listGroupName);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View view, ChatInfosBean bean, int position) {
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
        ImageHelper.onDestroy(MyApplication.getInstance());
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
