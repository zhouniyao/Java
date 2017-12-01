package com.z3niming3d;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import niming.util.*;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class dim3Renderer implements Renderer {
	private static final int POSITION_COMPONENT_COUNT = 2;
//	private static final int POSITION_COMPONENT_COUNT = 4;
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer vertexData;
	private final Context context;
	private int program;
	//	���һ��uniform��λ��
	private static final String U_COLOR = "u_Color";
	private int uColorLocation;
	//��ȡ���Ե�λ��
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;
	
	private static final String A_COLOR = "a_Color";
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private int aColorLoaction;
	
	/*������*/
	private static final String U_MATRIX = "u_Matrix";
	private static final String TAG = "Render";
	private final float[] projectionMatrix = new float[16];
	private int uMatrixLocation;
	
	/*������*/
	//ͶӰ����ƽ�ƾ�����ת����
	private final float[] modelMatrix = new float[16];

	public dim3Renderer(Context context) {
		this.context = context;
		
		float[] tableVerticesWithTriangles = {
		//table brim
		-0.55f, -0.55f,
		 0.55f,  0.55f,
		-0.55f,  0.55f,
		-0.55f, -0.55f,
		 0.55f, -0.55f,
		 0.55f,  0.55f,
		 
		//Triangle 1
		-0.5f, -0.5f,
		 0.5f,  0.5f,
		-0.5f,  0.5f,
		//Triangle 2
		-0.5f, -0.5f,
		 0.5f, -0.5f,
		 0.5f,  0.5f,
		 
		 //line1
		 -0.5f, 0f,
		  0.5f, 0f,
		  //mallets
		  0f, -0.25f,
		  0f,  0.25f,
		  //ice hockey
		  0.1f,  0.1f
			
	};
	//����һ����СΪ�������ı����ڴ��
	vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
			//�����ֽڻ�����(byte buffer)���ձ����ֽ���(native byte order)��֯��������
			.order(ByteOrder.nativeOrder())
			//���ǲ�Ըֱ�Ӳ����������ֽڣ�����ϣ��ʹ�ø���������˵���asFloatBuffer()�õ�һ�����Է�ӳ�ײ��ֽڵ�FloatBuffer���ʵ��
			.asFloatBuffer();
	//Ȼ�����		vertexData.put(tableVerticesWithTriangles)�����ݴ�Dalvik���ڴ��и��Ƶ������ڴ�
	vertexData.put(tableVerticesWithTriangles);
}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		//�洢�Ǹ�����ID
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
//		if(LoggerConfig.ON){
//			ShaderHelper.validateProgram(program);
//		}
		glUseProgram(program);
//		��ȡuniform��λ�ã��������λ�ô���uColorLocation�С�
		uColorLocation = glGetUniformLocation(program, U_COLOR);
//		һ����ɫ����������һ���ˣ�����glGetAttribLocation()��ȡ���Ե�λ�á�
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		vertexData.position(0);//ָ��λ��
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		//Ѱ�Ҷ�������
		glEnableVertexAttribArray(aPositionLocation);
//		Log.v(TAG, "��������");
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);
		/*���ӱ�Ե*/
		glUniform4f(uColorLocation, 0.5f, 0.5f, 1.0f, 1.0f);//ָ����ɫ
		glDrawArrays(GL_TRIANGLES, 0, 6);//�������ӣ�����˵������һ�����������������Σ��ڶ����������Ӷ�������Ŀ�ͷ�������㣻�����������Ƕ���Ķ�������6��ʾ����2�������Ρ�
		/*��������*/
		glUniform4f(uColorLocation, 0.75f, 0.75f, 0.75f, 1.0f);//ָ����ɫ
		glDrawArrays(GL_TRIANGLES, 6, 6);//�������ӣ�����˵������һ�����������������Σ��ڶ����������Ӷ�������Ŀ�ͷ�������㣻�����������Ƕ���Ķ�������6��ʾ����2�������Ρ�
		/*���Ʒָ���*/
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 12, 2);
		
		/*����ľ�*/
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);//Draw the first mallet blue.
		glDrawArrays(GL_POINTS, 14, 1);
		
		//Draw the second mallet red;
		glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 15, 1);
		//Draw the ice hockey
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_POINTS, 16, 1);
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 16, 1);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
		

}
