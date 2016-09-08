package com.kyd.springmachine.database;

import java.io.LineNumberInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wlf.filedownloader.base.Log;

import android.content.Context;

import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.springmachine.AppConfig.GoodsDisable;
import com.kyd.springmachine.AppConfig.GoodsStatus;
import com.kyd.springmachine.AppConfig.OrderStatus;
import com.kyd.springmachine.AppConfig.PayType;
import com.kyd.springmachine.AppConfig.SyncStatuc;
import com.kyd.springmachine.ControlApp;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.bean.KeyTestInfo;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.greendao.AdVerInfo;
import de.greenrobot.greendao.AdVerInfoDao;
import de.greenrobot.greendao.CommodityInfo;
import de.greenrobot.greendao.CommodityInfoDao;
import de.greenrobot.greendao.DaoSession;
import de.greenrobot.greendao.GoodsInfo;
import de.greenrobot.greendao.GoodsInfoDao;
import de.greenrobot.greendao.KeyInfo;
import de.greenrobot.greendao.KeyInfoDao;
import de.greenrobot.greendao.OrdersInfo;
import de.greenrobot.greendao.OrdersInfoDao;
import de.greenrobot.greendao.SetOpenDoor;
import de.greenrobot.greendao.SetOpenDoorDao;
import de.greenrobot.greendao.SetTimeInfo;
import de.greenrobot.greendao.SetTimeInfoDao;

public class DataBaseManager {
	private static DataBaseManager instance;

	private static Context appContext;

	private DaoSession mDaoSession;
	private CommodityInfoDao commodityInfoDao;
	private GoodsInfoDao goodsInfoDao;
	private OrdersInfoDao ordersInfoDao;
	private SetTimeInfoDao setTimeInfoDao;
	private AdVerInfoDao adVerInfoDao;
	private KeyInfoDao keyInfoDao;
	private SetOpenDoorDao setOpenDoorDao;

	private DataBaseManager() {
	}

	public static DataBaseManager getInstance(Context context) {
		if (instance == null) {
			instance = new DataBaseManager();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = ControlApp.getDaoSession(context);
			instance.commodityInfoDao = instance.mDaoSession.getCommodityInfoDao();
			instance.goodsInfoDao = instance.mDaoSession.getGoodsInfoDao();
			instance.ordersInfoDao = instance.mDaoSession.getOrdersInfoDao();
			instance.setTimeInfoDao = instance.mDaoSession.getSetTimeInfoDao();
			instance.adVerInfoDao = instance.mDaoSession.getAdVerInfoDao();
			instance.keyInfoDao=instance.mDaoSession.getKeyInfoDao();
			instance.setOpenDoorDao=instance.mDaoSession.getSetOpenDoorDao();
		}
		return instance;
	}
	
	/**
	 * 创建所有表
	 */
	public void createAllTable() {
		CommodityInfoDao.createTable(mDaoSession.getDatabase(), true);
		GoodsInfoDao.createTable(mDaoSession.getDatabase(), true);
		OrdersInfoDao.createTable(mDaoSession.getDatabase(), true);
		SetTimeInfoDao.createTable(mDaoSession.getDatabase(), true);
		AdVerInfoDao.createTable(mDaoSession.getDatabase(), true);
		KeyInfoDao.createTable(mDaoSession.getDatabase(), true);
		SetOpenDoorDao.createTable(mDaoSession.getDatabase(), true);
	}
	
	/**
	 * 删除Commodity表
	 */
	public void dropCommodityInfoTable() {
		CommodityInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}

	/**
	 * 删除GoodsInfo表
	 */
	public void dropGoodsInfoTable() {
		GoodsInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}

	/**
	 * 删除OrdersInfo表
	 */
	public void dropOrdersInfoTable() {
		OrdersInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}

	/**
	 * 删除setTimeInfo表
	 */
	public void dropSetTimeInfoTable() {
		SetTimeInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}
	
	/**
	 * 删除adVerInfo表
	 */
	public void dropAdVerInfoTable(){
		AdVerInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}
	
