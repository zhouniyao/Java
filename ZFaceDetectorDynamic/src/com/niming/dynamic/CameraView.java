package com.niming.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
	final private String TAG ="niming";
	
	protected Camera mCameraDevice = null;// 摄像头对象实例
	private long mScanBeginTime = 0;   // 扫描开始时间
	private long mScanEndTime = 0;   // 扫描结束时间
	private long mSpecPreviewTime = 0;   // 扫描持续时间
	private int orientionOfCamera ;   //前置摄像头layout角度
	int numberOfFaceDetected;    //最终识别人脸数目
	
	private long mSpecStopTime = 0;
	private long mSpecCameraTime = 0;

	private SurfaceHolder mSurfaceHolder;
	
	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
	}
	/**
	 * 打开摄像机
	 */
	public Camera openCamera(int cameraId){
		try{
			return Camera.open(cameraId);
		}catch(Exception e){
			 mCameraDevice = null;  
           Log.w(TAG, "open cameraFail");  
//           mHandler.postDelayed(r,5000);   //如果摄像头被占用，人眼识别每5秒检测看有没有释放前置  
           return null;  
		}
	}
	/**
	 * 预览方向
	 */
	public static void followScreenOrientation(Context context, Camera camera){
		final int orientation = context.getResources().getConfiguration().orientation;
		if(orientation == Configuration.ORIENTATION_LANDSCAPE){
			camera.setDisplayOrientation(180);
		}else if(orientation == Configuration.ORIENTATION_PORTRAIT){
			camera.setDisplayOrientation(90);
		}
	}
	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Camera.Parameters parameters = mCameraDevice.getParameters();  //获得当前相机的默认配置对象
//		parameters.getFlashMode();//闪光灯参数
		parameters.setFlashMode(parameters.FLASH_MODE_AUTO);
		parameters.setSceneMode(parameters.SCENE_MODE_BARCODE);
//		mCameraDevice.setParameters(parameters);
		openCamera(0);//打开后置摄像头
		
		startPreviewDisplay(holder);
		
	}

	private void startPreviewDisplay(SurfaceHolder holder) {
		checkCamera();
		try{
			mCameraDevice.setPreviewDisplay(holder);
			mCameraDevice.startPreview();
		}catch(IOException e){
			Log.e(TAG, "Error");
		}
	}
	private void checkCamera() {
		if(mCameraDevice == null){
			throw new IllegalStateException("Camera must be set start/stop preview.");
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		if(mSurfaceHolder.getSurface() == null){
			return;
		}
		followScreenOrientation(getContext(), mCameraDevice);
		Log.d(TAG, "Restart");
		stopPreviewDisplay();
		startPreviewDisplay(mSurfaceHolder);
	}
	private void stopPreviewDisplay(){
		checkCamera();
		try{
			mCameraDevice.stopPreview();
		}catch(Exception e){
			Log.e(TAG, "Error while STOP preview for camera" + e);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopPreviewDisplay();
	}


}
