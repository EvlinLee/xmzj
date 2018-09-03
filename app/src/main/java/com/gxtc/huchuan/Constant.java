package com.gxtc.huchuan;


public class Constant {

    public static final float GUARANTEE_SCALE = 0.02f;       //平台的担保费用比列 2%

    public static final String PACKAGE_NAME = "com.gxtc.huchuan";

    public static final String INTENT_DATA = "data";

    public static final String REPAY = "repay";

    public static final String SELECT = "select";

    public static final String ONLY_WIFI = "only_wifi";

    public static final int SELECT_TYPE_SHARE = 0;    //分享文章 交易之类的
    public static final int SELECT_TYPE_CARD = 1;     //分享个人名片
    public static final int SELECT_TYPE_COLLECT = 2;  //分享收藏
    public static final int SELECT_TYPE_RELAY = 3;    //转发消息
    public static final int SELECT_TYPE_GUARAN_DEAL = 4;    //申请担保交易

    public static final int STATUE_REFRESH_DYNIMIC = 4;  //动态刷新

    public static final String AUTO_PLAY_NEXT_VOICEMASSAGE = "auto_play_next_voicemassage";

    public static final int COMPRESS_VALUE = 1024 * 1024 * 2;      //压缩的阀值   1MB

    public static final String STATUE_LINKTYPE_CIRCLE = "0";  //圈子
    public static final String STATUE_LINKTYPE_ARTICLE = "1";  //文章
    public static final String STATUE_LINKTYPE_CLASS = "2";  //课堂
    public static final String STATUE_LINKTYPE_WITHDRAW_CASH = "3";  //提现
    public static final String STATUE_LINKTYPE_AUTHOR = "4";  //作者
    public static final String STATUE_LINKTYPE_ANCHOR = "5";  //主播
    public static final String STATUE_LINKTYPE_REAL_NAME = "6";  //实名
    public static final String STATUE_LINKTYPE_TRANSACTION = "7";  //交易

    public static final int[] REFRESH_COLOR = {
            R.color.refresh_color1,
            R.color.refresh_color2,
            R.color.refresh_color3};

    /**
     * activity 的 result码
     */
    public static final int INTENT_LOGIN_RESULT = 200;            //登录result
    public static final int INTENT_PAY_RESULT = 202;              //支付result

    public static final int PRE_REQUEST_CODE = 002;             //权限回调


    public static final int USERCODE = 204;                     //用户编码

    public static final String KEY = "7wfaWWDhzvP4";
    public static final String DES3_KEY = "uM4oxb724ElxluOphXvFja3eEUBxNOMG";


    public static final String LIVE_HOME = "LIVE_HOME";
    public static final String DEFUAL_SHARE_IMAGE_URI = "https://xmzjvip.b0.upaiyun.com/xmzj/81531490254618898.png";

    //参数type： 0，关于我们，1，加入我们,2，交易的帮助文档，3，课堂使用教程
    public static final String ABOUTLINK = "https://apps.xinmei6.com/publish/sysInfo.html?type=";

    public static final String GROUPCODE_OUT = "10261";     //不在该群
    public static final String GROUPCODE_CLEAR = "10278";   //群聊解散


    public interface Url {
        String API_VER = "1_0";                      //接口的版本号，在拦截器里面替换url
        String DEBUG = "http://120.25.56.153/";      //测试接口
        String DEBUG_LOCAL = "http://192.168.1.12/"; //测试接口
        String BASE_URL = "https://app.xinmei6.com/";//上线接口
        String WX_BASE_URL = "https://mp.weixin.qq.com";
        String YZ_BASE_URL = "https://uic.youzan.com/";

        String PROTOCOL_CIRCLE = "https://apps.xinmei6.com/publish/circleRule.html";       //创建圈子协议
        String PROTOCOL_REGISTER = "https://apps.xinmei6.com/publish/regRule.html";        //用户注册协议
        String SHARE_MARKET_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.gxtc.huchuan";//分享链接
    }

    /**
     * 请求码
     */
    public interface requestCode {
        //普通登录
        int LOGIN = 9001;
        //手机验证登录
        int PHONELOGIN = 9002;
        //qq登录
        int QQLOGIN = 9003;
        //微信登录
        int WEIXINLOGIN = 9004;
        //新浪微博登录
        int SINALOGIN = 9005;

        //新闻点赞收藏请求码
        int NEWS_LIKEANDCOLLECT = 9006;
        //新闻收藏列表
        int NEWS_COLLECT = 9007;
        //作者申请或个人发布请求码
        int NEWS_AUTHOR = 9008;
        //个人文章
        int MINE_ARTICLE = 9009;
        //上传课件
        int UPLOAD_KEJIAN = 9010;
        //上传文件
        int UPLOAD_FILE = 9011;
        //登录之后刷新圈子数据
        int REFRESH_CIRCLE_HOME = 9012;
        //点赞之后回退刷新圈子点赞
        int CIRCLE_DZ = 9013;
        //发个人动态之后刷新界面
        int CIRCLE_DT_REFRESH = 9014;
        //图片选择
        int REQUEST_CODE_AVATAR = 10000;
        //专题详情重新登录
        int SPECIAL_DETAIL_LOGIN = 9015;
    }

    /**
     * 返回码
     */
    public interface ResponseCode {
        int LOGINRESPONSE_CODE = 9999;
        int ORDER_CANCEL = 998;
        int ADD_ADDRESS = 168;
        int SELECT_ADDRESS = 169;
        int GOTO_MALL_ORDER_DETAIL = 1001;
        int NORMAL_FLAG = 222;
        int FOCUS_RESULT_CODE = 203;            //个人主页关注
        int CIRCLE_RESULT_DZ = 204;//点赞
        int CIRCLE_ISSUE = 205;//发表动态
        int ISSUE_TONG_BU = 206;//同步动态到圈子

    }
}
