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

	 //���ʶ��� paint  
	     private Paint paint = new Paint() ;   //�ǵ�ҪΪpaint������ɫ������ ������Ч��  
	     private ImageView imgClip ;  // ��ͼ�����Լ�clip����  
	     private ImageView imgSave ;  // save�����Լ�restore  
	       
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imgClip = (ImageView)findViewById(R.id.imgClip) ;  
        imgSave = (ImageView)findViewById(R.id.imgSave);  
           
        clip_drawCanvas() ; // ��ͼ�����Լ�clip����  
        save_drawCanvas();  // save�����Լ�restore  
    }
    	//����������£���Ҫ����Canvas����Ȼ���ڴ˶����Ͻ��в���  
         //��bitmap������ɺ󣬣���ʾ��Bitmap���������ֲ�����  
         //1����Ҫ��bitmapת��ΪDrawable����  Drawable drawable = new BitmapDrawable(bitmap) ;  
         //2��ֱ��setImageBitmap(bitmap)  
         private void  clip_drawCanvas(){  
             //��iconͼ��ת��ΪBitmap����  
             Bitmap iconbit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher) ;  
               
             //����һ����Bitmap����  
             Bitmap bitmap = Bitmap.createBitmap(400, 250, Config.ARGB_8888)  ;  
               
             Canvas canvas = new Canvas (bitmap) ;  
             //������ɫ����ʾ��ͼ����  
             canvas.drawColor(Color.RED);  
             paint.setAntiAlias(true);   //���û���Ϊ�޾��
             paint.setColor(Color.BLACK);  
             canvas.drawText("ԭ�ȵĻ�ͼ����--��ɫ����", 60,50,paint) ;  
             //��bitmap����  
             canvas.drawBitmap(iconbit, 20, 20, paint);  
               
             //����һ�����򣬵�ǰ�Ĳ�������ΪRect�ü�������  
             Rect rect = new Rect (10,80,180,120) ;  
               
             //��ǰ�Ļ�ͼ����ΪRect�ü������򣬶���������֮ǰ��ֵ��bitmap  
             canvas.clipRect(rect)  ;  
             canvas.drawColor(Color.YELLOW);  
             //������ɫ����ʾ��ͼ����  
             paint.setColor(Color.BLACK);  
             canvas.drawText("�ü�clip��ͼ����-��ɫ����", 10,100,paint) ;  
               
             //��Bitmap����ת��ΪDrawableͼ����Դ  
             //Drawable drawable = new BitmapDrawable(bitmap) ;  
             //img.setBackgroundDrawable(drawable) ;  
               
             //��ʾ��ͬ��  
             imgClip.setImageBitmap(bitmap);  
         }  
           
         private void save_drawCanvas(){  
             //��iconͼ��ת��ΪBitmap����  
             Bitmap iconbit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher) ;  
               
             //����һ����Bitmap����  
             Bitmap bitmap = Bitmap.createBitmap(200, 100, Config.ARGB_8888)  ;  
          
             Canvas canvas = new Canvas (bitmap) ;  
       
             paint.setColor(Color.GREEN);  
             paint.setTextSize(16);  //���������С  
             canvas.drawRect(10, 10, 50, 8, paint);  
             canvas.drawText("��û����ת",50, 10, paint);  
             //����canvas֮ǰ�Ĳ���,��sava()��restore֮��Ĳ��������canvas֮ǰ�Ĳ�������Ӱ��  
             canvas.save() ;  
               
             //˳ʱ����ת30��  
             canvas.rotate(30) ;  
             canvas.drawColor(Color.RED);  
             canvas.drawBitmap(iconbit, 20, 20, paint);  
             canvas.drawRect(50, 10, 80, 50, paint);  
             //canvas.translate(20,20);  
             canvas.drawText("������ת��",115,20, paint);  
               
             //��ԭ֮ǰsave()֮ǰ������,���ҽ�save()����֮���roate(),translate()�Լ�clipXXX()�����Ĳ������  
             canvas.restore();  
               
             //ƽ��(20,20)������  
             //canvas.translate(20,20);  
             canvas.drawRect(80, 10, 110,30, paint);  
             canvas.drawText("��û����ת",115,20, paint);  
       
             //��Bitmap����ת��ΪDrawableͼ����  
             //ΪImageView����ͼ��  
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
