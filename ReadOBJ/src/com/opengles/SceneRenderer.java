package com.opengles;

import static android.opengl.GLES20.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.MySurfaceView;

public class SceneRenderer implements  GLSurfaceView.Renderer  
{  
	
    float yAngle;//绕y轴旋转角度  
    float xAngle;//绕x轴旋转的角度  
    //从指定的obj文件中加载对象  
    LoadedObjectVertexOnly lovo;  
    @Override  
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {  
        //设置屏幕背景色RGBA  
        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);  
        //打开深度检测  
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);  
        //打开背面剪裁  
        GLES20.glEnable(GLES20.GL_CULL_FACE);  
        //初始化变换矩阵  
        MatrixState.setInitStack();  
        //加载要绘制的物体  
        lovo=LoadUtil.loadFromFile("ch.obj",MySurfaceView.this.getResources(),  
                MySurfaceView.this);  
    }  

    @Override  
    public void onSurfaceChanged(GL10 gl, int width, int height) {  
        //设置视窗大小及位置  
        GLES20.glViewport(0,0,width,height);  
        //计算GLSurface宽高比  
        float ratio=(float)width/height;  
        //计算透视投影矩阵  
        MatrixState.setProjectFrustum(-ratio,ratio,-1,1,2,100);  
        //计算摄像机9参数位置矩阵  
        MatrixState.setCamera(0,0,0,0f,0f,-1f,0f,1.0f,0.0f);  
    }  

    @Override  
    public void onDrawFrame(GL10 gl) {  
        //清除深度缓冲与颜色缓冲  
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);  
        //坐标系推远  
        MatrixState.pushMatrix();  
        MatrixState.translate(0,-2f,-25f);  
        //绕y轴，x轴旋转  
        MatrixState.rotate(yAngle,0,1,0);  
        MatrixState.rotate(xAngle,1,0,0);  

        //若加载的物体不为空则绘制物体  
        if(lovo!=null)  
        {  
            lovo.drawSelf();  
        }  
        MatrixState.popMatrix();  
    }

}  