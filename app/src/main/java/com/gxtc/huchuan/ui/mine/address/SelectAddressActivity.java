package com.gxtc.huchuan.ui.mine.address;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SelectAddressAdapter;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.event.EventUpdataAddrBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择地址页面
 */
public class SelectAddressActivity extends BaseTitleActivity implements View.OnClickListener,BaseUserView {


    @BindView(R.id.ll_add_address)          RelativeLayout      btnAdd;
    @BindView(R.id.rc_address_list)         RecyclerView        listView;

    private DealSource mData;

    private SelectAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_select_address));
        getBaseHeadView().showBackButton(this);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        listView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));
    }

    @OnClick({R.id.ll_add_address})
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_add_address:
                Intent addIntent = new Intent(this,AddAddressActivity.class);
                startActivityForResult(addIntent,101);
                break;

            case R.id.headBackButton:
                goBack();
                break;
        }
    }

    @Override
    public void initData() {
        mData = new DealRepository();
        List<AddressBean> datas = (List<AddressBean>) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        if(datas != null ){
            adapter = new SelectAddressAdapter(this,datas,R.layout.item_address_list);
            listView.setAdapter(adapter);
            adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    changeSelectFlag(position);
                    goBack();
                }
            });

            adapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
                @Override
                public void onItemLongClick(View v, int position) {
                    showDeleteDialog(adapter.getList().get(position));
                }
            });
        }
    }

    //删除地址dialog
    AlertDialog dialog;
    private void showDeleteDialog(final AddressBean addressBean) {
        dialog = DialogUtil.createDialog(this,
            "删除地址", "确定要删除地址？", "取消", "确定",
            new DialogUtil.DialogClickListener() {
                @Override
                public void clickLeftButton(View view) {
                    dialog.dismiss();
                    dialog = null;
                }

                @Override
                public void clickRightButton(View view) {
                    deletAddress(addressBean);
                    dialog.dismiss();
                    dialog = null;
                }
            });
        dialog.show();
    }


    //请求网络删除地址
    private void deletAddress(final AddressBean addressBean){
        showLoad();
        String token = UserManager.getInstance().getToken();
        mData.deletAddress(token, addressBean.getId(), new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                showLoadFinish();
                List<AddressBean> datas = adapter.getList();
                for (int i = 0; i < datas.size(); i++) {
                    if(addressBean.getId().equals(datas.get(i).getId())){
                        adapter.getList().remove(i);
                        adapter.notifyItemRemoved(i);
                    }
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(SelectAddressActivity.this,errorCode,message);
            }
        });




    }


    //修改原来的默认标志
    private void changeDefult(AddressBean bean){
        if("1".equals(bean.getIsdefault())){
            for (AddressBean temp : adapter.getList()){
                temp.setIsdefault("0");
                temp.setSelect(false);
            }
            bean.setSelect(true);
            adapter.notifyDataSetChanged();
        }
    }

    //修改选择标志
    private void changeSelectFlag(int index){
        if(index < adapter.getList().size()){
            for (int i = 0; i < adapter.getList().size(); i++) {
                adapter.getList().get(i).setSelect(false);
            }

            adapter.getList().get(index).setSelect(true);
        }
    }

    /**
     * 修改地址回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenUpdataAddress(EventUpdataAddrBean event){
        AddressBean tag = event.bean;
        for (int i = 0; i < adapter.getList().size(); i++) {
            AddressBean temp = adapter.getList().get(i);
            if(tag.getId().equals(temp.getId())){

                if("1".equals(tag.getIsdefault())){
                    for (AddressBean temp1 : adapter.getList()){
                        temp1.setIsdefault("0");
                    }
                    adapter.notifyDataSetChanged();
                }

                adapter.getList().get(i).setId(tag.getId());
                adapter.getList().get(i).setName(tag.getName());
                adapter.getList().get(i).setPhone(tag.getPhone());
                adapter.getList().get(i).setProvince(tag.getProvince());
                adapter.getList().get(i).setCity(tag.getCity());
                adapter.getList().get(i).setArea(tag.getArea());
                adapter.getList().get(i).setAddress(tag.getAddress());
                adapter.getList().get(i).setIsdefault(tag.getIsdefault());
                adapter.notifyItemChanged(i);

            }

        }
    }

    private void goBack(){
        ArrayList<AddressBean> list = new ArrayList<AddressBean>();
        list.addAll(adapter.getList());
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_DATA,list);
        setResult(Constant.ResponseCode.SELECT_ADDRESS,intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            //添加新的收货地址
            case Constant.ResponseCode.ADD_ADDRESS:
                if(data != null){
                    AddressBean bean = (AddressBean) data.getSerializableExtra(Constant.INTENT_DATA);
                    if(bean != null){
                        changeDefult(bean);
                        adapter.addData(bean);
                    }

                }
                break;
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            goBack();
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }


    @Override
    public void showNetError() {
        ToastUtil.showShort(this,getString(R.string.empty_net_error));
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showEmpty() {}

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showReLoad() {}

    @Override
    public void setPresenter(Object presenter) {}

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this,LoginAndRegisteActivity.class);
    }
}
