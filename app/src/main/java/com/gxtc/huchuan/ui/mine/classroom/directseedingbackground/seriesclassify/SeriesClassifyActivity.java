package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.seriesclassify;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SeriesManagerAdapter;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.event.EventPopBubleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.R.attr.id;

/**
 * Describe: 课堂后台 > 系列课分类管理
 * Created by ALing on 2017/3/29 .
 */

public class SeriesClassifyActivity extends BaseTitleActivity implements View.OnClickListener, SeriesClassifyContract.View {

    private static final String TAG = SeriesClassifyActivity.class.getSimpleName();

    @BindView(R.id.rl_add_classify) RelativeLayout mRlAddClassify;
    @BindView(R.id.rc_list)         RecyclerView   mRcList;
    @BindView(R.id.tv_des_classify) TextView       mTvDesClassify;

    private List<ChooseClassifyBean>         mDatas;
    private AlertDialog                      dialog;
    private HashMap<String, String>          map;
    private String                           token;
    private String                           chatRoomId;
    private SeriesClassifyContract.Presenter mPresenter;
    private SeriesManagerAdapter             adapter;
    private String                           editTypeName;
    private String                           typeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_series_class);
    }

    @Override
    public void initView() {
        super.initView();
        EventBusUtil.register(this);
        getBaseHeadView().showTitle(getString(R.string.title_classify_management));
        getBaseHeadView().showBackButton(this);

        mTvDesClassify.setVisibility(View.VISIBLE);
        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));

    }

    @Override
    public void initData() {
        super.initData();
        mDatas = new ArrayList<>();
        new SeriesClassifyPresenter(this);
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
        } else {
            GotoUtil.goToActivity(SeriesClassifyActivity.this, LoginAndRegisteActivity.class);
        }
        getChatSeriesTypeList();
    }

    private void getChatSeriesTypeList() {
        map = new HashMap<>();
        map.put("chatRoomId", chatRoomId);
        mPresenter.getChatSeriesTypeList(map);
    }

    @OnClick({R.id.rl_add_classify})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            case R.id.rl_add_classify:
                addClassify();
                break;
        }
    }

    private void addClassify() {
        dialog = DialogUtil.showInputDialog2(this, true, "系列课名称", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeName = DialogUtil.getEtInput();
                map = new HashMap<String, String>();
                map.put("token", UserManager.getInstance().getToken());
                map.put("typeName", typeName);
                map.put("chatRoomId", chatRoomId);  //课堂id
                mPresenter.addSeriesClassify(map);

                dialog.dismiss();
            }
        });
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showChatSeriesTypeList(List<ChooseClassifyBean> bean) {
        mDatas.addAll(bean);
        adapter = new SeriesManagerAdapter(this, mDatas, R.layout.item_classify_manager);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void showAddSeriesClassify(List<ChooseClassifyBean> bean) {
        //返回列表刷新数据
        mDatas.clear();
        mDatas.addAll(bean);
        ToastUtil.showShort(this, getString(R.string.toast_add_series_classify));
        adapter.notifyDataSetChanged();
        setResult(RESULT_OK);
    }

    //修改系列课分类名称
    @Override
    public void showEditClassifyName(List<ChooseClassifyBean> bean) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        for (int i = 0; i < adapter.getList().size(); i++) {
            ChooseClassifyBean chooseClassifyBean = adapter.getList().get(i);
            String             typeName           = adapter.getList().get(i).getTypeName();
            if (chooseClassifyBean.isSelect()) {
                if (editTypeName.equals(typeName)) {
                    adapter.notifyChangeData(bean);
                }
            }
        }

    }

    //删除系列课分类
    @Override
    public void showDelResult(List<ChooseClassifyBean> bean) {
        ToastUtil.showShort(this, getString(R.string.toast_del_success));
        adapter.notifyChangeData(bean);
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
        getChatSeriesTypeList();
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
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    public void setPresenter(SeriesClassifyContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }

    @Subscribe
    public void onEvent(EventPopBubleBean bean) {
        if ("0".equals(bean.getPosition())) {
            for (int i = 0; i < adapter.getList().size(); i++) {
                ChooseClassifyBean chooseClassifyBean = adapter.getList().get(i);
                if (chooseClassifyBean.isSelect()) {
                    editTypeName = chooseClassifyBean.getTypeName();
                    typeId = chooseClassifyBean.getId();

                }
            }

            //编辑
            dialog = DialogUtil.showSeriesClassifyDialog(this, true, "编辑分类", editTypeName,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String typeName = DialogUtil.getEtInput();
                            map = new HashMap<String, String>();
                            map.put("token", UserManager.getInstance().getToken());
                            map.put("typeName", typeName);
                            map.put("chatRoomId", chatRoomId);  //课堂id
                            map.put("typeId", typeId);
                            mPresenter.editClassifyName(map);

                            dialog.dismiss();
                        }
                    });

        } else {
            //删除
            for (int i = 0; i < adapter.getList().size(); i++) {
                ChooseClassifyBean chooseClassifyBean = adapter.getList().get(i);
                if (chooseClassifyBean.isSelect()) {
                    editTypeName = chooseClassifyBean.getTypeName();
                    typeId = chooseClassifyBean.getId();

                }
            }
            dialog = DialogUtil.showInputDialog(this, true, "确认删除", editTypeName,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            map = new HashMap<String, String>();
                            map.put("token", token);
                            map.put("typeId", typeId);
                            mPresenter.delSeriseClassify(map);

                            dialog.dismiss();
                        }
                    });
        }
    }

}


