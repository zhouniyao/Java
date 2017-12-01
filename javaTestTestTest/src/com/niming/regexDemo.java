package com.niming;

public class regexDemo {

	public static void main(String[] args) {
		String[] tokens = "1//1 2//2 3//3 4//4".split("[ ]+");
		int c = tokens.length;
//		System.out.println("c ===" + c);
		for (int i = 0; i < c; i++) {
			String[] parts = tokens[i].split("/");
			System.out.println("length====" + parts.length);
			for (int j = 0; j < parts.length; j++) {
				System.out.print(","+parts[j]);
			}
			System.out.println();
			
//			System.out.println(tokens[i]);
		}
	}

}
