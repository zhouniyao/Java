package com.particles1.android.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES20.*;

/**
 * ClassName:    VertexArray.java
 * @Description: ���������ݵ���GPU�����ڴ�
 * Company:      YNNU 
 * @author:      Ni Ming
 * @version:     V1.0 
 * CreateDate:   2017-8-24 ����3:44:02
 * Copyright:    Copyright(C) 2017
 * Modification  History:
 * Date          Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 2017-8-24      Ni Ming        1.0             1.0
 * Why & What is modified: <�޸�ԭ������>
 */
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
	/**
	 * @Title: updateBuffer
	 * @Description: ����������ӵ�FloatBuffer��
	 * @param vertexData
	 * @param start
	 * @param count   
	 * @return: void   
	 * @throws
	 */
	public void updateBuffer(float[] vertexData, int start, int count) {
		floatBuffer.position(start);
		floatBuffer.put(vertexData, start, count);
		floatBuffer.position(0);//����floatBuffer�Ŀ�ʼ��
	}
}
