package com.gxtc.huchuan.utils;

import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 业务类型。0、圈子；1、文章；2、课堂；3、提现；4、作者；5、主播；6、实名；7、交易'
 */
public class CheaekUtil {

    public static volatile CheaekUtil mHelper;
    Subscription subscription ;

    public static CheaekUtil getInstance() {
        if (mHelper == null) {
            synchronized (CheaekUtil.class) {
                if (mHelper == null) {
                    mHelper = new CheaekUtil();
                }
            }
        }
        return mHelper;
    }

    public  CheaekUtil getInfo(String token,String linkId,String linkType,ApiCallBack<CheckBean> callBack) {
        subscription = AllApi.getInstance()
                .getInfo(token, linkId, linkType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<CheckBean>>(callBack));
        return this;
    }

    public  void addTask(Object object){
        RxTaskHelper.getInstance().addTask(object,subscription);
    }

    public  void cancelTask(Object object){
        RxTaskHelper.getInstance().cancelTask(object);
    }
}
