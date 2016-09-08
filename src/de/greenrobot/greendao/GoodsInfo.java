package de.greenrobot.greendao;

import de.greenrobot.greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "GOODS_INFO".
 */
public class GoodsInfo {

    private Long id;
    private int goodsNumber;
    private int goodsStock;
    private int goodsCapacity;
    private int goodsStatus;
    private int goodsDisable;
    private long goodsID;
    private long keyID;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient GoodsInfoDao myDao;

    private KeyInfo keyInfo;
    private Long keyInfo__resolvedKey;

    private CommodityInfo commodityInfo;
    private Long commodityInfo__resolvedKey;


    public GoodsInfo() {
    }

    public GoodsInfo(Long id) {
        this.id = id;
    }

    public GoodsInfo(Long id, int goodsNumber, int goodsStock, int goodsCapacity, int goodsStatus, int goodsDisable, long goodsID, long keyID) {
        this.id = id;
        this.goodsNumber = goodsNumber;
        this.goodsStock = goodsStock;
        this.goodsCapacity = goodsCapacity;
        this.goodsStatus = goodsStatus;
        this.goodsDisable = goodsDisable;
        this.goodsID = goodsID;
        this.keyID = keyID;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGoodsInfoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public int getGoodsStock() {
        return goodsStock;
    }

    public void setGoodsStock(int goodsStock) {
        this.goodsStock = goodsStock;
    }

    public int getGoodsCapacity() {
        return goodsCapacity;
    }

    public void setGoodsCapacity(int goodsCapacity) {
        this.goodsCapacity = goodsCapacity;
    }

    public int getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(int goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public int getGoodsDisable() {
        return goodsDisable;
    }

    public void setGoodsDisable(int goodsDisable) {
        this.goodsDisable = goodsDisable;
    }

    public long getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(long goodsID) {
        this.goodsID = goodsID;
    }

    public long getKeyID() {
        return keyID;
    }

    public void setKeyID(long keyID) {
        this.keyID = keyID;
    }

    /** To-one relationship, resolved on first access. */
    public KeyInfo getKeyInfo() {
        long __key = this.keyID;
        if (keyInfo__resolvedKey == null || !keyInfo__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KeyInfoDao targetDao = daoSession.getKeyInfoDao();
            KeyInfo keyInfoNew = targetDao.load(__key);
            synchronized (this) {
                keyInfo = keyInfoNew;
            	keyInfo__resolvedKey = __key;
            }
        }
        return keyInfo;
    }

    public void setKeyInfo(KeyInfo keyInfo) {
        if (keyInfo == null) {
            throw new DaoException("To-one property 'keyID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.keyInfo = keyInfo;
            keyID = keyInfo.getId();
            keyInfo__resolvedKey = keyID;
        }
    }

    /** To-one relationship, resolved on first access. */
    public CommodityInfo getCommodityInfo() {
        long __key = this.goodsID;
        if (commodityInfo__resolvedKey == null || !commodityInfo__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CommodityInfoDao targetDao = daoSession.getCommodityInfoDao();
            CommodityInfo commodityInfoNew = targetDao.load(__key);
            synchronized (this) {
                commodityInfo = commodityInfoNew;
            	commodityInfo__resolvedKey = __key;
            }
        }
        return commodityInfo;
    }

    public void setCommodityInfo(CommodityInfo commodityInfo) {
        if (commodityInfo == null) {
            throw new DaoException("To-one property 'goodsID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.commodityInfo = commodityInfo;
            goodsID = commodityInfo.getId();
            commodityInfo__resolvedKey = goodsID;
        }
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