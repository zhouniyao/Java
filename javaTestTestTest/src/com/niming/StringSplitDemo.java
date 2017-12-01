package com.niming;

import java.util.StringTokenizer;

public class StringSplitDemo {
	public static void main(String[] args){
		String str = "7/7/13 8/8/14 2/10/15 1/9/16 ";
		String[] aa = str.split("[ ]+");//·Ö¸ô¿Õ¸ñ
		
		for (int i = 0; i < aa.length; i++) {
			String[] part = aa[i].split("/");
			
			System.out.println(aa[i]);
			
			for(int j=0; j<part.length; j++){
				System.out.println(part[j]);
			}
		}
		

	 
	}//End main
}
