package com.example.ztestndk;

public class JniClient {
	/*
	 * native�ؼ��֣��Ǹ��߱�������������Ǹ�jni������õģ���Ҫ���ҵ���abstract������
	 * �����������������ط��Ϳ���new JniClient() ������ˣ�abstract�ǲ�����new�ġ�������Ǳ��������
	 */
    public static native int getSum(int a, int b);  
    
    public static native String getUrl();  
    /*
     * ����,����
     */
    public static native int[] sort(int arr[], int length);
}


