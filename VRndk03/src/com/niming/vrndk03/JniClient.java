package com.niming.vrndk03;

/**
 * 原生接口有效地定义了Java 和 C 进行通信的双向管线。 
 * 一边LibMain 发送消息到C 库； 另一边是 OnMessage接收从C 发送来的信息，并且简单通过标准输出打印消息。
 */
public class JniClient {
	/*本地方法*/
	public static native int[] useArray(int arr[], int length);
	//只有以一个字符数组为参数的本地方法
	public static native int LibMain(String[] arr);
	
	/**
	 * 从C层触发消息
	 */
	private static void OnMessage(String text, int level){
		System.out.printf("OnMessage text:" + text + " level = " + level);
	}
}
