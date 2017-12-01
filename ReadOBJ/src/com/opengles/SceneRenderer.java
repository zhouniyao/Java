package com.opengles;

import static android.opengl.GLES20.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.MySurfaceView;

public class SceneRenderer implements  GLSurfaceView.Renderer  
{  
	
    float yAngle;//��y����ת�Ƕ�  
    float xAngle;//��x����ת�ĽǶ�  
    //��ָ����obj�ļ��м��ض���  
    LoadedObjectVertexOnly lovo;  
    @Override  
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {  
        //������Ļ����ɫRGBA  
        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);  
        //����ȼ��  
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);  
        //�򿪱������  
        GLES20.glEnable(GLES20.GL_CULL_FACE);  
        //��ʼ���任����  
        MatrixState.setInitStack();  
        //����Ҫ���Ƶ�����  
        lovo=LoadUtil.loadFromFile("ch.obj",MySurfaceView.this.getResources(),  
                MySurfaceView.this);  
    }  

    @Override  
    public void onSurfaceChanged(GL10 gl, int width, int height) {  
        //�����Ӵ���С��λ��  
        GLES20.glViewport(0,0,width,height);  
        //����GLSurface��߱�  
        float ratio=(float)width/height;  
        //����͸��ͶӰ����  
        MatrixState.setProjectFrustum(-ratio,ratio,-1,1,2,100);  
        //���������9����λ�þ���  
        MatrixState.setCamera(0,0,0,0f,0f,-1f,0f,1.0f,0.0f);  
    }  

    @Override  
    public void onDrawFrame(GL10 gl) {  
        //�����Ȼ�������ɫ����  
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);  
        //����ϵ��Զ  
        MatrixState.pushMatrix();  
        MatrixState.translate(0,-2f,-25f);  
        //��y�ᣬx����ת  
        MatrixState.rotate(yAngle,0,1,0);  
        MatrixState.rotate(xAngle,1,0,0);  

        //�����ص����岻Ϊ�����������  
        if(lovo!=null)  
        {  
            lovo.drawSelf();  
        }  
        MatrixState.popMatrix();  
    }

}  