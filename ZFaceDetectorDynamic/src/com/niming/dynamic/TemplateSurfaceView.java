package com.niming.dynamic;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TemplateSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mSurfaceHolder;
    // ���ڻ�ͼ��Canvas
    private Canvas mCanvas;
    // ���̱߳�־λ
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
     * ��ʼ�����ڹ��캯���е���
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
