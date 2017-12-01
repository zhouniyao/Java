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
 * 立方体双目旋转【这是个模板】
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
        mGLSurfaceView.setRenderer(new HandlerScreenRenderer(HandlerCube.this));	//画cube
//        mGLSurfaceView.setRenderer(new Self2ScreenRenderer(this, dis));	//画cube
//        mGLSurfaceView.setRenderer(new DoubleScene(this));	    //画两个正方形
        ll = (LinearLayout) findViewById(R.id.glPlane);
	    ll.addView(mGLSurfaceView);   
        mGLSurfaceView.requestFocus();//获取焦点
//        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
        mGLSurfaceView.setFocusableInTouchMode(false); 
        
        textView = (TextView) findViewById(R.id.copyLeft);
        textView2 = (TextView) findViewById(R.id.copyRight);
        /*
         * 众所周知Android中相关的view和控件操作都不是线程安全的，所以android才会禁止在非UI线程更新UI，对于显式的非法操作，
         * 比如说直接在Activity里创建子线程，然后直接在子线程中操作UI等，Android会直接异常退出，并提示should run on UIThread之类的错误日志信息。
         * Only the original thread that created a view hierarchy can touch its views便是一个例子，字面意思是只有创建视图层次结构的原始线程才能操作它的View，明显是线程安全相关的。
         */
        /**实现线程间通信*/
        /*
         * Android利用Handler实现线程间的通信完成UI更新
         */
        hd = new Handler(){
    		//内部类
    		/*
    		 * 子类必须实现这个方法，去接收消息
    		 */
    		public void handleMessage(Message msg) {//重写handlerMessage方法
    			switch (msg.what) {
    			case 0:
    				Bundle b = msg.getData();//获得Bundle对象
    				String str = b.getString("msg");//通过键，获得消息
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
	 * 设置菜单不可用
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Renderer内部类
	private class HandlerScreenRenderer implements GLSurfaceView.Renderer{
		private static final String TAG = "niming";
		private final Context context;
		/*定义各类变换矩阵*/
		private float[] modelMatrix = new float[16];
		private float[] viewMatrix = new float[16];
		private float[] projectionMatrix = new float[16];
	    private float[] MVMatrix = new float[16];//矩阵连乘的中间结果
	    private float[] MVPMatrix = new float[16];//最终的矩阵
	    /*顶点数据准备*/
	    /* Store our model data in a float buffer. */
	    private FloatBuffer mCubePositions;
	    private FloatBuffer mCubeColors;
	    private FloatBuffer mCubeTextureCoordinates;
	    /*openGL绑定的着色器程序ID*/
	    private int program;
	    /*常量定义区*/
	    private static final int BYTES_PER_FLOAT = 4;
		private static final int POSITION_COMPONENT_COUNT = 2;
		/*private static final int POSITION_COMPONENT_COUNT = 4;*/
		private static final int COLOR_COMPONENT_COUNT = 3;
	    private static final String A_POSITION = "a_Position";
	    private static final String A_COLOR = "a_Color";
	    private static final String A_TEXCOORD = "a_TexCoordinate";
	    private static final String U_MVPMATRIX = "u_MVPMatrix";
	    
	    /*管线装配时，各着色器变量在OpenGL内的Location*/
	    private int aPositionLocation;
	    private int aColorLocation;
	    private int aTextureCoordLocation;
	    private int uTextureUnitLoaction;
	    private int uMVPMatrixLocation;
	    
	    /*整个屏幕的宽高px值*/
	    private int screenWidth;
	    private int screenHeight;
	    float aspectRatio;//屏幕宽高比，宽/高 > 1
	    /*纹理ID*/
	    int texture1;
	    int texture2;
	    
		
		public HandlerScreenRenderer(Context context) {//添加数据通道,
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
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//黑色	//清屏用的颜色
	        // Use culling to remove back faces.
	        GLES20.glEnable(GLES20.GL_CULL_FACE);
	        // Enable depth testing
	        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.0 Enable texture mapping
	        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
	        
			/**
			 * 【第一步】将资源文件（着色器源代码）读取至 String
			 */
	        String vertexShaderSource   = TextResourceReader
					.readTextFileFromResource(context, R.raw.per_pixel_vertex_shader);
			String fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.per_pixel_fragment_shader);
			/**
			 * 【第二步】编译着色器源代码，返回OpenGL生成的ID
			 */
			int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
			int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
			/**
			 * 【第三步】一个OpenGL程序就是把一个顶点着色器和一个片段着色器链接在一起变成单个对象，顶点着色器和片段着色器总是一起工作的。 
			 */
			program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
			glUseProgram(program);
			/*attribute变量定位*/
			aPositionLocation = glGetAttribLocation(program, "a_Position");
			aColorLocation = glGetAttribLocation(program, "a_Color");
			aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
			/*uniform常量位置定位*/
			uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
			uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
			
			glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, mCubePositions);
			glEnableVertexAttribArray(aPositionLocation);
			glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, mCubeColors);
			glEnableVertexAttribArray(aColorLocation);
			glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, mCubeTextureCoordinates);
			glEnableVertexAttribArray(aTextureCoordLocation);
			
			/*加载纹理图片*/
			texture1 = TextureHelper.loadTexture(context, R.drawable.wl, true);//四只小蜜蜂
			texture2 = TextureHelper.loadTexture(context, R.drawable.taiji, true);//太极图
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			glViewport(0, 0, width, height);
//			Log.i(TAG, "$宽3："+ width + "    " + "$高3：" + height);
			screenWidth = width;
			screenHeight= height;
			/**screenWidth/2 < screenHeight */ 
			//屏幕高-宽比(两个半屏),即aspecRatio > 1
			aspectRatio = (width/2) > height ?
					(float)(width/2)/(float)height : (float)height/(float)(width/2);
		
		}
		/*时间和旋转角度*/
		long time;
		long startTime = SystemClock.uptimeMillis();
		
		float angleInDegrees;
		float binocularDistance = 0f;//双目距离
		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清屏所有颜色，用之前的glClearColor填充颜色
	        // Do a complete rotation every 10 seconds.
	        time = SystemClock.uptimeMillis() % 10000L;
	        angleInDegrees = (360.0f / 10000.0f) * ((int) time);
	        //因为屏幕分成左右两半
	        glViewport(0, 0, screenWidth/2, screenHeight);
	    	//透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 10f);
	        //视点位置
	        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
			setIdentityM(modelMatrix, 0);//归一化
			translateM(modelMatrix, 0, 0f, 0f, -2f);//沿z轴平移-3
			scaleM(modelMatrix, 0, 1f, 1f, 1f);
			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//绕x轴旋转-60°
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	    	//给图像添加纹理
	    	glActiveTexture(GL_TEXTURE0);
	    	glBindTexture(GL_TEXTURE_2D, texture1);
	    	glUniform1i(uTextureUnitLoaction, 0);
	        
	        drawCube();
	 /**************************************第二个物体【右边】************************************************/      
	        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
	    	//透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 10f);
	        //重设变换矩阵
	        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			setIdentityM(modelMatrix, 0);//归一化
