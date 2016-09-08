package com.kyd.springmachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloadConfiguration.Builder;
import org.wlf.filedownloader.FileDownloader;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kyd.library.hardware.UartHandler;
import com.kyd.library.util.AbFileUtil;
import com.kyd.springmachine.server.ReadSerialPortServer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.greenrobot.greendao.DaoMaster;
import de.greenrobot.greendao.DaoMaster.OpenHelper;
import de.greenrobot.greendao.DaoSession;

public class ControlApp extends Application{

	/**
	 * 串口通讯
	 */
	public static UartHandler uartHand1;

	public static ControlApp instance;

	/* 数据库 */
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	public static SQLiteDatabase db;
	private final static String DB_NAME = "kyd.db";

	public static RequestQueue queue;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		 setSerialPort();

		queue = Volley.newRequestQueue(getApplicationContext());
		// DataBaseManager data = DataBaseManager.getInstance(instance);

		// initData();
		initFileDownload();
		// 启动service
		Intent intent = new Intent(this, ReadSerialPortServer.class);
		startService(intent);
	}

	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public static SQLiteDatabase getSQLDatebase(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			db = daoMaster.getDatabase();
		}
		return db;
	}

	// 数据库是否存在
	public static boolean dbExist(Context context) {
		File file = context.getDatabasePath(DB_NAME);
		return file.exists();
	}

	// 初始化
	public void initData() {
		// 内置的商品图片信息保存到SD卡
		if (!AbFileUtil.dirIsExist(AppConfig.UPDATE_COMMODITY_NOTIFY)) {
			AbFileUtil.cearteDir(AppConfig.UPDATE_COMMODITY_NOTIFY);

			try {
				String[] fileName = this.getAssets().list("beverage_image");
				for (int i = 0; i < fileName.length; i++) {
					File file = new File(AppConfig.UPDATE_COMMODITY_NOTIFY + "/" + fileName[i]);
					FileOutputStream out = new FileOutputStream(file);
					Bitmap bitmap = BitmapFactory.decodeStream(this.getAssets().open("beverage_image/" + fileName[i]));

					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
					out.flush();
					out.close();
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 内置的广告信息保存到SD卡
		if (!AbFileUtil.dirIsExist(AppConfig.UPDATE_ADVERT_NOTIFY)) {
			AbFileUtil.cearteDir(AppConfig.UPDATE_ADVERT_NOTIFY);
		}
	}

	/**
	 * 开串口
	 */
	private void setSerialPort() {
		uartHand1 = new UartHandler();
		uartHand1.init(AppConfig.UART_1, AppConfig.BAUDRATES);
	}

	public void initFileDownload() {
		// 1、创建Builder
		Builder builder = new FileDownloadConfiguration.Builder(this);
		// 2.配置Builder
		// 配置下载文件保存的文件夹
		builder.configFileDownloadDir(AppConfig.UPDATE_ADVERT_NOTIFY);
		// 配置同时下载任务数量，如果不配置默认为2
		builder.configDownloadTaskSize(3);
		// 配置失败时尝试重试的次数，如果不配置默认为0不尝试
		builder.configRetryDownloadTimes(5);
		// 开启调试模式，方便查看日志等调试相关，如果不配置默认不开启
		// builder.configDebugMode(true);
		// 配置连接网络超时时间，如果不配置默认为15秒
		builder.configConnectTimeout(25000);// 25秒

		// 3、使用配置文件初始化FileDownloader
		FileDownloadConfiguration configuration = builder.build();
		FileDownloader.init(configuration);
	}

}
