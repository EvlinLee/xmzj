package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.WxResponse;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by Steven on 17/3/24.
 */

public interface WxSource extends BaseSource {

    void login(HashMap<String,String> map, ApiCallBack<WxResponse> callBack);
}
