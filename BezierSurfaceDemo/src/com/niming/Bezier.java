package com.niming;

import java.nio.ByteBuffer;

import static com.niming.Constant.*;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;
import android.util.Log;

//立方体
public class Bezier {
	int mProgram;// 自定义渲染管线着色器程序id
	int muMVPMatrixHandle;// 总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵引用
	int muRHandle;// 立方体的半径属性引用   
	int maPositionHandle; // 顶点位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    int maCameraHandle; //摄像机位置属性引用 
    
    
	String mVertexShader;// 顶点着色器
	String mFragmentShader;// 片元着色器

	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
	int vCount = 0;
	float yAngle = 0;// 绕y轴旋转的角度
	float xAngle = 0;// 绕x轴旋转的角度
	float zAngle = 0;// 绕z轴旋转的角度
	float r = 1;
	
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
	
	public Bezier(MySurfaceView mv) {
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader(mv);
	}

	// 初始化顶点坐标数据的方法
	public void initVertexData() {

    	//顶点坐标数据的初始化================begin============================
		//畸变体
//    	src[0][0].setXYZ(-1.5, -1.5, 4.0);src[0][1].setXYZ(-0.5, -1.8, 2.0);src[0][2].setXYZ(0.5, -2.0, -1);src[0][3].setXYZ(1.5, -1.5, 2.0);
//    	src[1][0].setXYZ(-2, -0.5, 1.0);src[1][1].setXYZ(-0.5, -0.5, 3.0);src[1][2].setXYZ(0.5, -0.5, 0.0);src[1][3].setXYZ(1.7, -0.5, -1.0);
//    	src[2][0].setXYZ(-2, 0.5, 4.0);src[2][1].setXYZ(-0.5, 0.5, 0.0);src[2][2].setXYZ(0.5, 0.5, -2.0);src[2][3].setXYZ(1.8, 0.5, 4.0);
//    	src[3][0].setXYZ(-1.5, 1.5, -2.0);src[3][1].setXYZ(-0.5, 2.0, 0.0);src[3][2].setXYZ(0.5, 2.5, 0.0);src[3][3].setXYZ(1.5, 1.5, -1.0);
    	
    	//长方体
    	src[0][0].setXYZ(-1, -1.0, 0.0);src[0][1].setXYZ(-0.5, -1.0, 0.0);src[0][2].setXYZ(0.5, -1.0, 0.0);src[0][3].setXYZ(1.0, -1.0, 0.0);
    	src[1][0].setXYZ(-1, -0.3, 0.0);src[1][1].setXYZ(-0.5, -0.3, 0.0);src[1][2].setXYZ(0.5, -0.3, 0.0);src[1][3].setXYZ(1.0, -0.3, 0.0);
    	src[2][0].setXYZ(-1, 0.4, 0.0);src[2][1].setXYZ(-0.5, 0.4, 0.0);src[2][2].setXYZ(0.5, 0.4, 0.0);src[2][3].setXYZ(1.0, 0.4, 0.0);
    	src[3][0].setXYZ(-1, 1.0, 0.0);src[3][1].setXYZ(-0.5, 1.0, 0.0);src[3][2].setXYZ(0.5, 1.0, 0.0);src[3][3].setXYZ(1.0, 1.0, 0.0);
    	resultOfBezierSurface();
    	
    	
    	////////////////////////////////////////////////
    }
    
