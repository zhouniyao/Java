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
				.put(vertexData);//���䱾���ڴ�floatBuffer,�����洢����������� 
	}
	
	/**��ɫ������װ��(shader plumbing)������ɫ��������Ӧ�ó�������ݹ�������*/
	public void setVertexAttribPointer(int dataOffset, int attributeLocation,
			int componentCount, int stride) {
		floatBuffer.position(dataOffset);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
		glEnableVertexAttribArray(attributeLocation);
		
	}
}
