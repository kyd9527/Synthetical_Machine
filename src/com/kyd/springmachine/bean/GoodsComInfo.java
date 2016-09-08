package com.kyd.springmachine.bean;

import java.io.Serializable;

/**
 * ���ݿ��goodsInfo��CommodityInfo��������Ϣ
 * 
 * @author 8015
 * 
 */
public class GoodsComInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private long goodsId;
	private long ComId;
	private int goodsNumber;//������
	private int goodsStock;//����
	private int goodsCapacity;//���
	private int goodsStatus;//״̬
	private int goodsKey;//����ֵ
	private String goodsCode;//��Ʒ����
	private int commType;//��Ʒ����
	private String goodsName;//��Ʒ����
	private int goodsPrice;//��Ʒ�۸�
	private String goodsPic;//��ƷͼƬ
	private int carNumber;//���ﳵ
	private int goodsDisable;//��������
	public int getGoodsDisable() {
		return goodsDisable;
	}
	public void setGoodsDisable(int goodsDisable) {
		this.goodsDisable = goodsDisable;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	public long getComId() {
		return ComId;
	}
	public void setComId(long comId) {
		ComId = comId;
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
	public int getGoodsKey() {
		return goodsKey;
	}
	public void setGoodsKey(int goodsKey) {
		this.goodsKey = goodsKey;
	}
	public String getGoodsCode() {
		return goodsCode;
	}
	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}
	public int getCommType() {
		return commType;
	}
	public void setCommType(int commType) {
		this.commType = commType;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(int goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public String getGoodsPic() {
		return goodsPic;
	}
	public void setGoodsPic(String goodsPic) {
		this.goodsPic = goodsPic;
	}
	public int getCarNumber() {
		return carNumber;
	}
	public void setCarNumber(int carNumber) {
		this.carNumber = carNumber;
	}
}
