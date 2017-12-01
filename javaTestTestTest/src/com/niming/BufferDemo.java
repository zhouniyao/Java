package com.niming;

import java.nio.Buffer;
import java.nio.ByteBuffer;
/**
 * ���ú͸�λ�����Լ���ѯ�ķ�����
                             capacity() �����ش˻�������������
                                clear() ������˻�������ʹ������Ϊһϵ���µ�ͨ����ȡ����Է��� ��������׼����������������Ϊ������С����λ������Ϊ 0��
                                 flip() ����ת�˻�������
                                limit() �����ش˻����������ơ�
                    limit(int newLimit) �����ô˻����������ơ�
                                 mark() ���ڴ˻�������λ�����ñ�ǡ���������˱�ǣ����ڽ�λ�û����Ƶ���ΪС�ڸñ�ǵ�ֵʱ���ñ�ǽ���������
                             position() �����ش˻�������λ�á� 
              position(int newPosition) �����ô˻�������λ�á� 
                            remaining() �����ص�ǰλ��������֮���Ԫ������ 
                                reset() �����˻�������λ������Ϊ��ǰ��ǵ�λ�á� 
                               rewind() �����ƴ˻������� ��ʹ���Ʊ��ֲ��䣬��λ������Ϊ 0�� 
                               
                               0 <= ��� <= λ�� <= ���� <= ���� 

 */

public class BufferDemo {

	static int capacity1, limit1, position1;
	
	final static int CAPACITY = 15;
	public static void main(String[] args) {
		byte[] b = "my test".getBytes();//��ȡ�ֽ�����
		ByteBuffer bytebuffer = ByteBuffer.allocate(CAPACITY);//����buffer��С����������
		bytebuffer.put(b);//���ֽ��������buffer��
		
		capacity1 = bytebuffer.capacity();//����
		limit1 = bytebuffer.limit();//����
		position1 = bytebuffer.position();//λ��
		
		System.out.println("������"+capacity1+"    ���ޣ�"+limit1+"   λ�ã� "+position1+"    mark:"+"��Ҫ�Լ�����");  
		/*������15    ���ޣ�15   λ�ã� 7    mark:��Ҫ�Լ�����*/
		showBuffer(bytebuffer);
		
		//��ת�˻��������ӽ������������֪����capacity���䣬limit=position  ��position=0��  
		bytebuffer.flip();//���Ƚ���������Ϊ��ǰλ�ã�Ȼ��λ������Ϊ 0��
        //��ʱ��buffer�о��������һ�����Ƚ��ȳ���
		capacity1 = bytebuffer.capacity();
		limit1 = bytebuffer.limit();
		position1 = bytebuffer.position();
		System.out.println("������"+capacity1+"    ���ޣ�"+limit1+"   λ�ã� "+position1+"    mark:"+"��Ҫ�Լ�����");  
		showBuffer(bytebuffer);
        //���ƴ˻���������ʵ�����൱��ˢ��һ�£�֪ͨbuffer���ö���д��׼������û��ʲô�ı䡣  
        bytebuffer.rewind();  
        capacity1 = bytebuffer.capacity();  
        limit1 = bytebuffer.limit();  
        position1 = bytebuffer.position();  
        System.out.println("������"+capacity1+"    ���ޣ�"+limit1+"   λ�ã� "+position1+"    mark:"+"��Ҫ�Լ�����"); 
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
