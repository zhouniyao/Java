package com.example.fullscreen;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

	Window window  = null;
	Button button2 = null;
	Button button3 = null;
	Button button1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        window = getWindow();
        /*����һ������ȫ��,�������Ȼ���ڡ�*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��,�������Ȼ���ڡ�
        
        setContentView(R.layout.activity_main);

      
        
        /*��������*/
        button1 = (Button) findViewById(R.id.button1); /*����ȫ��*/
        button1.setOnClickListener(new OnClickListener() {
        	
        	/*��ȡ��ǰϵͳ��android�汾��*/
        	int currentapiVersion=android.os.Build.VERSION.SDK_INT;
			@Override
			public void onClick(View v) {
				if (currentapiVersion >= 14) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//ȫ����ʾ
				}
			}
		});
        button2 = (Button) findViewById(R.id.button2); /*�˳�ȫ��*/
        button2.setOnClickListener(new OnClickListener() {
			/*������Ļ����λ�ã������˳�ȫ��ģʽ*/
			@Override
			public void onClick(View v) {
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//���Բ�Ҫ���д���
			}
		});
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*�ر�Ӧ�ó���*/
				finish(); //�˳���ǰactivity��OK
			}
		});

    }



    	
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
