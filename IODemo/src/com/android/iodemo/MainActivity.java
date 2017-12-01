package com.android.iodemo;

import java.io.File;
import java.io.FileInputStream;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button ok = (Button) findViewById(R.id.button1);
		
		ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				EditText edt = (EditText) findViewById(R.id.editText1);
				String nr = loadText(edt.getText().toString().trim());
				EditText edt2 = (EditText) findViewById(R.id.editText2);
				edt2.setText(nr);
			}
		});
	}

	/**
	 * 加载sd卡文件
	 */
	public String loadText(String name){
		String nr = null;
		try{
			File f = new File("/sdcard/" + name);
//			File f = new File("/sdcard/upper.txt");
			byte[] buffer = new byte[(int)f.length()];
			FileInputStream fis = new FileInputStream(f);
			fis.read(buffer);//读入文件
			fis.close();
			
			nr=new String(buffer, "utf-8");
			nr=nr.replaceAll("\\r\\n", "\n");//替换换行符
			
		}catch(Exception e){
			Toast.makeText(getBaseContext(), "文件不存在", Toast.LENGTH_LONG).show();
		}
		return nr;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
