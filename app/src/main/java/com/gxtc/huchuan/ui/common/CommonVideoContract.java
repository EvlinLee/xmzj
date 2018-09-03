package com.gxtc.huchuan.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/9.
 */
public interface CommonVideoContract {

    interface View{
        void setVideoPrsenter(CommonVideoContract.Presenter prsenter);
    }

    interface Presenter extends BasePresenter{
        void saveVideo(Context context);

        void shareVideoToFriends(Activity activity);

        void collectVideo(Activity activity);
    }

}
