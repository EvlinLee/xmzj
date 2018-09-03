package com.gxtc.huchuan.pop;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.widget.base.BaseDialog;
import com.flyco.dialog.widget.popup.base.BaseBubblePopup;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DiscussAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.event.EventPopBubleBean;
import com.gxtc.huchuan.data.UserManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.model.Message;

/**
 * Describe:讨论-管理
 */

public class PopRemoveMessge extends BaseBubblePopup<PopRemoveMessge> {


    @IntDef({REMOVE, MANAGE, SILENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public static final int REMOVE = 1; //删除
    public static final int MANAGE = 1 << 1;  //黑名单管理
    public static final int SILENT = 1 << 2; //禁言


    private static final String TAG = "PopManage";

    @BindView(R.id.tv_remove) TextView mTvRemove;
//    @BindView(R.id.tv_manage) TextView mTvManage;
//    @BindView(R.id.tv_silent) TextView mTvSilent;
    private                   Message  mMessage;


    public PopRemoveMessge(Context context) {
        super(context);
    }

    @Override
    public View onCreateBubbleView() {
        View inflate = View.inflate(mContext, R.layout.pop_remove_message, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
    }


    private int mPosition=-1;

    public PopRemoveMessge setPosition(int position) {
        mPosition = position;
        return this;
    }


    public PopRemoveMessge setData(Message message) {
        mMessage = message;
        return this;
    }


    private ChatInfosBean mBean;

    public PopRemoveMessge ChatInfosBean(ChatInfosBean bean) {
        mBean = bean;
        Log.d(TAG, "bean:" + bean);
        return this;
    }


    @OnClick({R.id.tv_remove})
    public void onViewClicked(View view) {
        Log.d(TAG, mBean.getId());
        switch (view.getId()) {
            case R.id.tv_remove:
                if (l != null) {
                    l.onAction(mMessage, REMOVE, mPosition);
                }
                break;
        }
    }


    public interface PopManageListener {
        public void onAction(Message message, @Status int status, int position);
    }

    private PopManageListener l;

    public PopRemoveMessge setPopManageListener(PopManageListener l) {
        this.l = l;
        return this;
    }
}
