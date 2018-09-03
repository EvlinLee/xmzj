package com.gxtc.huchuan.ui.live.hostpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SeresCourseItemAdapter;
import com.gxtc.huchuan.adapter.SeriesCourseAdapter;
import com.gxtc.huchuan.bean.ChatSeriesBean;
import com.gxtc.huchuan.bean.ChatSeriesTypesBean;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.IssueListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/3/7.
 * 系列课
 * 2017/3/9
 * 系列课不多不需要分页
 * 2017/3/10
 * 由于接口原因，商定暂时没有刷新功能，后期后台应该会单独把系列课抽出一个接口
 */

public class SeriesCourseFragment extends BaseTitleFragment {

    @BindView(R.id.tl_live_host_page_series_course) TabLayout    tlLiveHostPageSeriesCourse;
    @BindView(R.id.vp_live_host_page_series_course) ViewPager    mViewPager;
    @BindView(R.id.rl_serier)                       RecyclerView mRecyclerView;
    @BindView(R.id.tv_emptyview)                    NestedScrollView    mTvEmptyview;

    private HashMap<String, String> map = new HashMap<>();
    private ArrayList<Integer>          mGroupIds  = new ArrayList<>();//同步的圈子id

    LiveRoomBean              bean;
    List<ChatSeriesTypesBean> chatSeriesTypesBeens;
    List<ChatSeriesBean>      chatSeries;

    private                      SeriesCourseAdapter mAdapter;

    private ArrayList<Fragment> mFragments;

    //当分类存在时的滑动适配器
    private SeresCourseItemAdapter   mPageAdapter;
    //分类的fragment
    private SeriesCourseItemFragment mItemFragment;

