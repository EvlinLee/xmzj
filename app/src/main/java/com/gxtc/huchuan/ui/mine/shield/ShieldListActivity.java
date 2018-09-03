package com.gxtc.huchuan.ui.mine.shield;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ShieldListAdapter;
import com.gxtc.huchuan.bean.ShiledListBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePageActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.ShieldCircleDynamicHandler;
import com.gxtc.huchuan.widget.CircleRecyclerView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 宋家任 on 2017/6/7.
 * 屏蔽列表
 */

public class ShieldListActivity extends BaseTitleActivity {

    @BindView(R.id.rv_shield)      CircleRecyclerView mRecyclerView;
    @BindView(R.id.tv_shield_sum)  TextView           tvShieldSum;
    @BindView(R.id.headBackButton) ImageButton        headBackButton;
    @BindView(R.id.cb_editor)      CheckBox           cbEditor;
    @BindView(R.id.headTitle)      TextView           mtitke;

    Subscription sub;
    Subscription subShield;
    String       type;
    String       title;

    private ShieldListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shield_list);
    }

    public static void gotoActivity(Context m, String type, String title) {
        Intent intent = new Intent(m, ShieldListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        m.startActivity(intent);
    }

    @Override
    public void initData() {
        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("title");
        mtitke.setText(title);
        getShieldList("0", type);

    }

    /**
     * 获取屏蔽列表
     */
    private void getShieldList(final String start, final String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("start", start);
        map.put("pageSize", 150 + "");
        map.put("type", type);//0：不看该用户动态；1：不给该用户看动态；2：黑名单
        sub = MineApi.getInstance().shiledList(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ShiledListBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        List<ShiledListBean> datas = (List<ShiledListBean>) data;
                        if (datas.size() == 0 || datas == null) {
                            findViewById(R.id.vs).setVisibility(View.VISIBLE);
                            return;
                        }

                        if ("0".equals(start)) {
                            tvShieldSum.setVisibility(View.VISIBLE);
                            initRecyclerView(datas);
                        } else {
                            mAdapter.changeData(datas);
                        }
                        if ((Integer.valueOf(start) + 150) == datas.size()) {
                            getShieldList(String.valueOf(Integer.valueOf(start) + 150), type);
                        }

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(ShieldListActivity.this, errorCode, message);
                    }
                }));
    }

    private void initRecyclerView(List<ShiledListBean> datas) {

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mAdapter = new ShieldListAdapter(this, datas, R.layout.item_shield_list);
        mRecyclerView.setAdapter(mAdapter);

        tvShieldSum.setText(title + "(" + mAdapter.getList().size() + ")");
        setEdit();

        mAdapter.setOnDelItemListener(new ShieldListAdapter.OnDelItemListener() {
            @Override
            public void onDel(BaseRecyclerAdapter.ViewHolder holder, int position,
                              ShiledListBean bean) {
                removeData(holder, position);
            }
        });

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if("4".equals(type)){
                    CircleMainActivity.startActivity(ShieldListActivity.this,Integer.parseInt(mAdapter.getList().get(position).getTargetCode()));
                }else {
                    PersonalHomePageActivity.startActivity(ShieldListActivity.this,
                            mAdapter.getList().get(position).getTargetCode());
                }
            }
        });
    }


    private void removeData(final BaseRecyclerAdapter.ViewHolder holder, final int position) {
        DialogUtil.createDialog(this, "", "确定解除屏蔽", "取消", "确定", new DialogUtil.DialogClickListener() {
            @Override
            public void clickLeftButton(View view) {}

            @Override
            public void clickRightButton(View view) {
                //4 为解除屏蔽的圈子动态
                if("4".equals(type)){
                    removeCircleDynanic(mAdapter.getList().get(position), position);
                }else {
                    relieveShield(mAdapter.getList().get(position), position);
                }
            }
        });
    }

    private void removeCircleDynanic(final ShiledListBean bean, final int position) {
        // type 0 解除 1 屏蔽
        final int type = 0;
        ShieldCircleDynamicHandler.getInstant().receiveDynamic(UserManager.getInstance().getToken(),
               Integer.parseInt(bean.getTargetCode()), type, new ApiCallBack<Object>() {

                    @Override
                    public void onSuccess(Object data) {
                        mAdapter.getList().remove(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyDataSetChanged();
                        cbEditor.setChecked(true);
                        tvShieldSum.setText("屏蔽圈子的动态(" + mAdapter.getList().size() + ")");
                        if (mAdapter.getList().size() == 0) {
                            findViewById(R.id.vs).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                });
        ShieldCircleDynamicHandler.getInstant().addTask(this);
    }

    /**
     * 解除屏蔽
     */
    private void relieveShield(final ShiledListBean bean, final int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("userCode", String.valueOf(bean.getTargetCode()));
        map.put("type", type);//0：不看该用户动态；1：不给该用户看动态；2：黑名单 3：文章

        subShield = AllApi.getInstance().deleteByUserCode(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mAdapter.getList().remove(position);
                        mAdapter.notifyItemRemoved(position);
                        cbEditor.setChecked(true);
                        tvShieldSum.setText("不看他(她)的动态(" + mAdapter.getList().size() + ")");
                        if (mAdapter.getList().size() == 0) {
                            findViewById(R.id.vs).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
    }

    private void setEdit() {
        cbEditor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.setShowRemoveIcon(isChecked);
                if (isChecked) {
                    cbEditor.setText("取消");

                } else {
                    cbEditor.setText("解除");
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        if (subShield != null && !subShield.isUnsubscribed()) {
            subShield.unsubscribe();
        }
        ShieldCircleDynamicHandler.getInstant().cancelTask(this);
    }

    @OnClick(R.id.headBackButton)
    public void onViewClicked() {
        finish();
    }
}
