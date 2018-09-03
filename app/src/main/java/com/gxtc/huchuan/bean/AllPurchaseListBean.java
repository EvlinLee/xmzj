package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 来自 苏修伟 on 2018/4/25.
 */
public class AllPurchaseListBean implements Serializable {
    private int type;              // 0=ALL，1=话题，2=系列课，3=圈子，4=担保交易，5=商城
    private int id;                //  根据Type值对应的业务ID
    private String userCode;         //新媒号
    private long createTime;       //  创建时间
    private int isPay;            //   是否支付：0=未支付，1=已支付
    private int isSett;           //   是否结算：0=未结算，1=已结算
    private int isRefund;         //是否退款：0=未退款，1=已退款  type=4：0、未申请退款，1：审核中，2：完成，3：被拒
    private String cover;            //  封面
    private String title;            //  标题
    private String assistantTitle;  //  副标题
    private double fee;              //  价格
    private int number;          //   数量
    private String orderNo;         //   订单编号
    private double pay;             //    实付金额
    private int orderStart;     //    订单状态
    private List<Commodity> extra;           //   type=5时


    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public int getIsSett() {
        return isSett;
    }

    public void setIsSett(int isSett) {
        this.isSett = isSett;
    }

    public int getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(int isRefund) {
        this.isRefund = isRefund;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssistantTitle() {
        return assistantTitle;
    }

    public void setAssistantTitle(String assistantTitle) {
        this.assistantTitle = assistantTitle;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public int getOrderStart() {
        return orderStart;
    }

    public void setOrderStart(int orderStart) {
        this.orderStart = orderStart;
    }

    public List<AllPurchaseListBean.Commodity> getExtra() {
        return extra;
    }

    public void setExtra(List<Commodity> extra) {
        this.extra = extra;
    }

    public Commodity getNewCommodity(){
        return new Commodity();
    }

   public class Commodity{
        private String title;         //  商品标题
        private String pic;           //  商品封面
        private String formatName;   //  规格名称
        private double price;        //   单价
        private int sum;          //   数量

       public String getTitle() {
           return title;
       }

       public void setTitle(String title) {
           this.title = title;
       }

       public String getPic() {
           return pic;
       }

       public void setPic(String pic) {
           this.pic = pic;
       }

       public String getFormatName() {
           return formatName;
       }

       public void setFormatName(String formatName) {
           this.formatName = formatName;
       }

       public double getPrice() {
           return price;
       }

       public void setPrice(double price) {
           this.price = price;
       }

       public int getSum() {
           return sum;
       }

       public void setSum(int sum) {
           this.sum = sum;
       }
   }
}
