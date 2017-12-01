package com.niming;

import com.niming.util.SoundUtil;
import com.niming.zfacedetect.R;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Intent intent = null;
	SoundPool soundPool; 
	int scrId1, scrId2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);//»›ƒ…10∏ˆ…˘“Ù
		scrId1 = soundPool.load(MainActivity.this, R.raw.camera, 1);
		scrId2 = soundPool.load(MainActivity.this, R.raw.picture, 1);
		
		
		Button staticBtn = (Button) findViewById(R.id.btnStatic);
		/*…Ë÷√◊÷ÃÂ*/
		Typeface typeFace =Typeface.createFromAsset(getAssets(),"fonts/ADOBEKAITISTD-REGULAR.OTF");
		staticBtn.setTypeface(typeFace);
		
		Button dynamicBtn = (Button) findViewById(R.id.btnDynamic);
		staticBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, StaticDetect.class);
				startActivity(intent);
				soundPool.play(scrId2, 1, 1, 0, 0, 1);
			}
		});
		
		dynamicBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, Menu2.class);
//				intent = new Intent(MainActivity.this, MenuCameraDetect.class);
//				intent = new Intent(MainActivity.this, DynamicDetect.class);
				startActivity(intent);

				soundPool.play(scrId1, 1, 1, 0, 0, 1);
//				SoundUtil aSoundUtil = SoundUtil.getInstance(MainActivity.this);
//				aSoundUtil.playSound(0);
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
