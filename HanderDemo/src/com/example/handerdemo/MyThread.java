package com.example.handerdemo;

import android.os.Bundle;
import android.os.Message;

public class MyThread extends Thread{
	int count;
	MainActivity activity;
	boolean flag = true;
	public MyThread(MainActivity activity) {
		this.activity = activity;
	}
	public void run(){
		while (flag) {
			if(count >= 10){
				flag = false;
			}
			String msg = "��" + (count++) + "�θ���TextView������";
			Bundle bd = new Bundle();//����Bundle����
			bd.putString("msg", msg);//��Bundle�������Ϣ��putString(String key, String value)��ֵ����ʽ
			Message tempMessage = new Message();//����Message����
			tempMessage.setData(bd);//��Message���������
			tempMessage.what = 0;
			activity.hd.sendMessage(tempMessage);//���ء��������������е�Handler��������Ϣ
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();//��ӡ��ջ��Ϣ
			}
		}//END LOOP
	}
}
