package com.kyd.springmachine.bean;

/**
 * 货道测试
 * @author 8015
 *
 */
public class GoodsTestInfo {
	private int number;//货道号
	private int goodState;//有货,无货
	private int goodFault;//驱动状态
	private int state;//界面状态
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getGoodState() {
		return goodState;
	}
	public void setGoodState(int goodState) {
		this.goodState = goodState;
	}
	public int getGoodFault() {
		return goodFault;
	}
	public void setGoodFault(int goodFault) {
		this.goodFault = goodFault;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
