package com.example.virsualreality01;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;




import niming.util.LoggerConfig;
import niming.util.MatrixHelper;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;


import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
/**
 *  ������Ļ����ͬ�����壬��ͬ������ͬ�ӽǵĽ����������
 *
 */
public class Test2ScreenRenderer implements Renderer {
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
    private int program;
    
    /*����������*/
    private static final int BYTES_PER_FLOAT = 4;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String A_TEXCOORD = "a_TexCoordinate";
    private static final String U_MVPMATRIX = "u_MVPMatrix";
    
    private int aPositionLocation;
    private int aColorLocation;
    private int aTextureCoordLocation;
    private int uTextureUnitLoaction;
    private int uMVPMatrixLocation;
    private int screenWidth;
    private int screenHeight;
    //����
    int texture1;
    int texture2;
       
    float aspectRatio;
	private static final int POSITION_COMPONENT_COUNT = 2;
//	private static final int POSITION_COMPONENT_COUNT = 4;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    
    public Test2ScreenRenderer(Context context) {
    	this.context = context;
    	
    	
    	
    	 // Define points for a cube.

        final float[] cubePositionData =
        {
        		// X, Y, Z

                // Front face
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,

                // Right face
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,

                // Back face
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                // Left face
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,

                // Bottom face
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
        };

        // R, G, B, A
        final float[] cubeColorData =
        {
                // Front face (red)
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                // Right face (green)
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                // Back face (blue)
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                // Left face (yellow)
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                // Top face (cyan)
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,

                // Bottom face (magenta)
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f
        };

        // S, T (or X, Y)
        // Texture coordinate data.
        final float[] cubeTextureCoordinateData =
        {
                // Front face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Right face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Back face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Left face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Top face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Bottom face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        // Initialize the buffers.
        mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubePositions.put(cubePositionData).position(0);

        mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeColors.put(cubeColorData).position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
    	
    }

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//��ɫ
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//��ɫ	//�����õ���ɫ
        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.0 Enable texture mapping
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        
		/**
		 * ����һ��������Դ�ļ�����ɫ��Դ���룩��ȡ�� String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.per_pixel_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.per_pixel_fragment_shader);
		/**
		 * ���ڶ�����������ɫ��Դ���룬����OpenGL���ɵ�ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		/**
		 * ����������һ��OpenGL������ǰ�һ��������ɫ����һ��Ƭ����ɫ��������һ���ɵ������󣬶�����ɫ����Ƭ����ɫ������һ�����ġ� 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		glUseProgram(program);
		
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aColorLocation = glGetAttribLocation(program, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
		/*uniform����λ��*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
		
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, mCubePositions);
		glEnableVertexAttribArray(aPositionLocation);
		glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, mCubeColors);
		glEnableVertexAttribArray(aColorLocation);
		glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, mCubeTextureCoordinates);
		glEnableVertexAttribArray(aTextureCoordLocation);
		
		
		/*��������ͼƬ*/
		texture1 = TextureHelper.loadTexture(context, R.drawable.wl, true);//��ֻС�۷�
		texture2 = TextureHelper.loadTexture(context, R.drawable.taiji, true);	
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
//		Log.d(TAG, "$��3��"+ width + "    " + "$��3��" + height);
		screenWidth = width;
		screenHeight= height;
		aspectRatio = width > height ?
				(float)width/(float)height : (float)height/(float)width;
				
//		MatrixHelper.perspectiveM(projectionMatrix, 45, (float)height/(float)width, 1f, 10f);		
//		setIdentityM(modelMatrix, 0);//��һ��
//		translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
//		rotateM(modelMatrix, 0, -80f, 1f, 0f, 0f);//��x����ת-60��
//		//��������
//		/*
//		  vertex_clip = ProjectionMatrix * vertex_eye
//		  vertex_eye  = ModelMatrix * vertex_model
//		  vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
//		*/
//		final float[] temp = new float[16];
//		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
				
				//͸��ͶӰ
				Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 10f);
//				MatrixHelper.perspectiveM(projectionMatrix, 45, 1f, aspectRatio, 10f);	
		      
				
//				for (int i = 0; i < projectionMatrix.length; i++) {
//					Log.i("Matrix["+ i +"]", ""+MVPMatrix[i]);
//				}
	}

	long time;
	float angleInDegrees;
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//����������ɫ����֮ǰ��glClearColor�����ɫ
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        //�ӵ�λ��
        glViewport(0, 0, screenWidth/2, screenHeight);
        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
        
		setIdentityM(modelMatrix, 0);//��һ��
		translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
		scaleM(modelMatrix, 0, 1f, 1f, 1f);
		rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����ת
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//��ͼ���������
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture1);
    	glUniform1i(uTextureUnitLoaction, 0);
        
        drawCube();
 /**************************************�ڶ������塾�ұߵġ�************************************************/      
        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
        //�任����
//        Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 10f);
//        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)screenWidth/(float)screenHeight, 1f, 10f);	
        Matrix.setLookAtM(viewMatrix, 0, 1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//		setIdentityM(modelMatrix, 0);//��һ��
//		translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
//		scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
//		rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����ת
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//��ͼ���������
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
        drawCube();
	}
	
	/**
     * Draws a cube.
     */
    private void drawCube()
    {
    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

}
