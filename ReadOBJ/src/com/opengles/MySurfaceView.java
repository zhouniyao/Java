package com.opengles;

import static android.opengl.GLES20.*;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR=180.0f/320;//角度缩放比例  
    private SceneRenderer mRenderer;//场景渲染器  
  
    private float mPreviousY;//上次的触控位置Y坐标  
    private float mPreviousX;//上次的触控位置X坐标  
  
  
    public MySurfaceView(Context context) {  
        super(context);  
        this.setEGLContextClientVersion(2);  
        mRenderer=new SceneRenderer();  
        setRenderer(mRenderer);  
        //设置渲染模式为主动渲染  
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);  
    }  
  
    //触摸事件回调方法  
    @Override  
    public boolean onTouchEvent(MotionEvent e)  
    {  
        float y=e.getY();  
        float x=e.getX();  
        switch (e.getAction())  
        {  
            case MotionEvent.ACTION_MOVE:  
                float dy=y-mPreviousY;//计算y方向位移  
                float dx=x-mPreviousX;//计算x方向位移  
                mRenderer.yAngle+=dx*TOUCH_SCALE_FACTOR;  
                mRenderer.xAngle+=dy*TOUCH_SCALE_FACTOR;  
                requestRender();//重绘画面  
        }  
  
        mPreviousY=y;//记录触控笔位置  
        mPreviousX=x;//记录触控笔位置  
        return  true;  
    }  
}
    
