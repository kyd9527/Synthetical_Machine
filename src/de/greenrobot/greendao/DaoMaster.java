package de.greenrobot.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import de.greenrobot.greendao.KeyInfoDao;
import de.greenrobot.greendao.CommodityInfoDao;
import de.greenrobot.greendao.GoodsInfoDao;
import de.greenrobot.greendao.SetTimeInfoDao;
import de.greenrobot.greendao.SetOpenDoorDao;
import de.greenrobot.greendao.OrdersInfoDao;
import de.greenrobot.greendao.AdVerInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        KeyInfoDao.createTable(db, ifNotExists);
        CommodityInfoDao.createTable(db, ifNotExists);
        GoodsInfoDao.createTable(db, ifNotExists);
        SetTimeInfoDao.createTable(db, ifNotExists);
        SetOpenDoorDao.createTable(db, ifNotExists);
        OrdersInfoDao.createTable(db, ifNotExists);
        AdVerInfoDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        KeyInfoDao.dropTable(db, ifExists);
        CommodityInfoDao.dropTable(db, ifExists);
        GoodsInfoDao.dropTable(db, ifExists);
        SetTimeInfoDao.dropTable(db, ifExists);
        SetOpenDoorDao.dropTable(db, ifExists);
        OrdersInfoDao.dropTable(db, ifExists);
        AdVerInfoDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(KeyInfoDao.class);
        registerDaoClass(CommodityInfoDao.class);
        registerDaoClass(GoodsInfoDao.class);
        registerDaoClass(SetTimeInfoDao.class);
        registerDaoClass(SetOpenDoorDao.class);
        registerDaoClass(OrdersInfoDao.class);
        registerDaoClass(AdVerInfoDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
