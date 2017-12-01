package com.niming.airhockey1;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.util.LoggerConfig;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

public class ObjRenderer implements Renderer{
	private static final int POSITION_COMPONENT_COUNT = 2;
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
	
	public ObjRenderer(Context context){
		this.context = context;

		
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
	/**
	 * ����ɫ��Դ������ļ��ж�����
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	   /*
		// Set the background clear color to red. The first component is red,
		// the second is green, the third is blue, and the last component is
		// alpha, which we don't use in this lesson.
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
       */
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		//�洢�Ǹ�����ID
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		if(LoggerConfig.ON){
			ShaderHelper.validateProgram(program);
		}
		
		/*3.4 ���ӹ���*/
//		glUseProgram(program)����OpenGL�ڻ����κζ�������Ļ�ϵ�ʱ��Ҫʹ�����ﶨ��ĳ���
		glUseProgram(program);
//		��ȡuniform��λ�ã��������λ�ô���uColorLocation�С�
		uColorLocation = glGetUniformLocation(program, U_COLOR);
//		һ����ɫ����������һ���ˣ�����glGetAttribLocation()��ȡ���Ե�λ�á�
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
//		���������붥�����ݵ�����
//		��һ������OpenGL�������ҵ�����a_Position��Ӧ�����ݡ�
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
		vertexData.position(0);//ָ��λ��
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		//Ѱ�Ҷ�������
		glEnableVertexAttribArray(aPositionLocation);
		/*3.4 ���ӹ�����������*/
		
	}


	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);
//		/*��������*/
//		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);//ָ����ɫ
//		glDrawArrays(GL_TRIANGLES, 0, 6);//�������ӣ�����˵������һ�����������������Σ��ڶ����������Ӷ�������Ŀ�ͷ�������㣻�����������Ƕ���Ķ�������6��ʾ����2�������Ρ�
//		/*���Ʒָ���*/
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_LINES, 6, 2);
//		
//		/*����ľ�*/
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 1.0f, 1.0f);//Draw the first mallet blue.
//		glDrawArrays(GL_POINTS, 8, 1);
//		
//		//Draw the second mallet red;
//		glUniform4f(uColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_POINTS, 9, 1);
//		//Draw the ice hockey
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_POINTS, 10, 1);
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
