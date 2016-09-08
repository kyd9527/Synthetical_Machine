package com.kyd.springmachine.msg;

import java.util.List;

public class ReturnCommodityInfo {
	 /**
     * result : 0
     * detail_data : [{"id":1,"goods_barcode":"6935718000148","goods_name":"ÈóÌï","goods_price":1},{"id":2,"goods_barcode":"6926657290202","goods_name":"ÌùµØ°åµÄ","goods_price":5}]
     */

    private int result;
    private String msg;
    /**
     * id : 1
     * goods_barcode : 6935718000148
     * goods_name : ÈóÌï
     * goods_price : 1
     */

    private List<DetailDataBean> detail_data;

    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<DetailDataBean> getDetail_data() {
        return detail_data;
    }

    public void setDetail_data(List<DetailDataBean> detail_data) {
        this.detail_data = detail_data;
    }

    public static class DetailDataBean {
        private int id;
        private String goods_barcode;
        private String goods_name;
        private int goods_price;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGoods_barcode() {
            return goods_barcode;
        }

        public void setGoods_barcode(String goods_barcode) {
            this.goods_barcode = goods_barcode;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public int getGoods_price() {
            return goods_price;
        }

        public void setGoods_price(int goods_price) {
            this.goods_price = goods_price;
        }
    }
}
