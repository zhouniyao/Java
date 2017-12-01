package com.niming.zfacedetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;
import android.view.View;

public class myView extends View{  
    private int imageWidth, imageHeight;  
    private int numberOfFace = 5;       //������������  
    private FaceDetector myFaceDetect;  //����ʶ�����ʵ��  
    private FaceDetector.Face[] myFace; //�洢�����������������  
    float myEyesDistance;           //����֮��ľ���  
    int numberOfFaceDetected;       //ʵ�ʼ�⵽��������  
    Bitmap myBitmap;  

    public myView(Context context){     //view��Ĺ��캯����������  
        super(context);   
        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //����λͼ���ɵĲ���������Ϊ565������+enum  
        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baby, BitmapFactoryOptionsbfo); //����ͼƬ,ͼƬ��С�ڿ�*�ߣ�450*600��Ϊ��    
        imageWidth = myBitmap.getWidth();   
        imageHeight = myBitmap.getHeight();   
        myFace = new FaceDetector.Face[numberOfFace];       //������������ռ�  
        myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);   
        numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);    //FaceDetector ����ʵ������������  
        Log.v("numberOfFaceDetected is: ", "" + numberOfFaceDetected);  
    }  
      
    protected void onDraw(Canvas canvas){           //���� 
        canvas.drawBitmap(myBitmap, 0, 0, null);    
        Paint myPaint = new Paint();   
        myPaint.setColor(Color.RED);   
        myPaint.setStyle(Paint.Style.STROKE);   
        myPaint.setStrokeWidth(3);          //����λͼ��paint�����Ĳ���  

        for(int i=0; i < numberOfFaceDetected; i++){  
        	FaceDetector.Face face = myFace[i];  
            PointF myMidPoint = new PointF();   //һ��������ά����(x, y)����
            face.getMidPoint(myMidPoint);   
            myEyesDistance = face.eyesDistance();   //�õ��������ĵ���ۼ�������������ÿ���������л���  
            canvas.drawRect(            //���þ��ο��λ�ò���  
                    (int)(myMidPoint.x - myEyesDistance),   
                    (int)(myMidPoint.y - myEyesDistance),   
                    (int)(myMidPoint.x + myEyesDistance),   
                    (int)(myMidPoint.y + myEyesDistance*1.25),   
                    myPaint);  
        }  
    }  
}  