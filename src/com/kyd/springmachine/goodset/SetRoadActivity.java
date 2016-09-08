package com.kyd.springmachine.goodset;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.kyd.library.AbActivity;
import com.kyd.library.util.AbFileUtil;
import com.kyd.library.util.AbLogUtil;
import com.kyd.library.util.AbSharedUtil;
import com.kyd.library.util.AbStrUtil;
import com.kyd.library.util.AbToastUtil;
import com.kyd.library.util.AbWifiUtil;
import com.kyd.springmachine.AppConfig;
import com.kyd.springmachine.AppConfig.GoodsStatus;
import com.kyd.springmachine.R;
import com.kyd.springmachine.bean.GoodsComInfo;
import com.kyd.springmachine.database.DataBaseManager;

import de.greenrobot.greendao.CommodityInfo;
import de.greenrobot.greendao.GoodsInfo;

/**
 * 添加货道
 * 
 * @author 8015
 * 
 */
public class SetRoadActivity extends AbActivity implements OnClickListener {

	// title
	private Button bt_title_left;
	private TextView tv_title_name, tv_title_right;

	// 界面
	private EditText et_road_number;
	private EditText et_goods_code;
	private EditText et_goods_name;
	private EditText et_goods_type;
	private EditText et_goods_price;
	private EditText et_goods_stock;
	// private EditText et_pic;
	private Button bt_goods_pic;
	private ImageView iv_goods_pic;

	private LinearLayout ll_key;
	private EditText et_road_key;
	private Switch sw_key;

	private String newPath;

	private DataBaseManager dbManage;

	private boolean flag;
	private boolean sw_flag;
	private CommodityInfo com;//商品编码的goodscominfo
	private int number_flag;
	private GoodsComInfo g;//修改的goodscominfo

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_road);
		initViews();
		initData();
		setListeners();
	}

	@Override
	public void initViews() {
		// title
		bt_title_left = (Button) findViewById(R.id.bt_title_left);
		tv_title_name = (TextView) findViewById(R.id.tv_title_name);
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);

		// 界面
		ll_key = (LinearLayout) findViewById(R.id.ll_key);

		et_road_number = (EditText) findViewById(R.id.et_road_number);
		et_goods_code = (EditText) findViewById(R.id.et_goods_code);
		et_goods_name = (EditText) findViewById(R.id.et_goods_name);
		et_goods_type = (EditText) findViewById(R.id.et_goods_type);
		et_goods_price = (EditText) findViewById(R.id.et_goods_price);
		et_road_key = (EditText) findViewById(R.id.et_road_key);
		et_goods_stock=(EditText) findViewById(R.id.et_goods_stock);
		bt_goods_pic = (Button) findViewById(R.id.bt_goods_pic);
		iv_goods_pic = (ImageView) findViewById(R.id.iv_goods_pic);
		sw_key = (Switch) findViewById(R.id.sw_key);

		tv_title_name.setText("添加货道");
		tv_title_right.setText("保存");
	}

	@Override
	public void initData() {
		dbManage = DataBaseManager.getInstance(this);
		number_flag=getIntent().getIntExtra("goodsNumber", 0);//货道号，无0货道号
		if (number_flag!=0) {
			tv_title_name.setText("修改货道");
			g= dbManage.queryGoodsComInfoByGoodsNumber(number_flag);
			et_road_number.setText(g.getGoodsNumber()+"");
			et_goods_code.setText(g.getGoodsCode());
			et_goods_name.setText(g.getGoodsName());
			et_goods_type.setText(g.getCommType()+"");
			et_goods_price.setText(g.getGoodsPrice()*0.01+"");
			et_road_key.setText(g.getGoodsKey()+"");
			et_goods_stock.setText(g.getGoodsStock()+"");
			et_road_number.setEnabled(false);
			newPath=g.getGoodsPic();
			iv_goods_pic.setImageBitmap(BitmapFactory.decodeFile(newPath));
		//	if (AbStrUtil.isNumber(g.getGoodsKey())) {
				int key=g.getGoodsKey();
				if (g.getGoodsNumber()!=key) {
					ll_key.setVisibility(View.VISIBLE);
		//		}
			}else {
				ll_key.setVisibility(View.VISIBLE);
			}
		}
		flag = AbSharedUtil.getBoolean(this, "sw_key", true);
		if (!flag) {
			ll_key.setVisibility(View.VISIBLE);
		}
		sw_flag = sw_key.isChecked();
		
		
	}

	@Override
	public void setListeners() {
		tv_title_right.setOnClickListener(this);
		bt_title_left.setOnClickListener(this);
		bt_goods_pic.setOnClickListener(this);
		sw_key.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				sw_flag = arg1;
			}
		});
		et_goods_code.addTextChangedListener(watcher);
	}

	// 插入数据库
	private boolean insertDbManager() {
		long goodsId;
		String number = et_road_number.getText().toString().trim();
		String code = et_goods_code.getText().toString().trim();
		String name = et_goods_name.getText().toString();
		String type = et_goods_type.getText().toString();
		String price = et_goods_price.getText().toString();
		String key = et_road_key.getText().toString();
		String stock= et_goods_stock.getText().toString();
		// 判断是否填空值
		if (AbStrUtil.isEmpty(number) || AbStrUtil.isEmpty(code)|| AbStrUtil.isEmpty(name) || AbStrUtil.isEmpty(price)|| AbStrUtil.isEmpty(newPath)||AbStrUtil.isEmpty(stock)) {
			AbToastUtil.showToast(this, "请输入货道信息");
		} else if (dbManage.queryGoodsInfoByNumber(Integer.parseInt(number))!=null&&number_flag==0) {// 判读是否写了此货道号
			AbToastUtil.showToast(this, "已经添加此货道号");
		} else if (!AbStrUtil.isNumber(code) || code.length() != 14) {// 判断是否填写正确的商品编码
			AbToastUtil.showToast(this, "请输入正确的商品编码");
		} else if (sw_flag==false&&dbManage.queryCommodityInfoByCode(code)!=null) {
			AbToastUtil.showToast(this, "数据库已存在此商品编码");
		} else if (!AbStrUtil.isPrice05(price)) {// 判断是否填写正确的商品价格
			AbToastUtil.showToast(this, "请输入正确的商品价格格式");
		} else if (flag != true && (AbStrUtil.isEmpty(key)||dbManage.queryKeyInfoByValue(Integer.parseInt(key))==null)) {//
			AbToastUtil.showToast(this, "请输入按键值");
		} 
		else {
			if (sw_flag&&number_flag==0) {
				System.out.println("flag=0");
				com.setName(name);
				com.setGoodsCode(code);
				com.setPrice((int) (Float.parseFloat(price) * 100));
				com.setCommType(0);
				com.setImgPath(newPath);
				goodsId = dbManage.saveCommodityInfo(com);
			}else {
				System.out.println("else=0");
				CommodityInfo com = new CommodityInfo();
				if (number_flag!=0&&sw_flag) {
					com.setId(g.getComId());
					System.out.println(g.getComId()+" g  ");
					if (this.com!=null) {
						System.out.println(this.com.getId()+"  com");
						com.setId(this.com.getId());
					}
				}
				com.setName(name);
				com.setGoodsCode(code);
				com.setPrice((int) (Float.parseFloat(price) * 100));
				com.setCommType(0);
				com.setImgPath(newPath);
				goodsId = dbManage.saveCommodityInfo(com);
			}
			GoodsInfo good = new GoodsInfo();
			if (number_flag!=0) {
				good.setId(g.getGoodsId());
			//	System.out.println(g.getGoodsId()+"     ");
			}
			System.out.println(goodsId+"  ");
			good.setGoodsID(goodsId);
			good.setKeyID(dbManage.queryKeyInfoByValue(Integer.parseInt(key)).getId());
			good.setGoodsNumber(Integer.parseInt(number));
			if (number_flag!=0) {
				good.setGoodsStatus(g.getGoodsStatus());
			}else {
			//	good.setGoodsStatus();
			}
			good.setGoodsStock(Integer.parseInt(stock));
			if (g!=null) {				
				good.setGoodsCapacity(g.getGoodsCapacity());
			}else {
				good.setGoodsCapacity(0);
			}
			dbManage.saveGoodsInfo(good);
			return true;
		}
		return false;
	}
