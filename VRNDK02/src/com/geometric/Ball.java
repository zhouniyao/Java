package com.geometric;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;


import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.example.vrndk01.R;

import niming.util.ShaderHelper;
import niming.util.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;

//修改课本后的球
public class Ball {
	Context context;
	public static final float UNIT_SIZE = 1f;
	int mProgram;// 自定义渲染管线着色器程序id
//	int muMVPMatrixHandle;// 总变换矩阵引用
//    int muMMatrixHandle;//位置、旋转变换矩阵引用
//	int maPositionHandle; // 顶点位置属性引用
//	int muRHandle;// 球的半径属性引用   
//    int maNormalHandle; //顶点法向量属性引用
//    int maLightLocationHandle;//光源位置属性引用
    
    
	String mVertexShader;// 顶点着色器
	String mFragmentShader;// 片元着色器

	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
	int vCount = 0;
	float yAngle = 0;// 绕y轴旋转的角度
	float xAngle = 0;// 绕x轴旋转的角度
	float zAngle = 0;// 绕z轴旋转的角度
	float r = 0.8f;
	private int aPositionLocation;
	private int aTextureCoordLocation;
	private int aColorLocation;
	private int uMVPMatrixLocation;
	private int uColorLocation;
	private int uTextureUnitLoaction;
	public Ball(Context context) {
		this.context = context;
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
//		initShader(R.raw.simple_vertex_shader2, R.raw.simple_fragment_shader2);
		initShader(R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
	}

	// 初始化顶点坐标数据的方法
	public void initVertexData() {
		// 顶点坐标数据的初始化================begin============================
		ArrayList<Float> alVertix = new ArrayList<Float>();// 存放顶点坐标的ArrayList
		final int angleSpan = 10;// 将球进行单位切分的角度
		for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan)// 垂直方向angleSpan度一份
		{
			for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan)// 水平方向angleSpan度一份
			{// 纵向横向各到一个角度后计算对应的此点在球面上的坐标
				float x0 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
						.toRadians(hAngle)));
				float y0 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
						.toRadians(hAngle)));
				float z0 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle)));

				float x1 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
						.toRadians(hAngle + angleSpan)));
				float y1 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
						.toRadians(hAngle + angleSpan)));
				float z1 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle)));

				float x2 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.cos(Math.toRadians(hAngle + angleSpan)));
				float y2 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.sin(Math.toRadians(hAngle + angleSpan)));
				float z2 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle + angleSpan)));

				float x3 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.cos(Math.toRadians(hAngle)));
				float y3 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.sin(Math.toRadians(hAngle)));
				float z3 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle + angleSpan)));

				// 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
				alVertix.add(x1);
				alVertix.add(y1);
				alVertix.add(z1);
				alVertix.add(x3);
				alVertix.add(y3);
				alVertix.add(z3);
				alVertix.add(x0);
				alVertix.add(y0);
				alVertix.add(z0);

				alVertix.add(x1);
				alVertix.add(y1);
				alVertix.add(z1);
				alVertix.add(x2);
				alVertix.add(y2);
				alVertix.add(z2);
				alVertix.add(x3);
				alVertix.add(y3);
				alVertix.add(z3);
			}
		}
		vCount = alVertix.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

		// 将alVertix中的坐标值转存到一个float数组中
		float vertices[] = new float[vCount * 3];
		for (int i = 0; i < alVertix.size(); i++) {
			vertices[i] = alVertix.get(i);
		}

		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		
		//创建绘制顶点法向量缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mNormalBuffer.position(0);//设置缓冲区起始位置     
	}

	// 初始化shader
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		// 加载顶点着色器的脚本内容
		mVertexShader = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		// 加载片元着色器的脚本内容
		mFragmentShader = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
		
		int vertexShader = ShaderHelper.compileVertexShader(mVertexShader);
		int fragmentShader = ShaderHelper.compileFragmentShader(mFragmentShader);
		
		// 基于顶点着色器与片元着色器创建程序
		mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
        
        
		glUseProgram(mProgram);
		/*attribute变量定位*/
		aPositionLocation = glGetAttribLocation(mProgram, "a_Position");
		aColorLocation = glGetAttribLocation(mProgram, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(mProgram, "a_TexCoordinate");
		/*uniform常量定位*/
		uMVPMatrixLocation = glGetUniformLocation(mProgram, "u_Matrix");
		uColorLocation = glGetUniformLocation(mProgram, "u_Color");
		uTextureUnitLoaction = glGetUniformLocation(mProgram, "u_TextureUnit");
	}

	public void drawSelf(float[] MVPMatrix) {		
		// 制定使用某套着色器程序
		GLES20.glUseProgram(mProgram);
		// 将最终变换矩阵传入着色器程序
		// 将最终变换矩阵传入着色器程序
		GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false,
				MVPMatrix, 0);        
		// 将顶点位置数据传入渲染管线
		GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
//        //将顶点法向量数据传入渲染管线
//		GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false,
//				3 * 4, mNormalBuffer);
		// 启用顶点位置数据
		GLES20.glEnableVertexAttribArray(aPositionLocation); 
//        GLES20.glEnableVertexAttribArray(maNormalHandle);// 启用顶点法向量数据
		
//		glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);//设置颜色【常量】
		// 绘制球		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
}
