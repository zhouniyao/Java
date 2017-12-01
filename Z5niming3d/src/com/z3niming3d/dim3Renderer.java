package com.z3niming3d;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static niming.util.Constants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import niming.parser.Number3d;
import niming.parser.ParseObj;
import niming.util.*;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class dim3Renderer implements Renderer {
	private static final int POSITION_COMPONENT_COUNT = 3;
//	private static final int POSITION_COMPONENT_COUNT = 4;
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer vertexData; //��������
	private final FloatBuffer colorData; //��ɫ����
	private ByteBuffer indexArray; //��������
	private float[] verArray;
	private float[] colorArray;
	private final Context context;
	private int program;
	//	���һ��uniform��λ��
	private int aColorLocation;
	//��ȡ���Ե�λ��
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;
	
	private static final String A_COLOR = "a_Color";
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	
	/*������*/
	private static final String U_MATRIX = "u_Matrix";
	private static final int NUM_PER_VER = 3;
	private final float[] projectionMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	private int uMatrixLocation;
	
	/*������*/
	//ͶӰ����ƽ�ƾ�����ת����
	private final float[] modelMatrix = new float[16];

	
	/*ParseObj*/
	private static final String TAG = "Render";
	ParseObj obj = null;
	
	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	public dim3Renderer(Context context) {
		this.context = context;
		Log.v(TAG, "��ʼ����");
//		 obj = new ParseObj(context, R.raw.umbrella);
		 obj = new ParseObj(context, R.raw.cube);
	     obj.parser();
        //�������ݶ��Ѿ���ȡ��ϣ�
	        
	        
        /*����ת�������飨����ɺ�����*/
        verArray = new float[obj.verticesNum * NUM_PER_VER];
        int j = 0;
        Iterator iter = obj.vertices.iterator();
        while(iter.hasNext()){
        	Number3d vertex = (Number3d) iter.next();
        	verArray[j++] = vertex.getX();
        	verArray[j++] = vertex.getY();
        	verArray[j++] = vertex.getZ();
        }//��ArrayList<Number3d> vertices ��
        
        /*�����ݸ��Ƶ������ڴ�2.4*/
		vertexData = ByteBuffer
				.allocateDirect(j * BYTES_PER_FLOAT)//jΪ���鳤��
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(verArray);//���䱾���ڴ�vertexData,�����洢����������� 
		
		
		
//		/*׼��������������*/
		byte[] indexTemp = new byte[obj.vIndexNum];
		for (int i = 0; i < obj.vIndexNum; i++) {
			indexTemp[i] = (byte)obj.vIndex.get(i);
		}
		indexArray = ByteBuffer.allocateDirect(obj.vIndexNum)
				.put(indexTemp);
		
		/*�������ݳɹ����뱾���ڴ�*/
//		indexArray = ByteBuffer.allocateDirect(obj.vIndex.size())
//				.put(new byte[]{
//						1-1,2-1,4-1,  2-1,4-1,3-1,
//						3-1,4-1,6-1,  4-1,6-1,5-1,
//						5-1,6-1,8-1,  6-1,8-1,7-1,
//						7-1,8-1,2-1,  8-1,2-1,1-1,
//						2-1,8-1,6-1,  8-1,6-1,4-1,
//						7-1,1-1,3-1,  1-1,3-1,5-1
//						
//				});
		indexArray.position(0);
		
		/**��vn����ɫ����*/
		colorArray = new float[obj.normals.size() * NUM_PER_VER];;
		iter = obj.normals.iterator();
		j = 0;
        while(iter.hasNext()){
        	Number3d color = (Number3d) iter.next();
        	colorArray[j++] = color.getX();
        	colorArray[j++] = color.getY();
        	colorArray[j++] = color.getZ();
        }
        
        /*�����ݸ��Ƶ������ڴ�2.4*/
		colorData = ByteBuffer
				.allocateDirect(j * BYTES_PER_FLOAT)//jΪ���鳤��
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(colorArray);//���䱾���ڴ�vertexData,�����洢����������� 
//		Log.v(TAG, "" + obj.vIndexNum);//36
}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
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
//		һ����ɫ����������һ���ˣ�����glGetAttribLocation()��ȡ���Ե�λ�á�
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		vertexData.position(0);//ָ��λ��
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		//Ѱ�Ҷ�������
		glEnableVertexAttribArray(aPositionLocation);
		
//		��ȡuniform��λ�ã��������λ�ô���uColorLocation�С�
		aColorLocation = glGetUniformLocation(program, A_COLOR);
		colorData.position(0);
		glVertexAttribPointer(aColorLocation, 3, GL_FLOAT, false, 0, colorData);
		glEnableVertexAttribArray(aColorLocation);
		
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		MatrixHelper.perspectiveM(projectionMatrix, 45f, (float)width/(float)height, 1f, 1000f);
		
		setIdentityM(modelMatrix, 0);//��һ��
		translateM(modelMatrix, 0, 0f, 0f, -150f);//ƽ�Ʊ任
		rotateM(modelMatrix, 0, 60f, 1f, 0f, 0f);
		rotateM(modelMatrix, 0, 60f, 0f, 1f, 0f);
		//��������
		//vertex_clip = ProjectionMatrix * vertex_eye
		//vertex_eye = ModelMatrix * vertex_model
		//vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);
		glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//��projectionMatrix����ɫ��uMatrixLocation����
		glDrawElements(GL_TRIANGLES, obj.vIndexNum, GL_UNSIGNED_BYTE, indexArray);//��ʾ����Խ��    obj.vIndexNum
//		glDrawArrays(GL_POINTS, 0, obj.vIndexNum);
//		glDrawArrays(GL_LINES, 0, obj.vIndexNum);
//		glDrawArrays(GL_LINES, 0, 8);
//		drawbox();
	}
    private void drawbox() {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -5f);//ƽ�Ʊ任
        rotateM(modelMatrix, 0, - yRotate, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, - xRotate, 0f, 1f, 0f);
        yRotate++;
        xRotate++;
//        setLookAtM(viewMatrix, 0, xRotation, yRotation, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);//�������Ŵ���С
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
		glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);//��projectionMatrix����ɫ��uMatrixLocation����
		glDrawElements(GL_TRIANGLES, obj.vIndexNum, GL_UNSIGNED_BYTE, indexArray);//��ʾ����Խ��    obj.vIndexNum
//		glDrawElements(GL_TRIANGLES, 6*6, GL_UNSIGNED_BYTE, indexArray);//��ʾ����Խ��    obj.vIndexNum
    }

}
