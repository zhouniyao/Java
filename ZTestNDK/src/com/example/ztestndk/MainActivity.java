package com.example.ztestndk;


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
	int a=8;
	int b=10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) findViewById(R.id.textView1);
		Button btn1 = (Button) findViewById(R.id.button1);
		Button btn2 = (Button) findViewById(R.id.button2);
		
		btn1.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				int i = JniClient.getSum(a, b);
				tv.setText(Integer.toString(i));
				
			}
		});
		
		btn2.setOnClickListener(new OnClickListener(){

			int[] arr = {3,2,1,5,4,9,6,8,7};
			@Override
			public void onClick(View v) {
				arr = JniClient.sort(arr, 10);
				Toast.makeText(getApplicationContext(), "排序后："+ Integer.toString(arr[0])
						+ " " + Integer.toString(arr[1])
						+ " " + Integer.toString(arr[2])
						+ " " + Integer.toString(arr[3])
						+ " " + Integer.toString(arr[4])
						+ " " + Integer.toString(arr[5])
						+ " " + Integer.toString(arr[6])
						+ " " + Integer.toString(arr[7])
						+ " " + Integer.toString(arr[8])
//						+ " " + Integer.toString(arr[9])
						, Toast.LENGTH_LONG).show();
			}
		});
		
		
	}
	    
//	    private native int getSum(int i, int j);    // 声明Native方法getNum
//	    public static native String getUrl(); 
//	    
	    static {
	        System.loadLibrary("ZTestNDK");    // 载入动态链接库lib…….so
	    }
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

}
