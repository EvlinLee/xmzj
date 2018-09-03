package com.gxtc.huchuan.http;

import com.gxtc.commlibrary.utils.NetworkUtil;
import com.gxtc.huchuan.MyApplication;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observer;

/**
 * Created by Steven on 17/1/18.
 */

public class ApiObserver<T> implements Observer<T>{


    public static final String NET_ERROR = "400";
    public static final String RESOLVE_ERROR = "401";
    public static final String SERVER_ERROR = "500";
    public static final String UNKONW_SERVER_ERROR = "302";
    public static final String SERVER_TIME_OUT = "303";
    public static final String APP_EXCEPTION = "110";

    public static int responseCode = -1;

    private ApiCallBack callBack;

    public ApiObserver(ApiCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onCompleted() {
        callBack.onCompleted();
    }

    @Override
    public void onNext(T t) {
        if(t instanceof ApiResponseBean){
            ApiResponseBean apiResponseBean = (ApiResponseBean) t;

            if (!"00000".equals(apiResponseBean.getErrorCode())) {
                responseCode = -1;
                callBack.onError(apiResponseBean.getErrorCode(), apiResponseBean.getMessage());
                return;
            }
            responseCode = 0;
            callBack.onSuccess(apiResponseBean.getResult());
            return;
        }

        callBack.onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if(e instanceof UnknownHostException){
            callBack.onError(UNKONW_SERVER_ERROR,"无法连接服务器,服务器繁忙");
            return;
        }

        if(e instanceof ConnectException){
            callBack.onError(UNKONW_SERVER_ERROR,"无法连接服务器,请检查手机网络");
            return;
        }

        //连接网络超时
        if(e instanceof SocketTimeoutException || e instanceof SocketException){
            callBack.onError(SERVER_TIME_OUT,"连接服务器超时,请检查手机网络");

        //有网情况服务器错误
        }else if(NetworkUtil.isConnected(MyApplication.getInstance()) && responseCode == -1){
            callBack.onError(SERVER_ERROR, "服务器繁忙");

        //有网情况服务器错误
        } else if(e instanceof HttpException){
            callBack.onError(SERVER_ERROR, "服务器繁忙");

        //有网络情况，返回响应正常，内部app异常
        } else if(NetworkUtil.isConnected(MyApplication.getInstance()) && responseCode == 0){
            callBack.onError(APP_EXCEPTION,"app出错了~~");

        } else{
            callBack.onError(NET_ERROR,"无法连接服务器,请检查手机网络");

        }

    }


}