	public void dropKeyInfoTable(){
		KeyInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}
	
	public void dropSetOpenDoorTable(){
		SetOpenDoorDao.dropTable(mDaoSession.getDatabase(), true);
	}
	/**
	 * 删除所有表
	 */
	public void dropAllTable() {
		CommodityInfoDao.dropTable(mDaoSession.getDatabase(), true);
		GoodsInfoDao.dropTable(mDaoSession.getDatabase(), true);
		OrdersInfoDao.dropTable(mDaoSession.getDatabase(), true);
		SetTimeInfoDao.dropTable(mDaoSession.getDatabase(), true);
		AdVerInfoDao.dropTable(mDaoSession.getDatabase(), true);
		KeyInfoDao.dropTable(mDaoSession.getDatabase(), true);
		SetOpenDoorDao.dropTable(mDaoSession.getDatabase(), true);
	}
	
	/**
	 * 插入Commodity表
	 * @param entity Commodity
	 * @return id 
	 */
	public long saveCommodityInfo(CommodityInfo entity) {
		return commodityInfoDao.insertOrReplace(entity);
	}
	
	/**
	 * 插入Commodity表
	 * @param entities List CommodityInfo 
	 */
	public void saveCommodityList(Iterable<CommodityInfo> entities){
		commodityInfoDao.insertInTx(entities);
	}
	
	/**
	 * 插入goods表
	 * @param entity GoodsInfo
	 * @return id
	 */
	public long saveGoodsInfo(GoodsInfo entity) {
		return goodsInfoDao.insertOrReplace(entity);
	}

	/**
	 * 插入GoodsInfo表
	 * @param entities List GoodsInfo 
	 */
	public void saveGoodsList(Iterable<GoodsInfo> entities){
		goodsInfoDao.insertInTx(entities);
	}
	/**
	 * 插入OrdersInfo表
	 * @param entity OrdersInfo
	 * @return id
	 */
	public long saveOrdersInfo(OrdersInfo entity) {
		return ordersInfoDao.insertOrReplace(entity);
	}

	/**
	 * 插入keyInfo表
	 * @param entInfo KeyInfo
	 * @return
	 */
	public long saveKeyInfo(KeyInfo entInfo){
		return keyInfoDao.insertOrReplace(entInfo);
	}
	
	public void saveKeyInfoList(Iterable<KeyInfo> entities){
		keyInfoDao.insertInTx(entities);
	}
	
