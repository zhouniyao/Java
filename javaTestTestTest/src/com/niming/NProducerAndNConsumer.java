package com.niming;

public class NProducerAndNConsumer {

	public static void main(String[] args) {
		Queue Queue = new Queue();
		NProducer pro = new NProducer(Queue);
		NConsumer con = new NConsumer(Queue);
		

	}

}

/*
	wait( ) ��֪�����õ��̷߳����ܳ̽���˯��ֱ�������߳̽�����ͬ�̲ܳ��ҵ���notify( )��
	notify( ) �ָ���ͬ�����е�һ������ wait( ) ���̡߳�
	notifyAll( ) �ָ���ͬ���������е��� wait( ) ���̡߳�����������ȼ����߳��������С�
*/
/**
 * ������
 */
class Queue{
	int n;
	boolean valueComplete = false;
	synchronized int get(){
		if(!valueComplete){//����δ׼����ʱ��wait
			try {
				wait();//wait( )�����á���ʹִ�й���ֱ��NProducer ��֪�����Ѿ�Ԥ���á�
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			System.out.println("Got:" + n);
			valueComplete = false;//�����Ѷ����������ٴη�������
			notify();
			return n;
	}
	synchronized void put(int n){
		if(valueComplete){//�ȴ�get�����ݰ�
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.n = n;
		valueComplete = true;//���ݸո�׼���ã�������
		System.out.println("Put:" + n);
		notify();
	}
}

/**
 �������߳�
*/
class NProducer implements Runnable{
	Queue queue;
	public NProducer(Queue queue) {
		this.queue = queue;
		new Thread(this, "NProducer").start();
	}
	@Override
	public void run() {
		int i = 0;
		while (true) {
			queue.put(i++);
		}
	}
}

class NConsumer implements Runnable{
	Queue queue;
	public NConsumer(Queue queue) {
		this.queue = queue;
		new Thread(this, "NConsumer").start();
	}
	@Override
	public void run() {
//		System.out.println(Queue.get());
		while (true) { //ѭ�����ܶ�
			queue.get();
		}
	}
	
}