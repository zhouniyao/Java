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
//		/*ArrayAdapter用法,Adapter是将数据绑定到UI界面上的桥接类。Adapter负责创建显示每个项目的子View和提供对下层数据的访问。*/
//		int layoutID = android.R.layout.simple_list_item_1;
//		/* ArrayAdapter(Context context, int textViewResourceId, List<T> objects)来装配数据，
//		 * 要装配这些数据就需要一个连接ListView视图对象和数组数据的适配器来两者的适配工作，
//		 * ArrayAdapter的构造需要三个参数，依次为this,
//		 * 布局文件（注意这里的布局文件描述的是列表的每一行的布局，android.R.layout.simple_list_item_1是系统定义好的布局文件只显示一行文字，
//		 * objects:数据对象，即数据源。
//		 */
//		
//		/*adapter的作用就是将要在列表内显示的数据和列表本身结合起来。
//		 * 列表本身只完成显示的作用，其实他就是继承自VIEWGROUP类。*/
////		ArrayAdapter<String> myAdapterInstance = new ArrayAdapter<String>(this, layoutID, myStringArray);//Adapter装载数据
//		/*
//		 * 自定义一个custom_array_item.xml布局文件，列表将按照该布局显示。
//		 */
//		ArrayAdapter<String> myAdapterInstance = new ArrayAdapter<String>(this, R.layout.custom_array_item, myStringArray);//Adapter装载数据
//		
//		listview.setAdapter(myAdapterInstance);//控件接收数据
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
//        // 定义一个字符串数组
//        String[] arr1 = { "第1个列表项", "第1个列表项", "第1个列表项" };
//        // 将数组包装为ArrayAdapter
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
//                (this, R.layout.custom_array_item, arr1);
//        /*直接调用ListActivity的setListAdapter方法设置adapter，不能使用setContentView方法*/
//        setListAdapter(adapter1);
//    }
//}

/**====================================SimpleAdapter=======================================*/
/*SimpleAdapter扩展性比ArrayAdapter好，可以放上ImageView，Button，CheckBox等等组件*/
public class MainActivity extends ListActivity
{
    // 定义数据源
    private String[] names = new String[]
            { "List第一个表项", "List第二个表项", "List第三个表项", "List第四个表项"};
    private String[] descr = new String[]
            { "第一个表项的描述文字", "第二个表项的描述文字"
                    , "第三个表项的描述文字", "第四个表项的描述文字"};
    private int[] images = new int[]
            { R.drawable.ic_launcher , R.drawable.ic_launcher
                    , R.drawable.ic_launcher , R.drawable.ic_launcher};
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 创建一个List集合，List集合的元素是Map
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        // 向List中添加数据，数据元素是Map，每个Map包含一个name，一个discr，一个image
        for (int i = 0; i < names.length; i++)
        {
            Map<String, Object> listItem = new HashMap<String, Object>();
            // 这里put的第一个参数必须和SimpleAdapter的第四个参数里面的内容一致
            listItem.put("image", images[i]);
            listItem.put("name", names[i]);
            listItem.put("descr", descr[i]);
            listItems.add(listItem);
            /* 创建了一个List，通过for循环，分四次创建Map类型的数据元素，
             * 调用put函数往Map中添加了名称为"image"，"name"，"descr"的三个数据，
             * 然后将四个Map添加到List中，这样数据List集合就完成了。
             */
        }
        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.simple_item,
                new String[] {"image", "descr", "name"},
                new int[] { R.id.image, R.id.descr, R.id.name});
        /* SimpleAdapter的构造器参数，
         * 第二个为数据源，应该就是将数据源中名为"name"(这个地方应该与Map中数据的名称一致)的数据，添加到simple_item中标识符为name的View中，
         * 第四个和第五个参数，都是数组，数组内容的顺序可以改变，但是两个数组需要对应，
         * 比如String数组的"image"元素需要与int数组的R.id.image位置对应，才能添加到布局文件simple_item.xml的正确位置。
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
//        //map.put(参数名字,参数值)
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("title", "摩托罗拉");
//        map.put("img", R.drawable.icon);
//        list.add(map);
//        
//        map = new HashMap<String, Object>();
//        map.put("title", "诺基亚");
//        map.put("img", R.drawable.icon);
//        list.add(map);
//        
//        map = new HashMap<String, Object>();
//        map.put("title", "三星");
//        map.put("img", R.drawable.icon);
//        list.add(map);
//        return list;
//     }  
//    
//}