package com.kyd.springmachine.msg;

import java.util.List;

/**
 * 返回,心跳包协议
 * @author 8015
 *
 */
public class ReturnHeart {
	  /**
     * timestamp : 1471708773
     * machine_state : run
     * ad_ver : 1
     * app_ver : 2
     * disable_line : 2,4,6,8,11,13
     * order_list : [{"action":"editprice","line_no":"2","goods_price":"34","id":1},{"action":"editprice","line_no":"6","goods_price":"345","id":2},{"action":"editprice","line_no":"7","goods_price":"400","id":3}]
     * result : 0
     */

    private int timestamp;
    private String machine_state;
    private int ad_ver;
    private int app_ver;
    private String disable_line;
    private int result;
    private String msg;
    
    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
     * action : editprice
     * line_no : 2
     * goods_price : 34
     * id : 1
     */

    private List<OrderListEntity> order_list;

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setMachine_state(String machine_state) {
        this.machine_state = machine_state;
    }

    public void setAd_ver(int ad_ver) {
        this.ad_ver = ad_ver;
    }

    public void setApp_ver(int app_ver) {
        this.app_ver = app_ver;
    }

    public void setDisable_line(String disable_line) {
        this.disable_line = disable_line;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setOrder_list(List<OrderListEntity> order_list) {
        this.order_list = order_list;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getMachine_state() {
        return machine_state;
    }

    public int getAd_ver() {
        return ad_ver;
    }

    public int getApp_ver() {
        return app_ver;
    }

    public String getDisable_line() {
        return disable_line;
    }

    public int getResult() {
        return result;
    }

    public List<OrderListEntity> getOrder_list() {
        return order_list;
    }

    public static class OrderListEntity {
        private String action;
        private int line_no;
        private int goods_price;
        private int id;

        public void setAction(String action) {
            this.action = action;
        }

        public void setLine_no(int line_no) {
            this.line_no = line_no;
        }

        public void setGoods_price(int goods_price) {
            this.goods_price = goods_price;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAction() {
            return action;
        }

        public int getLine_no() {
            return line_no;
        }

        public int getGoods_price() {
            return goods_price;
        }

        public int getId() {
            return id;
        }
    }
}
