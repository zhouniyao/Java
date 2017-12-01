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
        /*方法一：设置全屏,虚拟键仍然存在。*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏,虚拟键仍然存在。
        
        setContentView(R.layout.activity_main);

      
        
        /*方法二：*/
        button1 = (Button) findViewById(R.id.button1); /*设置全屏*/
        button1.setOnClickListener(new OnClickListener() {
        	
        	/*获取当前系统的android版本号*/
        	int currentapiVersion=android.os.Build.VERSION.SDK_INT;
			@Override
			public void onClick(View v) {
				if (currentapiVersion >= 14) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//全屏显示
				}
			}
		});
        button2 = (Button) findViewById(R.id.button2); /*退出全屏*/
        button2.setOnClickListener(new OnClickListener() {
			/*触摸屏幕任意位置，即可退出全屏模式*/
			@Override
			public void onClick(View v) {
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//可以不要这行代码
			}
		});
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*关闭应用程序*/
				finish(); //退出当前activity，OK
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
