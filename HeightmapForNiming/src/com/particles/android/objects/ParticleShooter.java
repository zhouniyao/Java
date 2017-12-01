package com.particles.android.objects;

import java.util.Random;
import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import com.particles.android.util.Geometry.Vector;
import com.particles.android.util.Geometry.*;

public class ParticleShooter {
    private final Point position;
    private final Vector direction;
    private final int color;
    
    /*10.4 扩散粒子*/
    private final float angleVariance;
    private final float speedVariance;
    
    private final Random random = new Random();
    
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];
    
    public ParticleShooter(Point position, Vector direction, int color,
        float angleVarianceInDegrees, float speedVariance) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        
        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;
        
        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }
    
//    public ParticleShooter(Point position, Vector direction, int color) {
//        this.position = position;
//        this.direction = direction;
//        this.color = color;
//    }
    
    public void addParticles(ParticleSystem particleSystem, float currentTime, int count) {
        for (int i = 0; i < count; i++) {
            setRotateEulerM(rotationMatrix, 0,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance
                );
//            setRotateEulerM(rotationMatrix, 0,
//                (random.nextFloat() - 0.0f) * angleVariance,
//                (random.nextFloat() - 0.0f) * angleVariance,
//                (random.nextFloat() - 0.0f) * angleVariance
//                );
            /*随机改变发射角度*/
            multiplyMV(resultVector, 0, 
                        rotationMatrix, 0, 
                        directionVector, 0);
            float speedAdjustment = 1f + random.nextFloat() * speedVariance;
            
            /*调整速度*/
            Vector thisDirection = new Vector(resultVector[0] * speedAdjustment,
                resultVector[1] * speedAdjustment, 
                resultVector[2] * speedAdjustment);
            particleSystem.addParticle(position, color, thisDirection, currentTime);
            
//            particleSystem.addParticle(position, color, direction, currentTime);
        }
    }
    
    
}
