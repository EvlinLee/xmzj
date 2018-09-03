package com.gxtc.huchuan.ui.live.series;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.huchuan.R;

import butterknife.BindView;


/**
 * Created by Gubr on 2017/3/2.
 * 课堂简介
 */

public class SeriesIntroFragment extends BaseTitleFragment {

    @BindView(R.id.tv_host_page_intro_content)           TextView mTvHostPageIntroContent;
    @BindView(R.id.tv_host_page_intro_attestation_time)  TextView mTvHostPageIntroAttestationTime;
    @BindView(R.id.tv_host_page_intro_attestation_intro) TextView mTvHostPageIntroAttestationIntro;

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
        String introduce = bundle.getString("introduce");
        mTvHostPageIntroContent.setText(introduce);         //课堂简介

        mTvHostPageIntroAttestationTime.setText("完成新媒之家真播认证");//认证时间

        String s = "新媒之家课堂认证是新媒之家官方对于课堂创建者身份和资质的真实性和合法性进行甄别和核实的" + "过程。新媒之家为创建者提价在线分享的工具和平台。";
        mTvHostPageIntroAttestationIntro.setText(s);
    }
}
