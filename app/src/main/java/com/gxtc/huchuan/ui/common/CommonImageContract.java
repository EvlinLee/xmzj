package com.gxtc.huchuan.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.data.BaseSource;

import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/7.
 */

public interface CommonImageContract extends BaseSource{

    interface View extends BaseUiView<Presenter> {
        void showShareResult(Uri source);

        void showCollectResult(String errorMsg);

        void showSaveResult(Uri file);
    }

    interface Presenter extends BasePresenter {
        void shareImageToConversation(BaseTitleActivity activity, int requestCode, Uri uri);

        void collectImage(Uri uri);

        void saveImage(Uri uri);

        void editImage(BaseTitleActivity activity, int requestCode, Uri uri);

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

}
