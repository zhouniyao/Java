package com.particles.android.objects;

import com.paticles.android.data.IndexBuffer;
import com.paticles.android.data.VertexBuffer;
import com.paticles.android.data.VertexBuffer.*;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * ÿ�����㶼��һ������������ͼ���е�λ�ú�һ�������������ȵĸ߶ȡ�
 * һ�����еĶ��㶼�����ؽ������Ϳ���ʹ���������������������OpenGL�ܻ��Ƶ������Ρ�
 */
public class HeightMap {
    
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;//GPU���㻺��
    private final IndexBuffer indexBuffer;//GPU��������
    
    public HeightMap(Bitmap bitmap) {
        
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        
        if(width * height > 65536){
            throw new RuntimeException("Heightmap is too large for the index buffer.");
        }
        
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));/*��һ��Bitmap���󴫵ݽ�ȥ�������ݼ��ؽ�һ�����㻺��������Ϊ��Щ���㴴��һ������������*/
        indexBuffer = new IndexBuffer(createIndexData());
    }
    /**
     * ������Ҫ���ٸ�����Ԫ��
     */
    private int calculateNumElements() {
        /* 
         * �߶�ͼ�У�ÿ4�����㹹�ɵ��飬����2�������Σ���ÿ����������3���������ܹ�6������
         * ͨ���� (width - 1) * (height - 1) �����������Ҫ�����顣
         */
        return (width - 1) * (height - 1) * 2 * 3;
    }

    private float[] loadBitmapData(Bitmap bitmap){
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);/*��ȡ������һά����*/
        bitmap.recycle();
        
        final float[] heightmapVertices = 
            new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;
        
        /*For��λͼ����ת��Ϊ�߶�ͼ*/
        for (int row = 0; row < height ; row++) {
            for (int col = 0; col < width; col++) {  //����ͼ���ڴ沼�֣�һ���ж�ȡ
                //����ƫ��ֵ = ��ǰ��*�߶� + ��ǰ��
                final float xPosition = ((float)col / (float)(width - 1)) - 0.5f;
                final float yPosition = 
                    /*һ������0��Ӧ�߶�0����һ������ֵ255��Ӧ�߶�1.*/
                    (float)Color.red(pixels[(row * height) + col]) / (float)255;
                final float zPosition = ((float)row / (float)(height -1)) - 0.5f;
                /* ����λ�ã�
                 * �߶�ͼ��ÿ�������϶���1����λ��������x-zƽ���ϵ�λ��(0, 0)Ϊ���ģ�
                 * ͨ��ѭ����λͼ�����Ͻǽ���ӳ�䵽(-0.5, -0.5) �����½Ǳ�ӳ��Ϊ(0.5, 0.5)
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
                /*���ÿ4�������2��������*/
                short topLeftIndexNum = (short)(row * width + col);
                short topRightIndexNum = (short)(row * width + col + 1);
                short bottomLeftIndexNum = (short)((row + 1) * width + col);
                short bottomRightIndexNum = (short)((row + 1) * width + col + 1);
                
                //Write out two triangles.��ʱ�뵼������
                
            }
        }//End out for
    }
}
