package com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/6/8.
 */

public class HeadImageShoweActivity extends BaseTitleActivity {
    @BindView(R.id.iv_head)     ImageView   mIvHead;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private String coverUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.head_image_zoom);

        coverUrl = getIntent().getStringExtra("coverUrl");
        ImageHelper.loadImage(this, mIvHead, coverUrl);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return true;
    }

    public static void startActivity(Context context, String coverUrl) {
        Intent intent = new Intent(context, HeadImageShoweActivity.class);
        intent.putExtra("coverUrl", coverUrl);
        context.startActivity(intent);
    }
}
