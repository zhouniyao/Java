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
    
onSurfaceCreated(GL10 gl, EGLConfig config)方法在Surface被创建的时候调用。但在实践中，设备被唤醒或者用户从其他activity切换回来时，这个方法也可能被调用。
onSurfaceChanged(GL10 gl, int width, int height)方法在Surface尺寸变化时调用，在横竖屏切换时，Surface尺寸都会变化。
onDrawFrame(GL10 gl)方法在绘制一帧时调用。在这个方法中，一定要绘制一些东西，即使只是清空屏幕，因为在这个方法返回后，渲染缓冲区会被交换并显示到屏幕上，如果什么都没画，可能看到糟糕的闪烁效果。
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
	//	获得一个uniform的位置
	private static final String U_COLOR = "u_Color";
	private int uColorLocation;
	//获取属性的位置
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;
	
	private static final String A_COLOR = "a_Color";
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private int aColorLocation;
	
	/*第五章*/
	private static final String U_MATRIX = "u_Matrix";
	private final float[] projectionMatrix = new float[16];
	private int uMatrixLocation;
	
	/*第六章*/
	//投影矩阵、平移矩阵、旋转矩阵
	private final float[] modelMatrix = new float[16];
	
	
	/*
	 * 第三章内容，搭建游戏框架
	 * 创建一个浮点数数组，这些浮点数表示组成空气曲棍球桌子的顶点的位置；
	 * 本地内存中创建一个缓冲区，称为vertexData，并把这些位置复制到这个缓冲区内。
	 * 在我们告诉OpenGL从这个缓冲区中读取数据前，需要确保它会从开头处开始读取数据，而不是中间或尾。
	 * 每个缓冲区都有一个内部指针，可以通过调用position(int)移动它，
	 * 然后，我们调用glVertexAttribPointer()告诉OpenGL，它可以在缓冲区vertexData中找到a_Position对应的数据。
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
            -0.5f, -0.8f, 0.7f, 0.7f, 0f,        //平滑着黄色    
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
	/**
	 * 把着色器源代码从文件中读出来
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
		 * 【第一步】将资源文件（着色器源代码）读取至 String
		 */
		String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		/**
		 * 【第二步】编译着色器源代码，返回OpenGL生成的ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		/**
		 * 【第三步】一个OpenGL程序就是把一个顶点着色器和一个片段着色器链接在一起变成单个对象，顶点着色器和片段着色器总是一起工作的。 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		if(LoggerConfig.ON){
			ShaderHelper.validateProgram(program);
		}
		
/* 以上介绍了，如何使用属性数组定义一个物体的结构，
 * 也学习了创建着色器、加载并编译它们，以及把它们链接起来，形成一个OpenGL程序。 
 */
		
		/*3.4 链接工作*/
//		glUseProgram(program)告诉OpenGL在绘制任何东西到屏幕上的时候要使用这里定义的程序。
		glUseProgram(program);

//		uColorLocation = glGetUniformLocation(program, U_COLOR);//获取uniform的位置，并把这个位置存入uColorLocation中。
		/**
		 * 【第四步】Get获取属性位置，更新颜色属性
		 */
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		
//		一旦着色器被链接在一起了，调用glGetAttribLocation()获取属性的位置。
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		
	/**
	 * 【第五步】
	 * 【第五步】顶点数据与着色器属性变量bind
	 * 下一步告诉OpenGL到哪里找到属性a_Position对应的数据。 
	 */
		
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
		vertexData.position(0);//本地内存中的顶点数组，它的指针位置归0
//		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		/*OpenGL从缓存区（vertexData）中读取数据*/
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
		
		/**着色器管线装配(shader plumbing)，将着色器变量与应用程序的数据关联起来*/
		vertexData.position(POSITION_COMPONENT_COUNT);
		glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
		glEnableVertexAttribArray(aColorLocation);
		/*3.4 链接工作——结束*/
		
		/*第五章*/
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
//		/*桌面放大*/
//		if(width > height){
//			//Landscape
//			orthoM(projectionMatrix, 0, -aspectRatio/1.5f, aspectRatio/1.5f, -1f/1.5f, 1f/1.5f, -1f, 1f);//桌面放大
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
//		/*桌面触摸后平移*/
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
			/*功能：用45°的视野创建一个透视投影。这个视椎体从z为-1开始，到z为-10的位置结束*/
		}
//		orthoM(projectionMatrix, 0, -(float)height/(float)width, (float)height/(float)width, -1, 1, 1f, 10f);
		//视椎体平移
		setIdentityM(modelMatrix, 0);//归一化
		translateM(modelMatrix, 0, 0f, 0f, -3f);//沿z轴平移-3
		rotateM(modelMatrix, 0, -80f, 1f, 0f, 0f);//绕x轴旋转-60°
		//矩阵连乘
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
	 * 几乎所有的display()完成类似操作
	 * 1)调用glClear()来清除窗口内容；
	 * 2)调用OpenGL命令来渲染对象；
	 * 3)将最终图像输出到屏幕。
	 */
	public void onDrawFrame(GL10 gl) {
		// Clear the rendering surface.
		glViewport(0, 0, 500, 800);
		glClear(GL_COLOR_BUFFER_BIT);
		/*第五章*/
		glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//把projectionMatrix与着色器uMatrixLocation关联
		/*第四章*/
		/*桌子边缘*/
		glDrawArrays(GL_TRIANGLES, 0, 6);//绘制桌子，
										//参数说明：第一个参数，绘制为三角形；第二个参数，从顶点数组的开头处读顶点；第三个参数是读入的顶点数，6表示绘制2个三角形。
		/*绘制桌面*/
		glDrawArrays(GL_TRIANGLE_FAN, 6, 6);//围绕中心点扇形绘制三角形
		/*绘制分割线*/
		glDrawArrays(GL_LINES, 12, 2);
		
		/*绘制木槌*/
		glDrawArrays(GL_POINTS, 14, 1);
		//Draw the second mallet red;
		glDrawArrays(GL_POINTS, 15, 1);
		
		
		
		
		
		glViewport(500, 0, 500, 800);
		/*桌子边缘*/
		glDrawArrays(GL_TRIANGLES, 0, 6);//绘制桌子，
										//参数说明：第一个参数，绘制为三角形；第二个参数，从顶点数组的开头处读顶点；第三个参数是读入的顶点数，6表示绘制2个三角形。
		/*绘制桌面*/
		glDrawArrays(GL_TRIANGLE_FAN, 6, 6);//围绕中心点扇形绘制三角形
		/*绘制分割线*/
		glDrawArrays(GL_LINES, 12, 2);
		
		/*绘制木槌*/
		glDrawArrays(GL_POINTS, 14, 1);
		//Draw the second mallet red;
		glDrawArrays(GL_POINTS, 15, 1);
		
		//Draw the ice hockey
//		glDrawArrays(GL_POINTS, 16, 1);
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);		
	}


}
