package com.geometric;

/**
 * һ����������������������
 */
public class Face 
{
	NMIndex vIndex;//��������
	NMIndex vtIndex;//��������
	NMIndex vnIndex;//����������
	public Face(){}
	/**
	 * �������л��һ������Ϣ
	 */
	public Face(NMIndex a, NMIndex b, NMIndex c){
		vIndex  = a;
		vtIndex = b;
		vnIndex = c;
	}
	
	public Face(short[] a, short[] b, short[] c){
		vIndex  = new NMIndex(a);
		vtIndex = new NMIndex(b);
		vnIndex = new NMIndex(c);
	}
}

