package com.z3niming3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private Button buttonTest = null;
	private ListView listViewProgress = null;
	private List<HashMap<String, Object>> list;
	private MyAdapter myAdapter;
	private MyHandler myHandler;
	HashMap<String, Object> mapA;// ABCDE分别代表五个进度条
	HashMap<String, Object> mapB;
	HashMap<String, Object> mapC;
	HashMap<String, Object> mapD;
	HashMap<String, Object> mapE;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myHandler = new MyHandler();

		findView();
		addListerner();
		initList();
	}

	
	private void findView()
	{
		buttonTest = (Button) findViewById(R.id.buttonTest);
		listViewProgress = (ListView) findViewById(R.id.listViewProgress);
	}

	private void addListerner()
	{
		buttonTest.setOnClickListener(new ClickEvent());
	}

	private void initList()
	{
		if (myAdapter == null)
		{
			list = new ArrayList<HashMap<String, Object>>();
			mapA = new HashMap<String, Object>();
			mapB = new HashMap<String, Object>();
			mapC = new HashMap<String, Object>();
			mapD = new HashMap<String, Object>();
			mapE = new HashMap<String, Object>();
			mapA.put("title", "线程A");
			mapA.put("current", 0);
			list.add(mapA);

			mapB.put("title", "线程B");
			mapB.put("current", 0);
			list.add(mapB);

			mapC.put("title", "线程C");
			mapC.put("current", 0);
			list.add(mapC);

			mapD.put("title", "线程D");
			mapD.put("current", 0);
			list.add(mapD);

			mapE.put("title", "线程E");
			mapE.put("current", 0);
			list.add(mapE);

			myAdapter = new MyAdapter(this, list);
			listViewProgress.setAdapter(myAdapter);	//将数据适配器绑定到ListView
		}
	}

	class UpdateRunnable implements Runnable // 模拟处理线程
	{
		/*
		 * Runnable与Thread都可实现多线程，区别
		 * Runnable是接口，可实现多继承
		 * 以卖10本书为例，Runnable对变量是共享的，启动三个线程也就共卖10本书，前提：
		 * 	TestRunnable myRunnable = new TestRunable();
			new Thread(myRunnable).start();//同一个myRunnable
			new Thread(myRunnable).start();
			new Thread(myRunnable).start();
			如果采用不同的myRunnable，则不会数据共享，如本案例的启动方式
		 * Thread启动三个线程，会各自卖10本书
		 * 	  MyThread mt1=new MyThread();  
			　MyThread mt2=new MyThread();  
			　MyThread mt3=new MyThread();  
			　mt1.start();//每个线程都各卖了10张，共卖了30张票  
			　mt2.start();//但实际只有10张票，每个线程都卖自己的票  
			　mt3.start();
		 */
		int id;
		int currentPos = 0;
		int delay;

		public UpdateRunnable(int id, int delay)
		{
			this.id = id;
			this.delay = delay;
		}

		// 模拟下载情况，每当下载完成后就调用handler去更新进度条的显示。
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			while (currentPos < 100)
			{
				currentPos += 5;
				list.get(id).put("current", currentPos);
				myHandler.sendEmptyMessage(0);
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	class MyHandler extends Handler// 消息Handler用来更新UI
	/*
	 * 当主线程对UI操作时间超过5秒，会收到Android系统的一个错误提示  "强制关闭"，
	 * 如果使用子线程修改UI，而主线程是不安全的，为此引入Handler，
	 * ★Handler运行在主线程中(UI线程中),  它与子线程可以通过Message对象来传递数据，不会存在多个线程同时修改一个控件。
	 * ★Handler用来判断一个对象在多个线程中的使用者
	 * ★通过Handler可以统一进行对UI的管理，因为Handler采用消息处理的机制。简单理解就是另
	 * 一个线程发送一个编号给消息所在的线程，那么该线程的消息处理程序就会收到该消息并进行处理
	 */
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			myAdapter.list = list;
			myAdapter.notifyDataSetChanged();
			/*
			 *  1:如果要刷新的数据源改变了就调用notifyDataSetChanged（），之后调用BaseAdapter中的getView（）
			 *  2:如果那个数据源失效了之后就 调用notifyDataSetInvalidated（）
			 */
		}
	}

	class ClickEvent implements View.OnClickListener// 按钮事件
	{
		@Override
		public void onClick(View v)
		{ // TODO Auto-generated method stub
			switch (v.getId())
			{
				case (R.id.buttonTest):// 模拟文件下载
					Random radomTime = new Random(); // 在没带参数构造函数生成的Random对象的种子缺省是当前系统时间的毫秒数
					new Thread(new UpdateRunnable(0, (radomTime.nextInt(5) + 1) * 100)).start();
					/*
					 * 去0-5的随机数，然后放大100。没有用nextInt(500)防止数值太小，导致进度过快
					 * radomTime.nextInt(5)有可能出现0，可以用radomTime.nextInt(5)+1代替
					 */
					new Thread(new UpdateRunnable(1, (radomTime.nextInt(5) + 1) * 100)).start();
					new Thread(new UpdateRunnable(2, (radomTime.nextInt(5) + 1) * 100)).start();
					new Thread(new UpdateRunnable(3, (radomTime.nextInt(5) + 1) * 100)).start();
					new Thread(new UpdateRunnable(4, (radomTime.nextInt(5) + 1) * 100)).start();
					break;
			}
		}
	}

	/*
	 *BaseAdapter框架的生成
	 *首先输入class MyAdapter extends BaseAdapter{}
	 *系统会提出“必须实现继承的抽象方法”，
	 *点击错误图标，选择“添加未实现的方法”
	 *
	 *Adapter是将数据绑定到UI界面上的桥接类。Adapter负责创建显示每个项目的子View和提供对下层数据的访问。
	 */

	class MyAdapter extends BaseAdapter // List的显示
	{
		/*
		 * 当系统开始绘制ListView的时候，首先调用getCount()方法。得到它的返回值，即ListView的长度。
		 * 然后系统调用getView()方法，根据这个长度逐一绘制ListView的每一行。
		 */
		List<HashMap<String, Object>> list;
		LayoutInflater infl = null;

		public MyAdapter(Context context, List<HashMap<String, Object>> list)
		{
			this.infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
		}

		@Override
		public int getCount()	//返回的是list里面的个数，即你要显示在View上的item
		{
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// 根据item来绘制一个view，
			// 可以引用事先定义好的xml来确定显示的效果并返回一个View对象作为一个Item显示出来。
			
			// 读取listviewlayout.xml中进度条值，然后作为百分比写入到TextView中
			convertView = infl.inflate(R.layout.listviewlayout, null);	//inflate用于引入listviewlayout.xml中定义的布局
			ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
			//由于非当前View,所以findViewById前要加convertView
			TextView text1 = (TextView) convertView.findViewById(R.id.textview1);
			HashMap<String, Object> mapMsg = list.get(position);
			text1.setText(mapMsg.get("title") + ":" + mapMsg.get("current") + "%");
			int progress = (Integer) mapMsg.get("current");
			progressBar.setProgress(progress);
			return convertView;
		}
	}
}
