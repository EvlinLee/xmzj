package com.gxtc.huchuan.ui.live.member;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/4/3.
 */

public class MemberManagerSource extends BaseRepository implements MemberManagerContract.Source {


    @Override
    public void getDatas(HashMap<String,String> map,ApiCallBack<ArrayList<ChatJoinBean.MemberBean>> callBack) {
//        ArrayList<SignUpMemberBean> beans = new ArrayList<SignUpMemberBean>() {{
//            for (int i = 0; i < 20; i++) {
//                SignUpMemberBean signUpMemberBean = new SignUpMemberBean();
//                signUpMemberBean.setName("名字i" + i);
//                signUpMemberBean.setUserCode("sldkfj");
//                signUpMemberBean.setHeadPic("https://gss0.baidu.com/9vo3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/8ad4b31c8701a18bbef9f231982f07082838feba.jpg");
//                signUpMemberBean.setIsBlack("1");
//                signUpMemberBean.setIsBlock("1");
//                add(signUpMemberBean);
//            }
//        }};
//        callBack.onSuccess(beans);
        addSub(LiveApi.getInstance().getlistJoinMember(map).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new
                ApiObserver<ApiResponseBean<ArrayList<ChatJoinBean.MemberBean>>>(callBack)));
    }
}