    //分类课系列名字
    private List<String> mdatas;
    private IssueListDialog mIssueListDialog;
    private AlertDialog mAlertDialog;
    private Bundle bundle;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_host_page_series_course, container, false);
        return view;
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        bean = (LiveRoomBean) bundle.getSerializable("bean");
        chatSeriesTypesBeens = new ArrayList<>();
        chatSeriesTypesBeens = bean.getChatSeriesTypes();

        showSeriesItem();
    }

    /**
     * 展示系列课
     */
    private void showSeriesItem() {
        //没有分类的情况
        if (/*chatSeriesTypesBeens.size() == 0*/true) {
            tlLiveHostPageSeriesCourse.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            chatSeries = new ArrayList<>();
            chatSeries = bean.getChatSeries();

            getAllSeriesCourse();

        //存在分类的情况
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mFragments = new ArrayList<>();
            mdatas = new ArrayList<>();


            List<ChatSeriesBean>     chatSeries2 = bean.getChatSeries();
            HashMap<String, Integer> map         = new HashMap<>();
            for (int i = 0; i < chatSeries2.size(); i++) {
                String type = chatSeries2.get(i).getType();
                if (map.get(type) == null) {
                    map.put(type, 1);
                } else {
                    map.put(type, map.get(type) + 1);
                }
            }
            for (int i = 0; i < chatSeriesTypesBeens.size(); i++) {
                if (map.get(chatSeriesTypesBeens.get(i).getId()) == null && i != 0) {
                    chatSeriesTypesBeens.remove(i);
                    --i;
                }
            }

            for (int i = 0; i < chatSeriesTypesBeens.size(); i++) {
                mItemFragment = new SeriesCourseItemFragment();
                mdatas.add(chatSeriesTypesBeens.get(i).getTypeName());
                bundle = new Bundle();

                bundle.putSerializable("series_bean", bean);//把数据传到系列课展示
                bundle.putString("series_id", chatSeriesTypesBeens.get(i).getId());//根据id区分数据
                bundle.putString("series_name", chatSeriesTypesBeens.get(i).getTypeName());

                mItemFragment.setArguments(bundle);
                mFragments.add(mItemFragment);
            }
            tlLiveHostPageSeriesCourse.setupWithViewPager(mViewPager);
            mPageAdapter = new SeresCourseItemAdapter(getFragmentManager(), mFragments, mdatas);
            mViewPager.setOffscreenPageLimit(mFragments.size());
            mViewPager.setAdapter(mPageAdapter);

        }
    }

    /**
     * 获取全部系列课
     */
    private void getAllSeriesCourse() {

        //有系列课
        if (bean.getChatSeries().size() > 0) {
            mTvEmptyview.setVisibility(View.GONE);

            //系列课的网格布局要改成课程的那种布局
            /*mAdapter = new SeriesCourseAdapter(this.getContext(), bean.getChatSeries(), R.layout.item_serier_course);
            GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 2);*/
            mAdapter = new SeriesCourseAdapter(this.getContext(), bean.getChatSeries(), R.layout.item_series_course, bean);
            LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);

            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    ChatSeriesBean chatSeriesBean = mAdapter.getList().get(position);
                    if (chatSeriesBean == null) {
                        Log.d("SeriesCourseFragment", "chatSeriesBean: null");
                    } else {
                        Log.d("SeriesCourseFragment", "chatSeriesBean: have");
                        SeriesActivity.startActivity(getActivity(), chatSeriesBean.getId());
                    }

                }
            });
            mAdapter.setMoreListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int loacation = (int) v.getTag();
                    showCollectionDialogOld(getContext(), mAdapter.getList().get(loacation), loacation);
                }
            });
            //没有系列课
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mTvEmptyview.setVisibility(View.VISIBLE);
        }
    }

    public void showCollectionDialogOld(final Context context, final ChatSeriesBean mChatSeriesBean,
                                        final int loacation) {

        final ArrayList<String> itemList = new ArrayList<String>();
        itemList.add("删除");
        if (!"0".equals(mChatSeriesBean.getFee())) {//不是免费
            itemList.add("退款");
        }
        itemList.add("同步系列课");
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
                        //删除
                        case "删除":
                            mAlertDialog = DialogUtil.showInputDialog(getActivity(), false, "",
                                    "确认删除系列课？", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (UserManager.getInstance().isLogin()) {
                                                deleteSeries(mChatSeriesBean, loacation);
                                            } else {
                                                Toast.makeText(getContext(), "登录后才能操作",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                            break;
                        //退款
                        case "退款":
                            mAlertDialog = DialogUtil.showInputDialog(getActivity(), false, "",
                                    "确认退款？", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (UserManager.getInstance().isLogin()) {
                                                reFunds(mChatSeriesBean);
                                            } else {
                                                Toast.makeText(getContext(), "登录后才能操作",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                            break;
                        //同步系列课
                        case "同步系列课":
                            showSysDialog(mChatSeriesBean);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSysDialog(final ChatSeriesBean mChatSeriesBean) {
        if (mIssueListDialog == null) {
            mIssueListDialog = new IssueListDialog(getContext(), new String[]{});
            mIssueListDialog.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.tv_issue_tongbu) {
                        Intent intent = new Intent(getContext(), SyncIssueInCircleActivity.class);
                        intent.putExtra("default", -1);
                        intent.putExtra("isClass", true);
                        intent.putIntegerArrayListExtra(Constant.INTENT_DATA, mGroupIds);
                        getActivity().startActivityForResult(intent, 1000);
                    }else if(v.getId() == R.id.tv_sure){
                        sysSerise(mChatSeriesBean);
                        mIssueListDialog.dismiss();
                    }
                }
            });
        }
        mIssueListDialog.show();
    }

    private void sysSerise(ChatSeriesBean mChatSeriesBean) {
        map.clear();
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
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private void deleteSeries(ChatSeriesBean mChatSeriesBean, final int loacation) {
        map.clear();
        map.put("id", mChatSeriesBean.getId());
        map.put("token", UserManager.getInstance().getToken());
        Subscription sub = MineApi.getInstance().deleteSeries(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mRecyclerView.removeData(mAdapter, loacation);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void reFunds(ChatSeriesBean chatInfosBean) {
        map.clear();
        map.put("type", "1");//0、课程；1：系列课
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Constant.ResponseCode.ISSUE_TONG_BU) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}


