package com.niming.airhockey1;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.util.*;

public class AirHockeyRender implements Renderer {
	private static final int POSITION_COMPONENT_COUNT = 2;
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
	
	/*
	 * 创建一个浮点数数组，这些浮点数表示组成空气曲棍球桌子的顶点的位置；
	 * 本地内存中创建一个缓冲区，称为vertexData，并把这些位置复制到这个缓冲区内。
	 * 在我们告诉OpenGL从这个缓冲区中读取数据前，需要确保它会从开头处开始读取数据，而不是中间或尾。
	 * 每个缓冲区都有一个内部指针，可以通过调用position(int)移动它，
	 * 然后，我们调用glVertexAttribPointer()告诉OpenGL，它可以在缓冲区vertexData中找到a_Position对应的数据。
	 */
	public AirHockeyRender(Context context){
		this.context = context;
		float[] tableVertices = {
			0f,  0f,
			0f, 14f,
			9f, 14f,
			9f,  0f
		};
		float[] tableVerticesWithTriangles = {

					/*
					//Triangle 1
					0f,  0f,
					9f, 14f,
					0f, 14f,
					//Triangle 2
					0f,  0f,
					9f,  0f,
					9f, 14f,
					
					//Line
					0f, 7f,
					9f, 7f,
					
					//Mallets
					4.5f, 2f,
					4.5f, 12f,
					*/
					
			/*OpenGL把屏幕左边映射到[-1,1]区间*/
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
//		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		//存储那个链接ID
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		if(LoggerConfig.ON){
			ShaderHelper.validateProgram(program);
		}
		
		/*3.4 链接工作*/
//		glUseProgram(program)告诉OpenGL在绘制任何东西到屏幕上的时候要使用这里定义的程序。
		glUseProgram(program);
//		获取uniform的位置，并把这个位置存入uColorLocation中。
		uColorLocation = glGetUniformLocation(program, U_COLOR);
//		一旦着色器被链接在一起了，调用glGetAttribLocation()获取属性的位置。
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
//		关联属性与顶点数据的数组
//		下一步告诉OpenGL到哪里找到属性a_Position对应的数据。
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
		vertexData.position(0);//指针位置
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		//寻找顶点数据
		glEnableVertexAttribArray(aPositionLocation);
		/*3.4 链接工作——结束*/
		
	}


	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);
//		/*绘制桌面*/
//		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);//指定颜色
//		glDrawArrays(GL_TRIANGLES, 0, 6);//绘制桌子，参数说明：第一个参数，绘制三角形；第二个参数，从顶点数组的开头处读顶点；第三个参数是读入的顶点数，6表示绘制2个三角形。
//		/*绘制分割线*/
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_LINES, 6, 2);
//		
//		/*绘制木槌*/
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 1.0f, 1.0f);//Draw the first mallet blue.
//		glDrawArrays(GL_POINTS, 8, 1);
//		
//		//Draw the second mallet red;
//		glUniform4f(uColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_POINTS, 9, 1);
//		//Draw the ice hockey
//		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//		glDrawArrays(GL_POINTS, 10, 1);
		/*桌子边缘*/
		glUniform4f(uColorLocation, 0.5f, 0.5f, 1.0f, 1.0f);//指定颜色
		glDrawArrays(GL_TRIANGLES, 0, 6);//绘制桌子，参数说明：第一个参数，绘制三角形；第二个参数，从顶点数组的开头处读顶点；第三个参数是读入的顶点数，6表示绘制2个三角形。
		/*绘制桌面*/
		glUniform4f(uColorLocation, 0.75f, 0.75f, 0.75f, 1.0f);//指定颜色
		glDrawArrays(GL_TRIANGLES, 6, 6);//绘制桌子，参数说明：第一个参数，绘制三角形；第二个参数，从顶点数组的开头处读顶点；第三个参数是读入的顶点数，6表示绘制2个三角形。
		/*绘制分割线*/
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 12, 2);
		
		/*绘制木槌*/
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


}
