package com.kyd.springmachine.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder.OutputFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kyd.library.hardware.Request.HeartbeatPacket;
import com.kyd.library.hardware.Request.RequestFunctionBit;
import com.kyd.library.hardware.Request;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.Response.Listener;
import com.kyd.library.hardware.Response.ResponseFunctionBit;
import com.kyd.library.hardware.Response.UartErrorListener;
import com.kyd.library.hardware.UartResponse;
import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbFileUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.JacksonTools;
import com.kyd.library.util.SunTimes;
import com.kyd.springmachine.AppConfig;
import com.kyd.springmachine.AppConfig.GoodsDisable;
import com.kyd.springmachine.AppConfig.GoodsStatus;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.ControlApp;
import com.kyd.springmachine.EfficientActivity;
import com.kyd.springmachine.MainActivity;
import com.kyd.springmachine.R;
import com.kyd.springmachine.SystemSetActivity;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.uart.Uart1Request;
import com.kyd.springmachine.util.NetWorkUtil;

import de.greenrobot.greendao.CommodityInfo;
import de.greenrobot.greendao.GoodsInfo;
import de.greenrobot.greendao.KeyInfo;
import de.greenrobot.greendao.SetOpenDoor;

public class ReadSerialPortServer extends Service implements Listener,AMapLocationListener{

