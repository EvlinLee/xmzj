package com.gxtc.huchuan.data;


import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.dao.NewChannelItem;
import com.gxtc.huchuan.bean.dao.NewChannelItemDao;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/10.
 */

public class ChannelManager {

    public static ChannelManager channelManage;

    private ChannelManager() {
    }

    /**
     * 默认的用户选择频道列表
     */
    public static List<NewChannelItem> defaultUserChannels = new ArrayList<>();
    /**
     * 默认的其他频道列表
     */
    public static List<NewChannelItem> defaultOtherChannels = new ArrayList<>();
    /**
     * 判断数据库中是否存在用户数据
     */

    public static ChannelManager getInstance() {
        if (channelManage == null) {
            channelManage = new ChannelManager();
        }
        return channelManage;
    }

    private NewChannelItemDao getDao() {
        return GreenDaoHelper.getInstance().getSeeion().getNewChannelItemDao();
    }

    /**
     * 清除所有的频道
     */
    public void deleteAllChannel() {
        NewChannelItemDao dao = getDao();
        dao.deleteAll();
    }

    /**
     * 获取用户的频道
     *
     * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
     */
    public List<NewChannelItem> getUserChannel() {
        NewChannelItemDao dao = getDao();
        List<NewChannelItem> cacheList = dao.queryBuilder().
                where(NewChannelItemDao.Properties.Selected.like("1")).list();
//        if (cacheList != null && !cacheList.isEmpty()) {
//            userExist = true;

            return cacheList;
//        }
//        initDefaultChannel();
//        deleteAllChannel();
//        setDefaultUserChannels();
//        saveUserChannel(defaultUserChannels);
//        saveOtherChannel(defaultOtherChannels);
//        return defaultUserChannels;
    }

    /**
     * 获取其他的频道
     *
     * @return 数据库存在用户配置则获取用户配置的其他频道，否则用默认其他频道
     */
    public List<NewChannelItem> getOtherChannel() {
        NewChannelItemDao dao = getDao();
        List<NewChannelItem> cacheList = dao.queryBuilder().
                where(NewChannelItemDao.Properties.Selected.like("0")).list();
//        if (cacheList != null && !cacheList.isEmpty()) {
            return cacheList;
//        }
//        if (userExist)
//            return cacheList;
//        cacheList = defaultOtherChannels;
//        return cacheList;
    }

    /**
     * 保存用户频道到数据库
     *
     * @param userList
     */
    public void saveUserChannel(List<NewChannelItem> userList) {
        for (int i = 0; i < userList.size(); i++) {
            NewChannelItem channelItem = userList.get(i);
            channelItem.setOrderId(i);
            channelItem.setSelected(Integer.valueOf(1));
            getDao().insert(channelItem);
        }

    }

    public void saveOtherChannel(List<NewChannelItem> otherList) {
        for (int i = 0; i < otherList.size(); i++) {
            NewChannelItem channelItem = otherList.get(i);
            channelItem.setOrderId(i);
            channelItem.setSelected(Integer.valueOf(0));
            getDao().insert(channelItem);
        }

    }

    /**
     * 初始化数据库内的频道数据
     */
    private void initDefaultChannel() {
        deleteAllChannel();
        saveUserChannel(defaultUserChannels);
        saveOtherChannel(defaultOtherChannels);
    }
}
