package com.niming;

/*
 * 测试一个类只有一个静态的对象，因为它的构造器为private
 */
public class UniqueInstance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Uni uni = Uni.getA("woxianghuijia家");
		uni.play();
	}

}


class Uni{
	static Uni staticInstance;
	String str;
	private Uni(String str){
		this.str = str;
	}
	public void play(){
		System.out.println(str);
	}
	public static Uni getA(String str) {
		staticInstance = new Uni(str);
		return staticInstance;
	}
	
}
