package com.example.arrayndk;


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
		
		tv = (TextView) findViewById(R.id.textView1);
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener(){
			
			
			int[] arr = {3,2,1,5,4,9,6,8,7};
			public void onClick(View v) {
				tv.setText("导入的Java数组在原生代码中被修改");
				arr = JniClient.useArray(arr, 10);
				Toast.makeText(getApplicationContext(), "排序后："+ Integer.toString(arr[0])
						+ " " + Integer.toString(arr[1])
						+ " " + Integer.toString(arr[2])
						+ " " + Integer.toString(arr[3])
						+ " " + Integer.toString(arr[4])
						+ " " + Integer.toString(arr[5])
						+ " " + Integer.toString(arr[6])
						+ " " + Integer.toString(arr[7])
						+ " " + Integer.toString(arr[8])
//							+ " " + Integer.toString(arr[9])
						, Toast.LENGTH_LONG).show();
			}
		});
	}
    static {
        System.loadLibrary("ArrayNdk");    // 载入动态链接库lib…….so
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
