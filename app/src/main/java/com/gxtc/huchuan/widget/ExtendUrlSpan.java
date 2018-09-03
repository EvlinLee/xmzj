package com.gxtc.huchuan.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Toast;


import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;

/**
 * Created by 宋家任 on 2017/6/11.
 * 外链跳转app内部
 */
@SuppressLint("ParcelCreator")
public class ExtendUrlSpan extends URLSpan {
    public ExtendUrlSpan(String url) {
        super(url);
    }

    public ExtendUrlSpan(Parcel src) {
        super(src);
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(widget.getContext(), CommonWebViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("web_url", getURL());
        intent.putExtra("web_title", "");
        widget.getContext().startActivity(intent);
    }

}



