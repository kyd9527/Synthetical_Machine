package com.kyd.springmachine.msg;

import java.util.List;

/**
 * 销售记录上送(1006)
 * @author 8015
 *
 */
public class RootSale {

    /**
     * saleno : 12312431
     *  total_amount  : 2345
     *  detail_data : [{"lineno":3,"barcode ":"261261773","salenum":3,"price":350,"goods_name":"百事"," subtotal ":750},{"lineno":5,"barcode ":"418361734","salenum":5,"price":3,"goods_name":"怡宝"," subtotal ":15}]
     */

    private String saleno;
    private int total_amount;
    private int pay_channel;
    private String saletime;
    /**
     * lineno : 3
     * barcode  : 261261773
     * salenum : 3
     * price : 350
     * goods_name : 百事
     *  subtotal  : 750
     */

    private List<detailDataBean> detail_data;
    
    
    public String getSaletime() {
		return saletime;
	}


	public void setSaletime(String saletime) {
		this.saletime = saletime;
	}


	public int getPay_channel() {
		return pay_channel;
	}


	public void setPay_channel(int pay_channel) {
		this.pay_channel = pay_channel;
	}


	public String getSaleno() {
		return saleno;
	}


	public void setSaleno(String saleno) {
		this.saleno = saleno;
	}


	public int getTotal_amount() {
		return total_amount;
	}


	public void setTotal_amount(int total_amount) {
		this.total_amount = total_amount;
	}


	public List<detailDataBean> getDetail_data() {
		return detail_data;
	}


	public void setDetail_data(List<detailDataBean> detail_data) {
		this.detail_data = detail_data;
	}


	public static  class detailDataBean{

        /**
         * lineno : 3
         * barcode  : 261261773
         * salenum : 3
         * price : 350
         * goods_name : 百事
         *  subtotal  : 750
         */

        private int lineno;
        private String barcode;
        private int salenum;
        private int price;
        private String goods_name;
        private int subtotal;

        public int getLineno() {
            return lineno;
        }

        public void setLineno(int lineno) {
            this.lineno = lineno;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public int getSalenum() {
            return salenum;
        }

        public void setSalenum(int salenum) {
            this.salenum = salenum;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getGoods_name() {
            return goods_name;
        }

		public int getSubtotal() {
			return subtotal;
		}

		public void setSubtotal(int subtotal) {
			this.subtotal = subtotal;
		}

		public void setGoods_name(String goods_name) {
			this.goods_name = goods_name;
		}
        
    }
}
