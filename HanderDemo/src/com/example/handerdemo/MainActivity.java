package com.example.handerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
/**
 * �����̳�Activity����������
 */
public class MainActivity extends Activity {
	TextView tv;
	Handler hd = new Handler(){
		//�ڲ���
		/*
		 * �������ʵ�����������ȥ������Ϣ
		 */
		public void handleMessage(Message msg) {//��дhandlerMessage����
			switch (msg.what) {
			case 0:
				Bundle b = msg.getData();//���Bundle����
				String str = b.getString("msg");//ͨ�����������Ϣ
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
        new MyThread(this).start();//�����߳�
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
