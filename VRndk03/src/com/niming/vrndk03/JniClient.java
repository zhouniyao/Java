package com.niming.vrndk03;

/**
 * ԭ���ӿ���Ч�ض�����Java �� C ����ͨ�ŵ�˫����ߡ� 
 * һ��LibMain ������Ϣ��C �⣻ ��һ���� OnMessage���մ�C ����������Ϣ�����Ҽ�ͨ����׼�����ӡ��Ϣ��
 */
public class JniClient {
	/*���ط���*/
	public static native int[] useArray(int arr[], int length);
	//ֻ����һ���ַ�����Ϊ�����ı��ط���
	public static native int LibMain(String[] arr);
	
	/**
	 * ��C�㴥����Ϣ
	 */
	private static void OnMessage(String text, int level){
		System.out.printf("OnMessage text:" + text + " level = " + level);
	}
}