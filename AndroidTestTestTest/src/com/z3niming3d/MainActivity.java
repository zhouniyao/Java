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
	HashMap<String, Object> mapA;// ABCDE�ֱ�������������
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
			mapA.put("title", "�߳�A");
			mapA.put("current", 0);
			list.add(mapA);

			mapB.put("title", "�߳�B");
			mapB.put("current", 0);
			list.add(mapB);

			mapC.put("title", "�߳�C");
			mapC.put("current", 0);
			list.add(mapC);

			mapD.put("title", "�߳�D");
			mapD.put("current", 0);
			list.add(mapD);

			mapE.put("title", "�߳�E");
			mapE.put("current", 0);
			list.add(mapE);

			myAdapter = new MyAdapter(this, list);
			listViewProgress.setAdapter(myAdapter);	//�������������󶨵�ListView
		}
	}

	class UpdateRunnable implements Runnable // ģ�⴦���߳�
	{
		/*
		 * Runnable��Thread����ʵ�ֶ��̣߳�����
		 * Runnable�ǽӿڣ���ʵ�ֶ�̳�
		 * ����10����Ϊ����Runnable�Ա����ǹ���ģ����������߳�Ҳ�͹���10���飬ǰ�᣺
		 * 	TestRunnable myRunnable = new TestRunable();
			new Thread(myRunnable).start();//ͬһ��myRunnable
			new Thread(myRunnable).start();
			new Thread(myRunnable).start();
			������ò�ͬ��myRunnable���򲻻����ݹ����籾������������ʽ
		 * Thread���������̣߳��������10����
		 * 	  MyThread mt1=new MyThread();  
			��MyThread mt2=new MyThread();  
			��MyThread mt3=new MyThread();  
			��mt1.start();//ÿ���̶߳�������10�ţ�������30��Ʊ  
			��mt2.start();//��ʵ��ֻ��10��Ʊ��ÿ���̶߳����Լ���Ʊ  
			��mt3.start();
		 */
		int id;
		int currentPos = 0;
		int delay;

		public UpdateRunnable(int id, int delay)
		{
			this.id = id;
			this.delay = delay;
		}

		// ģ�����������ÿ��������ɺ�͵���handlerȥ���½���������ʾ��
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

	class MyHandler extends Handler// ��ϢHandler��������UI
	/*
	 * �����̶߳�UI����ʱ�䳬��5�룬���յ�Androidϵͳ��һ��������ʾ  "ǿ�ƹر�"��
	 * ���ʹ�����߳��޸�UI�������߳��ǲ���ȫ�ģ�Ϊ������Handler��
	 * ��Handler���������߳���(UI�߳���),  �������߳̿���ͨ��Message�������������ݣ�������ڶ���߳�ͬʱ�޸�һ���ؼ���
	 * ��Handler�����ж�һ�������ڶ���߳��е�ʹ����
	 * ��ͨ��Handler����ͳһ���ж�UI�Ĺ�����ΪHandler������Ϣ����Ļ��ơ�����������
	 * һ���̷߳���һ����Ÿ���Ϣ���ڵ��̣߳���ô���̵߳���Ϣ�������ͻ��յ�����Ϣ�����д���
	 */
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			myAdapter.list = list;
			myAdapter.notifyDataSetChanged();
			/*
			 *  1:���Ҫˢ�µ�����Դ�ı��˾͵���notifyDataSetChanged������֮�����BaseAdapter�е�getView����
			 *  2:����Ǹ�����ԴʧЧ��֮��� ����notifyDataSetInvalidated����
			 */
		}
	}

	class ClickEvent implements View.OnClickListener// ��ť�¼�
	{
		@Override
		public void onClick(View v)
		{ // TODO Auto-generated method stub
			switch (v.getId())
			{
				case (R.id.buttonTest):// ģ���ļ�����
					Random radomTime = new Random(); // ��û���������캯�����ɵ�Random���������ȱʡ�ǵ�ǰϵͳʱ��ĺ�����
					new Thread(new UpdateRunnable(0, (radomTime.nextInt(5) + 1) * 100)).start();
					/*
					 * ȥ0-5���������Ȼ��Ŵ�100��û����nextInt(500)��ֹ��ֵ̫С�����½��ȹ���
					 * radomTime.nextInt(5)�п��ܳ���0��������radomTime.nextInt(5)+1����
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
	 *BaseAdapter��ܵ�����
	 *��������class MyAdapter extends BaseAdapter{}
	 *ϵͳ�����������ʵ�ּ̳еĳ��󷽷�����
	 *�������ͼ�꣬ѡ�����δʵ�ֵķ�����
	 *
	 *Adapter�ǽ����ݰ󶨵�UI�����ϵ��Ž��ࡣAdapter���𴴽���ʾÿ����Ŀ����View���ṩ���²����ݵķ��ʡ�
	 */

	class MyAdapter extends BaseAdapter // List����ʾ
	{
		/*
		 * ��ϵͳ��ʼ����ListView��ʱ�����ȵ���getCount()�������õ����ķ���ֵ����ListView�ĳ��ȡ�
		 * Ȼ��ϵͳ����getView()�������������������һ����ListView��ÿһ�С�
		 */
		List<HashMap<String, Object>> list;
		LayoutInflater infl = null;

		public MyAdapter(Context context, List<HashMap<String, Object>> list)
		{
			this.infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
		}

		@Override
		public int getCount()	//���ص���list����ĸ���������Ҫ��ʾ��View�ϵ�item
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
			// ����item������һ��view��
			// �����������ȶ���õ�xml��ȷ����ʾ��Ч��������һ��View������Ϊһ��Item��ʾ������
			
			// ��ȡlistviewlayout.xml�н�����ֵ��Ȼ����Ϊ�ٷֱ�д�뵽TextView��
			convertView = infl.inflate(R.layout.listviewlayout, null);	//inflate��������listviewlayout.xml�ж���Ĳ���
			ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
			//���ڷǵ�ǰView,����findViewByIdǰҪ��convertView
			TextView text1 = (TextView) convertView.findViewById(R.id.textview1);
			HashMap<String, Object> mapMsg = list.get(position);
			text1.setText(mapMsg.get("title") + ":" + mapMsg.get("current") + "%");
			int progress = (Integer) mapMsg.get("current");
			progressBar.setProgress(progress);
			return convertView;
		}
	}
}
