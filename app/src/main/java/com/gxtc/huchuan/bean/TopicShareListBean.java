package com.gxtc.huchuan.bean;

import java.util.List;

/**
 * Created by sjr on 2017/4/17.
 *
 */

public class TopicShareListBean {


    /**
     * datas : [{"chatInfoId":"32","chatRoomId":"2","count":"1","headPic":"http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg","id":"11","joinType":"0","name":"测试","userCode":"821705443"},{"chatInfoId":"32","chatRoomId":"2","count":"1","headPic":"http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg","id":"12","joinType":"0","name":"aaaaa","userCode":"128399864"},{"chatInfoId":"32","chatRoomId":"2","count":"1","headPic":"","id":"16","joinType":"0","name":"740906051","userCode":"740906051"},{"chatInfoId":"32","chatRoomId":"2","count":"1","headPic":"","id":"17","joinType":"0","name":"395613686","userCode":"395613686"}]
     * selfData : {"chatInfoId":"32","chatRoomId":"2","count":"0","headPic":"http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg","joinType":"0","name":"522144","userCode":"455689593"}
     */

    private SelfDataBean selfData;
    private List<DatasBean> datas;

    public SelfDataBean getSelfData() {
        return selfData;
    }

    public void setSelfData(SelfDataBean selfData) {
        this.selfData = selfData;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class SelfDataBean {
        /**
         * chatInfoId : 32
         * chatRoomId : 2
         * count : 0
         * headPic : http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg
         * joinType : 0
         * name : 522144
             * userCode : 455689593
         */

        private String chatInfoId;
        private String chatRoomId;
        private String count;
        private String headPic;
        private String joinType;
        private String name;
        private String userCode;

        public String getChatInfoId() {
            return chatInfoId;
        }

        public void setChatInfoId(String chatInfoId) {
            this.chatInfoId = chatInfoId;
        }

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        public String getJoinType() {
            return joinType;
        }

        public void setJoinType(String joinType) {
            this.joinType = joinType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }
    }

    public static class DatasBean {
        /**
         * chatInfoId : 32
         * chatRoomId : 2
         * count : 1
         * headPic : http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg
         * id : 11
         * joinType : 0
         * name : 测试
         * userCode : 821705443
         */

        private String chatInfoId;
        private String chatRoomId;
        private String count;
        private String headPic;
        private String id;
        private String joinType;
        private String name;
        private String userCode;

        public String getChatInfoId() {
            return chatInfoId;
        }

        public void setChatInfoId(String chatInfoId) {
            this.chatInfoId = chatInfoId;
        }

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJoinType() {
            return joinType;
        }

        public void setJoinType(String joinType) {
            this.joinType = joinType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }
    }
}
