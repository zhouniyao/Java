package jikexueyuan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapDemo {
	public static void main(String[] args) {
		Map<Integer, String> map = new HashMap<Integer, String>();		
		map.put(1, "jike");
		map.put(2, "baba");
		map.put(3, "mama");
		map.put(4, "yeye");
		map.put(5, "daxue");
		String str = map.get(1);
		System.out.println(str);//��ͬkey������ĸ�����ֵ
		for (int i = 0; i <= map.size(); i++) {
			System.out.println(map.get(i));
		}
//		
//		//ͨ��keySet()��������е�key
//		Set<Integer> set = map.keySet();
//		Iterator it = set.iterator();
//		while (it.hasNext()) {
//			System.out.println(it.next());
//		}
		//�������value
//		Collection<String> c = map.values();
//		Iterator it2 = c.iterator();
//		while (it2.hasNext()) {
//			System.out.println(it2.next());
//		}
	}
}
