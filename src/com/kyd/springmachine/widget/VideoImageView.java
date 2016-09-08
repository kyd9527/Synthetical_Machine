package com.kyd.springmachine.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.kyd.library.util.AbFileUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.springmachine.AppConfig;
import com.kyd.springmachine.R;
import com.kyd.springmachine.database.DataBaseManager;





import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class VideoImageView extends LinearLayout implements SurfaceHolder.Callback, OnCompletionListener {

	private SurfaceView surfaceView;
	private ImageView imageView;
	private SurfaceHolder holder;
	private MediaPlayer player;
	private String filePath;
	private List<String> mediaPathList;
	private int num0 = 0, size = 0, num1 = 0;
	private Context context;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0x123:
				surfaceView.setVisibility(View.VISIBLE);
				setMedia();
				break;

			default:
				break;
			}

		}

	};

	public VideoImageView(Context context) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub

	}

	public VideoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// 在构造函数中将Xml中定义的布局解析出来。
		this.context=context;
		View view = LayoutInflater.from(context).inflate(R.layout.image_video_view, this, true);
		surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
		imageView = (ImageView) view.findViewById(R.id.imageView);
		holder = surfaceView.getHolder();
		holder.addCallback(this);
	}

	public List<String> getVideoList() {
		DataBaseManager dbManager=DataBaseManager.getInstance(getContext());
		mediaPathList=dbManager.getAdVerFile();
		boolean machine_adver=AbSharedUtil.getBoolean(context, "machine_adver", true);
		if (machine_adver==false) {
			mediaPathList=AbFileUtil.getFileList(AppConfig.SD_ADVERT);
		}
		int size = 0;
		size = mediaPathList.size();
		if (size==0) {
			List<String> list=new ArrayList<String>();
			list.add(AppConfig.LOGIN_ADVERT+"/startlogo.mp4");
			mediaPathList=list;
		}
		System.out.println("size    "+size);
		for (int i = 0; i < size; i++) {
			String str = mediaPathList.get(i);// 获取第一个视频文件
	    	String[] str1 = str.split("\\.");
	//		if (!str1[1].equals("jpg") && !str1[1].equals("png")) {
				filePath = str;
				System.out.println("---"+filePath);
				break;
	//		}
		}
		return mediaPathList;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(holder);
		player.setOnCompletionListener(this);
		// 设置显示视频显示在SurfaceView上
		try {
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("surfaceCreated    +++");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (player.isPlaying()) {
			player.stop();
		}
		player.release();
		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		setMedia();
	}

	private void setMedia() {
	//	int num0 = 0, size = 0, num1 = 0;
		size = mediaPathList.size();
		if (null != mediaPathList&&size!=0) {
			
			num0 = (int) (Math.random() * size );
			// 保证视频图片跟上一次播的不一样
			if (num0 == num1&&size!=1) {
				if (num0 == 0) {
					num1 += 1;
				} else {
					num1 -= 1;
				}
			} else {
				num1 = num0;
			}
			filePath = mediaPathList.get(num1);
			System.out.println(filePath);
		//	System.out.println("filepath    "+filePath+"   num1 "+num1+"   num0 "+num0);
		}

		String str[] = filePath.split("\\.");
		// 判断文件类型
		if (str[1].equals("jpg") || str[1].equals("png")) {
			Bitmap bm = BitmapFactory.decodeFile(filePath);
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(0x123);
					timer.cancel();
				}
			}, 5000);// 图片显示5秒
			surfaceView.setVisibility(View.GONE);
			imageView.setImageBitmap(bm);
		} else {
					
			try {
				player.reset();
				player.setDataSource(filePath);
				player.prepare();
				player.start();
			} catch (Exception e) {
				System.out.println(e.toString()+"-----313213--->"+filePath);	
				e.printStackTrace();
			}
		}
	}

}
