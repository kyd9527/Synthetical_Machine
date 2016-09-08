package com.kyd.springmachine.server;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;

import com.kyd.library.util.AbAppUtil;
import com.kyd.springmachine.database.DataBaseManager;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class FileDownloaderSever extends Service implements OnRetryableFileDownloadStatusListener{
	
	DataBaseManager dbManager;
	SQLiteDatabase db;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 // ����ǰserviceע��ΪFileDownloader����״̬������
        FileDownloader.registerDownloadStatusListener(this);
        // ���ϣ��service�����Ϳ�ʼ��������δ��ɵ�������������ʵ��
        FileDownloader.continueAll(true);
        db = openOrCreateDatabase("download_file.db", this.MODE_PRIVATE, null);
        dbManager=DataBaseManager.getInstance(this);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 // ����ǰserviceȡ��ע��ΪFileDownloader����״̬������
        FileDownloader.unregisterDownloadStatusListener(this);
       // ���ϣ��serviceֹͣ��ֹͣ��������������������ʵ��
        FileDownloader.pauseAll();// ��ͣ������������
	}

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
    	// �����������أ���������������Դ�������һ������ʧ��ʱ�᳢���������أ���retryTimes�ǵ�ǰ�ڼ�������
    }
    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
    	// �ȴ����أ��ȴ���������ִ����ɣ�����FileDownloader��æ��Ĳ�����
    }
    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
    	// ׼���У���������������Դ��
    }
    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
    	 // ��׼���ã������Ѿ����ӵ�����Դ��
    }
    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
    	// �������أ�downloadSpeedΪ��ǰ�����ٶȣ���λKB/s��remainingTimeΪԤ����ʣ��ʱ�䣬��λ��
    }
    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
    	// �����ѱ���ͣ
    }
    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
    	 // ������ɣ������ļ��Ѿ�ȫ��������ɣ�
    	String fileName=downloadFileInfo.getFileName();
    	Log.d("downloadFileInfo", fileName);
    	if(!isFileAPK(fileName)){
    	    String fileDir=downloadFileInfo.getFileDir();
     	    String url= queryFileDownload(fileName);
     	   Log.d("url", url);
    	    long id = dbManager.queryByAdVerID(url);
    	    Log.d("id", id+"");
    	    dbManager.replaceByAdVerState(id, fileDir, fileName);
    	}else {//apk��װ
    		File file=new File(downloadFileInfo.getFilePath());
    		AbAppUtil.installAPK(file, this);
		}
    }
    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        // ����֪ͨ���߹㲥
    }
    
    public String queryFileDownload(String fileName){
    	String url = null;
    	Cursor c = db.rawQuery("SELECT * FROM tb_download_file WHERE file_name=?", new String[]{fileName});  
        while (c.moveToNext()) {
        	url=c.getString(c.getColumnIndex("url"));
        }
		return url;
    }
    
    public boolean isFileAPK(String fileName){
    	Pattern p = Pattern.compile("\\S+((.apk))$");
		Matcher m = p.matcher(fileName);
		return m.matches();
    }
}
