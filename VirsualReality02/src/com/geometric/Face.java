package com.geometric;

import java.util.ArrayList;

/**
 * һ�������������������
 * ArrayList<Short> vSeq;//��������
 * ArrayList<Short> uvSeq;//��������
 * ArrayList<Short> nSeq;//����������
 */
public class Face 
{
	public ArrayList<Short> vSeq;//��������
	public ArrayList<Short> uvSeq;//��������
	public ArrayList<Short> nSeq;//����������
	/**
	 * �������л��һ������Ϣ
	 */
//	public Face(short[] a, short[] b, short[] c){
//		vSeq  = a;
//		uvSeq = b;
//		nSeq  = c;
//	}
	/**
	 * Ϊ��Ա�������ռ�
	 */
	public Face(){
		vSeq  = new ArrayList<Short>();
		uvSeq = new ArrayList<Short>();
		nSeq  = new ArrayList<Short>();
		vSeq.ensureCapacity(1024);
		uvSeq.ensureCapacity(1024);
		nSeq.ensureCapacity(1024);
	}
}

