package com.niming;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Bezier曲面绘制
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class BezierSurface extends JFrame {

    public BezierSurface() {
        super("Bezier Surfaces");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        add("Center", new CvBezierSurface());
        setSize(800,600);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new BezierSurface();
    }

}

@SuppressWarnings("serial")
class CvBezierSurface extends Canvas {
    private int maxX, maxY;
    private double rWidth = 10.0D;
    private double rHeight = 10.0D;
    private double pixelWidth, pixelHeight;
    
    private int srcCount = 4;
    private int dstCount = 40;
    
    private Point3D[][] src = new Point3D[srcCount][srcCount];// 由4x4网格组成
    private Point3D[][] dst = new Point3D[dstCount][dstCount];
    
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
    /**
     * 初始化src数组元素——3D点
     */
    private void initps() {
    	//用随机值，初始化控制点
//        for (int i = 0; i < srcCount; i++) {
//            for (int j = 0; j < srcCount; j++) {
//                src[i][j].x = random();
//                src[i][j].y = random();
//                src[i][j].z = random();
//            }
//        }   
    	
    	//数组值是错误的
//    	src[0][0].x = 1.0f;src[0][0].y = 1.0f;src[0][0].z = 1.0f; 
//    	src[0][1].x = 2.0f;src[0][1].y = 2.0f;src[0][1].z = 2.0f;
//    	src[0][2].x = 3.0f;src[0][2].y = 3.0f;src[0][2].z = 3.0f;
//    	src[0][3].x = 4.0f;src[0][3].y = 4.0f;src[0][3].z = 4.0f;
//    	
//    	src[1][0].x = 6.0f;src[1][0].y = 6.0f;src[1][0].z = 6.0f; 
//    	src[1][1].x = 6.0f;src[1][1].y = 6.0f;src[1][1].z = 6.0f;
//    	src[1][2].x = 6.0f;src[1][2].y = 6.0f;src[1][2].z = 6.0f;
//    	src[1][3].x = 6.0f;src[1][3].y = 6.0f;src[1][3].z = 6.0f;
//    	
//    	src[2][0].x = 7.0f;src[2][0].y = 7.0f;src[2][0].z = 7.0f; 
//    	src[2][1].x = 7.0f;src[2][1].y = 7.0f;src[2][1].z = 7.0f;
//    	src[2][2].x = 7.0f;src[2][2].y = 7.0f;src[2][2].z = 7.0f;
//    	src[2][3].x = 7.0f;src[2][3].y = 7.0f;src[2][3].z = 7.0f;
//    	
//    	src[3][0].x = 8.0f;src[3][0].y = 8.0f;src[3][0].z = 8.0f; 
//    	src[3][1].x = 8.0f;src[3][1].y = 8.0f;src[3][1].z = 8.0f;
//    	src[3][2].x = 8.0f;src[3][2].y = 8.0f;src[3][2].z = 8.0f;
//    	src[3][3].x = 8.0f;src[3][3].y = 8.0f;src[3][3].z = 8.0f;
    	
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
    
    private double random() {
        return Math.random() * 10;//随机值[0~10]
    }
    
    /**
     * 鼠标监听事件，重绘图形——Bezier曲面
     */
    public CvBezierSurface() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                repaint();//重绘
            }
        });
    }
    
    private int iX(double x) {
        return (int)Math.round(x / pixelWidth);//四舍五入
    }
    
    private int iY(double y) {
        return (int)Math.round(maxY - y / pixelHeight);
    }
    
    private void initgr() {
        Dimension d = getSize();//返回object的宽高
        maxX = d.width - 1;
        maxY = d.height - 1;
        pixelWidth = rWidth / maxX;
        pixelHeight = rHeight / maxY;
    }
    
    public void paint(Graphics g) {
        initgr();
        initps();
        int left = iX(0), right = iX(rWidth),
            bottom = iY(0), top = iY(rHeight);
        g.drawRect(left, top, right - left, bottom - top);
        drawBezierSurface();    
    }
    
    private void drawBezierSurface() {
        
        Graphics g = getGraphics();
        
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
        }
        //绘制结果数组dst中点与点间连线
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
                if (j != dstCount - 1) {
                    g.drawLine(iX(dst[i][j].x), iY(dst[i][j].y), iX(dst[i][j+1].x), iY(dst[i][j+1].y));
                }
                if (i != dstCount - 1) {
                    g.drawLine(iX(dst[i][j].x), iY(dst[i][j].y), iX(dst[i + 1][j].x), iY(dst[i + 1][j].y));
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
class Point3D{
	public double x, y, z;
	public void setXYZ(double x, double y, double z) {
		this.x = x+5;
		this.y = y+5;
		this.z = z+5;
	}
}
