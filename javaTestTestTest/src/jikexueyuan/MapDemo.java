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
		System.out.println(str);//相同key，最近的覆盖了值
		for (int i = 0; i <= map.size(); i++) {
			System.out.println(map.get(i));
		}
//		
//		//通过keySet()，输出所有的key
//		Set<Integer> set = map.keySet();
//		Iterator it = set.iterator();
//		while (it.hasNext()) {
//			System.out.println(it.next());
//		}
		//输出所有value
//		Collection<String> c = map.values();
//		Iterator it2 = c.iterator();
//		while (it2.hasNext()) {
//			System.out.println(it2.next());
//		}
	}
}
