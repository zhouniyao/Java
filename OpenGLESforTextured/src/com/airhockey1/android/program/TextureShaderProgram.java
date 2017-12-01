package com.airhockey1.android.program;

import static android.opengl.GLES20.*;
import com.niming.airhockey1.R;

import android.R.integer;
import android.content.Context;

public class TextureShaderProgram extends ShaderProgram {
	//Uniform locations
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	
	//Attribute locations
	private final int aPositionLocation;
	private final int aTextureCoordinatesLocation;
	
	public TextureShaderProgram(Context context, int vertexResourceId, int fragmentResourceId){
		//Compile the shaders and link the program.
		super(context, vertexResourceId, fragmentResourceId);
		//Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
		
		//Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
	}
	/**
	 * 传递矩阵和纹理给他们的uniform
	 * @param matrix
	 * @param textureId
	 */
	public void setUniforms(float[] matrix, int textureId){
		//Pass the matrix into the shader program.
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		/*
		 * 【纹理不能直接传递，需要被绑定到纹理单元，然后将纹理单元传递给着色器】
		 * 当我们在OpenGL里使用纹理进行绘制时，我们不需要直接给着色器传递纹理。
		 * 相反，我们使用纹理单元（texture unit）保存那个纹理。
		 */
		/*
		 * 通过调用glActiveTexture()把活动的纹理单元设置为纹理单元0，
		 * 我们由此开始，然后通过调用glBindTexture()把纹理绑定到这个单元。
		 * 接着，通过调用glUniform1i(uTextureUnitLocation, 0)把被选定的纹理单元传递给片段着色器中的u_TextureUnit。
		 */
		//Set the active texture unit to texture unit 0.
		glActiveTexture(GL_TEXTURE0);
		
		//Bind the texture to this unit.
		glBindTexture(GL_TEXTURE_2D, textureId);
		
		//Tell the texture uniform sampler to use this texture 
		//in the shader by telling it to read from texture unit 0.
		glUniform1i(uTextureUnitLocation, 0);
	}
	
	public int getPositionAttributeLocation(){
		return aPositionLocation;
	}
	
	public int getTextureCoordinatesAttributeLocation(){
		return aTextureCoordinatesLocation;
	}
}
