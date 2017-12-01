package com.example.canvasdemo;

import android.content.Context;  
 import android.graphics.Bitmap;  
 import android.graphics.BitmapFactory;  
 import android.graphics.Canvas;  
 import android.graphics.Color;  
import android.graphics.Paint;  
 import android.graphics.Typeface;  
 import android.graphics.Bitmap.Config;  
 import android.util.AttributeSet;  
 import android.view.View;  
  
 public class MyView extends View{  
   
     private Paint paint  = new Paint() ;  
       
     public MyView(Context context) {  
         super(context);  
         // TODO Auto-generated constructor stub  
     }  
     public MyView(Context context , AttributeSet attrs){  
         super(context,attrs);  
     }  
     //����canvas���󣬼�����Ĭ�ϵ���ʾ����  
     @Override  
     public void draw(Canvas canvas) {  
         // TODO Auto-generated method stub  
         super.draw(canvas);  
         //�Ӵ�  
         paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));  
         paint.setColor(Color.BLUE);  
         canvas.drawText("�Զ���View,canvas�����Ѿ����ڡ�", 30, 40, paint);  
         canvas.drawRect(10, 10, 30, 30, paint);  
           
        //��iconͼ��ת��ΪBitmap����  
         Bitmap dog = BitmapFactory.decodeResource(getResources(), R.drawable.dog2) ;  
         canvas.drawBitmap(dog, 40,40, paint);  
     }  
 }  