/**
 * 设置货道信息，货道信息保存在eeprom里面
 * 
 * */
	private void setGoodsInfo(){
		List<Integer> list = dbManage.queryAllGoodsNum();
		Collections.sort(list);
		int line=0,temp=0;
		int size = list.size();
		for(int i =0;i<size;i++){
			line = list.get(size)/100;
			if(temp==line){
				
			}else{
				temp=line;
			}
		}
	}
	
	private void saveDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("是否保存");
		builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if (insertDbManager()) {
					finish();
				}
			}
		});
		builder.setPositiveButton("否", null);
		builder.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			saveDialog();
			break;
		case R.id.bt_title_left:
			finish();
			break;
		case R.id.bt_goods_pic:
			if (!AbStrUtil.isEmpty(et_goods_code.getText().toString())) {
				Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 1);
			} else {
				AbToastUtil.showToast(this, "请先填写商品编号");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA,MediaStore.Images.Media.DISPLAY_NAME };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			// 获取选中图片的路径
			String picturePath = cursor.getString(columnIndex);
			newPath = (AppConfig.UPDATE_COMMODITY_NOTIFY + "/" + et_goods_code.getText().toString());
			AbFileUtil.deleteFile(newPath);
			AbFileUtil.copyFile(picturePath, newPath);
			iv_goods_pic.setImageBitmap(BitmapFactory.decodeFile(newPath));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==4) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (sw_flag) {
				com = dbManage.queryCommodityInfoByCode(s.toString());
				if (com != null) {
					et_goods_name.setText(com.getName());
					if (com.getPrice()!=null) {
						et_goods_price.setText(com.getPrice()*0.01f+"");
					}
					if (com.getCommType()!=null) {
						et_goods_type.setText(com.getCommType()+"");
					}
					newPath=com.getImgPath();
					iv_goods_pic.setImageBitmap(BitmapFactory.decodeFile(newPath));
				}
			}
		}
	};
}
