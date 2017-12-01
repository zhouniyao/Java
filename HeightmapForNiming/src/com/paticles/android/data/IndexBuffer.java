package com.paticles.android.data;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.particles.android.Constants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * 构造一个索引缓冲区 ，GPU内存
 */
public class IndexBuffer {
 private final int bufferId;
    
    public IndexBuffer(short[] vertexData) {
        //Allocate a buffer.
        final int buffers[] = new int[1];//一个只含一个元素的数组，用它来存储这个新缓冲区的ID
        glGenBuffers(buffers.length, buffers, 0);//参数为数组
        if(buffers[0] == 0){
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }
        bufferId = buffers[0];
        
        //Bind to the buffer.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        
        /*CPU数据——>本地内存——>缓冲区内存*/
        
        //Transfer data to native memory.
        ShortBuffer vertexArray = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(vertexData);
        vertexArray.position(0);
        
        //Transfer data from native memory to the GPU buffer.
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_SHORT, vertexArray, GL_STATIC_DRAW);
        /* int usage 告诉OpenGL对这个缓冲区对象所期望使用的模式
         * GL_STREAM_DRAW 这个对象只会被修改一次，且不会被经常使用
         * GL_STATIC_DRAW 这个对象只会被修改一次，且 会被经常使用
         * GL_DYNAMIC_DRAW 这个对象将被修改和使用很多次
         */
        //IMPORTANT:Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
        int componentCount, int stride) {        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);//绑定到缓冲区
        /**着色器管线装配(shader plumbing)，将着色器变量与应用程序的数据关联起来*/
        glVertexAttribPointer(attributeLocation, componentCount,
            GL_FLOAT, false, stride, dataOffset);//最后一个属性告诉OpenGL，当前属性对应的以字节为单位的偏移量
        glEnableVertexAttribArray(attributeLocation);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public int getBufferId() {
        return bufferId;
    }
}


