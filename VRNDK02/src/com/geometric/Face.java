package com.geometric;

import java.util.ArrayList;

/**
 * һ�������������������
 * ArrayList<Integer> vSeq;//��������
 * ArrayList<Integer> uvSeq;//��������
 * ArrayList<Integer> nSeq;//����������
 */
public class Face 
{
	public ArrayList<Integer> vSeq;//��������
	public ArrayList<Integer> uvSeq;//��������
	public ArrayList<Integer> nSeq;//����������
	/**
	 * �������л��һ������Ϣ
	 */
//	public Face(Integer[] a, Integer[] b, Integer[] c){
//		vSeq  = a;
//		uvSeq = b;
//		nSeq  = c;
//	}
	/**
	 * Ϊ��Ա�������ռ�
	 */
	public Face(){
		vSeq  = new ArrayList<Integer>();
		uvSeq = new ArrayList<Integer>();
		nSeq  = new ArrayList<Integer>();
		vSeq.ensureCapacity(1024);
		uvSeq.ensureCapacity(1024);
		nSeq.ensureCapacity(1024);
	}
}

