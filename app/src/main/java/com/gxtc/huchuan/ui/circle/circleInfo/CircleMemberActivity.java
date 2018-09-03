package com.gxtc.huchuan.ui.circle.circleInfo;

import android.content.Context;
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
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleMemberAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.TransCircleBeanEvent;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CustomDayDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.MD5Util;
import com.gxtc.huchuan.widget.MyActionSheetDialog;
import com.gxtc.huchuan.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子成员列表
 */
public class CircleMemberActivity extends BaseTitleActivity implements View.OnClickListener,
        CircleInfoContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView listView;
    @BindView(R.id.swipelayout)
    SwipeRefreshLayout swipeLayout;

    private CircleMemberAdapter mAdapter;
    private CircleInfoContract.Presenter mPresenter;
    private CircleBean bean;
    private int flag;
    private int mGroupId;
    private AlertDialog dialog;
    private AlertDialog mdialog;
    private ArrayList<String> itemList;
    private ArrayList<CircleMemberBean> teapData;
    private CircleMemberBean mcircleMemberBean;
    private String searKey = "";
    ;
    private Subscription subSear;
    private HashMap<String, String> map = new HashMap();
    private int location;
    private SearchView mSearchView;
    private int start = 0;
    private CustomDayDialog mCustomDayDialog;
    private ListDialog mListDialog;
    private String[] datas = new String[]{"1天", "2天", "3天", "4天", "30天", "永久"};
    private AlertDialog mAlertDialog;
    private String onlyLook = "";//只看某类成员 1禁发动态，2禁言， 3付费， 4免费， 5后台 传空字符串就是查看所有

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_member);
    }

    @Override
    public void initView() {
        EventBusUtil.register(this);
        if (flag == 0) {
            getBaseHeadView().showTitle("圈子成员");
        } else if (flag == 1) {
            getBaseHeadView().showTitle("管理员");
        } else {
            getBaseHeadView().showTitle("成员管理");
        }
        getBaseHeadView().showBackButton(this);
//        getBaseHeadView().showHeadRightButton("邀请好友", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                inviteFriends();
//            }
//        });
        if (flag != 0) {
            getBaseHeadView().showHeadRightButton("筛选", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterDialog();
                }
            });
        }

    }

