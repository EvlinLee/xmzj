package com.gxtc.huchuan.http;


import com.gxtc.huchuan.bean.CircleHomeBean;

import java.util.List;

public abstract class ApiCallBack<T>{

    public void onCompleted(){}

    public abstract void onSuccess(T data);

    public abstract void onError(String errorCode, String message);
}
