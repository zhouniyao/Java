package niming.virsualreality02;


import static android.opengl.GLES20.*;

import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.geometric.Number3d;
import com.geometric.Uv;

import niming.core.MyActivity;
import niming.parser.ObjParser;
import niming.parser.ParseObjectData;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;
import niming.util.Utils;


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
 * 立方体双目 [dragon]
 *
 */
public class womenOBJ extends MyActivity{
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
        mGLSurfaceView.setRenderer(new ScreenRenderer(womenOBJ.this));	//画cube
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
	private class ScreenRenderer implements GLSurfaceView.Renderer{
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
	    private FloatBuffer objPositions;
	    private FloatBuffer objNormalsss;
	    private FloatBuffer objTextureCoodinates;
	    private ShortBuffer objVerticesSeq;
	    
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
	    private static final String U_COLOR = "u_Color";
	    
	    /*管线装配时，各着色器变量在OpenGL内的Location*/
	    private int aPositionLocation;
	    private int aColorLocation;
	    private int aTextureCoordLocation;
	    private int uTextureUnitLoaction;
	    private int uMVPMatrixLocation;
	    private int uColorLocation;
	    
	    /*整个屏幕的宽高px值*/
	    private int screenWidth;
	    private int screenHeight;
	    float aspectRatio;//屏幕宽高比，宽/高 > 1
	    /*纹理ID*/
	    int texture1;
	    int texture2;
	    
	    final ObjParser obj;
		
		public ScreenRenderer(Context context) {//添加数据通道,
			this.context = context;
			
			/*OBJ文件导入*/
			obj = new ObjParser(context, "niming.virsualreality02:raw/women_obj", true);
			obj.parse();//解析OBJ文件
			ParseObjectData car = obj.getObjData();
			
			//ArrayList-->float[]
			//===================================顶点数据=========================================================================
			Number3d[] vertices = new Number3d[obj.verticesNum];
			car.vertices.toArray(vertices);
			float[] positions = new float[obj.verticesNum * 3];
			for (int i = 0, j = 0; i < obj.verticesNum; i++) {
				float[] tmp = Utils.Number3dTofloat(vertices[i]);
				positions[j++] = tmp[0];
				positions[j++] = tmp[1];
				positions[j++] = tmp[2];
			}
			
			/*测试顶点位置*/
//			for (int i = 0; i < positions.length; ) {
//				Log.i("顶点数组序列：", positions[i++]+" "+positions[i++]+" "+positions[i++]);//每行输出一个顶点xyz
//			}
			
			//===================================纹理数据=========================================================================
			Uv[] textureTmp = new Uv[obj.UVCoordsNum];
			car.texCoords.toArray(textureTmp);
			float[] textureCoords = new float[obj.UVCoordsNum * 2];
			for (int i = 0, j = 0; i < obj.UVCoordsNum; i++) {
				float[] tmp = Utils.UvTofloat(textureTmp[i]);
				textureCoords[j++] = tmp[0];
				textureCoords[j++] = tmp[1];
			}
//			
//			/*测试纹理坐标*/
//			for (int i = 0; i < textureCoords.length; ) {
//				Log.i("纹理数组序列：", textureCoords[i++]+" "+textureCoords[i++]);//每行输出一个纹理UV
//			}
			
			//===================================法向量数据=========================================================================
			Number3d[] normalTmp = new Number3d[obj.normalsNum];
			car.normals.toArray(normalTmp);
			float[] normals = new float[obj.normalsNum * 3];
			for (int i = 0, j = 0; i < obj.normalsNum; i++) {
				float[] tmp = Utils.Number3dTofloat(normalTmp[i]);
				normals[j++] = tmp[0];
				normals[j++] = tmp[1];
				normals[j++] = tmp[2];
			}
			//===================================face数据=========================================================================
			short[] faceVertices = new short[obj.numFaces * 3];
			for (int i = 0; i < obj.numFaces * 3; i++) {
				faceVertices[i] = car.faces.vSeq.get(i);
			}
		
//			Log.i("face的顶点数组大小", car.faces.vSeq.size()+"");
			/*测试face的顶点序列*/
//			for (int i = 0; i < faceVertices.length; ) {
//				Log.i("face的顶点序列", faceVertices[i++] + " " + faceVertices[i++] + " " + faceVertices[i++]);
//			}
			
	        // Initialize the buffers.
	        objPositions = ByteBuffer.allocateDirect(positions.length * BYTES_PER_FLOAT)
	        .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        objPositions.put(positions).position(0);

	        objNormalsss = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT)
	        .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        objNormalsss.put(normals).position(0);

