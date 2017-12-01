package com.particles1.android.objects;

import static android.opengl.GLES20.*;
import static com.particles1.android.Constants.BYTES_PER_FLOAT;
import android.graphics.Color;

import com.particles1.android.util.Geometry.Point;
import com.particles1.android.util.Geometry.Vector;
import com.particles1.android.data.VertexArray;
import com.particles1.android.program.ParticleShaderProgram;

public class ParticleSystem {
	private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;    
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT = 
						        POSITION_COMPONENT_COUNT
						      + COLOR_COMPONENT_COUNT 
						      + VECTOR_COMPONENT_COUNT      
						      + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private final float[] particles;//storage of particle
    private final VertexArray vertexArray;
    private final int maxParticleCount;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }
    /**
     * 为粒子系统 添加一个新的粒子
     * @param position
     * @param color
     * @param direction
     * @param particleStartTime
     */
    public void addParticle(Point position, int color, Vector direction,
        float particleStartTime) {                
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
		
        int currentOffset = particleOffset;        
        nextParticle++;
        
        if (currentParticleCount < maxParticleCount) {//数组未满
            currentParticleCount++;
        }
        
        if (nextParticle == maxParticleCount) {//数组刚满
            // Start over at the beginning, but keep currentParticleCount so
            // that all the other particles still get drawn.
            nextParticle = 0;
        }  
        
        //增加一个新粒子数据
        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;
        
        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;
        
        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;             
        
        particles[currentOffset++] = particleStartTime;
              
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }
    /**
     * 着色器程序ShaderProgram 将数据与属性绑定 
     */
    public void bindData(ParticleShaderProgram particleProgram) {
        int dataOffset = 0;
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;
        
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT, STRIDE);        
        dataOffset += COLOR_COMPONENT_COUNT;
        
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.getDirectionVectorAttributeLocation(),
            VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;       
        
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.getParticleStartTimeAttributeLocation(),
            PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, currentParticleCount);
    }
}
