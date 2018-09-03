package com.gxtc.huchuan.bean;

/**
 * 直播课程vo
 */
public class ChatInfoBean {

	private String id;//课程ID  （跳进话聊界面）
	private String chatRoom;//课堂id
	private String chatSeries;//系列课程id
	private String subtitle;//直播主题
	private String starttime;//直播开始时间
	private String pent;//邀请人获得比例
	private String endtime;//直播结束时间
	private String chatway;//直播形式 0：讲座，1：幻灯片
	private String chattype;//直播类型 0：公开，1：加密，2，收费
	private String password;//直播加密密码
	private String isfree;//是否收费（0：免费，1：收费）
	private String fee;//收费金额
	private String showinfo;//结束标识。 0：正常，1：结束
	private String freetime;//是否免费试听。0否，1免费试听
	private String chatRoomName;//课堂名称
	private String chatTypeSonId;//子分类id
	//显示内容
	private String joinCount;//参与人数
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getChatRoom() {
		return chatRoom;
	}
	public void setChatRoom(String chatRoom) {
		this.chatRoom = chatRoom;
	}
	public String getChatSeries() {
		return chatSeries;
	}
	public void setChatSeries(String chatSeries) {
		this.chatSeries = chatSeries;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getPent() {
		return pent;
	}
	public void setPent(String pent) {
		this.pent = pent;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getChatway() {
		return chatway;
	}
	public void setChatway(String chatway) {
		this.chatway = chatway;
	}
	public String getChattype() {
		return chattype;
	}
	public void setChattype(String chattype) {
		this.chattype = chattype;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIsfree() {
		return isfree;
	}
	public void setIsfree(String isfree) {
		this.isfree = isfree;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getShowinfo() {
		return showinfo;
	}
	public void setShowinfo(String showinfo) {
		this.showinfo = showinfo;
	}
	public String getFreetime() {
		return freetime;
	}
	public void setFreetime(String freetime) {
		this.freetime = freetime;
	}
	public String getChatRoomName() {
		return chatRoomName;
	}
	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	public String getJoinCount() {
		return joinCount;
	}
	public void setJoinCount(String joinCount) {
		this.joinCount = joinCount;
	}
	public String getChatTypeSonId() {
		return chatTypeSonId;
	}
	public void setChatTypeSonId(String chatTypeSonId) {
		this.chatTypeSonId = chatTypeSonId;
	}		


}
