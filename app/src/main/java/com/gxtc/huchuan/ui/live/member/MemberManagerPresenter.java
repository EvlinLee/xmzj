package com.gxtc.huchuan.ui.live.member;

import android.util.Log;

import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/4/2.
 */

public class MemberManagerPresenter implements MemberManagerContract.Presenter {


    private MemberManagerContract.View view;
    private MemberManagerSource memberManagerSource;

    public MemberManagerPresenter(MemberManagerContract.View view) {
        this.view = view;
        memberManagerSource = new MemberManagerSource();
        view.setPresenter(this);
    }


    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        memberManagerSource.destroy();
        view = null;
    }

    private long loadTime;
    @Override
    public void getDatas(String chatinfoid, String count,String searchKey, String type) {

        view.showLoad();
        if(count.equals("0")){
            loadTime = System.currentTimeMillis();
        }
        HashMap<String, String> map = new HashMap<>();
        if (UserManager.getInstance().isLogin()) {
            map.put("token",UserManager.getInstance().getToken());
        }

        map.put("userType","1");
        map.put("type",type);
        map.put("chatId",chatinfoid);
        map.put("start",count);
        if(searchKey == null)
            searchKey = "";
        if(!searchKey.equals(""))
            map.put("searchKey",searchKey);
        else
            map.put("loadTime",loadTime + "");

        memberManagerSource.getDatas(map, new ApiCallBack<ArrayList<ChatJoinBean.MemberBean>>() {
            @Override
            public void onSuccess(ArrayList<ChatJoinBean.MemberBean> data) {
                if(view == null)   return;
                view.showLoadFinish();
                view.showDatas(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(view == null)   return;
                view.showLoadFinish();
                view.showError(message);
            }
        });
    }

    @Override
    public void Search(String chatinfoid, String count, String searchKey) {
//        view.showLoad();
//        HashMap<String, String> map = new HashMap<>();
//        if (UserManager.getInstance().isLogin()) {
//            map.put("token",UserManager.getInstance().getToken());
//        }
//        map.put("chatInfoId",chatinfoid);
//        map.put("start",count);
//        if (searchKey!=null){
//            map.put("name",searchKey);
//        }
//        memberManagerSource.getDatas(map, new ApiCallBack<List<SignUpMemberBean>>() {
//            @Override
//            public void onSuccess(List<SignUpMemberBean> data) {
//                if(view == null)   return;
//                view.showDatas(data);
//                view.showLoadFinish();
//            }
//
//            @Override
//            public void onError(String errorCode, String message) {
//                if(view == null)   return;
//                view.showLoadFinish();
//                view.showError(message);
//            }
//        });
    }
}
