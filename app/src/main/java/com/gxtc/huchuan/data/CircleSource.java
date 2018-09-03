package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CircleSignBean;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.RecentBean;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface CircleSource extends BaseSource {

    void getFindCircle(String token, int start,String isFee,String orderType, String keyWord,Integer typeId,ApiCallBack<List<CircleBean>> callBack);

    void getDynamicList(String token, int id, int start, ApiCallBack<List<CircleHomeBean>> callBack);

    void issueDynamic(HashMap<String,String> map, ApiCallBack<Object> callBack);

    void issueArticle(HashMap<String,String> map, ApiCallBack<Object> callBack);

    void dianZan(String token, int id, ApiCallBack<ThumbsupVosBean> callBack);

    void getArticleType(ApiCallBack<ChannelBean> callBack);

    void getChartData(int id,ApiCallBack<CircleBean> callBack);

    void getActiveData(int id, int start, ApiCallBack<List<CircleSignBean>> callBack);

    void getNoticeData(int id, int start, ApiCallBack<List<CircleBean>> callBack);

    void getOpenRPList(String id, int start, String loadTime, ApiCallBack<List<RedPacketBean>> callBack);

    void getRedPacketInfo(String token, String redId, ApiCallBack<RedPacketBean> callBack);

    void receiveRedPacket(String ciphertext, ApiCallBack<RedPacketBean> callBack);

    void saveGroupRule(HashMap<String,Object> map,ApiCallBack<GroupRuleBean> callBack);

    void getGroupRule(int groupId,ApiCallBack<GroupRuleBean> callBack);

    void getChatList(String token ,int groupId, ApiCallBack<List<CircleBean>> callBack);

    void getActiveDataList(int start ,int groupId, ApiCallBack<List<CircleBean>> callBack);

    void getChatMemberCount(String chatId, String token, ApiCallBack<CircleBean> callBack);

    void getMemberTypeByChat(String token, String chatId, ApiCallBack<CircleBean> callBack);

    void shutup(String token, int groupId, String userCode, int days, ApiCallBack<Object> callBack);

    void getListType(ApiCallBack<List<CircleBean>> callBack);

    void verifyInviteCode(String token, String code, ApiCallBack<Object> callBack);

    void getCircleRecentNewUser(Map<String, String> map, ApiCallBack<List<RecentBean>> callBack);
}
