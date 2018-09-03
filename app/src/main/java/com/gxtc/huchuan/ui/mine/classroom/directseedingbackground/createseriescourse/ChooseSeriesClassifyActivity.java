package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ChooseClassifyAdapter;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.event.EventChooseClassifyBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.R.attr.id;

/**
 * Describe: 新建系列课 > 选择系列课分类
 * Created by ALing on 2017/3/15 .
 */

public class ChooseSeriesClassifyActivity extends BaseTitleActivity implements View.OnClickListener, CreateSeriesCourseContract.View, BaseRecyclerAdapter.OnReItemOnClickListener {
    private static final String TAG = ChooseSeriesClassifyActivity.class.getSimpleName();
    @BindView(R.id.rc_list)
    RecyclerView mRcList;

    private AlertDialog dialog;

    private String token;
    private String chatRoomId;
    private HashMap<String,String> map;

    private List<ChooseClassifyBean> mDatas;
    private CreateSeriesCourseContract.Presenter mPresenter;
    private ChooseClassifyAdapter adapter;
    private String typeId;
    private String typeName;
    private String mTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_series_class);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_create_series_classify));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton(getString(R.string.label_save), this);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));

    }

    @Override
    public void initData() {
        super.initData();

        mTypeId = getIntent().getStringExtra("typeId");
        mDatas = new ArrayList<>();
        if (mTypeId != null){       //显示上次选择的item
            getChatSeriesTypeList();
        }else {
            getChatSeriesTypeList();
        }

    }

    private void getChatSeriesTypeList() {
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
            Log.d(TAG, "initData: " + token+"//"+chatRoomId);
        } else {
            GotoUtil.goToActivity(ChooseSeriesClassifyActivity.this, LoginAndRegisteActivity.class);
        }
        new CreateSeriesCoursePresenter(this);

        map = new HashMap<>();
        map.put("chatRoomId",chatRoomId);
        mPresenter.getChatSeriesTypeList(map);
    }

    @OnClick({R.id.rl_add_classify})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //保存
            case R.id.headRightButton:
                save();
                break;
            case R.id.rl_add_classify:
                addClassify();
                break;

        }
    }

    private void save() {
        if (TextUtils.isEmpty(typeId)){
            ToastUtil.showShort(this,getString(R.string.toast_choose_classify));
            return;
        }else {
            EventBusUtil.post(new EventChooseClassifyBean(typeId,typeName));
            finish();
        }
    }

    private void addClassify() {
        dialog = DialogUtil.showInputDialog2(this, true,
                "系列课名称", null ,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WindowUtil.closeInputMethod(ChooseSeriesClassifyActivity.this);
                        String typeName = DialogUtil.getEtInput();
                        map = new HashMap<String, String>();
                        map.put("token", token);
                        map.put("typeName", typeName);
                        map.put("chatRoomId", chatRoomId);  //课堂id
                        mPresenter.addSeriesClassify(map);

                        Log.d(TAG, "onClick: " + typeName);
                        dialog.dismiss();
                    }
                });
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void createLiveSeriesResult(SeriesPageBean bean) {
    }

    //获取系列课分类
    @Override
    public void showChatSeriesTypeList(List<ChooseClassifyBean> bean) {
        if (mTypeId != null){
            mDatas.addAll(bean);
            adapter = new ChooseClassifyAdapter(this, mDatas, R.layout.item_create_series_classify);
            adapter.setOnReItemOnClickListener(this);
            mRcList.setAdapter(adapter);
            for (int i = 0;i < adapter.getList().size();i ++){
                ChooseClassifyBean chooseClassifyBean = adapter.getList().get(i);
                String id = adapter.getList().get(i).getId();
                if (mTypeId.equals(id)){
                    chooseClassifyBean.setSelect(true);
                    adapter.notifyItemChanged(i,chooseClassifyBean);
                }
            }
        }else {
            mDatas.addAll(bean);
            adapter = new ChooseClassifyAdapter(this, mDatas, R.layout.item_create_series_classify);
            adapter.setOnReItemOnClickListener(this);
            mRcList.setAdapter(adapter);
        }

    }

    @Override
    public void showAddSeriesClassify(List<ChooseClassifyBean> bean) {
        //返回列表刷新数据
        mDatas.clear();
        mDatas.addAll(bean);
        ToastUtil.showShort(this,getString(R.string.toast_add_series_classify));
        adapter = new ChooseClassifyAdapter(this, mDatas, R.layout.item_create_series_classify);
        adapter.notifyDataSetChanged();
        adapter.setOnReItemOnClickListener(this);
        Log.d(TAG, "showAddSeriesClassify: "+bean.get(bean.size()-1).getId()+bean.get(bean.size()-1).getTypeName());
    }

    @Override
    public void showCompressSuccess(File file) {}

    @Override
    public void showCompressFailure() {}

    //上传图片成功
    @Override
    public void showUploadingSuccess(String url) {}

    @Override
    public void showDelSeries(Object object) {

    }

    @Override
    public void setPresenter(CreateSeriesCourseContract.Presenter presenter) {
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
        getBaseEmptyView().showEmptyView();
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

    }

    @Override
    public void onItemClick(View v, int position) {
        for (int i = 0; i < adapter.getList().size(); i++) {
            //取消所有选择
            if(adapter.getList().get(i).isSelect()){
                adapter.getList().get(i).setSelect(false);
                mRcList.notifyItemChanged(i);
            }
        }

        typeId = adapter.getList().get(position).getId();
        typeName = adapter.getList().get(position).getTypeName();

        Log.d(TAG, "onItemClick: "+position+">>"+id);
        adapter.getList().get(position).setSelect(true);
        mRcList.notifyItemChanged(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
