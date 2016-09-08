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
	 * �������б�
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
	 * ɾ��Commodity��
	 */
	public void dropCommodityInfoTable() {
		CommodityInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}

	/**
	 * ɾ��GoodsInfo��
	 */
	public void dropGoodsInfoTable() {
		GoodsInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}

	/**
	 * ɾ��OrdersInfo��
	 */
	public void dropOrdersInfoTable() {
		OrdersInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}

	/**
	 * ɾ��setTimeInfo��
	 */
	public void dropSetTimeInfoTable() {
		SetTimeInfoDao.dropTable(mDaoSession.getDatabase(), true);
	}
	
	/**
	 * ɾ��adVerInfo��
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
	 * ɾ�����б�
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
	 * ����Commodity��
	 * @param entity Commodity
	 * @return id 
	 */
	public long saveCommodityInfo(CommodityInfo entity) {
		return commodityInfoDao.insertOrReplace(entity);
	}
	
	/**
	 * ����Commodity��
	 * @param entities List CommodityInfo 
	 */
	public void saveCommodityList(Iterable<CommodityInfo> entities){
		commodityInfoDao.insertInTx(entities);
	}
	
	/**
	 * ����goods��
	 * @param entity GoodsInfo
	 * @return id
	 */
	public long saveGoodsInfo(GoodsInfo entity) {
		return goodsInfoDao.insertOrReplace(entity);
	}

	/**
	 * ����GoodsInfo��
	 * @param entities List GoodsInfo 
	 */
	public void saveGoodsList(Iterable<GoodsInfo> entities){
		goodsInfoDao.insertInTx(entities);
	}
	/**
	 * ����OrdersInfo��
	 * @param entity OrdersInfo
	 * @return id
	 */
	public long saveOrdersInfo(OrdersInfo entity) {
		return ordersInfoDao.insertOrReplace(entity);
	}

	/**
	 * ����keyInfo��
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
	 * ����setopendoor��
	 * @param entity setopendoor
	 * @return
	 */
	public long saveSetOpenDoor(SetOpenDoor entity){
		return setOpenDoorDao.insertOrReplace(entity);
	}
	/**
	 * ����setTimeInfo��
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
	 * ����adVerInfo��
	 * @param entity AdVerInfo
	 * @return id
	 */
	public long saveAdVerInfo(AdVerInfo entity){
		return adVerInfoDao.insertOrReplace(entity);
	}
	
	// ����
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
	 * ���ݻ�����,ɾ��GoodsInfo
	 * @param number int
	 */
	public void dropGoodsInfoByGoogsNumber(int number){
		GoodsInfo g= queryGoodsInfoByNumber(number);
		 if(g!=null){
			 goodsInfoDao.deleteInTx(g);
		 }
	}
	/*
	 * ɾ������ʱ��
	 * */
	public long dropSetTimeInfo(SetTimeInfo entities){	
		setTimeInfoDao.deleteInTx(entities);
		return entities.getId();
	}
	/**
	 * ���ݻ�������,��ȡList<GoodsComInfo>
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
	 *���ݰ�������,��ȡList<GoodsComInfo>
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
	 * �������л����ı��
	 * @return ���л�����ŵ��б�
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
	 * ���� id��ȡKeyInfo
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
	 * ����value��ȡkeyInfo
	 * @param value the int
	 * @return KeyInfo
	 */
	public KeyInfo queryKeyInfoByValue(int value){
		QueryBuilder<KeyInfo> mqBuilder = keyInfoDao.queryBuilder();
		mqBuilder.where(KeyInfoDao.Properties.Key.eq(value));
		List<KeyInfo> entityList = mqBuilder.build().list();
		return entityList.get(0);
	}
	
	//��ȡGoodsInfo���ȫ����Ϣ
	public List<GoodsInfo> queryAllGoodsInfo(){
		return goodsInfoDao.queryBuilder().orderAsc(GoodsInfoDao.Properties.GoodsNumber).list();
	}
	
	//��ȡCommodityInfo���ȫ����Ϣ
	public List<CommodityInfo> queryAllCommodityInfo(){
		return commodityInfoDao.loadAll();
	}
	
	//��ȡSetTimeInfo���ȫ����Ϣ
	public List<SetTimeInfo> queryAllSetTimeInfo(){
		return setTimeInfoDao.loadAll();
	}
	//��ȡorder���ȫ����Ϣ
	public List<OrdersInfo> queryAllOrdersInfo(){
		return ordersInfoDao.loadAll();
	}
	
	//��ȡkeyInfo���ȫ����Ϣ
	public List<KeyInfo> queryAllKeyInfo(){
		return keyInfoDao.loadAll();
	}
	
