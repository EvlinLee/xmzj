package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.dao.ChatPush;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/3/15.
 * <p>
 * <p>
 * 聊天-推送直播-推送弹窗
 */

public class PopPush extends BasePopupWindow {
    private final static long EXCEEDTIME = 1000 * 60 * 60 * 4;//超时


    private final String countStr="还剩<font color=\"#ec6b46\">%d次</font>推送机会";


    @BindView(R.id.push_time)
    TextView mPushTime;
    @BindView(R.id.content)
    TextView mContent;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_submit)
    Button mBtnSubmit;
    private ChatInfosBean mBean;
    private ChatPush mChatPush;

    private boolean isCanPush;






    private int count = 2;

    public PopPush(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.btn_cancel, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                closePop();
                break;
            case R.id.btn_submit:
               if (isCanPush){
                   push();
               }else{
                   ToastUtil.showShort(getActivity(),"要四个小时后才能再推送");
               }

                push();
                break;
        }
    }

    private void push() {
            pushsuccessful();
            closePop();
    }

    private void pushsuccessful() {
        if (mChatPush == null) return;
        mChatPush.setId(Long.valueOf(mBean.getId()));
        mChatPush.setDate("" + System.currentTimeMillis());
        mChatPush.setCount(mChatPush.getCount() + 1);
        GreenDaoHelper.getInstance().getSeeion().getChatPushDao().insertOrReplace(mChatPush);
    }

    @Override
    public void showPopOnRootView(Activity activity) {
        checkCount();
        super.showPopOnRootView(activity);
    }

    public void setData(ChatInfosBean bean) {
        mBean = bean;
    }

    private void checkCount() {
        mChatPush = GreenDaoHelper.getInstance().getSeeion().getChatPushDao().load(Long.valueOf(mBean.getId()));
//        如果天数不一样  就会把次数清零
        if (mChatPush == null || !DateUtil.getCurTime("yyyy-MM-dd").equals(DateUtil.formatDate(mChatPush.getDate(), "yyyy-MM-dd"))) {
            mChatPush = new ChatPush(Long.valueOf(mBean.getId()), "" + 0, 0);
        }

        long time = System.currentTimeMillis() - Long.valueOf(mChatPush.getDate());

        //判断时间是否已经超过4小时了。
        if (time >= EXCEEDTIME) {
            count = 2 - mChatPush.getCount();
            if (count == 0) {
                isCanPush = false;
            } else {
                isCanPush = true;
            }
        } else {
            isCanPush = false;
        }

        setCount(mPushTime,count);


    }

    private void setCount(TextView textView,int count){

        textView.setText(Html.fromHtml(String.format(countStr,count)));
    }






}
