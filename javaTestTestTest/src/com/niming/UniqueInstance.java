package com.niming;

/*
 * ����һ����ֻ��һ����̬�Ķ�����Ϊ���Ĺ�����Ϊprivate
 */
public class UniqueInstance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Uni uni = Uni.getA("woxianghuijia��");
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
