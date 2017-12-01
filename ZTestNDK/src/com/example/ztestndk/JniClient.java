package com.example.ztestndk;

public class JniClient {
	/*
	 * native关键字，是告诉编译器，我这个是给jni程序调用的，不要把我当成abstract方法，
	 * 这样，我们在其它地方就可以new JniClient() 这个类了，abstract是不可以new的。这个我们必须清楚。
	 */
    public static native int getSum(int a, int b);  
    
    public static native String getUrl();  
    /*
     * 排序,升序
     */
    public static native int[] sort(int arr[], int length);
}


