package com.kyd.springmachine.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wlf.filedownloader.FileDownloader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.animation.RotateAnimation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kyd.library.util.AbAppUtil;
import com.kyd.library.util.AbDateUtil;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.JacksonTools;
import com.kyd.springmachine.AppConfig;
import com.kyd.springmachine.AppConfig.OrderStatus;
import com.kyd.springmachine.AppConfig.SyncStatuc;
import com.kyd.springmachine.ControlApp;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.database.DataBaseManager;
import com.kyd.springmachine.msg.Return;
import com.kyd.springmachine.msg.ReturnAd_ver;
import com.kyd.springmachine.msg.ReturnAliPay;
import com.kyd.springmachine.msg.ReturnAliPayState;
import com.kyd.springmachine.msg.ReturnAppUpdate;
import com.kyd.springmachine.msg.ReturnCommodityInfo;
import com.kyd.springmachine.msg.ReturnHeart;
import com.kyd.springmachine.msg.ReturnHeart.OrderListEntity;
import com.kyd.springmachine.msg.RootAliPay;
import com.kyd.springmachine.msg.RootAliPayRefund;
import com.kyd.springmachine.msg.RootAliPayState;
import com.kyd.springmachine.msg.RootCommodityInfo;
import com.kyd.springmachine.msg.RootGoodsFault;
import com.kyd.springmachine.msg.RootGoodsOut;
import com.kyd.springmachine.msg.RootGoodsSet;
import com.kyd.springmachine.msg.RootHeart;
import com.kyd.springmachine.msg.RootLogin;
import com.kyd.springmachine.msg.RootOpenDoor;
import com.kyd.springmachine.msg.RootOrderExecute;
import com.kyd.springmachine.msg.RootSale;
import com.kyd.springmachine.server.FileDownloaderSever;

import de.greenrobot.greendao.AdVerInfo;
import de.greenrobot.greendao.CommodityInfo;
import de.greenrobot.greendao.GoodsInfo;
import de.greenrobot.greendao.OrdersInfo;
import de.greenrobot.greendao.SetOpenDoor;

public class NetWorkUtil { 
	Context context;
	Timer heartTimer;
	DataBaseManager dbManager;
	Timer payTimer;
	PayTask payTask;
	HeartTask heartTask;
	
	public static boolean NetState = false;

	public NetWorkUtil(Context context) {
		this.context = context;
		dbManager = DataBaseManager.getInstance(context);

	}

