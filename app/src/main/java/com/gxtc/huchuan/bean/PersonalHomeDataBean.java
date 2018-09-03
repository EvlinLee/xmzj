package com.gxtc.huchuan.bean;

/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 * 当type== news，新闻：
   当type== chatInfo，直播课程
   当type== tradeInfo，交易
 */

public class PersonalHomeDataBean {
    /**
     * data : {"anonymous":"0","audit":"1","createTime":"1490606304000","id":"1","isTop":"0","isfinish":"1","liuYan":"21","read":"208","title":"【此姑娘太疯狂】公司+个人订阅号斤35万粉，月流量主收入2.7万","tradeType":"0","tradeTypeSonId":"4","tradeTypeSonName":"音乐影视","userId":"22","userName":"aaaaa","userPic":"http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg"}
     * type : tradeInfo
     */

    private DataBean data;
    private String type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class DataBean {
        private DealListBean dealListBean;
        /**
         * anonymous : 0
         * audit : 1
         * createTime : 1490606304000
         * id : 1
         * isTop : 0
         * isfinish : 1
         * liuYan : 21
         * read : 208
         * title : 【此姑娘太疯狂】公司+个人订阅号斤35万粉，月流量主收入2.7万
         * tradeType : 0
         * tradeTypeSonId : 4
         * tradeTypeSonName : 音乐影视
         * userId : 22
         * userName : aaaaa
         * userPic : http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg
         */

        private String anonymous;
        private String audit;
        private String createTime;
        private String id;
        private String isTop;
        private String isfinish;
        private String liuYan;
        private String read;
        private String title;
        private String tradeType;
        private String tradeTypeSonId;
        private String tradeTypeSonName;
        private String userId;
        private String userName;
        private String userPic;

        public String getAnonymous() {
            return anonymous;
        }

        public void setAnonymous(String anonymous) {
            this.anonymous = anonymous;
        }

        public String getAudit() {
            return audit;
        }

        public void setAudit(String audit) {
            this.audit = audit;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIsTop() {
            return isTop;
        }

        public void setIsTop(String isTop) {
            this.isTop = isTop;
        }

        public String getIsfinish() {
            return isfinish;
        }

        public void setIsfinish(String isfinish) {
            this.isfinish = isfinish;
        }

        public String getLiuYan() {
            return liuYan;
        }

        public void setLiuYan(String liuYan) {
            this.liuYan = liuYan;
        }

        public String getRead() {
            return read;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }

        public String getTradeTypeSonId() {
            return tradeTypeSonId;
        }

        public void setTradeTypeSonId(String tradeTypeSonId) {
            this.tradeTypeSonId = tradeTypeSonId;
        }

        public String getTradeTypeSonName() {
            return tradeTypeSonName;
        }

        public void setTradeTypeSonName(String tradeTypeSonName) {
            this.tradeTypeSonName = tradeTypeSonName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPic() {
            return userPic;
        }

        public void setUserPic(String userPic) {
            this.userPic = userPic;
        }

    }



    /*@SerializedName("type")
    private String type;

    @SerializedName("data")
    private List<?> data;


    private List databean;


    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public <T extends Object> List<T> getData() {
        if (databean == null) {
            Class claz = null;
            switch (getType()) {
                case "news"://新闻类型
                    claz = NewsBean.class;
                    break;
                case "chatInfo"://课程类型
                    claz= HomePageChatInfo.class;
                    break;
                *//*case "tradeInfo"://直播间类型
                    claz=LiveRoomBean.class;
                    break;*//*
                case "tradeInfo"://担保交易
                    claz = DealListBean.class;
            }

            String s = GsonUtil.objectToJson(data);
            System.out.println(s);
            Log.d("tag", "getData: "+s);
            databean = GsonUtil.fromJsonArray(s, claz);
        }
        return (List<T>) databean;
    }


    public void setData(List<?> data) { this.data = data;}

    @Override
    public String toString() {
        return "PersonalHomeDataBean{" +
                "type='" + type + '\'' +
                ", data=" + data +
                ", databean=" + databean +
                '}';
    }
    */


}
