package com.geometric;

/**
 * ���еĶ��㡢���������������� 
 */
public class NMIndex{
	public short index[];
	
	public NMIndex(){
		index = new short[3];
	}
	/**
	 * ���������ε�������������
	 * @param short a, short b, short c
	 */
	public NMIndex(short a, short b, short c)
	{
		this();
		this.index[0] = a;
		this.index[1] = b;
		this.index[2] = c;
	}
	
	/**
	 * Convenience method to cast int arguments to short's 
	 */
	public NMIndex(int a, int b, int c)
	{
		this();
		this.index[0] = (short)a;
		this.index[1] = (short)b;
		this.index[2] = (short)c;
	}
	public NMIndex(short[] copy){
		this();
		this.index[0] = copy[0];
		this.index[1] = copy[1];
		this.index[2] = copy[2];
	}
}