//    private void inviteFriends() {
//        //管理员或是圈主
//        Intent intent = new Intent();
//        if (bean.getMemberType() == 2) {
//            intent.setClass(this, InviteActivity.class);
//            intent.putExtra("CircleBean", bean);
//        } else {
//            String url = bean.getShareUrl() + "0";
//            intent.setClass(this, CircleInviteActivity.class);
//            intent.putExtra("id", bean.getId() + "");
//            intent.putExtra("share_img_url", bean.getCover());
//            intent.putExtra("share_title", bean.getName());
//            intent.putExtra("share_url", url);
//            intent.putExtra("memberType", 0);
//            intent.putExtra("money", bean.getBrokerage());
//        }
//        startActivity(intent);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    @Override
    public void initData() {
        teapData = new ArrayList<>();
        bean = (CircleBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        mGroupId = bean.getId();

        new CircleInfoPresenter(this);
        swipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        mSearchView = new SearchView(this);
        if (flag == 2) {
            listView.addHeadView(mSearchView);
        }
        mAdapter = new CircleMemberAdapter(this, new ArrayList<CircleMemberBean>(), R.layout.item_circle_member, flag);
        listView.setAdapter(mAdapter);

        if (flag == 0) {
            //获取圈子管理员数据
            mPresenter.getMemberList(mGroupId, flag, false, onlyLook);
        } else if (flag == 1) {
            //获取圈子成员数据
            mPresenter.getMemberList(mGroupId, flag, false, onlyLook);
        } else {
            //获取全部成员数据
            mPresenter.getMemberList(mGroupId, flag, false, onlyLook);
        }

        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                start = 0;
                if (!TextUtils.isEmpty(query.toString())) {
                    searKey = query.toString();
                    searchMeamber(query.toString(), false);
                } else {
                    searKey = "";
                    listView.reLoadFinish();
                    listView.notifyChangeData(teapData, mAdapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                start = 0;
                if (!TextUtils.isEmpty(query.toString())) {
                    searKey = query.toString();
                    searchMeamber(query.toString(), false);
                } else {
                    searKey = "";
                    listView.reLoadFinish();
                    listView.notifyChangeData(teapData, mAdapter);
                }
                return false;
            }
        });

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                location = position;
                //管理圈子成员
                if (flag == 2) {
                    mcircleMemberBean = mAdapter.getList().get(position);

                    //管理员只能管理普通用户
                    if (bean.getMemberType() == 1 && mcircleMemberBean.getMemberType() == bean.getMemberType()) {
                        return;
                    }

                    //圈主权限
                    if (bean.getMemberType() == 2) {

                        //管理员
                        if (1 == mcircleMemberBean.getMemberType()) {
                            itemList = new ArrayList<String>();
                            itemList.add("删除该成员");
                            itemList.add("转让圈子");
                            itemList.add("取消管理员");

                            //创建了群聊才有禁言功能，0：已创建，1：没有创建
                            if (bean.getCreateGroupChat() == 0) {
                                if (mcircleMemberBean.getIsNotalk() == 0) {
                                    itemList.add("禁言");
                                } else {
                                    itemList.add("取消禁言");
                                }
                            }

                            if ("0".equals(mcircleMemberBean.getIsAlsdinfo())) {
                                itemList.add("禁发动态");
                            } else {
                                itemList.add("取消禁发动态");
                            }
                            showSettingDialog(CircleMemberActivity.this, mcircleMemberBean, itemList);

                            //普通成员
                        } else if (0 == mcircleMemberBean.getMemberType()) {
                            itemList = new ArrayList<String>();
                            itemList.add("删除该成员");
                            itemList.add("转让圈子");
                            itemList.add("升为管理员");
                            //创建了群聊才有禁言功能，0：已创建，1：没有创建
                            if (bean.getCreateGroupChat() == 0) {
                                if (mcircleMemberBean.getIsNotalk() == 0) {
                                    itemList.add("禁言");
                                } else {
                                    itemList.add("取消禁言");
                                }
                            }

                            if ("0".equals(mcircleMemberBean.getIsAlsdinfo())) {
                                itemList.add("禁发动态");
                            } else {
                                itemList.add("取消禁发动态");
                            }
                            showSettingDialog(CircleMemberActivity.this, mcircleMemberBean, itemList);
                        }
                    }

                    //圈主权限
                    if (bean.getMemberType() == 1) {
                        if (0 == mcircleMemberBean.getMemberType()) {
                            itemList = new ArrayList<>();
                            itemList.add("删除该成员");
                            //创建了群聊才有禁言功能，0：已创建，1：没有创建
                            if (bean.getCreateGroupChat() == 0) {
                                if (mcircleMemberBean.getIsNotalk() == 0) {
                                    itemList.add("禁言");
                                } else {
                                    itemList.add("取消禁言");
                                }
                            }
                            if ("0".equals(mcircleMemberBean.getIsAlsdinfo())) {
                                itemList.add("禁发动态");
                            } else {
                                itemList.add("取消禁发动态");
                            }
                            showSettingDialog(CircleMemberActivity.this, mcircleMemberBean, itemList);
                        }
                    }

                    //跳转个人资料页
                } else {
                    PersonalInfoActivity.startActivity(CircleMemberActivity.this, mAdapter.getList().get(position).getUserCode());
                }
            }
        });
    }


    private void searchMeamber(String searchKey, final boolean isLoadMore) {
        map.put("start", start + "");
        map.put("searchKey", searchKey);
        map.put("groupId", mGroupId + "");
        map.put("pageSize", "15");
        Subscription subSear = CircleApi.getInstance().searchListMember(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<CircleMemberBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data != null && ((List<CircleMemberBean>) data).size() > 0) {
                            if (!isLoadMore) {
                                showSearchData((List<CircleMemberBean>) data);
                            } else {
                                showSearchLoadMoreData((List<CircleMemberBean>) data);
                            }
                        } else {
                            listView.loadFinish();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(CircleMemberActivity.this, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, subSear);
    }

    @Override
    public void initListener() {
        super.initListener();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                if (!TextUtils.isEmpty(searKey)) {
                    start = 0;
                    searchMeamber(searKey, false);
                } else {
                    if (flag == 0) {
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);     //刷新重新获取数据
                    } else {
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);     //刷新重新获取数据
                    }
                }
            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!TextUtils.isEmpty(searKey)) {
                    start = start + 15;
                    searchMeamber(searKey, true);
                } else {
                    mPresenter.loadMore(mGroupId, flag);       //加载更多
                }
            }
        });
    }

    /**
     * 筛选对话框
     */
    private void showFilterDialog() {
        final List<String> list = new ArrayList<>();
        list.add("查看全部成员");
        list.add("只看禁言中的成员");
        list.add("只看禁动态中的成员");
        list.add("只看免费加入的成员");
        list.add("只看付费加入的成员");
        list.add("只看后台添加的成员");

        final String[] contents = new String[list.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(CircleMemberActivity.this, list.toArray(contents), null);
        dialog.isTitleShow(false)
                .titleTextSize_SP(14.5f)
                .dividerColor(CircleMemberActivity.this.getResources().getColor(R.color.transparency))
                .dividerHeight(10)
                .cancelText("取消")
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (list.get(position)) {
                    case "查看全部成员":
                        onlyLook = "";
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);
                        dialog.dismiss();
                        break;
                    case "只看禁言中的成员":
                        onlyLook = "2";
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);
                        dialog.dismiss();
                        break;
                    case "只看禁动态中的成员":
                        onlyLook = "1";
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);
                        dialog.dismiss();
                        break;
                    case "只看免费加入的成员":
                        onlyLook = "4";
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);
                        dialog.dismiss();
                        break;
                    case "只看付费加入的成员":
                        onlyLook = "3";
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);
                        dialog.dismiss();
                        break;
                    case "只看后台添加的成员":
                        onlyLook = "5";
                        mPresenter.getMemberList(mGroupId, flag, true, onlyLook);
                        dialog.dismiss();
                        break;
                }
            }
        });
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    GotoUtil.goToActivity(CircleMemberActivity.this, LoginAndRegisteActivity.class);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void showMemberList(List<CircleMemberBean> datas) {
        getBaseEmptyView().hideEmptyView();
        swipeLayout.setRefreshing(false);
        listView.notifyChangeData(datas, mAdapter);
        listView.setAdapter(mAdapter);
        teapData.addAll(datas);
    }

    @Override
    public void showRefreshFinish(List<CircleMemberBean> datas) {
        getBaseEmptyView().hideEmptyView();
        swipeLayout.setRefreshing(false);
        teapData.clear();
        teapData.addAll(datas);
        listView.notifyChangeData(datas, mAdapter);
    }

    public void showSearchData(List<CircleMemberBean> datas) {
        swipeLayout.setRefreshing(false);
        listView.notifyChangeData(datas, mAdapter);
    }

    public void showSearchLoadMoreData(List<CircleMemberBean> datas) {
        listView.changeData(datas, mAdapter);
    }

    public void showSettingDialog(final Context context, final CircleMemberBean circleMemberBean, final ArrayList<String> itemList) {
        SparseArray<Integer> colorArray = new SparseArray<>();
        colorArray.put(0, R.color.color_fb4717);

        final String[] contents = new String[itemList.size()];
        final MyActionSheetDialog dialog = new MyActionSheetDialog(context, itemList.toArray(contents), null);
        dialog.isTitleShow(false)
                .titleTextSize_SP(14.5f)
                .dividerColor(context.getResources().getColor(R.color.transparency))
                .dividerHeight(10)
                .moreTextColor(colorArray)
                .cancelText("取消")
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //圈主权限
                    if (bean.getMemberType() == 2) {
                        switch (itemList.get(position)) {
                            //删除该成员
                            case "删除该成员":
                                removeMemberDialog(circleMemberBean);
                                break;

                            //转让圈子
                            case "转让圈子":
                                transCircleDialog(circleMemberBean);
                                break;

                            //升为管理员或取消管理员
                            case "升为管理员":
                            case "取消管理员":
                                //0 是普通成员，升级为管理员
                                if (circleMemberBean.getMemberType() == 0) {
                                    upAdministratorDialog(circleMemberBean);

                                    //1 是管理员，取消管理员
                                } else if (circleMemberBean.getMemberType() == 1) {
                                    cancelTheAdministrator(circleMemberBean);
                                }
                                break;

                            //禁言
                            case "禁言":
                            case "取消禁言":
                                if (mcircleMemberBean.getIsNotalk() == 0) {
                                    showSelectDialog();
                                } else {
                                    forbidentSay(0);//解禁
                                }
                                break;
                            //禁分动态或解禁
                            case "禁发动态":
                            case "取消禁发动态":
                                forbidentDynamic(mcircleMemberBean);
                                break;
                        }
                    }

                    if (bean.getMemberType() == 1) {
                        switch (itemList.get(position)) {
                            //删除该成员
                            case "删除该成员":
                                removeMemberDialog(circleMemberBean);
                                break;

                            //禁言
                            case "禁言":
                            case "取消禁言":
                                if (mcircleMemberBean.getIsNotalk() == 0) {
                                    showSelectDialog();
                                } else {
                                    forbidentSay(0);//解禁
                                }
                                break;
                            //禁分动态或解禁
                            case "禁发动态":
                            case "取消禁发动态":
                                forbidentDynamic(mcircleMemberBean);
                                break;
                        }
                    }
                    dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void forbidentDynamic(final CircleMemberBean mCircleHomeBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupId", mGroupId + "");
        map.put("userCode", mCircleHomeBean.getUserCode());
        map.put("token", UserManager.getInstance().getToken());

        Subscription sub = AllApi.getInstance().setGroupUserDynamicStart(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data == null) return;
                        JSONObject jb = null;
                        try {
                            jb = new JSONObject(GsonUtil.objectToJson(data));
                            String statue = String.valueOf(jb.optInt("dynamicStart"));
                            switch (statue) {
                                case "0":
                                    mCircleHomeBean.setIsAlsdinfo("0");
                                    ToastUtil.showShort(CircleMemberActivity.this, "解禁成功");
                                    break;
                                case "1":
                                    mCircleHomeBean.setIsAlsdinfo("1");
                                    ToastUtil.showShort(CircleMemberActivity.this, "禁发动态成功");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CircleMemberActivity.this, errorCode, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    public void showSelectDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this, datas, true, R.style.cusDialog);
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 5) {
                        int days = Integer.parseInt(datas[position].replace("天", "").trim());
                        forbidentSay(days * 24 * 60);       //后台接口按分计算
                    } else {
                        int days = -1;
                        forbidentSay(days);
                    }
                }
            });
        }
        mListDialog.show();
    }

    public void editDays() {
        if (mCustomDayDialog == null) {
            mCustomDayDialog = new CustomDayDialog(this, R.style.cusDialog);
            mCustomDayDialog.setOnItemClickListener(new CustomDayDialog.OnItemClickListener() {
                @Override
                public void confrm(String content) {
                    if (!TextUtils.isEmpty(content.trim())) {
                        int days = Integer.parseInt(content);
                        forbidentSay(days * 24 * 60);
                        mCustomDayDialog.dismiss();
                    } else {
                        ToastUtil.showShort(CircleMemberActivity.this, "禁言时间不能为空");
                    }
                }

                @Override
                public void cancel() {
                    mCustomDayDialog.dismiss();
                }
            });
        }
        mCustomDayDialog.show();
    }


    private void forbidentSay(int days) {
        Subscription sub = AllApi.getInstance()
                .setMeamberNotSay(mGroupId, UserManager.getInstance().getToken(), days, mcircleMemberBean.getUserCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mcircleMemberBean.getIsNotalk() == 0) {
                            mcircleMemberBean.setIsNotalk(1);
                            ToastUtil.showLong(CircleMemberActivity.this, "禁言成功");
                        } else {
                            mcircleMemberBean.setIsNotalk(0);
                            ToastUtil.showLong(CircleMemberActivity.this, "解禁成功");
                        }
                        listView.notifyItemChanged(location);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getBaseContext(), message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    //升为管理员
    private void upAdministratorDialog(final CircleMemberBean circleMemberBean) {
        dialog = DialogUtil.showInputDialog(this, false, "", "确认设置为管理员？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.changeMemberTpye(UserManager.getInstance().getToken(),
                                    circleMemberBean, 1);
                        } else {
                            Toast.makeText(CircleMemberActivity.this, "登录后才能操作",
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                });
    }

    //取消管理员
    private void cancelTheAdministrator(final CircleMemberBean circleMemberBean) {
        dialog = DialogUtil.showInputDialog(this, false, "", "确认取消管理员？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.changeMemberTpye(UserManager.getInstance().getToken(),
                                    circleMemberBean, 0);
                        } else {
                            Toast.makeText(CircleMemberActivity.this, "登录后才能操作",
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                });
    }

    private void transCircleDialog(final CircleMemberBean circleMemberBean) {
        dialog = DialogUtil.showInputDialog(this, false, "", "确认将圈子转让给此成员？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondConfirm(circleMemberBean);
                        dialog.dismiss();
                    }
                });
    }

    private void secondConfirm(final CircleMemberBean circleMemberBean) {
        mdialog = DialogUtil.showInputDialog(this, false, "", "请再次确认将圈子转让给此成员？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.transCircle(UserManager.getInstance().getToken(),
                                    circleMemberBean);
                        } else {
                            Toast.makeText(CircleMemberActivity.this, "登录后才能操作",
                                    Toast.LENGTH_SHORT).show();
                        }
                        mdialog.dismiss();

                    }
                });
    }

    /**
     * 删除成员
     *
     * @param circleMemberBean
     */
    private void removeMemberDialog(final CircleMemberBean circleMemberBean) {
        dialog = DialogUtil.showInputDialog(this, false, "", "确认将此成员移出圈子？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.removeMember(UserManager.getInstance().getToken(), circleMemberBean);
                        } else {
                            Toast.makeText(CircleMemberActivity.this, "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void showLoadMore(List<CircleMemberBean> datas) {
        listView.changeData(datas, mAdapter);
        teapData.addAll(datas);
    }

    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showCompressSuccess(File file) {
    }

    @Override
    public void showCompressFailure() {
    }

    @Override
    public void showUploadingSuccess(String url) {
    }

    @Override
    public void showCircleInfo(CircleBean bean) {
    }

    @Override
    public void showEditCircle(Object o) {
    }

    @Override
    public void removeMember(CircleMemberBean circleMemberBean) {
        mAdapter.getList().remove(circleMemberBean);
        teapData.remove(circleMemberBean);
        listView.notifyChangeData();
    }

    @Override
    public void transCircle(CircleMemberBean circleMemberBean) {
        bean.setIsMy(0);
        EventBusUtil.post(new TransCircleBeanEvent());
        finish();
    }

    @Override
    public void showChangeMemberTpye(CircleMemberBean circleMemberBean) {
        teapData.clear();
        mPresenter.getMemberList(mGroupId, flag, false, onlyLook);
    }

    @Override
    public void setPresenter(CircleInfoContract.Presenter presenter) {
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
        swipeLayout.setRefreshing(false);
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    //圈子成员类型
    @Subscribe(sticky = true)
    public void onEvent(EventCircleIntro bean) {
        if (bean.status == EventCircleIntro.MEMBER) {
            //圈子成员
            flag = 0;
        } else if (bean.status == EventCircleIntro.MANAGER) {
            //圈子管理员
            flag = 1;
        } else {
            //全部成员
            flag = 2;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
    }

}
