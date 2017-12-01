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
			String msg = "第" + (count++) + "次更改TextView的文字";
			Bundle bd = new Bundle();//创建Bundle对象
			bd.putString("msg", msg);//向Bundle中添加信息，putString(String key, String value)键值对形式
			Message tempMessage = new Message();//创建Message对象
			tempMessage.setData(bd);//向Message中添加数据
			tempMessage.what = 0;
			activity.hd.sendMessage(tempMessage);//【重】调用主控制类中的Handler对象发送消息
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();//打印堆栈信息
			}
		}//END LOOP
	}
}
