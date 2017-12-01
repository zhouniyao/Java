package com.z3niming3d;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import niming.util.*;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class dim3Renderer implements Renderer {
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
	private int aColorLoaction;
	
	/*第五章*/
	private static final String U_MATRIX = "u_Matrix";
	private static final String TAG = "Render";
	private final float[] projectionMatrix = new float[16];
	private int uMatrixLocation;
	
	/*第六章*/
	//投影矩阵、平移矩阵、旋转矩阵
	private final float[] modelMatrix = new float[16];

	public dim3Renderer(Context context) {
		this.context = context;
		
		float[] tableVerticesWithTriangles = {
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
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		//存储那个链接ID
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
//		if(LoggerConfig.ON){
//			ShaderHelper.validateProgram(program);
//		}
		glUseProgram(program);
//		获取uniform的位置，并把这个位置存入uColorLocation中。
		uColorLocation = glGetUniformLocation(program, U_COLOR);
//		一旦着色器被链接在一起了，调用glGetAttribLocation()获取属性的位置。
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		vertexData.position(0);//指针位置
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		//寻找顶点数据
		glEnableVertexAttribArray(aPositionLocation);
//		Log.v(TAG, "都结束了");
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);
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
