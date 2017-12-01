package com.particles.android.objects;

import com.paticles.android.data.IndexBuffer;
import com.paticles.android.data.VertexBuffer;
import com.paticles.android.data.VertexBuffer.*;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 每个顶点都有一个基于其所在图像中的位置和一个基于像素亮度的高度。
 * 一旦所有的顶点都被加载进来，就可以使用索引缓冲区把它们组成OpenGL能绘制的三角形。
 */
public class HeightMap {
    
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;//GPU顶点缓存
    private final IndexBuffer indexBuffer;//GPU索引缓存
    
    public HeightMap(Bitmap bitmap) {
        
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        
        if(width * height > 65536){
            throw new RuntimeException("Heightmap is too large for the index buffer.");
        }
        
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));/*把一个Bitmap对象传递进去，把数据加载进一个顶点缓冲区，并为那些顶点创建一个索引缓冲区*/
        indexBuffer = new IndexBuffer(createIndexData());
    }
    /**
     * 计算需要多少个索引元素
     */
    private int calculateNumElements() {
        /* 
         * 高度图中，每4个顶点构成的组，绘制2个三角形，且每个三角形有3个索引，总共6个索引
         * 通过用 (width - 1) * (height - 1) 计算出我们需要多少组。
         */
        return (width - 1) * (height - 1) * 2 * 3;
    }

    private float[] loadBitmapData(Bitmap bitmap){
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);/*提取像素至一维数组*/
        bitmap.recycle();
        
        final float[] heightmapVertices = 
            new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;
        
        /*For将位图像素转换为高度图*/
        for (int row = 0; row < height ; row++) {
            for (int col = 0; col < width; col++) {  //依照图像内存布局，一行行读取
                //像素偏移值 = 当前行*高度 + 当前列
                final float xPosition = ((float)col / (float)(width - 1)) - 0.5f;
                final float yPosition = 
                    /*一个像素0对应高度0，而一个像素值255对应高度1.*/
                    (float)Color.red(pixels[(row * height) + col]) / (float)255;
                final float zPosition = ((float)row / (float)(height -1)) - 0.5f;
                /* 顶点位置：
                 * 高度图在每个方向上都是1个单位宽，且其以x-z平面上的位置(0, 0)为中心，
                 * 通过循环，位图在左上角将被映射到(-0.5, -0.5) ，右下角被映射为(0.5, 0.5)
                 * 
                 */
                heightmapVertices[offset++] = xPosition;
                heightmapVertices[offset++] = yPosition;
                heightmapVertices[offset++] = zPosition;
            }
        }//End outer for
        return heightmapVertices;
    }
    
    private short[] createIndexData(){
        final short[] indexData = new short[numElements];
        int offset = 0;
        
        for (int row = 0; row < height ; row++) {
            for (int col = 0; col < width; col++) {
                /*针对每4个点绘制2个三角形*/
                short topLeftIndexNum = (short)(row * width + col);
                short topRightIndexNum = (short)(row * width + col + 1);
                short bottomLeftIndexNum = (short)((row + 1) * width + col);
                short bottomRightIndexNum = (short)((row + 1) * width + col + 1);
                
                //Write out two triangles.逆时针导入数据
                
            }
        }//End out for
    }
}
