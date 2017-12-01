package com.particles1.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;




import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.particles1.android.util.MatrixHelper;
import com.particles1.android.util.Geometry.*;
import com.particles1.android.objects.ParticleShooter;
import com.particles1.android.objects.ParticleSystem;
import com.particles1.android.program.ParticleShaderProgram;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;

public class ParticlesRenderer implements Renderer {
	/*既然我们已经把顶点数据和着色器程序分别放于不同的类中了，现在就可以更新渲染类*/
	private final Context context;
	
	public ParticlesRenderer(Context context) {

		this.context = context;
	}
	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] viewProjectionMatrix = new float[16];
	
	private ParticleShaderProgram particleProgram;      
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    /*private ParticleFireworksExplosion particleFireworksExplosion;
    private Random random;*/
    private long globalStartTime;
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		  glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	        
	        // Enable additive blending
//	        glEnable(GL_BLEND);
//	        glBlendFunc(GL_ONE, GL_ONE);
	        
	        particleProgram = new ParticleShaderProgram(context);        
	        particleSystem = new ParticleSystem(10000);        
	        globalStartTime = System.nanoTime();//以纳秒为单位的时间。纳秒，时间单位。一秒的十亿分之一，即等于10的负9次方秒（1 ns = 10 s）。
	        
	        final Vector particleDirection = new Vector(0f, 0.5f, 0f);
	        
	        final float angleVarianceInDegrees = 5f;
	        final float speedVariance = 1f;
	        
	        
//	        redParticleShooter = new ParticleShooter(
//	            new Point(-1f, 0f, 0f), 
//	            particleDirection,                
//	            Color.rgb(255, 50, 5),
//	            5f, 1f);
//	        
//	        greenParticleShooter = new ParticleShooter(
//	            new Point(0f, 0f, 0f), 
//	            particleDirection,
//	            Color.rgb(25, 255, 25),
//	            5f, 1f);
//	        
//	        blueParticleShooter = new ParticleShooter(
//	            new Point(1f, 0f, 0f), 
//	            particleDirection,
//	            Color.rgb(5, 50, 255),
//	            5f, 1f);     
	        
	        redParticleShooter = new ParticleShooter(
	                new Point(-1f, 0f, 0f), 
	                particleDirection,                
	                Color.rgb(255, 50, 5));
	            
	            greenParticleShooter = new ParticleShooter(
	                new Point(0f, 0f, 0f), 
	                particleDirection,
	                Color.rgb(25, 255, 25));
	            
	            blueParticleShooter = new ParticleShooter(
	                new Point(1f, 0f, 0f), 
	                particleDirection,
	                Color.rgb(5, 50, 255));  
	            
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);        

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
            / (float) height, 1f, 10f);
        
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);   
        rotateM(viewMatrix, 0, -60f, 1f, 0f, 0f);//绕x轴旋转-60°
       //vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
            viewMatrix, 0);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		 glClear(GL_COLOR_BUFFER_BIT);
	        
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        
        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);              
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);
        
        particleProgram.useProgram();
        particleProgram.setUniforms(viewProjectionMatrix, currentTime);//配置好Program
        particleSystem.bindData(particleProgram);
        particleSystem.draw(); 
	}
	

}
