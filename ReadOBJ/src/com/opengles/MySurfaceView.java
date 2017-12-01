package com.opengles;

import static android.opengl.GLES20.*;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR=180.0f/320;//�Ƕ����ű���  
    private SceneRenderer mRenderer;//������Ⱦ��  
  
    private float mPreviousY;//�ϴεĴ���λ��Y����  
    private float mPreviousX;//�ϴεĴ���λ��X����  
  
  
    public MySurfaceView(Context context) {  
        super(context);  
        this.setEGLContextClientVersion(2);  
        mRenderer=new SceneRenderer();  
        setRenderer(mRenderer);  
        //������ȾģʽΪ������Ⱦ  
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);  
    }  
  
    //�����¼��ص�����  
    @Override  
    public boolean onTouchEvent(MotionEvent e)  
    {  
        float y=e.getY();  
        float x=e.getX();  
        switch (e.getAction())  
        {  
            case MotionEvent.ACTION_MOVE:  
                float dy=y-mPreviousY;//����y����λ��  
                float dx=x-mPreviousX;//����x����λ��  
                mRenderer.yAngle+=dx*TOUCH_SCALE_FACTOR;  
                mRenderer.xAngle+=dy*TOUCH_SCALE_FACTOR;  
                requestRender();//�ػ滭��  
        }  
  
        mPreviousY=y;//��¼���ر�λ��  
        mPreviousX=x;//��¼���ر�λ��  
        return  true;  
    }  
}
    