    //BezierSurface
    private void resultOfBezierSurface() {
        
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
        ArrayList<Float> v = new ArrayList<Float>();
        int k = 0;
        //将dis[][]--->vertices[]
        
//        for (int i = 0; i < dstCount; i++) {
//            for (int j = 0; j < dstCount; j++) {
////            	vertices[k++] = dst[i][j].x;
////            	vertices[k++] = dst[i][j].y;
////            	vertices[k++] = dst[i][j].z;
//            	vertices[k++] = dst[i][j].x/1.3f - 3.8f;
//            	vertices[k++] = dst[i][j].y/1.3f - 3.9f;
//            	vertices[k++] = dst[i][j].z/ - 10f;
//            	
////            	Log.i("曲线点:", "("+dst[i][j].x+","+dst[i][j].y+","+dst[i][j].z+")");
//            }
//        }
        //三角形Face
        //绘制结果数组dst中点与点间连线
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
                if(i!=dstCount-1 && j!=dstCount-1){
	//            	vertices[k++] = dst[i][j].x;  vertices[k++] = dst[i][j].y; vertices[k++] = dst[i][j].z;
	//            	vertices[k++] = dst[i+1][j].x;  vertices[k++] = dst[i+1][j].y; vertices[k++] = dst[i+1][j].z;
	//            	vertices[k++] = dst[i][j+1].x;  vertices[k++] = dst[i][j+1].y; vertices[k++] = dst[i][j+1].z;
                	v.add(dst[i][j].x/1 -5f);  v.add(dst[i][j].y/1 -5f);   v.add(dst[i][j].z-5f);
                	v.add(dst[i+1][j].x/1 -5f);v.add(dst[i+1][j].y/1 -5f); v.add(dst[i+1][j].z-5f);
                	v.add(dst[i][j+1].x/1 -5f);v.add(dst[i][j+1].y/1 -5f); v.add(dst[i][j+1].z-5f);
                	
                	v.add(dst[i+1][j].x/1 -5f);v.add(dst[i+1][j].y/1 -5f); v.add(dst[i+1][j].z-5f);
                	v.add(dst[i+1][j+1].x/1 -5f);v.add(dst[i+1][j+1].y/1 -5f); v.add(dst[i+1][j+1].z-5f);
                	v.add(dst[i][j+1].x/1 -5f);v.add(dst[i][j+1].y/1 -5f); v.add(dst[i][j+1].z-5f);
                	
//                	Log.i("曲线面1(x,y,z):", "("+(dst[i][j].x-5f)+","+(dst[i][j].y-5f)+","+(dst[i][j].z-5f)+")"+ " " + "("+(dst[i+1][j].x-5f)+","+(dst[i+1][j].y-5f)+","+(dst[i+1][j].z-5f)+")" + " " + "("+(dst[i][j+1].x-5f)+","+(dst[i][j+1].y-5f)+","+(dst[i][j+1].z-5f)+")");
//                	Log.i("曲线面2(x,y,z):", "("+(dst[i][j].x-5f)+","+(dst[i][j].y-5f)+","+(dst[i][j].z-5f)+")"+ " " + "("+(dst[i+1][j+1].x-5f)+","+(dst[i+1][j+1].y-5f)+","+(dst[i+1][j+1].z-5f)+")" + " " + "("+(dst[i][j+1].x-5f)+","+(dst[i][j+1].y-5f)+","+(dst[i][j+1].z-5f)+")");
                	
                }
            }
        }//End for
        vCount = v.size()/3;
        Float vertices[] = new Float[v.size()];
        float ver[] = new float[v.size()]; 
        v.toArray(vertices);
        for (int i = 0; i < v.size(); i++) {
			ver[i] = vertices[i];
//			Log.i("点", ""+ver[i]);
		}
        
		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
		mVertexBuffer.put(ver);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置       
		
		//创建绘制顶点法向量缓冲[使用顶点数据作为法向量]
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(ver);//向缓冲区中放入顶点坐标数据
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
	// 初始化shader
	public void initShader(MySurfaceView mv) {
		// 加载顶点着色器的脚本内容
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh",
				mv.getResources());
		// 加载片元着色器的脚本内容
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_tmp.sh",
				mv.getResources());
		// 基于顶点着色器与片元着色器创建程序
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		// 获取程序中顶点位置属性引用
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		// 获取程序中总变换矩阵引用
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
		// 获取程序中立方体半径引用
		muRHandle = GLES20.glGetUniformLocation(mProgram, "uR");
        //获取程序中顶点法向量属性引用  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
	}

	public void drawSelf() {		
    	MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
    	MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
    	MatrixState.rotate(zAngle, 0, 0, 1);//绕Z轴转动
		// 制定使用某套着色器程序
		GLES20.glUseProgram(mProgram);
		// 将最终变换矩阵传入着色器程序
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0); 
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
		// 将半径尺寸传入着色器程序
		GLES20.glUniform1f(muRHandle, r * UNIT_SIZE);  
        //将光源位置传入着色器程序   
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入着色器程序   
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        
		// 将顶点位置数据传入渲染管线
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
        //将顶点法向量数据传入渲染管线
		GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false,
				3 * 4, mNormalBuffer);
		// 启用顶点位置数据
		GLES20.glEnableVertexAttribArray(maPositionHandle); 
        GLES20.glEnableVertexAttribArray(maNormalHandle);// 启用顶点法向量数据
		// 绘制	
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
}