	        objTextureCoodinates = ByteBuffer.allocateDirect(textureCoords.length * BYTES_PER_FLOAT)
	        .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        objTextureCoodinates.put(textureCoords).position(0);
	        //顶点序列，ShortBuffer
	        objVerticesSeq = ByteBuffer.allocateDirect(faceVertices.length * 2)
	        				.order(ByteOrder.nativeOrder()).asShortBuffer();
	        objVerticesSeq.put(faceVertices).position(0);
	        
	        
	
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
					.readTextFileFromResource(context, R.raw.simple_vertex_shader);
			String fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_fragment_shader);
//			String vertexShaderSource   = TextResourceReader
//					.readTextFileFromResource(context, R.raw.texture_vertex_shader);
//			String fragmentShaderSource = TextResourceReader
//					.readTextFileFromResource(context, R.raw.texture_fragment_shader);
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
//			uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
			uMVPMatrixLocation = glGetUniformLocation(program, "u_Matrix");//为women更改
			uColorLocation = glGetUniformLocation(program, "u_Color");//为women更改
			uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
			
			glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, objPositions);
			glEnableVertexAttribArray(aPositionLocation);
			glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, objNormalsss);
			glEnableVertexAttribArray(aColorLocation);
			glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, objTextureCoodinates);
			glEnableVertexAttribArray(aTextureCoordLocation);
			
			
			/*加载纹理图片*/
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
			        /*平移、放缩、旋转设置*/
			setIdentityM(modelMatrix, 0);//归一化
			translateM(modelMatrix, 0, 0f, -13f, -10f);//沿z轴平移-3，向y负半轴移动-9f【针对women】
			scaleM(modelMatrix, 0, 0.12f, 0.12f, 0.12f);//women太大啦
			rotateM(modelMatrix, 0, 30, 0f, 1f, 0f);
		
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
			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
	        //视点位置
	        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        /*平移、放缩、旋转设置*/
//			setIdentityM(modelMatrix, 0);//归一化
//			translateM(modelMatrix, 0, 0f, -13f, -10f);//沿z轴平移-3，向y负半轴移动-9f【针对women】
//			scaleM(modelMatrix, 0, 0.12f, 0.12f, 0.12f);//women太大啦
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//绕x轴旋转angleInDegrees
//			rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//绕y轴旋转angleInDegrees
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 1f, 0f);//绕x,y轴旋转angleInDegrees
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	    	//给图像添加颜色【固定的】
	        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
	        drawCube();
	 /**************************************第二个物体【右边】************************************************/      
	        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
	    	//透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
//			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
	        //重设变换矩阵
	        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			setIdentityM(modelMatrix, 0);//归一化
//			translateM(modelMatrix, 0, 0f, 0f, -3f);//沿z轴平移-3
//			scaleM(modelMatrix, 0, 1f, 1f, 1f);
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//绕x轴旋转-60°
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	        //给图像添加颜色【固定的】
	        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
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
//	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
//	    	GLES20.glDrawArrays(GLES20.GL_POINTS, 0, obj.verticesNum);
	    	/*顶点序列绘制*/
//	    	GLES20.glDrawElements(GL_POINTS, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//点
	    	GLES20.glDrawElements(GL_LINES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq); //网格线
//	    	GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//三角面
	    }
		
	}
}



