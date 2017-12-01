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
	
	protected Camera mCameraDevice = null;// ����ͷ����ʵ��
	private long mScanBeginTime = 0;   // ɨ�迪ʼʱ��
	private long mScanEndTime = 0;   // ɨ�����ʱ��
	private long mSpecPreviewTime = 0;   // ɨ�����ʱ��
	private int orientionOfCamera ;   //ǰ������ͷlayout�Ƕ�
	int numberOfFaceDetected;    //����ʶ��������Ŀ
	
	private long mSpecStopTime = 0;
	private long mSpecCameraTime = 0;

	private SurfaceHolder mSurfaceHolder;
	
	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
	}
	/**
	 * �������
	 */
	public Camera openCamera(int cameraId){
		try{
			return Camera.open(cameraId);
		}catch(Exception e){
			 mCameraDevice = null;  
           Log.w(TAG, "open cameraFail");  
//           mHandler.postDelayed(r,5000);   //�������ͷ��ռ�ã�����ʶ��ÿ5���⿴��û���ͷ�ǰ��  
           return null;  
		}
	}
	/**
	 * Ԥ������
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
		Camera.Parameters parameters = mCameraDevice.getParameters();  //��õ�ǰ�����Ĭ�����ö���
//		parameters.getFlashMode();//����Ʋ���
		parameters.setFlashMode(parameters.FLASH_MODE_AUTO);
		parameters.setSceneMode(parameters.SCENE_MODE_BARCODE);
//		mCameraDevice.setParameters(parameters);
		openCamera(0);//�򿪺�������ͷ
		
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