//	//��ȡgoodsInfo���ȫ��key����Ϣ
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
	
	//����id��ȡCommodityInfo
	public CommodityInfo queryCommodityInfoById(long id){
		return commodityInfoDao.load(id);
	}
	
	/**
	 * ���ݻ����Ż�ȡGoodsInfo
	 * @param number int 
	 * @return ����null ,�з���GoodsInfo
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
	 * ������Ʒ�����ȡCommodity
	 * @param code string���͵���Ʒ���
	 * @return ����null ,�з���CommodityInfo
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
	 * ���ݻ����Ż�ȡGoodsComInfo
	 * @param number ������
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
	 * ����
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
	 * ��ȡ������к�
	 * @return
	 */
	public Integer queryMaxSerial(){
		List<CommodityInfo> com= commodityInfoDao.queryBuilder().orderDesc(CommodityInfoDao.Properties.GoodsSerial).list();
		return com.get(0).getGoodsSerial();
	}
	
	/**
	 * ��ѯ�������Ƿ��ظ�
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
	 * ���û���
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
	 * ����ȫ��
	 */
	public void disableGoodsInfo(){
		List<GoodsInfo> list=queryAllGoodsInfo();
		for (GoodsInfo goodsInfo : list) {
			goodsInfo.setGoodsDisable(GoodsDisable.Undisable.ordinal());
		}
		goodsInfoDao.insertOrReplaceInTx(list);
	}
	
	/**
	 * ��ȡȱ������Ϣ
	 * @return
	 */
	public List<GoodsInfo> queryGoodOut(){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsStatus.eq(1));
		List<GoodsInfo> entityList=mqBuilder.build().list();
		return entityList;
	}
	
	/**
	 * ���ݰ���ֵ��ȡgoodsComInfo
	 * @param key keyֵ
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
	 * ��ѯû�ϴ��Ķ�����Ϣ
	 * @return
	 */
	public List<OrdersInfo> queryNoSyncStatuc(){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.SyncStatuc.eq(SyncStatuc.Unfinished.ordinal()),OrdersInfoDao.Properties.OrderStatus.eq(OrderStatus.Success.ordinal()));
		List<OrdersInfo> entityList=mqBuilder.build().list();
		return entityList;
	}
	
	/**
	 * ��ȫ���������ó����ϴ�
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
	 * ��ȡ��������Ʒ
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
	 * �޸Ļ����۸�
	 * @param number int ������
	 * @param price  int �۸�
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
	 * ������ҳ
	 * @param page �ڼ�ҳ
	 * @return 
 	 */
	public List<OrdersInfo> queryOrderPage(int page){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder().orderDesc(OrdersInfoDao.Properties.Id);
		List<OrdersInfo> list=mqBuilder.offset(page*20).limit(20).list();
		return list;
	}
	
	/**
	 * ���ݰ���ֵ�ж��Ƿ��������
	 * @param key_value the int
	 * @return ����������true ����false
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
	 * ����id,��ȡid����ֽ�֧��
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
	 * ����id��ѯ����ʱ��
	 * @param id the id 
	 * @return String ʱ��
	 */
	public String queryOrderTime(int id){
		QueryBuilder<OrdersInfo> mqBuilder=ordersInfoDao.queryBuilder();
		mqBuilder.where(OrdersInfoDao.Properties.Id.eq(id));
		List<OrdersInfo> list=mqBuilder.build().list();
		return list.get(0).getTime();
	}
	
	/**
	 * ���ݻ�����,����һ
	 * @param goodsNumber ������
	 */
	public void updateGoodsCapacity(int goodsNumber){
		QueryBuilder<GoodsInfo> mqBuilder=goodsInfoDao.queryBuilder();
		mqBuilder.where(GoodsInfoDao.Properties.GoodsNumber.eq(goodsNumber));
		List<GoodsInfo> list=mqBuilder.build().list();
		GoodsInfo good=list.get(0);
		good.setGoodsCapacity(good.getGoodsCapacity()-1);
		goodsInfoDao.insertOrReplace(good);
	}
	/*��������״̬*/
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

	/*��ȡ���ص�ַurl��id*/
	public long queryByAdVerID(String url){
		QueryBuilder<AdVerInfo> mqBuilder=adVerInfoDao.queryBuilder();
		mqBuilder.where(AdVerInfoDao.Properties.PathUrl.eq(url));
		List<AdVerInfo> entityList=mqBuilder.build().list();
		return entityList.get(0).getId();
	}
	
	/*��ȡ���汾��*/
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
	
	/*��ȡ����ļ�·��*/
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
	/**���»���״̬�������޻�
	 * @param number �������
	 * @param goodsStatus ����״̬
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
	/**��ȡ������״̬
	 * @param number �������
	 * @return int ���ػ���״̬��0�޻���1�л�
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
	/**��ȡ���л�����״̬
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
	/**���¶���֧��״̬
	 * @param orderID ������Ϣ�����ݿ��е�ID
	 * @param orderStatus Ҫ���µ�״̬
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
	/**���¶���֧������
	 * @param orderID ������Ϣ�����ݿ��е�ID
	 * @param payType Ҫ���µ�֧������
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
	 * ��ȡ����������id
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
	 * �����ϴ���
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
	 * ��ȡȫ���Ŀ����ֽ�,��id desc;
	 * @return List<SetOpenDoor>
	 */
	public List<SetOpenDoor> getAllSetOpenDoor(){
		QueryBuilder<SetOpenDoor> mqBuilder=setOpenDoorDao.queryBuilder().orderDesc(SetOpenDoorDao.Properties.Id);
		return mqBuilder.build().list();
	}
}
