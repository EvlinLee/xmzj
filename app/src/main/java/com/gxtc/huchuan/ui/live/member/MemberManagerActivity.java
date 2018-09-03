package com.gxtc.huchuan.ui.live.member;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MemeberManagerAdapter;
import com.gxtc.huchuan.bean.BannedOrBlackUserBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.MyActionSheetDialog;
import com.gxtc.huchuan.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * Created by Gubr on 2017/4/2.
 */

public class MemberManagerActivity extends BaseTitleActivity implements MemberManagerContract.View,
        View.OnClickListener {
    private static final String TAG = "MemberManagerActivity";

    private SearchView searchview;
    private TextView tvMemberCount;
    @BindView(R.id.rc_memeber_manager)
    RecyclerView rcMemeberManager;

    private MemberManagerContract.Presenter presenter;
    private MemeberManagerAdapter adapter;
    private String chatInfoId;
    private String searchKey;
    private boolean isLoadMore;
    private String jounCount;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memeber_manage);
    }

    @Override
    public void initView() {
        chatInfoId = getIntent().getStringExtra("chatInfoId");
        jounCount = getIntent().getStringExtra("jounCount");
        type = getIntent().getStringExtra("type");
        String joinType = getIntent().getStringExtra("joinType");
        if (chatInfoId == null) chatInfoId = "1";
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(getString(R.string.title_member_manage));
        new MemberManagerPresenter(this);
        searchview = new SearchView(this);
        tvMemberCount = new TextView(this);
        tvMemberCount.setPadding(20, 10, 10, 10);
        tvMemberCount.setGravity(Gravity.CENTER | Gravity.LEFT);
        rcMemeberManager.addHeadView(searchview);
        rcMemeberManager.addHeadView(tvMemberCount);
        rcMemeberManager.setLayoutManager(new LinearLayoutManager(this));
        rcMemeberManager.setLoadMoreView(R.layout.model_footview_loadmore);

        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("chatId", chatInfoId);
        map.put("joinType", joinType);
        adapter = new MemeberManagerAdapter(this, new ArrayList<ChatJoinBean.MemberBean>(), R.layout.item_member_manage, map, rcMemeberManager);
        rcMemeberManager.setHasFixedSize(true);
        rcMemeberManager.setAdapter(adapter);
    }


    @Override
    public void initListener() {
        rcMemeberManager.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                presenter.getDatas(chatInfoId, "" + adapter.getItemCount(), searchKey, type);
            }
        });

        searchview.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isLoadMore = false;
                searchKey = query;
                presenter.getDatas(chatInfoId, "0", searchKey, type);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    isLoadMore = false;
                    searchKey = null;
                    rcMemeberManager.reLoadFinish();
                    presenter.getDatas(chatInfoId, "0", searchKey, type);
                }
                return false;
            }
        });

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String userCode = adapter.getList().get(position).getUserCode();
                PersonalInfoActivity.startActivity(MemberManagerActivity.this, userCode);



            }
        });
    }

    @Override
    public void initData() {
        presenter.getDatas(chatInfoId, "0", "", type);
    }

    @Override
    public void showDatas(ArrayList<ChatJoinBean.MemberBean> datas) {
        if (!isLoadMore) {
            if (datas != null && datas.size() > 0) {
                tvMemberCount.setText("所有参与人共" + jounCount + "人");
                rcMemeberManager.notifyChangeData(datas, adapter);
            }else{
                getBaseEmptyView().showEmptyView();
            }
        } else {
            showLoadMoreDatas(datas);
        }
    }

    @Override
    public void showLoadMoreDatas(ArrayList<ChatJoinBean.MemberBean> beens) {
        if (beens != null && beens.size() > 0) {
            rcMemeberManager.changeData(beens, adapter);
        } else {
            rcMemeberManager.loadFinish();
        }
    }

    @Override
    public void setPresenter(MemberManagerContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseEmptyView().hideEmptyView();
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
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {

    }

    public static void startActivity(Context context, String chatInfoId) {
        Intent intent = new Intent(context, MemberManagerActivity.class);
        intent.putExtra("chatInfoId", chatInfoId);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String chatInfoId, String jounCount, String type, String joinType) {
        Intent intent = new Intent(context, MemberManagerActivity.class);
        intent.putExtra("chatInfoId", chatInfoId);
        intent.putExtra("jounCount", jounCount);
        intent.putExtra("joinType", joinType);

        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    public void ShowBottomMenu(String userCode, String joinType, boolean bisBlack) {
        if (userCode.equals(UserManager.getInstance().getUserCode())) return;
        final List<String> itemList = new ArrayList<>();
//        if (!joinType.equals("1"))
//            itemList.add("升级为管理员");
//        if (!joinType.equals("2"))
//         itemList.add("升级讲师");
        if (bisBlack)
            itemList.add("取消黑名单");
        else
            itemList.add("加入黑名单");

        String[] s = new String[itemList.size()];

        if (itemList.size() == 0) return;
        final MyActionSheetDialog dialog = new MyActionSheetDialog(this,
                itemList.toArray(s), null);
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginBottom(
                0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (itemList.get(position)) {
                        case "升级为管理员":

                            break;
                        case "移除成员":

                            break;
                        case "加入黑名单 ":
//                            collectMessage(message);
                            break;
                        case "取消黑名单 ":
//                            collectMessage(message);
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //flagStr  用户处理类型。1和2 1：黑名单，2：封禁
    public void pullBlack(final SignUpMemberBean bean, final String flagStr) {
        if (UserManager.getInstance().isLogin()) {

            AllApi.getInstance().cancelBannedOrBalck(UserManager.getInstance()
                            .getToken(),
                    UserManager.getInstance().getChatRoomId(), String.valueOf(flagStr),
                    bean.getUserCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new
                            ApiObserver<ApiResponseBean<List<BannedOrBlackUserBean>>>
                            (new ApiCallBack() {
                                @Override
                                public void onSuccess(Object data) {
//                                    flag = false;
                                    if ("2".equals(flagStr)) {
                                        bean.changeIsBlock();
                                    } else if ("1".equals(flagStr)) {
                                        bean.changeIsBlack();
                                    }
                                    adapter.notifyDataSetChanged();
                                    Log.d(TAG, "onSuccess: 成功");
                                }

                                @Override
                                public void onError(String errorCode, String message) {

//                                    flag = false;
                                    ToastUtil.showShort(MemberManagerActivity.this, message);
                                    Log.d(TAG, "onError: ");
                                }
                            }));
        }


    }

@Override
protected void onDestroy(){
        super.onDestroy();
        presenter.destroy();
        }
        }
