package com.gxtc.huchuan.widget;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gxtc.huchuan.R;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/17.
 */

public class EditMenuView {

    private View rootView;

    private View layoutText;
    private View layoutHl;

    private ImageButton btnFont;

    public EditMenuView(View menuView) {
        this.rootView = menuView;

        layoutText = rootView.findViewById(R.id.layout_text);
        layoutHl = rootView.findViewById(R.id.hl);

        btnFont = (ImageButton) layoutHl.findViewById(R.id.btn_font);
    }

    public void switchFontPanel(){
        if (layoutText.getVisibility() == View.GONE) {
            btnFont.setImageResource(R.drawable.circle_compile_icon_font_selected);
            layoutText.setVisibility(View.VISIBLE);
        } else {
            btnFont.setImageResource(R.drawable.circle_compile_icon_font);
            layoutText.setVisibility(View.GONE);
        }
    }

    public void hideMenu(){
        layoutText.setVisibility(View.GONE);
        layoutHl.setVisibility(View.GONE);
    }

    public View getLayoutHl(){
        return layoutHl;
    }

}
