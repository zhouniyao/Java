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
     //存在canvas对象，即存在默认的显示区域  
     @Override  
     public void draw(Canvas canvas) {  
         // TODO Auto-generated method stub  
         super.draw(canvas);  
         //加粗  
         paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));  
         paint.setColor(Color.BLUE);  
         canvas.drawText("自定义View,canvas对象已经存在。", 30, 40, paint);  
         canvas.drawRect(10, 10, 30, 30, paint);  
           
        //将icon图像转换为Bitmap对象  
         Bitmap dog = BitmapFactory.decodeResource(getResources(), R.drawable.dog2) ;  
         canvas.drawBitmap(dog, 40,40, paint);  
     }  
 }  