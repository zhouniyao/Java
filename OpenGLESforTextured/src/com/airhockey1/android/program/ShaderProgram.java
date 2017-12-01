package com.airhockey1.android.program;

import static android.opengl.GLES20.glUseProgram;

import com.airhockey1.android.util.ShaderHelper;
import com.airhockey1.android.util.TextResourceReader;

import android.content.Context;

public class ShaderProgram {
	//Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	
	//Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	
	//Shader program
	protected final int program;
	protected ShaderProgram(Context context, int vertexShadeResourceId,
			int fragmentShaderResourceId){
		//Compile the shaders and link the program.
		program = ShaderHelper.buildProgram(TextResourceReader.readTextFileFromResource(context, vertexShadeResourceId),
				TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));
	}
	
	public void useProgram(){
		//Set the current OpenGL shader program to this program.
//		glUseProgram(program)告诉OpenGL在绘制任何东西到屏幕上的时候要使用这里定义的程序。
		glUseProgram(program);
	}
}
