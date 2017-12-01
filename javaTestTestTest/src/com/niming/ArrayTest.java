package com.niming;

public class ArrayTest {
	public static void main(String[] args) {
		int[] a = { 2, 3, 5, 7, 8};
		int[] empty = {};
		int tmp = 4;
		int[] b = new int[a.length + 1];
		
//		if(empty.length == 0){
//			System.out.println("TRUE");
//		}else{
//			System.out.println("FLASE");
//			
//		}
		int location = 0;
		int i = 0;
		//找插入位置
		while(true){
			if(a[i] > tmp){
				location = i;//找到位置
				break;
			}
			i++;
		}
		
//		System.out.println(location);
		int j=0;
		
		for(i= 0, j=0; i<a.length; ){
			if(j != location){
				b[j] = a[i];
				j++;i++;
			}
			else{
				b[j] = tmp;
				j++;
//				continue;
			}
		}
		//输出b数组
		for (i=0; i<b.length; i++) {
			System.out.println(b[i]);
		}
	}
}
