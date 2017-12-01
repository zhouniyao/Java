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

public class CopyOfTest2ScreenRenderer implements Renderer {
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
//    private static final String A_POSITION = "a_Position";
//    private static final String A_COLOR = "a_Color";
//    private static final String A_TEXCOORD = "a_TexCoordinate";
//    private static final String U_MVPMATRIX = "u_MVPMatrix";
    
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
       
/*************************************************��������*************************************************/
    float aspectRatio;
    private FloatBuffer vertexData;
    int uMatrixLocation;
	private static final int POSITION_COMPONENT_COUNT = 2;
//	private static final int POSITION_COMPONENT_COUNT = 4;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    
    public void initData() {

	}
    public CopyOfTest2ScreenRenderer(Context context) {
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
		
        
        //����һ��������Դ�ļ�����ɫ��Դ���룩��ȡ�� String��
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
//		glUseProgram(program);
		/**�ж���ɫ�������Ƿ����*/
		if(LoggerConfig.ON){
			ShaderHelper.validateProgram(program);
		}
//		glUseProgram(program);
		/**
		 * �����Ĳ���Get��ȡ����λ�ã���ɫ����
		 */
//		һ����ɫ����������һ���ˣ����ɵ���glGetAttribLocation()��ȡ���Ե�λ�á�
//		aPositionLocation = glGetAttribLocation(program, "a_Position");
//		aColorLocation = glGetAttribLocation(program, "a_Color");
//		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
//		/*uniform����λ��*/
//		uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
//		uTextureUnitLoaction = glGetUniformLocation(program, "u_Texture");
		/**
		 * �����岽��������������ɫ�����Ա���bind
		 * ��ɫ������װ��(shader plumbing)������ɫ��������Ӧ�ó�������ݹ�������
		 */
//		mCubePositions.position(0);//�����ڴ��еĶ������飬����ָ��λ�ù�0
//		mCubeColors.position(0);
//		mCubeTextureCoordinates.position(0);
////		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
//		/*OpenGL�ӻ�����(FloatBuffer)�ж�ȡ����*/
//		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, mCubePositions);
//		glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, mCubeColors);
//		glVertexAttribPointer(aTexCoordLocation, 2, GL_FLOAT, false, 0, mCubeTextureCoordinates);
//		
//		glEnableVertexAttribArray(aPositionLocation);
//		glEnableVertexAttribArray(aColorLocation);
//		glEnableVertexAttribArray(aTexCoordLocation);
//		
		/*��������ͼƬ*/
		texture1 = TextureHelper.loadTexture(context, R.drawable.wl);//��ֻС�۷�
		texture2 = TextureHelper.loadTexture(context, R.drawable.taiji);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
//		Log.d(TAG, "$��3��"+ width + "    " + "$��3��" + height);
		screenWidth = width;
		screenHeight= height;
		aspectRatio = width > height ?
				(float)width/(float)height : (float)height/(float)width;
				

	}

	long time;
	float angleInDegrees;
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//����������ɫ����֮ǰ��glClearColor�����ɫ
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        
   
        
//		aPositionLocation = glGetAttribLocation(program, A_POSITION);
//		aColorLocation = glGetAttribLocation(program, A_COLOR);
//		aTexCoordLocation = glGetAttribLocation(program, A_TEXCOORD);
//		/*uniform����λ��*/
//		uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
		
//		glViewport(0, 0, screenWidth/2, screenHeight);
        
        /*�ӵ����*/
        // Position the eye in front of the origin.
        float eyeX = -0.0f;
        float eyeY = 0.0f;
        float eyeZ = 0.0f;
        
        // We are looking toward the distance
        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -5.0f;
        
        // Set our up vector. This is where our head would be pointing were we holding the camera.
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;
        
        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
//		Matrix.setLookAtM(viewMatrix, 0, lookX, lookY, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
        
//		orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        final float left = -aspectRatio;
        final float right = aspectRatio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
//		Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);
        
		setIdentityM(modelMatrix, 0);//��һ��
		translateM(modelMatrix, 0, 0f, 0f, -0.0f);//��z��ƽ��-3
		rotateM(modelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
//		scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
		

		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture2);
		glUniform1i(uTextureUnitLoaction, 0);
		
		drawCube();
		
//		glViewport(screenWidth/2, 0, screenWidth, screenHeight);
//		setIdentityM(modelMatrix, 0);//��һ��
//		translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
////		scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
////		temp = new float[16];
//		drawCube();
				
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	  /**
     * Draws a cube.
     */
    private void drawCube()
    {
//		glUseProgram(program);
		
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aColorLocation = glGetAttribLocation(program, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
		/*uniform����λ��*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_Texture");
		
		mCubePositions.position(0);//�����ڴ��еĶ������飬����ָ��λ�ù�0
		mCubeColors.position(0);
		mCubeTextureCoordinates.position(0);
//		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		/*OpenGL�ӻ�����(FloatBuffer)�ж�ȡ����*/
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, mCubePositions);
		glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, mCubeColors);
		glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, mCubeTextureCoordinates);
		
		glEnableVertexAttribArray(aPositionLocation);
		glEnableVertexAttribArray(aColorLocation);
		glEnableVertexAttribArray(aTextureCoordLocation);
		
        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

}
