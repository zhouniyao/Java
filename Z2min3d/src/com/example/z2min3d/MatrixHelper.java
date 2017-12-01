package com.example.z2min3d;

public class MatrixHelper {
	/**
	 * 	[透视投影矩阵]自定义的，稍微不同于OpenGL的API
	 * 	┌										 ┐
	 * 	|a/aspect	0		0			0		 |
	 * 	|	0		a		0			0		 |
	 * 	|	0		0  -(f+n)/(f-n)	  -2f*n/(f-n)|
	 * 	|	0		0		-1			0		 |
	 *  └									 	 ┘
	 * @param m : MatrixArray
	 * @param yFovInDegrees : 视角
	 * @param aspect : Width/Height 
	 * @param n : 近处平面距离
	 * @param f : 远处平面距离
	 */
//	public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f){
//		//计算焦距a
//		final float angleInRadians = (float) (yFovInDegrees*Math.PI / 180.0);//角度值转换为弧度制
//		final float a = (float) (1.0 / Math.tan(angleInRadians / 2));
//		
//		//矩阵m的值
//		for (int i = 0; i < 16; i++) {
//			m[i] = 0;
//		}
//		m[0] = a/aspect;
//		m[5] = a;
//		m[10] = -(f+n)/(f-n);
//		m[11] = -1;
//		m[14] = -(2f*n)/(f-n);
//	}
	  public static void perspectiveM(float[] m, float yFovInDegrees, float aspect,
		        float n, float f) {
		        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
				
		        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
		        m[0] = a / aspect;
		        m[1] = 0f;
		        m[2] = 0f;
		        m[3] = 0f;

		        m[4] = 0f;
		        m[5] = a;
		        m[6] = 0f;
		        m[7] = 0f;

		        m[8] = 0f;
		        m[9] = 0f;
		        m[10] = -((f + n) / (f - n));
		        m[11] = -1f;
		        
		        m[12] = 0f;
		        m[13] = 0f;
		        m[14] = -((2f * f * n) / (f - n));
		        m[15] = 0f;        
		    }
}
