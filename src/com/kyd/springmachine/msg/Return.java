package com.kyd.springmachine.msg;

/**
 * 默认返回,加上token
 * @author 8015
 *
 */
public class Return {
	
    /**
     * result : 0
     * msg : OK
     * token : token
     */

    private int result;
    private String msg;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	@Override
	public String toString() {
		return "Return [result=" + result + ", msg=" + msg + ", Token=" + token
				+ "]";
	}
    
    
}
