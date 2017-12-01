package com.example.virsualreality01;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
/**
 * ƽ�ֽ��棬������������
 */
public class DoubleScene implements Renderer{

		private static final String TAG = "niming";
		private final Context context;
		/*�������任����*/
		  /**
	     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	     * of being located at the center of the universe) to world space.
	     */
	    private float[] modelMatrix = new float[16];

	    /**
	     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	     * it positions things relative to our eye.
	     */
	    private float[] viewMatrix = new float[16];

	    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	    private float[] projectionMatrix = new float[16];

	    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	    private float[] MVMatrix = new float[16];
	    private float[] MVPMatrix = new float[16];
	    
	    /*��������׼��*/
	    /** Store our model data in a float buffer. */
	    private FloatBuffer mCubePositions;
	    private FloatBuffer mCubeColors;
	    private FloatBuffer mCubeTextureCoordinates;
	    private FloatBuffer floatBuffer;
	    private int program;
	    
	    /*����������*/
	    private static final int BYTES_PER_FLOAT = 4;
	    private static final String A_POSITION = "a_Position";
	    private static final String A_COLOR = "a_Color";
	    private static final String A_TEXCOORD = "a_TexCoordinate";
	    private static final String U_MVMATRIX = "u_MVMatrix";
	    private static final String U_MVPMATRIX = "u_MVPMatrix";
	    
	    private int aPositionLocation;
	    private int aColorLocation;
	    private int aTexCoordLocation;
	    private int uMVMatrixLocation;
	    private int uMVPMatrixLocation;
	    private int windowWidth;
	    private int windowHeight;
	    //����
	    int texture1;
	    int texture2;
	       
	/*************************************************��������*************************************************/
	    float aspectRatio;
	    private final FloatBuffer vertexData;
	    int uMatrixLocation;
		private static final int POSITION_COMPONENT_COUNT = 2;
//		private static final int POSITION_COMPONENT_COUNT = 4;
		private static final int COLOR_COMPONENT_COUNT = 3;
		private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	    
	    public void initData() {

		}
	    public DoubleScene(Context context) {
	    	this.context = context;
	    	
			float[] tableVerticesWithTriangles = {
					//table brim
					//x, y, r, g, b
//		            0f,     0f,    1f,    1f,    1f,         
//		            -0.1f, -0.1f, 0.7f, 0.7f, 0.7f,            
//		             0.1f, -0.1f, 0.7f, 0.7f, 0.7f,
//		             0.1f,  0.1f, 0.7f, 0.7f, 0.7f,
//		            -0.1f,  0.1f, 0.7f, 0.7f, 0.7f,
//		            -0.1f, -0.1f, 0.7f, 0.7f, 0.7f,
		            0f,     0f,    1f,    0f,    0f,         
		            -1.77f, -1f, 0.7f, 0.7f, 0.7f,            
		             1.77f, -1f, 0.7f, 0.7f, 0.7f,
		             1.77f,  1f, 0.7f, 0.7f, 0.7f,
		            -1.77f,  1f, 0.7f, 0.7f, 0.7f,
		            -1.77f, -1f, 0.7f, 0.7f, 0.7f,
//					 -0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
//					 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
//					 -0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
//					 -0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
//					 0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
//					 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
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
//			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//��ɫ
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//��ɫ	//�����õ���ɫ
			
			String vertexShaderSource   = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_vertex_shader);
			String fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_fragment_shader);
			int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
			int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
			program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
			glUseProgram(program);
			
//			һ����ɫ����������һ���ˣ�����glGetAttribLocation()��ȡ���Ե�λ�á�
			aColorLocation = glGetAttribLocation(program, "a_Color");
			aPositionLocation = glGetAttribLocation(program, "a_Position");
			
			vertexData.position(0);//�����ڴ��еĶ������飬����ָ��λ�ù�0
			glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
			glEnableVertexAttribArray(aPositionLocation);
			vertexData.position(POSITION_COMPONENT_COUNT);
			glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
			glEnableVertexAttribArray(aColorLocation);
			
			uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
//			glViewport(0, 0, width, height);
			Log.d(TAG, "$��3��"+ width + "    " + "$��3��" + height);
			windowWidth = width;
			windowHeight = height;
			aspectRatio = width > height ?
					(float)width/(float)height :
						(float)height/(float)width;
			
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//����������ɫ����֮ǰ��glClearColor�����ɫ
			
			glViewport(0, 0, windowWidth/2, windowHeight);
			setIdentityM(modelMatrix, 0);//��һ��
			translateM(modelMatrix, 0, 0f, 0f, -0.5f);//��z��ƽ��-3
			scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
			float[] temp = new float[16];
			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
			multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
			System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
			
					
			GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//��projectionMatrix����ɫ��uMatrixLocation����
			glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
			
	/**************************************�ڶ�������************************************************/   	
			glViewport(windowWidth/2, 0, windowWidth/2, windowHeight);
//			setIdentityM(modelMatrix, 0);//��һ��
//			translateM(modelMatrix, 0, -1.0f, 0f, -0.5f);//��z��ƽ��-3
//			scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
//			temp = new float[16];
//			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//			multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//			System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
			GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//��projectionMatrix����ɫ��uMatrixLocation����
			
			glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		}

	}


