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
 * 平分界面，画两个正方形
 */
public class DoubleScene implements Renderer{

		private static final String TAG = "niming";
		private final Context context;
		/*定义各类变换矩阵*/
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
	    
	    /*顶点数据准备*/
	    /** Store our model data in a float buffer. */
	    private FloatBuffer mCubePositions;
	    private FloatBuffer mCubeColors;
	    private FloatBuffer mCubeTextureCoordinates;
	    private FloatBuffer floatBuffer;
	    private int program;
	    
	    /*常量定义区*/
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
	    //纹理
	    int texture1;
	    int texture2;
	       
	/*************************************************测试数据*************************************************/
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
	    	//分配一个大小为………的本地内存块
	    	vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
	    			//告诉字节缓冲区(byte buffer)按照本地字节序(native byte order)组织它的内容
	    			.order(ByteOrder.nativeOrder())
	    			//我们不愿直接操作单独的字节，而是希望使用浮点数，因此调用asFloatBuffer()得到一个可以反映底层字节的FloatBuffer类的实例
	    			.asFloatBuffer();
	    	//然后调用		vertexData.put(tableVerticesWithTriangles)把数据从Dalvik的内存中复制到本地内存
	    	vertexData.put(tableVerticesWithTriangles);
	    	
	    	
	    }

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//白色
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//黑色	//清屏用的颜色
			
			String vertexShaderSource   = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_vertex_shader);
			String fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_fragment_shader);
			int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
			int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
			program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
			glUseProgram(program);
			
//			一旦着色器被链接在一起了，调用glGetAttribLocation()获取属性的位置。
			aColorLocation = glGetAttribLocation(program, "a_Color");
			aPositionLocation = glGetAttribLocation(program, "a_Position");
			
			vertexData.position(0);//本地内存中的顶点数组，它的指针位置归0
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
			Log.d(TAG, "$宽3："+ width + "    " + "$高3：" + height);
			windowWidth = width;
			windowHeight = height;
			aspectRatio = width > height ?
					(float)width/(float)height :
						(float)height/(float)width;
			
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清屏所有颜色，用之前的glClearColor填充颜色
			
			glViewport(0, 0, windowWidth/2, windowHeight);
			setIdentityM(modelMatrix, 0);//归一化
			translateM(modelMatrix, 0, 0f, 0f, -0.5f);//沿z轴平移-3
			scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
			float[] temp = new float[16];
			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
			multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
			System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
			
					
			GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//把projectionMatrix与着色器uMatrixLocation关联
			glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
			
	/**************************************第二个物体************************************************/   	
			glViewport(windowWidth/2, 0, windowWidth/2, windowHeight);
//			setIdentityM(modelMatrix, 0);//归一化
//			translateM(modelMatrix, 0, -1.0f, 0f, -0.5f);//沿z轴平移-3
//			scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
//			temp = new float[16];
//			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//			multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//			System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
			GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//把projectionMatrix与着色器uMatrixLocation关联
			
			glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		}

	}


