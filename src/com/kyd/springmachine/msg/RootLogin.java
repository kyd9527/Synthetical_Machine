package com.kyd.springmachine.msg;

/**
 * µÇÂ¼1001
 * @author 8015
 *
 */
public class RootLogin {


    /**
     * machine_id : ¡°T3G6SHEW¡±
     *  secret  : ¡°jisy28b¡±
     */

    private String machine_id;
    private String secret;

    public String getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(String machine_id) {
        this.machine_id = machine_id;
    }

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
    
}