	/**
	 * 插入setopendoor表
	 * @param entity setopendoor
	 * @return
	 */
	public long saveSetOpenDoor(SetOpenDoor entity){
		return setOpenDoorDao.insertOrReplace(entity);
	}
	/**
	 * 插入setTimeInfo表
	 * @param entity SetTimeInfo
	 * @return id
	 */
	public long saveSetTimeInfo(SetTimeInfo entity) {
		int flag=0;
		List<SetTimeInfo> setTimeInfoList=queryAllSetTimeInfo();
		SimpleDateFormat sdf=new SimpleDateFormat(AbDateUtil.dateFormatHM);
		Date dateStart = null;
		Date dateEnd = null;
		
		Date dateStartList = null;
		Date dateEndList = null;
		try {
			dateStart=sdf.parse(entity.getStartTime());
			dateEnd=sdf.parse(entity.getEndTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (SetTimeInfo setTimeInfo : setTimeInfoList) {
			try {
				dateStartList=sdf.parse(setTimeInfo.getStartTime());
				dateEndList=sdf.parse(setTimeInfo.getEndTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if((dateStartList.compareTo(dateStart)<0)&&(dateStart.compareTo(dateEndList)<0)||((dateStartList.compareTo(dateEnd)<0)&&(dateEnd.compareTo(dateEndList)<0))){
		//		System.out.println("-----");
				flag++;
				break;
			}else {
			//	System.out.println("true");
				//flag=true;
			}
		}
		if ((flag==0)||setTimeInfoList.size()==0) {
			return setTimeInfoDao.insertOrReplace(entity);
		}
		return 0;
	}
	
	/**
	 * 插入adVerInfo表
	 * @param entity AdVerInfo
	 * @return id
	 */
	public long saveAdVerInfo(AdVerInfo entity){
		return adVerInfoDao.insertOrReplace(entity);
	}
	
	// 数量
	public long getGoodsInfoCount() {

		return goodsInfoDao.count();
	}

	public long getCommodityInfoCount() {
		return commodityInfoDao.count();
	}

	public long getOrdersInfoCount() {
		return ordersInfoDao.count();
	}
		
	public long getAdVerInfoCount(){
		return adVerInfoDao.count();
	}
		
	public long getSetTimeInfoCount(){
		return setTimeInfoDao.count();
	}
	//end
	
	/**
	 * 根据货道号,删除GoodsInfo
	 * @param number int
	 */
	public void dropGoodsInfoByGoogsNumber(int number){
		GoodsInfo g= queryGoodsInfoByNumber(number);
		 if(g!=null){
			 goodsInfoDao.deleteInTx(g);
		 }
	}
	/*
	 * 删除节能时间
	 * */
	public long dropSetTimeInfo(SetTimeInfo entities){	
		setTimeInfoDao.deleteInTx(entities);
		return entities.getId();
	}
	/**
	 * 依据货道数量,获取List<GoodsComInfo>
	 * @return  List<GoodsComInfo>
	 */
	public List<GoodsComInfo> queryAllGoodsComInfoByGoodsNumber(){
		List<GoodsInfo> list=queryAllGoodsInfo();
		List<GoodsComInfo> listAll=new ArrayList<GoodsComInfo>();
		for (GoodsInfo goodsInfo : list) {
			GoodsComInfo good=new GoodsComInfo();
			CommodityInfo com= queryCommodityInfoById(goodsInfo.getGoodsID());
			KeyInfo keyInfo= queryKeyInfoById(goodsInfo.getKeyID());
			good.setGoodsId(goodsInfo.getId());
			good.setComId(com.getId());
			good.setGoodsCode(com.getGoodsCode());
			good.setCommType(com.getCommType());
			good.setGoodsName(com.getName());
			good.setGoodsPrice(com.getPrice());
			good.setGoodsCapacity(goodsInfo.getGoodsCapacity());
			good.setGoodsKey(keyInfo.getKey());
			good.setGoodsNumber(goodsInfo.getGoodsNumber());
			good.setGoodsStatus(goodsInfo.getGoodsStatus());
			good.setGoodsStock(goodsInfo.getGoodsStock());
			good.setGoodsPic(com.getImgPath());
			good.setGoodsDisable(goodsInfo.getGoodsDisable());
			listAll.add(good);
		}
		return listAll;
	}
	
	/**
	 *依据按键数量,获取List<GoodsComInfo>
	 * @return  List<GoodsComInfo>
	 */
	public List<GoodsComInfo> queryAllGoodsComInfoByKey(){
	//	List<GoodsInfo> list=query;
		List<KeyInfo> list=queryAllKeyInfo();
		List<GoodsComInfo> listAll=new ArrayList<GoodsComInfo>();
		
		for (KeyInfo keyInfo : list) {
			GoodsComInfo com= queryGoodsComInfoByKey(keyInfo.getKey());
			if (com!=null) {
				listAll.add(com);
			}
		}
		return listAll;
	}
	
	/**
	 * 查找所有货道的编号
	 * @return 所有货道编号的列表
	 * */
	
	public List<Integer> queryAllGoodsNum(){
		List<Integer> list = new ArrayList<Integer>();		
		List<GoodsInfo> goodslist = goodsInfoDao.loadAll();
		for(GoodsInfo gif:goodslist){
			list.add(gif.getGoodsNumber());		
		}
		return list;
	}
	
	
	/**
	 * 根据 id获取KeyInfo
	 * @param id the long
	 * @return KeyInfo
	 */
	public KeyInfo queryKeyInfoById(long id){
		QueryBuilder<KeyInfo> mqBuilder = keyInfoDao.queryBuilder();
		mqBuilder.where(KeyInfoDao.Properties.Id.eq(id));
		List<KeyInfo> entityList = mqBuilder.build().list();
		return entityList.get(0);
	}
	
	/**
	 * 根据value获取keyInfo
	 * @param value the int
	 * @return KeyInfo
	 */
	public KeyInfo queryKeyInfoByValue(int value){
		QueryBuilder<KeyInfo> mqBuilder = keyInfoDao.queryBuilder();
		mqBuilder.where(KeyInfoDao.Properties.Key.eq(value));
		List<KeyInfo> entityList = mqBuilder.build().list();
		return entityList.get(0);
	}
	
	//获取GoodsInfo表的全部信息
	public List<GoodsInfo> queryAllGoodsInfo(){
		return goodsInfoDao.queryBuilder().orderAsc(GoodsInfoDao.Properties.GoodsNumber).list();
	}
	
	//获取CommodityInfo表的全部信息
	public List<CommodityInfo> queryAllCommodityInfo(){
		return commodityInfoDao.loadAll();
	}
	
	//获取SetTimeInfo表的全部信息
	public List<SetTimeInfo> queryAllSetTimeInfo(){
		return setTimeInfoDao.loadAll();
	}
	//获取order表的全部信息
	public List<OrdersInfo> queryAllOrdersInfo(){
		return ordersInfoDao.loadAll();
	}
	
	//获取keyInfo表的全部信息
	public List<KeyInfo> queryAllKeyInfo(){
		return keyInfoDao.loadAll();
	}
	
//	//获取goodsInfo表的全部key的信息
//	public List<KeyTestInfo> queryAllKeyInfo(){
//		List<GoodsInfo> list=queryAllGoodsInfo();
//		List<KeyTestInfo> listKey=new ArrayList<KeyTestInfo>();
//		for (GoodsInfo goodsInfo : list) {
//			KeyTestInfo key=new KeyTestInfo();
//			key.setNumber(goodsInfo.getGoodsKey());
//			listKey.add(key);
//		}
//		return listKey;
//	}
	
	//根据id获取CommodityInfo
	public CommodityInfo queryCommodityInfoById(long id){
		return commodityInfoDao.load(id);
	}
	
	/**
	 * 根据货道号获取GoodsInfo
	 * @param number int 
	 * @return 无是null ,有返回GoodsInfo
	 */
	public GoodsInfo queryGoodsInfoByNumber(int number){
		QueryBuilder<GoodsInfo> mqBuilder = goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(number));
		List<GoodsInfo> entityList = mqBuilder.build().list();
		if (entityList.size()==0) {
			return null;
		}
		return entityList.get(0);
	}
	
	/**
	 * 根据商品编码获取Commodity
	 * @param code string类型的商品编号
	 * @return 无是null ,有返回CommodityInfo
	 */
	public CommodityInfo queryCommodityInfoByCode(String code){
		QueryBuilder<CommodityInfo> mqBuilder = commodityInfoDao.queryBuilder();
		mqBuilder.where(CommodityInfoDao.Properties.GoodsCode.eq(code));
		List<CommodityInfo> entityList = mqBuilder.build().list();
		if (entityList.size()==0) {
			return null;
		}
		return entityList.get(0);
	}
	
	/**
	 * 根据货道号获取GoodsComInfo
	 * @param number 货道号
	 * @return 
	 */
	public GoodsComInfo queryGoodsComInfoByGoodsNumber(int number){
		GoodsInfo goodsInfo=queryGoodsInfoByNumber(number);
		GoodsComInfo good=new GoodsComInfo();
	//	System.out.println("goodsInfo.getGoodsID()    " +goodsInfo.getGoodsID());
		CommodityInfo com= queryCommodityInfoById(goodsInfo.getGoodsID());
	//	System.out.println("com     " +com.getId());
		KeyInfo keyInfo= queryKeyInfoById(goodsInfo.getKeyID());
		good.setGoodsId(goodsInfo.getId());
		good.setComId(com.getId());
		good.setGoodsCode(com.getGoodsCode());
		good.setCommType(com.getCommType());
		good.setGoodsName(com.getName());
		good.setGoodsPrice(com.getPrice());
		good.setGoodsCapacity(goodsInfo.getGoodsCapacity());
		good.setGoodsKey(keyInfo.getKey());
		good.setGoodsNumber(goodsInfo.getGoodsNumber());
		good.setGoodsStatus(goodsInfo.getGoodsStatus());
		good.setGoodsStock(goodsInfo.getGoodsStock());
		good.setGoodsPic(com.getImgPath());
		good.setGoodsDisable(goodsInfo.getGoodsDisable());
		return good;
	}
	
	/**
	 * 补货
	 */
	public void updateGoodsMax(){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		List<GoodsInfo> entityList=mqBuilder.build().list();
		for (GoodsInfo goodsInfo : entityList) {
			goodsInfo.setGoodsCapacity(goodsInfo.getGoodsStock());
		}
		goodsInfoDao.insertOrReplaceInTx(entityList);
	}
	
	/**
	 * 获取最大序列号
	 * @return
	 */
	public Integer queryMaxSerial(){
		List<CommodityInfo> com= commodityInfoDao.queryBuilder().orderDesc(CommodityInfoDao.Properties.GoodsSerial).list();
		return com.get(0).getGoodsSerial();
	}
	
	/**
	 * 查询订单号是否重复
	 * @param number
	 * @return
	 */
	public boolean queryOrderNumber(String number){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.OrderNum.eq(number));
		List<OrdersInfo> entityList=mqBuilder.build().list();
		if (entityList.size()==0) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * 禁用货道
	 * @return
	 */
	public void disableGoodNumber(int GoodsNumber){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(GoodsNumber));
		List<GoodsInfo> entityList=mqBuilder.build().list();
		GoodsInfo goodsInfo=entityList.get(0);
		goodsInfo.setGoodsDisable(GoodsDisable.Disable.ordinal());
     	goodsInfoDao.insertOrReplace(goodsInfo);
	}
	
	/**
	 * 禁用全清
	 */
	public void disableGoodsInfo(){
		List<GoodsInfo> list=queryAllGoodsInfo();
		for (GoodsInfo goodsInfo : list) {
			goodsInfo.setGoodsDisable(GoodsDisable.Undisable.ordinal());
		}
		goodsInfoDao.insertOrReplaceInTx(list);
	}
	
	/**
	 * 获取缺货的信息
	 * @return
	 */
	public List<GoodsInfo> queryGoodOut(){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsStatus.eq(1));
		List<GoodsInfo> entityList=mqBuilder.build().list();
		return entityList;
	}
	
	/**
	 * 根据按键值获取goodsComInfo
	 * @param key key值
	 * @return 
	 */
	public GoodsComInfo queryGoodsComInfoByKey(int key){
		GoodsInfo goodsInfo;
		QueryBuilder<GoodsInfo> mqBuilder = goodsInfoDao.queryBuilder();
		KeyInfo keyInfo=queryKeyInfoByValue(key);
		mqBuilder.where(GoodsInfoDao.Properties.KeyID.eq(keyInfo.getId()));
		List<GoodsInfo> entityList = mqBuilder.build().list();
		if (entityList.size()==0) {
			return null;
		}
		if (entityList.size()>=2) {
		//	System.out.println("entityList.size()    "+key);
			goodsInfo=queryMaxGoodsCapacity(keyInfo.getId());
		}else {
			goodsInfo=entityList.get(0);
		}
		GoodsComInfo good=new GoodsComInfo();
		CommodityInfo com= queryCommodityInfoById(goodsInfo.getGoodsID());
		good.setComId(com.getId());
		good.setGoodsId(goodsInfo.getId());
		good.setGoodsCode(com.getGoodsCode());
		good.setCommType(com.getCommType());
		good.setGoodsName(com.getName());
		good.setGoodsPrice(com.getPrice());
		good.setGoodsCapacity(goodsInfo.getGoodsCapacity());
		good.setGoodsKey(key);
		good.setGoodsNumber(goodsInfo.getGoodsNumber());
		good.setGoodsStatus(goodsInfo.getGoodsStatus());
		good.setGoodsStock(goodsInfo.getGoodsStock());
		good.setGoodsPic(com.getImgPath());
		good.setGoodsDisable(goodsInfo.getGoodsDisable());
		return good;
	}
	
	/**
	 * 查询没上传的订单信息
	 * @return
	 */
	public List<OrdersInfo> queryNoSyncStatuc(){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.SyncStatuc.eq(SyncStatuc.Unfinished.ordinal()),OrdersInfoDao.Properties.OrderStatus.eq(OrderStatus.Success.ordinal()));
		List<OrdersInfo> entityList=mqBuilder.build().list();
		return entityList;
	}
	
