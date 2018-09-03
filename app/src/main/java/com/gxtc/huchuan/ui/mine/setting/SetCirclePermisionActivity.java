package com.gxtc.huchuan.ui.mine.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.bean.ShiledListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.mine.shield.ShieldListActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.GoToActivityIfLoginUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子权限
 * Created by zzg on 2017/7/3.
 */
//privacy
public class SetCirclePermisionActivity extends BaseTitleActivity {

    @BindView(R.id.switch_messsge_status)
    SwitchCompat mSwitchCompat;
    @BindView(R.id.switch_messsge_status1)
    SwitchCompat mSwitchCompat1;

    private String userCode ;
    private String type ;
    PersonInfoBean bean;
    private Subscription setSub;
    private Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_permision);
    }
    public static void startActivity(Context context, String userCode,PersonInfoBean bean) {
        Intent intent = new Intent(context, SetCirclePermisionActivity.class);
        intent.putExtra("userCode", userCode);
        intent.putExtra("PersonInfoBean", bean);
        context.startActivity(intent);
    }
    @Override
    public void initListener() {
        super.initListener();
        userCode = getIntent().getStringExtra("userCode");
        bean=(PersonInfoBean)getIntent().getSerializableExtra("PersonInfoBean");
        getBaseHeadView().showTitle("动态设置");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //不看他动态
        if(bean != null && bean.getUnLook().equals("1") ){
            mSwitchCompat1.setChecked(true);
        }else {
            mSwitchCompat1.setChecked(false);
        }
        //不给他看动态
        if(bean != null && bean.getWithhold().equals("1") ){
            mSwitchCompat.setChecked(true);
        }else {
            mSwitchCompat.setChecked(false);
        }


        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //不给他看动态
                if (isChecked) {
                    setUserPermision("1");
                } else {
                    relieveShield("1");
                }
            }
        });
        mSwitchCompat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //不看他动态
                if (isChecked) {
                    setUserPermision("0");
                } else {
                    relieveShield("0");
                }
            }
        });
    }
    void setUserPermision(String type){

        HashMap hashMap=new HashMap();
        hashMap.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        hashMap.put("userCode",userCode);
        hashMap.put("type",type);//0：不看该用户动态；1：不给该用户看动态；2：黑名单
        setSub= MineApi.getInstance().setUserScren(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Objects>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(SetCirclePermisionActivity.this,"设置成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(SetCirclePermisionActivity.this,message);
                    }
                }));
    }


    /**
     * 解除屏蔽
     */
    private void relieveShield(String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("userCode", userCode);
        map.put("type", type);//0：不看该用户动态；1：不给该用户看动态；2：黑名单

        sub=AllApi.getInstance().deleteByUserCode(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                      ToastUtil.showShort(SetCirclePermisionActivity.this,"解除成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(SetCirclePermisionActivity.this, errorCode,
                                message);
                    }
                }));
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sub != null && sub.isUnsubscribed()){
            sub.unsubscribe();
        }
        if(setSub != null && setSub.isUnsubscribed()){
            setSub.unsubscribe();
        }
    }
}
