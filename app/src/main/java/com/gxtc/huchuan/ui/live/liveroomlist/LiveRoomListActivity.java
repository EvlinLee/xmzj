package com.gxtc.huchuan.ui.live.liveroomlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveRoomListAdapter;
import com.gxtc.huchuan.bean.FollowLecturerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gubr on 2017/3/31.
 * 课堂  更多    课堂列表
 */

public class LiveRoomListActivity extends BaseTitleActivity implements LiveRoomListContract.View,
        View.OnClickListener, BaseRecyclerAdapter.OnReItemOnClickListener {
    private static final String TAG = "LiveRoomListActivity";

    public static final int LIVE_REQUEST = 1;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private LiveRoomListContract.Presenter mPresenter;
    private LiveRoomListAdapter mLiveRoomListAdapter;
    private String mTitle;
    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlist);

    }

    @Override
    public void initView() {
        new LiveRoomListPresenter(this);
        mTitle = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(mTitle)) {
            flag=true;
            getBaseHeadView().showTitle(mTitle);
        }else{
            flag=false;
            getBaseHeadView().showTitle("精选课堂");
        }



        getBaseHeadView().showBackButton(this);
//        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false));
        mRecyclerview.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void initData() {
        if (flag) {
            mPresenter.getFollowDatas("0",null);
        }else{

            mPresenter.getDatas("0", null);
        }
    }

    @Override
    public void initListener() {
        mRecyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (flag) {
                    mPresenter.getFollowDatas("" + mLiveRoomListAdapter.getList().size(), null);
                }else{
                    mPresenter.getDatas("" + mLiveRoomListAdapter.getList().size(), null);
                }
            }
        });
    }

    @Override
    public void setPresenter(LiveRoomListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
        mRecyclerview.loadFinish();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showDatas(List<FollowLecturerBean> datas) {
        if (mLiveRoomListAdapter == null) {
            mLiveRoomListAdapter = new LiveRoomListAdapter(this, datas, R.layout
                    .item_live_liveroomlist);
            mLiveRoomListAdapter.setOnReItemOnClickListener(this);
            mLiveRoomListAdapter.setOnFollowClickListener(new LiveRoomListAdapter.OnFollowClickListener() {
                @Override
                public void onFollowCick(String id, int position) {
                    if (UserManager.getInstance().isLogin()) {
                        mPresenter.changeFollow(UserManager.getInstance().getToken(),id,position);
                    }
                }
            });
            mRecyclerview.setAdapter(mLiveRoomListAdapter);
        } else {
            mRecyclerview.changeData(datas, mLiveRoomListAdapter);
        }
    }

    @Override
    public void updateFollow(String id, int position) {
//        if (mLiveRoomListAdapter != null) {
//            LiveRoomBean liveRoomBean = mLiveRoomListAdapter.getList().get(position);
//            if (liveRoomBean.getId().equals(id)) {
//                liveRoomBean.toggleFollow();
//                mRecyclerview.notifyItemChanged(position);
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, LiveRoomListActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(View v, int position) {
        String id = mLiveRoomListAdapter.getList().get(position).getChatRoomId();
        Intent intent = new Intent(this, LiveHostPageActivity.class);
        intent.putExtra("liveId", "" + id);
        intent.putExtra("isManage", "1");
        startActivityForResult(intent, LIVE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LIVE_REQUEST && resultCode == RESULT_OK) {
            LiveRoomBean bean = (LiveRoomBean) data.getSerializableExtra("bean");
            if (mLiveRoomListAdapter != null) {
                List<FollowLecturerBean> list = mLiveRoomListAdapter.getList();
               /* for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getChatRoomId().equals(bean.getId())) {
                        list.remove(i);
                        FollowLecturerBean followLecturerBean = new FollowLecturerBean();
                        followLecturerBean.setChatRoomId(bean.getId());
                        followLecturerBean.setFsCount(Integer.parseInt(bean.getFs()));
                        followLecturerBean.setHeadPic(bean.getHeadPic());
                        followLecturerBean.setName(bean.getName());
                        followLecturerBean.setUserCode(bean.getUserCode());
                        followLecturerBean.setNum(Integer.parseInt(bean.getChatInfoCount() == null ? "0" : bean.getChatInfoCount()));
                        list.add(i, followLecturerBean);
                        break;
                    }
                }*/
                if (mRecyclerview != null && mLiveRoomListAdapter != null) {
                    mRecyclerview.notifyChangeData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
