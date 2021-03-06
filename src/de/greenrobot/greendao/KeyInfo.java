package de.greenrobot.greendao;

import java.util.List;
import de.greenrobot.greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "KEY_INFO".
 */
public class KeyInfo {

    private Long id;
    private int key;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient KeyInfoDao myDao;

    private List<GoodsInfo> goodsInfoList;

    public KeyInfo() {
    }

    public KeyInfo(Long id) {
        this.id = id;
    }

    public KeyInfo(Long id, int key) {
        this.id = id;
        this.key = key;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getKeyInfoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<GoodsInfo> getGoodsInfoList() {
        if (goodsInfoList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GoodsInfoDao targetDao = daoSession.getGoodsInfoDao();
            List<GoodsInfo> goodsInfoListNew = targetDao._queryKeyInfo_GoodsInfoList(id);
            synchronized (this) {
                if(goodsInfoList == null) {
                    goodsInfoList = goodsInfoListNew;
                }
            }
        }
        return goodsInfoList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetGoodsInfoList() {
        goodsInfoList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
