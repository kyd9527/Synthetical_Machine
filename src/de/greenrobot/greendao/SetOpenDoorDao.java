package de.greenrobot.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import de.greenrobot.greendao.SetOpenDoor;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SET_OPEN_DOOR".
*/
public class SetOpenDoorDao extends AbstractDao<SetOpenDoor, Long> {

    public static final String TABLENAME = "SET_OPEN_DOOR";

    /**
     * Properties of entity SetOpenDoor.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OpenTime = new Property(1, String.class, "openTime", false, "OPEN_TIME");
        public final static Property OrderId = new Property(2, Integer.class, "OrderId", false, "ORDER_ID");
        public final static Property SyncStatuc = new Property(3, Integer.class, "syncStatuc", false, "SYNC_STATUC");
    };


    public SetOpenDoorDao(DaoConfig config) {
        super(config);
    }
    
    public SetOpenDoorDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SET_OPEN_DOOR\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"OPEN_TIME\" TEXT," + // 1: openTime
                "\"ORDER_ID\" INTEGER," + // 2: OrderId
                "\"SYNC_STATUC\" INTEGER);"); // 3: syncStatuc
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SET_OPEN_DOOR\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SetOpenDoor entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String openTime = entity.getOpenTime();
        if (openTime != null) {
            stmt.bindString(2, openTime);
        }
 
        Integer OrderId = entity.getOrderId();
        if (OrderId != null) {
            stmt.bindLong(3, OrderId);
        }
 
        Integer syncStatuc = entity.getSyncStatuc();
        if (syncStatuc != null) {
            stmt.bindLong(4, syncStatuc);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SetOpenDoor readEntity(Cursor cursor, int offset) {
        SetOpenDoor entity = new SetOpenDoor( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // openTime
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // OrderId
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3) // syncStatuc
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SetOpenDoor entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOpenTime(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOrderId(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setSyncStatuc(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SetOpenDoor entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SetOpenDoor entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}