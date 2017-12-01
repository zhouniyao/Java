package com.niming.dynamic;

import android.content.Context;
import android.graphics.*;

import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * ��������Demo
 */
public class test2SurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mSurfaceHolder;
    // ���ڻ�ͼ��Canvas
    private Canvas mCanvas;
    // ���̱߳�־λ
    private boolean mIsDrawing;
    private int x = 0;
    private int y = 0;
    private Path mPath;
    private Paint mPaint;
    
	public test2SurfaceView(Context context) {
		super(context);
		initView();
	}
	public test2SurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
    /**
     * ��ʼ�����ڹ��캯���е���
     */
    private void initView() {

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setKeepScreenOn(true);
        //mSurfaceHolder.setFormat(PixelFormat.OPAQUE);
        
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10); // ���û��ʿ��
        mPaint.setStrokeCap(Paint.Cap.ROUND); // ����ת�䴦ΪԲ��
        mPaint.setStrokeJoin(Paint.Join.ROUND);//��ϴ�ΪԲ��

    }
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            // draw something
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawPath(mPath, mPaint);//��ɫ����
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
            	/*
            	 * ��Ҫע����� ��mSurfaceHolder.unlockCanvasAndPost(mCanvas)�ŵ�finally��ȷ��ÿ�ζ��ܽ������ύ
            	 */
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

	@Override
	public void run() {
        while (mIsDrawing) {
            draw();
            /*������������*/
            x += 1;
            y = (int) (100 * Math.sin(x * 2 * Math.PI / 180) + 400);
            mPath.lineTo(x, y);
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        mPath.moveTo(0, 400);
        
        new Thread(this).start();
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

}
