package com.niming.airhockey1;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.util.*;


/**
 *  public interface Renderer {
       void onSurfaceCreated(GL10 gl, EGLConfig config);
       void onSurfaceChanged(GL10 gl, int width, int height);
       void onDrawFrame(GL10 gl);
    }
    
onSurfaceCreated(GL10 gl, EGLConfig config)������Surface��������ʱ����á�����ʵ���У��豸�����ѻ����û�������activity�л�����ʱ���������Ҳ���ܱ����á�
onSurfaceChanged(GL10 gl, int width, int height)������Surface�ߴ�仯ʱ���ã��ں������л�ʱ��Surface�ߴ綼��仯��
onDrawFrame(GL10 gl)�����ڻ���һ֡ʱ���á�����������У�һ��Ҫ����һЩ��������ʹֻ�������Ļ����Ϊ������������غ���Ⱦ�������ᱻ��������ʾ����Ļ�ϣ����ʲô��û�������ܿ���������˸Ч����
 * @author Niming
 *
 */
public class AirHockeyRenderer implements Renderer {
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
	private int aColorLocation;
	
	/*������*/
	private static final String U_MATRIX = "u_Matrix";
	private final float[] projectionMatrix = new float[16];
	private int uMatrixLocation;
	
	/*������*/
	//ͶӰ����ƽ�ƾ�����ת����
	private final float[] modelMatrix = new float[16];
	
	
	/*
	 * ���������ݣ����Ϸ���
	 * ����һ�����������飬��Щ��������ʾ��ɿ������������ӵĶ����λ�ã�
	 * �����ڴ��д���һ������������ΪvertexData��������Щλ�ø��Ƶ�����������ڡ�
	 * �����Ǹ���OpenGL������������ж�ȡ����ǰ����Ҫȷ������ӿ�ͷ����ʼ��ȡ���ݣ��������м��β��
	 * ÿ������������һ���ڲ�ָ�룬����ͨ������position(int)�ƶ�����
	 * Ȼ�����ǵ���glVertexAttribPointer()����OpenGL���������ڻ�����vertexData���ҵ�a_Position��Ӧ�����ݡ�
	 */
	public AirHockeyRenderer(Context context){
		this.context = context;
		
		float[] tableVertices = {
			0f,  0f,
			0f, 14f,
			9f, 14f,
			9f,  0f
		};
		
		float[] tableVerticesWithTriangles = {
			//table brim
			-0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
			-0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
			-0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
            // Triangle Fan
            0f,     0f,    1f,    1f,    1f,         
            -0.5f, -0.8f, 0.7f, 0.7f, 0f,        //ƽ���Ż�ɫ    
             0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
             0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
             0.5f, 0f, 1f, 0f, 0f,

            // Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f,  0.4f, 1f, 0f, 0f
		};
		
		
//		ByteBuffer b = ByteBuffer.allocateDirect(3 * BYTES_PER_FLOAT);
//		b.order(ByteOrder.nativeOrder());
//		FloatBuffer buffer = b.asFloatBuffer();
//		buffer.put($a);
		
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
		
		/**
		 * ����һ��������Դ�ļ�����ɫ��Դ���룩��ȡ�� String
		 */
		String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		/**
		 * ���ڶ�����������ɫ��Դ���룬����OpenGL���ɵ�ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		/**
		 * ����������һ��OpenGL������ǰ�һ��������ɫ����һ��Ƭ����ɫ��������һ���ɵ������󣬶�����ɫ����Ƭ����ɫ������һ�����ġ� 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		if(LoggerConfig.ON){
			ShaderHelper.validateProgram(program);
		}
		
/* ���Ͻ����ˣ����ʹ���������鶨��һ������Ľṹ��
 * Ҳѧϰ�˴�����ɫ�������ز��������ǣ��Լ������������������γ�һ��OpenGL���� 
 */
		
		/*3.4 ���ӹ���*/
//		glUseProgram(program)����OpenGL�ڻ����κζ�������Ļ�ϵ�ʱ��Ҫʹ�����ﶨ��ĳ���
		glUseProgram(program);

//		uColorLocation = glGetUniformLocation(program, U_COLOR);//��ȡuniform��λ�ã��������λ�ô���uColorLocation�С�
		/**
		 * �����Ĳ���Get��ȡ����λ�ã�������ɫ����
		 */
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		
//		һ����ɫ����������һ���ˣ�����glGetAttribLocation()��ȡ���Ե�λ�á�
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		
	/**
	 * �����岽��
	 * �����岽��������������ɫ�����Ա���bind
	 * ��һ������OpenGL�������ҵ�����a_Position��Ӧ�����ݡ� 
	 */
		
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
		vertexData.position(0);//�����ڴ��еĶ������飬����ָ��λ�ù�0
//		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		/*OpenGL�ӻ�������vertexData���ж�ȡ����*/
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
		
		/**��ɫ������װ��(shader plumbing)������ɫ��������Ӧ�ó�������ݹ�������*/
		vertexData.position(POSITION_COMPONENT_COUNT);
		glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
		glEnableVertexAttribArray(aColorLocation);
		/*3.4 ���ӹ�����������*/
		
		/*������*/
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
//		uMatrixLocation = glGetUniformLocation(program, " u_Matrix");
		
	}


//	@Override
//	public void onSurfaceChanged(GL10 gl, int width, int height) {
//		glViewport(0, 0, width, height);
//		final float aspectRatio = width > height ? (float)width/(float)height : (float)height/(float)width;
//
////		if(width > height){
////			//Landscape
////			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
////		}else {
////			//portrait or square
////			orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
////		}
//		/*����Ŵ�*/
//		if(width > height){
//			//Landscape
//			orthoM(projectionMatrix, 0, -aspectRatio/1.5f, aspectRatio/1.5f, -1f/1.5f, 1f/1.5f, -1f, 1f);//����Ŵ�
//		}else {
//			//portrait or square
//			orthoM(projectionMatrix, 0, -1f/1.5f, 1f/1.5f, -aspectRatio/1.5f, aspectRatio/1.5f, -1f, 1f);
//		}
//	}
	
//	public void onSurfaceChanged(GL10 gl, int width, int height) {
//		glViewport(0, 0, width, height);
//		final float aspectRatio = width > height ? (float)width/(float)height : (float)height/(float)width;
//		float offsetX = 0.5f;
//		float offsetY = 1f;
//		
//		/*���津����ƽ��*/
//		if(width > height){
//			//Landscape
//			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio + offsetX, -1f, 1f + offsetY, -1f, 1f);
//		}else {
//			//portrait or square
//			orthoM(projectionMatrix, 0, -1f, 1f + offsetX, -aspectRatio, aspectRatio + offsetY, -1f, 1f);
//		}
//	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
//		glViewport(0, 0, width, height);
		if(width < height){
			MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
		}else {
			MatrixHelper.perspectiveM(projectionMatrix, 45, (float)height/(float)width, 1f, 10f);
			/*���ܣ���45�����Ұ����һ��͸��ͶӰ�������׵���zΪ-1��ʼ����zΪ-10��λ�ý���*/
		}
//		orthoM(projectionMatrix, 0, -(float)height/(float)width, (float)height/(float)width, -1, 1, 1f, 10f);
		//��׵��ƽ��
		setIdentityM(modelMatrix, 0);//��һ��
		translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
		rotateM(modelMatrix, 0, -80f, 1f, 0f, 0f);//��x����ת-60��
		//��������
		/*
		  vertex_clip = ProjectionMatrix * vertex_eye
		  vertex_eye  = ModelMatrix * vertex_model
		  vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
		*/
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}
	
	@Override
	/**
	 * �������е�display()������Ʋ���
	 * 1)����glClear()������������ݣ�
	 * 2)����OpenGL��������Ⱦ����
	 * 3)������ͼ���������Ļ��
	 */
	public void onDrawFrame(GL10 gl) {
		// Clear the rendering surface.
		glViewport(0, 0, 500, 800);
		glClear(GL_COLOR_BUFFER_BIT);
		/*������*/
		glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//��projectionMatrix����ɫ��uMatrixLocation����
		/*������*/
		/*���ӱ�Ե*/
		glDrawArrays(GL_TRIANGLES, 0, 6);//�������ӣ�
										//����˵������һ������������Ϊ�����Σ��ڶ����������Ӷ�������Ŀ�ͷ�������㣻�����������Ƕ���Ķ�������6��ʾ����2�������Ρ�
		/*��������*/
		glDrawArrays(GL_TRIANGLE_FAN, 6, 6);//Χ�����ĵ����λ���������
		/*���Ʒָ���*/
		glDrawArrays(GL_LINES, 12, 2);
		
		/*����ľ�*/
		glDrawArrays(GL_POINTS, 14, 1);
		//Draw the second mallet red;
		glDrawArrays(GL_POINTS, 15, 1);
		
		
		
		
		
		glViewport(500, 0, 500, 800);
		/*���ӱ�Ե*/
		glDrawArrays(GL_TRIANGLES, 0, 6);//�������ӣ�
										//����˵������һ������������Ϊ�����Σ��ڶ����������Ӷ�������Ŀ�ͷ�������㣻�����������Ƕ���Ķ�������6��ʾ����2�������Ρ�
		/*��������*/
		glDrawArrays(GL_TRIANGLE_FAN, 6, 6);//Χ�����ĵ����λ���������
		/*���Ʒָ���*/
		glDrawArrays(GL_LINES, 12, 2);
		
		/*����ľ�*/
		glDrawArrays(GL_POINTS, 14, 1);
		//Draw the second mallet red;
		glDrawArrays(GL_POINTS, 15, 1);
		
		//Draw the ice hockey
//		glDrawArrays(GL_POINTS, 16, 1);
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);		
	}


}