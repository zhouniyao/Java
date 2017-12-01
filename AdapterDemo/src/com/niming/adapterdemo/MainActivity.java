package com.niming.adapterdemo;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

//public class MainActivity extends Activity {
//	ArrayList<String> myStringArray = new ArrayList<String>();
//	ListView listview = (ListView) findViewById(R.id.listView1);
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		arrayAdapterShow();
//	}
//
//
//
//	/**
//	 * ArrayAdapter show;
//	 */
//	public void arrayAdapterShow() {
//		myStringArray.add("A");
//		myStringArray.add("B");
//		myStringArray.add("C");
//		myStringArray.add("D");
//		myStringArray.add("E");
//		myStringArray.add("F");
//		myStringArray.add("G");
//		myStringArray.add("H");
//		myStringArray.add("I");
//		/*ArrayAdapter�÷�,Adapter�ǽ����ݰ󶨵�UI�����ϵ��Ž��ࡣAdapter���𴴽���ʾÿ����Ŀ����View���ṩ���²����ݵķ��ʡ�*/
//		int layoutID = android.R.layout.simple_list_item_1;
//		/* ArrayAdapter(Context context, int textViewResourceId, List<T> objects)��װ�����ݣ�
//		 * Ҫװ����Щ���ݾ���Ҫһ������ListView��ͼ������������ݵ������������ߵ����乤����
//		 * ArrayAdapter�Ĺ�����Ҫ��������������Ϊthis,
//		 * �����ļ���ע������Ĳ����ļ����������б��ÿһ�еĲ��֣�android.R.layout.simple_list_item_1��ϵͳ����õĲ����ļ�ֻ��ʾһ�����֣�
//		 * objects:���ݶ��󣬼�����Դ��
//		 */
//		
//		/*adapter�����þ��ǽ�Ҫ���б�����ʾ�����ݺ��б�����������
//		 * �б���ֻ�����ʾ�����ã���ʵ�����Ǽ̳���VIEWGROUP�ࡣ*/
////		ArrayAdapter<String> myAdapterInstance = new ArrayAdapter<String>(this, layoutID, myStringArray);//Adapterװ������
//		/*
//		 * �Զ���һ��custom_array_item.xml�����ļ����б����ոò�����ʾ��
//		 */
//		ArrayAdapter<String> myAdapterInstance = new ArrayAdapter<String>(this, R.layout.custom_array_item, myStringArray);//Adapterװ������
//		
//		listview.setAdapter(myAdapterInstance);//�ؼ���������
//	}
//	
//	
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//}



/**==================================ListActivity=========================================*/
//public class MainActivity extends ListActivity
//{
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        // ����һ���ַ�������
//        String[] arr1 = { "��1���б���", "��1���б���", "��1���б���" };
//        // �������װΪArrayAdapter
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
//                (this, R.layout.custom_array_item, arr1);
//        /*ֱ�ӵ���ListActivity��setListAdapter��������adapter������ʹ��setContentView����*/
//        setListAdapter(adapter1);
//    }
//}

/**====================================SimpleAdapter=======================================*/
/*SimpleAdapter��չ�Ա�ArrayAdapter�ã����Է���ImageView��Button��CheckBox�ȵ����*/
public class MainActivity extends ListActivity
{
    // ��������Դ
    private String[] names = new String[]
            { "List��һ������", "List�ڶ�������", "List����������", "List���ĸ�����"};
    private String[] descr = new String[]
            { "��һ���������������", "�ڶ����������������"
                    , "�������������������", "���ĸ��������������"};
    private int[] images = new int[]
            { R.drawable.ic_launcher , R.drawable.ic_launcher
                    , R.drawable.ic_launcher , R.drawable.ic_launcher};
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // ����һ��List���ϣ�List���ϵ�Ԫ����Map
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        // ��List��������ݣ�����Ԫ����Map��ÿ��Map����һ��name��һ��discr��һ��image
        for (int i = 0; i < names.length; i++)
        {
            Map<String, Object> listItem = new HashMap<String, Object>();
            // ����put�ĵ�һ�����������SimpleAdapter�ĵ��ĸ��������������һ��
            listItem.put("image", images[i]);
            listItem.put("name", names[i]);
            listItem.put("descr", descr[i]);
            listItems.add(listItem);
            /* ������һ��List��ͨ��forѭ�������Ĵδ���Map���͵�����Ԫ�أ�
             * ����put������Map�����������Ϊ"image"��"name"��"descr"���������ݣ�
             * Ȼ���ĸ�Map��ӵ�List�У���������List���Ͼ�����ˡ�
             */
        }
        // ����һ��SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.simple_item,
                new String[] {"image", "descr", "name"},
                new int[] { R.id.image, R.id.descr, R.id.name});
        /* SimpleAdapter�Ĺ�����������
         * �ڶ���Ϊ����Դ��Ӧ�þ��ǽ�����Դ����Ϊ"name"(����ط�Ӧ����Map�����ݵ�����һ��)�����ݣ���ӵ�simple_item�б�ʶ��Ϊname��View�У�
         * ���ĸ��͵�����������������飬�������ݵ�˳����Ըı䣬��������������Ҫ��Ӧ��
         * ����String�����"image"Ԫ����Ҫ��int�����R.id.imageλ�ö�Ӧ��������ӵ������ļ�simple_item.xml����ȷλ�á�
         */
        setListAdapter(simpleAdapter);
    }
}
/**====================================SimpleAdapter 2=======================================*/
//public class SimpleAdapterActivity extends ListActivity {
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        
//        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.simple, new String[] { "title",  "img" }, new int[] { R.id.title, R.id.img });
//        setListAdapter(adapter);
//    }
//    
//    private List<Map<String, Object>> getData() {
//        //map.put(��������,����ֵ)
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("title", "Ħ������");
//        map.put("img", R.drawable.icon);
//        list.add(map);
//        
//        map = new HashMap<String, Object>();
//        map.put("title", "ŵ����");
//        map.put("img", R.drawable.icon);
//        list.add(map);
//        
//        map = new HashMap<String, Object>();
//        map.put("title", "����");
//        map.put("img", R.drawable.icon);
//        list.add(map);
//        return list;
//     }  
//    
//}