package com.niming.vrndk03;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	 TextView tv = null;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        try{
	        	//����ԭ������
	        	String[] argv = {"MyFamily", "arg1", "arg2"};
//	        	Natives.LibMain(argv);
	        	JniClient.LibMain(argv);
	        }catch(Exception e){
	        	
	        }
	    }
	    static {
	        System.loadLibrary("VRndk03");    // ���붯̬���ӿ�lib����.so,����Ĭ��Ϊ��Ŀ����
	    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}