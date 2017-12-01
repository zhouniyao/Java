package com.niming;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

/**
 * ClassName:    FloatBufferDemo.java
 * @Description: FloatBuffer���ɡ�����, ͨ��ʹ�ã�һ��float�����һ��FloatBuffer��ʹ�ã���Сһ��������һ��
 * Company:      YNNU 
 * @author:      Ni Ming
 * @version:     V1.0 
 * CreateDate:   2017-8-24 ����4:15:12
 * Copyright:    Copyright(C) 2017
 * Modification  History:
 * Date          Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 2017-8-24       Ni Ming        1.0             1.0
 * Why & What is modified: <�޸�ԭ������>
 */
public class FloatBufferDemo {

	private static final int BYTES_PER_FLOAT = 4;//floatռ4��byte���ֽڣ�

	/**
	 * @Title:       main
	 * @Description: 
	 * @param args   
	 * @return:      void   
	 * @throws
	 */
	public static void main(String[] args) {
		//10��Ԫ�ص�float����
		float[] array = {
				0.0f,1.1f,2.2f,3.3f,4.4f,
				5.5f,6.6f,7.7f,8.8f,9.9f,
		};
		float[] array2 = {
				0.0f,2.2f,4.4f,
				6.6f,8.8f,
		};
//		System.out.println("array.length = " + array.length);//array.length = 10
		//Create FloatBuffer
		FloatBuffer floatBuffer = ByteBuffer
								.allocateDirect(array.length * BYTES_PER_FLOAT)
								.order(ByteOrder.nativeOrder())
								.asFloatBuffer()
								.put(array);//���䱾���ڴ�floatBuffer,�����洢����������� 
		floatBuffer.position(0);//�ص����
		
		floatBuffer.flip();
//		floatBuffer.clear();
//		floatBuffer.put(array2,0,array2.length );
//		floatBuffer.put(10f);//floatBuffer�����Ѿ�����������java.nio.BufferUnderflowException
		
		floatBuffer.position(0);
		System.out.println("������С = " + floatBuffer.capacity());
		
		System.out.println("==============================���FloatBuffer==================================");
		int capacity = floatBuffer.capacity();//��������  = 10
		for (int i = 0; i < capacity; i++) { //���ײ��� java.nio.BufferUnderflowException
			System.out.print(floatBuffer.get() + ", " );//�����ַ����
		}
		
	}

}
