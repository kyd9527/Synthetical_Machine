package com.kyd.springmachine;

import java.security.MessageDigest;

import com.kyd.library.util.AbBase64;
import com.kyd.library.util.AbMathUtil;
import com.kyd.library.util.AbMd5;


public class AppConfig {

	public static final String UART_1 = "/dev/ttyS3";


	public static final int BAUDRATES = 115200;

	public static final String ROOT_PATH = "/sdcard/kyd";
	public static final String START_LOGO = "/sdcard/DCIM/startlogo.mp4";
	public static final String UPDATE_COMMODITY_NOTIFY = "/sdcard/DCIM/goods_image";// 商品图片
																					// goods_update.ini
	public static final String UPDATE_GOODS_CONFIG_NOTIFY = "/sdcard/kyd/goodsconfig_update";// 商品配置更新
																								// goodsconfig_update.ini
	public static final String UPDATE_ADVERT_NOTIFY = "/sdcard/DCIM/adv_update";// 广告更新,后台广告路径
																				// adv_update.ini
	public static final String URL = "http://120.76.158.207/kydAPI/api/";//后台URL
	
	public static final String SD_ADVERT="/sdcard/DCIM/ad_file";//SD卡的广告路径
	
	public static final String LOGIN_ADVERT="/sdcard/DCIM/login";

	public static final String user="36781234";
	
	public static final String password="kyd123456";
	// 广州经纬度
	public static final double latitude = 23.117055;
	public static final double longitude = 113.275995;
	
	/**
	 * 生成加密的url路径
	 * 
	 * @param url
	 *            入参地址
	 * @return
	 */
	public static String url(String url) {
		return AppConfig.URL + AbMathUtil.getRandomChar(8)+ AbBase64.encode(url, "UTF-8");
	}

	/**
	 * 生成加密的消息体
	 * 
	 * @param strjson
	 *            要json的消息体
	 * @return
	 */
	public static String PostData(String strjson) {
		String jsonLength = "#" + strjson.length();
		String jsonBase = AbBase64.encode(strjson, "UTF-8", 128);
		String jsonTemp = AbMathUtil.getRandomChar(16) + jsonBase + jsonLength;
		String jsonMD5 = AbMd5.MD5(jsonTemp,"UTF-8");
		return jsonTemp + jsonMD5;
	}
	
	// 支付类型,支付宝，微信，现金,还没确认
	public enum PayType {
		 ALI_PAY,WX_PAY, CASH_PAY,NO_PAY
	}
	
	// 订单状态   未完成，已完成，成功，失败，状态未知,未支付，未退款
	public enum OrderStatus {
		Unfinished, Finished, Success, Fail, Unknown,Unpay,Refund,TestSuccess,TestFail
	}
	
	// 货道状态  无货,有货，故障
	public enum GoodsStatus {
		Out_Of_Stock,In_Stock,Fault
	}
	
	//货道禁用,不禁用,禁用
	public enum GoodsDisable{
		Undisable,Disable
	}
	// 数据同步状态，未完成,已完成
	public enum SyncStatuc {
		Unfinished, Finished
	}
}
