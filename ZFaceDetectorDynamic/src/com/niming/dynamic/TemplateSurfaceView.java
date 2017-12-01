package com.niming.dynamic;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TemplateSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mSurfaceHolder;
    // 用于绘图的Canvas
    private Canvas mCanvas;
    // 子线程标志位
    private boolean mIsDrawing;
    
	public TemplateSurfaceView(Context context) {
		super(context);
		initView();
	}
	public TemplateSurfaceView(Context context, AttributeSet attrs) {
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

    }
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            // draw something
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
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
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
