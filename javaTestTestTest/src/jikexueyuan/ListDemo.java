package jikexueyuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;



/**
 * �������� e1.equals(e2) ��Ԫ�ض� e1 �� e2����������б������� null Ԫ�صĻ���
 * ͨ������������ null Ԫ�ء�
 */
public class ListDemo {
	public static void main(String[] args){
//		List<String> lists  = new ArrayList<String>();
//		lists.add("a");
//		lists.add("b");
//		lists.add("c");
//		lists.add("a");
//		for (int i = 0; i < lists.size(); i++) {
//			System.out.println(lists.get(i));
//		}
//		lists.remove(0);
//		System.out.println("=====================================");
//		
//		for (int i = 0; i < lists.size(); i++) {
//			System.out.println(lists.get(i));
//		}
//		//�ж��Ƿ�Ϊ��
//		System.out.println("�����Ƿ�Ϊ�գ�" + lists.isEmpty());
//		
//		//����ָ��Ԫ���Ƿ����
//		System.out.println("B�Ƿ���ڣ�" + lists.indexOf("B"));//���ڷ�������ֵ�������ڣ�����-1
//		System.out.println("B�Ƿ���ڣ�" + lists.indexOf("a"));//���ڷ�������ֵ�������ڣ�����-1
		
		List<String> lists = null;
		lists = new Vector<String>();
		lists.add("a");
		lists.add("b");
		lists.add("a");
		for (int i = 0; i < lists.size(); i++) {
			System.out.println(lists.get(i));
		}
	}
}
