package com.gxtc.huchuan.bean;

import java.util.List;

public class GoodsCommentBean {


    /**
     * comment : 测试留言
     * createtime : 1490772209000
     * id : 52
     * isSelf : 0
     * replyVos : [{"comment":"测试回复","createtime":1490772286000,"id":52,"tradeCommentId":52,"userId":3,"userName":"测试","userPic":"http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg"}]
     * tradeInfoId : 1
     * userId : 3
     * userName : 测试
     * userPic : http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg
     */

    private String comment;
    private long createtime;
    private int id;
    private int isSelf;
    private int tradeInfoId;
    private int userId;
    private int dzNum;        //点赞数量
    private String userName;
    private String userPic;
    private String userCode;
    private String fbCode;
    private int isDZ;        //是否已点赞。0、否；1、是

    public int getDzNum() {
        return dzNum;
    }

    public void setDzNum(int dzNum) {
        this.dzNum = dzNum;
    }

    public int getIsDZ() {
        return isDZ;
    }

    public void setIsDZ(int isDZ) {
        this.isDZ = isDZ;
    }

    private List<ReplyVosBean> replyVos;

    public String getFbCode() {
        return fbCode;
    }

    public void setFbCode(String fbCode) {
        this.fbCode = fbCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

    public int getTradeInfoId() {
        return tradeInfoId;
    }

    public void setTradeInfoId(int tradeInfoId) {
        this.tradeInfoId = tradeInfoId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public List<ReplyVosBean> getReplyVos() {
        return replyVos;
    }

    public void setReplyVos(List<ReplyVosBean> replyVos) {
        this.replyVos = replyVos;
    }

    public static class ReplyVosBean {
        /**
         * comment : 测试回复
         * createtime : 1490772286000
         * id : 52
         * tradeCommentId : 52
         * userId : 3
         * userName : 测试
         * userPic : http://fssvip.b0.upaiyun.com/fss/65411488794908036.jpg
         */

        private String comment;
        private long createtime;
        private int id;
        private int tradeCommentId;
        private int userId;
        private String userName;        //留言用户名称
        private String userPic;         //留言用户头像
        private String userCode;         //留言用户头像
        private String targetUserId;    //回复给谁的用户id
        private String targetUserName;    //回复给谁的用户名称

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTradeCommentId() {
            return tradeCommentId;
        }

        public void setTradeCommentId(int tradeCommentId) {
            this.tradeCommentId = tradeCommentId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
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

        public String getTargetUserId() {
            return targetUserId;
        }

        public void setTargetUserId(String targetUserId) {
            this.targetUserId = targetUserId;
        }

        public String getTargetUserName() {
            return targetUserName;
        }

        public void setTargetUserName(String targetUserName) {
            this.targetUserName = targetUserName;
        }
    }
}
