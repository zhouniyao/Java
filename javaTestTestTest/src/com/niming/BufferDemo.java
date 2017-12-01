package com.niming;

import java.nio.Buffer;
import java.nio.ByteBuffer;
/**
 * 设置和复位索引以及查询的方法：
                             capacity() ：返回此缓冲区的容量。
                                clear() ：清除此缓冲区。使缓冲区为一系列新的通道读取或相对放置 操作做好准备：它将限制设置为容量大小，将位置设置为 0。
                                 flip() ：反转此缓冲区。
                                limit() ：返回此缓冲区的限制。
                    limit(int newLimit) ：设置此缓冲区的限制。
                                 mark() ：在此缓冲区的位置设置标记。如果定义了标记，则在将位置或限制调整为小于该标记的值时，该标记将被丢弃。
                             position() ：返回此缓冲区的位置。 
              position(int newPosition) ：设置此缓冲区的位置。 
                            remaining() ：返回当前位置与限制之间的元素数。 
                                reset() ：将此缓冲区的位置重置为以前标记的位置。 
                               rewind() ：重绕此缓冲区。 它使限制保持不变，将位置设置为 0。 
                               
                               0 <= 标记 <= 位置 <= 限制 <= 容量 

 */

public class BufferDemo {

	static int capacity1, limit1, position1;
	
	final static int CAPACITY = 15;
	public static void main(String[] args) {
		byte[] b = "my test".getBytes();//获取字节数组
		ByteBuffer bytebuffer = ByteBuffer.allocate(CAPACITY);//定义buffer大小，分配容量
		bytebuffer.put(b);//把字节数组放入buffer中
		
		capacity1 = bytebuffer.capacity();//容量
		limit1 = bytebuffer.limit();//界限
		position1 = bytebuffer.position();//位置
		
		System.out.println("容量："+capacity1+"    界限："+limit1+"   位置： "+position1+"    mark:"+"需要自己设置");  
		/*容量：15    界限：15   位置： 7    mark:需要自己设置*/
		showBuffer(bytebuffer);
		
		//反转此缓冲区，从结果来看，我们知道，capacity不变，limit=position  ；position=0；  
		bytebuffer.flip();//首先将限制设置为当前位置，然后将位置设置为 0。
        //此时的buffer感觉就像队列一样，先进先出。
		capacity1 = bytebuffer.capacity();
		limit1 = bytebuffer.limit();
		position1 = bytebuffer.position();
		System.out.println("容量："+capacity1+"    界限："+limit1+"   位置： "+position1+"    mark:"+"需要自己设置");  
		showBuffer(bytebuffer);
        //重绕此缓冲区，其实就是相当于刷新一下，通知buffer做好读或写的准备，并没有什么改变。  
        bytebuffer.rewind();  
        capacity1 = bytebuffer.capacity();  
        limit1 = bytebuffer.limit();  
        position1 = bytebuffer.position();  
        System.out.println("容量："+capacity1+"    界限："+limit1+"   位置： "+position1+"    mark:"+"需要自己设置"); 
        showBuffer(bytebuffer);
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
