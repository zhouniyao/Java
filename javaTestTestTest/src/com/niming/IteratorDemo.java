package com.niming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Integer> lis = new ArrayList<Integer>();
		lis.add(1);
		lis.add(1);
		lis.add(1);
		lis.add(1);
		lis.add(5);
		lis.add(4);
		lis.add(3);
		lis.add(2);
		lis.add(1);
		
//		Iterator iter = lis.iterator();
//		while(iter.hasNext()){
//			System.out.println(iter.next());
//		}
//		
		System.out.println("size:"+lis.size());
		Integer[] a = new Integer[lis.size()];
		lis.toArray(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
		
	}

}
