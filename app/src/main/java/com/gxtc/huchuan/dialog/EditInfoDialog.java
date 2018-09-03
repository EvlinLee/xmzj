package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ListDialogAdapterV1;
import com.gxtc.huchuan.data.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.gxtc.huchuan.adapter.IssueDialogAdapter;

/**
 * 来自 zzg on 17/7/10.
 */

public class EditInfoDialog extends Dialog implements View.OnClickListener {

    Context context;
    View    view;
    private ImageView            mHeadPic;
    private EditText             edNiCheng;
    private TextView             btnNext;
    private View.OnClickListener mItemClickListener;
    private ImageView            manPic;
    private ImageView            womanPic;

    public EditInfoDialog(@NonNull Context context) {
        super(context, R.style.dialog_Translucent);
        this.context = context;

        initView();
        initListener();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_info_layout, null);
        mHeadPic = (ImageView) view.findViewById(R.id.head_pic);
        manPic = (ImageView) view.findViewById(R.id.man);
        womanPic = (ImageView) view.findViewById(R.id.woman);
        edNiCheng = (EditText) view.findViewById(R.id.edit_nicheng);
        btnNext = (TextView) view.findViewById(R.id.btn_next);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        mHeadPic.setOnClickListener(this);
        manPic.setOnClickListener(this);
        womanPic.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        view.setLayoutParams(layoutParams);
        setHeadPic();
        setPicSex();
    }


    private void initListener() {
        btnNext.setOnClickListener(this);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public String getNiCheng() {
        return edNiCheng.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onClick(v);
        }
        if (v.getId() == R.id.man) {
            setPicSex();
            manPic.setImageResource(R.drawable.person_icon_man_blue_selected);
        } else if (v.getId() == R.id.woman) {
            setPicSex();
            womanPic.setImageResource(R.drawable.person_icon_female_red_selcted);

        }
    }

    private void setHeadPic() {
        if(!TextUtils.isEmpty( UserManager.getInstance().getHeadPic())){
            ImageHelper.loadHeadIcon(context, mHeadPic, R.drawable.person_icon_head, UserManager.getInstance().getHeadPic());
        }
    }

    private void setPicSex() {
        manPic.setImageResource(R.drawable.person_icon_man_unselect);
        womanPic.setImageResource(R.drawable.person_icon_female_unseltion);
    }

    public void setSex() {
        if("2".equals(UserManager.getInstance().getUser().getSex())){
            setPicSex();
            womanPic.setImageResource(R.drawable.person_icon_female_red_selcted);
        }else if("1".equals(UserManager.getInstance().getUser().getSex())){
            setPicSex();
            manPic.setImageResource(R.drawable.person_icon_man_blue_selected);
        }else {

        }
    }
}
