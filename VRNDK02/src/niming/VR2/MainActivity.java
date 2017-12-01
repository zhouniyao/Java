package niming.VR2;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.vrndk01.R;

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
 * Ӧ�ó���ĵ�һ������
 *
 */
public class MainActivity extends Activity{
	private Intent intent = null;
	//ͼƬ��Դ
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
		//�ַ�������
		String[] msgIds ={
				"BingMaYong",
				"Car",
				"Car22",
				"Dragon",
				"ElvyTower",
				"����",
				"Library",
				"BezierSurface",
				"Ship",
				"WindWheel",
				"Fo",
		};
		ListView lv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//���ر�����
		
		lv = new ListView(this);
		lv.setAdapter(getMenuAdapter( msgIds, drawableIds));//ΪListView attached Adapter
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ShowToast") @Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				switch (arg2) {
				case 0:
					startActivity(new Intent(MainActivity.this, BingMaYong.class));//���������
					//����dragon����
//					intent = new Intent(MainActivity.this, KaoGu.class);
//					startActivity(intent);
					break;
				case 1:
					startActivity(new Intent(MainActivity.this, Car.class));//
					break;
				case 2:
					startActivity(new Intent(MainActivity.this, Car22.class));//
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 3:
//					startActivity(new Intent(MainActivity.this, PuSa.class));//����handler����UI
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 4:
//					startActivity(new Intent(MainActivity.this, LibraryObj.class));//car OBJ3D�ļ�����
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 5:
//					startActivity(new Intent(MainActivity.this, SheepOBJ.class));//SheepOBJ3D�ļ�����
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					break;
				case 6:
//					startActivity(new Intent(MainActivity.this, HighHeelShoePLY.class));//OBJ3D�ļ�����
					
					
					break;
				case 7:
					startActivity(new Intent(MainActivity.this, BezierSurfaceDemo.class));//OBJ3D�ļ�����
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
	 * @Description: TODO(SimpleAdapter��ʾ)
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
	 * @Description: TODO(������һ�仰�����������������)
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