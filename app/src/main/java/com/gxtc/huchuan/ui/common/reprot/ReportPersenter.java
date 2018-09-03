package com.gxtc.huchuan.ui.common.reprot;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/4/7.
 */

public class ReportPersenter extends BaseRepository implements ReportContract.Presenter,
        ReportContract.Source {
    private ReportContract.View mView;

    public ReportPersenter(ReportContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void report(String content, String type, String id) {
        mView.showLoad();
        report(content, type, id, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showReportResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        super.destroy();
        mView = null;
    }

    @Override
    public void report(String content, String type, String id, ApiCallBack<Void> callBack) {
        addSub(AllApi.getInstance().complaintArticle(UserManager.getInstance().getToken(), type,
                id, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Void>>(callBack)));

    }
}
