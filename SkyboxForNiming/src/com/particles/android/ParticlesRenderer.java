/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.particles.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.particles.android.objects.ParticleShooter;
import com.particles.android.objects.ParticleSystem;
import com.particles.android.objects.Skybox;
import com.particles.android.programs.ParticleShaderProgram;
import com.particles.android.programs.SkyboxShaderProgram;
import com.particles.android.util.MatrixHelper;
import com.particles.android.util.TextureHelper;
import com.particles.android.util.Geometry.*;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;

public class ParticlesRenderer implements Renderer {    
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
    
    private long globalStartTime;//全局启动时间
    
    private int texture;
    
    /*11.5 在场景中加入天空盒*/
    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;
    //增加触控
    private float xRotation, yRotation;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        

        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();//nano译纳米
        
        final Vector particleDirection = new Vector(0f, 0.5f, 0f);//粒子被发送的方向向量，即速度
        
        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;
        /*
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
        */
        redParticleShooter = new ParticleShooter(
            new Point(-1f, 0f, 0f), 
            particleDirection,                
            Color.rgb(255, 50, 5),            
            angleVarianceInDegrees, 
            speedVariance);
        
        greenParticleShooter = new ParticleShooter(
            new Point(0f, 0f, 0f), 
            particleDirection,
            Color.rgb(25, 255, 25),            
            angleVarianceInDegrees, 
            speedVariance);
        
        blueParticleShooter = new ParticleShooter(
            new Point(1f, 0f, 0f), 
            particleDirection,
            Color.rgb(5, 50, 255),            
            angleVarianceInDegrees, 
            speedVariance); 
        
        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
        
        /*11.5*/
        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(
                        context,
                        new int[]{
                            R.drawable.left,
                            R.drawable.right,
                            R.drawable.bottom,
                            R.drawable.top,
                            R.drawable.front,
                            R.drawable.back
                        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
            / (float) height, 1f, 10f);
        
//        setIdentityM(viewMatrix, 0);
//        translateM(viewMatrix, 0, 0f, -1.5f, -5f);   
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
//            viewMatrix, 0);
        
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        
        drawSkybox();
//        drawParticles();
    }
    
    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;//不想让触控过于灵敏。
        yRotation += deltaY / 16f;
        
        //限制360°旋转，防止眩晕
        if(yRotation < -90){
            yRotation = -90;
        }else if(yRotation > 90) {
            yRotation = 90;
        }
    }
    

    private void drawSkybox() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
//        setLookAtM(viewMatrix, 0, xRotation, yRotation, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);//立方体会放大、缩小
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
    }
    
    private void drawParticles() {
        
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        
        redParticleShooter.addParticles(particleSystem, currentTime, 1);
        greenParticleShooter.addParticles(particleSystem, currentTime, 1);              
        blueParticleShooter.addParticles(particleSystem, currentTime, 1);
        
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);   
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
            viewMatrix, 0);
        
        // Enable additive blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        
        particleProgram.useProgram();
    //        particleProgram.setUniforms(viewProjectionMatrix, currentTime);
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw(); 
        
        glDisable(GL_BLEND);
    }
    
    


}
