package com.example.listviewdemo;

import java.util.logging.Logger;

import android.os.Bundle;
import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//图片资源
	int[] drawableIds = {
			R.drawable.m1,
			R.drawable.m2,
			R.drawable.m3,
			R.drawable.m4,
			R.drawable.m5,
			R.drawable.m6,
			R.drawable.m7,
			R.drawable.m8,
			R.drawable.m9,
			R.drawable.m10,
			R.drawable.m11,
			R.drawable.m12,
	};
	//字符串数组
	int[] msgIds ={
			R.string.baba,
			R.string.mum,
			R.string.gege,
			R.string.jiejie,
			R.string.son
	};
	
	ListView lv = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        lv = (ListView) findViewById(R.id.listView01);
        //为ListView准备适配器
        BaseAdapter ba = new BaseAdapter() {
        	
			public int getCount() {
				return 5; //共5个选项
			}
			public Object getItem(int arg0) {
				return null;
			}
			public long getItemId(int arg0) {
				return 0;
			}
			/**
			 * 代码书写布局LinearLayout，包含一个ImageView和TextView
			 */
			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				//初始化LinearLayout
				LinearLayout ll = new LinearLayout(MainActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setPadding(5, 5, 5, 5);//四周留白
				//初始化ImageView
				ImageView ii = new ImageView(MainActivity.this);
				ii.setImageDrawable(getResources().getDrawable(drawableIds[arg0]));
				ii.setScaleType(ImageView.ScaleType.FIT_XY);
				ii.setLayoutParams(new Gallery.LayoutParams(140, 140));
				ll.addView(ii);
				//初始化TextView
				TextView tv = new TextView(MainActivity.this);
				tv.setText(getResources().getText(msgIds[arg0]));
				tv.setTextSize(24);
				tv.setTextColor(getResources().getColor(android.R.color.black));
				tv.setPadding(5, 5, 5, 5);//四周留白
				tv.setGravity(Gravity.LEFT);
				ll.addView(tv);
				
				return ll;
			}
		};
		lv.setAdapter(ba);
		
		lv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
//				TextView tv = (TextView) findViewById(R.id.textView01);
//				LinearLayout ll = (LinearLayout) arg1;
//				TextView tvn = (TextView) ll.getChildAt(1);
//				StringBuilder sb = new StringBuilder();
//				sb.append(getResources().getText(R.string.app_name));
//				sb.append(":");
//				sb.append(tvn.getText());
//				String stemp = sb.toString();
//				tv.setText(stemp);
//				Log.v("测试：", stemp);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
//				Toast.makeText(arg1.getContext(), "第几个元素：" +arg2, Toast.LENGTH_SHORT);
				TextView tv = (TextView) findViewById(R.id.textView01);
				LinearLayout ll = (LinearLayout) arg1;
				TextView tvn = (TextView) ll.getChildAt(1);
				StringBuilder sb = new StringBuilder();
				sb.append(getResources().getText(R.string.app_name));
				sb.append(":");
				sb.append(tvn.getText());
				String stemp = sb.toString();
				tv.setText(stemp);
				Log.v("测试：", stemp);
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
