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
	public static int[] temp ;//�¶�
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
				netWork.sendGoodsFault("��չ��ͨѶ����", null);
				AbSharedUtil.putString(ReadSerialPortServer.this, "RES_UART_NOT_CONNECT1", "��չ��ͨѶ����");//
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
				req.setCommBit((byte) 0x5);// ��ȡ�¶�
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

	// ��ʼ��
	public void initData() {
		// ���õ���ƷͼƬ��Ϣ���浽SD��
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
		// ���õĹ����Ϣ���浽SD��
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
		// ��ʼ�����ݿ�
		if (!ControlApp.dbExist(this)) { 
			//�����õ���
//			AbSharedUtil.putString(this, "machine_number", AppConfig.user);
//			AbSharedUtil.putString(this, "password", AppConfig.password);
			//			
			data = DataBaseManager.getInstance(this);
			//��Ʒ�б�
			try {
				InputStream in = this.getAssets().open("beverage.info");
				List<CommodityInfo> cList = (List<CommodityInfo>) JacksonTools.parseJsonArrayFile(in,new TypeReference<List<CommodityInfo>>() {});
				data.saveCommodityList(cList);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//����ֵ
			List<KeyInfo> keyList=new ArrayList<KeyInfo>();
			for (int i = 1; i <= 20; i++) {
				KeyInfo k=new KeyInfo();
				k.setKey(i);
				keyList.add(k);
			}
			
			data.saveKeyInfoList(keyList);
			
			//������Ϣ
			List<GoodsInfo> list=new ArrayList<GoodsInfo>();
			for (int i = 1; i <= 23; i++) {
				GoodsInfo g=new  GoodsInfo();
				g.setGoodsNumber(i);
				if (i>=20) {					
					g.setKeyID(data.queryKeyInfoByValue(20).getId());
				}else {
					g.setKeyID(data.queryKeyInfoByValue(i).getId());
				}
				g.setGoodsStatus(0);//Ĭ���޻�
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
				SunTimes.GetSunTime(AppConfig.longitude, AppConfig.latitude);// ���û���Զ���λ����γ��Ĭ��Ϊ����
			} else {
				SunTimes.GetSunTime(Double.parseDouble(longitude), Double.parseDouble(latitude));
			}
			String sunset = SunTimes.getSunSet();
			String suntimes = SunTimes.getSunrise();
			
			AbSharedUtil.putString(this, "auto_liht_start", sunset.substring(0, 2)+sunset.substring(3,5));//�Զ�����ʱ��
			AbSharedUtil.putString(this, "auto_liht_close", suntimes.substring(0, 2)+suntimes.substring(3,5));// �Զ��ص�ʱ��
		
		}
				
	}

	private void initAmap(){
		//����mLocationOption����
		AMapLocationClientOption mLocationOption = null;
		AMapLocationClient mlocationClient = new AMapLocationClient(this);
		//��ʼ����λ����
		mLocationOption = new AMapLocationClientOption();
		//���ö�λ����
		mlocationClient.setLocationListener(this);
		//���ö�λģʽΪ�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ
		mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
		//���ö�λ���,��λ����,Ĭ��Ϊ2000ms
		mLocationOption.setInterval(2000);
		//���ö�λ����
		mlocationClient.setLocationOption(mLocationOption);
		// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
		// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����stopLocation()������ȡ����λ����
		// �ڶ�λ�������ں��ʵ��������ڵ���onDestroy()����
		// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������stopLocation()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
		//������λ
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
		case (byte) ResponseFunctionBit.RES_MSG_ERROR://����ͷ����
			
			break;
		case (byte) ResponseFunctionBit.RES_CRC_ERROR://crcУ�����
			
			break;
		case ResponseFunctionBit.RES_COIN_NOT_CONNECT:	
			msg = "Ӳ����������";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_NOT_CONNECT", msg);
			netWork.sendGoodsFault(msg, null);
			break;		
		case ResponseFunctionBit.RES_COIN_PAPER_ERROR:
			msg = "ֽ����Ӳ��������";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_PAPER_ERROR", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_LACK_COIN:
			msg = "Ӳ����������";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_LACK_COIN", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_COIN_PIPING_NOT_EXIST:
			msg = "Ӳ����Ǯ�ܲ�����";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_PIPING_NOT_EXIST", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_COIN_POLL_ERROR:
			msg = "Ӳ������ѯ����";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_COIN_POLL_ERROR", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_PAPER_NOT_CONNECT:
			msg = "ֽ����������";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_PAPER_NOT_CONNECT", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_PAPER_PIPING_NOT_EXIST:
			msg = "ֽ����Ǯ�ܱ�ȡ��";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_PAPER_PIPING_NOT_EXIST", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_PAPER_POLL_ERROR:
			msg = "ֽ������ѯ����";
			AbSharedUtil.putString(ReadSerialPortServer.this, "RES_PAPER_POLL_ERROR", msg);
			netWork.sendGoodsFault(msg, null);
			break;
		case ResponseFunctionBit.RES_GOODS_STATE:		//����״̬�����޻�
			byte[]goods = ((UartResponse) res).getGoodsState((int) data.getGoodsInfoCount());
			for(int i=0;i<goods.length;i++){
				data.replaceGoodsStatus(i+1, goods[i]);
			}
			
			/*Uart1Request req = new Uart1Request();			
			req.setMachineType((byte) (1+ AbSharedUtil.getInt(
					ReadSerialPortServer.this, "machine")));
			req.setFuncBit((byte) 0x16);//
			req.setCommBit((byte) 0);//
			Log.d("��û�л�", "-------rr------->"+res.data.length);			
			req.setData(getGoodsStatus());
			ControlApp.uartHand2.execute(req);*/
			Log.d("��û�л�", "-------rr------->"+res.data.length);	
			Intent intent = new Intent("KEY_ACTION");
			intent.putExtra("action", "UPDATE");
			sendBroadcast(intent);
			break;
		case ResponseFunctionBit.RES_IS_TEMP://temp[0] �ҿ��¶�   temp[1]����¶�
			temp = ((UartResponse) res).getTemp();//��ȡ�¶�			
			if(temp[0]<100&&temp[1]<100){
				int temp_set = AbSharedUtil.getInt(ReadSerialPortServer.this, "temp");
				int temp_status = AbSharedUtil.getInt(ReadSerialPortServer.this, "temperatures");//���������־��0�رգ�1�����䣬2������
				int doorStatus  = AbSharedUtil.getInt(ReadSerialPortServer.this, "doorStatus",0);//�ſ��ر�־��0�Źرգ�1�Ŵ�
				int buy_status = AbSharedUtil.getInt(ReadSerialPortServer.this, "buy_status",1);//Ӫҵ��־,1��ʼӪҵ��0����Ӫҵ
		//		Log.d("temp", doorStatus+"------"+temp_set+"------>"+temp_status);
				if(((temp[0]<=temp_set&&temp[1]<=temp_set)&&temp_status==1)||doorStatus==1||buy_status==0){
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//������
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//��������	  
			//		Log.d("temp", doorStatus+"-------�ر�����------->"+temp_status+"------>"+temp[0]+"------>"+temp[1]+"---->"+buy_status);
				}else if(((temp[0]>temp_set-5||temp[1]>temp_set-5)&&temp_status==2)||doorStatus==1||buy_status==0){
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//�ؼ���
					Log.d("temp", "-------�رռ���------->");
				}else if((temp_set+5<temp[0]||temp_set+5<temp[1])&&temp_status==1&&doorStatus==0&&buy_status==1){//������
			//	}else if((temp_set+5<temp[0]||temp_set+5<temp[1])&&temp_status==1&&doorStatus==0){//������	
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//�ؼ���
					ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)1,(byte) 0);//������
					if(temp_set+5<temp[0]&&temp_set+5<temp[1]){
						ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)2,(byte) 0);//������������
					//	Log.d("temp", "-------����---1---->"+temp[0]+"------>"+temp[1]);
					}else{
						if(temp_set>=temp[0]){
					//		Log.d("temp", "-------����---2---->"+temp[0]+"------>"+temp[1]);
							ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)2,(byte) 2);//���ұ�������	
						}
						if(temp_set>=temp[1]){
					//		Log.d("temp", "-------����---3---->"+temp[0]+"------>"+temp[1]);
							ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)2,(byte) 1);//�����������
						}
					}																				
				//	Log.d("temp", "-------����----4--->"+temp[0]+"------>"+temp[1]);
				}else if((temp_set-5>temp[0]||temp_set-5>temp[1])&&temp_status==2&&doorStatus==0&&buy_status==1){//������		
				//}else if((temp_set-5>temp[0]||temp_set-5>temp[1])&&temp_status==2&&doorStatus==1){//������	
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//������
					ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//��������
					ctrlTemp(ReadSerialPortServer.this,(byte)1,(byte)4,(byte) 0);//������
					Log.d("temp", "-------����------->");
				}
			}else{
				ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//������
				ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//��������	
				ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//�ؼ���
				msg = "�¶��쳣";
				AbSharedUtil.putString(ReadSerialPortServer.this, "RES_TEMP_WARING", msg);
				netWork.sendGoodsFault(msg, null);
				Log.d("temp", "------------->"+msg);
			}
			break;
		case ResponseFunctionBit.RES_TEMP_NOT_EXIST:
			ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)1,(byte) 0);//������
			ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)2,(byte) 0);//��������	
			ctrlTemp(ReadSerialPortServer.this,(byte)0,(byte)4,(byte) 0);//�ؼ���
			msg = "�¶ȴ�����������";
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
				AbSharedUtil.putInt(ReadSerialPortServer.this, "doorStatus", 1);// �Ŵ�
				Log.d("ReadSerialPortServer", "-------6------->");
			}else{
				AbSharedUtil.putInt(ReadSerialPortServer.this, "doorStatus", 0);// �Źر�
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
        //��λ�ɹ��ص���Ϣ�����������Ϣ
        amapLocation.getLocationType();//��ȡ��ǰ��λ�����Դ�������綨λ����������λ���ͱ�
        amapLocation.getLatitude();//��ȡγ��
        amapLocation.getLongitude();//��ȡ����
   //     AbToastUtil.showToast(this, amapLocation.getLatitude()+"    "+ amapLocation.getLongitude());
        Log.i("a map",  amapLocation.getLatitude()+"    "+ amapLocation.getLongitude());
        AbSharedUtil.putString(this, "latitude", amapLocation.getLatitude()+"");
        AbSharedUtil.putString(this, "longitude", amapLocation.getLongitude()+"");
        
    } else {
              //��ʾ������ϢErrCode�Ǵ����룬errInfo�Ǵ�����Ϣ������������
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
		req.setCommBit(sw);//��
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
			if(EfficientActivity.ACTION_END.equals(action)){//����ģʽ����
				Log.d("temp", "-------ACTION_END------->");	
				AbSharedUtil.putInt(context, "temperatures", 0);//�ر�
				ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//������
				ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//��������
				ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//�ؼ���
				
				
			}else if(EfficientActivity.ACTION_START.equals(action)){//����ģʽ��ʼ
				Log.d("temp", "-------ACTION_START------->");	
				if(temp_set>temp[0]&&temp_set>temp[1]){//������
					AbSharedUtil.putInt(context, "temperatures", 2);//������
					ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//������
					ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//��������
					ctrlTemp(context,(byte)1,(byte)4,(byte) 0);
				}else {//������
					AbSharedUtil.putInt(context, "temperatures", 1);//������
					ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//�ؼ���
					ctrlTemp(context,(byte)1,(byte)1,(byte) 0);//������
					ctrlTemp(context,(byte)1,(byte)2,(byte) 0);//��������					
				}								
			}else if(EfficientActivity.ACTION_AUTO.equals(action)){//�Զ�ģʽ
				if(temp_set>temp[0]&&temp_set>temp[1]){//������
					AbSharedUtil.putInt(context, "temperatures", 2);//������
					ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//������
					ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//��������
					ctrlTemp(context,(byte)1,(byte)4,(byte) 0);
				}else {//������
					AbSharedUtil.putInt(context, "temperatures", 1);//������
					ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//�ؼ���
					ctrlTemp(context,(byte)1,(byte)1,(byte) 0);//������
					ctrlTemp(context,(byte)1,(byte)2,(byte) 0);//��������					
				}	
				Log.d("temp", "-------ACTION_AUTO------->");								
			}else if(EfficientActivity.ACTION_STOP.equals(action)){
				AbSharedUtil.putInt(context, "temperatures", 0);//�ر�
				ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//������
				ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//��������
				ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//�ؼ���		
				Log.d("temp", "-------ACTION_STOP------->");
			}else if(SystemSetActivity.LIGHT_OPEN.equals(action)){//
				String str_buy_time_start=AbSharedUtil.getString(context, "buy_time_start","00:00");				
				str_buy_time_start = str_buy_time_start.substring(0, 2)+str_buy_time_start.substring(3, 5);
				int buy_time_start = Integer.parseInt(str_buy_time_start);				
				int auto_liht_start =Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_start","0000"));
				int light_status=AbSharedUtil.getInt(context, "light",0);
				if((buy_time_start<auto_liht_start)&&light_status!=0){
					ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//����	
					
				}	
				Log.d("temp", "-------LIGHT_OPEN------->");
			}else if(SystemSetActivity.LIGHT_CLOSE.equals(action)){	
				String str_buy_time_end =  AbSharedUtil.getString(context, "buy_time_end","24:00");
				str_buy_time_end = str_buy_time_end.substring(0, 2)+str_buy_time_end.substring(3, 5);
				int buy_time_end = Integer.parseInt(str_buy_time_end);		
				int auto_liht_close = Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_close","2400"));
				int auto_liht_start =Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_start","0000"));
				
				if(auto_liht_close<buy_time_end&&buy_time_end<auto_liht_start){
					ctrlTemp(context,(byte)0,(byte)3,(byte) 0);//�ص�	
				}
				Log.d("temp",auto_liht_close+ "-------LIGHT_CLOSE----"+auto_liht_start+"--->"+buy_time_end);
			}else if(SystemSetActivity.BUY_OPEN.equals(action)){//Ӫҵ��ʼ
				int light_status= AbSharedUtil.getInt(context, "light",0);
				String str_buy_time_start=AbSharedUtil.getString(context, "buy_time_start","00:00");								
				str_buy_time_start = str_buy_time_start.substring(0, 2)+str_buy_time_start.substring(3, 5);
				int buy_time_start = Integer.parseInt(str_buy_time_start);
				if(light_status==1){
					int auto_liht_start =Integer.parseInt(AbSharedUtil.getString(context, "auto_liht_start"));
					if(buy_time_start>auto_liht_start){
						ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//����	
					}								
				}else if(light_status==2){
					ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//����	
				}
				AbSharedUtil.putInt(context, "buy_status", 1);//��ʼӪҵ��־
				if(buyTimeListener!=null){
					buyTimeListener.onListener(true);//��ʼӪҵ
				}				
				Log.d("temp", "-------BUY_OPEN------->");
			}else if(SystemSetActivity.BUY_CLOSE.equals(action)){//Ӫҵ�ر�
				int light_status = AbSharedUtil.getInt(context, "light",0);
				if(light_status==1||light_status==2){						
					ctrlTemp(context,(byte)0,(byte)3,(byte) 0);//�ص�						
				}	
				ctrlTemp(context,(byte)0,(byte)1,(byte) 0);//������
				ctrlTemp(context,(byte)0,(byte)2,(byte) 0);//��������
				ctrlTemp(context,(byte)0,(byte)4,(byte) 0);//�ؼ���	
				AbSharedUtil.putInt(context, "buy_status", 0);//����Ӫҵ��־
				if(buyTimeListener!=null){
					buyTimeListener.onListener(false);//����Ӫҵ
				}
				
				Log.d("temp", "-------BUY_CLOSE------->");
			}else if(SystemSetActivity.ACTION_LIGHT_STOP.equals(action)){
				ctrlTemp(context,(byte)0,(byte)3,(byte) 0);//�ص�	
			}else if(SystemSetActivity.ACTION_LIGHT_AUTO.equals(action)){
				ctrlTemp(context,(byte)1,(byte)3,(byte) 0);//�ص�	
			}
		}
	}
	

	private byte[] getGoodsStatus(){
		
		List<GoodsComInfo>list = data.queryAllGoodsComInfoByKey();
		int size = list.size();
		byte[] goodsStatus = {0,0,0,0};
		for(int i =0;i<size;i++){
			GoodsComInfo ginfo = list.get(i);	
			//�ж��л���û�б�����
			if(ginfo.getGoodsStatus()==GoodsStatus.In_Stock.ordinal()&&ginfo.getGoodsDisable()==GoodsDisable.Undisable.ordinal()){
				goodsStatus[i/8]|=(byte) (0x1<<(i%8));
			}
		}
		return goodsStatus;
	}
	
}
