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
	public static final String UPDATE_COMMODITY_NOTIFY = "/sdcard/DCIM/goods_image";// ��ƷͼƬ
																					// goods_update.ini
	public static final String UPDATE_GOODS_CONFIG_NOTIFY = "/sdcard/kyd/goodsconfig_update";// ��Ʒ���ø���
																								// goodsconfig_update.ini
	public static final String UPDATE_ADVERT_NOTIFY = "/sdcard/DCIM/adv_update";// ������,��̨���·��
																				// adv_update.ini
	public static final String URL = "http://120.76.158.207/kydAPI/api/";//��̨URL
	
	public static final String SD_ADVERT="/sdcard/DCIM/ad_file";//SD���Ĺ��·��
	
	public static final String LOGIN_ADVERT="/sdcard/DCIM/login";

	public static final String user="36781234";
	
	public static final String password="kyd123456";
	// ���ݾ�γ��
	public static final double latitude = 23.117055;
	public static final double longitude = 113.275995;
	
	/**
	 * ���ɼ��ܵ�url·��
	 * 
	 * @param url
	 *            ��ε�ַ
	 * @return
	 */
	public static String url(String url) {
		return AppConfig.URL + AbMathUtil.getRandomChar(8)+ AbBase64.encode(url, "UTF-8");
	}

	/**
	 * ���ɼ��ܵ���Ϣ��
	 * 
	 * @param strjson
	 *            Ҫjson����Ϣ��
	 * @return
	 */
	public static String PostData(String strjson) {
		String jsonLength = "#" + strjson.length();
		String jsonBase = AbBase64.encode(strjson, "UTF-8", 128);
		String jsonTemp = AbMathUtil.getRandomChar(16) + jsonBase + jsonLength;
		String jsonMD5 = AbMd5.MD5(jsonTemp,"UTF-8");
		return jsonTemp + jsonMD5;
	}
	
	// ֧������,֧������΢�ţ��ֽ�,��ûȷ��
	public enum PayType {
		 ALI_PAY,WX_PAY, CASH_PAY,NO_PAY
	}
	
	// ����״̬   δ��ɣ�����ɣ��ɹ���ʧ�ܣ�״̬δ֪,δ֧����δ�˿�
	public enum OrderStatus {
		Unfinished, Finished, Success, Fail, Unknown,Unpay,Refund,TestSuccess,TestFail
	}
	
	// ����״̬  �޻�,�л�������
	public enum GoodsStatus {
		Out_Of_Stock,In_Stock,Fault
	}
	
	//��������,������,����
	public enum GoodsDisable{
		Undisable,Disable
	}
	// ����ͬ��״̬��δ���,�����
	public enum SyncStatuc {
		Unfinished, Finished
	}
}
