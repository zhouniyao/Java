package com.niming.dynamic;

import java.io.ByteArrayOutputStream;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.media.FaceDetector;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

@SuppressLint("WrongCall")
public class mySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	
	final private String TAG ="niming";
	protected Camera mCameraDevice = null;// 摄像头对象实例
	private long mScanBeginTime = 0;   	  // 扫描开始时间
	private long mScanEndTime = 0;   	  // 扫描结束时间
	private long mSpecPreviewTime = 0;    // 扫描持续时间
	private int orientionOfCamera ;   	  //前置摄像头layout角度
	int numberOfFaceDetected;     		  //最终识别人脸数目
	
	private long mSpecStopTime = 0;
	private long mSpecCameraTime = 0;
	
	MainActivity ma;//Activiy对象
	Bitmap bitmapForDraw;
	
	public mySurfaceView(Context context) {
		super(context);
		this.ma = (MainActivity) context;
	}
	
	/**
	 * 该方法在程序创建时被调用，完成获取画布和锁定画布的功能，以完成绘制
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "懒虫快起床！");
//		startFaceDetection();
		/*
		 * 需要注意的是，获取到的Canvas对象还是继续上次的Canvas对象，而不是一个新的对象。
		 * 因此，之前的绘图操作将会被保留，如果需要擦除，则可以在绘制前，通过drawColor()方法来机型清屏操作。
		 */
		Canvas canvas = holder.lockCanvas();//获取当前的Canvas绘图对象。
		this.initBitmap();
		synchronized(holder){//单步执行
			onDraw(canvas);
		}
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true); //打开抗锯齿
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);//绘制白色矩形背景
		paint.reset(); //清除画笔设置
		paint.setColor(Color.RED);//设置颜色
		paint.setStrokeWidth(10);//设置笔线条宽
		RectF rf = new RectF(50, 100, 160, 180);//创建矩形
		canvas.drawRect(rf, paint);//在画布上用画笔画矩形
		
		//画图片
		drawBitmap(canvas, paint);
	}
	
	public void initBitmap(){
		BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //构造位图生成的参数，必须为565。类名+enum  
        bitmapForDraw = BitmapFactory.decodeResource(ma.getResources(), 0x7f020000, BitmapFactoryOptionsbfo); //导入图片,图片大小在宽*高（450*600）为宜    
	}
	protected void drawBitmap(Canvas canvas, Paint paint){
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmapForDraw, 10, 10, paint);
		canvas.save();
		
		paint.setAlpha(180);//设置透明度
		Matrix m1 = new Matrix();
		Matrix m2 = new Matrix();
		Matrix m3 = new Matrix();
		m1.setTranslate(20, 40);
		m2.setScale(0.5f, 0.5f);
		m3.setConcat(m1, m2);
		canvas.drawBitmap(bitmapForDraw, m3, paint);
		paint.reset();
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 打开摄像头，获得初步摄像头回调数据，用到是setpreviewcallback
	 */
	public void startFaceDetection() {  
        try {  
                mCameraDevice = Camera.open(0);     //打开前置  
                if (mCameraDevice != null)  
                    Log.v(TAG, "open cameradevice success! ");  
            } catch (Exception e) {             //Exception代替很多具体的异常  
                mCameraDevice = null;  
                Log.w(TAG, "open cameraFail");  
//                mHandler.postDelayed(r,5000);   //如果摄像头被占用，人眼识别每5秒检测看有没有释放前置  
                return;  
        }   
              
        Log.i(TAG, "startFaceDetection");  
        Camera.Parameters parameters = mCameraDevice.getParameters();  
        setCameraDisplayOrientation(1,mCameraDevice);              //设置预览方向   
              
        mCameraDevice.setPreviewCallback(new PreviewCallback(){  
            public void onPreviewFrame(byte[] data, Camera camera){  
                mScanEndTime = System.currentTimeMillis();   //记录摄像头返回数据的时间  
                mSpecPreviewTime = mScanEndTime - mScanBeginTime;  //从onPreviewFrame获取摄像头数据的时间  
                Log.i(TAG, "onPreviewFrame and mSpecPreviewTime = " + String.valueOf(mSpecPreviewTime));  
                Camera.Size localSize = camera.getParameters().getPreviewSize();  //获得预览分辨率  
                YuvImage localYuvImage = new YuvImage(data, 17, localSize.width, localSize.height, null);  
                ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();  
                localYuvImage.compressToJpeg(new Rect(0, 0, localSize.width, localSize.height), 80, localByteArrayOutputStream);    //把摄像头回调数据转成YUV，再按图像尺寸压缩成JPEG，从输出流中转成数组  
                byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();  
                CameraRelease();   //及早释放camera资源，避免影响camera设备的正常调用  
                StoreByteImage(arrayOfByte);  
            }  
        });  
  
        mCameraDevice.startPreview();         //该语句可放在回调后面，当执行到这里，调用前面的setPreviewCallback  
        mScanBeginTime = System.currentTimeMillis();// 记录下系统开始扫描的时间  
    }  
	
	protected void CameraRelease() {
		mCameraDevice.release();
	}
	/**
	 * 设置预览方向的函数说明，该函数比较重要，因为方向直接影响bitmap构造时的矩阵旋转角度，影响最终人脸识别的成功与否
	 */
	public void setCameraDisplayOrientation(int paramInt, Camera paramCamera){  
        CameraInfo info = new CameraInfo();  
        Camera.getCameraInfo(paramInt, info);  
        
        WindowManager winMan = (WindowManager)ma.getSystemService("window");
        int rotation = winMan.getDefaultDisplay().getRotation();  //获得显示器件角度  
        int degrees = 0;  
        Log.i(TAG,"getRotation's rotation is " + String.valueOf(rotation));  
        switch (rotation) {  
            case Surface.ROTATION_0: degrees = 0; break;  
            case Surface.ROTATION_90: degrees = 90; break;  
            case Surface.ROTATION_180: degrees = 180; break;  
            case Surface.ROTATION_270: degrees = 270; break;  
        }  
  
        orientionOfCamera = info.orientation;      //获得摄像头的安装旋转角度  
        int result;  
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {  
            result = (info.orientation + degrees) % 360;  
            result = (360 - result) % 360;  // compensate the mirror  
        } else {  // back-facing  
            result = (info.orientation - degrees + 360) % 360;  
        }  
        paramCamera.setDisplayOrientation(result);  //注意前后置的处理，前置是映象画面，该段是SDK文档的标准DEMO  
    }  
	
	/**
	 * 对摄像头回调数据进行转换并最终解成BITMAP后再人脸识别的过程
	 */
	public void StoreByteImage(byte[] paramArrayOfByte){  
        mSpecStopTime = System.currentTimeMillis();  
        mSpecCameraTime = mSpecStopTime - mScanBeginTime;  
                       
        Log.i(TAG, "StoreByteImage and mSpecCameraTime is " + String.valueOf(mSpecCameraTime));  
  
        BitmapFactory.Options localOptions = new BitmapFactory.Options();  
            Bitmap localBitmap1 = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, localOptions);  
            int i = localBitmap1.getWidth();  
            int j = localBitmap1.getHeight();   //从上步解出的JPEG数组中接出BMP，即RAW->JPEG->BMP  
            Matrix localMatrix = new Matrix();  
            //int k = cameraResOr;  
            Bitmap localBitmap2 = null;  
            FaceDetector localFaceDetector = null;  
  
        switch(orientionOfCamera){   //根据前置安装旋转的角度来重新构造BMP  
            case 0:  
                localFaceDetector = new FaceDetector(i, j, 1);  
                        localMatrix.postRotate(0.0F, i / 2, j / 2);  
                        localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);  
                break;  
            case 90:  
                localFaceDetector = new FaceDetector(j, i, 1);   //长宽互换  
                        localMatrix.postRotate(-270.0F, j / 2, i / 2);  //正90度的话就反方向转270度，一样效果  
                        localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);  
                break;                        
            case 180:  
                localFaceDetector = new FaceDetector(i, j, 1);  
                        localMatrix.postRotate(-180.0F, i / 2, j / 2);  
                        localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);  
                break;  
            case 270:  
                localFaceDetector = new FaceDetector(j, i, 1);  
                        localMatrix.postRotate(-90.0F, j / 2, i / 2);  
                        localBitmap2 = Bitmap.createBitmap(j, i, Bitmap.Config.RGB_565);  //localBitmap2应是没有数据的  
                break;  
        }  
  
        FaceDetector.Face[] arrayOfFace = new FaceDetector.Face[1];  
            Paint localPaint1 = new Paint();  
            Paint localPaint2 = new Paint();  
        localPaint1.setDither(true);  
            localPaint2.setColor(-65536);  
            localPaint2.setStyle(Paint.Style.STROKE);  
            localPaint2.setStrokeWidth(2.0F);  
            Canvas localCanvas = new Canvas();  
            localCanvas.setBitmap(localBitmap2);  
            localCanvas.setMatrix(localMatrix);  
            localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, localPaint1); //该处将localBitmap1和localBitmap2关联（可不要？）  
  
        numberOfFaceDetected = localFaceDetector.findFaces(localBitmap2, arrayOfFace); //返回识脸的结果  
            localBitmap2.recycle();  
            localBitmap1.recycle();   //释放位图资源  
  
//        FaceDetectDeal(numberOfFaceDetected);  
    }  

}
