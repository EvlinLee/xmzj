package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventIntroBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Describe:新建系列课 》 简介
 * Created by ALing on 2017/3/15 .
 */

public class SeriesCourseIntroduceActivity extends BaseTitleActivity implements View.OnClickListener {
    @BindView(R.id.et_series_intro)
    EditText mEtSeriesIntro;
    private String mEditIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_course_introduce);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_create_series_intro));
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton(getString(R.string.label_save), this);
    }

    @Override
    public void initData() {
        super.initData();
        mEditIntro = getIntent().getStringExtra("editIntro");
        if (mEditIntro != null){
            mEtSeriesIntro.setText(mEditIntro);
        }
    }

    @Override
   public void onClick(View v) {
        WindowUtil.closeInputMethod(this);
       switch (v.getId()) {
           //返回
           case R.id.headBackButton:
               finish();
               break;
           //保存
           case R.id.headRightButton:
               save();
               break;
       }
   }

    private void save() {
        if (TextUtils.isEmpty(mEtSeriesIntro.getText().toString())){
            ToastUtil.showShort(this,getString(R.string.toast_empty_series_intro));
            return;
        }else {
            /*HashMap<String,String> map = new HashMap<>();
            map.put("series_intro",mEtSeriesIntro.getText().toString());
            finish();
            GotoUtil.goToActivityWithData(this,CreateSeriesCourseActivity.class,map);*/
            EventBusUtil.post(new EventIntroBean(mEtSeriesIntro.getText().toString()));
            finish();
        }

    }

}
