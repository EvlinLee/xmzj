package com.luck.picture.lib.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.dialog.GraffitiTextDialog;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.utils.FileUtils;
import com.luck.picture.lib.utils.Utils;
import com.luck.picture.lib.widget.ColorDotView;

import java.io.IOException;
import java.util.ArrayList;

import cn.forward.androids.utils.ImageUtils;
import cn.hzw.graffiti.GraffitiListener;
import cn.hzw.graffiti.GraffitiSelectableItem;
import cn.hzw.graffiti.GraffitiText;
import cn.hzw.graffiti.GraffitiView;

/**
 * 图片编辑页面
 */
public class PictureEditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 998;

    private int TAB_PEN = 1;
    private int TAB_TEXT = 2;
    private int TAB_MOSAIC = 3;

    private GraffitiView mGraffitiView;
    private ImageView imgPen;
    private ImageView imgText;
    private ImageView imgMosaic;
    private View layoutColorTools;

    private int mColor = 0;
    private boolean isSave = false;

    private ArrayList<ColorDotView> colors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);

        initView();
        initListener();
    }

    private void initView() {
        imgPen = (ImageView) findViewById(R.id.img_pen);
        imgText = (ImageView) findViewById(R.id.img_text);
        imgMosaic = (ImageView) findViewById(R.id.img_mosaic);
        ImageView imgUndo = (ImageView) findViewById(R.id.img_undo);
        TextView  tvCancel = (TextView) findViewById(R.id.btn_cancel);
        TextView  tvFinish = (TextView) findViewById(R.id.btn_finish);
        layoutColorTools = findViewById(R.id.layout_color_tools);
        ViewGroup layoutContent = (ViewGroup) findViewById(R.id.layout_content);

        imgPen.setOnClickListener(this);
        imgText.setOnClickListener(this);
        imgMosaic.setOnClickListener(this);
        imgUndo.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvFinish.setOnClickListener(this);

        colors = new ArrayList<>();
        for(int i = 0; i < 8; i++ ){
            int id = getResources().getIdentifier("color_dot" + i, "id", getPackageName());
            ColorDotView color = (ColorDotView) findViewById(id);
            colors.add(color);
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(ColorDotView dotView: colors){
                        dotView.setSelected(false);
                    }
                    v.setSelected(true);
                    mColor = ((ColorDotView)v).getColor();
                    mGraffitiView.setColor(mColor);
                }
            });
        }
        mColor = colors.get(2).getColor();
        colors.get(2).setSelected(true);
        String path = getIntent().getStringExtra("data");
        Bitmap bitmap = ImageUtils.createBitmapFromPath(path, this);
        mGraffitiView = new GraffitiView(this, bitmap, mGraffitiListener);
        mGraffitiView.setColor(mColor);

        layoutContent.addView(mGraffitiView);
    }

    private void initListener() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.img_pen){
            changeTab(TAB_PEN);
            tabToolsIn(layoutColorTools, true);
            mGraffitiView.setPen(GraffitiView.Pen.HAND);

        } else if(id == R.id.img_text){
            changeTab(TAB_TEXT);
            tabToolsIn(layoutColorTools, true);
            mGraffitiView.setPen(GraffitiView.Pen.TEXT);

        } else if(id == R.id.img_mosaic){
            changeTab(TAB_MOSAIC);
            tabToolsIn(layoutColorTools, false);
            mGraffitiView.setPen(GraffitiView.Pen.MOSAIC);

        } else if(id == R.id.img_undo){
            mGraffitiView.undo();

        } else if(id == R.id.btn_cancel){
            finish();

        } else if(id == R.id.btn_finish){
            mGraffitiView.save();
        }
    }


    //切换画笔工具栏的选中图案
    private void changeTab(int index){
        imgPen.setImageResource(index == TAB_PEN ? R.drawable.compile_icon_bi_selected : R.drawable.compile_icon_bi);
        imgText.setImageResource(index == TAB_TEXT ? R.drawable.compile_icon_wenzi_selected : R.drawable.compile_icon_wenzi);
        imgMosaic.setImageResource(index == TAB_MOSAIC ? R.drawable.compile_icon_masaike_selected : R.drawable.compile_icon_masaike);
    }


    //底部工具栏的切换动画 ， true为入场动画  false 是出场动画
    private void tabToolsIn(final View targetView, final boolean isIn){
        if(isIn && targetView.getVisibility() == View.VISIBLE){
            return;
        }

        if(!isIn && targetView.getVisibility() == View.INVISIBLE){
            return;
        }

        float [] arrays = new float[2];
        if(isIn){
            arrays[0] = 0f;
            arrays[1] = 1f;
        }else{
            arrays[0] = 1f;
            arrays[1] = 0f;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(targetView, "alpha", arrays);
        anim.setDuration(150);
        anim.addListener(new Animator.AnimatorListener() {
             @Override
             public void onAnimationStart(Animator animation) {
                 targetView.setVisibility(View.VISIBLE);
             }

             @Override
             public void onAnimationEnd(Animator animation) {
                 if(!isIn){
                     targetView.setVisibility(View.INVISIBLE);
                 }
             }

             @Override
             public void onAnimationCancel(Animator animation) {}

             @Override
             public void onAnimationRepeat(Animator animation) {}
        });
        anim.start();
    }


    //保存编辑好的图片到手机
    private void saveToDevice(Bitmap bitmap){
        String savePath = FileUtils.createCameraFile(this,
                FunctionConfig.TYPE_IMAGE).getAbsolutePath();
        try {
            Utils.saveImageToSD(this, savePath, bitmap, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra("data", savePath);
        setResult(RESULT_OK, intent);
        finish();
    }


    private GraffitiListener mGraffitiListener = new GraffitiListener() {
        @Override
        public void onSaved(Bitmap bitmap, Bitmap bitmapEraser) {
            saveToDevice(bitmap);
        }

        @Override
        public void onError(int i, String msg) {

        }

        @Override
        public void onReady() {
            mGraffitiView.setPaintSize(10f);
        }

        @Override
        public void onSelectedItem(GraffitiSelectableItem selectableItem, boolean selected) {

        }

        @Override
        public void onCreateSelectableItem(GraffitiView.Pen pen, float x, float y) {
            if(pen == GraffitiView.Pen.TEXT){
                showCreateTextDialog(null, x, y);
            }
        }
    };


    //显示添加文字弹窗
    private void showCreateTextDialog(final GraffitiSelectableItem selectableItem, final Float x, final Float y) {
        GraffitiTextDialog graffitiTextDialog = new GraffitiTextDialog();
        graffitiTextDialog.setOnTextListener(new GraffitiTextDialog.OnTextListener() {
            @Override
            public void textFinish(String text) {
                addGraffitiText(selectableItem ,text, x, y);
            }
        });
        graffitiTextDialog.setText(selectableItem == null ? "" : ((GraffitiText)selectableItem).getText());
        graffitiTextDialog.setColor(mColor);
        graffitiTextDialog.show(getSupportFragmentManager(), GraffitiTextDialog.class.getSimpleName());
    }


    private void addGraffitiText(GraffitiSelectableItem selectableItem, String text, Float x, Float y){
        float textSize = getResources().getDimension(R.dimen.textSize_s);
        if(selectableItem == null){
            GraffitiText graffitiText = new GraffitiText(mGraffitiView.getPen(), text, textSize, mGraffitiView.getColor().copy(), 0,
                    mGraffitiView.getGraffitiRotateDegree(), x, y, mGraffitiView.getOriginalPivotX(), mGraffitiView.getOriginalPivotY());
            mGraffitiView.addSelectableItem(graffitiText);
        }else{
            GraffitiText graffitiText = (GraffitiText) selectableItem;
            graffitiText.setText(text);
            mGraffitiView.invalidate();
        }
    }


    public static void startActivit(Activity activity, String path){
        Intent intent = new Intent(activity, PictureEditActivity.class);
        intent.putExtra("data", path);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }


}
