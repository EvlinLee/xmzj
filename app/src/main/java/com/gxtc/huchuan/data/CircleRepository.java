package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CircleSignBean;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.RecentBean;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/5.
 */

public class CircleRepository extends BaseRepository implements CircleSource {

    @Override
    public void getFindCircle(String token, int start,String isFee,String orderType, String keyWord,Integer typeId, ApiCallBack<List<CircleBean>> callBack) {
        addSub(CircleApi.getInstance()
                        .getFindCirCle(token, start,isFee,orderType,keyWord,typeId,15)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));
    }

    @Override
    public void getDynamicList(String token, int id, int start, ApiCallBack<List<CircleHomeBean>> callBack) {
        addSub(CircleApi.getInstance()
                        .getCircleDynamic(token,id,start)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleHomeBean>>>(callBack)));
    }

    @Override
    public void issueDynamic(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        addSub(CircleApi.getInstance()
                        .issueDynamic(map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void issueArticle(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        addSub(CircleApi.getInstance()
                        .issueArticle(map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void dianZan(String token, int id, ApiCallBack<ThumbsupVosBean> callBack) {
        addSub(AllApi.getInstance()
                     .support(token,id)
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribeOn(Schedulers.io())
                     .subscribe(new ApiObserver<ApiResponseBean<ThumbsupVosBean>>(callBack)));
    }

    @Override
    public void getArticleType(ApiCallBack<ChannelBean> callBack) {
        addSub(AllApi.getInstance()
                     .getNewsChannels("")
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribeOn(Schedulers.io())
                     .subscribe(new ApiObserver<ApiResponseBean<ChannelBean>>(callBack)));
    }

    @Override
    public void getChartData(int id, ApiCallBack<CircleBean> callBack) {
        String token=UserManager.getInstance().getToken();
        addSub(CircleApi.getInstance()
                     .getChartData(id,token)
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribeOn(Schedulers.io())
                     .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(callBack)));
    }

    @Override
    public void getActiveData(int id, int start, ApiCallBack<List<CircleSignBean>> callBack) {
        /*addSub(CircleApi.getInstance()
                        .getActiveData(id,start)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));*/
        String token=UserManager.getInstance().getToken();
        addSub(CircleApi.getInstance().getPersonByFeeOrFree(id, start, "2", "", System.currentTimeMillis(),token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<ArrayList<CircleSignBean>>>(callBack)));
    }

    @Override
    public void getNoticeData(int id, int start, ApiCallBack<List<CircleBean>> callBack) {
        addSub(CircleApi.getInstance()
                        .getNoticeData(id,start)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));
    }

    //获取打开红包列表
    @Override
    public void getOpenRPList(String id, int start,String loadTime, ApiCallBack<List<RedPacketBean>> callBack) {
        addSub(CircleApi.getInstance().redPacketList(id,start,loadTime)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<RedPacketBean>>>(callBack)));
    }

    @Override
    public void getRedPacketInfo(String token, String redId, ApiCallBack<RedPacketBean> callBack) {
        addSub(CircleApi.getInstance().getRedPacketInfo(token,redId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<RedPacketBean>>(callBack)));
    }

    @Override
    public void receiveRedPacket(String ciphertext, ApiCallBack<RedPacketBean> callBack) {
        addSub(CircleApi.getInstance().receiveRedPacket(ciphertext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<RedPacketBean>>(callBack)));
    }

    @Override
    public void saveGroupRule(HashMap<String,Object> map,ApiCallBack<GroupRuleBean> callBack) {
        addSub(CircleApi.getInstance().saveRole(map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<GroupRuleBean>>(callBack)));
    }

    @Override
    public void getGroupRule(int groupId, ApiCallBack<GroupRuleBean> callBack) {
        addSub(CircleApi.getInstance().getRole(UserManager.getInstance().getToken(),groupId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<GroupRuleBean>>(callBack)));
    }

    @Override
    public void getChatList(String token, int groupId, ApiCallBack<List<CircleBean>> callBack) {
        addSub(CircleApi.getInstance()
                        .getChatList(token,groupId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));
    }

    @Override
    public void getActiveDataList(int start, int groupId,
                                  ApiCallBack<List<CircleBean>> callBack) {
           addSub(CircleApi.getInstance()
                        .getActiveData(groupId,start)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));

    }

    @Override
    public void getChatMemberCount(String chatId, String token, ApiCallBack<CircleBean> callBack) {
        addSub(CircleApi.getInstance()
                        .getChatMemberCount(chatId, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(callBack)));
    }

    @Override
    public void getMemberTypeByChat(String token, String chatId, ApiCallBack<CircleBean> callBack) {
        addSub(CircleApi.getInstance()
                        .getMemberByChat(token, chatId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(callBack)));
    }

    @Override
    public void shutup(String token, int groupId, String userCode, int days, ApiCallBack<Object> callBack) {
        addSub(AllApi.getInstance()
                     .setMeamberNotSay(groupId, token, days, userCode)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<Void>>(callBack)));
    }

    @Override
    public void getListType(ApiCallBack<List<CircleBean>> callBack) {
        addSub(CircleApi.getInstance()
                        .groupType()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleBean>>>(callBack)));
    }

    @Override
    public void verifyInviteCode(String token, String code, ApiCallBack<Object> callBack) {
        addSub(CircleApi.getInstance()
                        .verifyInviteCode(token,code)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void getCircleRecentNewUser(Map<String, String> map, ApiCallBack<List<RecentBean>> callBack) {
        addSub(CircleApi.getInstance()
                        .getRecentNewData(map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ApiObserver<ApiResponseBean<List<RecentBean>>>(callBack)));
    }


}
