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
		msg[1] = HEAD2;// ͷ����Ϣ���̶�����
		msg[2] = getMachineType();
		msg[3] = getFuncBit();
		msg[4] = getCommBit();
		for (int i = 0; i < data.length; i++) {
			msg[i + 5] = data[i];
		}
		crc = AbCrc16.crc16(msg, 5 + len);// ����crcֵ
		msg[5 + len] = crc[0];// crc��8λ
		msg[6 + len] = crc[1];// crc��8λ
		return msg;
	}

	@Override
	protected Response parseResponse(byte[] msg) {
		// TODO Auto-generated method stub
		return  new UartResponse(msg);
	}

}
