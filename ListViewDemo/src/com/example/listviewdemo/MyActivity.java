package com.example.listviewdemo;

import java.util.ArrayList;
import java.util.HashMap;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 */
public class MyActivity extends Activity {
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
				R.string.son,
				R.string.author,
				R.string.baba,
				R.string.mum,
				R.string.gege,
				R.string.jiejie,
				R.string.son,
				R.string.author,
		};
		ListView lv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lv = (ListView) findViewById(R.id.listView01);
		lv.setAdapter(getMenuAdapter( msgIds, drawableIds));
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ShowToast") @Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				TextView tv = (TextView) findViewById(R.id.textView01);
				StringBuilder sb = new StringBuilder();
				String string = null;
				sb.append(getResources().getText(R.string.app_name));
				sb.append(":");
				
				switch (arg2) {
				case 0:
					Toast.makeText(MyActivity.this, "第几个元素：" +arg2, Toast.LENGTH_SHORT).show();
					break;
				case 1:
					
					break;
				case 2:
					
					break;
				case 3:
					
					break;
				case 4:
					
					break;
				case 5:
					
					break;
				default:
					break;
				}
				string = ""+arg2;
				sb.append(string);
				String stemp = sb.toString();
				tv.setText(stemp);
			}//End OnItemClick
		});
		
	}
	
	
	/**
	 * @Title: getMenuAdapter
	 * @Description: TODO(SimpleAdapter演示)
	 * @return: SimpleAdapter   
	 * @throws
	 */
	private SimpleAdapter getMenuAdapter(int[] NameArray, int[] imageResourceArray) {
		
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < imageResourceArray.length && i < NameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemName", getResources().getText(NameArray[i]));
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.menu_stye, new String[] { "itemImage", "itemName" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }
	/**
	 * 
	 * @Title: getMenuAdapter
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param menuNameArray
	 * @param imageResourceArray
	 * @return   
	 * @return: SimpleAdapter   
	 * @throws
	 */
	  private SimpleAdapter getMenuAdapter(String[] menuNameArray,
	            int[] imageResourceArray) {
	        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	        for (int i = 0; i < menuNameArray.length; i++) {
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("itemImage", imageResourceArray[i]);
	            map.put("itemText", menuNameArray[i]);
	            data.add(map);
	        }
	        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
	                R.layout.menu_stye, new String[] { "itemImage", "itemText" },
	                new int[] { R.id.item_image, R.id.item_text });
	        return simperAdapter;
	    }
}