//			translateM(modelMatrix, 0, 0f, 0f, -3f);//沿z轴平移-3
//			scaleM(modelMatrix, 0, 1f, 1f, 1f);
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//绕x轴旋转-60°
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	    	//给图像添加纹理
	    	glActiveTexture(GL_TEXTURE0);
	    	glBindTexture(GL_TEXTURE_2D, texture1);
	    	glUniform1i(uTextureUnitLoaction, 0);
	        drawCube();
	        
	        //打印双目距离
	        long endTime = SystemClock.uptimeMillis();
	        if((endTime - startTime) > 1000){//每秒输出一次
		        /*更改视点的距离*/
//	        	Log.i("双目距离put:", ""+binocularDistance);
//	        	Log.i("startTime:", ""+startTime);
//	        	Log.i("endTime:", ""+endTime);
	        	DecimalFormat dFormat = new DecimalFormat("##0.000");
	        	String msg = dFormat.format(binocularDistance);
				Bundle bd = new Bundle();//创建Bundle对象
				bd.putString("msg", msg);//向Bundle中添加信息，putString(String key, String value)键值对形式
				Message tempMessage = new Message();//创建Message对象
				tempMessage.setData(bd);//向Message中添加数据
				tempMessage.what = 0;
				hd.sendMessage(tempMessage);//【重】调用主控制类中的Handler对象发送消息
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
 * 自定义的线程
 */
//class LooperThread2 extends Thread{
//	public Handler mHandler;//创建Handler引用
//	/* 重写run()方法，在该方法中首先通过Looper调用prepare方法创建消息队列。
//	 * 然后，创建Handler内部类，并且重写了handleMessage(Message msg)方法，在该方法中主要是处理消息的业务逻辑
//	 * 最后，通过Looper调用loop方法进入消息循环。
//	 */
//	public void run(){
//		Looper.prepare();  //创建消息队列
//		mHandler = new Handler(){//创建Handler内部类
//			public void handleMessage(Message msg) { //重写handleMessage方法
//				//TODO
//				//处理消息的业务逻辑
//			}
//		};
//		Looper.loop(); //进入消息循环
//	}
//}