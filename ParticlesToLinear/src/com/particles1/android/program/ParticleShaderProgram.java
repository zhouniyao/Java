package com.particles1.android.program;

import static android.opengl.GLES20.*;

import com.particles1.android.R;

import android.content.Context;

public class ParticleShaderProgram extends ShaderProgram {
    //Uniform locations
    private final int uMatrixLocation;
    private final int uTimeLocation;
    
    //Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;
    
    public ParticleShaderProgram(Context context) {
        //导入着色器源代码
        super(context, R.raw.particle_vertex_shader,
            R.raw.particle_fragment_shader);        
        //Retrieve（vt. 取回; 恢复; [计]检索; 重新得到;） uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);
        
        //Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }
    
    public void setUniforms(float[] matrix, float elapsedTime){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);
    }
    
    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }
    public int getColorAttributeLocation(){
        return aColorLocation;
    }
    public int getDirectionVectorAttributeLocation() {
        return aDirectionVectorLocation;
    }
    public int getParticleStartTimeAttributeLocation(){
        return aParticleStartTimeLocation;
    }
}
