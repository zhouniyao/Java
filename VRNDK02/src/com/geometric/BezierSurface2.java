package com.geometric;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.example.vrndk01.R;

import niming.util.ShaderHelper;
import niming.util.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;


public class BezierSurface2 {
	Context context;
	int program;// 自定义渲染管线着色器程序id
	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mNormalBuffer;// 顶点法向量坐标数据缓冲
	int vCount = 0;//顶点总数量
    /*管线装配时，各着色器变量在OpenGL内的Location*/
    private int aPositionLocation;
    private int aColorLocation;
    private int aTextureCoordLocation;
    private int uTextureUnitLoaction;
    private int uMVPMatrixLocation;
    private int uColorLocation;
	
    private final int srcCount = 4;
    public final int dstCount = 40;
    
    private Point3D[][] src = new Point3D[srcCount][srcCount];// 由4x4网格组成
    public Point3D[][] dst = new Point3D[dstCount][dstCount];// Bezier曲面点集
    
    {//初始化二维Point3D数组
        for (int i = 0; i < srcCount; i++) {
            for (int j = 0; j < srcCount; j++) {
                src[i][j] = new Point3D();
            }
        }
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
                dst[i][j] = new Point3D();
            }
        }
    }
    public BezierSurface2(Context context) {
    	this.context = context;
    	initps();
    	resultBezierSurface();
    	/*测试顶点缓存区*/
//    	for (int i = 0; i < mVertexBuffer.capacity(); i++) {
//			Log.i("顶点缓存区：", ""+mVertexBuffer.get());
//		}
//    	
    	initShader(R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);;
	}
    /**
     * 初始化src数组元素——3D控制点
     */
    private void initps() {
    	//畸变体
    	src[0][0].setXYZ(-1.5, -1.5, 4.0);src[0][1].setXYZ(-0.5, -2.5, 2.0);src[0][2].setXYZ(0.5, -3.0, -1);src[0][3].setXYZ(1.5, -1.5, 2.0);
    	src[1][0].setXYZ(-2, -0.5, 1.0);src[1][1].setXYZ(-0.5, -0.5, 3.0);src[1][2].setXYZ(0.5, -0.5, 0.0);src[1][3].setXYZ(1.7, -0.5, -1.0);
    	src[2][0].setXYZ(-2, 0.5, 4.0);src[2][1].setXYZ(-0.5, 0.5, 0.0);src[2][2].setXYZ(0.5, 0.5, -2.0);src[2][3].setXYZ(1.8, 0.5, 4.0);
    	src[3][0].setXYZ(-1.5, 1.5, -2.0);src[3][1].setXYZ(-0.5, 3.0, 0.0);src[3][2].setXYZ(0.5, 3.5, 0.0);src[3][3].setXYZ(1.5, 1.5, -1.0);
    	
    	//长方体
//    	src[0][0].setXYZ(-2, -1.5, 4.0);src[0][1].setXYZ(-0.5, -1.5, 2.0);src[0][2].setXYZ(0.5, -1.5, -1);src[0][3].setXYZ(1.5, -1.5, 2.0);
//    	src[1][0].setXYZ(-2, -0.5, 1.0);src[1][1].setXYZ(-0.5, -0.5, 3.0);src[1][2].setXYZ(0.5, -0.5, 0.0);src[1][3].setXYZ(1.5, -0.5, -1.0);
//    	src[2][0].setXYZ(-2, 0.5, 4.0);src[2][1].setXYZ(-0.5, 0.5, 0.0);src[2][2].setXYZ(0.5, 0.5, -2.0);src[2][3].setXYZ(1.5, 0.5, 4.0);
//    	src[3][0].setXYZ(-2, 1.5, -2.0);src[3][1].setXYZ(-0.5, 1.5, 0.0);src[3][2].setXYZ(0.5, 1.5, 0.0);src[3][3].setXYZ(1.5, 1.5, -1.0);
    }
    
    //BezierSurface
    private void resultBezierSurface() {
        
        double mui, muj, bi = 0, bj = 0;
        for (int i = 0; i < dstCount; i++) {
            mui = (i) / (double)(dstCount - 1);
            for (int j = 0; j < dstCount; j++) {
                muj = (j) / (double)(dstCount - 1);
                dst[i][j].x = 0;
                dst[i][j].y = 0;
                dst[i][j].z = 0;
                for (int ki = 0; ki < srcCount; ki++) {
                    bi = bezierBlendFunction(ki,mui,srcCount - 1);
                    for (int kj = 0; kj < srcCount; kj++) {
                        bj = bezierBlendFunction(kj,muj,srcCount - 1);
                        dst[i][j].x += (src[ki][kj].x * bi * bj);
                        dst[i][j].y += (src[ki][kj].y * bi * bj);
                        dst[i][j].z += (src[ki][kj].z * bi * bj);
                    }
                }
            }
        }//End outest for
        vCount = dstCount * dstCount;
        int k = 0;
        float vertices[] = new float[vCount * 3];
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
            	vertices[k++] = dst[i][j].x;
            	vertices[k++] = dst[i][j].y;
            	vertices[k++] = dst[i][j].z;
            }
        }
		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置       
		//创建绘制顶点法向量缓冲[使用顶点数据作为法向量]
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mNormalBuffer.position(0);//设置缓冲区起始位置     
        
    }
    
    private double bezierBlendFunction(int k, double mu, int n) {
        int nn = n, kn = k, nkn = n - k;
        double blend = 1;
        
        while (nn >= 1) {
            blend *= nn;
            nn = nn - 1;
            if (kn > 1) {
                blend /= (double)kn;
                kn--;
            }
            if (nkn > 1) {
                blend /= (double)nkn;
                nkn--;
            }
        }            
        if (k > 0) {
            blend *= Math.pow(mu, (double)k);
        }
        if (n - k > 0) {
            blend *= Math.pow(1 - mu, (double)(n - k)); 
        }
        return blend;
    }
    
    /**
	 * 生成着色器程序，应用程序与着色器变量进行【管线装配】
	 */
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		/**
		 * 【第一步】将资源文件（着色器源代码）读取至 String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
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
		/*uniform常量定位*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_Matrix");
		uColorLocation = glGetUniformLocation(program, "u_Color");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
		
	}
    
	public void drawSelf(float[] MVPMatrix){
		glUseProgram(program);
		// 将最终变换矩阵传入着色器程序
		GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false,
				MVPMatrix, 0);
		// 将顶点位置数据传入渲染管线
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, mVertexBuffer);
		glEnableVertexAttribArray(aPositionLocation);
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);//设置颜色【常量】
		//绘制
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);
	}
}

