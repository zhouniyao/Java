package niming.virsualreality02;


import static android.opengl.GLES20.*;

import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import niming.core.MyActivity;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * ������˫Ŀ��ת�����Ǹ�ģ�塿
 *
 */
public class HandlerCube extends MyActivity{
	private GLSurfaceView mGLSurfaceView;
	private LinearLayout ll;
	TextView textView ;
	TextView textView2 ;
	Handler hd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		mGLSurfaceView = new GLSurfaceView(this);
		  // Request an OpenGL ES 2.0 compatible context.
        mGLSurfaceView.setEGLContextClientVersion(2);            
        // Assign our renderer.
        mGLSurfaceView.setRenderer(new HandlerScreenRenderer(HandlerCube.this));	//��cube
//        mGLSurfaceView.setRenderer(new Self2ScreenRenderer(this, dis));	//��cube
//        mGLSurfaceView.setRenderer(new DoubleScene(this));	    //������������
        ll = (LinearLayout) findViewById(R.id.glPlane);
	    ll.addView(mGLSurfaceView);   
        mGLSurfaceView.requestFocus();//��ȡ����
//        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
        mGLSurfaceView.setFocusableInTouchMode(false); 
        
        textView = (TextView) findViewById(R.id.copyLeft);
        textView2 = (TextView) findViewById(R.id.copyRight);
        /*
         * ������֪Android����ص�view�Ϳؼ������������̰߳�ȫ�ģ�����android�Ż��ֹ�ڷ�UI�̸߳���UI��������ʽ�ķǷ�������
         * ����˵ֱ����Activity�ﴴ�����̣߳�Ȼ��ֱ�������߳��в���UI�ȣ�Android��ֱ���쳣�˳�������ʾshould run on UIThread֮��Ĵ�����־��Ϣ��
         * Only the original thread that created a view hierarchy can touch its views����һ�����ӣ�������˼��ֻ�д�����ͼ��νṹ��ԭʼ�̲߳��ܲ�������View���������̰߳�ȫ��صġ�
         */
        /**ʵ���̼߳�ͨ��*/
        /*
         * Android����Handlerʵ���̼߳��ͨ�����UI����
         */
        hd = new Handler(){
    		//�ڲ���
    		/*
    		 * �������ʵ�����������ȥ������Ϣ
    		 */
    		public void handleMessage(Message msg) {//��дhandlerMessage����
    			switch (msg.what) {
    			case 0:
    				Bundle b = msg.getData();//���Bundle����
    				String str = b.getString("msg");//ͨ�����������Ϣ
    				textView.setText(str);
    				textView2.setText(str);
    				break;

    			default:
    				break;
    			}
    		}
    	};
	}
	/**
	 * ���ò˵�������
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Renderer�ڲ���
	private class HandlerScreenRenderer implements GLSurfaceView.Renderer{
		private static final String TAG = "niming";
		private final Context context;
		/*�������任����*/
		private float[] modelMatrix = new float[16];
		private float[] viewMatrix = new float[16];
		private float[] projectionMatrix = new float[16];
	    private float[] MVMatrix = new float[16];//�������˵��м���
	    private float[] MVPMatrix = new float[16];//���յľ���
	    /*��������׼��*/
	    /* Store our model data in a float buffer. */
	    private FloatBuffer mCubePositions;
	    private FloatBuffer mCubeColors;
	    private FloatBuffer mCubeTextureCoordinates;
	    /*openGL�󶨵���ɫ������ID*/
	    private int program;
	    /*����������*/
	    private static final int BYTES_PER_FLOAT = 4;
		private static final int POSITION_COMPONENT_COUNT = 2;
		/*private static final int POSITION_COMPONENT_COUNT = 4;*/
		private static final int COLOR_COMPONENT_COUNT = 3;
	    private static final String A_POSITION = "a_Position";
	    private static final String A_COLOR = "a_Color";
	    private static final String A_TEXCOORD = "a_TexCoordinate";
	    private static final String U_MVPMATRIX = "u_MVPMatrix";
	    
	    /*����װ��ʱ������ɫ��������OpenGL�ڵ�Location*/
	    private int aPositionLocation;
	    private int aColorLocation;
	    private int aTextureCoordLocation;
	    private int uTextureUnitLoaction;
	    private int uMVPMatrixLocation;
	    
	    /*������Ļ�Ŀ��pxֵ*/
	    private int screenWidth;
	    private int screenHeight;
	    float aspectRatio;//��Ļ��߱ȣ���/�� > 1
	    /*����ID*/
	    int texture1;
	    int texture2;
	    
		
		public HandlerScreenRenderer(Context context) {//�������ͨ��,
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
			/*attribute������λ*/
			aPositionLocation = glGetAttribLocation(program, "a_Position");
			aColorLocation = glGetAttribLocation(program, "a_Color");
			aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
			/*uniform����λ�ö�λ*/
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
			texture2 = TextureHelper.loadTexture(context, R.drawable.taiji, true);//̫��ͼ
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			glViewport(0, 0, width, height);
//			Log.i(TAG, "$��3��"+ width + "    " + "$��3��" + height);
			screenWidth = width;
			screenHeight= height;
			/**screenWidth/2 < screenHeight */ 
			//��Ļ��-���(��������),��aspecRatio > 1
			aspectRatio = (width/2) > height ?
					(float)(width/2)/(float)height : (float)height/(float)(width/2);
		
		}
		/*ʱ�����ת�Ƕ�*/
		long time;
		long startTime = SystemClock.uptimeMillis();
		
		float angleInDegrees;
		float binocularDistance = 0f;//˫Ŀ����
		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//����������ɫ����֮ǰ��glClearColor�����ɫ
	        // Do a complete rotation every 10 seconds.
	        time = SystemClock.uptimeMillis() % 10000L;
	        angleInDegrees = (360.0f / 10000.0f) * ((int) time);
	        //��Ϊ��Ļ�ֳ���������
	        glViewport(0, 0, screenWidth/2, screenHeight);
	    	//͸��ͶӰ
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 10f);
	        //�ӵ�λ��
	        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
			setIdentityM(modelMatrix, 0);//��һ��
			translateM(modelMatrix, 0, 0f, 0f, -2f);//��z��ƽ��-3
			scaleM(modelMatrix, 0, 1f, 1f, 1f);
			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����ת-60��
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	    	//��ͼ���������
	    	glActiveTexture(GL_TEXTURE0);
	    	glBindTexture(GL_TEXTURE_2D, texture1);
	    	glUniform1i(uTextureUnitLoaction, 0);
	        
	        drawCube();
	 /**************************************�ڶ������塾�ұߡ�************************************************/      
	        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
	    	//͸��ͶӰ
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 10f);
	        //����任����
	        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			setIdentityM(modelMatrix, 0);//��һ��
