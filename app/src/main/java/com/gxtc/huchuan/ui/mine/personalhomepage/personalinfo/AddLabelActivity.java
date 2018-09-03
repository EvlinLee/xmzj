package com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo;

import android.os.Bundle;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;

/**
 * Describe:
 * Created by ALing on 2017/5/31 .
 */

public class AddLabelActivity extends BaseTitleActivity{
    private String[] mVals = new String[]
            {"三生三世", "onpiece", "Weclome Hi ", "Button", "TextView", "Hello",
                    "Android", "Weclome", "十里桃花"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);

        initFlowLayout();
    }

    private void initFlowLayout() {
       /* LayoutInflater mInflater = LayoutInflater.from(this);
        for (int i = 0; i < bean.size(); i++) {
            final TextView tv = (TextView) mInflater.inflate(R.layout.flowlayout_textview,
                    mFlowHistory, false);
            tv.setText(bean.get(i).getSearchContent());
            mFlowHistory.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = tv.getText().toString();
                    mEtInputSearch.setText(s);
//                    search(s);
                    Log.d("tag", "onClick: " + s);
                }
            });
        }*/
    }

}
