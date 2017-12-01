package com.niming;

import android.content.Context;
import android.os.Vibrator;

/** 
 * �𶯰����� 
 * androidManifest.xml�м��� ����Ȩ�� 
 * <uses-permission android:name="android.permission.VIBRATE" /> 
 */  
public class Vibrate{  
    private static Vibrator vibrator;  
      
    /** 
     * ���� 
     * @param context     �����𶯵�Context 
     * @param millisecond �𶯵�ʱ�䣬���� 
     */  
    @SuppressWarnings("static-access")  
    public static void vSimple(Context context, int millisecond) {  
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);  
        vibrator.vibrate(millisecond);  
    }  
      
    /** 
     * ���ӵ��� 
     * @param context �����𶯵�Context 
     * @param pattern ����ʽ 
     * @param repeate �𶯵Ĵ�����-1���ظ�����-1Ϊ��pattern��ָ���±꿪ʼ�ظ� 
     */  
    @SuppressWarnings("static-access")  
    public static void vComplicated(Context context, long[] pattern, int repeate) {  
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);  
        vibrator.vibrate(pattern, repeate);  
    }  
      
    /** 
     * ֹͣ�� 
     */  
    public static void stop() {  
        if (vibrator != null) {  
            vibrator.cancel();  
        }  
    }  
}  
