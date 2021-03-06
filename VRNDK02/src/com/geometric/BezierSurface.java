package com.geometric;

import java.util.ArrayList;

import android.graphics.PointF;


public class BezierSurface {

    private final int srcCount = 4;
    public final int dstCount = 40;
    
    private Point3D[][] src = new Point3D[srcCount][srcCount];// 由4x4网格组成
    public Point3D[][] dst = new Point3D[dstCount][dstCount];// Bezier曲面点集
    public ArrayList<TowPoint> lineArrayList = new ArrayList<TowPoint>();//点与点间连线
    
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
    public BezierSurface() {
    	initps();
    	resultBezierSurface();
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
        //Line Message
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
                if (j != dstCount - 1) {
                    lineArrayList.add(new TowPoint(
                    		new PointF(dst[i][j].x, dst[i][j].y), new PointF(dst[i][j+1].x, dst[i][j+1].y)
                    		));
                }
                if (i != dstCount - 1) {
                    lineArrayList.add(new TowPoint(
                    		new PointF(dst[i][j].x, dst[i][j].y), new PointF(dst[i + 1][j].x, dst[i + 1][j].y)
                    		));
                }
            }
        }
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
}

class TowPoint{
	public PointF start;
	public PointF end;
	public TowPoint(PointF start, PointF end){
		this.start = start;
		this.end = end;
	}
}