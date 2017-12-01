package com.geometric;

/**
 * 一个面由三个顶点的序列组成
 */
public class Face 
{
	NMIndex vIndex;//顶点序列
	NMIndex vtIndex;//纹理序列
	NMIndex vnIndex;//法向量序列
	public Face(){}
	/**
	 * 三组序列绘出一个面信息
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