	private MyBinder binder = new MyBinder();
	private DataBaseManager data;
	public static NetWorkUtil netWork;
	public static int[] temp ;//温度
	private static BuyTimeListener buyTimeListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initData();
		initAmap();
		netWork=new NetWorkUtil(this);
		netWork.sendLogin("service");
		ControlApp.uartHand1.addListener(this);
		ControlApp.uartHand1.setOnErrorListener(new UartErrorListener() {
			
			@Override
			public void onError(int errorCode) {
				// TODO Auto-generated method stub
				netWork.sendGoodsFault("扩展板通讯故障", null);
				AbSharedUtil.putString(ReadSerialPortServer.this, "RES_UART_NOT_CONNECT1", "扩展板通讯故障");//
				Log.d("err", "-------RES_UART_NOT_CONNECT1--------->");
			}
		});
		ControlApp.uartHand1.setHeartbeatPacket(new HeartbeatPacket() {

			@Override
			public Request setPacket() {
				// TODO Auto-generated method stub
				Uart1Request req = new Uart1Request();
				byte[] data = { 0, 0, 0, 0, 0 };
				req.setMachineType((byte) (1+AbSharedUtil.getInt(
						ReadSerialPortServer.this, "machine")));
				req.setFuncBit((byte) RequestFunctionBit.CMD_READ);
				req.setCommBit((byte) 0x5);// 读取温度
				req.setData(data);
				return req;
			}

			@Override
			public Listener setListener() {
				// TODO Auto-generated method stub
				return ReadSerialPortServer.this;
			}
		},ResponseFunctionBit.RES_IS_TEMP);

	}

	// 初始化
	public void initData() {
		// 内置的商品图片信息保存到SD卡
		if (!AbFileUtil.dirIsExist(AppConfig.UPDATE_COMMODITY_NOTIFY)) {
			AbFileUtil.cearteDir(AppConfig.UPDATE_COMMODITY_NOTIFY);

			try {
				String[] fileName = this.getAssets().list("beverage_image");
				for (int i = 0; i < fileName.length; i++) {
					File file = new File(AppConfig.UPDATE_COMMODITY_NOTIFY
							+ "/" + fileName[i]);
					FileOutputStream out = new FileOutputStream(file);
					Bitmap bitmap = BitmapFactory.decodeStream(this.getAssets()
							.open("beverage_image/" + fileName[i]));

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
		if (!AbFileUtil.dirIsExist(AppConfig.SD_ADVERT)) {
			AbFileUtil.cearteDir(AppConfig.SD_ADVERT);
		}
		if (!AbFileUtil.dirIsExist(AppConfig.LOGIN_ADVERT)) {
			AbFileUtil.cearteDir(AppConfig.LOGIN_ADVERT);
			InputStream inStream = this.getResources().openRawResource(R.raw.startlogo);  
			OutputStream out =null;
			try {
				 out = new FileOutputStream(new File(AppConfig.LOGIN_ADVERT+"/startlogo.mp4"));
				byte[] buffer =new byte[1024];
				int len=0;
				while((len=inStream.read(buffer))>0){
					out.write(buffer, 0, len);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				if(inStream!=null){
					try {
						inStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(out!=null){
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// 初始化数据库
		if (!ControlApp.dbExist(this)) { 
			//测试用到的
//			AbSharedUtil.putString(this, "machine_number", AppConfig.user);
//			AbSharedUtil.putString(this, "password", AppConfig.password);
			//			
			data = DataBaseManager.getInstance(this);
			//商品列表
			try {
				InputStream in = this.getAssets().open("beverage.info");
				List<CommodityInfo> cList = (List<CommodityInfo>) JacksonTools.parseJsonArrayFile(in,new TypeReference<List<CommodityInfo>>() {});
				data.saveCommodityList(cList);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//按键值
			List<KeyInfo> keyList=new ArrayList<KeyInfo>();
			for (int i = 1; i <= 20; i++) {
				KeyInfo k=new KeyInfo();
				k.setKey(i);
				keyList.add(k);
			}
			
			data.saveKeyInfoList(keyList);
			
			//货道信息
			List<GoodsInfo> list=new ArrayList<GoodsInfo>();
			for (int i = 1; i <= 23; i++) {
				GoodsInfo g=new  GoodsInfo();
				g.setGoodsNumber(i);
				if (i>=20) {					
					g.setKeyID(data.queryKeyInfoByValue(20).getId());
				}else {
					g.setKeyID(data.queryKeyInfoByValue(i).getId());
				}
				g.setGoodsStatus(0);//默认无货
				g.setGoodsStock(30);
				g.setGoodsID(i);
				g.setGoodsCapacity(30);
				g.setGoodsDisable(GoodsDisable.Undisable.ordinal());
				list.add(g);
			}
			data.saveGoodsList(list);
			
			SetOpenDoor openDoor=new SetOpenDoor();
			openDoor.setOpenTime(AbDateUtil.getCurrentDate(AbDateUtil.dateFormatYMDHMS));
			openDoor.setOrderId(0);
			openDoor.setSyncStatuc(AppConfig.SyncStatuc.Finished.ordinal());
			data.saveSetOpenDoor(openDoor);
		}else {
			data = DataBaseManager.getInstance(this);
		}
		
		if(AbSharedUtil.getString(this, "auto_liht_start")==null){
			String latitude = AbSharedUtil.getString(this, "latitude");
			String longitude = AbSharedUtil.getString(this, "longitude");
			if (latitude == null) {
				SunTimes.GetSunTime(AppConfig.longitude, AppConfig.latitude);// 如果没有自动定位，则经纬度默认为广州
			} else {
				SunTimes.GetSunTime(Double.parseDouble(longitude), Double.parseDouble(latitude));
			}
			String sunset = SunTimes.getSunSet();
			String suntimes = SunTimes.getSunrise();
			
			AbSharedUtil.putString(this, "auto_liht_start", sunset.substring(0, 2)+sunset.substring(3,5));//自动开灯时间
			AbSharedUtil.putString(this, "auto_liht_close", suntimes.substring(0, 2)+suntimes.substring(3,5));// 自动关灯时间
		
		}
				
	}

	private void initAmap(){
		//声明mLocationOption对象
		AMapLocationClientOption mLocationOption = null;
		AMapLocationClient mlocationClient = new AMapLocationClient(this);
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位监听
		mlocationClient.setLocationListener(this);
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用onDestroy()方法
		// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
		//启动定位
		mlocationClient.startLocation();
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		ControlApp.uartHand1.removeListener(this);
		
	}

	public class MyBinder extends Binder implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 279533599665L;

		public ReadSerialPortServer getService() {
			return ReadSerialPortServer.this;
		}
		public void setBuyTimeListener(BuyTimeListener listener){
			buyTimeListener = listener;
		}
		
		public void updateKeyStaus(){//
			Uart1Request req = new Uart1Request();			
			req.setMachineType((byte) (1+ AbSharedUtil.getInt(ReadSerialPortServer.this, "machine")));
			req.setFuncBit((byte) 0x16);//
			req.setCommBit((byte) 0);//		
			req.setData(getGoodsStatus());
		//	ControlApp.uartHand2.execute(req);
		}

	}

	@Override
	public void onResponse(Response res) {
		// TODO Auto-generated method stub
		
		
		String msg = null;
		switch (res.getFuncBit()) {
		case (byte) ResponseFunctionBit.RES_MSG_ERROR://报文头错误
			
			break;
		case (byte) ResponseFunctionBit.RES_CRC_ERROR://crc校验错误
			
			break;
		case ResponseFunctionBit.RES_COIN_NOT_CONNECT:	
			msg = "硬币器无连接";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_NOT_CONNECT", msg);
			netWork.sendGoodsFault(msg, null);
			break;		
		case ResponseFunctionBit.RES_COIN_PAPER_ERROR:
			msg = "纸币器硬币器错误";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_PAPER_ERROR", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_LACK_COIN:
			msg = "硬币数量不足";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_LACK_COIN", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_COIN_PIPING_NOT_EXIST:
			msg = "硬币器钱管不存在";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_PIPING_NOT_EXIST", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_COIN_POLL_ERROR:
			msg = "硬币器轮询错误";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_POLL_ERROR", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_PAPER_NOT_CONNECT:
			msg = "纸币器无连接";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_PAPER_NOT_CONNECT", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_PAPER_PIPING_NOT_EXIST:
			msg = "纸币器钱管被取走";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_PAPER_PIPING_NOT_EXIST", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_PAPER_POLL_ERROR:
			msg = "纸币器轮询错误";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_PAPER_POLL_ERROR", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_GOODS_STATE:		//货道状态，有无货
			byte[]goods = ((UartResponse) res).getGoodsState((int) data.getGoodsInfoCount());
			for(int i=0;i<goods.length;i++){
				data.replaceGoodsStatus(i+1, goods[i]);
			}
			
			/*Uart1Request req = new Uart1Request();			
			req.setMachineType((byte) (1+ AbSharedUtil.getInt(
					ReadSerialPortServer.this, "machine")));
			req.setFuncBit((byte) 0x16);//
			req.setCommBit((byte) 0);//
			Log.d("有没有货", "-------rr------->"+res.data.length);			
			req.setData(getGoodsStatus());
			ControlApp.uartHand2.execute(req);*/
			Log.d("有没有货", "-------rr------->"+res.data.length);	
			Intent intent = new Intent("KEY_ACTION");
			intent.putExtra("action", "UPDATE");
			sendBroadcast(intent);
			break;
		case ResponseFunctionBit.RES_IS_TEMP://temp[0] 右库温度   temp[1]左库温度
			temp = ((UartResponse) res).getTemp();//获取温度			
			if(temp[0]<100&&temp[1]<100){
				int temp_set = AbSharedUtil.getInt(ReadSerialPortServer.this, "temp");
				int temp_status = AbSharedUtil.getInt(ReadSerialPortServer.this, "temperatures");//加热制冷标志，0关闭，1开制冷，2开加热
				int doorStatus  = AbSharedUtil.getInt(ReadSerialPortServer.this, "doorStatus",0);//门开关标志，0门关闭，1门打开
				int buy_status = AbSharedUtil.getInt(ReadSerialPortServer.this, "buy_status",1);//营业标志,1开始营业，0结束营业
		//		Log.d("temp", doorStatus+"------"+temp_set+"------>"+temp_status);
				if(((temp[0]<=temp_set&&temp[1]<=temp_set)&&temp_status==1)||doorStatus==1||buy_status==0){
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//关制冷
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//关冷风风扇	  
			//		Log.d("temp", doorStatus+"-------关闭制冷------->"+temp_status+"------>"+temp[0]+"------>"+temp[1]+"---->"+buy_status);
				}else if(((temp[0]>temp_set-5||temp[1]>temp_set-5)&&temp_status==2)||doorStatus==1||buy_status==0){
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//关加热
					Log.d("temp", "-------关闭加热------->");
				}else if((temp_set+5<temp[0]||temp_set+5<temp[1])&&temp_status==1&&doorStatus==0&&buy_status==1){//开制冷
			//	}else if((temp_set+5<temp[0]||temp_set+5<temp[1])&&temp_status==1&&doorStatus==0){//开制冷	
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//关加热
					ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)1,(byte) 0);//开制冷
					if(temp_set+5<temp[0]&&temp_set+5<temp[1]){
						ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)2,(byte) 0);//开所有冷风风扇
					//	Log.d("temp", "-------制冷---1---->"+temp[0]+"------>"+temp[1]);
					}else{
						if(temp_set>=temp[0]){
					//		Log.d("temp", "-------制冷---2---->"+temp[0]+"------>"+temp[1]);
							ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)2,(byte) 2);//开右边冷风风扇	
						}
						if(temp_set>=temp[1]){
					//		Log.d("temp", "-------制冷---3---->"+temp[0]+"------>"+temp[1]);
							ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)2,(byte) 1);//开左边冷风风扇
						}
					}																				
				//	Log.d("temp", "-------制冷----4--->"+temp[0]+"------>"+temp[1]);
				}else if((temp_set-5>temp[0]||temp_set-5>temp[1])&&temp_status==2&&doorStatus==0&&buy_status==1){//开加热		
				//}else if((temp_set-5>temp[0]||temp_set-5>temp[1])&&temp_status==2&&doorStatus==1){//开加热	
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//关制冷
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//关冷风风扇
					ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)4,(byte) 0);//开加热
					Log.d("temp", "-------加热------->");
				}
			}else{
				ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//关制冷
				ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//关冷风风扇	
				ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//关加热
				msg = "温度异常";
				AbSharedUtil.putString(ReadSerialPortServer.this, "RES_TEMP_WARING", msg);
				netWork.sendGoodsFault(msg, null);
				Log.d("temp", "------------->"+msg);
			}
			break;
		case ResponseFunctionBit.RES_TEMP_NOT_EXIST:
			ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//关制冷
			ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//关冷风风扇	
			ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//关加热
			msg = "温度传感器不存在";
			netWork.sendGoodsFault(msg, null);
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_TEMP_NOT_EXIST", msg);
			//Log.d(TAG, "-------1------->");
			break;
		case ResponseFunctionBit.RES_LED_ONLINE:
			
			switch (res.data[0]) {
			case 1:
				
				break;
			case 2:
				break;				
			case 3:
				break;
			case 4:
				break;
			default:
				break;
			}
			Log.d("key", res.data[0]+"------------->"+res.data[1]);
			break;
		case ResponseFunctionBit.RES_DOOR_KEY:
			if (((UartResponse) res).doorIsOpen()) {
				AbSharedUtil.putInt(ReadSerialPortServer.this, "doorStatus", 1);// 门打开
				Log.d("ReadSerialPortServer", "-------6------->");
			}else{
				AbSharedUtil.putInt(ReadSerialPortServer.this, "doorStatus", 0);// 门关闭
				Log.d("ReadSerialPortServer", "-------10------->");
			}
			break;
		default:
			break;
		}	
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
        if (amapLocation != null) {
        if (amapLocation.getErrorCode() == 0) {
        //定位成功回调信息，设置相关消息
        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        amapLocation.getLatitude();//获取纬度
        amapLocation.getLongitude();//获取经度
   //     AbToastUtil.showToast(this, amapLocation.getLatitude()+"    "+ amapLocation.getLongitude());
        Log.i("a map",  amapLocation.getLatitude()+"    "+ amapLocation.getLongitude());
        AbSharedUtil.putString(this, "latitude", amapLocation.getLatitude()+"");
        AbSharedUtil.putString(this, "longitude", amapLocation.getLongitude()+"");
        
    } else {
              //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
        Log.e("AmapError","location Error, ErrCode:"
            + amapLocation.getErrorCode() + ", errInfo:"
            + amapLocation.getErrorInfo());
        }
    }
    
	}
	
	private static void ctrlTemp(Context context,byte sw,byte type,byte arg){
		Uart1Request req = new Uart1Request();	
		byte[] data = {0,0,0,0,0};
		
		req.setMachineType((byte) (1+AbSharedUtil.getInt(context, "machine")));
		req.setFuncBit((byte) RequestFunctionBit.CMD_CTRL);//
		req.setCommBit(sw);//打开
		data[0]=type;
		data[1]=arg;
		req.setData(data);	
		ControlApp.uartHand1.execute(req);
	}
	
	public static class EfficientReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			int temp_set = AbSharedUtil.getInt(context, "temp");
		//	Log.d("temp", "-------onReceive------->"+temp_set);	
			if(EfficientActivity.ACTION_END.equals(action)){//节能模式结束
				Log.d("temp", "-------ACTION_END------->");	
				AbSharedUtil.putInt(context, "temperatures", 0);//关闭
				ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//关制冷
				ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//关冷风风扇
				ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//关加热
				
				
			}else if(EfficientActivity.ACTION_START.equals(action)){//节能模式开始
				Log.d("temp", "-------ACTION_START------->");	
				if(temp_set>temp[0]&&temp_set>temp[1]){//开加热
					AbSharedUtil.putInt(context, "temperatures", 2);//开加热
					ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//关制冷
					ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//关冷风风扇
					ctrlTemp(context,(byte)1,(byte)4,(byte) 0);
				}else {//开制冷
					AbSharedUtil.putInt(context, "temperatures", 1);//开制冷
					ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//关加热
					ctrlTemp(context,(byte)1,(byte)1,(byte) 0);//开制冷
					ctrlTemp(context,(byte)1,(byte)2,(byte) 0);//开冷风风扇					
				}								
			}else if(EfficientActivity.ACTION_AUTO.equals(action)){//自动模式
				if(temp_set>temp[0]&&temp_set>temp[1]){//开加热
					AbSharedUtil.putInt(context, "temperatures", 2);//开加热
					ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//关制冷
					ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//关冷风风扇
					ctrlTemp(context,(byte)1,(byte)4,(byte) 0);
				}else {//开制冷
					AbSharedUtil.putInt(context, "temperatures", 1);//开制冷
					ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//关加热
					ctrlTemp(context,(byte)1,(byte)1,(byte) 0);//开制冷
					ctrlTemp(context,(byte)1,(byte)2,(byte) 0);//开冷风风扇					
				}	
				Log.d("temp", "-------ACTION_AUTO------->");								
			}else if(EfficientActivity.ACTION_STOP.equals(action)){
				AbSharedUtil.putInt(context, "temperatures", 0);//关闭
				ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//关制冷
				ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//关冷风风扇
				ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//关加热		
				Log.d("temp", "-------ACTION_STOP------->");
			}else if(SystemSetActivity.LIGHT_OPEN.equals(action)){//
				String str_buy_time_start=AbSharedUtil.getString(context, "buy_time_start","00:00");				
				str_buy_time_start = str_buy_time_start.substring(0, 2)+str_buy_time_start.substring(3, 5);
				int buy_time_start = Integer.parseInt(str_buy_time_start);				
				int auto_liht_start =Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_start","0000"));
				int light_status=AbSharedUtil.getInt(context, "light",0);
				if((buy_time_start<auto_liht_start)&&light_status!=0){
					ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//开灯	
					
				}	
				Log.d("temp", "-------LIGHT_OPEN------->");
			}else if(SystemSetActivity.LIGHT_CLOSE.equals(action)){	
				String str_buy_time_end =  AbSharedUtil.getString(context, "buy_time_end","24:00");
				str_buy_time_end = str_buy_time_end.substring(0, 2)+str_buy_time_end.substring(3, 5);
				int buy_time_end = Integer.parseInt(str_buy_time_end);		
				int auto_liht_close = Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_close","2400"));
				int auto_liht_start =Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_start","0000"));
				
				if(auto_liht_close<buy_time_end&&buy_time_end<auto_liht_start){
					ctrlTemp(context,(byte)0,(byte)3,(byte) 0);//关灯	
				}
				Log.d("temp",auto_liht_close+ "-------LIGHT_CLOSE----"+auto_liht_start+"--->"+buy_time_end);
			}else if(SystemSetActivity.BUY_OPEN.equals(action)){//营业开始
				int light_status= AbSharedUtil.getInt(context, "light",0);
				String str_buy_time_start=AbSharedUtil.getString(context, "buy_time_start","00:00");								
				str_buy_time_start = str_buy_time_start.substring(0, 2)+str_buy_time_start.substring(3, 5);
				int buy_time_start = Integer.parseInt(str_buy_time_start);
				if(light_status==1){
					int auto_liht_start =Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_start"));
					if(buy_time_start>auto_liht_start){
						ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//开灯	
					}								
				}else if(light_status==2){
					ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//开灯	
				}
				AbSharedUtil.putInt(context, "buy_status", 1);//开始营业标志
				if(buyTimeListener!=null){
					buyTimeListener.onListener(true);//开始营业
				}				
				Log.d("temp", "-------BUY_OPEN------->");
			}else if(SystemSetActivity.BUY_CLOSE.equals(action)){//营业关闭
				int light_status = AbSharedUtil.getInt(context, "light",0);
				if(light_status==1||light_status==2){						
					ctrlTemp(context,(byte)0,(byte)3,(byte) 0);//关灯						
				}	
				ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//关制冷
				ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//关冷风风扇
				ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//关加热	
				AbSharedUtil.putInt(context, "buy_status", 0);//结束营业标志
				if(buyTimeListener!=null){
					buyTimeListener.onListener(false);//结束营业
				}
				
				Log.d("temp", "-------BUY_CLOSE------->");
			}else if(SystemSetActivity.ACTION_LIGHT_STOP.equals(action)){
				ctrlTemp(context,(byte)0,(byte)3,(byte) 0);//关灯	
			}else if(SystemSetActivity.ACTION_LIGHT_AUTO.equals(action)){
				ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//关灯	
			}
		}
	}
	

	private byte[] getGoodsStatus(){
		
		List<GoodsComInfo>list = data.queryAllGoodsComInfoByKey();
		int size = list.size();
		byte[] goodsStatus = {0,0,0,0};
		for(int i =0;i<size;i++){
			GoodsComInfo ginfo = list.get(i);	
			//判断有货且没有被禁用
			if(ginfo.getGoodsStatus()==GoodsStatus.In_Stock.ordinal()&&ginfo.getGoodsDisable()==GoodsDisable.Undisable.ordinal()){
				goodsStatus[i/8]|=(byte) (0x1<<(i%8));
			}
		}
		return goodsStatus;
	}
	
}
