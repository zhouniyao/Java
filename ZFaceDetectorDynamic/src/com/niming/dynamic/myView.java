package com.niming.dynamic;

import com.niming.zfacedetector.R;

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
    private int numberOfFace = 5;       //最大检测的人脸数  
    private FaceDetector myFaceDetect;  //人脸识别类的实例  
    private FaceDetector.Face[] myFace; //存储多张人脸的数组变量  
    float myEyesDistance;           //两眼之间的距离  
    int numberOfFaceDetected;       //实际检测到的人脸数  
    Bitmap myBitmap;  

    public myView(Context context){     //view类的构造函数，必须有  
        super(context);   
        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //构造位图生成的参数，必须为565。类名+enum  
//        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baby, BitmapFactoryOptionsbfo); //导入图片,图片大小在宽*高（450*600）为宜    
        imageWidth = myBitmap.getWidth();   
        imageHeight = myBitmap.getHeight();   
        myFace = new FaceDetector.Face[numberOfFace];       //分配人脸数组空间  
        myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);   
        numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);    //FaceDetector 构造实例并解析人脸  
        Log.v("numberOfFaceDetected is: ", "" + numberOfFaceDetected);  
    }  
      
    protected void onDraw(Canvas canvas){           //画布 
        canvas.drawBitmap(myBitmap, 0, 0, null);    
        Paint myPaint = new Paint();   
        myPaint.setColor(Color.RED);   
        myPaint.setStyle(Paint.Style.STROKE);   
        myPaint.setStrokeWidth(3);          //设置位图上paint操作的参数  

        for(int i=0; i < numberOfFaceDetected; i++){  
        	FaceDetector.Face face = myFace[i];  
            PointF myMidPoint = new PointF();   //一个包含二维坐标(x, y)的类
            face.getMidPoint(myMidPoint);   
            myEyesDistance = face.eyesDistance();   //得到人脸中心点和眼间距离参数，并对每个人脸进行画框  
            canvas.drawRect(            //设置矩形框的位置参数  
                    (int)(myMidPoint.x - myEyesDistance),   
                    (int)(myMidPoint.y - myEyesDistance),   
                    (int)(myMidPoint.x + myEyesDistance),   
                    (int)(myMidPoint.y + myEyesDistance*1.25),   
                    myPaint);  
        }  
    }  
}  