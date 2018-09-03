package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventRefundBean;
import com.gxtc.huchuan.customemoji.fragment.EmotionMainFragment;
import com.gxtc.huchuan.widget.MultiRadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class PopFileAction extends PopupWindow {

    @BindView(R.id.sys_btn)     TextView btnSys;
    @BindView(R.id.move_btn)   TextView btnMove;
    @BindView(R.id.rename_btn)       TextView btnRename;
    @BindView(R.id.down_btn)   TextView btnDown;
    @BindView(R.id.delete_btn)   TextView btnDelete;
    @BindView(R.id.sys_line)   TextView sysline;
    @BindView(R.id.rename_line)   TextView renameline;
    @BindView(R.id.down_line)   TextView downline;

    Context mContext;
    boolean isFile;
    private View.OnClickListener mClickListener;
    private  View conentView;
    public PopFileAction(Context mContext,boolean isFile) {
        super();
        this.mContext=mContext;
        this.isFile=isFile;
        init(mContext);
        setView();
    }

    private void setView() {
        if(isFile){
            btnMove.setVisibility(View.VISIBLE);
            btnDown.setVisibility(View.VISIBLE);
            btnSys.setVisibility(View.VISIBLE);
            sysline.setVisibility(View.VISIBLE);
            renameline.setVisibility(View.VISIBLE);
            downline.setVisibility(View.VISIBLE);
        }else {
            btnMove.setVisibility(View.GONE);
            btnDown.setVisibility(View.GONE);
            btnSys.setVisibility(View.GONE);
            sysline.setVisibility(View.GONE);
            renameline.setVisibility(View.GONE);
            downline.setVisibility(View.GONE);
        }
    }

    public void init(Context mContext) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_file_action, null);
        this.setContentView(conentView);
        ButterKnife.bind(this, conentView);
        if(isFile){
            this.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.px540dp));
        }else {
            this.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.px300dp));
        }
        this.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.px78dp));
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    @OnClick({R.id.sys_btn, R.id.move_btn, R.id.rename_btn, R.id.down_btn, R.id.delete_btn})
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onClick(v);
        }
        dismiss();
    }

    public void setOnClickListener(View.OnClickListener listener){
        mClickListener=listener;

    }

}
