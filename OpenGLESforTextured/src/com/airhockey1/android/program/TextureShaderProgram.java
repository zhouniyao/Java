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
	 * ���ݾ������������ǵ�uniform
	 * @param matrix
	 * @param textureId
	 */
	public void setUniforms(float[] matrix, int textureId){
		//Pass the matrix into the shader program.
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		/*
		 * ��������ֱ�Ӵ��ݣ���Ҫ���󶨵�����Ԫ��Ȼ������Ԫ���ݸ���ɫ����
		 * ��������OpenGL��ʹ��������л���ʱ�����ǲ���Ҫֱ�Ӹ���ɫ����������
		 * �෴������ʹ������Ԫ��texture unit�������Ǹ�����
		 */
		/*
		 * ͨ������glActiveTexture()�ѻ������Ԫ����Ϊ����Ԫ0��
		 * �����ɴ˿�ʼ��Ȼ��ͨ������glBindTexture()������󶨵������Ԫ��
		 * ���ţ�ͨ������glUniform1i(uTextureUnitLocation, 0)�ѱ�ѡ��������Ԫ���ݸ�Ƭ����ɫ���е�u_TextureUnit��
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
