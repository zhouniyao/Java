package com.niming;

public class NProducerAndNConsumer {

	public static void main(String[] args) {
		Queue Queue = new Queue();
		NProducer pro = new NProducer(Queue);
		NConsumer con = new NConsumer(Queue);
		

	}

}

/*
	wait( ) 告知被调用的线程放弃管程进入睡眠直到其他线程进入相同管程并且调用notify( )。
	notify( ) 恢复相同对象中第一个调用 wait( ) 的线程。
	notifyAll( ) 恢复相同对象中所有调用 wait( ) 的线程。具有最高优先级的线程最先运行。
*/
/**
 * 数据类
 */
class Queue{
	int n;
	boolean valueComplete = false;
	synchronized int get(){
		if(!valueComplete){//数据未准备好时，wait
			try {
				wait();//wait( )被调用。这使执行挂起直到NProducer 告知数据已经预备好。
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			System.out.println("Got:" + n);
			valueComplete = false;//数据已读过，请求再次放入数据
			notify();
			return n;
	}
	synchronized void put(int n){
		if(valueComplete){//等待get读数据吧
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.n = n;
		valueComplete = true;//数据刚刚准备好，来读吧
		System.out.println("Put:" + n);
		notify();
	}
}

/**
 生产者线程
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
		while (true) { //循环不能断
			queue.get();
		}
	}
	
}