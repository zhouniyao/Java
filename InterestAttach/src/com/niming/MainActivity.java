package com.niming;



import android.os.Bundle;
import android.app.Activity;
import com.iflytek.facedemo.R;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Intent intent = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button staticBtn = (Button) findViewById(R.id.btnStatic);
		Button dynamicBtn = (Button) findViewById(R.id.btnDynamic);
		
		staticBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, StaticDetect.class);
				startActivity(intent);
			}
		});
		
		dynamicBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, DynamicDetect.class);
				startActivity(intent);
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
