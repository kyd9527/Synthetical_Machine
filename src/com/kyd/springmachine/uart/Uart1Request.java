package com.kyd.springmachine.uart;

import com.kyd.library.hardware.Request;
import com.kyd.library.hardware.Response;
import com.kyd.library.hardware.UartResponse;
import com.kyd.library.util.AbCrc16;

public class Uart1Request extends Request{

	@Override
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		byte[] data = getData();
		if (data == null) {
			return null;
		}
		int len = data.length;
		byte[] msg = new byte[7 + len];
		byte[] crc = null;
		msg[0] = HEAD1;
		msg[1] = HEAD2;// 头部信息，固定不变
		msg[2] = getMachineType();
		msg[3] = getFuncBit();
		msg[4] = getCommBit();
		for (int i = 0; i < data.length; i++) {
			msg[i + 5] = data[i];
		}
		crc = AbCrc16.crc16(msg, 5 + len);// 计算crc值
		msg[5 + len] = crc[0];// crc低8位
		msg[6 + len] = crc[1];// crc高8位
		return msg;
	}

	@Override
	protected Response parseResponse(byte[] msg) {
		// TODO Auto-generated method stub
		return  new UartResponse(msg);
	}

}
