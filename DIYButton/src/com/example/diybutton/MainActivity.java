package com.example.diybutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	DiyToggle diy = null;
	TextView tv ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		diy = (DiyToggle) findViewById(R.id.mTog);
		tv = (TextView) findViewById(R.id.mTvCenter);
		
		diy.setBackground(R.drawable.taiji);
		diy.setToggleClickListener(new OnToggleClickListener(){


			@Override
			public void onDoClick() {
				tv.setText("我是最棒的");
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