	/**
	 * 把全部订单设置成已上传
	 */
	public void updateOrderSyncStatuc(){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.SyncStatuc.eq(SyncStatuc.Unfinished.ordinal()));
		List<OrdersInfo> entityList=mqBuilder.build().list();
		for (OrdersInfo ordersInfo : entityList) {
			ordersInfo.setSyncStatuc(SyncStatuc.Finished.ordinal());
		}
		ordersInfoDao.insertOrReplaceInTx(entityList);
	}
	
	/**
	 * 获取最多库存的商品
	 * @param id
	 * @return
	 */
	public GoodsInfo queryMaxGoodsCapacity(long id){
		QueryBuilder<GoodsInfo> mqBuilder = goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.KeyID.eq(id),GoodsInfoDao.Properties.GoodsDisable.eq(GoodsDisable.Undisable.ordinal()),GoodsInfoDao.Properties.GoodsStatus.eq(GoodsStatus.In_Stock.ordinal()));
		mqBuilder.orderDesc(GoodsInfoDao.Properties.GoodsCapacity);
		List<GoodsInfo> entityList=mqBuilder.build().list();
		if (entityList.size()!=0) {
		//	AbToastUtil.showToast(appContext, entityList.get(0).getGoodsNumber()+"   "+entityList.get(0).getGoodsDisable());
		//	System.out.println(entityList.get(0).getGoodsNumber()+"   "+entityList.get(0).getGoodsDisable());
			return entityList.get(0);
		} else {
			QueryBuilder<GoodsInfo> mqBu = goodsInfoDao.queryBuilder();
			mqBu.where(GoodsInfoDao.Properties.KeyID.eq(id));
			mqBu.orderDesc(GoodsInfoDao.Properties.GoodsCapacity);
			return mqBu.build().list().get(0);
		}
	}
	
	/**
	 * 修改货道价格
	 * @param number int 货道号
	 * @param price  int 价格
	 */
	public void updateGoodsPrice(int number,int price){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(number));
		List<GoodsInfo> entityList=mqBuilder.build().list();
		
		QueryBuilder<CommodityInfo> mqBuil=commodityInfoDao.queryBuilder();
		mqBuil.where(CommodityInfoDao.Properties.Id.eq(entityList.get(0).getGoodsID()));
		List<CommodityInfo> entity=mqBuil.build().list();
		CommodityInfo com=entity.get(0);
		com.setPrice(price);
		commodityInfoDao.insertOrReplace(com);
	}
	
	/**
	 * 订单分页
	 * @param page 第几页
	 * @return 
 	 */
	public List<OrdersInfo> queryOrderPage(int page){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder().orderDesc(OrdersInfoDao.Properties.Id);
		List<OrdersInfo> list=mqBuilder.offset(page*20).limit(20).list();
		return list;
	}
	
	/**
	 * 根据按键值判断是否可以销售
	 * @param key_value the int
	 * @return 可以销售是true 否则false
	 */
	public boolean queryGoodsStatuByKey(int key_value){
		KeyInfo keyInfo= queryKeyInfoByValue(key_value);
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsDisable.eq(GoodsDisable.Undisable.ordinal()),GoodsInfoDao.Properties.GoodsStatus.eq(GoodsStatus.In_Stock.ordinal()),GoodsInfoDao.Properties.KeyID.eq(keyInfo.getId()));
		List<GoodsInfo> entityList=mqBuilder.build().list();
		if (entityList.size()>0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据id,获取id后的现金支付
	 * @param id the old id
	 * @param id the new id
	 * @return List<OrdersInfo>
	 */
	public int queryOrderCash(int oldId,int newId){
		int cashNumber=0;
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(mqBuilder.and(OrdersInfoDao.Properties.Id.gt(oldId),OrdersInfoDao.Properties.Id.le(newId), OrdersInfoDao.Properties.PayType.eq(PayType.CASH_PAY.ordinal()),OrdersInfoDao.Properties.OrderStatus.eq(OrderStatus.Success.ordinal())));
		List<OrdersInfo> list=mqBuilder.build().list();
		if (list!=null) {
			for (OrdersInfo ordersInfo : list) {
				cashNumber=cashNumber+ordersInfo.getTotalPrice();
			}
		}
		return cashNumber;
	}
	
	/**
	 * 根据id查询订单时间
	 * @param id the id 
	 * @return String 时间
	 */
	public String queryOrderTime(int id){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.Id.eq(id));
		List<OrdersInfo> list=mqBuilder.build().list();
		return list.get(0).getTime();
	}
	
	/**
	 * 根据货道号,库存减一
	 * @param goodsNumber 货道号
	 */
	public void updateGoodsCapacity(int goodsNumber){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(goodsNumber));
		List<GoodsInfo> list=mqBuilder.build().list();
		GoodsInfo good=list.get(0);
		good.setGoodsCapacity(good.getGoodsCapacity()-1);
		goodsInfoDao.insertOrReplace(good);
	}
	/*更换下载状态*/
	public void replaceByAdVerState(long id,String fileDir,String fileName){
		QueryBuilder<AdVerInfo> mqBuilder=adVerInfoDao.queryBuilder();
		mqBuilder.where(AdVerInfoDao.Properties.Id.eq(id));
		List<AdVerInfo> entityList=mqBuilder.build().list();
		AdVerInfo adVerInfo=entityList.get(0);
		adVerInfo.setStatus(1);
		adVerInfo.setFileName(fileName);
		adVerInfo.setFileDir(fileDir);
		adVerInfoDao.insertOrReplace(adVerInfo);
	}

	/*获取下载地址url的id*/
	public long queryByAdVerID(String url){
		QueryBuilder<AdVerInfo> mqBuilder=adVerInfoDao.queryBuilder();
		mqBuilder.where(AdVerInfoDao.Properties.PathUrl.eq(url));
		List<AdVerInfo> entityList=mqBuilder.build().list();
		return entityList.get(0).getId();
	}
	
	/*获取广告版本号*/
	public int  getAdVerNumber(){
		List<AdVerInfo> list=adVerInfoDao.queryBuilder().orderDesc(AdVerInfoDao.Properties.AdNumber).list();
		for (AdVerInfo adVerInfo : list) {
			int number=adVerInfo.getAdNumber();
			QueryBuilder<AdVerInfo> mqBuilder=adVerInfoDao.queryBuilder();
			mqBuilder.where(AdVerInfoDao.Properties.Status.eq(0), AdVerInfoDao.Properties.AdNumber.eq(number));
			if(mqBuilder.build().list().size()==0){
			      return number;
		    }
		}
		return 0;
	}
	
	/*获取广告文件路径*/
	public List<String> getAdVerFile(){
		List<String> listFile=new ArrayList<String>();
		QueryBuilder<AdVerInfo> mqBuilder=adVerInfoDao.queryBuilder();
		mqBuilder.where(AdVerInfoDao.Properties.AdNumber.eq(getAdVerNumber()));
		List<AdVerInfo> entityList=mqBuilder.build().list();
		for (AdVerInfo adVerInfo : entityList) {
			listFile.add(adVerInfo.getFileDir()+"/"+adVerInfo.getFileName());
		}
		return listFile;
	}
	/**更新货道状态，如有无货
	 * @param number 货道编号
	 * @param goodsStatus 货道状态
	 * */
	public void replaceGoodsStatus(int number,int goodsStatus){
		QueryBuilder<GoodsInfo> mqBuilder = goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(number));
		List<GoodsInfo> entityList = mqBuilder.build().list();
		if (entityList.size()!=0) {
			GoodsInfo gsf = entityList.get(0);
			gsf.setGoodsStatus(goodsStatus);
			goodsInfoDao.insertOrReplace(gsf);
		}
	}
	/**获取货道的状态
	 * @param number 货道编号
	 * @return int 返回货道状态，0无货，1有货
	 * */
	
	
	public int getGoodsStatus(int number){
		QueryBuilder<GoodsInfo> mqBuilder = goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(number));
		List<GoodsInfo> entityList = mqBuilder.build().list();
		if (entityList.size()!=0) {
			return entityList.get(0).getGoodsStatus();
		}else{
			return 0;
		}
	}
	/**获取所有货道的状态
	 * @return List<Integer>
	 * 
	 * **/
	public List<Integer> getGoodsStatusList(){
		List<Integer> list =  new ArrayList<Integer>();
		List<GoodsInfo> goodsInfo = goodsInfoDao.loadAll();
		for(GoodsInfo goods:goodsInfo){			
			list.add(goods.getGoodsStatus());
		}
		return list;
		
	}
	/**更新订单支付状态
	 * @param orderID 订单信息在数据库中的ID
	 * @param orderStatus 要更新的状态
	 * */
	
	
	public void replaceOrdersInfoByID(long orderID,int orderStatus){
		QueryBuilder<OrdersInfo> mqBuilder = ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.Id.eq(orderID));
		List<OrdersInfo> entityList = mqBuilder.build().list();
		if (entityList.size()!=0) {
			OrdersInfo oif = entityList.get(0);
			oif.setOrderStatus(orderStatus);
			ordersInfoDao.insertOrReplace(oif);
		}
	}
	/**更新订单支付类型
	 * @param orderID 订单信息在数据库中的ID
	 * @param payType 要更新的支付类型
	 * */
	
	public void replaceOrdersInfoByPayType(long orderID,int payType){
		QueryBuilder<OrdersInfo> mqBuilder = ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.Id.eq(orderID));
		List<OrdersInfo> entityList = mqBuilder.build().list();
		if (entityList.size()!=0) {
			OrdersInfo oif = entityList.get(0);
			oif.setPayType(payType);
			ordersInfoDao.insertOrReplace(oif);
		}
	}
	
	/**
	 * 获取订单表的最大id
	 * @return id
	 */
	public Integer getOrdersInfoMaxId(){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder().orderDesc(OrdersInfoDao.Properties.Id);
		List<OrdersInfo> list=mqBuilder.build().list();
		if (list.size()!=0) {
			return mqBuilder.build().list().get(0).getId().intValue();
		}else {
			return 0;
		}
	}
	
	/**
	 * 开门上传了
	 */
	public void updateSetOpenDoorSyncStatuc(){
		QueryBuilder<SetOpenDoor> mqBuilder=setOpenDoorDao.queryBuilder();
		List<SetOpenDoor> entityList=mqBuilder.build().list();
		for (SetOpenDoor setOpenDoor : entityList) {
			setOpenDoor.setSyncStatuc(SyncStatuc.Finished.ordinal());
		}
		setOpenDoorDao.insertOrReplaceInTx(entityList);
	}
	
	/**
	 * 获取全部的开门现金,按id desc;
	 * @return List<SetOpenDoor>
	 */
	public List<SetOpenDoor> getAllSetOpenDoor(){
		QueryBuilder<SetOpenDoor> mqBuilder=setOpenDoorDao.queryBuilder().orderDesc(SetOpenDoorDao.Properties.Id);
		return mqBuilder.build().list();
	}
}
