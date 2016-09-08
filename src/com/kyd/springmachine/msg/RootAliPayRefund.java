package com.kyd.springmachine.msg;

public class RootAliPayRefund {

	private String saleno;
	private int refund_amount;
	private String refund_reason;
	public String getSaleno() {
		return saleno;
	}
	public void setSaleno(String saleno) {
		this.saleno = saleno;
	}
	public int getRefund_amount() {
		return refund_amount;
	}
	public void setRefund_amount(int refund_amount) {
		this.refund_amount = refund_amount;
	}
	public String getRefund_reason() {
		return refund_reason;
	}
	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}
	
}
