package com.niming;

import java.util.StringTokenizer;

public class StringTokenizerDemo {

	public static void main(String[] args) {
		
		/*StringTokenizer(String str)��Ĭ���ԡ� \t\n\r\f����ǰ��һ���ո����Ų��ǣ�Ϊ�ָ��*/
		/*StringTokenizer(String str, String delim, boolean returnDelims)��returnDelimsΪtrue�Ļ���delim�ָ��Ҳ����Ϊ��ǡ�*/
		StringTokenizer s = new StringTokenizer("my family zhouniyao, zhouyan niming!	ya\fya");
		System.out.println("Total tockens :" + s.countTokens());//����Ĭ�Ϸָ�����ʽ�����Ӵ���
//		while(s.hasMoreElements()){  // ������ hasMoreTokens ������ͬ��ֵ��
////			System.out.println("Next token :" + s.nextToken());
//			System.out.println("Next token :" + s.nextToken(" "));
//		}
		while(s.hasMoreTokens()){  // ���Դ� tokenizer ���ַ������Ƿ��и���Ŀ��ñ�ǡ�
			System.out.println("Next token :" + s.nextToken());
//			System.out.println("Next token :" + s.nextToken(" "));//���ش� string tokenizer ���ַ����е���һ����ǡ�����ָ����delim�ָ����ָ���
		}
		
//	     StringTokenizer st = new StringTokenizer("this is a test");
//	     while (st.hasMoreTokens()) {
//	         System.out.println(st.nextToken());
//	     }
	}

}
