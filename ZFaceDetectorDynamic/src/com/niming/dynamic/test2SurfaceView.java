package com.niming.dynamic;

import android.content.Context;
import android.graphics.*;

import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * 绘制正弦Demo
 */
public class test2SurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mSurfaceHolder;
    // 用于绘图的Canvas
    private Canvas mCanvas;
    // 子线程标志位
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
     * 初始化，在构造函数中调用
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
        mPaint.setStrokeWidth(10); // 设置画笔宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 设置转弯处为圆角
        mPaint.setStrokeJoin(Paint.Join.ROUND);//结合处为圆角

    }
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            // draw something
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawPath(mPath, mPaint);//白色背景
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
            	/*
            	 * 需要注意的是 将mSurfaceHolder.unlockCanvasAndPost(mCanvas)放到finally中确保每次都能讲内容提交
            	 */
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

	@Override
	public void run() {
        while (mIsDrawing) {
            draw();
            /*绘制正弦曲线*/
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
