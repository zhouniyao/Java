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
 *���Գ���һ��TextView��activity��ģ��
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
        					WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��,�������Ȼ���ڡ�
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//����ģʽ
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//�ɹ��������������
        /*����activity����ʾ����*/
        setContentView(R.layout.activity_framelayout);   //���д���ע��ڷ�λ��
        textView = (TextView) findViewById(R.id.copyLeft);        
        textView2 = (TextView) findViewById(R.id.copyRight);
        ll = (LinearLayout) findViewById(R.id.glPlane);
        
//        mGLSurfaceView = new MyGLSurfaceView(this, renderer);
//		  // Request an OpenGL ES 2.0 compatible context.
//        mGLSurfaceView.setEGLContextClientVersion(2);  
//	    ll.addView(mGLSurfaceView);   
//        mGLSurfaceView.requestFocus();//��ȡ����
//        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
//        mGLSurfaceView.setFocusableInTouchMode(false); 
        
        
        /**ʵ���̼߳�ͨ��*/
        /*
         * Android����Handlerʵ���̼߳��ͨ�����UI����
         */
        hd = new Handler(){
    		//�ڲ���
    		/*
    		 * �������ʵ�����������ȥ������Ϣ
    		 */
    		public void handleMessage(Message msg) {//��дhandlerMessage����
    			switch (msg.what) {
    			case 0:
    				Bundle b = msg.getData();//���Bundle����
    				String str = b.getString("msg");//ͨ�����������Ϣ
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
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//�ɹ��������������
			}
		});
    	
    	textView2.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//�ɹ��������������
    		}
    	});
    	
	}
}
