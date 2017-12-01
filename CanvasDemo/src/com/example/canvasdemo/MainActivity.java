package com.example.canvasdemo;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {

	 //画笔对象 paint  
	     private Paint paint = new Paint() ;   //记得要为paint设置颜色，否则 看不到效果  
	     private ImageView imgClip ;  // 绘图区域以及clip方法  
	     private ImageView imgSave ;  // save方法以及restore  
	       
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imgClip = (ImageView)findViewById(R.id.imgClip) ;  
        imgSave = (ImageView)findViewById(R.id.imgSave);  
           
        clip_drawCanvas() ; // 绘图区域以及clip方法  
        save_drawCanvas();  // save方法以及restore  
    }
    	//这样的情况下，需要创建Canvas对象，然后在此对象上进行操作  
         //对bitmap操作完成后，，显示该Bitmap有以下两种操作。  
         //1、需要将bitmap转换为Drawable对象  Drawable drawable = new BitmapDrawable(bitmap) ;  
         //2、直接setImageBitmap(bitmap)  
         private void  clip_drawCanvas(){  
             //将icon图像转换为Bitmap对象  
             Bitmap iconbit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher) ;  
               
             //创建一个的Bitmap对象  
             Bitmap bitmap = Bitmap.createBitmap(400, 250, Config.ARGB_8888)  ;  
               
             Canvas canvas = new Canvas (bitmap) ;  
             //设置颜色来显示画图区域  
             canvas.drawColor(Color.RED);  
             paint.setAntiAlias(true);   //设置画笔为无锯齿
             paint.setColor(Color.BLACK);  
             canvas.drawText("原先的画图区域--红色部分", 60,50,paint) ;  
             //画bitmap对象  
             canvas.drawBitmap(iconbit, 20, 20, paint);  
               
             //剪裁一个区域，当前的操作对象为Rect裁剪的区域  
             Rect rect = new Rect (10,80,180,120) ;  
               
             //当前的画图区域为Rect裁剪的区域，而不是我们之前赋值的bitmap  
             canvas.clipRect(rect)  ;  
             canvas.drawColor(Color.YELLOW);  
             //设置颜色来显示画图区域  
             paint.setColor(Color.BLACK);  
             canvas.drawText("裁剪clip后画图区域-黄色部分", 10,100,paint) ;  
               
             //将Bitmap对象转换为Drawable图像资源  
             //Drawable drawable = new BitmapDrawable(bitmap) ;  
             //img.setBackgroundDrawable(drawable) ;  
               
             //显示，同上  
             imgClip.setImageBitmap(bitmap);  
         }  
           
         private void save_drawCanvas(){  
             //将icon图像转换为Bitmap对象  
             Bitmap iconbit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher) ;  
               
             //创建一个的Bitmap对象  
             Bitmap bitmap = Bitmap.createBitmap(200, 100, Config.ARGB_8888)  ;  
          
             Canvas canvas = new Canvas (bitmap) ;  
       
             paint.setColor(Color.GREEN);  
             paint.setTextSize(16);  //设置字体大小  
             canvas.drawRect(10, 10, 50, 8, paint);  
             canvas.drawText("我没有旋转",50, 10, paint);  
             //保存canvas之前的操作,在sava()和restore之间的操作不会对canvas之前的操作进行影响  
             canvas.save() ;  
               
             //顺时针旋转30度  
             canvas.rotate(30) ;  
             canvas.drawColor(Color.RED);  
             canvas.drawBitmap(iconbit, 20, 20, paint);  
             canvas.drawRect(50, 10, 80, 50, paint);  
             //canvas.translate(20,20);  
             canvas.drawText("我是旋转的",115,20, paint);  
               
             //复原之前save()之前的属性,并且将save()方法之后的roate(),translate()以及clipXXX()方法的操作清空  
             canvas.restore();  
               
             //平移(20,20)个像素  
             //canvas.translate(20,20);  
             canvas.drawRect(80, 10, 110,30, paint);  
             canvas.drawText("我没有旋转",115,20, paint);  
       
             //将Bitmap对象转换为Drawable图像资  
             //为ImageView设置图像  
             //imgSave.setImageBitmap(bitmap);  
              
           Drawable drawable = new BitmapDrawable(bitmap) ;  
             imgSave.setBackgroundDrawable(drawable) ;  
               
         }  

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
