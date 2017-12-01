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
	protected Camera mCameraDevice = null;// ����ͷ����ʵ��
	private long mScanBeginTime = 0;   	  // ɨ�迪ʼʱ��
	private long mScanEndTime = 0;   	  // ɨ�����ʱ��
	private long mSpecPreviewTime = 0;    // ɨ�����ʱ��
	private int orientionOfCamera ;   	  //ǰ������ͷlayout�Ƕ�
	int numberOfFaceDetected;     		  //����ʶ��������Ŀ
	
	private long mSpecStopTime = 0;
	private long mSpecCameraTime = 0;
	
	MainActivity ma;//Activiy����
	Bitmap bitmapForDraw;
	
	public mySurfaceView(Context context) {
		super(context);
		this.ma = (MainActivity) context;
	}
	
	/**
	 * �÷����ڳ��򴴽�ʱ�����ã���ɻ�ȡ���������������Ĺ��ܣ�����ɻ���
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "������𴲣�");
//		startFaceDetection();
		/*
		 * ��Ҫע����ǣ���ȡ����Canvas�����Ǽ����ϴε�Canvas���󣬶�����һ���µĶ���
		 * ��ˣ�֮ǰ�Ļ�ͼ�������ᱻ�����������Ҫ������������ڻ���ǰ��ͨ��drawColor()��������������������
		 */
		Canvas canvas = holder.lockCanvas();//��ȡ��ǰ��Canvas��ͼ����
		this.initBitmap();
		synchronized(holder){//����ִ��
			onDraw(canvas);
		}
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true); //�򿪿����
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);//���ư�ɫ���α���
		paint.reset(); //�����������
		paint.setColor(Color.RED);//������ɫ
		paint.setStrokeWidth(10);//���ñ�������
		RectF rf = new RectF(50, 100, 160, 180);//��������
		canvas.drawRect(rf, paint);//�ڻ������û��ʻ�����
		
		//��ͼƬ
		drawBitmap(canvas, paint);
	}
	
	public void initBitmap(){
		BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //����λͼ���ɵĲ���������Ϊ565������+enum  
        bitmapForDraw = BitmapFactory.decodeResource(ma.getResources(), 0x7f020000, BitmapFactoryOptionsbfo); //����ͼƬ,ͼƬ��С�ڿ�*�ߣ�450*600��Ϊ��    
	}
	protected void drawBitmap(Canvas canvas, Paint paint){
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmapForDraw, 10, 10, paint);
		canvas.save();
		
		paint.setAlpha(180);//����͸����
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
	 * ������ͷ����ó�������ͷ�ص����ݣ��õ���setpreviewcallback
	 */
	public void startFaceDetection() {  
        try {  
                mCameraDevice = Camera.open(0);     //��ǰ��  
                if (mCameraDevice != null)  
                    Log.v(TAG, "open cameradevice success! ");  
            } catch (Exception e) {             //Exception����ܶ������쳣  
                mCameraDevice = null;  
                Log.w(TAG, "open cameraFail");  
//                mHandler.postDelayed(r,5000);   //�������ͷ��ռ�ã�����ʶ��ÿ5���⿴��û���ͷ�ǰ��  
                return;  
        }   
              
        Log.i(TAG, "startFaceDetection");  
        Camera.Parameters parameters = mCameraDevice.getParameters();  
        setCameraDisplayOrientation(1,mCameraDevice);              //����Ԥ������   
              
        mCameraDevice.setPreviewCallback(new PreviewCallback(){  
            public void onPreviewFrame(byte[] data, Camera camera){  
                mScanEndTime = System.currentTimeMillis();   //��¼����ͷ�������ݵ�ʱ��  
                mSpecPreviewTime = mScanEndTime - mScanBeginTime;  //��onPreviewFrame��ȡ����ͷ���ݵ�ʱ��  
                Log.i(TAG, "onPreviewFrame and mSpecPreviewTime = " + String.valueOf(mSpecPreviewTime));  
                Camera.Size localSize = camera.getParameters().getPreviewSize();  //���Ԥ���ֱ���  
                YuvImage localYuvImage = new YuvImage(data, 17, localSize.width, localSize.height, null);  
                ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();  
                localYuvImage.compressToJpeg(new Rect(0, 0, localSize.width, localSize.height), 80, localByteArrayOutputStream);    //������ͷ�ص�����ת��YUV���ٰ�ͼ��ߴ�ѹ����JPEG�����������ת������  
                byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();  
                CameraRelease();   //�����ͷ�camera��Դ������Ӱ��camera�豸����������  
                StoreByteImage(arrayOfByte);  
            }  
        });  
  
        mCameraDevice.startPreview();         //�����ɷ��ڻص����棬��ִ�е��������ǰ���setPreviewCallback  
        mScanBeginTime = System.currentTimeMillis();// ��¼��ϵͳ��ʼɨ���ʱ��  
    }  
	
	protected void CameraRelease() {
		mCameraDevice.release();
	}
	/**
	 * ����Ԥ������ĺ���˵�����ú����Ƚ���Ҫ����Ϊ����ֱ��Ӱ��bitmap����ʱ�ľ�����ת�Ƕȣ�Ӱ����������ʶ��ĳɹ����
	 */
	public void setCameraDisplayOrientation(int paramInt, Camera paramCamera){  
        CameraInfo info = new CameraInfo();  
        Camera.getCameraInfo(paramInt, info);  
        
        WindowManager winMan = (WindowManager)ma.getSystemService("window");
        int rotation = winMan.getDefaultDisplay().getRotation();  //�����ʾ�����Ƕ�  
        int degrees = 0;  
        Log.i(TAG,"getRotation's rotation is " + String.valueOf(rotation));  
        switch (rotation) {  
            case Surface.ROTATION_0: degrees = 0; break;  
            case Surface.ROTATION_90: degrees = 90; break;  
            case Surface.ROTATION_180: degrees = 180; break;  
            case Surface.ROTATION_270: degrees = 270; break;  
        }  
  
        orientionOfCamera = info.orientation;      //�������ͷ�İ�װ��ת�Ƕ�  
        int result;  
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {  
            result = (info.orientation + degrees) % 360;  
            result = (360 - result) % 360;  // compensate the mirror  
        } else {  // back-facing  
            result = (info.orientation - degrees + 360) % 360;  
        }  
        paramCamera.setDisplayOrientation(result);  //ע��ǰ���õĴ���ǰ����ӳ���棬�ö���SDK�ĵ��ı�׼DEMO  
    }  
	
	/**
	 * ������ͷ�ص����ݽ���ת�������ս��BITMAP��������ʶ��Ĺ���
	 */
	public void StoreByteImage(byte[] paramArrayOfByte){  
        mSpecStopTime = System.currentTimeMillis();  
        mSpecCameraTime = mSpecStopTime - mScanBeginTime;  
                       
        Log.i(TAG, "StoreByteImage and mSpecCameraTime is " + String.valueOf(mSpecCameraTime));  
  
        BitmapFactory.Options localOptions = new BitmapFactory.Options();  
            Bitmap localBitmap1 = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, localOptions);  
            int i = localBitmap1.getWidth();  
            int j = localBitmap1.getHeight();   //���ϲ������JPEG�����нӳ�BMP����RAW->JPEG->BMP  
            Matrix localMatrix = new Matrix();  
            //int k = cameraResOr;  
            Bitmap localBitmap2 = null;  
            FaceDetector localFaceDetector = null;  
  
        switch(orientionOfCamera){   //����ǰ�ð�װ��ת�ĽǶ������¹���BMP  
            case 0:  
                localFaceDetector = new FaceDetector(i, j, 1);  
                        localMatrix.postRotate(0.0F, i / 2, j / 2);  
                        localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);  
                break;  
            case 90:  
                localFaceDetector = new FaceDetector(j, i, 1);   //������  
                        localMatrix.postRotate(-270.0F, j / 2, i / 2);  //��90�ȵĻ��ͷ�����ת270�ȣ�һ��Ч��  
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
                        localBitmap2 = Bitmap.createBitmap(j, i, Bitmap.Config.RGB_565);  //localBitmap2Ӧ��û�����ݵ�  
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
            localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, localPaint1); //�ô���localBitmap1��localBitmap2�������ɲ�Ҫ����  
  
        numberOfFaceDetected = localFaceDetector.findFaces(localBitmap2, arrayOfFace); //����ʶ���Ľ��  
            localBitmap2.recycle();  
            localBitmap1.recycle();   //�ͷ�λͼ��Դ  
  
//        FaceDetectDeal(numberOfFaceDetected);  
    }  

}
