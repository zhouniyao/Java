package com.airhockey1.android.program;

import com.niming.airhockey1.R;
import static android.opengl.GLES20.*;
import android.content.Context;

/**
 * 颜色着色器程序
 * @author Niming
 */
public class ColorShaderProgram extends ShaderProgram {
	//Uniform locations
	private final int uMatrixLocation;
	
	//Attribute locations
	private final int aPositionLocation;
	private final int aColorLocation;
	
	public ColorShaderProgram(Context context, int vertexResourceId, int fragmentResourceId) {
		//Compile the shaders and link the program.
		super(context,  vertexResourceId,  fragmentResourceId);
		//Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		//Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		
		aColorLocation = glGetAttribLocation(program, A_COLOR);
	}
	
	public void setUniforms(float[] matrix){
		//Pass the matrix into the shader program.
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
	}
	
	public int getPositionAttrbuteLocation(){
		return aPositionLocation;
	}
	
	public int getColorAttributeLocation(){
		return aColorLocation;
	}
}
