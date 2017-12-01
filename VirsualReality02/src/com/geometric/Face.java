package com.geometric;

import java.util.ArrayList;

/**
 * 一个面由三个子序列组成
 * ArrayList<Short> vSeq;//顶点序列
 * ArrayList<Short> uvSeq;//纹理序列
 * ArrayList<Short> nSeq;//法向量序列
 */
public class Face 
{
	public ArrayList<Short> vSeq;//顶点序列
	public ArrayList<Short> uvSeq;//纹理序列
	public ArrayList<Short> nSeq;//法向量序列
	/**
	 * 三组序列绘出一个面信息
	 */
//	public Face(short[] a, short[] b, short[] c){
//		vSeq  = a;
//		uvSeq = b;
//		nSeq  = c;
//	}
	/**
	 * 为成员数组分配空间
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

