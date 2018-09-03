package com.gxtc.huchuan.data;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.dao.UserDao;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import io.rong.imlib.model.UserInfo;


/**
 * Created by Steven on 16/11/9.
 * 用户信息管理类
 */
public class UserManager {

    private static UserManager manager;
    private static User user;

    private UserManager() {

    }

    public static UserManager getInstance() {
        if (manager == null) {
            manager = new UserManager();
        }
        return manager;
    }

    private UserDao getDao() {
        return GreenDaoHelper.getInstance().getSeeion().getUserDao();
    }

    /**
     * 获取用户token
     * @return
     */
    public String getToken() {
        User user = getUser();
        if (user != null) {
            return user.getToken();
        }
        return "";
    }

    /**
     * 获取用户phone
     *
     * @return
     */
    public String getPhone() {
        User user = getUser();
        if (user != null) {
            return user.getPhone();
        }
        return "";
    }

    /**
     * 是否是作者
     *
     * @return 0，不是，1 是
     */
    public String getIsAuthor() {
        User user = getUser();
        if (user != null) {
            return user.getIsAuthor();
        }
        return "0";
    }

    /**
     * 是否是主播
     *
     * @return 0，不是，1 是
     */
    public String getIsAnchor() {
        User user = getUser();
        if (user != null) {
            return user.getIsAnchor();
        }
        return "0";
    }


    /**
     * 是否是实名
     * @return 0，不是，1 是
     */
    public String getIsRealAudit() {
        User user = getUser();
        if (user != null) {
            return user.getIsRealAudit();
        }
        return "0";
    }

    /**
     * 获取课堂id
     *
     * @return
     */
    public String getChatRoomId() {
        User user = getUser();
        if (user != null && "1".equals(getIsAnchor())) {
            return user.getChatRoomId();
        }
        return "";
    }

    /**
     * 用户编码
     *
     * @return
     */
    public String getUserCode() {
        User user = getUser();
        if (user != null) {
            return user.getUserCode();
        }
        return "";
    }

    /**
     * 获取融云token
     *
     * @return
     */
    public String getImToken() {
        User user = getUser();
        if (user != null) {
            return user.getImToken();
        }
        return "";
    }

    public String getUserName() {
        User user = getUser();
        if (user != null) {
            return user.getName();
        }
        return "";
    }

    public String getHeadPic() {
        User user = getUser();
        if (user != null) {
            return user.getHeadPic();
        }
        return "";
    }

    //刷新用户钱数
    public void updateUserMoney(User temp) {
        User user = getUser();
        if (temp != null && user != null) {
            user.setUsableBalance(temp.getUsableBalance());
            user.setBalance(temp.getBalance());
            user.setFrozenBalance(temp.getFrozenBalance());
            updataUser(user);
        }
    }

    /**
     * 是否登录
     *
     * @return true : false ? 已登录 : 未登录
     */
    public boolean isLogin() {
        if (getUser() != null && !TextUtils.isEmpty(getToken())) {
            return true;
        }
        return false;
    }


    /**
     * 如果没有登录  可以跳到登录界面
     *
     * @param activity
     * @return
     */
    public boolean isLogin(Activity activity) {
        if (getUser() != null) {
            return true;
        }
        GotoUtil.goToActivity(activity, LoginAndRegisteActivity.class);
        return false;
    }


    public void saveUser(User userBean) {
        /**
         * save操作会判断是否有id ，有的话就updata，没有的话就insert
         */
        if(userBean != null && userBean.getId() == null && getUser() != null){
            userBean.setId(getUser().getId());
        }

        UserDao dao = getDao();
        dao.save(userBean);
    }

    public void updataUser(User user) {
        if (user == null) return;

        UserManager.user = user;
        saveUser(user);
    }


    //获取融云的userInfo对象
    public UserInfo obtinUserInfo(){
        User user = getUser();
        if(user != null){
           return new UserInfo(user.getUserCode(), user.getName(), Uri.parse(user.getHeadPic()));
        }
        return null;
    }


    public User getUser() {
        if(user != null){
            return user;
        }
        UserDao dao = getDao();
        try {
            if (dao.queryBuilder().unique() == null)
                return null;
            else
                return dao.queryBuilder().unique();

        } catch (Exception e) {
            dao.deleteAll();
            e.printStackTrace();
            return null;
        }
    }

    public void deleteUser() {
        UserDao dao = getDao();
        dao.deleteAll();
        user = null;
    }
}
