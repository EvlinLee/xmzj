package com.gxtc.huchuan.bean;

/**
 * Describe:
 * Created by ALing on 2017/6/9 .
 */

public class GroupRuleBean {

    /**
     * id : 3
     * createTime : 2017-06-10 11:33:00.0
     * groupId : 168
     * roletext : I like you,It takes two to tango.
     * userId : 22
     * isRedbag : 1
     * isSendRule 新人进圈是否发送圈规，0：否，1：是
       createGroupChat  是否已创 建了群聊，0：已创建，1：没有创建
     */

    private String id;
    private String createTime;
    private String groupId;
    private String roletext;
    private String userId;
    private String isRedbag;
    private String isSendRule;
    private String createGroupChat;

    public String getIsSendRule() {
        return isSendRule;
    }

    public void setIsSendRule(String isSendRule) {
        this.isSendRule = isSendRule;
    }

    public String getCreateGroupChat() {
        return createGroupChat;
    }

    public void setCreateGroupChat(String createGroupChat) {
        this.createGroupChat = createGroupChat;
    }

    public GroupRuleBean(String roletext) {
        this.roletext = roletext;
    }

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public String getCreateTime() { return createTime;}

    public void setCreateTime(String createTime) { this.createTime = createTime;}

    public String getGroupId() { return groupId;}

    public void setGroupId(String groupId) { this.groupId = groupId;}

    public String getRoletext() { return roletext;}

    public void setRoletext(String roletext) { this.roletext = roletext;}

    public String getUserId() { return userId;}

    public void setUserId(String userId) { this.userId = userId;}

    public String getIsRedbag() { return isRedbag;}

    public void setIsRedbag(String isRedbag) { this.isRedbag = isRedbag;}
}
