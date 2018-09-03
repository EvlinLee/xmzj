package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Steven on 17/3/24.
 */

public class WxResponse {

    /**
     * base_resp : {"err_msg":"acct/password error","ret":200023}
     */
    @SerializedName("base_resp")
    private BaseRespBean resp;

    public BaseRespBean getResp() {
        return resp;
    }

    public void setResp(BaseRespBean resp) {
        this.resp = resp;
    }

    public static class BaseRespBean {
        /**
         * err_msg : acct/password error
         * ret : 200023
         */

        private String err_msg;
        private int ret;

        public String getErr_msg() {
            return err_msg;
        }

        public void setErr_msg(String err_msg) {
            this.err_msg = err_msg;
        }

        public int getRet() {
            return ret;
        }

        public void setRet(int ret) {
            this.ret = ret;
        }
    }



}