	/**
	 * ��¼
	 */
	public void sendLogin(final String name) {
		 NetState = false;
		 ControlApp.queue.cancelAll("1001");
		 ControlApp.queue.cancelAll("1002");
		 ControlApp.queue.cancelAll("1003");
		 ControlApp.queue.cancelAll("1004");
		 ControlApp.queue.cancelAll("1005");
		
		 ControlApp.queue.cancelAll("1007");
		 ControlApp.queue.cancelAll("1008");
		 ControlApp.queue.cancelAll("1009");
		 ControlApp.queue.cancelAll("1010");
		 ControlApp.queue.cancelAll("1011");
		 ControlApp.queue.cancelAll("1012");
		 ControlApp.queue.cancelAll("1013");
		 ControlApp.queue.cancelAll("1014");
		 ControlApp.queue.cancelAll("1015");
		 ControlApp.queue.cancelAll("1016");
		 ControlApp.queue.cancelAll("1017");
		 ControlApp.queue.cancelAll("1018");
		 ControlApp.queue.cancelAll("1019");
		 ControlApp.queue.cancelAll("1020");
		 ControlApp.queue.cancelAll("1021");
		 ControlApp.queue.cancelAll("1022");
		String machine_number=AbSharedUtil.getString(context, "machine_number");
		String password=AbSharedUtil.getString(context, "password");
		if (machine_number != null&& password != null) {
		String url = AppConfig.url("v=1&m=1001");
		RootLogin login = new RootLogin();

		login.setMachine_id(machine_number);
		login.setSecret(password);

		final String strjson = JacksonTools.creatJsonString(login,new TypeReference<RootLogin>() {});

		AbLogUtil.i("send1001", strjson);
		// ����StringRequest�������ַ������������ʽΪPOST��
		StringRequest request = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					// ����ɹ���ִ�еĺ���
					@Override
					public void onResponse(String s) {
						// ��ӡ��POST���󷵻ص��ַ���
						AbLogUtil.i("post1001", s);
						Return r = new Return();
						r = (Return) JacksonTools.parseJsonObject(s,new TypeReference<Return>() {});
						if (r != null) {

							if (r.getResult() == 0) {// �ɹ�
								NetState = true;
								AbToastUtil.showToast(context, name+"��½�ɹ�");
								AbSharedUtil.putString(context, "token",r.getToken());
								Intent intent = new Intent(context,FileDownloaderSever.class);
								context.startService(intent);
								sendGoodsSet();
								sendsale();
								sendFaultClean();
							} else {// ʧ��

							}
						}
						openHeartTime();
					}
				}, new Response.ErrorListener() {
					// ����ʧ��ʱִ�еĺ���
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						NetState = false;
						// Log.e("post error", volleyError.getMessage());
						openHeartTime();   
					}
				}) {
			// ������������
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("PostDATA", AppConfig.PostData(strjson));
				return hashMap;
			}
		};
		// ���ø�����ı�ǩ
		request.setTag("1001");
		// ��������ӵ�������
		ControlApp.queue.add(request);

		 }
	}

	/**
	 * ����Э��
	 */
	public void sendHeart() {
		String url = AppConfig.url("v=1&m=1002&t="+ AbSharedUtil.getString(context, "token"));

		RootHeart heart = new RootHeart();

		heart.setTimestamp(AbDateUtil.getCurrentUnix());

		final String strjson = JacksonTools.creatJsonString(heart,new TypeReference<RootHeart>() {});

		AbLogUtil.i("send1002", strjson);
		// ����StringRequest�������ַ������������ʽΪPOST��
		StringRequest request = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					// ����ɹ���ִ�еĺ���
					@Override
					public void onResponse(String s) {
						// ��ӡ��POST���󷵻ص��ַ���
						AbLogUtil.i("post1002", s);
						ReturnHeart r = new ReturnHeart();
						r = (ReturnHeart) JacksonTools.parseJsonObject(s,
								new TypeReference<ReturnHeart>() {
								});
						if (r != null) {

							if (r.getResult() == 0) {// �ɹ�
								/* ��� ���� */
								if (r.getAd_ver() != AbSharedUtil.getInt(context, "Adver")&&AbSharedUtil.getBoolean(context, "machine_adver", true)==true) {// ���汾
									AbSharedUtil.putInt(context, "Adver",r.getAd_ver());
									sendAd_ver();
								}
								/* �汾 ���� */
								 if(AbAppUtil.getVerCode(context)!=r.getApp_ver())
								 {//�汾
									 sendAppUpdate();
								 }
								/* �豸����״̬ */
								if (r.getMachine_state().equals("run")) {
									// System.out.println("run");
								} else if (r.getMachine_state().equals("stop")) {

								} else if (r.getMachine_state().equals("close")) {

								}

								// ���û���
								dbManager.disableGoodsInfo();
								if (!AbStrUtil.isEmpty(r.getDisable_line())) {
									String[] strArr = r.getDisable_line().split(",");
									for (String string : strArr) {
										dbManager.disableGoodNumber(Integer.parseInt(string));
									}
								}
								// ����
								int size = r.getOrder_list().size();
								if (size != 0) {
									String msg = "";
									List<ReturnHeart.OrderListEntity> list = r
											.getOrder_list();
									for (OrderListEntity orderListEntity : list) {
										if (orderListEntity.getAction().equals(
												"editprice")) {// �༭�۸�
											dbManager.updateGoodsPrice(
													orderListEntity
															.getLine_no(),
													orderListEntity
															.getGoods_price());
											msg = msg + orderListEntity.getId()
													+ ",";
										}
									}
									msg = msg.substring(0, msg.length() - 1);
									sendOrderExecute(msg);

								}
								Intent intent = new Intent("KEY_ACTION");
								intent.putExtra("action", "UPDATE");
								context.sendBroadcast(intent);
							} else if (r.getResult() == 400007) {// ʧ��
								 sendLogin("1002 tokenʧЧ");
							}
						}
					}
				}, new Response.ErrorListener() {
					// ����ʧ��ʱִ�еĺ���
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						// Log.e("post error", volleyError.getMessage());
						// sendLogin("1002����ʧ��");
						AbSharedUtil.putString(context, "token","1002");
					}
				}) {
			// ������������
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("PostDATA", AppConfig.PostData(strjson));
				return hashMap;
			}
		};
		// ���ø�����ı�ǩ
		request.setTag("1002");
		// ��������ӵ�������
		ControlApp.queue.add(request);
	}

	/**
	 * ȱ��֪ͨ
	 */
	public void sendGoodsOut() {
		if (NetWorkUtil.NetState == true) {
			String lineList = "";
			String url = AppConfig.url("v=1&m=1003&t="
					+ AbSharedUtil.getString(context, "token"));

			RootGoodsOut goodsOut = new RootGoodsOut();

			List<GoodsInfo> list = dbManager.queryGoodOut();
			for (GoodsInfo goodsInfo : list) {
				lineList = lineList + goodsInfo.getGoodsNumber() + ",";
			}
			lineList = lineList.substring(0, lineList.length() - 1);
			// �ϱ���Ϣ
			goodsOut.setLinelist(lineList);

			final String strjson = JacksonTools.creatJsonString(goodsOut,
					new TypeReference<RootGoodsOut>() {
					});

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1003", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�

								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1003");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1003");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}

	/**
	 * �����ϴ�
	 */
	public void sendGoodsSet() {
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1004&t="
					+ AbSharedUtil.getString(context, "token"));

			List<GoodsComInfo> listInfo = dbManager
					.queryAllGoodsComInfoByGoodsNumber();

			List<RootGoodsSet> list = new ArrayList<RootGoodsSet>();
			int i = 1;
			for (GoodsComInfo goodsComInfo : listInfo) {
				RootGoodsSet root = new RootGoodsSet();
				root.setLineno(goodsComInfo.getGoodsNumber());
				root.setBarcode(goodsComInfo.getGoodsCode());
				root.setGoods_name(goodsComInfo.getGoodsName());
				root.setMax(goodsComInfo.getGoodsStock());
				root.setPrice(goodsComInfo.getGoodsPrice());
				root.setStock(goodsComInfo.getGoodsCapacity());
				list.add(root);
			}

			final String strjson = JacksonTools.creatJsonString(list,
					new TypeReference<List>() {
					});

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1004", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�

								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {

							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1004");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1004");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/**
	 * �������֪ͨ
	 */
	public void sendGoodsReplenish() {
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1005&t="
					+ AbSharedUtil.getString(context, "token"));

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1005", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									AbToastUtil.showToast(context, "���ϴ�����ȫ����Ϣ");
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1005");
						}
					});
			// ���ø�����ı�ǩ
			request.setTag("1005");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}

	/** ���ۼ�¼ */
	public void sendsale() {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1006&t="
					+ AbSharedUtil.getString(context, "token"));

			List<OrdersInfo> orderInfo = dbManager.queryNoSyncStatuc();
			List<RootSale> list = new ArrayList<RootSale>();

			for (OrdersInfo ordersInfo : orderInfo) {
				RootSale root = new RootSale();
				// ������Ϣ
				RootSale.detailDataBean bean = new RootSale.detailDataBean();
				bean.setLineno(ordersInfo.getGoodsID());
				bean.setBarcode(ordersInfo.getGoodsCode());
				bean.setPrice(ordersInfo.getTotalPrice());
				bean.setSalenum(ordersInfo.getTotalNum());
				bean.setSubtotal(ordersInfo.getTotalPrice());
				bean.setGoods_name(ordersInfo.getGoodsName());

				List<RootSale.detailDataBean> l = new ArrayList<RootSale.detailDataBean>();
				l.add(bean);

				root.setSaleno(ordersInfo.getOrderNum());
				root.setTotal_amount(bean.getSubtotal());
				root.setDetail_data(l);
				switch (ordersInfo.getPayType()) {
				// ֧��������0���ֽ� ��1��֧������2��΢�ţ�
				case 0:// ֧����
					root.setPay_channel(1);
					break;
				case 1:// ΢��
					root.setPay_channel(2);
					break;
				case 2:// �ֽ�
					root.setPay_channel(0);
					break;
				default:
					break;
				}
				root.setSaletime(ordersInfo.getTime());
				list.add(root);

			}

			final String strjson = JacksonTools.creatJsonString(list,
					new TypeReference<List<RootSale>>() {
					});

			Log.i("send1006", strjson);

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1006", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									dbManager.updateOrderSyncStatuc();
								} else if (r.getResult()==400005) {
									sendGoodsFault("������Ϣ�ϴ�����",null);
								}
								else	{// ʧ��
									// sendLogin();
									
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							 // Log.e("post error", volleyError.getMessage());
							// sendLogin();
						   //	AbSharedUtil.putString(context, "token","1006");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1006");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/** ���汾 */
	public void sendAd_ver() {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1010&t="
					+ AbSharedUtil.getString(context, "token"));

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1010", s);
							ReturnAd_ver r = new ReturnAd_ver();
							r = (ReturnAd_ver) JacksonTools.parseJsonObject(s,
									new TypeReference<ReturnAd_ver>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									List<ReturnAd_ver.detailDataBean> bean = r
											.getDetail_data();

									for (ReturnAd_ver.detailDataBean detailDataBean : bean) {
										String url = detailDataBean
												.getPath_url();
										if (isDownloadUrl(url)) {
											final AdVerInfo a = new AdVerInfo();
											a.setAdNumber(AbSharedUtil.getInt(
													context, "Adver"));
											a.setPathUrl(url);
											a.setPlayTime(detailDataBean
													.getPlaytime());
											a.setSoureName(detailDataBean
													.getSoure_name());
											a.setSoureType(detailDataBean
													.getSoure_type());
											a.setStatus(0);

											FileDownloader.start(url);

											dbManager.saveAdVerInfo(a);
										}
									}

								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1010");
						}
					});
			// ���ø�����ı�ǩ
			request.setTag("1010");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}

	/** APP���� */
	public void sendAppUpdate() {
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1011&t="
					+ AbSharedUtil.getString(context, "token"));

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1011", s);
							ReturnAppUpdate r = new ReturnAppUpdate();
							r = (ReturnAppUpdate) JacksonTools.parseJsonObject(
									s, new TypeReference<ReturnAppUpdate>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									// �Զ����´���
									
									FileDownloader.start(r.getDownurl());
									
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1011");
						}
					});
			// ���ø�����ı�ǩ
			request.setTag("1011");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}

	/** ֧������ά�� */
	public void sendAliPay(final RootAliPay aliPay,final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1007&t="+ AbSharedUtil.getString(context, "token"));

			final String strjson = JacksonTools.creatJsonString(aliPay,
					new TypeReference<RootAliPay>() {
					});

			// System.out.println(strjson);

			Log.i("send1007", strjson);
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1007", s);
							ReturnAliPay r = new ReturnAliPay();
							r = (ReturnAliPay) JacksonTools.parseJsonObject(s,
									new TypeReference<ReturnAliPay>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									// �Զ����´���
									listener.Message(r.getResult(),r.getCode_url());
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1012");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1007");
			// ��������ӵ�������
			
			ControlApp.queue.add(request);
		}

	}

	/** ֧������ά����Խӿ� */
	public void sendAliPayTest(final RootAliPay aliPay,final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1020&t="+ AbSharedUtil.getString(context, "token"));

			final String strjson = JacksonTools.creatJsonString(aliPay,
					new TypeReference<RootAliPay>() {
					});

			// System.out.println(strjson);

			Log.i("send1020", strjson);
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1020", s);
							ReturnAliPay r = new ReturnAliPay();
							r = (ReturnAliPay) JacksonTools.parseJsonObject(s,
									new TypeReference<ReturnAliPay>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									// �Զ����´���
									listener.Message(r.getResult(),r.getCode_url());
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1020");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1020");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/** ֧�����˿� */
	public void sendAliPayRefund(RootAliPayRefund root,final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1009&t="+ AbSharedUtil.getString(context, "token"));
			final String strjson = JacksonTools.creatJsonString(root,new TypeReference<RootAliPayRefund>() {});
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1009", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {
									if (listener != null) {
										listener.Message(r.getResult(),r.getMsg());
									}
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1009");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1009");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/**
	 * ΢��֧��
	 * 
	 * @param aliPay
	 * @param listener
	 */
	public void sendWeiXinPay(final RootAliPay aliPay,
			final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1013&t="
					+ AbSharedUtil.getString(context, "token"));

			final String strjson = JacksonTools.creatJsonString(aliPay,
					new TypeReference<RootAliPay>() {
					});

			// System.out.println(strjson);
			Log.i("send1013", strjson);
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1013", s);
							ReturnAliPay r = new ReturnAliPay();
							r = (ReturnAliPay) JacksonTools.parseJsonObject(s,
									new TypeReference<ReturnAliPay>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									// �Զ����´���
									listener.Message(r.getResult(),	r.getCode_url());
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1013");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1013");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}
	
	/**
	 * ΢��֧�����Խӿ�
	 * 
	 * @param aliPay
	 * @param listener
	 */
	public void sendWeiXinPayTest(final RootAliPay aliPay,final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1021&t="
					+ AbSharedUtil.getString(context, "token"));

			final String strjson = JacksonTools.creatJsonString(aliPay,new TypeReference<RootAliPay>() {});

			// System.out.println(strjson);
			Log.i("send1021", strjson);
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1021", s);
							ReturnAliPay r = new ReturnAliPay();
							r = (ReturnAliPay) JacksonTools.parseJsonObject(s,
									new TypeReference<ReturnAliPay>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									// �Զ����´���
									listener.Message(r.getResult(),r.getCode_url());
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1021");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1021");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/** ΢��֧���˿� */
	public void sendWeiXinPayRefund(RootAliPayRefund root,
			final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1015&t="+ AbSharedUtil.getString(context, "token"));
			final String strjson = JacksonTools.creatJsonString(root,
					new TypeReference<RootAliPayRefund>() {
					});
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1015", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {
									if (listener != null) {
										listener.Message(r.getResult(),
												r.getMsg());
									}
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1015");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1015");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/** ������Ϣ */
	public void sendGoodsFault(String msg, final OnHttpListener listener) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1012&t="
					+ AbSharedUtil.getString(context, "token"));

			RootGoodsFault root = new RootGoodsFault();
			root.setErrorinfo(msg);

			final String strjson = JacksonTools.creatJsonString(root,
					new TypeReference<RootGoodsFault>() {
					});

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1012", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									if (listener != null) {
										listener.Message(r.getResult(),r.getMsg());
									}
									
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// sendLogin();
							AbSharedUtil.putString(context, "token","1012");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1012");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}

	}

	/** ����ȫ�� */
	public void sendFaultClean() {
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1016&t="+ AbSharedUtil.getString(context, "token"));

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1016", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									AbToastUtil.showToast(context, "���ϴ�����ȫ����Ϣ");
								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1016");
						}
					});
			// ���ø�����ı�ǩ
			request.setTag("1016");
			// ��������ӵ�������
			ControlApp.queue.add(request);

		}
	}

	/** ������Ϣ */
	public void sendOpenDoor() {
		if (NetWorkUtil.NetState == true) {
		//	if (dbManager.queryOrderCash(idFront).size()!=0) {
			String url = AppConfig.url("v=1&m=1017&t="+ AbSharedUtil.getString(context, "token"));
			List<SetOpenDoor> list=dbManager.getAllSetOpenDoor();
			List<RootOpenDoor> listRoot=new ArrayList<RootOpenDoor>();
			int size=list.size();
			for (int i = 0; i < size-1; i++) {
				int cash=0;
				RootOpenDoor root=new RootOpenDoor();
				SetOpenDoor setOpenDoorNew=list.get(i);//���µ�
				SetOpenDoor setOpenDoorOld=list.get(i+1);
				if (setOpenDoorNew.getSyncStatuc()==SyncStatuc.Finished.ordinal()) {
					break;
				}else {
					cash=dbManager.queryOrderCash(setOpenDoorOld.getOrderId(), setOpenDoorNew.getOrderId());
				}
				root.setCash(cash);
				root.setOpentime(setOpenDoorNew.getOpenTime());
				listRoot.add(root);
			}
			final String strjson = JacksonTools.creatJsonString(listRoot,
					new TypeReference<List>() {
					});
			// ����StringRequest�������ַ������������ʽΪPOST��
			Log.i("send1017", strjson);
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1017", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {
								  dbManager.updateSetOpenDoorSyncStatuc();   
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1017");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1017");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		    }
	//	}
	}

	/** ��Ʒ��Ϣ���� */
	public void sendCommodityInfo(final OnHttpListener listener) {
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1018&t="+ AbSharedUtil.getString(context, "token"));
			RootCommodityInfo root = new RootCommodityInfo();
			Integer number = dbManager.queryMaxSerial();
			if (number == null) {
				root.setLastid(0);
			} else {
				root.setLastid(number);
			}

			final String strjson = JacksonTools.creatJsonString(root,
					new TypeReference<RootCommodityInfo>() {
					});
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1018", s);
							ReturnCommodityInfo r = new ReturnCommodityInfo();
							r = (ReturnCommodityInfo) JacksonTools.parseJsonObject(s,new TypeReference<ReturnCommodityInfo>() {});
							if (r != null) {

								if (r.getResult() == 0) {
									List<CommodityInfo> list = new ArrayList<CommodityInfo>();
									for (ReturnCommodityInfo.DetailDataBean bean : r.getDetail_data()) {
										CommodityInfo com = new CommodityInfo();
										com.setCommType(0);
										com.setGoodsSerial(bean.getId());
										com.setGoodsCode(bean.getGoods_barcode());
										com.setName(bean.getGoods_name());
										com.setPrice(bean.getGoods_price());
										list.add(com);
									}
									dbManager.saveCommodityList(list);
									if (listener != null) {
										listener.Message(r.getResult(), "OK");
									}
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1018");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1018");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}

	/**
	 * ִ������
	 * 
	 * @param msg
	 */
	public void sendOrderExecute(String msg) {
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1019&t="+ AbSharedUtil.getString(context, "token"));

			RootOrderExecute root = new RootOrderExecute();

			// �ϱ���Ϣ
			root.setOrderlist(msg);

			final String strjson = JacksonTools.creatJsonString(root,
					new TypeReference<RootOrderExecute>() {
					});

			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1019", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�

								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1019");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1019");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}

	/**
	 * ֧����ȡ���ӿ�
	 * @param saleno the saleno
	 */
	public void sendAliPayCancel(String saleno){
		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1022&t="+ AbSharedUtil.getString(context, "token"));
			RootAliPayState root=new RootAliPayState();
			root.setSaleno(saleno);
			
			final String strjson = JacksonTools.creatJsonString(root,new TypeReference<RootAliPayState>() {});
			
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1022", s);
							Return r = new Return();
							r = (Return) JacksonTools.parseJsonObject(s,
									new TypeReference<Return>() {
									});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�

								} else {// ʧ��
									// sendLogin();
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							AbSharedUtil.putString(context, "token","1022");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1022");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
		
	}
	
	
	/** ֧��״̬ */
	public void sendPayState(String saleno, final OnPayStateListenner listener,final int num) {

		if (NetWorkUtil.NetState == true) {
			String url = AppConfig.url("v=1&m=1023&t="+ AbSharedUtil.getString(context, "token"));

			RootAliPayState r = new RootAliPayState();
			r.setSaleno(saleno);
			final String strjson = JacksonTools.creatJsonString(r,new TypeReference<RootAliPayState>() {});
			// ����StringRequest�������ַ������������ʽΪPOST��
			StringRequest request = new StringRequest(Request.Method.POST, url,
					new Response.Listener<String>() {
						// ����ɹ���ִ�еĺ���
						@Override
						public void onResponse(String s) {
							// ��ӡ��POST���󷵻ص��ַ���
							Log.i("post1023", s);
							ReturnAliPayState r = new ReturnAliPayState();
							r = (ReturnAliPayState) JacksonTools.parseJsonObject(s,new TypeReference<ReturnAliPayState>() {});
							if (r != null) {

								if (r.getResult() == 0) {// �ɹ�
									// �Զ����´���
									if (r.getState() == 0 || num <= 0) {// 0���Ѿ�֧��// 1��δ֧��
										if (listener!=null) {
											listener.PayMessage(r.getChannel(), r.getState());
										}
										closePayTimer();
									}
								}
							}
						}
					}, new Response.ErrorListener() {
						// ����ʧ��ʱִ�еĺ���
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							// Log.e("post error", volleyError.getMessage());
							// sendLogin();
							//AbSharedUtil.putString(context, "token","1023");
						}
					}) {
				// ������������
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("PostDATA", AppConfig.PostData(strjson));
					return hashMap;
				}
			};
			// ���ø�����ı�ǩ
			request.setTag("1023");
			// ��������ӵ�������
			ControlApp.queue.add(request);
		}
	}
	
	
	public boolean isDownloadUrl(String url) {
		Pattern p = Pattern.compile("^((http)|(https))+\\S+((.mp4)(.png)|(.jpg))$");
		Matcher m = p.matcher(url);
		return m.matches();
	}

	public void openHeartTime() {
	//	if (heartTimer == null) {
			heartTimer = new Timer();
			heartTask =new HeartTask();
			
			heartTimer.schedule(heartTask, 1000, 5 * 60 * 1000);
	//	}
	}

	public void closeHeartTime() {
		if (heartTask!=null) {
			heartTask.cancel();
		}
		if (heartTimer != null) {
			heartTimer.cancel();
		}
	}

	public void closePayTimer() {
		ControlApp.queue.cancelAll("1007");
		ControlApp.queue.cancelAll("1013");
		ControlApp.queue.cancelAll("1020");
		ControlApp.queue.cancelAll("1021");
		if (payTask!=null) {
			payTask.cancel();
		}
		if (payTimer!=null) {
			payTimer.cancel();
		}
	}
	
	public void openPayTimer(String saleno, OnPayStateListenner listener){
		payTimer=new Timer();
		payTask=new PayTask(saleno, listener);
		payTimer.schedule(payTask, 5*1000, 5*1000);
	}

	class PayTask extends TimerTask {

		private String saleno;
		private OnPayStateListenner listener;
		private int num = 10;

		public PayTask(String saleno, OnPayStateListenner listener) {
			// TODO Auto-generated constructor stub
			this.saleno = saleno;
			this.listener = listener;
		}

		@Override
		public void run() {
			if (num>=0) {
				num--;
				sendPayState(saleno, listener, num);
			}
		}

	}

	class HeartTask extends TimerTask {

		@Override
		public void run() {
			sendHeart();
		}
	}

	public interface OnHttpListener {

		/**
		 * 
		 * @param result
		 *            ��Ӧ��
		 * @param msg
		 *            ֧���Ƕ�ά��
		 */
		public void Message(int result, String msg);
	}
	
	public interface OnPayStateListenner{
		/**
		 * 
		 * @param channel  ֧������
		 * @param state    ֧��״̬ 0���Ѿ�֧�� 1��δ֧��
		 */
		public void PayMessage(int channel,int state);
	}
	
}
