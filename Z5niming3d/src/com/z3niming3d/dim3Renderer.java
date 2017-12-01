package com.z3niming3d;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static niming.util.Constants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import niming.parser.Number3d;
import niming.parser.ParseObj;
import niming.util.*;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class dim3Renderer implements Renderer {
	private static final int POSITION_COMPONENT_COUNT = 3;
//	private static final int POSITION_COMPONENT_COUNT = 4;
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer vertexData; //顶点坐标
	private final FloatBuffer colorData; //颜色属性
	private ByteBuffer indexArray; //顶点序列
	private float[] verArray;
	private float[] colorArray;
	private final Context context;
	private int program;
	//	获得一个uniform的位置
	private int aColorLocation;
	//获取属性的位置
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;
	
	private static final String A_COLOR = "a_Color";
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	
	/*第五章*/
	private static final String U_MATRIX = "u_Matrix";
	private static final int NUM_PER_VER = 3;
	private final float[] projectionMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	private int uMatrixLocation;
	
	/*第六章*/
	//投影矩阵、平移矩阵、旋转矩阵
	private final float[] modelMatrix = new float[16];

	
	/*ParseObj*/
	private static final String TAG = "Render";
	ParseObj obj = null;
	
	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	public dim3Renderer(Context context) {
		this.context = context;
		Log.v(TAG, "开始啊了");
//		 obj = new ParseObj(context, R.raw.umbrella);
		 obj = new ParseObj(context, R.raw.cube);
	     obj.parser();
        //所有数据都已经读取完毕；
	        
	        
        /*顶点转换成数组（打包成函数）*/
        verArray = new float[obj.verticesNum * NUM_PER_VER];
        int j = 0;
        Iterator iter = obj.vertices.iterator();
        while(iter.hasNext()){
        	Number3d vertex = (Number3d) iter.next();
        	verArray[j++] = vertex.getX();
        	verArray[j++] = vertex.getY();
        	verArray[j++] = vertex.getZ();
        }//拆ArrayList<Number3d> vertices 包
        
        /*把数据复制到本地内存2.4*/
		vertexData = ByteBuffer
				.allocateDirect(j * BYTES_PER_FLOAT)//j为数组长度
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(verArray);//分配本地内存vertexData,用来存储顶点矩阵数据 
		
		
		
//		/*准备顶点索引数据*/
		byte[] indexTemp = new byte[obj.vIndexNum];
		for (int i = 0; i < obj.vIndexNum; i++) {
			indexTemp[i] = (byte)obj.vIndex.get(i);
		}
		indexArray = ByteBuffer.allocateDirect(obj.vIndexNum)
				.put(indexTemp);
		
		/*索引数据成功导入本地内存*/
//		indexArray = ByteBuffer.allocateDirect(obj.vIndex.size())
//				.put(new byte[]{
//						1-1,2-1,4-1,  2-1,4-1,3-1,
//						3-1,4-1,6-1,  4-1,6-1,5-1,
//						5-1,6-1,8-1,  6-1,8-1,7-1,
//						7-1,8-1,2-1,  8-1,2-1,1-1,
//						2-1,8-1,6-1,  8-1,6-1,4-1,
//						7-1,1-1,3-1,  1-1,3-1,5-1
//						
//				});
		indexArray.position(0);
		
		/**把vn当颜色属性*/
		colorArray = new float[obj.normals.size() * NUM_PER_VER];;
		iter = obj.normals.iterator();
		j = 0;
        while(iter.hasNext()){
        	Number3d color = (Number3d) iter.next();
        	colorArray[j++] = color.getX();
        	colorArray[j++] = color.getY();
        	colorArray[j++] = color.getZ();
        }
        
        /*把数据复制到本地内存2.4*/
		colorData = ByteBuffer
				.allocateDirect(j * BYTES_PER_FLOAT)//j为数组长度
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(colorArray);//分配本地内存vertexData,用来存储顶点矩阵数据 
//		Log.v(TAG, "" + obj.vIndexNum);//36
}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
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
//		一旦着色器被链接在一起了，调用glGetAttribLocation()获取属性的位置。
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		vertexData.position(0);//指针位置
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		//寻找顶点数据
		glEnableVertexAttribArray(aPositionLocation);
		
//		获取uniform的位置，并把这个位置存入uColorLocation中。
		aColorLocation = glGetUniformLocation(program, A_COLOR);
		colorData.position(0);
		glVertexAttribPointer(aColorLocation, 3, GL_FLOAT, false, 0, colorData);
		glEnableVertexAttribArray(aColorLocation);
		
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		MatrixHelper.perspectiveM(projectionMatrix, 45f, (float)width/(float)height, 1f, 1000f);
		
		setIdentityM(modelMatrix, 0);//归一化
		translateM(modelMatrix, 0, 0f, 0f, -150f);//平移变换
		rotateM(modelMatrix, 0, 60f, 1f, 0f, 0f);
		rotateM(modelMatrix, 0, 60f, 0f, 1f, 0f);
		//矩阵连乘
		//vertex_clip = ProjectionMatrix * vertex_eye
		//vertex_eye = ModelMatrix * vertex_model
		//vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);
		glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);//把projectionMatrix与着色器uMatrixLocation关联
		glDrawElements(GL_TRIANGLES, obj.vIndexNum, GL_UNSIGNED_BYTE, indexArray);//提示数组越界    obj.vIndexNum
//		glDrawArrays(GL_POINTS, 0, obj.vIndexNum);
//		glDrawArrays(GL_LINES, 0, obj.vIndexNum);
//		glDrawArrays(GL_LINES, 0, 8);
//		drawbox();
	}
    private void drawbox() {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -5f);//平移变换
        rotateM(modelMatrix, 0, - yRotate, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, - xRotate, 0f, 1f, 0f);
        yRotate++;
        xRotate++;
//        setLookAtM(viewMatrix, 0, xRotation, yRotation, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);//立方体会放大、缩小
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
		glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);//把projectionMatrix与着色器uMatrixLocation关联
		glDrawElements(GL_TRIANGLES, obj.vIndexNum, GL_UNSIGNED_BYTE, indexArray);//提示数组越界    obj.vIndexNum
//		glDrawElements(GL_TRIANGLES, 6*6, GL_UNSIGNED_BYTE, indexArray);//提示数组越界    obj.vIndexNum
    }

}
