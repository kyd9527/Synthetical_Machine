package com.kyd.springmachine.msg;

public class ReturnAliPay {


    /**
     * result : 0
     * msg : ¡±OK¡±
     *  code_url  : ¡± weixin£º
     */

    private int result;
    private String msg;
    private String code_url;
    private String saleno;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

	public String getCode_url() {
		return code_url;
	}

	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}

	public String getSaleno() {
		return saleno;
	}

	public void setSaleno(String saleno) {
		this.saleno = saleno;
	}

}
