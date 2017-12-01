package jikexueyuan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 集合输出的标准操作
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
				it.remove();//迭代输出，尽量避免删除该操作
			}
			else{
				System.out.println(str);
			}
		}
	}
}
