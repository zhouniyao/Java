package com.niming;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

/**
 * ClassName:    FloatBufferDemo.java
 * @Description: FloatBuffer生成、操作, 通常使用：一个float数组和一个FloatBuffer绑定使用，大小一样，数据一样
 * Company:      YNNU 
 * @author:      Ni Ming
 * @version:     V1.0 
 * CreateDate:   2017-8-24 下午4:15:12
 * Copyright:    Copyright(C) 2017
 * Modification  History:
 * Date          Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 2017-8-24       Ni Ming        1.0             1.0
 * Why & What is modified: <修改原因描述>
 */
public class FloatBufferDemo {

	private static final int BYTES_PER_FLOAT = 4;//float占4个byte（字节）

	/**
	 * @Title:       main
	 * @Description: 
	 * @param args   
	 * @return:      void   
	 * @throws
	 */
	public static void main(String[] args) {
		//10个元素的float数组
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
								.put(array);//分配本地内存floatBuffer,用来存储顶点矩阵数据 
		floatBuffer.position(0);//回到起点
		
		floatBuffer.flip();
//		floatBuffer.clear();
//		floatBuffer.put(array2,0,array2.length );
//		floatBuffer.put(10f);//floatBuffer容量已经放满，产生java.nio.BufferUnderflowException
		
		floatBuffer.position(0);
		System.out.println("容量大小 = " + floatBuffer.capacity());
		
		System.out.println("==============================输出FloatBuffer==================================");
		int capacity = floatBuffer.capacity();//返回容量  = 10
		for (int i = 0; i < capacity; i++) { //容易产生 java.nio.BufferUnderflowException
			System.out.print(floatBuffer.get() + ", " );//单个字符输出
		}
		
	}

}
