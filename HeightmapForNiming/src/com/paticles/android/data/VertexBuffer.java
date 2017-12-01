package com.paticles.android.data;

import static android.opengl.GLES20.*;

import static com.particles.android.Constants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * ����һ�����㻺��������,GPU�ڴ�
 */
public class VertexBuffer {
    private final int bufferId;
    
    public VertexBuffer(float[] vertexData) {
        //Allocate a buffer.
        final int buffers[] = new int[1];//һ��ֻ��һ��Ԫ�ص����飬�������洢����»�������ID
        glGenBuffers(buffers.length, buffers, 0);//����Ϊ����
        if(buffers[0] == 0){
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }
        bufferId = buffers[0];
        
        //Bind to the buffer.
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        
        /*CPU���ݡ���>�����ڴ桪��>GPU�������ڴ�*/
        
        //Transfer data to native memory.
        FloatBuffer vertexArray = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
        vertexArray.position(0);
        
        //Transfer data from native memory to the GPU buffer.
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_FLOAT, vertexArray, GL_STATIC_DRAW);
        /* int usage ����OpenGL���������������������ʹ�õ�ģʽ
         * GL_STREAM_DRAW �������ֻ�ᱻ�޸�һ�Σ��Ҳ��ᱻ����ʹ��
         * GL_STATIC_DRAW �������ֻ�ᱻ�޸�һ�Σ��� �ᱻ����ʹ��
         * GL_DYNAMIC_DRAW ������󽫱��޸ĺ�ʹ�úܶ��
         */
        //IMPORTANT:Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
        int componentCount, int stride) {        
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);//�󶨵�������
        /**��ɫ������װ��(shader plumbing)������ɫ��������Ӧ�ó�������ݹ�������*/
        glVertexAttribPointer(attributeLocation, componentCount,
            GL_FLOAT, false, stride, dataOffset);//���һ�����Ը���OpenGL����ǰ���Զ�Ӧ�����ֽ�Ϊ��λ��ƫ����
        glEnableVertexAttribArray(attributeLocation);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
