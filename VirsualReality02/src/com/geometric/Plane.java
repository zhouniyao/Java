package com.geometric;

/**
 * �㷨ʽ����ƽ��
 */
public class Plane{
	public final Number3d Number3d;
	public final Vector normal;
	

	public Plane(Number3d Number3d2, Vector vector) {
		this.Number3d = Number3d2;
		this.normal = vector;
	}
}