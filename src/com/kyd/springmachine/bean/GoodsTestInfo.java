package com.kyd.springmachine.bean;

/**
 * ��������
 * @author 8015
 *
 */
public class GoodsTestInfo {
	private int number;//������
	private int goodState;//�л�,�޻�
	private int goodFault;//����״̬
	private int state;//����״̬
	
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
