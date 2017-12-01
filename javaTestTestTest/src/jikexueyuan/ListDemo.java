package jikexueyuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;



/**
 * 允许满足 e1.equals(e2) 的元素对 e1 和 e2，并且如果列表本身允许 null 元素的话，
 * 通常它们允许多个 null 元素。
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
//		//判断是否为空
//		System.out.println("集合是否为空：" + lists.isEmpty());
//		
//		//查找指定元素是否存在
//		System.out.println("B是否存在：" + lists.indexOf("B"));//存在返回索引值，不存在，返回-1
//		System.out.println("B是否存在：" + lists.indexOf("a"));//存在返回索引值，不存在，返回-1
		
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
