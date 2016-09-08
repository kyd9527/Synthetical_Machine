package com.kyd.springmachine.msg;

/**
 * 货道设置上送(1004)
 * @author 8015
 *
 */
public class RootGoodsSet {

    /**
     * lineno : 1
     * barcode : 261261773
     * max : 10
     * price : 3.22
     * goods_name : 矿泉水
     */

    private int lineno;
    private String barcode;
    private int max;
    private float price;
    private String goods_name;
    private int stock;
    
    public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

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

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }
}
