package jikexueyuan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ��������ı�׼����
 */
public class IteratorDemo {
	public static void  main(String[] args) {
		List<String> lists = new ArrayList<String>();
		lists.add("A");
		lists.add("B");
		lists.add("C");
		lists.add("D");
		lists.add("E");
		lists.add("F");
		Iterator it = lists.iterator();
		while(it.hasNext()){
			String str = (String) it.next();
			if("A".equals(str)){
				it.remove();//�����������������ɾ���ò���
			}
			else{
				System.out.println(str);
			}
		}
	}
}
