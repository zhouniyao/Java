package niming.virsualreality02;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 应用程序的第一个界面
 *
 */
public class MainActivity extends Activity{
	private Intent intent = null;
	//图片资源
		int[] drawableIds = {
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,
				R.drawable.m1,

		};
		//字符串数组
		String[] msgIds ={
				"DragonOBJ",
				"womenOBJ",
				"触碰滚动Demo",
				"HandlerTest",
				"CarOBJ",
				"SheepOBJ",
				"Statue",
				"HighHeelShoe",
				"Ship",
				"WindWheel",
		};
		ListView lv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
		
		lv = new ListView(this);
		lv.setAdapter(getMenuAdapter( msgIds, drawableIds));//为ListView attached Adapter
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ShowToast") @Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				switch (arg2) {
				case 0:
					//激活dragon例子
					intent = new Intent(MainActivity.this, dragonOBJ.class);
					startActivity(intent);
					break;
				case 1:
					startActivity(new Intent(MainActivity.this, womenOBJ.class));//无纹理测试
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 2:
					startActivity(new Intent(MainActivity.this, TouchRolling.class));//
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 3:
					startActivity(new Intent(MainActivity.this, HandlerCube.class));//利用handler更新UI
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 4:
					startActivity(new Intent(MainActivity.this, OBJtest.class));//car OBJ3D文件导入
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 5:
					startActivity(new Intent(MainActivity.this, SheepOBJ.class));//SheepOBJ3D文件导入
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 6:
//					startActivity(new Intent(MainActivity.this, HighHeelShoePLY.class));//OBJ3D文件导入
					
					
					break;
				default:
					break;
				}
			}//End OnItemClick
		});
		
		setContentView(lv);
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
	 * @param String[] NameArray
	 * @param int[] imageResourceArray
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