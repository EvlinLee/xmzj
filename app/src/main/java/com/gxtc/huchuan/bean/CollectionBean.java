package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.huchuan.data.SpecialBean;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by Gubr on 2017/3/31.
 */
public class CollectionBean implements Serializable {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SerializedName("id")

    private String id;
    @SerializedName("type")
    private String type;

    @SerializedName("isDel")
    private String isDel;//0=正常，1=已删除

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    @SerializedName("searchKey")
    private String searchKey;

    @SerializedName("data")
    private LinkedHashMap data;

    private String userName;
    private String createTime;
    private String userPic;

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    private boolean isCheck;//是否选中
    private boolean isShow;//是否显示cb

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    private Object datasbean;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public <T extends Object> T getData() {
        if (datasbean == null) {
            Class claz = null;
            switch (getType()) {
                case "1"://新闻类型
                    claz = NewsBean.class;
                    break;

                case "2"://课程类型
                    claz = ChatInfosBean.class;
                    break;

                case "3"://交易信息
                    claz = DealListBean.class;
                    break;

                case "4"://圈子动态
                    claz = CircleHomeBean.class;
                    break;

                case "5"://自定义类型
                    claz = CustomCollectBean.class;
                    break;

                case "6"://会话里文字类型
                    claz = ConversationTextBean.class;
                    break;

                case "7"://会话里图片类型
                    claz = ConversationTextBean.class;
                    break;

                case "8"://会话里图片类型
                    claz = CircleBean.class;
                    break;

                case "9"://系列课类型
                    claz = SeriesPageBean.class;
                    break;

                case "10"://商品类型
                    claz = CollectMallDetailBean.class;
                    break;

                case "11"://小视频类型
                    claz = ConversationTextBean.class;
                    break;

                case "12"://直播间
                    claz = LiveRoomBean.class;
                    break;
                case "13"://专题
                    claz = SpecialBean.class;
                    break;


            }

            String s = GsonUtil.objectToJson(data);
            datasbean = GsonUtil.jsonToBean(s, claz);
        }
        return (T) datasbean;
    }


    public void setDatas(LinkedHashMap datas) {
        this.data = datas;
    }


}
