package com.gxtc.huchuan.ui.live.hostpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.MineCircleBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Gubr on 2017/3/2.
 * 课堂简介
 */

public class LiveIntroFragment extends BaseTitleFragment {
    @BindView(R.id.tv_host_page_intro_content)           TextView mTvHostPageIntroContent;
    @BindView(R.id.tv_host_page_intro_attestation_time)  TextView mTvHostPageIntroAttestationTime;
    @BindView(R.id.tv_host_page_intro_attestation_intro) TextView mTvHostPageIntroAttestationIntro;
    @BindView(R.id.edit) TextView edit;
    private LiveRoomBean bean;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_host_page_intro, container, false);
        return view;
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        bean = (LiveRoomBean) bundle.getSerializable("bean");
        String       introduce = bundle.getString("introduce");
        mTvHostPageIntroContent.setText(introduce);//课堂简介
//        if(bean.bIsSelf()){
//            edit.setVisibility(View.VISIBLE);
//        }else {
//            edit.setVisibility(View.GONE);
//        }
        String           createTime = bundle.getString("create_time");
        long             time       = Long.valueOf(createTime);
        Date             date       = new Date(time);
        SimpleDateFormat format     = new SimpleDateFormat("yyyy年MM月dd日");
        String           str        = format.format(date);
        mTvHostPageIntroAttestationTime.setText("认证信息：已认证");//认证时间
        String s = "新媒之家课堂认证是新媒之家官方对于课堂创建者身份和资质的真实性和合法性进行甄别和核实的" + "过程。新媒之家为创建者提价在线分享的工具和平台。";
        mTvHostPageIntroAttestationIntro.setText(s);
    }

    @OnClick(R.id.edit)
    public void OnClick(View v){
        EditLiveHostIntroduceActivity.jumpToEditLiveHostIntroduceActivity(getActivity(),bean);
    }

    public void setIntroduce(String introduce) {
        if (mTvHostPageIntroContent != null) {
            mTvHostPageIntroContent.setText(introduce);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if(data != null){
                String introduce = data.getStringExtra("data");
                mTvHostPageIntroContent.setText(introduce);//课堂简介
            }
        }
    }
}
