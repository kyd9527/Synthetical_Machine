package com.kyd.springmachine.msg;

public class ReturnAliPayState {
	  /**
     * result : 0
     * state : 0
     * msg : ¡±OK¡±
     */

    private int result;
    private int state;
    private String msg;
    private int channel;
    
    

    public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
