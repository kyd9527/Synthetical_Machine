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
		 // 将当前service注册为FileDownloader下载状态监听器
        FileDownloader.registerDownloadStatusListener(this);
        // 如果希望service启动就开始下载所有未完成的任务，则开启以下实现
        FileDownloader.continueAll(true);
        db = openOrCreateDatabase("download_file.db", this.MODE_PRIVATE, null);
        dbManager=DataBaseManager.getInstance(this);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 // 将当前service取消注册为FileDownloader下载状态监听器
        FileDownloader.unregisterDownloadStatusListener(this);
       // 如果希望service停止就停止所有下载任务，则开启以下实现
        FileDownloader.pauseAll();// 暂停所有下载任务
	}

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
    	// 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试
    }
    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
    	// 等待下载（等待其它任务执行完成，或者FileDownloader在忙别的操作）
    }
    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
    	// 准备中（即，正在连接资源）
    }
    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
    	 // 已准备好（即，已经连接到了资源）
    }
    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
    	// 正在下载，downloadSpeed为当前下载速度，单位KB/s，remainingTime为预估的剩余时间，单位秒
    }
    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
    	// 下载已被暂停
    }
    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
    	 // 下载完成（整个文件已经全部下载完成）
    	String fileName=downloadFileInfo.getFileName();
    	Log.d("downloadFileInfo", fileName);
    	if(!isFileAPK(fileName)){
    	    String fileDir=downloadFileInfo.getFileDir();
     	    String url= queryFileDownload(fileName);
     	   Log.d("url", url);
    	    long id = dbManager.queryByAdVerID(url);
    	    Log.d("id", id+"");
    	    dbManager.replaceByAdVerState(id, fileDir, fileName);
    	}else {//apk安装
    		File file=new File(downloadFileInfo.getFilePath());
    		AbAppUtil.installAPK(file, this);
		}
    }
    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        // 发送通知或者广播
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
