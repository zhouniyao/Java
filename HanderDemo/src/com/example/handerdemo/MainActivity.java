package com.example.handerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
/**
 * 创建继承Activity的主控制类
 */
public class MainActivity extends Activity {
	TextView tv;
	Handler hd = new Handler(){
		//内部类
		/*
		 * 子类必须实现这个方法，去接收消息
		 */
		public void handleMessage(Message msg) {//重写handlerMessage方法
			switch (msg.what) {
			case 0:
				Bundle b = msg.getData();//获得Bundle对象
				String str = b.getString("msg");//通过键，获得消息
				tv.setText(str);
				break;

			default:
				break;
			}
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv =  (TextView) findViewById(R.id.textView01);
        new MyThread(this).start();//开启线程
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
