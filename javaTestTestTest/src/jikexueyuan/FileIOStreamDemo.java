package jikexueyuan;

import java.io.*;

public class FileIOStreamDemo {
	private static final int EOF = -1;

	public static void main(String[] args){
		try {
			FileInputStream fis = new FileInputStream("AVkou.gif");
			FileOutputStream fos = new FileOutputStream("new_AVkou.gif");
			
			byte[] value = new byte[50];
			while(fis.read(value) != EOF){
				fos.write(value);
			}
			fis.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e){}
		
	}
}
