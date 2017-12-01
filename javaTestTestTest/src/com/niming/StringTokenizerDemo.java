package com.niming;

import java.util.StringTokenizer;

public class StringTokenizerDemo {

	public static void main(String[] args) {
		
		/*StringTokenizer(String str)。默认以” \t\n\r\f”（前有一个空格，引号不是）为分割符*/
		/*StringTokenizer(String str, String delim, boolean returnDelims)。returnDelims为true的话则delim分割符也被视为标记。*/
		StringTokenizer s = new StringTokenizer("my family zhouniyao, zhouyan niming!	ya\fya");
		System.out.println("Total tockens :" + s.countTokens());//安照默认分隔符方式计算子串数
//		while(s.hasMoreElements()){  // 返回与 hasMoreTokens 方法相同的值。
////			System.out.println("Next token :" + s.nextToken());
//			System.out.println("Next token :" + s.nextToken(" "));
//		}
		while(s.hasMoreTokens()){  // 测试此 tokenizer 的字符串中是否还有更多的可用标记。
			System.out.println("Next token :" + s.nextToken());
//			System.out.println("Next token :" + s.nextToken(" "));//返回此 string tokenizer 的字符串中的下一个标记。依照指定的delim分隔符分隔。
		}
		
//	     StringTokenizer st = new StringTokenizer("this is a test");
//	     while (st.hasMoreTokens()) {
//	         System.out.println(st.nextToken());
//	     }
	}

}
