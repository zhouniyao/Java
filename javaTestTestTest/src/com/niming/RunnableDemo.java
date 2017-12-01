package com.niming;

class AThread implements Runnable{
	public Thread t;
	String name;
	private volatile boolean isRun = true;
	public void goSleep() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			
			System.out.println(name +": " + i);
//			t.sleep(1000);
		}
	}
	public AThread(String name) {
		this.name = name;
		t = new Thread(this, name);
	}
	@Override
	public void run() {
		if(isRun){
			try {
				goSleep();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void start(){
		t.start();
	}
	
}

public class RunnableDemo {

	public static void main(String[] args) {
		AThread one = new AThread("one");
		AThread two = new AThread("two");
		AThread three = new AThread("three");
		
		one.start();
		two.start();
		three.start();
		
		System.out.println(one.name + one.t.isAlive());
		System.out.println(two.name + two.t.isAlive());
		System.out.println(three.name + three.t.isAlive());
		
		try {
			one.t.join();//等待该线程结束
			two.t.join();
			three.t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(one.name + one.t.isAlive());
		System.out.println(two.name + two.t.isAlive());
		System.out.println(three.name + three.t.isAlive());
		
		System.out.println("main Thread exit!");
		
		
	}

}
