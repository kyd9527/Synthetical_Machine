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
	 * ����ͨѶ
	 */
	public static UartHandler uartHand1;

	public static ControlApp instance;

	/* ���ݿ� */
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
		// ����service
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

	// ���ݿ��Ƿ����
	public static boolean dbExist(Context context) {
		File file = context.getDatabasePath(DB_NAME);
		return file.exists();
	}

	// ��ʼ��
	public void initData() {
		// ���õ���ƷͼƬ��Ϣ���浽SD��
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
		// ���õĹ����Ϣ���浽SD��
		if (!AbFileUtil.dirIsExist(AppConfig.UPDATE_ADVERT_NOTIFY)) {
			AbFileUtil.cearteDir(AppConfig.UPDATE_ADVERT_NOTIFY);
		}
	}

	/**
	 * ������
	 */
	private void setSerialPort() {
		uartHand1 = new UartHandler();
		uartHand1.init(AppConfig.UART_1, AppConfig.BAUDRATES);
	}

	public void initFileDownload() {
		// 1������Builder
		Builder builder = new FileDownloadConfiguration.Builder(this);
		// 2.����Builder
		// ���������ļ�������ļ���
		builder.configFileDownloadDir(AppConfig.UPDATE_ADVERT_NOTIFY);
		// ����ͬʱ�����������������������Ĭ��Ϊ2
		builder.configDownloadTaskSize(3);
		// ����ʧ��ʱ�������ԵĴ��������������Ĭ��Ϊ0������
		builder.configRetryDownloadTimes(5);
		// ��������ģʽ������鿴��־�ȵ�����أ����������Ĭ�ϲ�����
		// builder.configDebugMode(true);
		// �����������糬ʱʱ�䣬���������Ĭ��Ϊ15��
		builder.configConnectTimeout(25000);// 25��

		// 3��ʹ�������ļ���ʼ��FileDownloader
		FileDownloadConfiguration configuration = builder.build();
		FileDownloader.init(configuration);
	}

}
