package com.example.z2min3d;

import static android.opengl.GLES20.*;
import static niming.util.Constants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Table {
	FloatBuffer vertexData;
	float[] tableVerticesWithTriangles = {
			//table brim
			-0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
			-0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
			-0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
            // Triangle Fan
            0f,     0f,    1f,    1f,    1f,         
            -0.5f, -0.8f, 0.7f, 0.7f, 0f,        //平滑着黄色    
             0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
             0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
             0.5f, 0f, 1f, 0f, 0f,

            // Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f,  0.4f, 1f, 0f, 0f
		};
	
	public Table() {
		//分配一个大小为………的本地内存块
		vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * 4)
				//告诉字节缓冲区(byte buffer)按照本地字节序(native byte order)组织它的内容
				.order(ByteOrder.nativeOrder())
				//我们不愿直接操作单独的字节，而是希望使用浮点数，因此调用asFloatBuffer()得到一个可以反映底层字节的FloatBuffer类的实例
				.asFloatBuffer();
		//然后调用		vertexData.put(tableVerticesWithTriangles)把数据从Dalvik的内存中复制到本地内存
		vertexData.put(tableVerticesWithTriangles);
	}
	public void bindData(int aPositionLocation){
		vertexData.position(0);//本地内存中的顶点数组，它的指针位置归0
	//	glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		/*OpenGL从缓存区（vertexData）中读取数据*/
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 2, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
	}
	public void draw(){
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 16);
	}
}
