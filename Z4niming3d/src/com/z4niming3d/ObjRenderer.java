package com.z4niming3d;

import static android.opengl.GLES20.*;
import static niming.util.Constants.*;
import niming.util.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import niming.parser.Number3d;
import niming.parser.ParseObj;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class ObjRenderer implements Renderer {
	private static final int BYTES_PER_FLOAT = 4;
	private static final int NUM_PER_VER = 3;
	private static final int GL_TRIANGLES_STRIP = 0;
	
	public ParseObj obj = null;
	private final Context context;
	private FloatBuffer floatBuffer;
	private ByteBuffer indexArray;
	
	private int program;
//	���һ��uniform��λ��
	private int uColorLocation;
	private int aPositionLocation;
	
	private final FloatBuffer vertexData;
	
	public ObjRenderer(Context context) {
		this.context = context;

		//==========================Test========================================
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
		
		//==================================================================	
		
		
		
		
		
		
		
		
//		 obj = new ParseObj(context, R.raw.cube);
//	     obj.parser();
        //�������ݶ��Ѿ���ȡ��ϣ�
	        
	        
//        /*����ת�������飨����ɺ�����*/
//        float[] verArray = new float[obj.verticesNum * NUM_PER_VER];
//        int j = 0;
//        Iterator iter = obj.vertices.iterator();
//        while(iter.hasNext()){
//        	Number3d vertex = (Number3d) iter.next();
//        	verArray[j++] = vertex.getX();
//        	verArray[j++] = vertex.getY();
//        	verArray[j++] = vertex.getZ();
//        }//��ArrayList<Number3d> vertices ��
//        
//        /*�����ݸ��Ƶ������ڴ�2.4*/
//		floatBuffer = ByteBuffer
//				.allocateDirect(j * BYTES_PER_FLOAT)//jΪ���鳤��
//				.order(ByteOrder.nativeOrder())
//				.asFloatBuffer()
//				.put(verArray);//���䱾���ڴ�floatBuffer,�����洢����������� 
//		
//		
//		
//		/*׼����������*/
//		byte[] indexTemp = new byte[obj.vIndexNum];
//		for (int i = 0; i < obj.vIndexNum; i++) {
//			indexTemp[i] = (byte)obj.vIndex.get(i);
//		}
//		indexArray = ByteBuffer.allocateDirect(obj.vIndexNum)
//				.put(indexTemp);
//		indexArray.position(0);
//		

		
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		/*���ݴ���OpenGL����*/
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
//		if(LoggerConfig.ON){
//			ShaderHelper.validateProgram(program);//���program�Ƿ���Ч
//		}
		glUseProgram(program); //����OpenGL�ڻ����κζ�������Ļ�ϵ�ʱ��Ҫʹ�����ﶨ��ĳ���
		
		/**�����Ĳ�����ȡuniformλ��*/
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		/*��ȡ����λ��*/
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		
		/**�����岽�����������붥������*/
		/**��ɫ������װ��(shader plumbing)������ɫ��������Ӧ�ó�������ݹ�������*/
		vertexData.position(0);//�����ڴ��еĶ������飬����ָ��λ�ù�0
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
	
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);//Clear the rendering surface.
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
	
	
	
	
	
	
	
	
	
//		ByteBuffer bb = arr2ByteBuffer(Arrays.toString(obj.vIndex.toArray()).getBytes());//��ArrayListת��Ϊ����
	public static ByteBuffer arr2ByteBuffer(byte[] arr){  
	    //�����ֽڻ������ռ�,��Ŷ�������  
	    ByteBuffer ibb=ByteBuffer.allocateDirect(arr.length);  
	    //����˳�򣨱������ݣ�  
	    ibb.order(ByteOrder.nativeOrder());  
	    //���ö�����������  
	    ibb.put(arr);  
	    //��λָ��λ��,�Ӹ�λ�ÿ�ʼ��ȡ��������  
	    ibb.position(0);  
	    return ibb;  
	}  

}