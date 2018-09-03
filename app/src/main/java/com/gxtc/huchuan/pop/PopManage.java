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

public class PopManage extends BaseBubblePopup<PopManage> {


    @IntDef({REMOVE, MANAGE, SILENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public static final int REMOVE = 1; //删除
    public static final int MANAGE = 1 << 1;  //黑名单管理
    public static final int SILENT = 1 << 2; //禁言


    private static final String TAG = "PopManage";

    @BindView(R.id.tv_remove) TextView mTvRemove;
    @BindView(R.id.tv_manage) TextView mTvManage;
    @BindView(R.id.tv_silent) TextView mTvSilent;
    private                   Message  mMessage;


    public PopManage(Context context) {
        super(context);
    }

    @Override
    public View onCreateBubbleView() {
        View inflate = View.inflate(mContext, R.layout.simple_pop_manager, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        if ("2".equals(mBean.getRoleType())) {
            if (UserManager.getInstance().getUser().getUserCode().equals(
                    mMessage.getContent().getUserInfo().getUserId())) {
                mTvRemove.setVisibility(View.VISIBLE);
                mTvManage.setVisibility(View.GONE);
                mTvSilent.setVisibility(View.GONE);

            } else {
                mTvRemove.setVisibility(View.GONE);
                mTvManage.setVisibility(View.GONE);
                mTvSilent.setVisibility(View.GONE);
            }
        } else {

            mTvRemove.setVisibility(View.VISIBLE);
            mTvManage.setVisibility(mBean.isSelff() || mBean.getRoleType().equals(ChatInfosBean.ROLE_MANAGER) ? View.VISIBLE : View.GONE);
            mTvSilent.setVisibility(View.VISIBLE);
        }
    }


    private int mPosition;

    public PopManage setPosition(int position) {
        mPosition = position;
        return this;
    }


    public PopManage setData(Message message) {
        mMessage = message;
        return this;
    }


    private ChatInfosBean mBean;

    public PopManage ChatInfosBean(ChatInfosBean bean) {
        mBean = bean;
        return this;
    }


    @OnClick({R.id.tv_remove, R.id.tv_manage, R.id.tv_silent})
    public void onViewClicked(View view) {
        Log.d(TAG, mBean.getId());
        switch (view.getId()) {
            case R.id.tv_remove:
                if (l != null) {
                    l.onAction(mMessage, REMOVE, mPosition);
                }
                break;
            case R.id.tv_manage:
                if (l != null) {
                    l.onAction(mMessage, MANAGE, mPosition);
                }
                break;
            case R.id.tv_silent:
                if (l != null) {
                    l.onAction(mMessage, SILENT, mPosition);
                }
                break;
        }
    }


    public interface PopManageListener {
        public void onAction(Message message, @Status int status, int position);
    }

    private PopManageListener l;

    public PopManage setPopManageListener(PopManageListener l) {
        this.l = l;
        return this;
    }
}
