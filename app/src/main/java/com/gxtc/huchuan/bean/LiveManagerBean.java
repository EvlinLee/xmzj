package com.gxtc.huchuan.bean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/13 .
 */

public class LiveManagerBean {


    /**
     * datas : [{"chatInfoId":"174","chatRoomId":"4","count":"1","headPic":"http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg","id":"7","joinType":"1","name":"aaaaa","userCode":"128399864"}]
     * selfData : {"chatInfoId":"174","chatRoomId":"4","count":"1","headPic":"http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg","id":"7","joinType":"1","name":"aaaaa","userCode":"128399864"}
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
         * chatInfoId : 174
         * chatRoomId : 4
         * count : 1
         * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg
         * id : 7
         * joinType : 1
         * name : aaaaa
         * userCode : 128399864
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

    public static class DatasBean {
        /**
         * chatInfoId : 174
         * chatRoomId : 4
         * count : 1
         * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg
         * id : 7
         * joinType : 1
         * name : aaaaa
         * userCode : 128399864
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
