package com.niming;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ByteBufferDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    String str = "helloWorld";  
        ByteBuffer buff  = ByteBuffer.wrap(str.getBytes());  
        System.out.println("position:"+buff.position()+"\t limit:"+buff.limit());  
        //读取两个字节  
        buff.get();  
        buff.get();  
        System.out.println("position:"+buff.position()+"\t limit:"+buff.limit());  
        buff.mark();  
        System.out.println("position:"+buff.position()+"\t limit:"+buff.limit());  
//        showBuffer(buff);//OK
        buff.flip();  
        System.out.println("position:"+buff.position()+"\t limit:"+buff.limit());
        showBuffer(buff);//java.nio.BufferUnderflowException,读取数据，缓冲区内的剩余没有要求的多，limit=2，限制了读取范围
	}
	static void showBuffer(Buffer b){
		System.out.println("============================================");
		b.position(0);
		for (int i = 0; i < b.capacity(); i++) {
			System.out.print(" "+((ByteBuffer)b).get());
		}
		System.out.println();
	}
}
