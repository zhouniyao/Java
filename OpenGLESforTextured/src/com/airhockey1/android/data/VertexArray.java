package com.airhockey1.android.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES20.*;

public class VertexArray {
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer floatBuffer;
	
	public VertexArray(float[] vertexData) {
		floatBuffer = ByteBuffer
				.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertexData);//分配本地内存floatBuffer,用来存储顶点矩阵数据 
	}
	
	/**着色器管线装配(shader plumbing)，将着色器变量与应用程序的数据关联起来*/
	public void setVertexAttribPointer(int dataOffset, int attributeLocation,
			int componentCount, int stride) {
		floatBuffer.position(dataOffset);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
		glEnableVertexAttribArray(attributeLocation);
		
	}
}
