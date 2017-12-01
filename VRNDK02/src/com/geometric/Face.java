package com.geometric;

import java.util.ArrayList;

/**
 * 一个面由三个子序列组成
 * ArrayList<Integer> vSeq;//顶点序列
 * ArrayList<Integer> uvSeq;//纹理序列
 * ArrayList<Integer> nSeq;//法向量序列
 */
public class Face 
{
	public ArrayList<Integer> vSeq;//顶点序列
	public ArrayList<Integer> uvSeq;//纹理序列
	public ArrayList<Integer> nSeq;//法向量序列
	/**
	 * 三组序列绘出一个面信息
	 */
//	public Face(Integer[] a, Integer[] b, Integer[] c){
//		vSeq  = a;
//		uvSeq = b;
//		nSeq  = c;
//	}
	/**
	 * 为成员数组分配空间
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

