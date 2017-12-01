package com.example.z2min3d;

import static android.opengl.GLES20.*;
import static niming.util.Constants.*;
import niming.util.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import niming.parser.Number3d;
import niming.parser.ParseObj;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class ObjRenderer implements Renderer {
	private static final int BYTES_PER_FLOAT = 4;
	private static final int NUM_PER_VER = 3;
	private static final int GL_TRIANGLES_STRIP = 0;
	
	public ParseObj obj = null;
	private final Context context;
	private FloatBuffer floatBuffer;
	private ByteBuffer indexArray;
	
	private int program;
//	获得一个uniform的位置
	private int uColorLocation;
	private int aPositionLocation;
	
	/**Test*/
	Table table = new Table();
	private final FloatBuffer vertexData;
	
	public ObjRenderer(Context context) {
		this.context = context;

		//==================================================================
		/**Test*/
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
			//分配一个大小为………的本地内存块
			vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
					//告诉字节缓冲区(byte buffer)按照本地字节序(native byte order)组织它的内容
					.order(ByteOrder.nativeOrder())
					//我们不愿直接操作单独的字节，而是希望使用浮点数，因此调用asFloatBuffer()得到一个可以反映底层字节的FloatBuffer类的实例
					.asFloatBuffer();
			//然后调用		vertexData.put(tableVerticesWithTriangles)把数据从Dalvik的内存中复制到本地内存
			vertexData.put(tableVerticesWithTriangles);
	//==================================================================	
		
		
		
		
		
		
		
		
		 obj = new ParseObj(context, R.raw.cube);
	     obj.parser();
        //所有数据都已经读取完毕；
	        
	        
        /*顶点转换成数组（打包成函数）*/
        float[] verArray = new float[obj.verticesNum * NUM_PER_VER];
        int j = 0;
        Iterator iter = obj.vertices.iterator();
        while(iter.hasNext()){
        	Number3d vertex = (Number3d) iter.next();
        	verArray[j++] = vertex.getX();
        	verArray[j++] = vertex.getY();
        	verArray[j++] = vertex.getZ();
        }//拆ArrayList<Number3d> vertices 包
        
        /*把数据复制到本地内存2.4*/
		floatBuffer = ByteBuffer
				.allocateDirect(j * BYTES_PER_FLOAT)//j为数组长度
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(verArray);//分配本地内存floatBuffer,用来存储顶点矩阵数据 
		
		
		
		/*准备索引数据*/
		byte[] indexTemp = new byte[obj.vIndexNum];
		for (int i = 0; i < obj.vIndexNum; i++) {
			indexTemp[i] = (byte)obj.vIndex.get(i);
		}
		indexArray = ByteBuffer.allocateDirect(obj.vIndexNum)
				.put(indexTemp);
		indexArray.position(0);
		

		
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
		/*数据传递OpenGL管线*/
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
			ShaderHelper.validateProgram(program);//输出program是否有效
		}
		glUseProgram(program); //告诉OpenGL在绘制任何东西到屏幕上的时候要使用这里定义的程序。
		
		/**【第四步】获取uniform位置*/
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		/*获取属性位置*/
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		
		/**【第五步】关联属性与顶点数组*/
		/**着色器管线装配(shader plumbing)，将着色器变量与应用程序的数据关联起来*/
//		floatBuffer.position(0);
//		glVertexAttribPointer(
//				aPositionLocation, 
//				POSITION_COMPONENT_COUNT, 
//				GL_FLOAT,
//				false, 
//				0 , 
//				floatBuffer); //3.4.4
//		glEnableVertexAttribArray(aPositionLocation);//让OpenGL指定哪儿寻找它的数据
		/**Test*/
//		table.bindData(aPositionLocation);
		vertexData.position(0);//本地内存中的顶点数组，它的指针位置归0
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
	
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);//Clear the rendering surface.
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
//		gl.glDrawElements(GL_LINE_STRIP, obj.vIndexNum, GL_UNSIGNED_BYTE, indexArray);
		/*
		 * 第一个参数是点的类型，第二个参数是点的个数，第三个是第四个参数的类型，第四个参数是点的存储绘制顺序。
		 */
//		glDrawElements()
		
		
		
		
//		table.draw();
		
		/*桌子边缘*/
		glDrawArrays(GL_TRIANGLES_STRIP, 0, 6);//绘制桌子，
										//参数说明：第一个参数，绘制为三角形；第二个参数，从顶点数组的开头处读顶点；第三个参数是读入的顶点数，6表示绘制2个三角形。
		/*绘制桌面*/
		glDrawArrays(GL_TRIANGLE_FAN, 6, 6);//围绕中心点扇形绘制三角形
		/*绘制分割线*/
		glDrawArrays(GL_LINES, 12, 2);
	}
	
	
	
	
	
	
	
	
	
//		ByteBuffer bb = arr2ByteBuffer(Arrays.toString(obj.vIndex.toArray()).getBytes());//将ArrayList转换为数组
	public static ByteBuffer arr2ByteBuffer(byte[] arr){  
	    //分配字节缓冲区空间,存放顶点坐标  
	    ByteBuffer ibb=ByteBuffer.allocateDirect(arr.length);  
	    //设置顺序（本地数据）  
	    ibb.order(ByteOrder.nativeOrder());  
	    //放置顶点坐标数组  
	    ibb.put(arr);  
	    //定位指针位置,从该位置开始读取顶点数据  
	    ibb.position(0);  
	    return ibb;  
	}  

}
