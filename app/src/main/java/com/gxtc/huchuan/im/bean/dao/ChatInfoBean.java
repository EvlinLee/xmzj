package com.gxtc.huchuan.im.bean.dao;

import com.gxtc.huchuan.im.bean.RemoteMessageBean;
import com.gxtc.huchuan.im.bean.RemoteMessageBeanDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import com.gxtc.huchuan.bean.DaoSession;




/**
 * Created by Gubr on 2017/4/26.
 */
@Entity
public class ChatInfoBean {
    @Id private                                                             Long                    id;
    @ToMany(referencedJoinProperty = "targetId") @OrderBy("id ASC")
    private                                                                 List<RemoteMessageBean> oders;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 323600364)
    private transient ChatInfoBeanDao myDao;
    @Generated(hash = 591846069)
    public ChatInfoBean(Long id) {
        this.id = id;
    }
    @Generated(hash = 1948775465)
    public ChatInfoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1284088838)
    public List<RemoteMessageBean> getOders() {
        if (oders == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RemoteMessageBeanDao targetDao = daoSession.getRemoteMessageBeanDao();
            List<RemoteMessageBean> odersNew = targetDao._queryChatInfoBean_Oders(id);
            synchronized (this) {
                if (oders == null) {
                    oders = odersNew;
                }
            }
        }
        return oders;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 347137610)
    public synchronized void resetOders() {
        oders = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 106133703)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChatInfoBeanDao() : null;
    }
}