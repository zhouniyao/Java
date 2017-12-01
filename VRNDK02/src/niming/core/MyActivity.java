package niming.core;

import com.example.vrndk01.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 *可以呈现一对TextView的activity的模板
 */
public class MyActivity extends Activity {
	protected GLSurfaceView mGLSurfaceView;
	protected LinearLayout ll;
	protected TextView textView ;
	protected TextView textView2 ;
	protected Handler hd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        					WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏,虚拟键仍然存在。
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏模式
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//成功的隐藏了虚拟键
        /*设置activity的显示内容*/
        setContentView(R.layout.activity_framelayout);   //此行代码注意摆放位置
        textView = (TextView) findViewById(R.id.copyLeft);        
        textView2 = (TextView) findViewById(R.id.copyRight);
        ll = (LinearLayout) findViewById(R.id.glPlane);
        
//        mGLSurfaceView = new MyGLSurfaceView(this, renderer);
//		  // Request an OpenGL ES 2.0 compatible context.
//        mGLSurfaceView.setEGLContextClientVersion(2);  
//	    ll.addView(mGLSurfaceView);   
//        mGLSurfaceView.requestFocus();//获取焦点
//        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
//        mGLSurfaceView.setFocusableInTouchMode(false); 
        
        
        /**实现线程间通信*/
        /*
         * Android利用Handler实现线程间的通信完成UI更新
         */
        hd = new Handler(){
    		//内部类
    		/*
    		 * 子类必须实现这个方法，去接收消息
    		 */
    		public void handleMessage(Message msg) {//重写handlerMessage方法
    			switch (msg.what) {
    			case 0:
    				Bundle b = msg.getData();//获得Bundle对象
    				String str = b.getString("msg");//通过键，获得消息
    				textView.setText(str);
    				textView2.setText(str);
    				break;

    			default:
    				break;
    			}
    		}
    	};//end Handler
    	
    	textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//成功的隐藏了虚拟键
			}
		});
    	
    	textView2.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//成功的隐藏了虚拟键
    		}
    	});
    	
	}
}
