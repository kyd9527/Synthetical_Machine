package com.kyd.springmachine.msg;

import java.util.List;

public class RootAliPay {



    /**
     * saleno : test102120322016071200000018
     * total_amount : 1
     * pay_channel : 1
     * saletime : 2016-07-19 17:29:00
     * detail_data : [{"lineno":1,"barcode":"261261773","salenum":1,"price":1,"goods_name":"矿泉水","subtotal":"322"},{"lineno":2,"barcode":"261261773","salenum":1,"price":1,"goods_name":"王老吉","subtotal":"122"}]
     */

    private String saleno;
    private int total_amount;
    private int pay_channel;
    private String saletime;
    /**
     * lineno : 1
     * barcode : 261261773
     * salenum : 1
     * price : 1
     * goods_name : 矿泉水
     * subtotal : 322
     */

    private List<DetailDataBean> detail_data;

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

    public int getPay_channel() {
        return pay_channel;
    }

    public void setPay_channel(int pay_channel) {
        this.pay_channel = pay_channel;
    }

    public String getSaletime() {
        return saletime;
    }

    public void setSaletime(String saletime) {
        this.saletime = saletime;
    }

    public List<DetailDataBean> getDetail_data() {
        return detail_data;
    }

    public void setDetail_data(List<DetailDataBean> detail_data) {
        this.detail_data = detail_data;
    }

    public static class DetailDataBean {
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

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public int getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(int subtotal) {
            this.subtotal = subtotal;
        }
    }

}