//			translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
//			scaleM(modelMatrix, 0, 1f, 1f, 1f);
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����ת-60��
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	    	//��ͼ���������
	    	glActiveTexture(GL_TEXTURE0);
	    	glBindTexture(GL_TEXTURE_2D, texture1);
	    	glUniform1i(uTextureUnitLoaction, 0);
	        drawCube();
	        
	        //��ӡ˫Ŀ����
	        long endTime = SystemClock.uptimeMillis();
	        if((endTime - startTime) > 1000){//ÿ�����һ��
		        /*�����ӵ�ľ���*/
//	        	Log.i("˫Ŀ����put:", ""+binocularDistance);
//	        	Log.i("startTime:", ""+startTime);
//	        	Log.i("endTime:", ""+endTime);
	        	DecimalFormat dFormat = new DecimalFormat("##0.000");
	        	String msg = dFormat.format(binocularDistance);
				Bundle bd = new Bundle();//����Bundle����
				bd.putString("msg", msg);//��Bundle�������Ϣ��putString(String key, String value)��ֵ����ʽ
				Message tempMessage = new Message();//����Message����
				tempMessage.setData(bd);//��Message���������
				tempMessage.what = 0;
				hd.sendMessage(tempMessage);//���ء��������������е�Handler��������Ϣ
	        	startTime = endTime;
	        	binocularDistance += 0.001f;
	        	if(binocularDistance > 2)
	        		binocularDistance = 0.0f;
	        }
		}
		/**
	     * Draws a cube.
	     */
	    private void drawCube()
	    {
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
	    }
		
	}
}




/**
 * �Զ�����߳�
 */
//class LooperThread2 extends Thread{
//	public Handler mHandler;//����Handler����
//	/* ��дrun()�������ڸ÷���������ͨ��Looper����prepare����������Ϣ���С�
//	 * Ȼ�󣬴���Handler�ڲ��࣬������д��handleMessage(Message msg)�������ڸ÷�������Ҫ�Ǵ�����Ϣ��ҵ���߼�
//	 * ���ͨ��Looper����loop����������Ϣѭ����
//	 */
//	public void run(){
//		Looper.prepare();  //������Ϣ����
//		mHandler = new Handler(){//����Handler�ڲ���
//			public void handleMessage(Message msg) { //��дhandleMessage����
//				//TODO
//				//������Ϣ��ҵ���߼�
//			}
//		};
//		Looper.loop(); //������Ϣѭ��
//	}
//}