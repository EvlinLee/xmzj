package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse;

import android.widget.LinearLayout;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;

/**
 * Created by Steven on 17/2/28.
 */

public interface TextContract {

    interface SingleView extends BaseUserView<TextContract.SinglePresenter> {

    }

    interface SinglePresenter extends BasePresenter {
        void changeTab(LinearLayout layout,int index);
    }


    interface AllView extends BaseUserView<TextContract.AllPresenter> {

    }


    interface AllPresenter extends BasePresenter {

    }


    interface Source extends BaseSource {

    }


}
